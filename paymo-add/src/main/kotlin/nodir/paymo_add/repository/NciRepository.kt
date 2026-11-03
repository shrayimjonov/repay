package nodir.paymo_add.repository

import lombok.extern.slf4j.Slf4j
import nodir.paymo_add.model.DateDeserializer
import nodir.paymo_add.model.GeneralArh
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Date
import kotlin.math.log


@Repository

class NciRepository(
     private val jdbcTemplate: JdbcTemplate,
    private val dateDeserializer: DateDeserializer
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)


    fun findTransactionByLoanAccount(branch: String, loan: String): List<GeneralArh> {
        val dateFrom: String? = dateDeserializer.dateDeserialize(LocalDate.now().minusDays(10).toString())
        return try {
            val sql =
                "select g.branch,g.v_date,g.acc_cl,g.name_cl,g.acc_co,g.name_co, round(g.summa/100,2) as summa, g.purpose, s.name as status from general_arh g, state_general s" +
                        " where g.acc_cl like '17%' and g.acc_co in (select a.id from account a, bank$branch.ld_forms b" +
                        " where a.branch = b.branch and a.client = b.client" +
                        " and b.id = $loan and acc_bal like '29%') and g.v_date >= to_date('$dateFrom','dd.mm.yyyy') \n" +
                        "and g.s_deal_id = s.deal_id and g.state = s.id" +
                        " union all" +

                        " select g.branch,g.v_date,g.acc_cl,g.name_cl,g.acc_co,g.name_co, round(g.summa/100,2) as summa, g.purpose, s.name as status from general g, state_general s\n" +
                        " where g.acc_cl like '17%' and g.acc_co in (select a.id from account a, bank$branch.ld_forms b" +
                        " where a.branch = b.branch and a.client = b.client" +
                        " and b.id = $loan and acc_bal like '29%') \n" +
                        " and g.s_deal_id = s.deal_id and g.state = s.id"
            jdbcTemplate.query(sql, DataClassRowMapper(GeneralArh::class.java))
        } finally {
            try {
                jdbcTemplate.dataSource!!.connection.close()
            } catch (exception: Exception) {
                log.info("close exception: $exception")
            }
        }
    }

    fun findReverseTransactionByLoanAccount(branch: String, loan: String): List<GeneralArh> {
        val dateFrom: String? = dateDeserializer.dateDeserialize(LocalDate.now().minusDays(10).toString())
        return try {
            val sql =
                "select g.branch,g.v_date,g.acc_cl,g.name_cl,g.acc_co,g.name_co, round(g.summa/100,2) as summa, g.purpose, s.name as status  from general_arh g, state_general s\n" +
                        "where g.acc_co like '17%' and g.acc_cl in (select a.id from account a, bank$branch.ld_forms b \n" +
                        "where a.branch = b.branch and a.client = b.client\n" +
                        "and b.id = $loan and acc_bal like '29%') and g.v_date >= to_date('$dateFrom','dd.mm.yyyy') \n" +
                        "and g.s_deal_id = s.deal_id and g.state = s.id \n" +
                        "union all" +

                        " select g.branch,g.v_date,g.acc_cl,g.name_cl,g.acc_co,g.name_co,  round(g.summa/100,2) as summa, g.purpose, s.name as status  from general g, state_general s \n" +
                        "where g.acc_co like '17%' and g.acc_cl in (select a.id from account a, bank$branch.ld_forms b \n" +
                        "where a.branch = b.branch and a.client = b.client\n" +
                        "and b.id = $loan and acc_bal like '29%') \n" +
                        "and g.s_deal_id = s.deal_id and g.state = s.id"
            jdbcTemplate.query(sql, DataClassRowMapper(GeneralArh::class.java))
        } finally {
            try {
                jdbcTemplate.dataSource!!.connection.close()
            } catch (exception: Exception) {
                log.info("close exception: $exception")
            }
        }
    }

}