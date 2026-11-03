package uz.nodir.file.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;

@Getter
@Setter
@ToString
@Entity
@Table(name = "location",
        indexes = {
                @Index(name = "location_loan_ext_id_ind", columnList = "loan_ext_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class Location extends BaseEntity {
    @SequenceGenerator(
            name = "location_seq",
            sequenceName = "location_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "location_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Id
    private Long id;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "loan_ext_id")
    private String loanExtId;

    @Column(name = "description")
    private String description;

    public Location(String latitude, String longitude, String loanExtId, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.loanExtId = loanExtId;
        this.description = description;
    }
}
