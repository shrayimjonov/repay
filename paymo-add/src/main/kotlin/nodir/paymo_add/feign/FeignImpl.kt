package nodir.paymo_add.feign

interface FeignImpl {
    fun <E> svgateConnect(value: E): Any?

    fun <E> genesisConnect(value: E): Any?

}