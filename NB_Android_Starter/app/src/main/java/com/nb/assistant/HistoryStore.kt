package com.nb.assistant

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Minimal local action-history example. Avoid storing raw audio, secrets, or private message contents. */
class HistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("nb_history", Context.MODE_PRIVATE)
    private val key = "actions"

    @Synchronized
    fun add(action: String, status: String, detail: String = "") {
        val items = JSONArray(prefs.getString(key, "[]"))
        items.put(JSONObject().apply {
            put("action", action.take(200))
            put("status", status.take(40))
            put("detail", detail.take(300))
            put("timestamp", System.currentTimeMillis())
        })
        // Keep the most recent 500 entries.
        val trimmed = JSONArray()
        val start = (items.length() - 500).coerceAtLeast(0)
        for (i in start until items.length()) trimmed.put(items.getJSONObject(i))
        prefs.edit().putString(key, trimmed.toString()).apply()
    }

    fun readAll(): JSONArray = JSONArray(prefs.getString(key, "[]"))
    fun clear() { prefs.edit().remove(key).apply() }
}
