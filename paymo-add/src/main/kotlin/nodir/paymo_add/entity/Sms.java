package nodir.paymo_add.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "message")
public class Sms {
    @Id
    private Long id;
    private String message_id;
    private String originator;
    private String recipient;
    private String text;
    private String STATUS;
    private String instime;
}
