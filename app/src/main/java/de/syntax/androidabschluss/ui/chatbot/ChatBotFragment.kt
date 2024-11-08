package de.syntax.androidabschluss.ui.chatbot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.models.Message
import de.syntax.androidabschluss.databinding.FragmentChatBotBinding
import de.syntax.androidabschluss.viewModels.ChatViewModel

class ChatBotFragment : Fragment() {

    private lateinit var binding: FragmentChatBotBinding

    // ViewModel für dieses Fragment, durch Lazy-Initialization initialisiert
    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(this)[ChatViewModel::class.java]
    }

    // Erstellt die View des Fragments
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBotBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Initialisierung der UI-Komponenten und ViewModel-Beobachtung
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referenz zum GIF-Bild und zum Info-Bild
        val gifImage = binding.lexiGif
        val infoImage = binding.lexiInfo

        // Lade das GIF-Bild mit Glide
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(gifImage)

        // Setze das Info-Bild
        infoImage.setImageResource(R.drawable.lexitextchat)

        // OnClickListener für die Senden-Schaltfläche
        binding.sendButton.setOnClickListener {

            // Tastatur verstecken
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            // Nachrichtentext aus dem Eingabefeld holen und trimmen
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Debug: Logge die gesendete Nachricht
                Log.d("ChatBotFragment", "Nachricht gesendet: $messageText")

                // Neue Nachricht erstellen
                val message = Message(content = messageText, role = "user")
                // Chat-Nachricht an das ViewModel senden
                viewModel.createChatCompletion(listOf(message), "gpt-3.5-turbo")
                // Eingabefeld leeren
                binding.messageInput.text = null
                // GIF und Info-Bild ausblenden und aus der Ansicht entfernen
                gifImage.visibility = View.GONE
                (gifImage.parent as? ViewGroup)?.removeView(gifImage)
                infoImage.visibility = View.GONE
                (infoImage.parent as? ViewGroup)?.removeView(infoImage)
            } else {
                // Debug: Logge den Fall einer leeren Nachricht
                Log.w("ChatBotFragment", "Leere Nachricht eingegeben")
                // Benachrichtigung anzeigen, wenn keine Nachricht eingegeben wurde
                showToast("Bitte eine Frage eingeben")
            }
        }

        // Beobachtet den Ladezustand des ViewModels und aktualisiert die Ansicht entsprechend
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.responseText.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Beobachtet die Antwort des ViewModels und aktualisiert die Ansicht entsprechend
        viewModel.chatResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                // Debug: Logge die erhaltene Antwort
                Log.d("ChatBotFragment", "Erhaltene Antwort: $it")
                // Antworttext anzeigen
                binding.responseText.text = it
            } ?: Log.w("ChatBotFragment", "Keine Antwort erhalten")
        }

        // Beobachtet Fehlermeldungen des ViewModels und zeigt sie an, wenn sie auftreten
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Debug: Logge die Fehlermeldung
                Log.e("ChatBotFragment", "Fehlermeldung erhalten: $it")
                showToast("Fehler aufgetreten")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}