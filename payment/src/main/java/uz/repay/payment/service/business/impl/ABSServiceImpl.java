package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.dto.nciBilling.request.NCIForexTranRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCISimpleTranRequest;
import uz.repay.payment.model.dto.nciBilling.response.NCIBaseResponse;
import uz.repay.payment.model.entity.ABSSetting;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.service.business.ABSService;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.NCIBillingService;
import uz.repay.payment.utils.BillUtils;
import uz.repay.payment.utils.PurposeTools;

@Service
@Slf4j
@RequiredArgsConstructor
public class ABSServiceImpl implements ABSService {
    private final NCIBillingService nciBillingService;
    private final TransactionDAO transactionDAO;


    @Override
    public SimpleResponse<NCIBaseResponse> forexTransaction(Transaction transaction, ABSSetting absSetting) {
        log.info("Begin forex transaction for {} USD", transaction.getAmount() / 100);


        var request = new NCIForexTranRequest(
                absSetting.getBranchDebit(),
                absSetting.getAccountDebit(),
                absSetting.getAccountCredit(),
                transaction.getAmount(),
                Currency.USD.getCode().toString(),
                transaction.getId() * (-1),
                PurposeTools.purposeBuilder(transaction, absSetting.getPurpose())
        );

        log.info("ABS forex prepared request: {}", request);


        var forexBill = new Bill(
                transaction.getAmount(),
                absSetting.getAccountCredit(),
                absSetting.getAccountDebit(),
                null,
                null,
                transaction,
                ResponseStatus.PENDING,
                TranType.FOREX,
                request.toMap(),
                Currency.USD
        );

        transaction.getBills().add(forexBill);


        var response = nciBillingService.doForex(request);

        forexBill.setResponse(response.toMap());
        return getABSResult(transaction, forexBill, response);
    }

    @Override
    public SimpleResponse<NCIBaseResponse> simpleTransaction(Transaction transaction, ABSSetting absSetting) {
        log.info("Begin simple transaction for tranId {}", transaction.getId());


        var tranAmount = transaction.getAmount();
        log.info("Transaction amount: {} in USD and must be converted to UZS", tranAmount);

        Long rate = transaction.getRate();

        log.info("Taken purchase rate: {}", rate);

        var debitBill = BillUtils.takeSuccessBill(transaction, TranType.DEBIT);

        String accountCredit = absSetting.getAccountCredit();


        if (accountCredit.equals("EPOS_ACC"))
            accountCredit = debitBill.getCreditTo();

        String branchCredit = accountCredit.substring(12, 17);

        var simpleTranBill = new Bill(
                rate * tranAmount,
                accountCredit,
                absSetting.getAccountDebit(),
                transaction,
                ResponseStatus.PENDING,
                TranType.TRANSIT,
                Currency.UZS
        );

        transaction.getBills().add(simpleTranBill);
        transactionDAO.save(transaction);

        var savedTranBillOptional = transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.TRANSIT) && z.getStatus().equals(ResponseStatus.PENDING))
                .findFirst();

        if (savedTranBillOptional.isEmpty()) {
            String note = "Not found TRANSIT bill with Pending status for continue transaction";
            transaction.setNote(note);
            transaction.setStatus(ResponseStatus.NEED_CONFIRM);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    7044,
                    note
            );
        }

        var savedTranBill = savedTranBillOptional.get();

        var request = new NCISimpleTranRequest(
                absSetting.getAccountDebit(),
                absSetting.getBranchDebit(),
                accountCredit,
                branchCredit,
                rate * tranAmount,
                PurposeTools.purposeBuilder(transaction, absSetting.getPurpose()),
                savedTranBill.getId() * (-1)
        );

        savedTranBill.setRequest(request.toMap());
        log.info("ABS simple transaction prepared request: {}", request);

        var response = nciBillingService.doSimpleTransaction(request);
        savedTranBill.setResponse(response.toMap());

        return getABSResult(transaction, savedTranBill, response);
    }


    private SimpleResponse<NCIBaseResponse> getABSResult(Transaction transaction, Bill simpleTranBill, SimpleResponse<NCIBaseResponse> response) {
        if (!response.getStatus()) {
            simpleTranBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(response.getError().getMessage());
            transaction.setStatus(ResponseStatus.NEED_CONFIRM);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    637,
                    response.getError().getMessage()
            );
        }


        switch (response.getData().getCode()) {
            case 446 -> {
                simpleTranBill.setStatus(ResponseStatus.FAILED);
                transaction.setNote(response.getData().getMessage());
                transaction.setStatus(ResponseStatus.NEED_CONFIRM);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(
                        637,
                        response.getData().getMessage()
                );
            }
            case 200 -> {
                simpleTranBill.setStatus(ResponseStatus.SUCCESS);
                simpleTranBill.setRefNum(response.getData().getGeneralId());
                var status = simpleTranBill.getTranType().equals(TranType.TRANSIT)
                        ? ResponseStatus.TRANSIT_SUCCESS : ResponseStatus.FOREX_SUCCESS;
                transaction.setStatus(status);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(response.getData());
            }
            default -> {
                simpleTranBill.setStatus(ResponseStatus.FAILED);
                transaction.setNote(response.getData().getMessage());
                transaction.setStatus(ResponseStatus.FAILED);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(
                        638,
                        response.getData().getMessage()
                );
            }
        }
    }

    @Override
    public SimpleResponse<NCIBaseResponse> transaction(Transaction transaction,
                                                       ABSSetting absSetting
    ) {
        log.info("Do ABS transaction with setting: {}", absSetting);

        switch (absSetting.getPayType()) {
            case FOREX_RESIDENT, FOREX_NON_RESIDENT -> {
                return forexTransaction(transaction, absSetting);
            }
            case SIMPLE_PAY -> {
                return simpleTransaction(transaction, absSetting);
            }
            default -> {
                return new SimpleResponse<>(638, "Not found transaction method for: %s".formatted(absSetting.getPayType()));
            }
        }
    }

    @Override
    public SimpleResponse<NCIBaseResponse> forexReverseTransaction(Transaction transaction, ABSSetting absSetting) {
        log.info("Begin reverse forex transaction for {} USD", transaction.getAmount() / 100);


        var request = new NCIForexTranRequest(
                absSetting.getBranchDebit(),
                absSetting.getAccountCredit(),
                absSetting.getAccountDebit(),
                transaction.getAmount(),
                Currency.USD.getCode().toString(),
                transaction.getId() * (-1),
                "Обратная проводка " + PurposeTools.purposeBuilder(transaction, absSetting.getPurpose())
        );

        log.info("ABS reverse forex prepared request: {}", request);


        var forexBill = new Bill(
                transaction.getAmount(),
                absSetting.getAccountDebit(),
                absSetting.getAccountCredit(),
                null,
                null,
                transaction,
                ResponseStatus.PENDING,
                TranType.FOREX_REVERSE,
                request.toMap(),
                Currency.USD
        );

        transaction.getBills().add(forexBill);


        var response = nciBillingService.doForex(request);

        forexBill.setResponse(response.toMap());

        return getABSResult(transaction, forexBill, response);
    }

}
