package uz.nodir.client.service.business

import uz.nodir.client.model.dto.yhxbb.response.YHXBBPersonCarResponseDTO
import uz.repay.common.model.dto.response.core.ResultData



interface YHXBBService {

    fun personCars(personCode: String): ResultData<YHXBBPersonCarResponseDTO>
}