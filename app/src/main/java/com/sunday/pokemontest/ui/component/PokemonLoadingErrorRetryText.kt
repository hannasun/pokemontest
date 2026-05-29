package com.sunday.pokemontest.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sunday.pokemontest.R
import com.sunday.pokemontest.ui.theme.PokemonYellow

@Composable
fun PokemonLoadingErrorText(error: String, modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.loading_error_text, error),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(
                text = stringResource(R.string.loading_error_retry),
                color = PokemonYellow,
                textAlign = TextAlign.Center
            )
        }
    }

}
