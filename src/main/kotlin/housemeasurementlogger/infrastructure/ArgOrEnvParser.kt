package housemeasurementlogger.infrastructure

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class ArgOrEnvParser(
    private val applicationName: String,
    private val args: Array<String>,
    private val env: Map<String, String>
) {
    interface CastableToString {
        override fun toString(): String
    }

    interface CastableToInt {
        fun toInt(): Int
    }

    interface CastableToDouble {
        fun toDouble(): Double
    }

    interface CastableToLong {
        fun toLong(): Long
    }

    private val parser = ArgParser(applicationName)

    fun parse() = parser.parse(args)

    fun requiredString(argName: String, envName: String): CastableToString {
        val value by parser.option(
            ArgType.String,
            argName,
        )

        return object : CastableToString {
            override fun toString(): String {
                return (env[envName] ?: value)
                    ?: throw RuntimeException("Argument --$argName or env variable $envName must be set")
            }
        }
    }

    fun optionalString(argName: String, envName: String, default: String): CastableToString {
        val value by parser.option(
            ArgType.String,
            argName,
        )

        return object : CastableToString {
            override fun toString(): String {
                return ((env[envName] ?: value) ?: default).toString()
            }
        }
    }

    fun requiredInt(argName: String, envName: String): CastableToInt {
        val value by parser.option(
            ArgType.Int,
            argName,
        )

        return object : CastableToInt {
            override fun toInt(): Int {
                return (env[envName]?.toInt() ?: value)
                    ?: throw RuntimeException("Argument --$argName or env variable $envName must be set")
            }
        }
    }

    fun optionalInt(argName: String, envName: String, default: Int): CastableToInt {
        val value by parser.option(
            ArgType.Int,
            argName,
        )

        return object : CastableToInt {
            override fun toInt(): Int {
                return ((env[envName]?.toInt() ?: value) ?: default).toInt()
            }
        }
    }

    fun requiredDouble(argName: String, envName: String): CastableToDouble {
        val value by parser.option(
            ArgType.Double,
            argName,
        )

        return object : CastableToDouble {
            override fun toDouble(): Double {
                return (env[envName]?.toDouble() ?: value)
                    ?: throw RuntimeException("Argument --$argName or env variable $envName must be set")
            }
        }
    }

    fun optionalLong(argName: String, envName: String, default: Long): CastableToLong {
        val value by parser.option(
            ArgType.Int,
            argName,
        )

        return object : CastableToLong {
            override fun toLong(): Long {
                return ((env[envName]?.toInt() ?: value) ?: default).toLong()
            }
        }
    }
}
