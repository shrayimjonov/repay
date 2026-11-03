package uz.repay.card.service.core;


import uz.repay.card.redis.entity.Message;
import uz.repay.common.model.enums.Processing;

public interface MessageRedisService {

    Message createOTP(String id,
                      String code,
                      String phone,
                      String card,
                      Processing processing,
                      Long loanId);

    Message createOTPV2(String id,
                        String code,
                        String phone,
                        String card,
                        Processing processing,
                        Long loanId);

    Message findById(String id);

    Message createOTPV3(String id,
                        String code,
                        String phone,
                        String card,
                        Processing processing,
                        Long loanId,
                        String loanExtId);
}
