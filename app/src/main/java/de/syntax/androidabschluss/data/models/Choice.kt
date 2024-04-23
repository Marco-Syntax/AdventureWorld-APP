package de.syntax.androidabschluss.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// JSON-Anmerkung zur Generierung des Adapters
@JsonClass(generateAdapter = true)
data class Choice(
    // Index der Auswahl
    @Json(name = "index")
    val index: Int,

    // Nachricht, die der Auswahl zugeordnet ist
    @Json(name = "message")
    val message: Message
)