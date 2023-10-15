package mob.smilefjesapp

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
                    KommuneSiden(kommuneTabell)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KommuneSiden(kommuneTabell: List<String>, modifier: Modifier = Modifier){
    // Fra powerpoint
    // Lager layoutet på kommunesiden
    Scaffold (topBar = {TopAppBarKommune()}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
                //.background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            KommuneListe(kommuneTabell)
        }
    }
}

@Composable
fun KommuneListe(kommuneTabell: List<String>, modifier: Modifier = Modifier) {

    // HENTET FRA POWERPOINT

    // Lager liste med LazyColumn() og en iterator.
    // LazyColumn lager nye Compose-elementer i listen etterhvert som de skal vises når brukeren ruller i listen
    LazyColumn(
        modifier = modifier
    ) {
        // Iterator som gjennomløper hele fylkeslisten.
        items(kommuneTabell) { kommune ->  // Identifikatoren fylke peker på hvert av elementene i listen.
            Text(
                text = kommune,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable(
                        enabled = true,
                        onClick = {
                            /* Sende videre til spisesteder*/
                        }
                    )
                    .fillMaxWidth(),
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            )
            Divider(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarKommune(modifier: Modifier = Modifier){
    val localContext = LocalContext.current
    // Top App Bar for enkel oversikt og navigasjon
    CenterAlignedTopAppBar(
        title = {
            Text( text = "Kommuner",
                modifier = Modifier.fillMaxWidth(),
                style=MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        // Tilbakeknapp som sender bruker tilbake til forrige activity
        navigationIcon = {
            IconButton(onClick = { localContext.startActivity(Intent(localContext, MainActivity::class.java)) }) {
                Icon(imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Tilbake"
                ) }
        },
        // Søkeknapp lar brukeren raskt søke blant kommunene
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(imageVector = Icons.Filled.Search,
                    contentDescription = "Søk"
                ) }
        },
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
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