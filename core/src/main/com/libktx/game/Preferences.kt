package com.libktx.game

import com.badlogic.gdx.Gdx
import com.libktx.game.Preferences.PreferenceKey.*
import kotlin.reflect.KProperty

/**
 * Mutable settings which are stored on filesystem
 */
object Preferences {

    private val preferences = Gdx.app.getPreferences(Config.appIdentifier)

    private enum class PreferenceKey { HueIP, HueRoomName, HueApiKey, TimerIp, BombIP, BombTimer }

    private fun get(preference: PreferenceKey): String? = preferences.getString(preference.name)
    private fun get(preference: PreferenceKey, default: String): String = preferences.getString(preference.name, default)

    private fun save(preference: PreferenceKey, value: String?) {
        if (value == null) {
            preferences.remove(preference.name)
        } else {
            preferences.putString(preference.name, value)
            preferences.flush()
        }
    }

    var hueIp: String by Preference(HueIP)

    var hueRoomName: String by Preference(HueRoomName)

    var hueApiKey: String? by OptionalPreference(HueApiKey)

    var timerIp: String? by OptionalPreference(TimerIp)

    var bombIp: String? by OptionalPreference(BombIP)

    var bombTimer: Int by PreferenceInt(BombTimer,42)

    private class OptionalPreference(private val prefKey: PreferenceKey) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            val value = get(prefKey)
            return if (value != null) {
                if (value.isEmpty()) null else value
            } else {
                null
            }
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            save(prefKey, value)
        }
    }

    private class Preference(private val prefKey: PreferenceKey, private val default: String = "") {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            val value = get(prefKey)
            return if (value != null) {
                if (value.isEmpty()) default else value
            } else {
                default
            }
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            save(prefKey, value)
        }
    }

    private class PreferenceInt(private val prefKey: PreferenceKey, val default: Int) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return preferences.getInteger(prefKey.name, default)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            preferences.putInteger(prefKey.name, value)
            preferences.flush()
        }
    }

    private class PreferenceBoolean(private val prefKey: PreferenceKey, val default: Boolean = false) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return preferences.getBoolean(prefKey.name, default)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            preferences.putBoolean(prefKey.name, value)
            preferences.flush()
        }
    }


}