package mob.smilefjesapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mob.smilefjesapp.dataklasse.RestaurantInfo
import mob.smilefjesapp.nettverk.RestaurantApi
import retrofit2.HttpException
import java.io.IOException

class RestaurantInfoActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val valgtKommune = intent.getStringExtra("valgtKommune")
                    // Henter skjermstørrelsen. Skjermens bredde avgjør hvor mange kort vi viser pr rad
                    val windowSizeClass = calculateWindowSizeClass(this)
                    RestaurantInfo(Modifier, windowSizeClass, valgtKommune) // Bygger UI
                }
            }
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition") // Får ikke launchet korutine uten denne (generert av Android Studio)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfo(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass, valgtKommune: String?){
    val vinduBredde = windowSizeClass.widthSizeClass
    val coroutineScope = rememberCoroutineScope()
    var restaurantListe by remember {
        mutableStateOf(emptyList<RestaurantInfo>())
    }
    // Starter en korutine som henter restauranter fra Mattilsynets API
    coroutineScope.launch(Dispatchers.IO) {
        val nyListe =
            hentRestauranter(valgtKommune) // Denne tar en stund, vi burde vise et loading ikon eller lignende mens skjermen er tom
        restaurantListe = nyListe
    }

    Scaffold (topBar = {TopAppBarInfoCard()}
        ) {
        when (vinduBredde) {
            WindowWidthSizeClass.Compact -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    // Lager et kort for hvert element i lista
                    LagKort(restaurantListe)
                }
            }
            WindowWidthSizeClass.Medium -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(2) { // 2 kort ved siden av hverandre
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            LagKort(restaurantListe)
                        }
                    }
                }
            }
            WindowWidthSizeClass.Expanded -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) {// 4 kort ved siden av hverandre
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            LagKort(restaurantListe)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Oppretter et kort for en restaurant
 */
@Composable
fun InfoCard(navn: String,
             postnr: String,
             stedsnavn: String,
             adrlinje1: String,
             totalKarakter: String,
             tema1: String,
             karakter1: String,
             tema2: String,
             karakter2: String,
             tema3: String,
             karakter3: String,
             tema4: String,
             karakter4: String,
             modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier= modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(
                true,
                onClick = {
                    expanded = !expanded
                }
            )
    ) {
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
                    // Setter farge på smilefjeset (basert på totalkarakter)
                    if(totalKarakter == "0" || totalKarakter == "1")
                        Image(
                            painter = painterResource(id = R.drawable.grnn),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(10.dp),
                            contentDescription = ("Grønt smilefjes, karakter 0 eller 1")
                        )
                    else if(totalKarakter == "2")
                        Image(
                            painter = painterResource(id = R.drawable.gul),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(10.dp),
                            contentDescription = ("Gult smilefjes, karakter 1")
                        )
                    else if(totalKarakter == "3")
                        Image(
                            painter = painterResource(id = R.drawable.rd),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(10.dp),
                            contentDescription = ("Rødt smilefjes, karakter 2")
                        )
                    // Totalkarakter 4 eller 5 er ikke aktuelt / ikke vurdert, derfor får de ikke smilefjes
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
            // Utvider kortet og viser detaljert informasjon
            if (expanded) {
                UtvidInfo(
                    adrlinje1,
                    tema1,
                    karakter1,
                    tema2,
                    karakter2,
                    tema3,
                    karakter3,
                    tema4,
                    karakter4,
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

/**
 * I hovedsak kun for at brukeren skal forstå at man kan trykke på kortene
 */
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

/**
 * Detaljert info om hvert spisested
 */
@Composable
fun UtvidInfo(
    adrlinje1: String,
    tema1: String,
    karakter1: String,
    tema2: String,
    karakter2: String,
    tema3: String,
    karakter3: String,
    tema4: String,
    karakter4: String,
    modifier: Modifier = Modifier
) {
    var favoritt by remember { mutableStateOf(false) } // Husk å endre på denne?
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
                text = adrlinje1,
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
            text = "$tema1 $karakter1",
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema2 $karakter2",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema3 $karakter3",
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema4 $karakter4",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Setter i gang API-kallet, sjekker at vi får noe data og printer dataen i logcat
 * Henter en liste med restauranter (**basert på fylke/kommune/søk, vi må sende med noen parametre her etterhvert**)
 * og returnerer denne lista hvis vi har fått tak i data, hvis ikke returneres en tom liste
 */
suspend fun hentRestauranter(valgtKommune: String? = null): List<RestaurantInfo>{

    try {
        // !! = not null assertion operator :
        val svar = RestaurantApi.retrofitService.hentRestauranter(valgtKommune!!)
        if (svar.isSuccessful) {
            val apiSvar = svar.body()
            Log.d("Svarsjekk", "Svar isSuccessful")
            if (apiSvar != null) {
                Log.d("Antall svar", "Antall restauranter: ${apiSvar.entries.size}")
                for (restaurant in apiSvar.entries) { // Kun for å printe i logcat
                    Log.d("Svar", "Restaurantnavn: ${restaurant.navn}, Adresse: ${restaurant.adrlinje1}")
                }
                return apiSvar.entries // Returnerer lista med restauranter
            } else {
                Log.d("apiSvar", "apiSvar er null")
            }
        }else{
            Log.d("Error", "Svar fra API er tomt - ${svar.code()}")
        }
    } catch (e: IOException) {
        Log.d("IOException", "IOException :( $e")
    } catch (e: HttpException) {
        Log.d("HTTPException", "HTTPException :( $e")
    }
    return emptyList() // Ikke et suksessfullt API kall, tom liste returneres
}

/**
 * Lager et kort for hvert element i lista med restauranter vi henter fra API
 */
@Composable
fun LagKort(restaurantListe: List<RestaurantInfo>){
    for(restaurant in restaurantListe){
        InfoCard(restaurant.navn,
            restaurant.postnr,
            restaurant.poststed,
            restaurant.adrlinje1,
            restaurant.totalKarakter,
            restaurant.tema1,
            restaurant.karakter1,
            restaurant.tema2,
            restaurant.karakter2,
            restaurant.tema3,
            restaurant.karakter3,
            restaurant.tema4,
            restaurant.karakter4)
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