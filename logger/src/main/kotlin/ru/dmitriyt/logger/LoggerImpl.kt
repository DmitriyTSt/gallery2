package ru.dmitriyt.logger

internal class LoggerImpl : Logger {
    override fun d(message: String) {
        log("\uD83D\uDCD8: ", message)
    }

    override fun e(e: Throwable) {
        e.printStackTrace()
    }

    override fun e(message: String) {
        log("", message)
    }

    private fun log(prefix: String, message: String, suffix: String = "") {
        println(prefix + message + suffix)
    }
}