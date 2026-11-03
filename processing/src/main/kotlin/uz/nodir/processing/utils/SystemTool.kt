package uz.nodir.processing.utils

class SystemTool {

    companion object{
        @JvmStatic
        fun requestIDGenerator() = "DVR_REPAY_${System.currentTimeMillis()}"
    }
}