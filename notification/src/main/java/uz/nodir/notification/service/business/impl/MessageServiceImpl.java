package uz.nodir.notification.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.message.request.NotificationBaseRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationDebtMessageRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationMobileLinkRequestDTO;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.mongo.entity.Limit;
import uz.nodir.notification.redis.entity.Fraud;
import uz.nodir.notification.service.business.MessageService;
import uz.nodir.notification.service.business.SenderService;
import uz.nodir.notification.service.core.FraudRedisDAO;
import uz.nodir.notification.service.core.LimitDAO;
import uz.nodir.notification.service.core.TemplateDAO;
import uz.nodir.notification.utils.PhoneImprover;
import uz.nodir.notification.utils.TextBuilder;
import uz.repay.common.model.dto.request.kafka.CardAttachedEventDTO;
import uz.repay.common.model.dto.request.kafka.CardConfirmationEventDTO;
import uz.repay.common.model.dto.request.kafka.NotificationDebitPayEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CommonUtils;
import uz.repay.common.utils.DateUtils;

import java.util.Comparator;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SenderService senderService;
    private final TemplateDAO templateDAO;
    private final FraudRedisDAO fraudRedisDAO;
    private final LimitDAO limitDAO;

    @Override
    public ResultData<CoreError> debitPay(@NonNull NotificationDebitPayEventDTO requestDTO) {
        log.info("Received command to debitPay with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.DEBIT_PAY);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template, requestDTO.getLanguage(),
                requestDTO.getPan(),
                requestDTO.getFullName(),
                requestDTO.getLoanExtId(),
                requestDTO.getAmount().toString());

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.DEBIT_PAY);
    }

    private SimpleResponse<Template> findTemplateByType(TemplateType type) {
        return templateDAO
                .findAll()
                .stream()
                .filter(z -> z.getSmsType().equals(type))
                .findFirst()
                .map(SimpleResponse::new)
                .orElseGet(() -> new SimpleResponse<>(ResultState.EntityNotFound.getCode(), "Type: %s not found!".formatted(type)));
    }

    @Override
    public ResultData<CoreError> debt(@NonNull NotificationDebtMessageRequestDTO requestDTO) {
        log.info("Received command to debt message with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.DEBT_ON);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(template, requestDTO.getLanguage(),
                requestDTO.getDebtor(),
                requestDTO.getAmount().toString(),
                requestDTO.getLoanExtId());

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.DEBT_ON);
    }


    private ResultData<CoreError> baseSender(String phone, String loanExtId, String text, TemplateType type) {
        var fraudResult = checkToFraud(loanExtId, phone, type);


        if (!fraudResult.getStatus())
            return new ResultData<>(fraudResult.getError(), false);

        var phoneImprovedResult = CommonUtils.phoneImprover(phone);

        var phoneCheckResult = PhoneImprover.checkPhoneNumber(phoneImprovedResult);

        if (!phoneCheckResult) {
            var message = "Phone: %s is incorrect! Taken value: %s".formatted(phoneImprovedResult, phone);
            log.error(message);
            return new ResultData<>(ResultState.INCORRECT_PHONE, message);
        }


        var sentResult = senderService.send(text, phoneImprovedResult, type, loanExtId);

        if (!sentResult.getStatus()) {
            log.error("Message send method finished with error... {}", sentResult.getError());
            return new ResultData<>(sentResult.getError(), false);
        }


        saveToLimit(phone, loanExtId, type);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to send base message with response: {}", result);
        return result;
    }

    private void saveToLimit(String phone, String loanExtId, TemplateType type) {
        if (type.getIsUsable()) {
            log.info("Type: {} is usable and will be saved to limit dao", type);

            var limit = new Limit(
                    UUID.randomUUID(),
                    DateUtils.getDateTimeNow(),
                    phone,
                    loanExtId,
                    type
            );

            limitDAO.save(limit);
        }
    }


    @Override
    public ResultData<CoreError> confirmation(@NonNull CardConfirmationEventDTO requestDTO) {
        log.info("Received command to card confirmation with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.CONFIRMATION);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template,
                requestDTO.getLanguage(),
                requestDTO.getCode(),
                requestDTO.getLoanExtId(),
                requestDTO.getPan());

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.CONFIRMATION);
    }

    @Override
    public ResultData<CoreError> mib(@NonNull NotificationBaseRequestDTO requestDTO) {
        log.info("Received command to mib message with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.MIB);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template,
                requestDTO.getLanguage(),
                requestDTO.getLoanExtId()
        );

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.MIB);
    }


    @Override
    public ResultData<CoreError> notice(@NonNull NotificationBaseRequestDTO requestDTO) {
        log.info("Received command to notice message with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.NOTICE);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template,
                requestDTO.getLanguage(),
                requestDTO.getLoanExtId()
        );

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.NOTICE);
    }


    private SimpleResponse<CoreError> checkToFraud(String loanExtId, String phone, TemplateType type) {
        if (type.getIsUsable()) {
            var limitResult = limitDAO.findByLoanAndPhoneAndType(loanExtId, phone, type);

            if (!limitResult.isEmpty() && limitResult.size() > 1) { // access for 2 messages daily
                limitResult.sort(Comparator.comparing(Limit::getCreatedTime));
                var lastLimit = limitResult.getFirst();
                var message = "Today already has message for loan: %s with phone: %s with type: %s. Last sent time: %s now: %s"
                        .formatted(loanExtId, phone, type, lastLimit.getCreatedTime(), DateUtils.getDateTimeNow());
                log.error(message);
                return new SimpleResponse<>(ResultState.LIMIT_REACHED.getCode(), message);
            }

            var fraudRecordResult = fraudRedisDAO.findByLoanExtIdAndPhone(loanExtId, phone);

            if (!fraudRecordResult.isEmpty() && fraudRecordResult.size() > 1) { // access for 2 messages daily
                var message = "In queue already has message for loan: %s with phone: %s".formatted(loanExtId, phone);
                log.error(message);
                return new SimpleResponse<>(ResultState.LIMIT_REACHED.getCode(), message);
            }

            var fraud = new Fraud(
                    UUID.randomUUID(),
                    phone,
                    loanExtId
            );

            log.info("Fraud not detected and will be created: {}", fraud);
            fraudRedisDAO.createDebtorQueue(fraud);
        }

        return new SimpleResponse<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );
    }

    @Override
    public ResultData<CoreError> cardAttach(@NonNull CardAttachedEventDTO requestDTO) {
        log.info("Received command to card attach with request:{}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.CARD_ATTACHED);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template,
                requestDTO.getLanguage(),
                requestDTO.getPan(),
                requestDTO.getLoanExtId());

        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), text, TemplateType.CARD_ATTACHED);
    }

    @Override
    public ResultData<CoreError> mobileDeepLink(@NonNull NotificationMobileLinkRequestDTO requestDTO) {
        log.info("Received command to send message \"Mobile deep link \" with request dto: {}", requestDTO);

        var templateResult = findTemplateByType(TemplateType.MOBILE_DEEP_LINK);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        var template = templateResult.getData();

        var textResult = TextBuilder.buildTextFromTemplate(
                template,
                requestDTO.getLanguage(),
                requestDTO.getLoanExtId());


        if (!textResult.getStatus())
            return new ResultData<>(textResult.getError(), false);

        var text = textResult.getData();

        var newText = text.replaceAll("\\$\\{loan_ext_id}", requestDTO.getLoanExtId().substring(0,5) + "_" + requestDTO.getLoanExtId().substring(5));

        return baseSender(requestDTO.getPhone(), requestDTO.getLoanExtId(), newText, TemplateType.MOBILE_DEEP_LINK);
    }
}
