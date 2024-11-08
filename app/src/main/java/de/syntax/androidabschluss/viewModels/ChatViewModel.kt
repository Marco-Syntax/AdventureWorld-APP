package de.syntax.androidabschluss.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.syntax.androidabschluss.data.ChatRepository
import de.syntax.androidabschluss.data.models.ChatRequest
import de.syntax.androidabschluss.data.models.Message

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatRepository = ChatRepository()

    private val _chatResponse = MutableLiveData<String?>()
    val chatResponse: LiveData<String?>
        get() = _chatResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // Erweiterte Liste von Beleidigungen und unangemessenen Ausdrücken
    private val prohibitedWords = listOf(
        "Beleidigung1", "Beleidigung2", "Hassausdruck1", "unangemessenes Wort",
        "Gewalt", "Mord", "Vergewaltigung", "Drogen", "Sex", "Nackt", "Bikini"
        // Weitere Wörter hinzufügen
    )

    // Methode zur Überprüfung und Filterung der Antwort mit Mustererkennung
    private fun filterResponse(response: String?): String {
        response?.let {
            for (word in prohibitedWords) {
                val regex = Regex("\\b$word\\b", RegexOption.IGNORE_CASE)
                if (regex.containsMatchIn(it)) {
                    Log.w("ChatViewModel", "Gefundene unangemessene Antwort: $it")
                    return "Diese Antwort wurde gefiltert, um unangemessene Inhalte zu vermeiden."
                }
            }
        }
        return response ?: "Keine Antwort erhalten."
    }

    fun createChatCompletion(messages: List<Message>, model: String) {
        _isLoading.value = true
        val request = ChatRequest(messages, model)

        chatRepository.createChatCompletion(request, { response ->
            _isLoading.value = false
            val filteredResponse = filterResponse(response?.choices?.firstOrNull()?.message?.content)
            _chatResponse.value = filteredResponse
            Log.d("ChatViewModel", "Gefilterte Antwort: $filteredResponse")
        }, { error ->
            _isLoading.value = false
            _errorMessage.value = error
            Log.e("ChatViewModel", "Fehler bei der Chat-Anfrage: $error")
        })
    }
}