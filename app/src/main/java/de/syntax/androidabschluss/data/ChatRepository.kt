package de.syntax.androidabschluss.data

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
                        // Aufruf des onSuccess-Callbacks mit der empfangenen Chat-Antwort
                        onSuccess(response.body())
                    } else {
                        // Aufruf des onError-Callbacks mit einer Fehlermeldung
                        onError(
                            "API-Aufruf: einen Fehler zurückgegeben ${response.errorBody()?.string()}"
                        )
                    }
                }

                // Callback für Fehler beim API-Aufruf
                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    // Aufruf des onError-Callbacks mit einer Fehlermeldung
                    onError("API-Aufruf fehlgeschlagen: ${t.message}")
                }
            })
    }
}