package de.syntax.androidabschluss.data.models

// Datenklasse, die ein Quiz repräsentiert
data class Quiz(
    // ID des Quiz
    val id: String = "",

    // Titel des Quiz
    val title: String = "",

    // Untertitel des Quiz
    val subtitle: String = "",

    // Zeitdauer des Quiz
    val time: String = "",

    // Liste von Fragenmodelle im Quiz
    val questionList: List<QuestionModel> = emptyList()
)

// Datenklasse, die ein Fragemodell repräsentiert
data class QuestionModel(
    // Die Frage
    val question: String = "",

    // Liste von Antwortoptionen für die Frage
    val options: List<String> = emptyList(),

    // Die korrekte Antwort
    val correct: String = ""
)