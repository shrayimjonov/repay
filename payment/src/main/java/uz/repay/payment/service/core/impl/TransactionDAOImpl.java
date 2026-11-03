package uz.repay.payment.service.core.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.payment.model.dto.core.transaction.request.TransactionFilterRequestDTO;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.entity.domain.MonthlyReportDTO;
import uz.repay.payment.model.entity.meta.TransactionFields;
import uz.repay.payment.model.entity.meta.TransactionMonthlyDTO;
import uz.repay.payment.repository.TransactionRepository;
import uz.repay.payment.service.core.TransactionDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionDAOImpl extends JpaSpecificationBuilder<Transaction> implements TransactionDAO {

    private final TransactionRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        log.info("Save/Update transaction: {}", transaction);
        return repository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByFilter(Specification<Transaction> specification, Pageable pageable) {
        log.info("Search transaction with filter");
        return repository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        log.info("Search transaction by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id: %d not found".formatted(id)));
    }

    @Override
    public Optional<Transaction> findTranById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByLoanId(Pageable pageable, String loanExtId) {
        log.info("Search transaction by loanExtId: {}", loanExtId);
        return repository.findByLoanExtId(pageable, loanExtId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionMonthlyDTO> findMonthlyTransactions() {
        log.info("Get monthly transactions info");
        return repository.getMonthlyTransactionsByType();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyReportDTO> getReportByMonthly() {
        log.info("Get transactions by monthly and pay type");
        return repository.getReportByMonthly();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getOrphanTransactions() {
        var needConfirmStatuses = List.of(
                ResponseStatus.R_NEED_CONFIRM,
                ResponseStatus.S_NEED_CONFIRM,
                ResponseStatus.NEED_CONFIRM,
                ResponseStatus.DEBIT_CONFIRM_NEED_CONFIRM,
                ResponseStatus.UZCARD_NEED_CONFIRM
        );
        log.info("Get orphan transactions... with status: {}", needConfirmStatuses);
        return repository.getNeedConfirmTransactions(needConfirmStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getActiveTransactions() {
        log.info("Get huMo active transactions list...");
        var list = repository.getActiveTransactions();
        log.info("huMo active transactions size: {}", list.size());
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findHistoryByLoanId(String loanExtId) {
        log.info("Get full transaction history for loan: {}", loanExtId);
        var transactions = repository.findByLoanExtIdOrderByInitTimeDesc(loanExtId);
        log.info("Found {} transactions for loan: {}", transactions.size(), loanExtId);
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionExcelRow> findRowsForExcel(TransactionFilterRequestDTO.TransactionParams params,
                                                      List<Branch> branches,
                                                      LocalDate afterCreatedDate,
                                                      Long afterId,
                                                      int limit) {
        var builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TransactionExcelRow> query = builder.createQuery(TransactionExcelRow.class);
        Root<Transaction> root = query.from(Transaction.class);

        Path<LocalDate> createdDate = root.get(TransactionFields.CREATED_DATE);
        Path<Long> id = root.get(TransactionFields.ID);

        query.select(builder.construct(
                TransactionExcelRow.class,
                root.get(TransactionFields.LOAN_EXT_ID),
                root.get(TransactionFields.CARD_OWNER),
                root.get(TransactionFields.CARD_PAN),
                root.get(TransactionFields.AMOUNT),
                root.get(TransactionFields.CURRENCY),
                root.get(TransactionFields.STATUS),
                root.get(TransactionFields.PAY_TYPE),
                root.get(TransactionFields.REF_NUM),
                root.get(TransactionFields.TERMINAL_ID),
                root.get(TransactionFields.INIT_TIME),
                root.get(TransactionFields.CREATED_BY),
                root.get(TransactionFields.NOTE),
                root.get(TransactionFields.REVERSE_REASON),
                createdDate,
                id
        ));

        var predicate = buildExcelSpecification(params, branches).toPredicate(root, query, builder);
        var keyset = keysetPredicate(builder, createdDate, id, afterCreatedDate, afterId);

        if (predicate != null && keyset != null) {
            query.where(builder.and(predicate, keyset));
        } else if (predicate != null) {
            query.where(predicate);
        } else if (keyset != null) {
            query.where(keyset);
        }

        query.orderBy(builder.desc(createdDate), builder.desc(id));

        var rows = entityManager.createQuery(query)
                .setMaxResults(limit)
                .getResultList();

        log.debug("Read {} transactions for excel export after ({}, {})", rows.size(), afterCreatedDate, afterId);

        return rows;
    }

    private static Predicate keysetPredicate(CriteriaBuilder builder,
                                             Path<LocalDate> createdDate,
                                             Path<Long> id,
                                             LocalDate afterCreatedDate,
                                             Long afterId) {
        if (afterCreatedDate == null || afterId == null) {
            return null;
        }

        return builder.or(
                builder.lessThan(createdDate, afterCreatedDate),
                builder.and(
                        builder.equal(createdDate, afterCreatedDate),
                        builder.lessThan(id, afterId)
                )
        );
    }

    private Specification<Transaction> buildExcelSpecification(TransactionFilterRequestDTO.TransactionParams params,
                                                               List<Branch> branches) {
        var predicatedValues = params != null ? params : new TransactionFilterRequestDTO.TransactionParams();

        Specification<Transaction> specification = Specification
                .where(createSpecByBranchInCondition(TransactionFields.BRANCH, branches));

        if (predicatedValues.getAmount() != null && predicatedValues.getAmount() != 0)
            specification = specification.and(createSpecByLikeCondition(TransactionFields.AMOUNT, String.valueOf(predicatedValues.getAmount())));

        if (predicatedValues.getRefNum() != null && !predicatedValues.getRefNum().isEmpty())
            specification = specification.and(createSpecByLikeCondition(TransactionFields.REF_NUM, predicatedValues.getRefNum()));

        if (predicatedValues.getLoanExtId() != null && !predicatedValues.getLoanExtId().isEmpty())
            specification = specification.and(createSpecByLikeCondition(TransactionFields.LOAN_EXT_ID, predicatedValues.getLoanExtId()));

        if (predicatedValues.getCardPan() != null && !predicatedValues.getCardPan().isEmpty())
            specification = specification.and(createSpecByLikeCondition(TransactionFields.CARD_PAN, predicatedValues.getCardPan()));

        var range = predicatedValues.getRange();
        if (range != null && hasText(range.getDateFrom()) && hasText(range.getDateTo()))
            specification = specification.and(createSpecByDateBetweenCondition(TransactionFields.CREATED_DATE, range.getDateFrom(), range.getDateTo()));

        if (predicatedValues.getStatuses() != null && !predicatedValues.getStatuses().isEmpty())
            specification = specification.and(createSpecByResponseStatusInCondition(TransactionFields.STATUS, predicatedValues.getStatuses()));

        return specification;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
