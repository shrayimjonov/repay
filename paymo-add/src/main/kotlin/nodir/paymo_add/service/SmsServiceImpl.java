package nodir.paymo_add.service;

import lombok.RequiredArgsConstructor;
import nodir.paymo_add.entity.Sms;
import nodir.paymo_add.repository.SmsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsRepository repository;

    @Override
    public List<Sms> findAllSmsByPhone(String phone) {
        return repository.findAllSmsByPhone(phone);
    }

}
