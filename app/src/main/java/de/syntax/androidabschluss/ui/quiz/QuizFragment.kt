package de.syntax.androidabschluss.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentQuizBinding
import de.syntax.androidabschluss.util.QuizAdapter
import de.syntax.androidabschluss.viewModels.FirebaseViewModel

class QuizFragment : Fragment() {
    lateinit var binding: FragmentQuizBinding
    private lateinit var adapter: QuizAdapter
    private val viewModel: FirebaseViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quizdaten von Firebase abrufen
        viewModel.getQuizFromFirebase()

        // Ladebildschirm anzeigen, während die Daten geladen werden
        binding.progressBar.visibility = View.VISIBLE

        // Chatbot-Bild einrichten
        val image = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // OnClickListener für Chatbot-Bild festlegen
        binding.chatBot.setOnClickListener {
            findNavController().navigate(QuizFragmentDirections.actionQuizFragmentToChatBotFragment())
        }

        // Beobachte die Quizliste im ViewModel und aktualisiere die RecyclerView, wenn sich die Daten ändern
        viewModel.quizList.observe(viewLifecycleOwner) { quizList ->
            adapter = QuizAdapter(quizList)
            binding.rvQuiz.adapter = adapter
            // Ladebildschirm ausblenden, nachdem die Daten geladen wurden
            binding.progressBar.visibility = View.GONE
        }
    }
}