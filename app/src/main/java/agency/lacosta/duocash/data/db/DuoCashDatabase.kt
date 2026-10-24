package agency.lacosta.duocash.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovementEntity::class], version = 1, exportSchema = true)
abstract class DuoCashDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao

    companion object {
        fun create(context: Context): DuoCashDatabase =
            Room.databaseBuilder(context, DuoCashDatabase::class.java, "duocash.db").build()
    }
}
