package de.syntax.androidabschluss.ui.audiobook

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentAudioBookBinding
import de.syntax.androidabschluss.util.AudioBookAdapter
import de.syntax.androidabschluss.viewModels.ApiStatus
import de.syntax.androidabschluss.viewModels.SharedViewModel

class AudioBookFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentAudioBookBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Datenbindung initialisieren
        binding = FragmentAudioBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        // Zurück Taste ist in MainActivity deaktiviert um nicht in Login oder StartScreens zu gelangen
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        // Laden aller Audiobücher beim Start der Ansicht
        viewModel.loadAllAudioEbook()

        // Bild für den Chatbot laden
        val image = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // Navigationsaktion zum Chatbot
        binding.chatBot.setOnClickListener {
            findNavController().navigate(AudioBookFragmentDirections.actionAudioBookFragmentToChatBotFragment())
        }

        // Spracheingabe starten, wenn die Mikrofon-Schaltfläche geklickt wird
        binding.btnMic.setOnClickListener {
//            startSpeechToText()
        }

        // Beobachten des Ladezustands und Anzeigen des Fortschrittsbalkens bzw. Fehlerbilds
        viewModel.loading.observe(viewLifecycleOwner) { status ->
            binding.progressBar.visibility = when (status) {
                ApiStatus.LOADING -> View.VISIBLE
                else -> View.GONE
            }
            binding.errorImage.visibility =
                if (status == ApiStatus.ERROR) View.VISIBLE else View.GONE
        }

        // Suchen nach Audiobüchern beim Drücken der Eingabetaste
        binding.titelSucheAudioBook.setOnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val searchText = (view as EditText).text.toString()
                if (searchText.isNotEmpty() && searchText.length >= 3) {
                    viewModel.searchAudioBook(searchText)
                } else {
                    viewModel.loadAllAudioEbook()
                }
                // Eingabefeld leeren und Tastatur ausblenden
                view.text.clear()
                view.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        // Beobachten der Liste der Audiobücher und Aktualisieren des RecyclerView-Adapters
        viewModel.audioBookList.observe(viewLifecycleOwner) { audioBook ->
            if (audioBook.isEmpty()) {
                // Dialog anzeigen, wenn keine passenden Audiobücher gefunden wurden
                noFoundDialog()
                viewModel.loadAllAudioEbook()
            } else {
                // Tastatur ausblenden und Suchfeld leeren, wenn Audiobücher gefunden wurden
                hideKeyboard()
                binding.titelSucheAudioBook.setText("")
            }
            // Adapter für den RecyclerView aktualisieren
            binding.rvAudioBook.adapter = AudioBookAdapter(audioBook)
            binding.rvAudioBook.setHasFixedSize(true)
        }
    }

    // Funktion zum Starten der Spracheingabe
//    private fun startSpeechToText() {
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Spracheingabe starten...")
//        speechToTextLauncher.launch(intent)
//    }

    // ActivityResultLauncher für die Spracheingabe
//    private val speechToTextLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//                val spokenText = result?.get(0)
//                binding.titelSucheAudioBook.setText(spokenText)
//                if (!spokenText.isNullOrEmpty() && spokenText.length >= 3) {
//                    viewModel.searchAudioBook(spokenText)
//                } else {
//                    viewModel.loadAllAudioEbook()
//                }
//            }
//        }

    // Dialog anzeigen, wenn keine Audiobücher gefunden wurden
    private fun noFoundDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("Keine passenden Hörbücher gefunden")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Suchfeld leeren
                binding.titelSucheAudioBook.setText("")
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Tastatur ausblenden
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.titelSucheAudioBook.windowToken, 0)
    }
}