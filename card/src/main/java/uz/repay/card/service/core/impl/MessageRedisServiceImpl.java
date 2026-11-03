package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.redis.entity.Message;
import uz.repay.card.redis.repository.MessageRepository;
import uz.repay.card.service.core.MessageRedisService;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageRedisServiceImpl implements MessageRedisService {
    private final MessageRepository repository;

    @Override
    public synchronized Message createOTP(String id,
                                          String code,
                                          String phone,
                                          String card,
                                          Processing processing,
                                          Long loanId) {
        card = CardUtils.encryptPan(card);

        log.info("Create OTP for phone: {} and card: {}", phone, card);
        var optionalMessage = findByPhone(card, phone);

        if (processing.equals(Processing.UZCARD) || processing.equals(Processing.DAVR_MASTER_CARD)) {

            if (optionalMessage.isPresent()) {
                var message = optionalMessage.get();
                log.info("Message for phone: {} is already has and returned from cache", phone);

                message.setCode(code);
                message.setId(id);
                repository.save(message); // in uzCard maybe change sms id

                return message;
            } else {
                return createOTPInRedis(id, code, phone, card, processing, loanId);
            }
        } else {
            if (optionalMessage.isPresent()) {
                log.info("Message for phone: {} is already has and returned from cache", phone);
                var redisEntity = optionalMessage.get();

                repository.delete(redisEntity);

            }
            return createOTPInRedis(id, code, phone, card, processing, loanId);
        }
    }

    public synchronized Message createOTPV2(String id,
                                            String code,
                                            String phone,
                                            String card,
                                            Processing processing,
                                            Long loanId) {

        log.info("Create OTP for phone: {} and card: {}", phone, card);
        var optionalMessage = findByPhone(card, phone);

        if (processing.equals(Processing.UZCARD) || processing.equals(Processing.DAVR_MASTER_CARD)) {

            if (optionalMessage.isPresent()) {
                var message = optionalMessage.get();
                log.info("Message for phone: {} is already has and returned from cache", phone);

                message.setCode(code);
                message.setId(id);
                repository.save(message); // in uzCard maybe change sms id

                return message;
            } else {
                return createOTPInRedis(id, code, phone, card, processing, loanId);
            }
        } else {
            if (optionalMessage.isPresent()) {
                log.info("Message for phone: {} is already has and returned from cache", phone);
                var redisEntity = optionalMessage.get();

                repository.delete(redisEntity);

            }
            return createOTPInRedis(id, code, phone, card, processing, loanId);
        }
    }

    private Message createOTPInRedis(String id, String code, String phone, String card, Processing processing, Long loanId) {
        log.info("Message for phone: {} is not found and will be created!", phone);
        var preparedEntity = new Message(id, phone, code, card, processing, loanId);

        log.info("Prepared entity: {}", preparedEntity);
        return repository.save(preparedEntity);
    }

    private Message createOTPInRedisV2(String id, String code, String phone, String card, Processing processing, Long loanId, String loanExtId) {
        log.info("Message for phone: {} is not found and will be created!", phone);
        var preparedEntity = new Message(id, phone, code, card, processing, loanId, loanExtId);

        log.info("Prepared entity: {}", preparedEntity);
        return repository.save(preparedEntity);
    }

    public Optional<Message> findByPhone(String card, String phone) {
        log.info("Checking phone from cache...");
        return repository.findByCardAndPhone(card, phone);
    }

    @Override
    public Message findById(String id) {
        log.info("Find message by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Id not found! Maybe time is up... Try again"));
    }


    public synchronized Message createOTPV3(String id,
                                            String code,
                                            String phone,
                                            String card,
                                            Processing processing,
                                            Long loanId,
                                            String loanExtId) {
        if (card.matches("\\d{16}$|\\d{6}\\*{6}\\d{4}$"))
            card = CardUtils.encryptPan(card);

        System.err.println(card);

        log.info("Create OTP for phone: {} and card: {}", phone, card);
        var optionalMessage = findByPhone(card, phone);

        if (processing.equals(Processing.UZCARD) || processing.equals(Processing.DAVR_MASTER_CARD)) {

            if (optionalMessage.isPresent()) {
                var message = optionalMessage.get();
                log.info("Message for phone: {} is already has and returned from cache", phone);

                message.setCode(code);
                message.setId(id);
                repository.save(message); // in uzCard maybe change sms id

                return message;
            } else {
                return createOTPInRedisV2(id, code, phone, card, processing, loanId, loanExtId);
            }
        } else {
            if (optionalMessage.isPresent()) {
                log.info("Message for phone: {} is already has and returned from cache", phone);
                var redisEntity = optionalMessage.get();

                repository.delete(redisEntity);

            }
            return createOTPInRedisV2(id, code, phone, card, processing, loanId, loanExtId);
        }
    }

}
