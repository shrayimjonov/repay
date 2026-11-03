package uz.repay.payment.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.PageUtils;
import uz.repay.payment.model.dto.core.transaction.ExcelReportMeta;
import uz.repay.payment.model.dto.core.transaction.request.TransactionByLoanRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionExcelRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionFilterRequestDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionFilterResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionReportDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionsMonthlyResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.result.ExcelFile;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionReportResult;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.entity.meta.Transaction_;
import uz.repay.payment.service.business.TransactionService;
import uz.repay.payment.service.component.TransactionExcelReporter;
import uz.repay.payment.service.core.TransactionDAO;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl extends JpaSpecificationBuilder<Transaction> implements TransactionService {

    private static final DateTimeFormatter FILTER_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("d.M.yyyy");
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("ddMMyyyy");

    private static final int MAX_PERIOD_MONTHS = 3;
    private static final int PAN_LENGTH = 16;

    private static final List<ResponseStatus> SUCCESS_STATUSES = List.of(ResponseStatus.SUCCESS);

    private static final int EXCEL_PAGE_SIZE = 5_000;
    private static final int EXCEL_MAX_ROWS = 500_000;
    private static final int EXCEL_FETCH_CONCURRENCY = 8;
    private static final String FILE_NAME_PREFIX = "repay_transactions_";
    private static final String FILE_NAME_EXTENSION = ".xlsx";
    private static final String UNSAFE_FILE_NAME_CHARS = "[^A-Za-z0-9._-]";

    private final TransactionDAO transactionDAO;
    private final TransactionExcelReporter excelReporter;

    @Override
    public ResultData<TransactionResponseDTO> getTransactionById(@NonNull Long id) {
        log.info("Received command to get transaction by id: {}", id);
        var transaction = transactionDAO.findById(id);

        TransactionResponseDTO transactionDTO = new TransactionResponseDTO(transaction);
        ResultData<TransactionResponseDTO> resultData = new ResultData<>(transactionDTO);

        log.info("Finished command to get transaction by id with result data: {}", resultData);
        return resultData;
    }

    @Override
    public ResultData<TransactionFilterResponseDTO> getWithFilter(@NonNull TransactionFilterRequestDTO requestDTO) {
        var pageable = PageUtils.pageableBuilder(requestDTO);
        var specification = buildSpecification(requestDTO);

        var entity = transactionDAO.findByFilter(specification, pageable);

        return new ResultData<>(new TransactionFilterResponseDTO(entity));
    }


    private Specification<Transaction> buildSpecification(TransactionFilterRequestDTO dto) {
        Specification<Transaction> specification = Specification.where(null);
        var branches = getAccessedBranches(dto);

        if (dto.getParams() != null) {
            var predicatedValues = dto.getParams();

            specification = specification.and(createSpecByBranchInCondition(Transaction_.BRANCH, branches));

            if (predicatedValues.getAmount() != null && predicatedValues.getAmount() != 0)
                specification = specification.and(createSpecByLikeCondition(Transaction_.AMOUNT, String.valueOf(predicatedValues.getAmount())));

            if (predicatedValues.getRefNum() != null && !predicatedValues.getRefNum().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Transaction_.REF_NUM, predicatedValues.getRefNum()));

            if (predicatedValues.getLoanExtId() != null && !predicatedValues.getLoanExtId().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Transaction_.LOAN_EXT_ID, predicatedValues.getLoanExtId()));

            if (predicatedValues.getCardPan() != null && !predicatedValues.getCardPan().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Transaction_.CARD_PAN, predicatedValues.getCardPan()));

            if (predicatedValues.getRange() != null && predicatedValues.getRange().getDateFrom() != null && predicatedValues.getRange().getDateTo() != null) {
                validateDate(predicatedValues.getRange());
                specification = specification.and(createSpecByDateBetweenCondition(Transaction_.CREATED_DATE, predicatedValues.getRange().getDateFrom(), predicatedValues.getRange().getDateTo()));
            }

            if (predicatedValues.getStatuses() != null && !predicatedValues.getStatuses().isEmpty())
                specification = specification.and(createSpecByResponseStatusInCondition(Transaction_.STATUS, predicatedValues.getStatuses()));

        }

        return specification;
    }

    private void validateDate(TransactionFilterRequestDTO.DateRange dateRange) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateStart;
        LocalDate dateFinish;

        if (dateRange.getDateFrom() == null || dateRange.getDateTo() == null || dateRange.getDateFrom().isEmpty() || dateRange.getDateTo().isEmpty())
            throw new InvalidDataException("Date cannot be null: StartDate: %s EndDate: %s".formatted(dateRange.getDateFrom(), dateRange.getDateTo()));

        try {
            dateStart = LocalDate.parse(dateRange.getDateFrom(), dateFormatter);
        } catch (DateTimeException e) {
            throw new InvalidDataException("Date From catch error: %s".formatted(e.getMessage()));
        }

        try {
            dateFinish = LocalDate.parse(dateRange.getDateTo(), dateFormatter);
        } catch (DateTimeException e) {
            throw new InvalidDataException("Date To catch error: %s".formatted(e.getMessage()));
        }


        if (dateFinish.isBefore(dateStart))
            throw new InvalidDataException("The End date cannot be less than the Start date! %s %s"
                    .formatted(dateStart.getMonth(), dateFinish.getMonth()));

    }

    private static List<Branch> getAccessedBranches(TransactionFilterRequestDTO dto) {
        var branches = SecurityUtils.getUserBranches();
        var accessedBranches = new ArrayList<Branch>();

        if (dto.getParams().getBranches() != null && !dto.getParams().getBranches().isEmpty())
            dto
                    .getParams()
                    .getBranches()
                    .forEach(
                            b -> branches
                                    .forEach(d -> {
                                                if (b.getCode().equals(d))
                                                    accessedBranches.add(b);
                                            }
                                    )
                    );
        else
            Arrays.stream(Branch
                            .values())
                    .forEach(
                            b -> branches
                                    .forEach(
                                            d -> {
                                                if (b.getCode().equals(d))
                                                    accessedBranches.add(b);
                                            }
                                    )
                    );


        if (accessedBranches.isEmpty())
            throw new InvalidDataException("User branches not provided or not accessed!");

        return accessedBranches;
    }


    @Override
    public ResultData<TransactionFilterResponseDTO> getByLoanId(@NonNull TransactionByLoanRequestDTO requestDTO) {
        var pageable = PageUtils.pageableBuilder(requestDTO);
        var pageEntity = transactionDAO.findByLoanId(pageable, requestDTO.getParams().getLoanExtId());
        return new ResultData<>(new TransactionFilterResponseDTO(pageEntity));
    }

    @Override
    public ResultData<List<TransactionsMonthlyResponseDTO>> getMonthlyTransactions() {
        var entity = transactionDAO.findMonthlyTransactions();
        List<TransactionsMonthlyResponseDTO> resultList = new ArrayList<>();
        entity
                .forEach(
                        transaction -> resultList.stream()
                                .filter(z -> z.getBranch().equals(transaction.getBranch()))
                                .findFirst()
                                .ifPresentOrElse(coreTransMonthlyResult -> coreTransMonthlyResult.getTrans()
                                                .stream()
                                                .filter(z -> z.getDate().equals(transaction.getCreated_date()))
                                                .findFirst()
                                                .ifPresentOrElse(
                                                        transByDate -> transByDate.getInfo().add(new TransactionsMonthlyResponseDTO.TransInfo(transaction)),
                                                        () -> coreTransMonthlyResult.getTrans().add(new TransactionsMonthlyResponseDTO.TransByDate(transaction))
                                                ),
                                        () -> {
                                            var newBranch = new TransactionsMonthlyResponseDTO(transaction.getBranch());
                                            newBranch.getTrans().add(new TransactionsMonthlyResponseDTO.TransByDate(transaction));
                                            resultList.add(newBranch);
                                        })
                );

        return new ResultData<>(resultList);
    }

    @Override
    public ResultData<List<TransactionReportDTO>> getReportByMonthly() {
        var transactions = transactionDAO.getReportByMonthly();

        var list = new ArrayList<TransactionReportDTO>();

        transactions
                .forEach(
                        t -> {
                            var dayHas = list
                                    .stream()
                                    .filter(l -> t.getDate().toLocalDate().equals(l.getDate()))
                                    .findFirst();

                            if (dayHas.isPresent()) {
                                dayHas.get().getData().add(
                                        new TransactionReportResult(t)
                                );
                            } else {
                                list.add(new TransactionReportDTO(t));
                            }
                        }
                );


        return ResultData.ok(
                list
        );
    }

    @Override
    public ResultData<List<TransactionResponseDTO>> getHistoryByLoanId(@NonNull String loanExtId) {
        log.info("Received command to get full transaction history for loan: {}", loanExtId);
        var transactions = transactionDAO.findHistoryByLoanId(loanExtId);

        var responseDTOList = transactions.stream()
                .map(TransactionResponseDTO::new)
                .toList();

        log.info("Returning {} transactions for loan: {}", responseDTOList.size(), loanExtId);
        return new ResultData<>(responseDTOList);
    }

    @Override
    public ExcelFile generateExcel(@NonNull TransactionExcelRequestDTO requestDTO) {
        var requestedAt = LocalDateTime.now();
        var requestedParams = requestDTO.getParams() != null
                ? requestDTO.getParams()
                : new TransactionFilterRequestDTO.TransactionParams();

        var period = resolvePeriod(requestedParams.getRange());
        var accessedBranches = getAccessedBranches(new TransactionFilterRequestDTO(requestedParams));
        var exportParams = withSuccessStatuses(requestedParams);

        var rows = readRowsWithinCap(exportParams, accessedBranches, period);
        var appliedFilters = describeFilters(exportParams, accessedBranches);

        var meta = new ExcelReportMeta(
                SecurityUtils.getUsername(),
                requestedAt,
                period.from(),
                period.to(),
                period.defaulted(),
                appliedFilters,
                rows.size()
        );

        var fileName = buildFileName(period, requestedParams.getStatuses());
        var content = excelReporter.generate(meta, rows);

        log.info("Transactions excel exported: user={}, period={} - {}, defaultedPeriod={}, filters={}, rows={}, file={}, bytes={}",
                meta.generatedBy(), FILTER_DATE.format(period.from()), FILTER_DATE.format(period.to()),
                period.defaulted(), appliedFilters, rows.size(), fileName, content.length);

        return new ExcelFile(fileName, content);
    }

    private List<TransactionExcelRow> readRowsWithinCap(TransactionFilterRequestDTO.TransactionParams params,
                                                        List<Branch> accessedBranches,
                                                        ExportPeriod period) {
        var slices = sliceByDate(period, EXCEL_FETCH_CONCURRENCY);
        var executor = Executors.newFixedThreadPool(Math.min(slices.size(), EXCEL_FETCH_CONCURRENCY));
        var budget = new AtomicInteger();

        List<List<TransactionExcelRow>> perSlice;
        try {
            var futures = slices.stream()
                    .map(slice -> CompletableFuture.supplyAsync(
                            () -> readSlice(params, accessedBranches, slice, budget), executor))
                    .toList();

            perSlice = joinAll(futures);
        } finally {
            executor.shutdown();
        }

        int total = perSlice.stream().mapToInt(List::size).sum();

        if (total > EXCEL_MAX_ROWS)
            throw new InvalidDataException(
                    ("Period %s - %s matches more than %d transactions: data is huge and it cause stacking "
                            + "repayment process error. Please narrow the filter.")
                            .formatted(FILTER_DATE.format(period.from()),
                                    FILTER_DATE.format(period.to()),
                                    EXCEL_MAX_ROWS));

        var rows = new ArrayList<TransactionExcelRow>(total);
        perSlice.forEach(rows::addAll);

        return rows;
    }

    private List<TransactionExcelRow> readSlice(TransactionFilterRequestDTO.TransactionParams params,
                                                List<Branch> accessedBranches,
                                                ExportPeriod slice,
                                                AtomicInteger budget) {
        var sliceParams = withResolvedPeriod(params, slice);
        var rows = new ArrayList<TransactionExcelRow>();

        LocalDate afterCreatedDate = null;
        Long afterId = null;

        while (budget.get() <= EXCEL_MAX_ROWS) {
            var page = transactionDAO.findRowsForExcel(
                    sliceParams, accessedBranches, afterCreatedDate, afterId, EXCEL_PAGE_SIZE);

            rows.addAll(page);
            budget.addAndGet(page.size());

            if (page.size() < EXCEL_PAGE_SIZE) {
                break;
            }

            var last = page.get(page.size() - 1);
            afterCreatedDate = last.getCreatedDate();
            afterId = last.getId();

            if (afterCreatedDate == null || afterId == null) {
                break;
            }
        }

        return rows;
    }

    private static List<ExportPeriod> sliceByDate(ExportPeriod period, int maxSlices) {
        long days = ChronoUnit.DAYS.between(period.from(), period.to()) + 1;
        int slices = (int) Math.max(1, Math.min(maxSlices, days));

        long base = days / slices;
        long remainder = days % slices;

        var result = new ArrayList<ExportPeriod>(slices);
        var cursor = period.to();

        for (int index = 0; index < slices; index++) {
            long length = base + (index < remainder ? 1 : 0);
            var sliceFrom = cursor.minusDays(length - 1);

            result.add(new ExportPeriod(sliceFrom, cursor, period.defaulted()));
            cursor = sliceFrom.minusDays(1);
        }

        return result;
    }

    private static <T> List<T> joinAll(List<CompletableFuture<T>> futures) {
        try {
            return futures.stream().map(CompletableFuture::join).toList();
        } catch (CompletionException e) {
            var cause = e.getCause();

            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new GeneralErrorException("Excel export page read failed: %s".formatted(cause.getMessage()));
        }
    }

    private record ExportPeriod(LocalDate from, LocalDate to, boolean defaulted) {
    }

    private static boolean isRangeAbsent(TransactionFilterRequestDTO.DateRange range) {
        return range == null
                || (isBlank(range.getDateFrom()) && isBlank(range.getDateTo()));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static LocalDate parsePeriodBound(String value, String label) {
        if (isBlank(value))
            throw new InvalidDataException("Date cannot be null: %s: %s".formatted(label, value));

        try {
            return LocalDate.parse(value.trim(), INPUT_DATE);
        } catch (DateTimeException e) {
            throw new InvalidDataException("%s catch error: %s".formatted(label, e.getMessage()));
        }
    }

    private ExportPeriod resolvePeriod(TransactionFilterRequestDTO.DateRange range) {
        if (isRangeAbsent(range)) {
            var today = LocalDate.now();
            log.info("No date filter provided for excel export, defaulting to current month");
            return new ExportPeriod(today.withDayOfMonth(1), today, true);
        }

        var from = parsePeriodBound(range.getDateFrom(), "Date From");
        var to = parsePeriodBound(range.getDateTo(), "Date To");

        if (to.isBefore(from))
            throw new InvalidDataException("The End date cannot be less than the Start date! %s %s"
                    .formatted(FILTER_DATE.format(from), FILTER_DATE.format(to)));

        if (from.plusMonths(MAX_PERIOD_MONTHS).isBefore(to))
            throw new InvalidDataException(
                    ("Requested period %s - %s exceeds %d months: data is huge and it cause stacking repayment "
                            + "process error. Please request a shorter period.")
                            .formatted(range.getDateFrom(), range.getDateTo(), MAX_PERIOD_MONTHS));

        return new ExportPeriod(from, to, false);
    }

    private static TransactionFilterRequestDTO.TransactionParams withSuccessStatuses(
            TransactionFilterRequestDTO.TransactionParams params) {

        var requested = params.getStatuses();
        var statuses = requested == null || requested.isEmpty()
                ? SUCCESS_STATUSES
                : requested.stream().filter(SUCCESS_STATUSES::contains).toList();

        if (statuses.isEmpty())
            throw new InvalidDataException(
                    "Only successful transactions are exported. Requested statuses %s contain none of %s."
                            .formatted(requested, SUCCESS_STATUSES));

        return new TransactionFilterRequestDTO.TransactionParams(
                params.getAmount(),
                params.getBranches(),
                params.getCardPan(),
                params.getLoanExtId(),
                params.getRefNum(),
                params.getRange(),
                statuses
        );
    }

    private static TransactionFilterRequestDTO.TransactionParams withResolvedPeriod(
            TransactionFilterRequestDTO.TransactionParams params, ExportPeriod period) {

        return new TransactionFilterRequestDTO.TransactionParams(
                params.getAmount(),
                params.getBranches(),
                params.getCardPan(),
                params.getLoanExtId(),
                params.getRefNum(),
                new TransactionFilterRequestDTO.DateRange(
                        FILTER_DATE.format(period.from()),
                        FILTER_DATE.format(period.to())
                ),
                params.getStatuses()
        );
    }

    private static List<String> describeFilters(TransactionFilterRequestDTO.TransactionParams params,
                                                List<Branch> accessedBranches) {
        var filters = new ArrayList<String>();

        if (params.getStatuses() != null && !params.getStatuses().isEmpty())
            filters.add("Статус: " + params.getStatuses().stream().map(Enum::name).collect(Collectors.joining(", ")));

        if (params.getBranches() != null && !params.getBranches().isEmpty())
            filters.add("Филиал: " + accessedBranches.stream().map(Enum::name).collect(Collectors.joining(", ")));

        if (params.getLoanExtId() != null && !params.getLoanExtId().isBlank())
            filters.add("Номер кредита: " + params.getLoanExtId());

        if (params.getRefNum() != null && !params.getRefNum().isBlank())
            filters.add("Номер платежа: " + params.getRefNum());

        if (params.getCardPan() != null && !params.getCardPan().isBlank())
            filters.add("Карта: " + maskCardFilter(params.getCardPan()));

        if (params.getAmount() != null && params.getAmount() != 0)
            filters.add("Сумма: " + params.getAmount());

        return filters;
    }

    private static String maskCardFilter(String cardPan) {
        return cardPan.length() >= PAN_LENGTH ? CardUtils.maskPanDigits(cardPan) : cardPan;
    }

    private static String buildFileName(ExportPeriod period, List<ResponseStatus> statuses) {
        var fileName = new StringBuilder(FILE_NAME_PREFIX)
                .append(FILE_DATE.format(period.from()))
                .append('-')
                .append(FILE_DATE.format(period.to()));

        if (statuses != null && statuses.size() == 1 && statuses.get(0) != null)
            fileName.append('_').append(statuses.get(0).name());

        return fileName.toString().replaceAll(UNSAFE_FILE_NAME_CHARS, "_") + FILE_NAME_EXTENSION;
    }

}
