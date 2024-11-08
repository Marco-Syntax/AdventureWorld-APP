package de.syntax.androidabschluss.data

import android.util.Log
import de.syntax.androidabschluss.data.models.ChatRequest
import de.syntax.androidabschluss.data.models.ChatResponse
import de.syntax.androidabschluss.data.remote.OpAiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Repository-Klasse für die Chat-Funktionalität
class ChatRepository {

    // API-Client für die Kommunikation mit der OpenAI API
    private val apiClient = OpAiClient.getInstance()

    // Methode zum Erstellen einer Chat-Vervollständigungsanfrage
    fun createChatCompletion(
        chatRequest: ChatRequest,
        onSuccess: (ChatResponse?) -> Unit, // Callback für erfolgreiche Antwort
        onError: (String) -> Unit // Callback für Fehlerbehandlung
    ) {
        // Debug: Logge die Anfrage
        Log.d("ChatRepository", "ChatRequest: $chatRequest")

        // Aufruf der OpenAI API, um eine Chat-Vervollständigungsanfrage zu erstellen
        apiClient.createChatCompletion(chatRequest)
            .enqueue(object : Callback<ChatResponse> {
                // Callback für erfolgreiche Antwort
                override fun onResponse(
                    call: Call<ChatResponse>,
                    response: Response<ChatResponse>
                ) {
                    // Überprüfen, ob die Antwort erfolgreich war
                    if (response.isSuccessful) {
                        // Debug: Logge die erhaltene Antwort
                        Log.d("ChatRepository", "Erfolgreiche Antwort erhalten: ${response.body()}")
                        onSuccess(response.body())
                    } else {
                        // Debug: Logge den Fehlercode und den Fehlerkörper
                        Log.e("ChatRepository", "Fehlercode: ${response.code()}, Fehlerkörper: ${response.errorBody()?.string()}")
                        onError("API-Aufruf: Fehlercode ${response.code()}")
                    }
                }

                // Callback für Fehler beim API-Aufruf
                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    // Debug: Logge den Fehler beim API-Aufruf
                    Log.e("ChatRepository", "Fehler beim API-Aufruf: ${t.message}", t)
                    onError("API-Aufruf fehlgeschlagen: ${t.message}")
                }
            })
    }
}