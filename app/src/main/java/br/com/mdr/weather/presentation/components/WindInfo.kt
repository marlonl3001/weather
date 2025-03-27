package br.com.mdr.weather.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.mdr.weather.R
import br.com.mdr.weather.data.model.Wind
import br.com.mdr.weather.presentation.ui.theme.EXTRA_SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.TopCloudySky
import br.com.mdr.weather.presentation.ui.theme.TransparentWhite
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WindInfo(backgroundColor: Color, windModel: Wind) {
    BlurCard(
        backgroundColor = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = MEDIUM_PADDING, vertical = SMALL_PADDING)
                .height(160.dp)
        ) {
            val (iconImage, titleLabel, constraintLayout, windCompass, frontIcon, backIcon) = createRefs()

            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .constrainAs(iconImage) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "",
                tint = TransparentWhite,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = EXTRA_SMALL_PADDING)
                    .constrainAs(titleLabel) {
                        bottom.linkTo(iconImage.bottom)
                        start.linkTo(iconImage.end)
                        top.linkTo(iconImage.top)
                    },
                text = stringResource(R.string.wind_label).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = TransparentWhite
            )

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.6f)
                    .constrainAs(constraintLayout) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            ) {
                val (windLabel, wind, gustLabel, gust, directionLabel,
                    direction, divider1, divider2) = createRefs()

                Text(
                    modifier = Modifier
                        .padding(top = MEDIUM_PADDING)
                        .constrainAs(windLabel) {
                            start.linkTo(divider1.start)
                            top.linkTo(parent.top)
                        },
                    text = stringResource(R.string.wind_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .padding(top = MEDIUM_PADDING)
                        .constrainAs(wind) {
                            end.linkTo(divider1.end)
                            top.linkTo(parent.top)
                        },
                    text = "${windModel.speed.roundToInt()} km/h",
                    style = MaterialTheme.typography.titleMedium,
                    color = TransparentWhite,
                    textAlign = TextAlign.End
                )
                CustomDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SMALL_PADDING)
                        .constrainAs(divider1) {
                            start.linkTo(parent.start)
                            top.linkTo(windLabel.bottom)
                        },
                    color = TransparentWhite
                )
                Text(
                    modifier = Modifier
                        .constrainAs(gustLabel) {
                            start.linkTo(divider2.start)
                            top.linkTo(divider1.bottom)
                        },
                    text = stringResource(R.string.gust_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .constrainAs(gust) {
                            end.linkTo(divider2.end)
                            top.linkTo(divider1.bottom)
                        },
                    text = "${windModel.gust.roundToInt()} km/h",
                    style = MaterialTheme.typography.titleMedium,
                    color = TransparentWhite,
                    textAlign = TextAlign.End
                )
                CustomDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SMALL_PADDING)
                        .constrainAs(divider2) {
                            start.linkTo(parent.start)
                            top.linkTo(gustLabel.bottom)
                        },
                    color = TransparentWhite
                )

                Text(
                    modifier = Modifier
                        .constrainAs(directionLabel) {
                            start.linkTo(divider2.start)
                            top.linkTo(divider2.bottom)
                        },
                    text = stringResource(R.string.direction),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .constrainAs(direction) {
                            end.linkTo(divider2.end)
                            top.linkTo(divider2.bottom)
                        },
                    text = windModel.getWindDirection(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TransparentWhite,
                    textAlign = TextAlign.End
                )
            }
            WindCompass(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight()
                    .padding(start = MEDIUM_PADDING, top = SMALL_PADDING)
                    .constrainAs(windCompass) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(constraintLayout.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },
                speed = windModel.speed.toInt(),
                degree = windModel.deg.toFloat(),
                circleColor = backgroundColor.copy(alpha = 0.6f)
            )
