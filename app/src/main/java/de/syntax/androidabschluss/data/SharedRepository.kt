package de.syntax.androidabschluss.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.syntax.androidabschluss.data.local.FavoriteDatabase
import de.syntax.androidabschluss.data.models.AudioBook
import de.syntax.androidabschluss.data.models.Ebook
import de.syntax.androidabschluss.data.models.Favorite
import de.syntax.androidabschluss.data.remote.ItunesApi

// Konstante für das Logging-Tag
const val TAG = "ItunesRepository"

// Repository-Klasse für die iTunes-Daten
class SharedRepository(private val api: ItunesApi, private val database: FavoriteDatabase) {

    // Mutable LiveData für die Liste der E-Books
    private val _ebookList = MutableLiveData<List<Ebook>>()
    val ebookList: LiveData<List<Ebook>>
        get() = _ebookList

    // Mutable LiveData für die Liste der Hörbücher
    private val _audioBookList = MutableLiveData<List<AudioBook>>()
    val audioBookList: LiveData<List<AudioBook>>
        get() = _audioBookList

    // LiveData für die Liste der Favoriten
    private val _favorite = database.favoriteDao.getAll()
    val favorite: LiveData<List<Favorite>>
        get() = _favorite

    // Funktion zum Abrufen aller Favoriten
    fun getAllFavorite() {
        try {
            database.favoriteDao.getAll()
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Abrufen aller Favoriten: $e")
        }
    }

    // Funktion zum Einfügen eines Favoriten
    suspend fun insertFavorite(fav: Favorite) {
        try {
            database.favoriteDao.insert(fav)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Einfügen von Favorite: $e")
        }
    }

    // Funktion zum Löschen anhand ihrer ID
    suspend fun deleteById(id: Int) {
        try {
            database.favoriteDao.deleteById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Löschen von deleteById: $e")
        }
    }

    // Funktion zum Abrufen aller E-Books
    suspend fun getBooks(textInput: String) {
        try {
            val response = api.retrofitService.getAllBooks(term = "Kinder")
            val allBooks = response.body()?.results ?: emptyList()

            // Suche nach dem Suchbegriff in der Liste der E-Books
            val searchedBooks = searchEbooksInList(textInput, allBooks)

            _ebookList.value = searchedBooks
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden der Daten (getBooks): $e")
        }
    }

    // Funktion zum Abrufen aller E-Books
    suspend fun getAllBooks() {
        try {
            val response = api.retrofitService.getAllBooks(term = "Kinder")
            _ebookList.value = response.body()?.results
            Log.e(TAG, "${_ebookList.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden der Daten (getAllBooks): $e")
        }
    }

    // Funktion zum Suchen von E-Books in der Liste basierend auf einem Suchbegriff
    private fun searchEbooksInList(searchQuery: String, ebooksList: List<Ebook>): List<Ebook> {
        // Filtere die Liste der E-Books basierend auf dem Suchbegriff
        return ebooksList.filter { ebook ->
            ebook.primaryGenreName.contains(searchQuery, ignoreCase = true) ||
                    ebook.artistName.contains(searchQuery, ignoreCase = true) ||
                    ebook.collectionName.contains(searchQuery, ignoreCase = true) ||
                    ebook.primaryGenreName.contains(searchQuery, ignoreCase = true)
        }
    }

    // Funktion zum Abrufen aller Hörbücher für Kinder
    suspend fun getAllAudioBooksForChildren() {
        try {
            val response = api.retrofitService.getAllAudioBooks(
                term = "Kinder",
                country = "DE",
                media = "audiobook",
                entity = "audiobook"
            )
            _audioBookList.value = response.body()?.results
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden der Daten (getAllAudioBooksForChildren): $e")
        }
    }

    // Funktion zum Abrufen eines Hörbuchs basierend auf einem Suchbegriff
    suspend fun getAudioBook(textInput: String) {
        try {
            val response = api.retrofitService.getAllAudioBooks(
                term = "Kinder",
                country = "DE",
                media = "audiobook",
                entity = "audiobook"
            )
            Log.d(TAG, "Raw JSON response: ${response.body()}")
            val allAudioBooks = response.body()?.results ?: emptyList()

            // Suche nach dem Suchbegriff in der Liste der Hörbücher
            val searchedAudioBooks = searchAudioBooksInList(textInput, allAudioBooks)

            _audioBookList.value = searchedAudioBooks
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden der Daten (getAudioBook): $e")
        }
    }

    // Funktion zum Suchen von Hörbüchern in der Liste basierend auf einem Suchbegriff
    private fun searchAudioBooksInList(searchQuery: String, audioBooksList: List<AudioBook>): List<AudioBook> {
        // Filtere die Liste der Hörbücher basierend auf dem Suchbegriff
        return audioBooksList.filter { audioBook ->
            audioBook.trackName.contains(searchQuery, ignoreCase = true) ||
                    audioBook.artistName.contains(searchQuery, ignoreCase = true) ||
                    audioBook.collectionName.contains(searchQuery, ignoreCase = true) ||
                    audioBook.primaryGenreName.contains(searchQuery, ignoreCase = true)
        }
    }
}