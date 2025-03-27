package br.com.mdr.weather.presentation.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

//Dia limpo
val TopDaySkyBlue = Color(0xFF1f80a8)
val BottomDaySkyBlue = Color(0xFF91c7ea)

//Tarde limpa
val TopMidSkyBlue = Color(0xFFFF4682B4)
val BottomMidSkyBlue = Color(0xFFADD8E6)

//Noite limpa
val TopDarkSkyBlue = Color(0xFF050419)
val BottomDarkSkyBlue = Color(0xFF2f3e60)

//Noite nublada
val TopCloudySky = Color(0xFF20222f)
val BottomCloudySky = Color(0xFF1f2535)

val TransparentWhite = Color(0x44FFFFFF)

@Composable
fun WindCompass1(
    degree: Float,
    speed: Int,
    modifier: Modifier = Modifier,
    circleColor: Color
) {
    Canvas(modifier = modifier) {
        val size = this.size
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2.0f // Ajusta para caber dentro do Canvas

        val textPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
            isFakeBoldText = true
        }

        val directions = listOf("N", "L", "S", "O")
        val angles = listOf(0f, 90f, 180f, 270f)

        // Tamanho proporcional para texto
        val cardinalTextSize = size.minDimension / 15
        val speedTextSize = size.minDimension / 8
        val unitTextSize = size.minDimension / 10

        textPaint.textSize = cardinalTextSize

        fun canDrawLine(i: Int): Boolean {
            return (i != 0) && (i != 1) && (i != 22) && (i != 23) && (i !in 44..46) &&
                    (i != 67) && (i != 68) && (i != 89) && (i != 89) && (i != 89)
        }

        // Draw clock-like ticks (90 points)
        for (i in 0 until 90) {
            val angle = Math.toRadians(i * 4.0) // 4° increments for 90 points
            val start = Offset(
                center.x + (radius - size.minDimension / 20) * cos(angle).toFloat(),
                center.y - (radius - size.minDimension / 20) * sin(angle).toFloat()
            )
            val end = Offset(
                center.x + radius * cos(angle).toFloat(),
                center.y - radius * sin(angle).toFloat()
            )
            if (canDrawLine(i)) {
                drawLine(
                    color = if (i % 9 == 0)
                        Color.White.copy(alpha = 0.7f)
                    else
                        TransparentWhite, // Major ticks every 10 points
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Draw direction letters
        for (i in directions.indices) {
            val angle = Math.toRadians(angles[i].toDouble())
            val x = center.x + (radius - size.minDimension / 40) * cos(angle).toFloat()
            val y = center.y - (radius - size.minDimension / 40) * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(
                directions[i],
                x,
                y + textPaint.textSize / 3, // Adjust for baseline
                textPaint
            )
        }

        // Draw arrow (front and back parts)
        val pointerLength = radius - size.minDimension / 15
        val arrowAngle = Math.toRadians(degree.toDouble())
        val arrowHead = Offset(
            center.x + pointerLength * cos(arrowAngle).toFloat(),
            center.y - pointerLength * sin(arrowAngle).toFloat()
        )
        val arrowTail = Offset(
            center.x - pointerLength * cos(arrowAngle).toFloat(),
            center.y + pointerLength * sin(arrowAngle).toFloat()
        )

        drawLine(
            color = Color.White,
            start = arrowTail,
            end = arrowHead,
            strokeWidth = size.minDimension / 40,
            cap = StrokeCap.Round
        )

        // Draw arrow head (triangle)
        val arrowHeadSize = size.minDimension / 15
        val arrowHeadLeft = Offset(
            arrowHead.x - arrowHeadSize * sin(arrowAngle).toFloat(),
            arrowHead.y - arrowHeadSize * cos(arrowAngle).toFloat()
        )
        val arrowHeadRight = Offset(
            arrowHead.x + arrowHeadSize * sin(arrowAngle).toFloat(),
            arrowHead.y + arrowHeadSize * cos(arrowAngle).toFloat()
        )
        drawPath(
            path = Path().apply {
                moveTo(arrowHead.x, arrowHead.y)
                lineTo(arrowHeadLeft.x, arrowHeadLeft.y)
                lineTo(arrowHeadRight.x, arrowHeadRight.y)
                close()
            },
            color = Color.Red
        )

        drawCircle(
            color = circleColor,
            radius = radius / 2,
            center
        )

        // Draw speed text
        textPaint.textSize = speedTextSize
        drawContext.canvas.nativeCanvas.drawText(
            "$speed",
            center.x,
            center.y - speedTextSize / 10,
            textPaint
        )

        // Draw unit text
        textPaint.textSize = unitTextSize
        drawContext.canvas.nativeCanvas.drawText(
            "km/h",
            center.x,
            center.y + unitTextSize / 1.2f,
            textPaint.apply {
                isFakeBoldText = false
            }
        )
    }
}