package de.syntax.androidabschluss.ui

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentSettingBinding
import de.syntax.androidabschluss.viewModels.FirebaseViewModel

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var userMail: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rotate()

        translate()


        // Beobachte den aktuellen Benutzer im ViewModel und aktualisiere die Ansicht entsprechend
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                userMail = user.email.toString()
                binding.tvLoginUser.text = userMail
            }
        }

        // OnClickListener für die Abmelde- und Lösch-Schaltflächen
        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }
        binding.btnDel.setOnClickListener {
            deleteDialog()
        }

        // Chatbot-Bild einrichten
        val image = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // OnClickListener für Chatbot-Bild festlegen
        binding.chatBot.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToChatBotFragment())
        }

        binding.info.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToInfoFragment())
        }
    }

    private fun rotate() {

        val animator = ObjectAnimator.ofFloat(binding.info, View.ROTATION, 0f, 360f)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    private fun translate() {

        val animator = ObjectAnimator.ofFloat(binding.info, View.TRANSLATION_Y, -20f)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()

    }


    /**
     * Methode zum Anzeigen eines Dialogs zur Bestätigung der Abmeldung des Benutzers.
     */
    private fun logoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val radius = 20f
        val shapeDrawable = ShapeDrawable()

        shapeDrawable.shape = RoundRectShape(
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null
        )

        shapeDrawable.paint.color = Color.GRAY
        builder.setTitle("Möchtest du dich abmelden?")

        builder.setPositiveButton("Abmelden") { _, _ ->
            viewModel.logout()
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginActivity2())
        }

        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(shapeDrawable)
        dialog.show()
    }

    /**
     * Methode zum Anzeigen eines Dialogs zur Bestätigung der Löschung des Benutzerkontos.
     */
    private fun deleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val radius = 20f
        val shapeDrawable = ShapeDrawable()

        shapeDrawable.shape = RoundRectShape(
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null
        )
        shapeDrawable.paint.color = Color.GRAY
        builder.setTitle("Möchtest du dein Konto wirklich dauerhaft löschen?")

        builder.setPositiveButton("Konto Löschen") { _, _ ->
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Erfolgreich gelöcht", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginActivity2())
                } else {
                    Toast.makeText(
                        context,
                        "Bitte melde dich zuerst ab um dein Konto zu löschen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(shapeDrawable)
        dialog.show()
    }
}