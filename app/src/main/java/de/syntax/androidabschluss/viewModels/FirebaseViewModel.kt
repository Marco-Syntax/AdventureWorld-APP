package de.syntax.androidabschluss.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import de.syntax.androidabschluss.data.models.Quiz

const val FIREBASE_TAG = "FirebaseViewModel"

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    // Kommunikationspunkt mit der FirebaseAuth
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // currentuser ist null wenn niemand eingeloggt ist
    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _quizList = MutableLiveData<List<Quiz>>()
    val quizList: LiveData<List<Quiz>>
        get() = _quizList

    // Funktion zum Abrufen von Quizdaten aus der Firebase-Datenbank
    fun getQuizFromFirebase() {
        val quizList = mutableListOf<Quiz>()
        database.reference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    val quizModel = snapshot.getValue(Quiz::class.java)
                    if (quizModel != null) {
                        quizList.add(quizModel)
                    }
                }
                _quizList.value = quizList // LiveData mit den abgerufenen Quizdaten aktualisieren
            } else {
                _quizList.value = emptyList() // Wenn keine Daten vorhanden sind, leere Liste setzen
            }
        }
    }

    // Funktion zum Erstellen eines neuen Benutzerkontos
    fun signup(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { user ->
            if (user.isSuccessful) {
                login(email, password) // Automatischer Login nach erfolgreicher Registrierung
                showToast("Konto erfolgreich erstellt")
            } else {
                Log.e(FIREBASE_TAG, "Signup failed: ${user.exception}")
            }
        }
    }

    // Funktion zum Anmelden eines Benutzers
    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _currentUser.value = firebaseAuth.currentUser
            } else {
                Log.e(FIREBASE_TAG, "Login failed: ${it.exception}")
                showToast("Bitte gültige Email-Adresse oder Passwort eingeben")
            }
        }
    }

    // Hilfsfunktion zum Anzeigen von Toast-Nachrichten
    private fun showToast(message: String) {
        val context = getApplication<Application>().applicationContext
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Funktion zum Abmelden des aktuellen Benutzers
    fun logout() {
        firebaseAuth.signOut()
        _currentUser.value = firebaseAuth.currentUser
    }
}