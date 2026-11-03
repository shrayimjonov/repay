package uz.repay.card.service.core;

import uz.repay.card.model.entity.EPos;

import java.util.List;

public interface EPosDAO {

    EPos save(EPos EPos);

    List<EPos> getAll();

    void deleteById(Long id);

    void evictTerminalCache();

    void makeExceptional(List<String> terminalIds);
}
