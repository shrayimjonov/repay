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
import uz.davrbank.repay.loggingrest.dto.ActiveUserIpPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ActiveUsersIpRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiLogServiceImplActiveUsersByIpTest {

    @Mock
    private ApiLogDao dao;

    @InjectMocks
    private ApiLogServiceImpl service;

    @Test
    void should_keep_page_params_own_default_when_page_size_not_requested() {
        service.activeUsersByIp(new ActiveUsersIpRequest());

        assertThat(captured().getPageSize()).isEqualTo(3);
    }

    @Test
    void should_apply_service_default_page_size_when_non_positive() {
        var request = new ActiveUsersIpRequest();
        request.setPageSize(0);

        service.activeUsersByIp(request);

        assertThat(captured().getPageSize()).isEqualTo(20);
    }

    @Test
    void should_cap_page_size_at_the_maximum() {
        var request = new ActiveUsersIpRequest();
        request.setPageSize(5_000);

        service.activeUsersByIp(request);

        assertThat(captured().getPageSize()).isEqualTo(500);
    }

    @Test
    void should_reset_negative_page_number_to_first_page() {
        var request = new ActiveUsersIpRequest();
        request.setPageNumber(-3);

        service.activeUsersByIp(request);

        assertThat(captured().getPageNumber()).isZero();
    }

    @Test
    void should_keep_requested_paging_when_within_limits() {
        var request = new ActiveUsersIpRequest();
        request.setPageNumber(4);
        request.setPageSize(100);

        service.activeUsersByIp(request);

        assertThat(captured().getPageNumber()).isEqualTo(4);
        assertThat(captured().getPageSize()).isEqualTo(100);
    }

    @Test
    void should_pass_username_filter_through_untouched() {
        var request = new ActiveUsersIpRequest();
        request.setUsername("user@example.com");

        service.activeUsersByIp(request);

        assertThat(captured().getUsername()).isEqualTo("user@example.com");
    }

    @Test
    void should_return_the_result_from_the_dao() {
        var expected = new ActiveUserIpPageResponseDTO(List.of(), 0, 20, 0);
        when(dao.getActiveUsersByIp(any())).thenReturn(expected);

        var actual = service.activeUsersByIp(new ActiveUsersIpRequest());

        assertThat(actual).isSameAs(expected);
    }

    private ActiveUsersIpRequest captured() {
        var captor = ArgumentCaptor.forClass(ActiveUsersIpRequest.class);
        verify(dao).getActiveUsersByIp(captor.capture());
        return captor.getValue();
    }
}
