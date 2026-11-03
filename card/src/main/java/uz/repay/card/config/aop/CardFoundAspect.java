package uz.repay.card.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.model.dto.core.request.card.CardBaseParam;
import uz.repay.card.service.core.PaidRequestMongoDAO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.model.enums.UserPermission;
import uz.repay.common.utils.DateUtils;


@Lazy
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class CardFoundAspect {
    private final PaidRequestMongoDAO dao;

    @Pointcut("@annotation(uz.repay.card.config.aop.CardFounder) && args(param,..))")
    public void handleCardFoundRequest(CardBaseParam param) {
    }


    public Object checkCardFoundToFraud(ProceedingJoinPoint pjp, CardBaseParam param) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("Start checking card find fraud for request: {}", param);

        String username = SecurityUtils.getUsername();
        var paidRequests = dao.findAllByLoanExtIdAndUsername(param.getLoanExtId(), username);

        if (paidRequests.isEmpty()) {
            log.info("Paid requests for: {} is empty. Card finding is possible", param.getLoanExtId());
            return pjp.proceed();
        }

        var todaysOperationOptional = paidRequests
                .stream()
                .filter(z -> z.getCreatedTime().toLocalDate().equals(DateUtils.getDateNow()))
                .findFirst();

        if (todaysOperationOptional.isPresent()) {
            var message = "Finished method, cause: Today for loan: %s card finding already is used by: %s".formatted(param.getLoanExtId(), username);
            log.error(message);
            return new ResultData<>(ResultState.BlockOperation, message);
        }


        log.info("Card finding is possible. Fraud checking spent time : {} ms", System.currentTimeMillis() - start);
        return pjp.proceed();
    }

    @Around(value = "handleCardFoundRequest(param)", argNames = "pjp, param")
    public Object checkCardFoundToFraudV2(ProceedingJoinPoint pjp, CardBaseParam param) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("Start checking card find fraud for request: {}", param);

        String username = SecurityUtils.getUsername();
        var userRoles = SecurityUtils.extractRoles();

        if (userRoles.contains(UserPermission.ROLE_ADMIN.toString()) ||
                userRoles.contains(UserPermission.ROLE_MODER.toString())) {
            log.info("Card finding is possible. Cause user is admin/moderator: {}", username);
            return pjp.proceed();
        }

        var paidRequests = dao.findAllByLoanExtId(param.getLoanExtId());

        if (paidRequests.size() >= 3) {
            var message = "For loan: %s card finding already is used!".formatted(param.getLoanExtId());

            log.error("Finished method. Cause: {}", message);
            return new ResultData<>(ResultState.BlockOperation, message);
        }

        log.info("Card finding is possible. Fraud checking spent time : {} ms", System.currentTimeMillis() - start);
        return pjp.proceed();
    }

}
