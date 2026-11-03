package uz.nodir.file.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;

@Getter
@Setter
@ToString
@Entity
@Table(name = "files",
        indexes = {
                @Index(name = "file_loanId_ind", columnList = "loan_ext_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class FileSaver extends BaseEntity {
    @SequenceGenerator(
            name = "file_save_seq",
            sequenceName = "file_save_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "file_save_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "loan_ext_id")
    private String loanExtId;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String contentType;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "is_internal")
    private Boolean isInternal;

    public FileSaver(String loanExtId, String fileName, String contentType, String description, boolean isInternal) {
        this.loanExtId = loanExtId;
        this.name = fileName;
        this.contentType = contentType;
        this.description = description;
        this.isInternal = isInternal;
    }
}
