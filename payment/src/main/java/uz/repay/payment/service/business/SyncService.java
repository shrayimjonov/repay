package uz.repay.payment.service.business;

import uz.repay.common.model.enums.Processing;
import uz.repay.payment.model.dto.core.pay.response.result.SyncResult;
import uz.repay.payment.model.entity.Transaction;

public interface SyncService {

    SyncResult syncPC(String extId, Transaction transaction);

    SyncResult syncABS(String refNum);

    SyncResult syncUzCard(String extId);

    SyncResult syncHuMo(String extId);

    SyncResult syncDavrVISA(Transaction transaction);

    SyncResult syncMaster(String extId);
}
