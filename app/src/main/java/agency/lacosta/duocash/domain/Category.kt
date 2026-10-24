package agency.lacosta.duocash.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import agency.lacosta.duocash.R

enum class Category(
    val key: String,
    val type: MovementType,
    val icon: ImageVector,
    @StringRes val nameRes: Int,
) {
    FOOD("food", MovementType.EXPENSE, Icons.Outlined.Restaurant, R.string.category_food),
    GROCERIES("groceries", MovementType.EXPENSE, Icons.Outlined.ShoppingCart, R.string.category_groceries),
    TRANSPORT("transport", MovementType.EXPENSE, Icons.Outlined.DirectionsCar, R.string.category_transport),
    HOME("home", MovementType.EXPENSE, Icons.Outlined.Home, R.string.category_home),
    UTILITIES("utilities", MovementType.EXPENSE, Icons.Outlined.Bolt, R.string.category_utilities),
    HEALTH("health", MovementType.EXPENSE, Icons.Outlined.LocalHospital, R.string.category_health),
    FUN("fun", MovementType.EXPENSE, Icons.Outlined.Celebration, R.string.category_fun),
    SHOPPING("shopping", MovementType.EXPENSE, Icons.Outlined.ShoppingBag, R.string.category_shopping),
    EDUCATION("education", MovementType.EXPENSE, Icons.Outlined.School, R.string.category_education),
    OTHER_EXPENSE("other_expense", MovementType.EXPENSE, Icons.Outlined.MoreHoriz, R.string.category_other),
    SALARY("salary", MovementType.INCOME, Icons.Outlined.Payments, R.string.category_salary),
    SALES("sales", MovementType.INCOME, Icons.Outlined.Storefront, R.string.category_sales),
    GIFT("gift", MovementType.INCOME, Icons.Outlined.CardGiftcard, R.string.category_gift),
    OTHER_INCOME("other_income", MovementType.INCOME, Icons.Outlined.MoreHoriz, R.string.category_other);

    companion object {
        fun forType(type: MovementType): List<Category> = entries.filter { it.type == type }

        fun fromKey(key: String): Category = entries.firstOrNull { it.key == key } ?: OTHER_EXPENSE
    }
}