//            WindCompass(
////                modifier = Modifier
////                    .fillMaxWidth(fraction = 0.4f)
////                    .constrainAs(windCompass) {
////                        bottom.linkTo(parent.bottom)
////                        end.linkTo(parent.end)
////                        start.linkTo(constraintLayout.end)
////                        top.linkTo(parent.top)
////                    },
//                degree = windModel.deg.toFloat(),
//                speed = windModel.speed.roundToInt()
//            )
//            Icon(
//                modifier = Modifier
//                    .size(18.dp)
//                    .constrainAs(frontIcon) {
//                        bottom.linkTo(windCompass.bottom)
//                        top.linkTo(windCompass.top)
//                        start.linkTo(windCompass.start, margin = 36.dp)
//                    },
//                painter = painterResource(R.drawable.front_arrow),
//                tint = Color.White,
//                contentDescription = ""
//            )
//            Icon(
//                modifier = Modifier
//                    .size(18.dp)
//                    .constrainAs(backIcon) {
//                        bottom.linkTo(windCompass.bottom)
//                        top.linkTo(windCompass.top)
//                        end.linkTo(windCompass.end, margin = 36.dp)
//                    },
//                painter = painterResource(R.drawable.back_arrow),
//                tint = Color.White,
//                contentDescription = ""
//            )
        }
    }
}
//
//@Composable
//fun WindCompass(
//    windDirectionDegrees: Float,
//    windSpeed: Int,
//    modifier: Modifier = Modifier
//) {
//    Canvas(modifier = modifier) {
//        val size = this.size
//        val center = Offset(size.width / 2, size.height / 2)
//        val outerRadius = min(size.width, size.height) / 2.5f
//        val innerRadius = outerRadius / 2 // Raio do círculo central
//        val textPaint = Paint().asFrameworkPaint().apply {
//            isAntiAlias = true
//            textAlign = android.graphics.Paint.Align.CENTER
//            color = android.graphics.Color.WHITE
//            isFakeBoldText = true
//        }
//
//        val directions = listOf("N", "L", "S", "O")
//        val angles = listOf(0f, 90f, 180f, 270f)
//
//        // Tamanhos proporcionais
//        val cardinalTextSize = size.minDimension / 15
//        val speedTextSize = size.minDimension / 8
//        val unitTextSize = size.minDimension / 12
//        textPaint.textSize = cardinalTextSize
//
//        // Desenhar o círculo central
//        drawCircle(
//            color = Color.LightGray,
//            center = center,
//            radius = innerRadius
//        )
//
//        // Desenhar os traços da bússola (90 pontos)
//        for (i in 0 until 90) {
//            val angle = Math.toRadians(i * 4.0) // 4° increments
//            val start = Offset(
//                center.x + (outerRadius - size.minDimension / 20) * cos(angle).toFloat(),
//                center.y - (outerRadius - size.minDimension / 20) * sin(angle).toFloat()
//            )
//            val end = Offset(
//                center.x + outerRadius * cos(angle).toFloat(),
//                center.y - outerRadius * sin(angle).toFloat()
//            )
//
//            // Remover traços onde estão as letras
//            if (i % 22 == 0) continue
//
//            drawLine(
//                color = if (i % 10 == 0) Color.Black else Color.Gray,
//                start = start,
//                end = end,
//                strokeWidth = if (i % 10 == 0) 4.dp.toPx() else 2.dp.toPx()
//            )
//        }
//
//        // Desenhar as letras (N, L, S, O)
//        for (i in directions.indices) {
//            val angle = Math.toRadians(angles[i].toDouble())
//            val x = center.x + (outerRadius - size.minDimension / 12) * cos(angle).toFloat()
//            val y = center.y - (outerRadius - size.minDimension / 12) * sin(angle).toFloat()
//            drawContext.canvas.nativeCanvas.drawText(
//                directions[i],
//                x,
//                y + textPaint.textSize / 3,
//                textPaint
//            )
//        }
//
//        // Desenhar a flecha (dividida em duas partes)
//        val pointerLength = outerRadius - size.minDimension / 15
//        val arrowAngle = Math.toRadians(windDirectionDegrees.toDouble())
//
//        // Parte da frente da flecha
//        val arrowHeadStart = Offset(
//            center.x + innerRadius * cos(arrowAngle).toFloat(),
//            center.y - innerRadius * sin(arrowAngle).toFloat()
//        )
//        val arrowHeadEnd = Offset(
//            center.x + pointerLength * cos(arrowAngle).toFloat(),
//            center.y - pointerLength * sin(arrowAngle).toFloat()
//        )
//        drawLine(
//            color = Color.Red,
//            start = arrowHeadStart,
//            end = arrowHeadEnd,
//            strokeWidth = size.minDimension / 40,
//            cap = StrokeCap.Round
//        )
//
//        // Parte de trás da flecha
//        val arrowTailStart = Offset(
//            center.x - innerRadius * cos(arrowAngle).toFloat(),
//            center.y + innerRadius * sin(arrowAngle).toFloat()
//        )
//        val arrowTailEnd = Offset(
//            center.x - pointerLength * cos(arrowAngle).toFloat(),
//            center.y + pointerLength * sin(arrowAngle).toFloat()
//        )
//        drawLine(
//            color = Color.Black,
//            start = arrowTailStart,
//            end = arrowTailEnd,
//            strokeWidth = size.minDimension / 40,
//            cap = StrokeCap.Round
//        )
//
//        // Texto de velocidade
//        textPaint.textSize = speedTextSize
//        drawContext.canvas.nativeCanvas.drawText(
//            "$windSpeed",
//            center.x,
//            center.y - speedTextSize / 4,
//            textPaint
//        )
//
//        // Texto de unidade (km/h)
//        textPaint.textSize = unitTextSize
//        drawContext.canvas.nativeCanvas.drawText(
//            "km/h",
//            center.x,
//            center.y + unitTextSize * 1.5f,
//            textPaint
//        )
//    }
//}

