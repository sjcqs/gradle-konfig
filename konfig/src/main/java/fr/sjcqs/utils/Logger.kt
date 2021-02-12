package fr.sjcqs.utils

interface Logger {

    fun l(message: String, vararg args: Any)
    fun l(t: Throwable, message: String, vararg args: Any)

    fun d(message: String, vararg args: Any)
    fun d(t: Throwable, message: String, vararg args: Any)

    fun i(message: String, vararg args: Any)
    fun i(t: Throwable, message: String, vararg args: Any)

    fun w(message: String, vararg args: Any)
    fun w(t: Throwable, message: String, vararg args: Any)

    fun e(message: String, vararg args: Any)
    fun e(t: Throwable, message: String, vararg args: Any)
}