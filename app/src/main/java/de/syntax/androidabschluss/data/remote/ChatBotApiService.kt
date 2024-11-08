package de.syntax.androidabschluss.data.remote

import de.syntax.androidabschluss.BuildConfig
import de.syntax.androidabschluss.data.models.ChatRequest
import de.syntax.androidabschluss.data.models.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


const val apiKey: String = BuildConfig.API_KEY
//const val apiKey: String = ""
// Retrofit Service Interface für die OpenAI API
interface OpenAiApiService {
    // Methode zur Erstellung einer Chat-Vervollständigungsanfrage
    @POST("chat/completions")
    fun createChatCompletion(
        // Anfrageobjekt mit Chatnachrichten und Modell
        @Body chatRequest: ChatRequest,
        // Header für den Inhaltstyp (Content-Type)
        @Header("Content-Type") contentType: String = "application/json",
        // Header für die Autorisierung mit dem API-Schlüssel
        @Header("Authorization") authorization: String = "Bearer $apiKey"
    ): Call<ChatResponse> // Rückgabetyp ist eine Chat-Antwort
}