package fr.sjcqs.utils

class NoOpLogger : Logger {
    override fun l(message: String, vararg args: Any) {
        /* no-op */
    }

    override fun l(t: Throwable, message: String, vararg args: Any) {
        /* no-op */
    }

    override fun d(message: String, vararg args: Any) {
        /* no-op */
    }

    override fun d(t: Throwable, message: String, vararg args: Any) {
        /* no-op */
    }

    override fun i(message: String, vararg args: Any) {
        /* no-op */
    }

    override fun i(t: Throwable, message: String, vararg args: Any) {
        /* no-op */
    }

    override fun w(message: String, vararg args: Any) {
        /* no-op */
    }

    override fun w(t: Throwable, message: String, vararg args: Any) {
        /* no-op */
    }

    override fun e(message: String, vararg args: Any) {
        /* no-op */
    }

    override fun e(t: Throwable, message: String, vararg args: Any) {
        /* no-op */
    }
}