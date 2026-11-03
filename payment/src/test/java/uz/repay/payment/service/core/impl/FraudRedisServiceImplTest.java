package uz.repay.payment.service.core.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.repay.payment.model.dto.core.fraud.FraudClearListDTO;
import uz.repay.payment.redis.entity.Fraud;
import uz.repay.payment.redis.repository.FraudRepository;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudRedisServiceImplTest {

    @Mock
    private FraudRepository repository;

    @InjectMocks
    private FraudRedisServiceImpl fraudRedisService;

    @Test
    void deleteByLoanExternalIdsShouldDeleteMatchedFraudsWhenLoanIdsProvided() {
        Fraud fraud = new Fraud();
        FraudClearListDTO request = new FraudClearListDTO(List.of("loan-1", "loan-2"));
        List<Fraud> frauds = List.of(fraud);

        when(repository.findAllByLoanIdIn(request.getLoanExternalIds())).thenReturn(frauds);

        fraudRedisService.deleteByLoanExternalIds(request);

        verify(repository).findAllByLoanIdIn(request.getLoanExternalIds());
        verify(repository).deleteAll(frauds);
    }

    @Test
    void deleteByLoanExternalIdsShouldDoNothingWhenRequestIsNull() {
        fraudRedisService.deleteByLoanExternalIds(null);

        verifyNoInteractions(repository);
    }

    @Test
    void deleteByLoanExternalIdsShouldDoNothingWhenLoanIdsAreEmpty() {
        FraudClearListDTO request = new FraudClearListDTO(List.of());

        fraudRedisService.deleteByLoanExternalIds(request);

        verifyNoInteractions(repository);
    }
}
