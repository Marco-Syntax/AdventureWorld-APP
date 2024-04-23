package de.syntax.androidabschluss.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Datenbindung initialisieren
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gifImage = binding.lexi
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(gifImage)

        // Navigationsaktion zum Anmeldefragment
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToLoginFragment())
        }

        // Navigationsaktion zur Registrierungsfragment
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToSignupFragment())
        }
    }
}