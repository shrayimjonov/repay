package nodir.paymo_add.model

import lombok.Getter
import lombok.Setter
import java.io.Serializable
import java.time.LocalDate

@Getter
@Setter
data class GeneralArh (
    var id: String,
    var branch: String,
    var acc_CL: String,
    var name_CL: String,
    var acc_CO: String,
    var name_CO: String,
    var purpose: String,
    var summa: String,
    var v_DATE: String,
    var status: String

)
