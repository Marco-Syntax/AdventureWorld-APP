package de.syntax.androidabschluss.data.models

// Datenklasse, die ein Audiobook repräsentiert
data class AudioBook(
    val wrapperType: String = "",
    val artistId: Long = 0,
    val collectionId: Long = 0,
    val amgArtistId: Int = 0,
    val artistName: String = "",
    val collectionName: String = "",
    val trackName: String = "",
    val collectionCensoredName: String = "",
    val artistViewUrl: String = "",
    val collectionViewUrl: String = "",
    val artworkUrl60: String = "",
    val artworkUrl100: String = "",
    val collectionPrice: Double = 0.0,
    val collectionExplicitness: String = "",
    val trackCount: Int = 0,
    val copyright: String = "",
    val country: String = "",
    val currency: String = "",
    val releaseDate: String = "",
    val primaryGenreName: String = "",
    val previewUrl: String = "",
    val description: String = ""
)
// Datenklasse, die die Antwort auf eine Audiobook-Anfrage repräsentiert

data class AudioBookResponse(
    // Liste von Audiobook als Ergebnis der Anfrage
    val results: List<AudioBook>
)
