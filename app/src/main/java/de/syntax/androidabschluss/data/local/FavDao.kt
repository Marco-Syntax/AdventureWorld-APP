package de.syntax.androidabschluss.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.syntax.androidabschluss.data.models.Favorite

// DAO (Data Access Object) definiert die Datenzugriffsmethoden für die Favoriten-Datenbank
@Dao
interface FavoriteDatabaseDao {

    // Fügt einen Favoriten zur Datenbank hinzu oder aktualisiert ihn, wenn er bereits vorhanden ist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: Favorite)

    // Gibt alle Favoriten als LiveData-Objekt zurück, um Änderungen in Echtzeit zu beobachten
    @Query("SELECT * FROM fav_table")
    fun getAll(): LiveData<List<Favorite>>

    // Löscht einen Favoriten anhand seiner ID aus der Datenbank
    @Query("DELETE FROM fav_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}




