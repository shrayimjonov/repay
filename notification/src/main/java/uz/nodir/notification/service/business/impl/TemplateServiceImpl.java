package uz.nodir.notification.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.core.TextProp;
import uz.nodir.notification.model.dto.template.request.TemplateBaseRequestDTO;
import uz.nodir.notification.model.dto.template.request.TemplateFilterRequestDTO;
import uz.nodir.notification.model.dto.template.request.params.TemplateParams;
import uz.nodir.notification.model.dto.template.response.TemplateBaseResponseDTO;
import uz.nodir.notification.model.dto.template.response.TemplateFilterResponseDTO;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.entity.meta.Template_;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.model.enums.ValueType;
import uz.nodir.notification.service.business.TemplateService;
import uz.nodir.notification.service.core.TemplateDAO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.PageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl extends JpaSpecificationBuilder<Template> implements TemplateService {
    private final TemplateDAO templateDAO;


    @Override
    public ResultData<TemplateBaseResponseDTO> create(@NonNull TemplateBaseRequestDTO requestDTO) {
        log.info("Received command to create template with request: {}", requestDTO);

        var validateResult = validateAndCheckTemplate(requestDTO, true);

        if (!validateResult.getStatus())
            return new ResultData<>(validateResult.getError(), false);


        var template = templateBuilder(requestDTO);

        var result = ResultData.ok(
                new TemplateBaseResponseDTO(
                        template
                )
        );

        log.info("Finished command to create template with response: {}", result);
        return result;
    }


    private Template templateBuilder(TemplateBaseRequestDTO requestDTO) {
        var textValuesMap = new HashMap<Language, TextProp>();
        requestDTO.getParams()
                .forEach(p -> createTextValues(p, textValuesMap));

        var preparedTemplate = new Template(
                requestDTO.getType(),
                textValuesMap
        );
        return templateDAO.save(preparedTemplate);
    }

    private static void createTextValues(TemplateParams p, HashMap<Language, TextProp> textValuesMap) {
        var propMap = new HashMap<Integer, ValueType>();

        p.getTextProp().getValues()
                .forEach(v -> propMap.put(v.getOrder(), v.getValueType()));

        var textProp = new TextProp(
                p.getTextProp().getText(),
                p.getTextProp().getValues().size(),
                propMap
        );

        textValuesMap.put(p.getLanguage(), textProp);
    }

    private SimpleResponse<TemplateBaseRequestDTO> validateRequest(TemplateBaseRequestDTO requestDTO) {
        log.info("Validating request dto: {}", requestDTO);
        var listResult = requestDTO.getParams().stream().collect(Collectors.groupingBy(TemplateParams::getLanguage))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();


        if (!listResult.isEmpty()) {
            var message = "There are duplicate values in request!";
            log.error(message);
            return new SimpleResponse<>(1052, message);
        }

        AtomicBoolean isFail = new AtomicBoolean(false);

        requestDTO
                .getParams()
                .forEach(z -> {
                    var count = 0;

                    for (int i = 0; i < z.getTextProp().getText().length(); i++) {
                        var character = z.getTextProp().getText().charAt(i);
                        if (String.valueOf(character).equals("#"))
                            count++;
                    }


                    if (z.getTextProp().getValues().size() != count)
                        isFail.set(true);

                });


        if (isFail.get()) {
            log.error("isFail result is un success");
            return new SimpleResponse<>(1053, "text_props values size not matched with #'s size in text!");
        }

        return new SimpleResponse<>(requestDTO);
    }

    private SimpleResponse<Boolean> findTemplateByType(TemplateType type) {
        log.info("Find template by type: {}", type);

        var templateOptional = templateDAO.findAll()
                .stream()
                .filter(z -> z.getSmsType().equals(type))
                .findFirst();

        if (templateOptional.isPresent()) {
            var message = "Template: %s already has!".formatted(type);
            log.error(message);
            return new SimpleResponse<>(1054, message);
        }

        return new SimpleResponse<>(true);
    }

    @Override
    public ResultData<CoreError> evict() {
        log.info("Received command to evict cache");
        templateDAO.evictTemplateCache();

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to evict cache");
        return result;
    }

    @Override
    public ResultData<TemplateFilterResponseDTO> filter(@NonNull TemplateFilterRequestDTO requestDTO) {
        log.info("Received command to get templates with filter with request: {}", requestDTO);

        var specification = buildSpecification(requestDTO);
        var pageable = PageUtils.pageableBuilder(requestDTO);

        var pageResult = templateDAO.findAll(
                specification, pageable
        );


        var result = ResultData.ok(
                new TemplateFilterResponseDTO(
                        pageResult
                )
        );
        log.info("Finished command to get templates with response size: {}", result.getResult().getList().size());
        return result;
    }

    private Specification<Template> buildSpecification(TemplateFilterRequestDTO requestDTO) {
        Specification<Template> specification = Specification.where(null);

        var params = requestDTO.getParams();

        if (params != null) {

            if (params.getTypes() != null && !params.getTypes().isEmpty())
                specification = specification.and(createSpecByStatusInCondition(
                        Template_.TYPE, params.getTypes()
                                .stream().map(Enum::name)
                                .collect(Collectors.toList())
                ));

        }

        return specification;
    }

    @Override
    public ResultData<TemplateBaseResponseDTO> findById(@NonNull Long id) {
        log.info("Received command to find template by id: {}", id);

        var templateResult = findTemplateById(id);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }


        var result = ResultData.ok(
                new TemplateBaseResponseDTO(templateResult.getData())
        );

        log.info("Finished command to find template by id with response: {}", result);
        return result;
    }


    private SimpleResponse<Template> findTemplateById(Long id) {
        return templateDAO.findAll()
                .stream()
                .filter(z -> z.getId().equals(id))
                .findFirst()
                .map(SimpleResponse::new)
                .orElseGet(() -> new SimpleResponse<>(
                        ResultState.EntityNotFound.getCode(),
                        "Template with id: %d not found!".formatted(id)));
    }

    @Override
    public ResultData<CoreError> deleteById(@NonNull Long id) {
        log.info("Received command to delete notification by id: {}", id);

        var templateResult = findTemplateById(id);

        if (!templateResult.getStatus()) {
            log.error(templateResult.getError().getMessage());
            return new ResultData<>(templateResult.getError(), false);
        }

        templateDAO.deleteById(id);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );
        log.info("Finished command to delete notification with response: {}", result);
        return result;
    }

    @Override
    public ResultData<CoreError> update(@NonNull Long id, @NonNull TemplateBaseRequestDTO requestDTO) {
        log.info("Received command to update notification by id: {} with request: {}", id, requestDTO);

        var templateOptional = findTemplateById(id);

        if (!templateOptional.getStatus()) {
            log.error(templateOptional.getError().getMessage());
            return new ResultData<>(templateOptional.getError(), false);
        }

        var validateResult = validateAndCheckTemplate(requestDTO, false);

        if (!validateResult.getStatus())
            return new ResultData<>(validateResult.getError(), false);


        var template = templateOptional.getData();

        template.setSmsType(requestDTO.getType());

        var textValuesMap = new HashMap<Language, TextProp>();

        requestDTO.getParams()
                .forEach(p -> createTextValues(p, textValuesMap));

        template.setTextValues(textValuesMap);
        templateDAO.save(template);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );
        log.info("Finished command to update notification by id with response: {}", result);
        return result;
    }


    private SimpleResponse<TemplateBaseRequestDTO> validateAndCheckTemplate(TemplateBaseRequestDTO requestDTO, Boolean isNew) {
        var validateResult = validateRequest(requestDTO);

        if (!validateResult.getStatus())
            return new SimpleResponse<>(validateResult.getError().getCode(), validateResult.getError().getMessage());


        var templateResult = findTemplateByType(requestDTO.getType());

        if (!templateResult.getStatus()) {
            if (isNew)
                return new SimpleResponse<>(templateResult.getError().getCode(), templateResult.getError().getMessage());
        }

        return new SimpleResponse<>(requestDTO);
    }
}
