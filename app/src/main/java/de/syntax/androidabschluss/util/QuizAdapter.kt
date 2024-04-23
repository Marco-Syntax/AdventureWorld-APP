package de.syntax.androidabschluss.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import de.syntax.androidabschluss.data.models.Quiz
import de.syntax.androidabschluss.databinding.QuizItemBinding
import de.syntax.androidabschluss.ui.quiz.QuizDetailFragment
import de.syntax.androidabschluss.ui.quiz.QuizFragmentDirections

class QuizAdapter(private var dataset: List<Quiz>) :
    RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    // ViewHolder für jedes Quiz-Element in der Liste
    inner class ViewHolder(val binding: QuizItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Erstellen eines ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Aufblasen des Layouts für jedes Listenelement
        val binding = QuizItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Anzahl der Elemente in der Liste
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Binden von Daten an die Ansichtselemente jedes Listenelements
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = dataset[position] // Aktuelles Quiz

        // Log-Ausgabe für das gebundene Quiz
        Log.d("Adapter", "Binding quiz: $quiz")

        // Binding-Logik für jedes Listenelement
        holder.binding.apply {
            // Setzen des Titels des Quiz
            quizTitelText.text = quiz.title
            // Setzen des Untertitels des Quiz
            quizSubtitelText.text = quiz.subtitle
            // Setzen der Zeit des Quiz
            quizTimeText.text = "${quiz.time}min"

            // Klick-Listener für das Listenelement
            cvQuizCard.setOnClickListener {
                // Navigation zur Detailansicht des ausgewählten Quiz
                val controller = cvQuizCard.findNavController()
                // Setzen der Fragenliste und der Zeit in der QuizDetailFragment-Companion-Objekte
                QuizDetailFragment.questionModelList = quiz.questionList
                QuizDetailFragment.time = quiz.time
                // Navigieren zur QuizDetailFragment
                controller.navigate(QuizFragmentDirections.actionQuizFragmentToQuizDetailFragment())
            }
        }
    }
}
