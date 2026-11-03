package uz.repay.payment.service.core;

import uz.repay.payment.model.entity.PCMaintenance;

import java.util.List;

public interface PCMaintenanceDAO {

    PCMaintenance save(PCMaintenance pcMaintenance);

    List<PCMaintenance> getAll();

    void deleteById(Long id);

    void cacheEvict();


}
