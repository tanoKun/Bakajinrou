package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class DIContext {
    private val instances = mutableMapOf<KClass<*>, Any>()

    fun <T : Any> register(type: KClass<T>, instance: T) {
        instances[type] = instance
    }

    inline fun <reified T : Any> register(instance: T) {
        register(T::class, instance)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(type: KClass<T>): T = instances[type] as? T ?: throw IllegalStateException("対応していない型です。(${type.qualifiedName})")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNull(type: KClass<T>): T? = instances[type] as? T

    inline fun <reified T : Any> get(): T = get(T::class)

    inline fun <reified T : Any> getOrNull(): T? = getOrNull(T::class)

    fun <T : Any> construct(type: KClass<T>): T {
        val constructor = type.primaryConstructor
            ?: error("No primary constructor found for ${type.qualifiedName}")

        val args = constructor.parameters.map { param ->
            val klass = param.type.classifier as KClass<*>

            return@map get(klass)
        }

        return constructor.call(*args.toTypedArray())
    }

    fun getParameters(function: KFunction<*>): Array<Any> {
        val args = function.parameters.map { param ->
            val klass = param.type.classifier as KClass<*>

            return@map get(klass)
        }

        return args.toTypedArray()
    }
}