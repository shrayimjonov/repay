package nodir.paymo_add.feign.Request

data class SvgateReverse(var jsonrpc: String,
                         var method: String,
                         var params: Params,
                         var id: String
) {

      data class Params(var extId: String)

}