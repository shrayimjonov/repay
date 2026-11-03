package uz.repay.payment.service.business;

import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.nciBilling.response.NCIBaseResponse;
import uz.repay.payment.model.entity.ABSSetting;
import uz.repay.payment.model.entity.Transaction;

public interface ABSService {

    SimpleResponse<NCIBaseResponse> forexTransaction(Transaction transaction, ABSSetting absSetting);

    SimpleResponse<NCIBaseResponse> simpleTransaction(Transaction transaction, ABSSetting absSetting);

    SimpleResponse<NCIBaseResponse> transaction(Transaction transaction,
                                                ABSSetting absSetting
    );

    SimpleResponse<NCIBaseResponse> forexReverseTransaction(Transaction transaction, ABSSetting absSetting);
}
