package nodir.paymo_add.service

import nodir.paymo_add.model.GeneralArh
import nodir.paymo_add.model.GeneralArhDTO

interface NciService {

    fun takeTransactionAccount(loanId: String) : MutableList<List<GeneralArhDTO>>
}