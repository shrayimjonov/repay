package uz.repay.payment.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.TranType;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class BillUtils {
    public static Bill billBuilder(Bill bill,
                                   Map<Object, Object> request,
                                   Transaction transaction,
                                   TranType tranType,
                                   ResponseStatus status) {
        String creditTo;
        String debitFrom;

        switch (tranType) {
            case DEBIT_CANCEL: {
                creditTo = transaction.getCardPan();
                debitFrom = bill.getCreditTo();
                break;
            }
            case CREDIT_REVERSE: {
                creditTo = bill.getDebitFrom();
                debitFrom = transaction.getLoanExtId();
                break;
            }
            case CREDIT: {
                creditTo = transaction.getLoanExtId();
                debitFrom = bill.getDebitFrom();
                break;
            }
            case SYNCHRONIZE_PC: {
                creditTo = bill.getCreditTo();
                debitFrom = bill.getDebitFrom();
                break;
            }
            default:
                throw new InvalidDataException("%s type is not matched for creating BILL".formatted(tranType));

        }
        log.info("Bill builder for {}... Debit From: {} Credit To: {} ", tranType, debitFrom, creditTo);
        return new Bill(
                bill.getRequestId(),
                request,
                creditTo,
                transaction.getGeneralId(),
                tranType,
                transaction.getAmount(),
                status,
                transaction,
                bill.getBillingId(),
                bill.getTerminalId(),
                bill.getMerchantId(),
                debitFrom
        );
    }


    public static Optional<Bill> getSuccessBill(Transaction transaction, TranType tranType) {
        return transaction
                .getBills()
                .stream()
                .filter(r -> r.getTranType().equals(tranType))
                .filter(r -> r.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();
    }

    public final static String HUMO_GATE_SUBSYSTEM = "PAYMO";
    public final static String NCI_SUB_SYSTEM = "PAYMO";
    public final static String VISA_SUB_SYSTEM = "REPAY";


    public static Bill takeSuccessBill(Transaction t, TranType tranType) {
        return t.getBills()
                .stream()
                .filter(z -> z.getTranType().equals(tranType) && z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Bill in tran: %d with type: %s with SUCCESS status not found!".formatted(t.getId(), tranType)));
    }


    public static Optional<Bill> getBillByStatus(Transaction transaction, TranType tranType, ResponseStatus status) {
        return transaction
                .getBills()
                .stream()
                .filter(r -> r.getTranType().equals(tranType))
                .filter(r -> r.getStatus().equals(status))
                .findFirst();
    }

    public static Optional<Bill> getBillByTranType(Transaction transaction, TranType tranType) {
        return transaction
                .getBills()
                .stream()
                .filter(r -> r.getTranType().equals(tranType))
                .findFirst();
    }
}
