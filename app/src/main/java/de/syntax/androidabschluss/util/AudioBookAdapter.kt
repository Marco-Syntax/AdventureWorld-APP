package de.syntax.androidabschluss.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.models.AudioBook
import de.syntax.androidabschluss.databinding.ItemAudioBookBinding
import de.syntax.androidabschluss.ui.audiobook.AudioBookFragmentDirections

class AudioBookAdapter(
    private val dataset: List<AudioBook> // Liste von Hörbüchern
) : RecyclerView.Adapter<AudioBookAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemAudioBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Erstellen eines ViewHolders
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioBookAdapter.ItemViewHolder {
        // Aufblasen des Layouts für jedes Listenelement
        val binding =
            ItemAudioBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // Anzahl der Elemente in der Liste
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Binden von Daten an die Ansichtselemente jedes Listenelements
    override fun onBindViewHolder(holder: AudioBookAdapter.ItemViewHolder, position: Int) {
        val audioBook = dataset[position] // Aktuelles Hörbuch

        // Laden und Anzeigen des Hörbuch-Covers
        val imageUri = audioBook.artworkUrl100.toUri().buildUpon().scheme("https").build()
        holder.binding.imgAudioBook.load(imageUri) {
            transformations(RoundedCornersTransformation(5f)) // Abrunden der Ecken
            error(R.drawable.ic_broken_image) // Fallback-Bild, falls das Cover nicht geladen werden kann
        }

        // Setzen des Hörbuchnamens
        holder.binding.audioBookName.text = audioBook.collectionName

        // Klick-Listener für das Listenelement
        holder.binding.cvAudioBook.setOnClickListener {
            // Navigieren zu den Details des ausgewählten Hörbuchs
            val controller = holder.binding.cvAudioBook.findNavController()
            controller.navigate(
                AudioBookFragmentDirections.actionAudioBookFragmentToAudioBookDetailFragment(
                    audioBook.artistName
                )
            )
        }
    }
}