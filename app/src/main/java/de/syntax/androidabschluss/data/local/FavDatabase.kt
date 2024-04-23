package de.syntax.androidabschluss.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.androidabschluss.data.models.Favorite

// Annotation zur Definition der Datenbank mit den Entitäten und der Version
@Database(entities = [Favorite::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    // Abstrakte Funktion zur Rückgabe des DAOs für Favoritenoperationen
    abstract val favoriteDao: FavoriteDatabaseDao
}

// Singleton-Muster für den Zugriff auf die Datenbank
private lateinit var INSTANCE: FavoriteDatabase

// Funktion zur Erstellung oder Rückgabe der Datenbankinstanz
fun getDatabase(context: Context): FavoriteDatabase {
    // Synchronisiert den Zugriff auf den Datenbank-Instanzen-Checker
    synchronized(FavoriteDatabase::class.java) {
        // Überprüft, ob die Datenbankinstanz bereits initialisiert wurde
        if (!::INSTANCE.isInitialized) {
            // Erstellt die Datenbankinstanz mithilfe des Room-Datenbankbuilders
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                FavoriteDatabase::class.java,
                "fav_database" // Name der Datenbankdatei
            ).build()
        }
        // Gibt die Datenbankinstanz zurück
        return INSTANCE
    }
}