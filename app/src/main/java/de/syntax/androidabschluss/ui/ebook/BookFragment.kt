package de.syntax.androidabschluss.ui.ebook

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentBookBinding
import de.syntax.androidabschluss.util.EbookAdapter
import de.syntax.androidabschluss.viewModels.ApiStatus
import de.syntax.androidabschluss.viewModels.SharedViewModel

class BookFragment : Fragment() {

    // ViewModel-Instanz für Itunes-bezogene Operationen
    private val viewModel: SharedViewModel by viewModels()

    // View-Binding-Instanz für dieses Fragment
    private lateinit var binding: FragmentBookBinding

    // Navigationscontroller-Instanz zum Navigieren zu anderen Zielen
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Das Layout für dieses Fragment aufblasen
        viewModel.loadAllEbook()
        binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        // Bild für den Chatbot laden
        val image = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // Klick-Listener für den Chatbot-Button
        binding.chatBot.setOnClickListener {
            findNavController().navigate(BookFragmentDirections.actionBookFragmentToChatBotFragment())
        }

        // Beobachtet den Ladezustand des ViewModel
        viewModel.loading.observe(viewLifecycleOwner) { status ->
            when (status) {
                ApiStatus.LOADING -> binding.progressBar.visibility = View.VISIBLE
                ApiStatus.DONE -> binding.progressBar.visibility = View.GONE
                ApiStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorImage.visibility = View.VISIBLE
                }
            }
        }

        // Setzt den KeyListener für die Suchfunktion
        binding.titelSuche.setOnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val searchText = (view as EditText).text.toString()
                if (searchText.isNotEmpty() && searchText.length >= 3) {
                    viewModel.searchEbook(searchText)
                } else {
                    viewModel.loadAllEbook()
                }
                // Eingabefeld leeren und Fokus entfernen, um die Tastatur auszublenden
                view.text.clear()
                view.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        // Beobachtet die Liste der E-Books und aktualisiert die RecyclerView
        viewModel.ebookList.observe(viewLifecycleOwner) { book ->
            if (book.isEmpty()) {
                // Wenn keine Bücher gefunden wurden, zeige eine Meldung an
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.apply {
                    setTitle("Kein passendes Buch gefunden")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
                viewModel.loadAllEbook()
            } else {
                // Wenn Bücher gefunden wurden, Tastatur ausblenden
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.titelSuche.windowToken, 0)
            }

            // RecyclerView mit dem EbookAdapter aktualisieren
            binding.rvBook.adapter = EbookAdapter(book)
            binding.rvBook.setHasFixedSize(true)
        }
    }

}
