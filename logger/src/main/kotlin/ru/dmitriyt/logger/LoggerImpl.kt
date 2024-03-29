package ru.dmitriyt.logger

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_BLACK = "\u001B[30m"
private const val ANSI_RED = "\u001B[31m"
private const val ANSI_GREEN = "\u001B[32m"
private const val ANSI_YELLOW = "\u001B[33m"
private const val ANSI_BLUE = "\u001B[34m"
private const val ANSI_PURPLE = "\u001B[35m"
private const val ANSI_CYAN = "\u001B[36m"
private const val ANSI_WHITE = "\u001B[37m"

internal class LoggerImpl : ILogger {
    override fun d(message: Any) {
        log(LogLevel.DEBUG, message.toString())
    }

    override fun w(message: Any) {
        log(LogLevel.WARNING, message.toString())
    }

    override fun e(e: Throwable) {
        e.printStackTrace()
    }

    override fun e(message: String) {
        log(LogLevel.ERROR, message)
    }

    private fun log(level: LogLevel, message: String) {
        val logMessage = buildString {
            val prefix = when (level) {
                LogLevel.DEBUG -> ANSI_CYAN
                LogLevel.WARNING -> ANSI_YELLOW
                LogLevel.ERROR -> ANSI_RED
            }
            append(prefix)
            append(message)
            append(ANSI_RESET)
        }
        println(logMessage)
    }
}