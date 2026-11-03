package uz.nodir.client.util

import com.fasterxml.jackson.databind.util.StdConverter
import uz.repay.common.exception.GeneralErrorException
import uz.repay.common.exception.InvalidDataException
import uz.repay.common.model.enums.Sex



class JusticeGenderConverter : StdConverter<Int, Sex>() {

    override fun convert(p0: Int?): Sex =
        when (p0) {
            1 -> Sex.MALE
            2 -> Sex.FEMALE
            null -> throw GeneralErrorException("Int value for gender is null")
            else -> throw InvalidDataException("Invalid gender code $p0")
        }

}