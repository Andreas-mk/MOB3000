package mob.smilefjesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme

class KommuneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val kommuneTabell  = resources.getStringArray(R.array.kommuner).asList()
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KommuneListe(kommuneTabell)
                }
            }
        }
    }
}

@Composable
fun KommuneListe(kommuneTabell: List<String>, modifier: Modifier = Modifier) {
    // Lager liste med LazyColumn() og en iterator.
    // LazyColumn lager nye Compose-elementer i listen etterhvert som de skal vises når brukeren ruller i listen
    LazyColumn(
        modifier = modifier
    ) {
        // Iterator som gjennomløper hele fylkeslisten.
        items(kommuneTabell) { kommune ->  // Identifikatoren fylke peker på hvert av elementene i listen.

            val lokalContext = LocalContext.current

            Text(
                text = kommune,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable(
                        enabled=true,
                        onClick = {
                            //visFylkeWeb(lokalContext, fylke)
                            // Vis spisestedet
                        }
                    )
                    .fillMaxWidth(),
                color = Color.Blue,
                fontSize = 28.sp,
                maxLines = 1
            )

            Spacer(Modifier.fillMaxWidth().padding(2.dp))

            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp))

        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    SmilefjesappTheme {
        KommuneListe(kommuneTabell)
    }
}
*/