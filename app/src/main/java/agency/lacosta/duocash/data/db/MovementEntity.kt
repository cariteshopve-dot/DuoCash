package agency.lacosta.duocash.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import agency.lacosta.duocash.domain.Category
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.Movement
import agency.lacosta.duocash.domain.MovementType
import java.time.Instant

@Entity(tableName = "movements", indices = [Index("occurredAt")])
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amountMinor: Long,
    val currency: String,
    val counterAmountMinor: Long,
    val counterCurrency: String,
    /** BigDecimal como texto, para no perder decimales. */
    val rate: String,
    val category: String,
    val note: String,
    val occurredAt: Long,
    val createdAt: Long,
)

fun MovementEntity.toDomain() = Movement(
    id = id,
    type = MovementType.valueOf(type),
    amountMinor = amountMinor,
    currency = Currency.fromCode(currency),
    counterAmountMinor = counterAmountMinor,
    counterCurrency = Currency.fromCode(counterCurrency),
    rate = rate.toBigDecimal(),
    category = Category.fromKey(category),
    note = note,
    occurredAt = Instant.ofEpochMilli(occurredAt),
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun Movement.toEntity() = MovementEntity(
    id = id,
    type = type.name,
    amountMinor = amountMinor,
    currency = currency.code,
    counterAmountMinor = counterAmountMinor,
    counterCurrency = counterCurrency.code,
    rate = rate.toPlainString(),
    category = category.key,
    note = note,
    occurredAt = occurredAt.toEpochMilli(),
    createdAt = createdAt.toEpochMilli(),
)
