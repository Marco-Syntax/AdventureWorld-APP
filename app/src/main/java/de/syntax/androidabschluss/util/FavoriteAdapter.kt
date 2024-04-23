package de.syntax.androidabschluss.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.syntax.androidabschluss.data.models.Favorite
import de.syntax.androidabschluss.databinding.ItemFavoriteBinding
import de.syntax.androidabschluss.ui.FavoriteFragmentDirections
import de.syntax.androidabschluss.viewModels.SharedViewModel

class FavoriteAdapter(
    private var dataset: List<Favorite>, // Liste der Favoriten
    private var viewModel: SharedViewModel, // Viewmodel für Interaktionen mit den Favoriten
    private val context: Context // Aktueller Kontext
) : RecyclerView.Adapter<FavoriteAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Erstellen eines ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Aufblasen des Layouts für jedes Listenelement
        val binding =
            ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // Anzahl der Elemente in der Liste
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Binden von Daten an die Ansichtselemente jedes Listenelements
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val fav = dataset[position] // Aktueller Favorit

        // Laden und Anzeigen des Cover-Bilds des Favoriten
        val imageUri = fav.artworkUrl100.toUri().buildUpon().scheme("https").build()
        holder.binding.imgFav.load(imageUri)

        // Setzen des Namens des Favoriten
        holder.binding.tvFavName.text = fav.artistName

        // Klick-Listener für das Listenelement
        holder.binding.cvFavorite.setOnClickListener {
            // Navigation zur Detailansicht des ausgewählten Favoriten
            val controller = holder.binding.cvFavorite.findNavController()
            controller.navigate(
                FavoriteFragmentDirections.actionFavoritenFragmentToAudioBookDetailFragment(
                    fav.artistName
                )
            )
        }

        // Langer Klick-Listener für das Listenelement
        holder.binding.cvFavorite.setOnLongClickListener {
            // Erstellen eines Bestätigungsdialogs für das Löschen des Favoriten
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.apply {
                setTitle("Möchtest du es wirklich löschen?") // Dialogtitel
                setPositiveButton("Ja") { _, _ ->
                    // Wenn "Ja" geklickt wird, rufe die Methode zum Löschen des Favoriten auf
                    viewModel.deleteById(fav.id!!)
                }
                setNegativeButton("Nein") { dialog, _ ->
                    // Wenn "Nein" geklickt wird, schließe den Dialog
                    dialog.cancel()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show() // Zeige den Dialog an
            true // Rückgabe "true", um zu signalisieren, dass das Ereignis verarbeitet wurde
        }
    }
}