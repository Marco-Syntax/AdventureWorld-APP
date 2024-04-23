package de.syntax.androidabschluss.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// JSON-Anmerkung zur Generierung des Adapters
@JsonClass(generateAdapter = true)
data class ChatRequest(
    // Liste von Nachrichten im Chat-Anforderungsobjekt
    @Json(name = "messages")
    val messages: List<Message>,

    // Modell, das für den Chat verwendet wird
    @Json(name = "model")
    val model: String
)