package de.syntax.androidabschluss.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// JSON-Anmerkung zur Generierung des Adapters
@JsonClass(generateAdapter = true)
data class Message(
    // Der Inhalt der Nachricht
    @Json(name = "content")
    val content: String,

    // Die Rolle der Nachricht (z. B. "user" oder "bot")
    @Json(name = "role")
    val role: String
)