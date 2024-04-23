package de.syntax.androidabschluss.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentLoginBinding
import de.syntax.androidabschluss.viewModels.FirebaseViewModel

class LoginFragment : Fragment() {
    // ViewModel für die Firebase-Authentifizierung
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Datenbindung initialisieren
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = binding.lexi
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // Navigationsaktion zum Registrierungsfragment
        binding.loginSignupButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }

        // Anmeldeaktion
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEdit.text.toString()
            val password = binding.loginPasswordEdit.text.toString()

            // Überprüfen, ob Email und Passwort nicht leer sind
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                viewModel.login(email, password) // Anmeldeversuch über das ViewModel
            }
        }


        // Beobachten des aktuellen Benutzers und Weiterleitung zur Hauptaktivität, wenn eingeloggt
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                findNavController().navigate(R.id.mainActivity)
            }
        }
    }
}