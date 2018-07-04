package org.stepik.android.exams.data.preference

import android.content.SharedPreferences
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface SharedPreferenceProvider {
    val sharedPreferences: SharedPreferences
}

class SharedPreferenceDelegate<in R: SharedPreferenceProvider, T: Any>(
        private val key: String,
        private val klass: KClass<T>
) : ReadWriteProperty<R, T>, ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>) = thisRef.sharedPreferences[klass, key]

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        thisRef.sharedPreferences[key] = value
    }
}

inline fun <R: SharedPreferenceProvider, reified T: Any>preference(key: String) = SharedPreferenceDelegate<R, T>(key, T::class)

inline fun SharedPreferences.edit(operation: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.operation()
    editor.apply()
}

operator fun SharedPreferences.set(key: String, value: Any?) = when(value) {
    is Boolean -> edit { putBoolean(key, value) }
    is Long    -> edit { putLong(key, value) }
    is Int     -> edit { putInt(key, value) }
    is String? -> edit { putString(key, value) }
    is Float   -> edit { putFloat(key, value) }
    else -> throw IllegalStateException("unsupported type ${value?.javaClass}")
}

@Suppress("UNCHECKED_CAST")
operator fun <T: Any>SharedPreferences.get(klass: KClass<T>, key: String, default: T? = null) = when(klass) {
    Boolean::class  -> getBoolean(key, default as? Boolean ?: false) as T
    Long::class     -> getLong(key, default as? Long ?: 0) as T
    Int::class      -> getInt(key, default as? Int ?: 0) as T
    String::class   -> getString(key, default as? String ?: "") as T
    Float::class    -> getFloat(key, default as? Float ?: 0f) as T
    else -> throw IllegalStateException("unsupported type $klass")
}

inline operator fun <reified T: Any>SharedPreferences.get(key: String, default: T? = null): T = get(T::class, key, default)