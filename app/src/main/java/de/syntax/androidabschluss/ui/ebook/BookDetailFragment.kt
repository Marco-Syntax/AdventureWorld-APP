package de.syntax.androidabschluss.ui.ebook

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentBookDetailBinding
import de.syntax.androidabschluss.viewModels.SharedViewModel

class BookDetailFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailBinding
    private val viewModel: SharedViewModel by activityViewModels()

    private var artistName: String = ""
    private var artworkUrl100: String = ""
    private var previewUrl: String = ""

    // Wird aufgerufen, wenn das Fragment erstellt wird
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Holen der Argumente aus dem Bundle
            artistName = it.getString("artistName").toString()
            artworkUrl100 = it.getString("artworkUrl100").toString()
            previewUrl = it.getString("previewUrl").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Wird aufgerufen, nachdem die Ansicht des Fragments erstellt wurde
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        translate()

        val gifImage = binding.lexiGif
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(gifImage)

        binding.lexiGif.setOnClickListener {
            findNavController().navigate(BookDetailFragmentDirections.actionBookDetailFragmentToChatBotFragment())
        }

        // Klicken-Listener für die Schaltfläche zum Lesen des Buches
        binding.btnRead.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://itunes.apple.com/$previewUrl"))
            startActivity(intent)
        }
        // Laden des Buchcovers und Anzeigen des Buchtitels
        binding.imgCoverDetail.load(artworkUrl100)
        binding.tvTitleEbook.text = artistName

    }


    private fun translate() {

        val animator = ObjectAnimator.ofFloat(binding.lexiGif, View.TRANSLATION_Y, -40f)
        animator.duration = 600
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }
}