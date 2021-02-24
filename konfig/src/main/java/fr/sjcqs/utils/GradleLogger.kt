package fr.sjcqs.utils

import org.gradle.api.logging.Logging.getLogger
import kotlin.reflect.KClass
import org.gradle.api.logging.Logger as BaseGradleLogger

class GradleLogger<out T : Any>(instance: KClass<T>? = null) : Logger {
    private val logger: BaseGradleLogger = if (instance != null) {
        getLogger(instance.java)
    } else {
        getLogger(DEFAULT_TAG)
    }

    override fun l(message: String, vararg args: Any) {
        logger.lifecycle(message, args)
    }

    override fun l(t: Throwable, message: String, vararg args: Any) {
        logger.lifecycle(String.format(message, args), t)
    }

    override fun d(message: String, vararg args: Any) {
        logger.debug(message, args)
    }

    override fun d(t: Throwable, message: String, vararg args: Any) {
        logger.debug(String.format(message, args), t)
    }

    override fun i(message: String, vararg args: Any) {
        logger.info(message, args)
    }

    override fun i(t: Throwable, message: String, vararg args: Any) {
        logger.info(String.format(message, args), t)
    }

    override fun w(message: String, vararg args: Any) {
        logger.warn(message, args)
    }

    override fun w(t: Throwable, message: String, vararg args: Any) {
        logger.warn(String.format(message, args), t)
    }

    override fun e(message: String, vararg args: Any) {
        logger.error(message, args)
    }

    override fun e(t: Throwable, message: String, vararg args: Any) {
        logger.error(String.format(message, args), t)
    }

    companion object {
        private const val DEFAULT_TAG = "Logger"
    }
}
