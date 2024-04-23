package de.syntax.androidabschluss.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.syntax.androidabschluss.data.SharedRepository
import de.syntax.androidabschluss.data.local.getDatabase
import de.syntax.androidabschluss.data.models.AudioBook
import de.syntax.androidabschluss.data.models.Ebook
import de.syntax.androidabschluss.data.models.Favorite
import de.syntax.androidabschluss.data.remote.ItunesApi
import kotlinx.coroutines.launch

// Tag für die Protokollierung
const val ITUNES_TAG = "ItunesViewModel"

// Enum zur Darstellung des Status der API-Aufrufe
enum class ApiStatus { LOADING, ERROR, DONE }

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    // Erstellen einer Instanz der Datenbank und des Repositorys
    private val database = getDatabase(application)
    private val repository = SharedRepository(ItunesApi, database)

    // LiveData für die Favoritenliste
    private val _favorite = repository.favorite
    val favorite: LiveData<List<Favorite>>
        get() = _favorite

    // LiveData für die Liste der E-Books
    private val _ebookList = repository.ebookList
    val ebookList: LiveData<List<Ebook>>
        get() = _ebookList

    // LiveData für die Liste der Hörbücher
    private val _audioBookList = repository.audioBookList
    val audioBookList: LiveData<List<AudioBook>>
        get() = _audioBookList

    // LiveData für den Ladezustand der API-Aufrufe
    private val _loading = MutableLiveData<ApiStatus>()
    val loading: LiveData<ApiStatus>
        get() = _loading

    // Funktionen für die Dateninteraktion

    // Funktion zum Abrufen aller Favoriten
    fun getAllFavorite() {
        viewModelScope.launch {
            try {
                repository.getAllFavorite()
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error getAllFavorite:$e")
            }
        }
    }

    // Funktion zum Einfügen eines Favoriten
    fun insertFavorite(fav: Favorite) {
        viewModelScope.launch {
            try {
                repository.insertFavorite(fav)
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error insertFavorite:$e")
            }
        }
    }

    // Funktion zum Löschen eines Favoriten anhand seiner ID
    fun deleteById(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(id)
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error deleteById:$e")
            }
        }
    }

    // Funktionen zum Suchen von E-Books und Hörbüchern

    fun searchEbook(searchInput: String) {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            try {
                repository.getBooks(searchInput)
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error loading data (searchEbook): $e")
                _loading.value = ApiStatus.ERROR
            }
        }
    }

    fun searchAudioBook(searchInput: String) {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            try {
                repository.getAudioBook(searchInput)
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error loading data (searchAudioBook): $e")
                _loading.value = ApiStatus.ERROR
            }
        }
    }

    // Funktionen zum Laden aller E-Books und Hörbücher

    fun loadAllEbook() {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            try {
                repository.getAllBooks()
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error loading data (loadAllEbook): $e")
                _loading.value = ApiStatus.ERROR
            }
        }
    }

    fun loadAllAudioEbook() {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            try {
                repository.getAllAudioBooksForChildren()
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(ITUNES_TAG, "Error loading data (loadAllAudioEbook): $e")
                _loading.value = ApiStatus.ERROR
            }
        }
    }
}