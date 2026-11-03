package uz.repay.common.model.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseProperty {
    private String url;
    private Long readTimeOut;
    private Long connectTimeOut;
}