@Composable
fun WindCompass(
    modifier: Modifier = Modifier,
    degree: Float,
    speed: Int,
    circleColor: Color
) {
    Canvas(modifier = modifier) {
        val size = this.size
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = min(size.width, size.height) / 2.0f
        val innerRadius = outerRadius / 2.5f // Raio do círculo central
        val textPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
            isFakeBoldText = true
        }

        val directions = listOf("N", "L", "S", "O")
        val angles = listOf(0f, 90f, 180f, 270f)

        // Tamanhos proporcionais
        val cardinalTextSize = size.minDimension / 15
        val speedTextSize = size.minDimension / 7
        val unitTextSize = size.minDimension / 11
        textPaint.textSize = cardinalTextSize

        // Desenhar o círculo central
        drawCircle(
            color = circleColor,
            center = center,
            radius = innerRadius
        )

        fun canDrawLine(i: Int): Boolean {
            return (i != 0) && (i != 1) && (i != 22) && (i != 23) && (i !in 44..46) &&
                    (i != 67) && (i != 68) && (i != 89) && (i != 89) && (i != 89)
        }

        // Desenhar os traços da bússola (90 pontos)
        for (i in 0 until 90) {
            val angle = Math.toRadians(i * 4.0) // 4° increments
            val start = Offset(
                center.x + (outerRadius - size.minDimension / 20) * cos(angle).toFloat(),
                center.y - (outerRadius - size.minDimension / 20) * sin(angle).toFloat()
            )
            val end = Offset(
                center.x + outerRadius * cos(angle).toFloat(),
                center.y - outerRadius * sin(angle).toFloat()
            )

            // Remover traços onde estão as letras
            if (canDrawLine(i)) {
                drawLine(
                    color = if (i % 9 == 0)
                        Color.White.copy(alpha = 0.7f)
                    else
                        TransparentWhite,
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Desenhar as letras (N, L, S, O)
        for (i in directions.indices) {
            val angle = Math.toRadians(angles[i].toDouble())
            val x = center.x + (outerRadius - size.minDimension / 40) * cos(angle).toFloat()
            val y = center.y - (outerRadius - size.minDimension / 40) * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(
                directions[i],
                x,
                y + textPaint.textSize / 3,
                textPaint
            )
        }

        // Desenhar a flecha (dividida em duas partes)
        val pointerLength = outerRadius - size.minDimension / 15
        val arrowAngle = Math.toRadians(degree.toDouble())

        // Parte da frente da flecha
        val arrowHeadStart = Offset(
            center.x + (innerRadius + 5) * cos(arrowAngle).toFloat(),
            center.y - (innerRadius + 5) * sin(arrowAngle).toFloat()
        )
        val arrowHeadEnd = Offset(
            center.x + pointerLength * cos(arrowAngle).toFloat(),
            center.y - pointerLength * sin(arrowAngle).toFloat()
        )
        drawLine(
            color = Color.White,
            start = arrowHeadStart,
            end = arrowHeadEnd,
            strokeWidth = size.minDimension / 40,
            cap = StrokeCap.Square
        )

        // Parte de trás da flecha
        val arrowTailStart = Offset(
            center.x - (innerRadius + 5) * cos(arrowAngle).toFloat(),
            center.y + (innerRadius + 5) * sin(arrowAngle).toFloat()
        )
        val arrowTailEnd = Offset(
            center.x - (pointerLength - 10) * cos(arrowAngle).toFloat(),
            center.y + (pointerLength - 10) * sin(arrowAngle).toFloat()
        )
        drawLine(
            color = Color.White,
            start = arrowTailStart,
            end = arrowTailEnd,
            strokeWidth = size.minDimension / 40,
            cap = StrokeCap.Square
        )

        drawCircle(
            color = Color.White,
            radius = 5.dp.toPx(),
            center = arrowTailEnd
        )

        // Texto de velocidade
        textPaint.textSize = speedTextSize
        drawContext.canvas.nativeCanvas.drawText(
            "$speed",
            center.x,
            center.y - speedTextSize / 10f,
            textPaint
        )

        // Texto de unidade (km/h)
        textPaint.textSize = unitTextSize
        drawContext.canvas.nativeCanvas.drawText(
            "km/h",
            center.x,
            center.y + unitTextSize / 1.2f,
            textPaint.apply { isFakeBoldText = false }
        )
    }
}




//@Composable
//fun WindCompass(
//    degree: Float,
//    speed: Int,
//    modifier: Modifier = Modifier,
//    circleColor: Color
//) {
//    Canvas(modifier = modifier) {
//        val size = this.size
//        val center = Offset(size.width / 2, size.height / 2)
//        val radius = min(size.width, size.height) / 2.0f // Ajusta para caber dentro do Canvas
//
//        val textPaint = Paint().asFrameworkPaint().apply {
//            isAntiAlias = true
//            textAlign = android.graphics.Paint.Align.CENTER
//            color = android.graphics.Color.WHITE
//            isFakeBoldText = true
//        }
//
//        val directions = listOf("N", "L", "S", "O")
//        val angles = listOf(0f, 90f, 180f, 270f)
//
//        // Tamanho proporcional para texto
//        val cardinalTextSize = size.minDimension / 15
//        val speedTextSize = size.minDimension / 8
//        val unitTextSize = size.minDimension / 10
//
//        textPaint.textSize = cardinalTextSize
//
//        fun canDrawLine(i: Int): Boolean {
//            return (i != 0) && (i != 1) && (i != 22) && (i != 23) && (i !in 44..46) &&
//                    (i != 67) && (i != 68) && (i != 89) && (i != 89) && (i != 89)
//        }
//
//        // Draw clock-like ticks (90 points)
//        for (i in 0 until 90) {
//            val angle = Math.toRadians(i * 4.0) // 4° increments for 90 points
//            val start = Offset(
//                center.x + (radius - size.minDimension / 20) * cos(angle).toFloat(),
//                center.y - (radius - size.minDimension / 20) * sin(angle).toFloat()
//            )
//            val end = Offset(
//                center.x + radius * cos(angle).toFloat(),
//                center.y - radius * sin(angle).toFloat()
//            )
//            if (canDrawLine(i)) {
//                drawLine(
//                    color = if (i % 9 == 0)
//                        Color.White.copy(alpha = 0.7f)
//                    else
//                        TransparentWhite, // Major ticks every 10 points
//                    start = start,
//                    end = end,
//                    strokeWidth = 1.dp.toPx()
//                )
//            }
//        }
//
//        // Draw direction letters
//        for (i in directions.indices) {
//            val angle = Math.toRadians(angles[i].toDouble())
//            val x = center.x + (radius - size.minDimension / 40) * cos(angle).toFloat()
//            val y = center.y - (radius - size.minDimension / 40) * sin(angle).toFloat()
//            drawContext.canvas.nativeCanvas.drawText(
//                directions[i],
//                x,
//                y + textPaint.textSize / 3, // Adjust for baseline
//                textPaint
//            )
//        }
//
//        // Draw arrow (front and back parts)
//        val pointerLength = radius - size.minDimension / 15
//        val arrowAngle = Math.toRadians(degree.toDouble())
//        val arrowHead = Offset(
//            center.x + pointerLength * cos(arrowAngle).toFloat(),
//            center.y - pointerLength * sin(arrowAngle).toFloat()
//        )
//        val arrowTail = Offset(
//            center.x - pointerLength * cos(arrowAngle).toFloat(),
//            center.y + pointerLength * sin(arrowAngle).toFloat()
//        )
//
//        drawLine(
//            color = Color.White,
//            start = arrowTail,
//            end = arrowHead,
//            strokeWidth = size.minDimension / 40,
//            cap = StrokeCap.Round
//        )
//
//        // Draw arrow head (triangle)
//        val arrowHeadSize = size.minDimension / 15
//        val arrowHeadLeft = Offset(
//            arrowHead.x - arrowHeadSize * sin(arrowAngle).toFloat(),
//            arrowHead.y - arrowHeadSize * cos(arrowAngle).toFloat()
//        )
//        val arrowHeadRight = Offset(
//            arrowHead.x + arrowHeadSize * sin(arrowAngle).toFloat(),
//            arrowHead.y + arrowHeadSize * cos(arrowAngle).toFloat()
//        )
//        drawPath(
//            path = Path().apply {
//                moveTo(arrowHead.x, arrowHead.y)
//                lineTo(arrowHeadLeft.x, arrowHeadLeft.y)
//                lineTo(arrowHeadRight.x, arrowHeadRight.y)
//                close()
//            },
//            color = Color.Red
//        )
//
//        drawCircle(
//            color = circleColor,
//            radius = radius / 2,
//            center
//        )
//
//        // Draw speed text
//        textPaint.textSize = speedTextSize
//        drawContext.canvas.nativeCanvas.drawText(
//            "$speed",
//            center.x,
//            center.y - speedTextSize / 10,
//            textPaint
//        )
//
//        // Draw unit text
//        textPaint.textSize = unitTextSize
//        drawContext.canvas.nativeCanvas.drawText(
//            "km/h",
//            center.x,
//            center.y + unitTextSize / 1.2f,
//            textPaint.apply {
//                isFakeBoldText = false
//            }
//        )
//    }
//}

@Preview
@Composable
fun WindInfoPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //WindCompass( 93f, 12, modifier = Modifier.fillMaxWidth())
        WindInfo(TopCloudySky, Wind(7.0, 147, 18.0))
    }
}