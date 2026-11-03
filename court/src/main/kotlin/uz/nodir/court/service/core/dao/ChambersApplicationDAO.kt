package uz.nodir.court.service.core.dao

import uz.nodir.court.model.entity.ChambersApplication
import java.util.Optional

interface ChambersApplicationDAO {

    fun save(chambersApplication: ChambersApplication): ChambersApplication

    fun findByLoanExtId(loanExtId: String): List<ChambersApplication>

    fun findByChamberId(chamberId: String): ChambersApplication?
}