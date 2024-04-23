package de.syntax.androidabschluss.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// JSON-Anmerkung zur Generierung des Adapters
@JsonClass(generateAdapter = true)
data class ChatResponse(
    // Liste von Antwortoptionen im Chat-Antwortobjekt
    @Json(name = "choices")
    val choices: List<Choice>
)