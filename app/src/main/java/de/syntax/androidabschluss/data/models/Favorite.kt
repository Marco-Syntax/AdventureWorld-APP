package de.syntax.androidabschluss.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
// Entitätsannotation für Room-Datenbank
@Entity(tableName = "fav_table")
data class Favorite(
    // Primärschlüssel für die Favoriten, automatisch generiert
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    // Name des Künstlers
    val artistName: String = "",

    // URL des Album-Artworks
    val artworkUrl100: String = "",

    // URL für die Hörprobe
    val previewUrl: String = "",

    // Primärer Genre-Name
    val primaryGenreName: String = ""
)