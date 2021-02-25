package fr.sjcqs.transpiler.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import fr.sjcqs.ast.Token
import fr.sjcqs.transpiler.FileTranspiler
import fr.sjcqs.transpiler.FileWriter
import fr.sjcqs.utils.Logger
import java.util.Date

class KotlinFileTranspiler(private val logger: Logger) : FileTranspiler {

    override fun transpile(packageName: String, root: Token.Root): FileWriter {
        logger.i("Creating file specs")
        return KotlinFileWriter(root.toFileSpec(packageName))
    }

    private fun Token.Root.toFileSpec(packageName: String): FileSpec {
        return FileSpec.builder(packageName, Token.Root.ROOT_KEY)
            .addType(toTypeSpecBuilder(packageName).build())
            .build()
    }

    private val Token.generateTypeSpec: Boolean
        get() {
            return when (this) {
                is Token.Root,
                is Token.Class -> true
                else -> false
            }
        }

    private fun Token.toTypeSpecBuilder(packageName: String): TypeSpec.Builder {
        val className = this.getClassName(packageName)
        return when (this) {
            is Token.Root -> {
                val typeSpecBuilder = TypeSpec.objectBuilder(className)
                typeSpecBuilder.addElementsPropertySpec(
                    className.toString(),
                    settings,
                    true,
                    null
                )
            }
            is Token.Class -> {
                val constructor = FunSpec.constructorBuilder()
                val initial = TypeSpec.classBuilder(className)
                    .addModifiers(KModifier.DATA)
                initial.addElementsPropertySpec(
                    className.toString(),
                    settings,
                    false,
                    constructor
                )
                    .primaryConstructor(constructor.build())
            }
            else -> throw IllegalArgumentException("UnsupportedType: $key")
        }
    }

    private fun TypeSpec.Builder.addElementsPropertySpec(
        packageName: String,
        list: List<Token>,
        isTopLevel: Boolean,
        constructor: FunSpec.Builder?,
    ): TypeSpec.Builder {
        return addProperties(
            list
                .map { element ->
                    element.toPropertySpec(packageName, isTopLevel, constructor, this)
                }
        )
    }

    private fun Token.toPropertySpec(
        packageName: String,
        isTopLevel: Boolean,
        constructor: FunSpec.Builder?,
        parentTypeSpecBuilder: TypeSpec.Builder,
    ): PropertySpec {
        val propertySpec = toPropertySpec(packageName, isTopLevel)

        if (this is Token.KList) {
            val last = settings.last()
            if (last.generateTypeSpec) {
                val elementTypeSpec = last.toTypeSpecBuilder(packageName).build()
                parentTypeSpecBuilder.addType(elementTypeSpec)
            }
        }

        if (this.generateTypeSpec) {
            val type = toTypeSpecBuilder(packageName).build()
            parentTypeSpecBuilder.addType(type)
        }

        if (constructor != null) {
            val parameter = ParameterSpec
                .builder(propertySpec.name, propertySpec.type)
                .build()
            constructor.addParameter(parameter)
        }

        return propertySpec
    }

    private fun Token.toPropertySpec(
        packageName: String,
        isTopLevel: Boolean,
    ): PropertySpec {
        val builder = when (this) {
            is Token.Class -> toPropertySpecBuilder(packageName, isTopLevel)
            is Token.KList -> toPropertySpecBuilder(packageName, isTopLevel)
            is Token.Field<*> -> toPropertySpecBuilder(packageName, isTopLevel)
            else -> throw IllegalArgumentException("UnsupportedType: $key")
        }
        builder.addModifiers(KModifier.PUBLIC)
        if (!builder.modifiers.contains(KModifier.CONST)) {
            builder.addAnnotation(JvmField::class)
        }
        return builder.build()
    }

    private fun Token.Class.toPropertySpecBuilder(
        packageName: String,
        isTopLevel: Boolean
    ): PropertySpec.Builder {
        val builder = PropertySpec.builder(fieldName, this.getClassName(packageName))
        if (!isTopLevel) {
            return builder.initializer(fieldName)
        }
        return builder.initializer(initializer(packageName))
    }

    private fun Token.KList.toPropertySpecBuilder(
        packageName: String,
        isTopLevel: Boolean
    ): PropertySpec.Builder {
        val builder = PropertySpec.builder(fieldName, this.getClassName(packageName))
        if (!isTopLevel) {
            return builder.initializer(fieldName)
        }
        return builder.initializer(initializer(packageName))
    }

    private fun <T : Any> Token.Field<T>.toPropertySpecBuilder(
        packageName: String,
        isTopLevel: Boolean
    ): PropertySpec.Builder {
        val builder = PropertySpec.builder(fieldName, kClass)
        if (kClass in Token.Field.CONSTANT_TYPES && isTopLevel) {
            builder.addModifiers(KModifier.CONST)
        }
        if (!isTopLevel) {
            return builder.initializer(fieldName)
        }
        val codeBlock = initializer(packageName)
        return builder.initializer(codeBlock)
    }

    private fun Token.initializer(packageName: String): CodeBlock {
        return when (this) {
            is Token.Field<*> -> initializer()
            is Token.Class -> initializer(packageName)
            is Token.KList -> initializer(packageName)
            else -> CodeBlock.of("")
        }
    }

    private fun Token.KList.initializer(packageName: String): CodeBlock {
        val parameters = parametersInitializers(packageName, settings)
        return CodeBlock.of("listOf(%L)", parameters)
    }

    private fun Token.Class.initializer(packageName: String): CodeBlock {
        val className = this.getClassName(packageName)
        val parameters = parametersInitializers(className.toString(), settings)
        return CodeBlock.of("%T(%L)", className, parameters)
    }

    private fun parametersInitializers(packageName: String, settings: List<Token>): CodeBlock {
        return settings.joinToCode { element ->
            element.initializer(packageName)
        }
    }

    private fun <T : Any> Token.Field<T>.initializer(): CodeBlock {
        return when (kClass) {
            String::class -> CodeBlock.of("%S", value)
            in Token.Field.CONSTANT_TYPES -> CodeBlock.of("%L", value)
            Date::class -> {
                val time = (value as Date).time
                CodeBlock.of("%T(%LL)", kClass, time)
            }
            else -> throw IllegalArgumentException("Unsupported type: $kClass")
        }
    }

    private fun <T> Collection<T>.joinToCode(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        suffix: CharSequence = "",
        transform: ((T) -> CodeBlock)
    ): CodeBlock {
        val blocks = map(transform).toTypedArray()
        val placeholders = Array(blocks.size) { "%L" }
        return CodeBlock.of(placeholders.joinToString(separator, prefix, suffix), *blocks)
    }

    private fun Token.getClassName(packageName: String): ClassName {
        if (this is Token.Field<*>) {
            return kClass.asClassName()
        }
        val simpleName = key.fromSnakeCaseToCamelCase(true)
        return ClassName(packageName, simpleName)
    }

    private fun String.fromSnakeCaseToCamelCase(usePascalCase: Boolean = false): String {
        val words = split("_")
        val firstWord = if (usePascalCase) {
            words[0].capitalize()
        } else {
            words[0]
        }
        return firstWord + words
            .drop(1)
            .joinToString("") { it.capitalize() }
    }

    private fun Token.KList.getClassName(packageName: String): ParameterizedTypeName {
        return LIST.parameterizedBy(settings.first().getClassName(packageName))
    }

    private val Token.fieldName: String
        get() = key
}
