package uz.repay.payment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uz.repay.payment.model.dto.core.fraud.FraudClearListDTO;
import uz.repay.payment.service.core.FraudRedisService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FraudControllerTest {

    @Mock
    private FraudRedisService fraudRedisService;

    @InjectMocks
    private FraudController fraudController;

    @Test
    void removeFraudsByLoanShouldDelegateToServiceAndReturnOk() {
        ResponseEntity<?> response = fraudController.removeFraudsByLoan("loan-1");

        verify(fraudRedisService).deleteByLoan("loan-1");
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void removeFraudsByLoanIdsShouldDelegateToServiceAndReturnOk() {
        FraudClearListDTO request = new FraudClearListDTO(List.of("loan-1", "loan-2"));

        ResponseEntity<?> response = fraudController.removeFraudsByLoanIds(request);

        verify(fraudRedisService).deleteByLoanExternalIds(request);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
