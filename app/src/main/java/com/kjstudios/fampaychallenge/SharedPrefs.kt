package com.kjstudios.fampaychallenge

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kjstudios.fampaychallenge.models.Card


class SharedPrefs(context: Context) {

    companion object {
        const val PREFS_NAME = "FAMPAY"
        const val PREFS_KEY_REMIND_LATER = "REMIND_LATER"
        const val PREFS_KEY_DISMISS_NOW = "DISMISS_NOW"
        val gson = Gson()
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun clearRemindLater(){
        sharedPreferences.edit().remove(PREFS_KEY_REMIND_LATER).apply()
    }

    fun addRemindLaterCard(card: Card) {
        val remindLaterCardsList = getRemindLaterCards()
        remindLaterCardsList.add(card)
        sharedPreferences.edit(false) {
            putString(PREFS_KEY_REMIND_LATER, gson.toJson(remindLaterCardsList))
        }
    }

    fun getRemindLaterCards(): MutableList<Card> {
        val json = sharedPreferences.getString(PREFS_KEY_REMIND_LATER, null)
        val type = object : TypeToken<List<Card>?>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun addDismissNowCard(card: Card) {
        val dismissNowCardsList = getDismissNowCards()
        dismissNowCardsList.add(card)
        sharedPreferences.edit(false) {
            putString(PREFS_KEY_DISMISS_NOW, gson.toJson(dismissNowCardsList))
        }
    }

    fun getDismissNowCards(): MutableList<Card> {
        val json = sharedPreferences.getString(PREFS_KEY_DISMISS_NOW, null)
        val type = object : TypeToken<List<Card>?>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

}