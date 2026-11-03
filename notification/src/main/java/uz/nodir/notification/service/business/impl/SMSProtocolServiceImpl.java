package uz.nodir.notification.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.protocol.request.ProtocolFilterRequestDTO;
import uz.nodir.notification.model.dto.protocol.request.params.ProtocolFilterParam;
import uz.nodir.notification.model.dto.protocol.response.ProtocolFilterResponseDTO;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.nodir.notification.model.entity.meta.Protocol_;
import uz.nodir.notification.service.business.SMSProtocolService;
import uz.nodir.notification.service.core.SMSProtocolDAO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.DateUtils;
import uz.repay.common.utils.PageUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSProtocolServiceImpl extends JpaSpecificationBuilder<SMSProtocol> implements SMSProtocolService {
    private final SMSProtocolDAO dao;

    @Override
    public ResultData<ProtocolFilterResponseDTO> findMessagesByFilter(@NonNull ProtocolFilterRequestDTO requestDTO) {
        var pageable = PageUtils.pageableBuilder(requestDTO);
        var specification = buildSpecification(requestDTO.getParams());

        var result = dao.findAll(specification, pageable);
        return new ResultData<>(
                new ProtocolFilterResponseDTO(
                        result
                )
        );
    }

    private Specification<SMSProtocol> buildSpecification(ProtocolFilterParam params) {
        Specification<SMSProtocol> specification = Specification.where(null);


        if (params != null) {

            if (params.getMessage() != null && !params.getMessage().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Protocol_.TEXT, params.getMessage().toUpperCase()));

            if (params.getPhone() != null && !params.getPhone().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Protocol_.PHONE, params.getPhone()));

            if (params.getTypes() != null && !params.getTypes().isEmpty()) {
                var templateTypes = params.getTypes()
                        .stream().map(Enum::name)
                        .toList();
                specification = specification.and(createSpecByStatusInCondition(Protocol_.TYPE, templateTypes));
            }

            if (params.getRangeParams() != null)
                if (params.getRangeParams().getStartDate() != null && !params.getRangeParams().getStartDate().isEmpty()
                        && params.getRangeParams().getEndDate() != null && !params.getRangeParams().getEndDate().isEmpty()) {
                    DateUtils.validateDateFields(params.getRangeParams().getStartDate(), params.getRangeParams().getEndDate());
                    specification = specification.and(createSpecByDateBetweenCondition(Protocol_.DATE, params.getRangeParams().getStartDate(), params.getRangeParams().getEndDate()));
                }
        }

        return specification;
    }


}
