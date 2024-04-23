package de.syntax.androidabschluss

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import de.syntax.androidabschluss.databinding.ActivityLogin2Binding
class LoginActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityLogin2Binding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Überprüfen, ob ein Benutzer bereits angemeldet ist
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Benutzer ist bereits angemeldet, MainActivity starten
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Aktuelle Aktivität beenden, um nicht zurückgehen zu können
        }
    }
}