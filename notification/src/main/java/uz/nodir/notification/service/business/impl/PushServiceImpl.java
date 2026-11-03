package uz.nodir.notification.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.push.response.PushV2ResponseDTO;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.model.property.DefaultProperty;
import uz.nodir.notification.mongo.entity.Push;
import uz.nodir.notification.service.business.PushService;
import uz.nodir.notification.service.core.PushMongoDAO;
import uz.nodir.notification.service.core.TemplateDAO;
import uz.nodir.notification.service.web.LoanClientService;
import uz.nodir.notification.utils.TextBuilder;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.request.kafka.LoanPaidEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.DateUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {
    private final PushMongoDAO pushMongoDAO;
    private final LoanClientService loanClientService;
    private final TemplateDAO templateDAO;
    private final DefaultProperty defaultProperty;

    @Override
    public void receiveLoanDebit(LoanPaidEventDTO eventDTO) {
        log.info("Received loan debit event dto: {}", eventDTO);

        var loanExtId = eventDTO.getLoanExtId();

        var loanResult = loanClientService.takeLoanFeaturedCreators(loanExtId);

        if (!loanResult.getStatus()) {
            log.error("Loan: {} result is unsuccessful", loanExtId);
            return;
        }

        if (loanResult.getData().getError() != null) {
            log.error("Loan Client has error: {}", loanResult.getData().getError().getMessage());
            return;
        }

        var loans = loanResult.getData().getResult();

        if (loans.isEmpty()) {
            log.warn("Loan: {} don't added to featured list to any users", loanExtId);
            return;
        }

        loans
                .forEach(l -> {
                            log.info("Saving loan: {} for user: {}", loanExtId, l);

                            var push = new Push(
                                    UUID.randomUUID(),
                                    DateUtils.getDateTimeNow(),
                                    loanExtId,
                                    eventDTO.getPan(),
                                    eventDTO.getAmount(),
                                    l,
                                    false
                            );

                            pushMongoDAO.save(push);

                        }
                );

        log.info("Finished loan debit event handler method");
    }


    @Override
    public ResultData<List<PushV2ResponseDTO>> findUserPushes() {
        var username = SecurityUtils.getUsername();
        log.info("Received command to get user pushes by: {}", username);

        var pushList = pushMongoDAO.findByUsername(username);

        if (pushList.isEmpty()) {
            log.info("Finished command to get user pushes with empty list!");
            return ResultData.ok(
                    List.of()
            );
        }

        var templateResult = findTemplateByType(TemplateType.PUSH);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(
                    templateResult.getError().getCode(),
                    templateResult.getError().getMessage()
            );
        }


        var template = templateResult.getData();


        var result = ResultData.ok(
                pushList
                        .stream()
                        .sorted((k, v) -> v.getCreatedTime().compareTo(k.getCreatedTime()))
                        .map(z -> {
                            var textResult = TextBuilder
                                    .buildTextFromTemplate(
                                            template, defaultProperty.getLanguage(),
                                            z.getLoanExtId(), z.getPan(), z.getAmount().toString()
                                    );

                            if (!textResult.getStatus())
                                throw new InvalidDataException(textResult.getError().getMessage());

                            return new PushV2ResponseDTO(z.getId(), textResult.getData(), z.getIsRead());
                        })
                        .toList()
        );

        log.info("Finished command to get user pushes with size: {}", result.getResult().size());
        return result;

    }

    @Override
    public ResultData<CoreError> checkReadPushes() {
        var username = SecurityUtils.getUsername();
        log.info("Received command to mark user all pushes read by: {}", username);

        var pushList = pushMongoDAO.findByUsername(username);

        if (pushList.isEmpty()) {
            log.info("Finished command to get user pushes with empty list!");
            return new ResultData<>(
                    ResultState.NotProcessed,
                    "User hasn't any pushes!"
            );
        }

        pushList
                .forEach(p -> {
                            p.setIsRead(true);
                            pushMongoDAO.save(p);
                        }
                );

        var result = ResultData.ok(
                new CoreError(
                        0, ResponseStatus.SUCCESS.name()
                )
        );
        log.info("Finished command to mark user all pushes with response: {}", result);
        return result;
    }

    private SimpleResponse<Template> findTemplateByType(TemplateType templateType) {
        return templateDAO.findAll()
                .stream()
                .filter(z -> z.getSmsType().equals(templateType))
                .findFirst()
                .map(SimpleResponse::new)
                .orElseGet(() -> new SimpleResponse<>(1051, "Template: %s not found!".formatted(templateType)));
    }
}
