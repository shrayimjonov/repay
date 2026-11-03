package uz.nodir.notification.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.nodir.notification.model.dto.core.TextProp;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Language;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sms_template")
@AllArgsConstructor
@NoArgsConstructor
public class Template extends BaseEntity {

    @SequenceGenerator(name = "sms_temp_seq",
            sequenceName = "sms_temp_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "sms_temp_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "sms_type", unique = true)
    @Enumerated(EnumType.STRING)
    private TemplateType smsType;

    @Column(name = "text_values")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<Language, TextProp> textValues = new HashMap<>();


    public Template(TemplateType type, HashMap<Language, TextProp> textValuesMap) {
        this.smsType = type;
        this.textValues = textValuesMap;
    }
}
