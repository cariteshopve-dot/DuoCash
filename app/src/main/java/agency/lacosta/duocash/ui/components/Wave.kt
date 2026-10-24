package agency.lacosta.duocash.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ola de relleno: bandas onduladas una sobre otra, la firma de lacosta.agency.
 * La última banda debe ser el color del fondo que sigue debajo.
 */
@Composable
fun WaveBands(colors: List<Color>, modifier: Modifier = Modifier, height: Dp = 36.dp, waves: Int = 3) {
    Canvas(modifier.fillMaxWidth().height(height)) {
        val band = size.height / (colors.size + 1)
        val step = size.width / waves
        colors.forEachIndexed { i, color ->
            val mid = band * (i + 1)
            val amp = band * 0.6f
            val path = Path().apply {
                moveTo(0f, mid)
                for (k in 0 until waves) {
                    val x = k * step
                    cubicTo(x + step * 0.3f, mid - amp, x + step * 0.7f, mid + amp, x + step, mid)
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color)
        }
    }
}

/** Ola de línea: separador y subrayado. */
@Composable
fun WaveLine(color: Color, modifier: Modifier = Modifier, height: Dp = 8.dp, waves: Int = 6) {
    Canvas(modifier.height(height)) {
        val step = size.width / waves
        val mid = size.height / 2
        val path = Path().apply {
            moveTo(0f, mid)
            for (k in 0 until waves) {
                val x = k * step
                cubicTo(x + step * 0.3f, 0f, x + step * 0.7f, size.height, x + step, mid)
            }
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
    }
}
