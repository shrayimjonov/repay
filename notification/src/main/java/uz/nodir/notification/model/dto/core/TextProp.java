package uz.nodir.notification.model.dto.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.ValueType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextProp implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    private String text;
    private int valuesCount;
    private Map<Integer, ValueType> valueType;
}
