package uz.nodir.court.client.decoder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.ErrorDecoder
import uz.nodir.court.model.dto.chambers.response.error.ChamberErrorResponse
import uz.repay.common.exception.GeneralErrorException
import java.io.IOException



class ChamberErrorDecoder(
    private val errorDecoder: ErrorDecoder = ErrorDecoder.Default(),
) : ErrorDecoder {

    override fun decode(p0: String?, p1: Response?): Exception {
        var errorResponse: ChamberErrorResponse

        try {
            if (p1 == null)
                throw GeneralErrorException("Response is null for decoding in chambers error decoder!")


            if (p1.body() == null)
                throw GeneralErrorException("Response body is null for decoding in chambers error decoder!")

            p1.body().asInputStream().use { bodyIs ->
                    val mapper = ObjectMapper()
                    errorResponse = mapper.readValue(
                        bodyIs,
                        object : TypeReference<ChamberErrorResponse>() {
                        })
                }
        } catch (e: IOException) {
            return Exception(e.message)
        }


        return when (p1.status()) {
            400 -> GeneralErrorException(if (!errorResponse.errors?.message.isNullOrEmpty()) errorResponse.errors?.message?.first() else "Chambers message is null in 400 error")
            404 -> GeneralErrorException(if (!errorResponse.errors?.message.isNullOrEmpty()) errorResponse.errors?.message?.first() else "Chambers message is null in 404 error")
            500 -> GeneralErrorException(if (!errorResponse.errors?.message.isNullOrEmpty()) errorResponse.errors?.message?.first() else "Chambers message is null in 500 error")
            else -> errorDecoder.decode(p0, p1)
        }
    }
}