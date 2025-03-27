package br.com.mdr.weather.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.mdr.weather.R
import br.com.mdr.weather.presentation.ui.theme.EXTRA_SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.MEDIUM_PADDING
import br.com.mdr.weather.presentation.ui.theme.SMALL_PADDING
import br.com.mdr.weather.presentation.ui.theme.TopDaySkyBlue
import br.com.mdr.weather.presentation.ui.theme.TransparentWhite

@Composable
fun WeatherInfoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: Int,
    title: String,
    info: String,
    description: String
) {
    BlurCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = MEDIUM_PADDING, vertical = SMALL_PADDING)
                .height(160.dp)
        ) {
            val (iconImage, titleLabel, infoLabel, descriptionLabel) = createRefs()

            Icon(
                modifier = Modifier
                    .constrainAs(iconImage) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                painter = painterResource(id = icon),
                contentDescription = "",
                tint = TransparentWhite
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = EXTRA_SMALL_PADDING)
                    .constrainAs(titleLabel) {
                        bottom.linkTo(iconImage.bottom)
                        start.linkTo(iconImage.end)
                        top.linkTo(iconImage.top)
                    },
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = TransparentWhite
            )
            Text(
                modifier = Modifier
                    .padding(top = SMALL_PADDING)
                    .constrainAs(infoLabel) {
                        start.linkTo(parent.start)
                        top.linkTo(iconImage.bottom)
                    },
                text = info.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
            Text(
                modifier = Modifier
                    .constrainAs(descriptionLabel) {
                        bottom.linkTo(parent.bottom)
                    },
                text = description,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MEDIUM_PADDING),
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
        ) {
            WeatherInfoCard(
                Modifier.weight(1f, true),
                TopDaySkyBlue,
                R.drawable.ic_visibility,
                "Visibilidade",
                "23 KM",
                "Visibilidade excelente."
            )
            WeatherInfoCard(
                Modifier.weight(1f, true),
                TopDaySkyBlue,
                R.drawable.ic_thermometer,
                "Sensação",
                "23º",
                "Similar à temperatura real."
            )
        }
        WeatherInfoCard(
            Modifier,
            TopDaySkyBlue,
            R.drawable.ic_thermometer,
            "Sensação",
            "25º",
            "Similar à temperatura real."
        )
    }
}