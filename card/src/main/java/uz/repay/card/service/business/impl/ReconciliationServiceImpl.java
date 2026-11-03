package uz.repay.card.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.reco.ReconciliationFilterRequestDTO;
import uz.repay.card.model.dto.core.response.reconciliation.ReconciliationFilterResponseDTO;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.card.model.entity.meta.Reconciliation_;
import uz.repay.card.service.business.ReconciliationService;
import uz.repay.card.service.core.ReconciliationDAO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.DateUtils;
import uz.repay.common.utils.PageUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReconciliationServiceImpl extends JpaSpecificationBuilder<Reconciliation> implements ReconciliationService {
    private final ReconciliationDAO reconciliationDAO;

    @Override
    public ResultData<ReconciliationFilterResponseDTO> filter(@NotNull ReconciliationFilterRequestDTO requestDTO) {
        log.info("Get reconciliation by filter: {}", requestDTO);

        var pageable = PageUtils.pageableBuilder(requestDTO);
        var specification = buildSpecification(requestDTO);

        var entityPageResult = reconciliationDAO.findAll(specification, pageable);

        return ResultData.ok(
                new ReconciliationFilterResponseDTO(
                        entityPageResult
                )
        );
    }


    private Specification<Reconciliation> buildSpecification(ReconciliationFilterRequestDTO dto) {
        Specification<Reconciliation> specification = Specification.where(null);


        if (dto.getParams() != null) {
            var predicatedValues = dto.getParams();

            if (predicatedValues.getTerminalId() != null && !predicatedValues.getTerminalId().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Reconciliation_.TERMINAL, predicatedValues.getTerminalId()));

            if (predicatedValues.getRange() != null && predicatedValues.getRange().getDateFrom() != null && predicatedValues.getRange().getDateTo() != null) {
                DateUtils.validateDate(predicatedValues.getRange());
                specification = specification.and(createSpecByDateBetweenCondition(Reconciliation_.CREATED_DATE, predicatedValues.getRange().getDateFrom(), predicatedValues.getRange().getDateTo()));
            }

            if (predicatedValues.getStatuses() != null && !predicatedValues.getStatuses().isEmpty())
                specification = specification.and(createSpecByResponseStatusInCondition(Reconciliation_.STATUS, predicatedValues.getStatuses()));


            if (predicatedValues.getPaymentId() != null && !predicatedValues.getPaymentId().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Reconciliation_.PAYMENT_ID, predicatedValues.getPaymentId()));

        }

        return specification;
    }
}
