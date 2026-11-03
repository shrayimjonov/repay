package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.mongo.entity.Push;
import uz.nodir.notification.mongo.repository.PushRepository;
import uz.nodir.notification.service.core.PushMongoDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushMongoDAOImpl implements PushMongoDAO {
    private final PushRepository repository;

    @Override
    @Transactional
    public Push save(Push push) {
        log.info("Save/Update push data to mongo: {}", push);
        return repository.save(push);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Push> findByUsername(String username) {
        log.info("Find pushes by username: {}", username);
        return repository.findAllByUsername(username);
    }
}
