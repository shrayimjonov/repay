package uz.repay.autodebit.service.business;

import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO;

public interface AutoPaymentServiceV3 {
    void switchAP(AutoPaymentSwitcherDTO dto);
}
