package nodir.paymo_add.feign.Genesis

import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(value = "genesis", url = "\${genesis.url}", configuration = [GenesisFeignConfig::class])
interface GenesisFeign {

    @PostMapping
    @Headers("Content-Type: application/json")
    fun <E> genesisConnect(@RequestBody value: E): Any?
}