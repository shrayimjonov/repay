package nodir.paymo_add.model

import lombok.Getter
import lombok.Setter
import java.io.Serializable
import java.time.LocalDate

@Getter
@Setter
data class GeneralArhDTO (
    var branch: String,
    var acc_cl: String,
    var name_cl: String,
    var acc_co: String,
    var name_co: String,
    var purpose: String,
    var summa: String,
    var v_date: String,
    var status: String
)
