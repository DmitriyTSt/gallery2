package ru.dmitriyt.logger

interface ILogger {
    fun d(message: Any)

    fun w(message: Any)

    fun e(e: Throwable)
    fun e(message: String)
}