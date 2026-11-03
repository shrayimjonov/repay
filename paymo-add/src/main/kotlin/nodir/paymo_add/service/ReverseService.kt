package nodir.paymo_add.service

import javax.servlet.ServletRequest


interface ReverseService {

    fun reverseTransaction(id: Long, servlet: ServletRequest)
}