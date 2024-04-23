package de.syntax.androidabschluss.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentSignupBinding
import de.syntax.androidabschluss.viewModels.FirebaseViewModel

class SignupFragment : Fragment() {
    // ViewModel für die Firebase-Authentifizierung
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Datenbindung initialisieren
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = binding.lexi
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // Navigationsaktion zum vorherigen Fragment
        binding.signupCancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Registrierungsaktion
        binding.signupButton.setOnClickListener {
            signup()
        }

        // Beobachten des aktuellen Benutzers und Weiterleitung zur Hauptaktivität, wenn eingeloggt
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                findNavController().navigate(R.id.mainActivity)
            }
        }
    }

    // Funktion für die Registrierung
    private fun signup() {
        val email = binding.signupMail.text.toString()
        val password = binding.signupPassword.text.toString()

        // Überprüfen, ob Email und Passwort nicht leer sind
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            viewModel.signup(email, password) // Registrierung über das ViewModel
            // Navigation zur Hauptaktivität nach erfolgreicher Registrierung
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToMainActivity())
        } else {
            Toast.makeText(
                requireContext(),
                "Email und Passwort dürfen nicht leer sein",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}