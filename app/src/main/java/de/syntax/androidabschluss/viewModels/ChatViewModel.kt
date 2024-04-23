package de.syntax.androidabschluss.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.syntax.androidabschluss.data.ChatRepository
import de.syntax.androidabschluss.data.models.ChatRequest
import de.syntax.androidabschluss.data.models.Message

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    // Erstellen einer Instanz des ChatRepository
    private val chatRepository = ChatRepository()

    // LiveData für die Chat-Antwort
    private val _chatResponse = MutableLiveData<String?>()
    val chatResponse: LiveData<String?>
        get() = _chatResponse

    // LiveData für Fehlermeldungen
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    // LiveData, um den Ladezustand anzuzeigen
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // Methode zur Erstellung einer Chat-Antwort
    fun createChatCompletion(messages: List<Message>, model: String) {
        // Setzen des Ladezustands auf true, um anzuzeigen, dass die Anfrage läuft
        _isLoading.value = true

        // Erstellen eines ChatRequest-Objekts mit den übergebenen Nachrichten und dem Modell
        val request = ChatRequest(messages, model)

        // Aufruf der Methode im Chat-Repository, um die Chat-Antwort zu erhalten
        chatRepository.createChatCompletion(request, { response ->
            // Setzen des Ladezustands auf false nach dem Erhalt der Antwort
            _isLoading.value = false

            // Setzen der erhaltenen Chat-Antwort in das LiveData-Objekt
            _chatResponse.value = response?.choices?.firstOrNull()?.message?.content
        }, { error ->
            // Setzen des Ladezustands auf false im Fehlerfall
            _isLoading.value = false

            // Setzen der Fehlermeldung in das LiveData-Objekt
            _errorMessage.value = error
        })
    }
}