package agency.lacosta.duocash.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Query("SELECT * FROM movements WHERE occurredAt >= :from AND occurredAt < :to ORDER BY occurredAt DESC, id DESC")
    fun observeBetween(from: Long, to: Long): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE id = :id")
    suspend fun getById(id: Long): MovementEntity?

    @Upsert
    suspend fun upsert(movement: MovementEntity): Long

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun delete(id: Long)
}
