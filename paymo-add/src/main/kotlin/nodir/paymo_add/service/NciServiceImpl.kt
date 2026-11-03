package nodir.paymo_add.service

import lombok.extern.slf4j.Slf4j
import nodir.paymo_add.feign.Request.LoanTransactionReq
import nodir.paymo_add.feign.Svgate.NciGatewayFeign
import nodir.paymo_add.model.GeneralArh
import nodir.paymo_add.model.GeneralArhDTO
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.log

@Service

class NciServiceImpl(private val nciGatewayFeign: NciGatewayFeign) : NciService {

    override fun takeTransactionAccount(loanId: String): MutableList<List<GeneralArhDTO>> {
        val branch: String = loanId.substring(0, 5)
        val loan: String = loanId.substring(5)
        val dateBuilder = LocalDate.now()
            .minusDays(15)
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));


        val to29: List<GeneralArh> = nciGatewayFeign.getLoanTransactionsByDate(
            LoanTransactionReq(
                branch,
                loan,
                dateBuilder
            )
        )


        return mutableListOf(to29.map(this::jsonToDTO))
    }

    fun jsonToDTO(generalArh: GeneralArh): GeneralArhDTO {
        return GeneralArhDTO(
            generalArh.branch,
            generalArh.acc_CL,
            generalArh.name_CL,
            generalArh.acc_CO,
            generalArh.name_CO,
            generalArh.purpose,
            generalArh.summa,
            generalArh.v_DATE,
            generalArh.status
        )

    }


}