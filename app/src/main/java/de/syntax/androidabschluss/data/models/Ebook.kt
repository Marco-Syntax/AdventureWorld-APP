package de.syntax.androidabschluss.data.models
// Datenklasse, die ein E-Book repräsentiert
data class Ebook(
    val artistName: String = "",
    val artistId: Long = 0,
    val previewUrl: String = "",
    val wrapperType: String = "",
    val collectionName: String = "",
    val artworkUrl100: String = "",
    val primaryGenreName: String = "",
    val collectionPrice: String = "",
    val releaseDate: String = "",
    val description: String = ""
)

// Datenklasse, die die Antwort auf eine E-Book-Anfrage repräsentiert
data class EbookResponse(
    // Liste von E-Books als Ergebnis der Anfrage
    val results: List<Ebook>
)