package nodir.paymo_add.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "svgate_uzcard_error_codes")
public class SvgateErrorCodes {
    @SequenceGenerator(name = "svgate_card_error_seq",
    sequenceName = "svgate_card_error_seq",
    allocationSize = 1)
    @GeneratedValue(generator = "svgate_card_error_seq",
    strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    public String codeNum;
    public String codeMessage;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SvgateErrorCodes that = (SvgateErrorCodes) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
