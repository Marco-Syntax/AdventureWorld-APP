package de.syntax.androidabschluss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentFavoritBinding
import de.syntax.androidabschluss.util.FavoriteAdapter
import de.syntax.androidabschluss.viewModels.SharedViewModel

class FavoriteFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var binding: FragmentFavoritBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bild für den Chatbot laden
        val image = binding.chatBot
        Glide.with(this)
            .load(R.drawable.lexigif)
            .into(image)

        // Klick-Listener für den Chatbot-Button
        binding.chatBot.setOnClickListener {
            findNavController().navigate(FavoriteFragmentDirections.actionFavoritenFragmentToChatBotFragment())
        }

        // Beobachtet die Favoritenliste im ViewModel und aktualisiert die RecyclerView, wenn sich die Daten ändern
        viewModel.favorite.observe(viewLifecycleOwner) { fav ->
            binding.rvFavoriten.adapter = FavoriteAdapter(
                fav.reversed(),
                viewModel,
                requireContext()
            ) // Setzt den Adapter für die RecyclerView
            viewModel.getAllFavorite()
        }
    }
}