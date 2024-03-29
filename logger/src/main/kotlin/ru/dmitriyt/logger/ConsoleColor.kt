package ru.dmitriyt.logger

internal const val ConsoleColorReset: String = "\u001B[0m"

internal sealed class ConsoleColor(
    val value: String
) {

    data object Black : ConsoleColor(value = "\u001B[30m")
    data object Red : ConsoleColor(value = "\u001B[31m")
    data object Green : ConsoleColor(value = "\u001B[32m")
    data object Yellow : ConsoleColor(value = "\u001B[33m")
    data object Blue : ConsoleColor(value = "\u001B[34m")
    data object Purple : ConsoleColor(value = "\u001B[35m")
    data object Cyan : ConsoleColor(value = "\u001B[36m")
    data object White : ConsoleColor(value = "\u001B[37m")

    override fun toString(): String {
        return value
    }
}