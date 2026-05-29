package com.sunday.pokemontest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunday.pokemontest.R
import com.sunday.pokemontest.ui.theme.PokemonBlue

@Composable
fun PokemonItemCard(
    title: String,
    subtitle:String,
    captureRate: String,
    data: List<String>,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = PokemonBlue,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                if (captureRate.isNotBlank()) {
                    Text(
                        text = "Capture Rate: $captureRate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokemonBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            // Pokémon chips
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
            SpeciesRows(data)
        }
    }
}


@Composable
private fun SpeciesRows(names: List<String>) {
    val chunks = names.chunked(3)
    chunks.forEach { row ->
        Row(
            modifier = Modifier.padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            row.forEach { name ->
                PokemonChunk(name)
            }
        }
    }
}

@Composable
private fun PokemonChunk(name: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PokemonBlue.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            color = PokemonBlue,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}