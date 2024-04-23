package de.syntax.androidabschluss.ui

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
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentInfoBinding
import de.syntax.androidabschluss.viewModels.FirebaseViewModel

class InfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoBinding
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var userMail: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rotate()
        val gifImage = binding.gifDev
        Glide.with(this)
            .load(R.drawable.dev)
            .into(gifImage)


        // Chatbot-Bild einrichten
        val lexiGif = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(lexiGif)

        // OnClickListener für Chatbot-Bild festlegen
        binding.chatBot.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionInfoFragmentToChatBotFragment())
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivLinkedIn.setOnClickListener {
            openLinkedInProfile()
        }

        binding.gitHub.setOnClickListener {
            openGithubProfile()
        }

    }

    private fun rotate() {

        val animator = ObjectAnimator.ofFloat(binding.ivLinkedIn, View.ROTATION, 0f, 360f)
        animator.duration = 1000
        animator.repeatCount = 2
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()

        val animatorTwo = ObjectAnimator.ofFloat(binding.gitHub, View.ROTATION, 0f, 360f)
        animatorTwo.duration = 1000
        animatorTwo.repeatCount = 2
        animatorTwo.repeatMode = ObjectAnimator.REVERSE
        animatorTwo.start()

    }

    private fun openLinkedInProfile() {
        val linkedInProfileUrl = "https://www.linkedin.com/in/marco-grimme/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInProfileUrl))
        startActivity(intent)
    }

    private fun openGithubProfile() {
        val gitHubProfileUrl = "https://github.com/Marco-Syntax"
        val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse(gitHubProfileUrl))
        startActivity(intent2)
    }


}