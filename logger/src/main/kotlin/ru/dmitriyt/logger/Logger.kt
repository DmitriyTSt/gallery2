package ru.dmitriyt.logger

interface Logger {
    fun d(message: String)
    fun e(e: Throwable)
    fun e(message: String)
}

fun Logger(): Logger = LoggerImpl()