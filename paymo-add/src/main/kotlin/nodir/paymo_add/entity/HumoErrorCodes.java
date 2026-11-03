package nodir.paymo_add.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HumoErrorCodes {
    @CsvBindByPosition(position = 0)
    public String codeNum;
    @CsvBindByPosition(position = 1)
    public String codeMessage;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
