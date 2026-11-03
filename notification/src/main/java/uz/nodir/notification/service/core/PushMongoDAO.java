package uz.nodir.notification.service.core;

import uz.nodir.notification.mongo.entity.Push;

import java.util.List;

public interface PushMongoDAO {
    Push save(Push push);

    List<Push> findByUsername(String username);
}
