package uz.repay.payment.model.dto.core.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.entity.meta.TransactionMonthlyDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsMonthlyResponseDTO {
    private String branch;
    private List<TransByDate> trans = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransByDate {
        private String date;
        private List<TransInfo> info = new ArrayList<>();

        public TransByDate(TransactionMonthlyDTO dto) {
            this.date = dto.getCreated_date();
            getInfo().add(new TransInfo(dto.getPay_type(), dto.getCount(), dto.getAmount()));
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransInfo {

        private String type;
        private Integer count;
        private Long amount;

        public TransInfo(TransactionMonthlyDTO dto) {
            this.type = dto.getPay_type();
            this.count = dto.getCount();
            this.amount = dto.getAmount();
        }
    }


    public TransactionsMonthlyResponseDTO(String branch) {
        this.branch = branch;
    }
}
