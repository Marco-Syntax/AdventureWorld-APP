package de.syntax.androidabschluss.util

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.models.Ebook
import de.syntax.androidabschluss.databinding.ItemBookBinding
import de.syntax.androidabschluss.ui.ebook.BookFragmentDirections

class EbookAdapter(
    private val dataset: List<Ebook>, // Liste der E-Books
) : RecyclerView.Adapter<EbookAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Erstellen eines ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Aufblasen des Layouts für jedes Listenelement
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // Anzahl der Elemente in der Liste
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Binden von Daten an die Ansichtselemente jedes Listenelements
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val ebook = dataset[position] // Aktuelles E-Book

        // Laden und Anzeigen des E-Book-Covers
        val imageUri = ebook.artworkUrl100.toUri().buildUpon().scheme("https").build()
        holder.binding.ivBook.load(imageUri) {
            transformations(RoundedCornersTransformation(5f)) // Abrunden der Ecken
            error(R.drawable.ic_broken_image) // Fallback-Bild, falls das Cover nicht geladen werden kann
        }

        // Setzen des Autors und des Erscheinungsdatums des E-Books
        holder.binding.tvBookAutor.text = ebook.artistName
        holder.binding.tvBookRelease.text = ebook.releaseDate

        // Setzen der Buchbeschreibung
        holder.binding.tvDescription.text = ebook.description

        // Klick-Listener für die Schaltfläche zum Anzeigen/Verbergen der Buchbeschreibung
        holder.binding.btnDetail.setOnClickListener {
            if (holder.binding.descriptionLayout.visibility == View.GONE) {
                // Wenn die Beschreibung ausgeblendet ist, sie einblenden
                TransitionManager.beginDelayedTransition(holder.binding.itemBook, AutoTransition())
                holder.binding.descriptionLayout.visibility = View.VISIBLE
            } else {
                // Wenn die Beschreibung angezeigt wird, sie ausblenden
                TransitionManager.beginDelayedTransition(holder.binding.itemBook, AutoTransition())
                holder.binding.descriptionLayout.visibility = View.GONE
            }
        }

        // Klick-Listener für das Listenelement
        holder.binding.cvBook.setOnClickListener {
            // Navigation zu den Detailinformationen des ausgewählten E-Books
            val controller = holder.binding.cvBook.findNavController()
            controller.navigate(
                BookFragmentDirections.actionBookFragmentToBookDetailFragment(
                    ebook.artistName,
                    ebook.artworkUrl100,
                    ebook.previewUrl
                )
            )
        }
    }
}