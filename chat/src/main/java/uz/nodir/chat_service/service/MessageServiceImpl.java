package uz.nodir.chat_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.chat_service.repository.MessageRepository;
import uz.repay.common.model.dto.response.core.ResultData;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl {
    private final MessageRepository repository;


}
