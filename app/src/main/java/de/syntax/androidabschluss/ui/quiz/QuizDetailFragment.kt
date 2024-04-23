package de.syntax.androidabschluss.ui.quiz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.CircularProgressIndicator
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.models.QuestionModel
import de.syntax.androidabschluss.databinding.FragmentQuizDetailBinding

class QuizDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentQuizDetailBinding
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
    private var answerSelected = false

    private lateinit var timer: CountDownTimer

    /**
     * Wird aufgerufen, um die Ansichtshierarchie des Fragments zu erstellen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizDetailBinding.inflate(inflater, container, false)
        // Fragen laden und Timer starten
        loadQuestions()
        startTimer()
        // OnClick-Listener für die Buttons setzen
        setOnClickListeners()
        return binding.root
    }

    /**
     * Startet den Countdown-Timer für das Quiz.
     */
    private fun startTimer() {
        // Timer für die Quiz-Dauer setzen
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        timer = object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                // Timer aktualisieren
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text =
                    String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                // Quiz beenden, wenn Timer abgelaufen ist
                finishQuiz()
            }
        }.start()
    }

    /**
     * Lädt die Fragen und aktualisiert die Ansicht mit der aktuellen Frage.
     */
    @SuppressLint("SetTextI18n")
    private fun loadQuestions() {
        // Überprüfen, ob alle Fragen geladen wurden
        if (currentQuestionIndex == questionModelList.size) {
            finishQuiz()
            return
        }

        // Aktuelle Frage anzeigen
        val currentQuestion = questionModelList[currentQuestionIndex]
        binding.frageIndicatorTextview.text =
            "Frage ${currentQuestionIndex + 1}/${questionModelList.size}"
        binding.fragenProgressIndicator.progress =
            (currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100).toInt()
        binding.tvFrage.text = currentQuestion.question
        binding.btnA.text = currentQuestion.options.getOrNull(0) ?: ""
        binding.btnB.text = currentQuestion.options.getOrNull(1) ?: ""
        binding.btnC.text = currentQuestion.options.getOrNull(2) ?: ""
        binding.btnD.text = currentQuestion.options.getOrNull(3) ?: ""
    }

    /**
     * Behandelt Klick-Ereignisse für die Buttons.
     */
    @SuppressLint("ResourceAsColor")
    override fun onClick(view: View?) {
        // Hintergrundfarbe der Buttons zurücksetzen
        binding.btnA.setBackgroundColor(android.R.color.holo_green_dark)
        binding.btnB.setBackgroundColor(android.R.color.holo_green_dark)
        binding.btnC.setBackgroundColor(android.R.color.holo_green_dark)
        binding.btnD.setBackgroundColor(android.R.color.holo_green_dark)

        // Behandlung des Button-Klicks mit when-Abfrage
        when (view?.id) {
            R.id.btn_next -> {
                if (answerSelected) { // Überprüfen, ob eine Antwort ausgewählt wurde
                    // Antwort überprüfen und zur nächsten Frage gehen
                    checkAnswer()
                    currentQuestionIndex++
                    loadQuestions()
                    answerSelected = false // Zurücksetzen für die nächste Frage
                } else {
                    // Wenn keine Antwort ausgewählt wurde, Benachrichtigung anzeigen
                    Toast.makeText(
                        requireContext(),
                        "Bitte wähle eine Antwort aus.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> {
                // Ausgewählte Antwort merken und Hintergrundfarbe des Buttons ändern
                selectedAnswer = (view as? Button)?.text?.toString() ?: ""
                view?.setBackgroundColor(Color.BLUE)
                answerSelected = true // Markieren, dass eine Antwort ausgewählt wurde
            }
        }
    }

    /**
     * Überprüft die ausgewählte Antwort und aktualisiert die Punktzahl.
     */
    private fun checkAnswer() {
        // Antwort überprüfen und Punktzahl aktualisieren
        if (selectedAnswer == questionModelList.getOrNull(currentQuestionIndex)?.correct) {
            score++
        }
    }

    /**
     * Diese Methode wird aufgerufen, um das Quiz zu beenden und ein Dialogfeld mit dem Ergebnis anzuzeigen.
     * Der Timer wird gestoppt und die Punktzahl sowie die Prozentsatz des Ergebnisses werden berechnet.
     * Ein AlertDialog wird mit dem Ergebnis angezeigt und der Benutzer kann über einen Button zum vorherigen Fragment zurückkehren.
     */

    @SuppressLint("SetTextI18n")
    private fun finishQuiz() {
        // Timer stoppen
        timer.cancel()

        // Gesamtanzahl der Fragen und erreichte Punktzahl berechnen
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        // Dialog für das Quiz-Ergebnis anzeigen
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.score_dialog, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()

        val scoreTitle = dialogView.findViewById<TextView>(R.id.score_title)
        val imgPokal = dialogView.findViewById<ImageView>(R.id.img_pokal)
        val scoreProgressIndicator =
            dialogView.findViewById<CircularProgressIndicator>(R.id.score_porgress_indicator)
        val scoreProgressText = dialogView.findViewById<TextView>(R.id.score_progress_text)
        val scoreSubtitle = dialogView.findViewById<TextView>(R.id.score_subtitle)
        val btnFinish = dialogView.findViewById<Button>(R.id.btn_finish)

        // Ergebnis anzeigen und Ressourcen entsprechend des Ergebnisses anpassen
        scoreProgressIndicator.progress = percentage
        scoreProgressText.text = "$percentage %"
        scoreTitle.text =
            if (percentage > 50) "Herzlichen Glückwunsch" else "Schade, beim nächsten Mal"
        scoreTitle.setTextColor(if (percentage > 50) Color.GREEN else Color.RED)
        imgPokal.setImageResource(if (percentage > 50) R.drawable.winsmily else R.drawable.lostsmily)
        scoreSubtitle.text = "$score von $totalQuestions hast du richtig beantwortet"

        // OnClickListener für den Button zum Beenden des Dialogs
        btnFinish.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigateUp()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /**
     * Setzt die OnClickListener für die Antwortbuttons (A, B, C, D) und den Button "Weiter".
     */
    private fun setOnClickListeners() {
        binding.btnA.setOnClickListener(this)
        binding.btnB.setOnClickListener(this)
        binding.btnC.setOnClickListener(this)
        binding.btnD.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    /**
     * Begleitendes Objekt, das die Liste von QuestionModel-Objekten und die Zeit für das Quiz speichert.
     */
    companion object {
        lateinit var questionModelList: List<QuestionModel>
        lateinit var time: String
    }
}