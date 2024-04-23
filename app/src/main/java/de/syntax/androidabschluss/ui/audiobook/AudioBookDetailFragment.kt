package de.syntax.androidabschluss.ui.audiobook

import android.animation.ObjectAnimator
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.models.AudioBook
import de.syntax.androidabschluss.data.models.Favorite
import de.syntax.androidabschluss.databinding.FragmentAudioBookDetailBinding
import de.syntax.androidabschluss.viewModels.SharedViewModel

class AudioBookDetailFragment : Fragment() {

    private lateinit var binding: FragmentAudioBookDetailBinding
    private val viewModel: SharedViewModel by activityViewModels()
    private var artistName = ""
    private var mediaPlayer: MediaPlayer? = null
    private var isSeekbarTracking =
        false // Flag, um zu verfolgen, ob der Benutzer die SeekBar bearbeitet
    private var seekBarUpdateHandler: Handler? = null
    private var audioBooks: List<AudioBook> = emptyList() // Liste der verfügbaren AudioBooks
    private var currentAudioIndex: Int = 0 // Index des aktuellen AudioBooks
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            artistName = it.getString("artistName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        translate()

        // Setze den Fortschritt der SeekBar auf 0 beim Erstellen der Ansicht
        binding.seekbar.progress = 0


        val gifImageLexi = binding.lexiGif
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(gifImageLexi)

        binding.lexiGif.setOnClickListener {
            findNavController().navigate(AudioBookDetailFragmentDirections.actionAudioBookDetailFragmentToChatBotFragment())
        }

        // Beobachten der Liste der AudioBooks
        viewModel.audioBookList.observe(viewLifecycleOwner) { audioBooks ->
            this.audioBooks = audioBooks // Aktualisiere die Liste der AudioBooks
            val audioBook = audioBooks.find { it.artistName == artistName }
            audioBook?.let {
                // Lade Bild und Details des AudioBooks
                binding.ivAudiobook.load(audioBook.artworkUrl100)
                binding.titleAudio.text = audioBook.artistName
                binding.gener.text = audioBook.primaryGenreName

                // Event Listener für Abspiel-, Nächster-, Vorheriger- und Like-Schaltflächen
                binding.playBtn.setOnClickListener {
                    togglePlayback(audioBook.previewUrl)
                }
                binding.nextBtn.setOnClickListener {
                    playNext()
                }
                binding.backBtn.setOnClickListener {
                    playPrevious()
                }
                // Initialisierung des Like-Status beim Laden der Ansicht
                viewModel.favorite.observe(viewLifecycleOwner) { favorites ->
                    isLiked = favorites.any { it.artistName == audioBook.artistName }
                    // Bild entsprechend des Like-Status laden
                    loadLikeImage(isLiked)
                }

                // Überprüfe den Like-Status vor dem Setzen des Klicklisteners
                if (isLiked) {
                    // Wenn das Audiobook bereits geliked ist, lade das statische Like-Bild
                    loadLikeImage(true)
                    binding.btnLike.isEnabled = false // Deaktiviere den Button


                } else {
                    // Wenn das Audiobook nicht geliked ist, lade das animierte Like-GIF
                    loadLikeImage(false)
                    // Setze den Klicklistener für die Like-Schaltfläche
                    binding.btnLike.setOnClickListener {
                        // Füge das AudioBook als Favorit hinzu und zeige eine Benachrichtigung
                        viewModel.insertFavorite(
                            Favorite(
                                null,
                                audioBook.artistName,
                                audioBook.artworkUrl100,
                                audioBook.previewUrl,
                                audioBook.primaryGenreName
                            )
                        )
                        Toast.makeText(context, "Als Favorite hinzugefügt", Toast.LENGTH_SHORT)
                            .show()
                        isLiked = true
                        // Lade das statische Like-Bild, da das Audiobook jetzt geliked ist
                        loadLikeImage(true)
                        binding.btnLike.isEnabled = false // Deaktiviere den Button

                    }
                }

            }
        }


        // Event Listener für die SeekBar
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Verfolge die SeekBar Änderungen und aktualisiere die MediaPlayer Position
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Setze das Flag, um zu verfolgen, dass der Benutzer die SeekBar bearbeitet
                isSeekbarTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Setze das Flag zurück, da der Benutzer die Bearbeitung der SeekBar beendet hat
                isSeekbarTracking = false
            }
        })
    }

    // Funktion zum Laden des Like-Bildes basierend auf dem Like-Status
    private fun loadLikeImage(isLiked: Boolean) {
        // Lade das GIF für die "Like" Schaltfläche
        val gifImage = binding.btnLike
        val imageResId = if (isLiked) R.drawable.likepng else R.drawable.like
        Glide.with(this)
            .load(imageResId)
            .into(gifImage)
    }


    private fun translate() {

        val animator = ObjectAnimator.ofFloat(binding.lexiGif, View.TRANSLATION_Y, -40f)
        animator.duration = 600
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    // Funktion zum Starten oder Anhalten der Wiedergabe des AudioClips
    private fun togglePlayback(audioUrl: String?) {
        if (mediaPlayer == null) {
            // Erstelle einen neuen MediaPlayer und bereite ihn vor
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    // Starte die Wiedergabe, wenn die Vorbereitung abgeschlossen ist
                    start()
                    // Setze den maximalen Fortschritt der SeekBar auf die Dauer des Mediums
                    binding.seekbar.max = mediaPlayer!!.duration
                    // Starte die Aktualisierung der SeekBar
                    startSeekBarUpdate()
                    // Ändere das Play-Icon auf Pause
                    binding.playBtn.setImageResource(R.drawable.baseline_pause_24)
                }
            }
        } else {
            mediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    // Pause die Wiedergabe, wenn der MediaPlayer gerade spielt
                    mediaPlayer.pause()
                    // Ändere das Pause-Icon auf Play
                    binding.playBtn.setImageResource(R.drawable.baseline_play_arrow_24)
                } else {
                    // Fortsetzen der Wiedergabe, wenn der MediaPlayer gerade pausiert
                    mediaPlayer.start()
                    // Ändere das Play-Icon auf Pause
                    binding.playBtn.setImageResource(R.drawable.baseline_pause_24)
                }
            }
        }
    }

    // Funktion zum Starten der Aktualisierung der SeekBar
    private fun startSeekBarUpdate() {
        // Beginne mit dem Aktualisieren des Fortschritts der SeekBar
        seekBarUpdateHandler = Handler(Looper.getMainLooper())
        seekBarUpdateHandler?.post(object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (!isSeekbarTracking) {
                        // Aktualisiere die SeekBar Position basierend auf der aktuellen Position des MediaPlayers
                        binding.seekbar.progress = player.currentPosition
                    }
                }
                // Aktualisierung alle 100 Millisekunden
                seekBarUpdateHandler?.postDelayed(this, 100)
            }
        })
    }

    // Funktion zum Abspielen des nächsten AudioClips in der Liste
    private fun playNext() {
        // Erhöhe den Index um 1 und überprüfe, ob das Ende der Liste erreicht ist
        currentAudioIndex++
        if (currentAudioIndex >= audioBooks.size) {
            // Wenn das Ende der Liste erreicht ist, setze den Index auf 0, um von vorne zu beginnen
            currentAudioIndex = 0
        }
        // Stoppe die Wiedergabe des aktuellen Tracks, falls gerade aktiv
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        // Starte die Wiedergabe des nächsten Tracks
        togglePlayback(audioBooks[currentAudioIndex].previewUrl)
        // Aktualisiere das Bild und den Titel des neuen Audio-Books
        val nextAudioBook = audioBooks[currentAudioIndex]
        binding.ivAudiobook.load(nextAudioBook.artworkUrl100)
        binding.titleAudio.text = nextAudioBook.artistName
        binding.gener.text = nextAudioBook.primaryGenreName
    }

    // Funktion zum Abspielen des vorherigen AudioClips in der Liste
    private fun playPrevious() {
        // Verringere den Index um 1 und überprüfe, ob wir das Anfang der Liste erreicht haben
        currentAudioIndex--
        if (currentAudioIndex < 0) {
            // Wenn der Anfang der Liste erreicht ist, setze den Index auf das Ende der Liste
            currentAudioIndex = audioBooks.size - 1
        }
        // Stoppe die Wiedergabe des aktuellen Tracks, falls gerade aktiv
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        // Starte die Wiedergabe des vorherigen Tracks
        togglePlayback(audioBooks[currentAudioIndex].previewUrl)
        // Aktualisiere das Bild und den Titel des neuen Audio-Books
        val previousAudioBook = audioBooks[currentAudioIndex]
        binding.ivAudiobook.load(previousAudioBook.artworkUrl100)
        binding.titleAudio.text = previousAudioBook.artistName
        binding.gener.text = previousAudioBook.primaryGenreName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Freigebe des MediaPlayers und Handler beim Verlassen der Ansicht
        mediaPlayer?.release()
        mediaPlayer = null
        seekBarUpdateHandler?.removeCallbacksAndMessages(null) // Entferne alle ausstehenden Nachrichten und Rückrufe aus der Warteschlange
        seekBarUpdateHandler = null // Setzt den Handler zurück
    }
}