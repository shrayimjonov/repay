package nodir.paymo_add.feign.Svgate

import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(value = "svgate", url = "\${svgate.url}", configuration = [SvgateFeignConfig::class])
interface SvGateFeign {

    @PostMapping
    @Headers("Content-Type: application/json")
    fun <E> svgateConnect(@RequestBody value: E): Any?
}