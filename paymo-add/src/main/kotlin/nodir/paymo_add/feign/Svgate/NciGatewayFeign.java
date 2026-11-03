package nodir.paymo_add.feign.Svgate;

import nodir.paymo_add.feign.Request.LoanTransactionReq;
import nodir.paymo_add.model.GeneralArh;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "nciGateway", url = "${nci.gateway}")
public interface NciGatewayFeign {
    @PostMapping("/loan/transactions")
    List<GeneralArh> getLoanTransactionsByDate(@RequestBody LoanTransactionReq request);
}
