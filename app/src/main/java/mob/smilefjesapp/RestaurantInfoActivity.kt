package mob.smilefjesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme

class RestaurantInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantInfo()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfo(modifier: Modifier = Modifier){
Scaffold (topBar = {TopAppBarInfoCard()}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            InfoCard("Nellys Kebab", "1601", "Fredrikstad")
            InfoCard("Stasjonsbua", "3801", "Bø i Telemark")
            InfoCard("Aasmundsen Bakeri", "3800", "Bø i Telemark")
        }
    }
}
@Composable
fun InfoCard(navn : String, postnr : String, stedsnavn : String,/*context: Context,*/ modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard(
        modifier= modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(
                true,
                onClick = {
                    expanded = !expanded
                    /* Gjør kortet større, vis MerInfo*/

                }
            )
    ) {
       // var expanded by remember { mutableStateOf(false) }
        Column(
            modifier= modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
        )
        {

            Row {
                // Kolonner med Restaurant-navn, beliggenhet og smilefjesranking
                Column {
                    Text(
                        text = navn,
                        modifier = Modifier.padding(5.dp),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "$postnr, $stedsnavn",
                        modifier = Modifier.padding(5.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Column (
                    //modifier = Modifier,
                    //horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Image(
                        painter = painterResource(id = R.drawable.grnn),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                            .padding(10.dp),

                        contentDescription = ("Grønn Smilefjes")
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                horizontalArrangement = Arrangement.Center
            ){
                UtvidButton(
                    expanded = expanded,
                    //onClick = { expanded = !expanded }
                )
            }
            if (expanded) {
                UtvidInfo(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun UtvidButton(
    expanded: Boolean,
    //onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = { /*???*/ },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription =
            if (expanded) "Lukk" else "Utvid",
            tint = MaterialTheme.colorScheme.tertiary
        )

    }
}

@Composable
fun UtvidInfo(
    //restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    var favoritt by remember { mutableStateOf(false) } // HUSK ENDRE PÅ DENNE
    /*
    * Vis favorittside? I burgermeny?
    * */
    Column(
        modifier= Modifier
            .padding(5.dp)
            .fillMaxWidth()
    )
    {
        Row {
            Text(
                text = "Storgata 13.",
                modifier = Modifier.padding(5.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Favoritt",
                modifier = Modifier.padding(5.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = if (favoritt) Icons.Filled.Star else Icons.Outlined.Star, /* Få tak i favoritt fra database?*/
                contentDescription = "Favoritt-ikon"
            )

        }

        Text(
            text = "Rutiner og ledelse: 1"/*${restaurant.rating.toString()}*/,
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Lokaler og utstyr: 0",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )


        Text(
            text = "Mat-håndtering og tilberedning: 1",
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Merking og sporbarhet: 2",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarInfoCard(modifier: Modifier = Modifier){
    val localContext = LocalContext.current
    // Top App Bar for enkel oversikt og navigasjon
    CenterAlignedTopAppBar(
        title = {
            Text( text = "Restauranter",
                modifier = Modifier.fillMaxWidth(),
                style=MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
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