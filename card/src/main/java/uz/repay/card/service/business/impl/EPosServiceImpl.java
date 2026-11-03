package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.epos.EPosCreateRequestDTO;
import uz.repay.card.model.dto.core.request.epos.EPosExceptionalDTO;
import uz.repay.card.model.dto.core.response.epos.EposResponseListDTO;
import uz.repay.card.model.entity.EPos;
import uz.repay.card.service.business.EPosService;
import uz.repay.card.service.core.EPosDAO;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EPosServiceImpl implements EPosService {
    private final EPosDAO ePosDAO;

    @Override
    public ResultData<EPosResponseDTO> create(@NonNull EPosCreateRequestDTO requestDTO) {
        log.info("Create ePos request: {}", requestDTO);
        var ePos = new EPos(requestDTO);

        return new ResultData<>(entity2dto(ePosDAO.save(ePos)));
    }

    public EPosResponseDTO entity2dto(EPos EPos) {
        return new EPosResponseDTO(
                EPos.getId(),
                EPos.getBranch(),
                EPos.getProcessing(),
                EPos.getMerchantId(),
                EPos.getService(),
                EPos.getTerminalId(),
                EPos.getAccount()
        );
    }

    public EposResponseListDTO entityToDTO(EPos EPos) {
        return new EposResponseListDTO(
                EPos.getId(),
                EPos.getBranch(),
                EPos.getProcessing(),
                EPos.getMerchantId(),
                EPos.getService(),
                EPos.getTerminalId(),
                EPos.getAccount(),
                Optional.ofNullable(EPos.getExceptional()).orElse(Boolean.FALSE)
        );
    }

    @Override
    public ResultData<EPosResponseDTO> update(@NonNull Long id, @NonNull EPosCreateRequestDTO requestDTO) {
        var terminal = findById(id);

        log.info("Updating ePos From: {} To: {}", terminal, requestDTO);
        terminal.setMerchantId(requestDTO.getMerchantId());
        terminal.setTerminalId(requestDTO.getTerminalId());
        terminal.setAccount(requestDTO.getAccount());
        terminal.setService(requestDTO.getService());
        terminal.setProcessing(requestDTO.getProcessing());
        terminal.setBranch(requestDTO.getBranch());

        return new ResultData<>(entity2dto(ePosDAO.save(terminal)));
    }


    private EPos findById(Long id) {
        return ePosDAO
                .getAll()
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ePos with id: %d not found".formatted(id)));
    }

    @Override
    public ResultData<List<EposResponseListDTO>> getAll() {
        return new ResultData<>(ePosDAO
                .getAll()
                .stream()
                .sorted(Comparator.comparing(EPos::getId))
                .map(this::entityToDTO)
                .toList());
    }

    @Override
    public ResultData<EPosResponseDTO> delete(@NonNull Long id) {
        var ePos = findById(id);
        log.info("Remove ePos with id: {} from database", id);

        ePosDAO.deleteById(id);

        return new ResultData<>(entity2dto(ePos));
    }

    @Override
    public ResultData<String> evictCache() {
        ePosDAO.evictTerminalCache();
        return new ResultData<>("CACHE EVICTED");
    }

    @Override
    public void makeExceptional(EPosExceptionalDTO requestDTO) {
        ePosDAO.makeExceptional(requestDTO.getTerminalIds());
    }
}
