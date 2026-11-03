package uz.davrbank.repay.loggingrest.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uz.davrbank.repay.loggingrest.dao.ApiLogDao;
import uz.davrbank.repay.loggingrest.dto.ActiveSessionPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ActiveSessionsRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiLogServiceImplActiveSessionsTest {

    @Mock
    private ApiLogDao dao;

    @InjectMocks
    private ApiLogServiceImpl service;

    @Test
    void should_apply_service_default_page_size_when_non_positive() {
        var request = new ActiveSessionsRequest();
        request.setPageSize(0);

        service.activeSessions(request);

        assertThat(captured().getPageSize()).isEqualTo(20);
    }

    @Test
    void should_cap_page_size_at_the_maximum() {
        var request = new ActiveSessionsRequest();
        request.setPageSize(5_000);

        service.activeSessions(request);

        assertThat(captured().getPageSize()).isEqualTo(500);
    }

    @Test
    void should_reset_negative_page_number_to_first_page() {
        var request = new ActiveSessionsRequest();
        request.setPageNumber(-3);

        service.activeSessions(request);

        assertThat(captured().getPageNumber()).isZero();
    }

    @Test
    void should_keep_requested_paging_when_within_limits() {
        var request = new ActiveSessionsRequest();
        request.setPageNumber(4);
        request.setPageSize(100);

        service.activeSessions(request);

        assertThat(captured().getPageNumber()).isEqualTo(4);
        assertThat(captured().getPageSize()).isEqualTo(100);
    }

    @Test
    void should_pass_username_filter_through_untouched() {
        var request = new ActiveSessionsRequest();
        request.setUsername("user@example.com");

        service.activeSessions(request);

        assertThat(captured().getUsername()).isEqualTo("user@example.com");
    }

    @Test
    void should_return_the_result_from_the_dao() {
        var expected = new ActiveSessionPageResponseDTO(List.of(), 0, 20, 0);
        when(dao.getActiveSessions(any())).thenReturn(expected);

        var actual = service.activeSessions(new ActiveSessionsRequest());

        assertThat(actual).isSameAs(expected);
    }

    private ActiveSessionsRequest captured() {
        var captor = ArgumentCaptor.forClass(ActiveSessionsRequest.class);
        verify(dao).getActiveSessions(captor.capture());
        return captor.getValue();
    }
}
