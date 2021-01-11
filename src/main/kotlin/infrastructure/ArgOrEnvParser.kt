package infrastructure

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

interface CastableToInt {
    fun toInt(): Int
}

interface CastableToString {
    override fun toString(): String
}

class ArgOrEnvParser(
    private val applicationName: String,
    private val args: Array<String>,
    private val env: Map<String, String>
) {

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
}
