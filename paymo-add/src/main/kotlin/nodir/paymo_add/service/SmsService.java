package nodir.paymo_add.service;

import nodir.paymo_add.entity.Sms;

import java.util.List;

public interface SmsService {
    List<Sms> findAllSmsByPhone(String phone);
}
