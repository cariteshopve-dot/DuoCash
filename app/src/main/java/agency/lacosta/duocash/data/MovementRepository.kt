package agency.lacosta.duocash.data

import agency.lacosta.duocash.data.db.MovementDao
import agency.lacosta.duocash.data.db.toDomain
import agency.lacosta.duocash.data.db.toEntity
import agency.lacosta.duocash.domain.Movement
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovementRepository(private val dao: MovementDao) {

    fun observeMonth(month: YearMonth, zone: ZoneId = ZoneId.systemDefault()): Flow<List<Movement>> {
        val from = month.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val to = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return dao.observeBetween(from, to).map { list -> list.map { it.toDomain() } }
    }

    suspend fun get(id: Long): Movement? = dao.getById(id)?.toDomain()

    suspend fun save(movement: Movement): Long = dao.upsert(movement.toEntity())

    suspend fun delete(id: Long) = dao.delete(id)
}
