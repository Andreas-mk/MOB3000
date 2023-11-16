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
import androidx.compose.ui.unit.dp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mob.smilefjesapp.dataklasse.ApiResponse
import mob.smilefjesapp.dataklasse.RestaurantInfo
import mob.smilefjesapp.nettverk.RestaurantApi
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.Integer.parseInt

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
                    val tekstSøk = intent.getStringExtra("navn")
                    // Henter skjermstørrelsen. Skjermens bredde avgjør hvor mange kort vi viser pr rad
                    val windowSizeClass = calculateWindowSizeClass(this)
                    val vinduBredde = windowSizeClass.widthSizeClass
                    RestaurantInfo(Modifier, vinduBredde, valgtKommune, tekstSøk) // Bygger UI
                    // gjør likt som med valgtKommune, bare sende med stringen bruker skriver inn i søkefelt i stedet
                    // gjør en if-sjekk på om valgtKommune eller søkefelt er null -> bygg gui etter svar på dette
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition") // Får ikke launchet korutine uten denne (generert av Android Studio)
@OptIn(ExperimentalMaterial3Api::class) // ToppAppBar
@Composable
fun RestaurantInfo(modifier: Modifier = Modifier, vinduBredde: WindowWidthSizeClass, valgtKommune: String?, tekstSøk: String?){
    val coroutineScope = rememberCoroutineScope()
    var restaurantListe by rememberSaveable { // Saveable for at lista skal overleve skjermrotasjon MEN skjermen blir svart?
        mutableStateOf(emptyList<RestaurantInfo>()) // Test dette med andre maskiner?
    }
    // Starter en korutine som henter restauranter fra Mattilsynets API
    coroutineScope.launch(Dispatchers.IO) {
        val nyListe = hentRestauranter(valgtKommune, tekstSøk)
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

                    // Forteller bruker at søket ga 0 resultater ELLER loading??
                    if(restaurantListe.isEmpty()){
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                        // Animert loading-ikon som vises mens restauranter hentes (og kort bygges)
                        // KILDE: https://developer.android.com/jetpack/compose/components/progress
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        /*
                        Text(text = "", // Denne kan vises også før kortene lages når vi får svar
                           style = MaterialTheme.typography.headlineMedium)
                         */
                    } else {
                        // Lager et kort for hvert element i lista
                        LagKort(restaurantListe)
                    }
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
                    // 2 kort ved siden av hverandre i hver sin kolonne.
                    // Vi deler lista med alle restauranter i to slik at vi kan bruke LagKort funksjonen.
                    // Sublist KILDE: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/sub-list.html
                    val midten = restaurantListe.size/2
                    val delListe1: List<RestaurantInfo> = restaurantListe.subList(0, midten)
                    val delListe2: List<RestaurantInfo> = restaurantListe.subList(midten, restaurantListe.size)

                    FlereKolonner(delListe1, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                    FlereKolonner(delListe2, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                }
            }
            WindowWidthSizeClass.Expanded -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Samme som WindowWidthSizeClass.Medium, men her lager vi 3 kolonner for å bedre utnytte skjermen
                    val førsteSkille = restaurantListe.size/3
                    val andreSkille = (restaurantListe.size/3) * 2
                    //val tredjeSkille = førsteSkille + andreSkille
                    val delListe1: List<RestaurantInfo> = restaurantListe.subList(0, førsteSkille)
                    val delListe2: List<RestaurantInfo> = restaurantListe.subList(førsteSkille, andreSkille)
                    val delListe3: List<RestaurantInfo> = restaurantListe.subList(andreSkille, restaurantListe.size)
                    //val delListe4: List<RestaurantInfo> = restaurantListe.subList(tredjeSkille, restaurantListe.size)

                    FlereKolonner(delListe1, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                    FlereKolonner(delListe2, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                    FlereKolonner(delListe3, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                }
            }
        }
    }
}

/**
 * Oppretter en ny kolonne og fyller den med restauranter.
 * Brukes kun for å utnytte bred skjerm slik at bruker kan se flere restauranter ved siden av hverandre
 */
@Composable
fun FlereKolonner(delListe: List<RestaurantInfo>, modifier: Modifier){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        LagKort(delListe)
    }
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
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        )
        {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                // Kolonner med Restaurant-navn, beliggenhet og smilefjesranking
                Column (
                    modifier = modifier.width(300.dp)
                ){
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
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
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
        onClick = { /* expanded = !expanded */ },
        modifier = modifier,
        enabled = false // Bruker kan trykke hvor som helst på kortet for å utvide
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded) "Lukk utvidet kort" else "Utvid kort",
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
        }
        Text(
            text = "$tema1: $karakter1",
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema2: $karakter2",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema3: $karakter3",
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$tema4: $karakter4",
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
suspend fun hentRestauranter(valgtKommune: String?, tekstSøk: String?): List<RestaurantInfo>{
    try {
        val svar: Response<ApiResponse>
        var alleRestauranter: MutableList<RestaurantInfo> = mutableListOf() // bruker denne til å filtrere ut gamle tilsyn

        if(valgtKommune == null && tekstSøk == null){
            return emptyList() // Bruker har trykket søk på restaurant uten å skrive inn restaurant
            // FUNKER IKKE, må utbedres
        }

        // I appen er det to muligheter for å se restauranter. Hvis det ikke er den ene så er det den andre
        if(tekstSøk==null){
            // !! = not null assertion operator :
            svar = RestaurantApi.retrofitService.hentRestauranter(valgtKommune!!,1) // Vi henter alltid page 1 fra APIet først
        }
        else {
            svar = RestaurantApi.retrofitService.hentMedSøk(tekstSøk)
        }
        if (svar.isSuccessful) {
            val headers = svar.headers()
            val totalPages = headers["X-Datahotel-Total-Pages"]?.toInt() // Henter antall sider fra API headers (Pagination), parse til Int slik at vi kan bruke tallet i en løkke
            val totalPosts = headers["X-Datahotel-Total-Posts"] // "Ditt søk ga $totaltPosts resultater" mens man venter på kortene?
            val apiSvar = svar.body()
            //Log.d("Svarsjekk", "Svar isSuccessful")
            if (apiSvar != null) {
                Log.d("Antall svar", "Antall restauranter: $totalPosts")
                Log.d("HEADERS", " $totalPages")
                val nyListe = behandleSvar(apiSvar, alleRestauranter)
                alleRestauranter = nyListe

                // Paginering (?) Dersom det er flere enn 1 side i APIet må vi hente resten av sidene
                if(totalPages != null && totalPages > 1){
                    for(i in 2..totalPages){ // starter på 2 siden vi allerede har hentet page 1
                        val nestePage = RestaurantApi.retrofitService.hentRestauranter(valgtKommune!!,i)
                        if(nestePage.isSuccessful){
                            val pageSvar = nestePage.body()
                            if(pageSvar != null){
                                val nyListe2 = behandleSvar(pageSvar, alleRestauranter)
                                alleRestauranter = nyListe2
                            }
                        }
                    }
                }
                return alleRestauranter // Returnerer lista med restauranter
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

fun behandleSvar(apiSvar: ApiResponse, liste: MutableList<RestaurantInfo>): MutableList<RestaurantInfo>{
    var teller = 0
    for (restaurant in apiSvar.entries) {
        // Mulig vi kan lage en SimpleDateFormat? https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat
        // format på dato fra API: "ddmmyyyy"
        val år: Int = parseInt(restaurant.dato.substring(4))
        val måned: Int = parseInt(restaurant.dato.substring(2,4))
        val dag: Int = parseInt(restaurant.dato.substring(0,2))
        var nyttTilsyn = false

        // If-sjekkene finner ut dette restaurant-tilsynet er det nyeste
        // (Denne ser grusom ut, kan den forbedres????)
        if(!liste.any{it.orgnummer == restaurant.orgnummer}){
            nyttTilsyn = true
            liste.add(restaurant) // hvis ikke orgnummer finnes i lista legges restauranten inn uansett
        }else if(liste.any {
                it.orgnummer == restaurant.orgnummer
                && parseInt(it.dato.substring(4)) < år }
            ){
            nyttTilsyn = true
        }else if (liste.any {
                it.orgnummer == restaurant.orgnummer
                && parseInt(it.dato.substring(4)) == år
                && parseInt(it.dato.substring(2,4)) < måned }
            ){
            nyttTilsyn = true
        }else if (liste.any {
                it.orgnummer == restaurant.orgnummer
                && parseInt(it.dato.substring(4)) == år
                && parseInt(it.dato.substring(2,4)) == måned
                && parseInt(restaurant.dato.substring(0,2)) < dag }
            ){
            nyttTilsyn = true
        }
        // Fjerner et element fra lista dersom orgnummeret finnes fra før og datosjekken har slått til
        liste.removeIf{
           it.orgnummer == restaurant.orgnummer && nyttTilsyn
        }
        if(!nyttTilsyn) {
            //liste.removeIf { it.orgnummer == restaurant.orgnummer }
            Log.d("False", "Ikke nytt ${restaurant.dato}")
        }
        if(nyttTilsyn) {
            liste.add(restaurant)
            Log.d("ADD", "Addet orgnr ${restaurant.orgnummer} Dato ${restaurant.dato}")
        }
        // Kun for testing, kan fjernes
        val verdi = liste.any{it.orgnummer == restaurant.orgnummer && nyttTilsyn}
        if(verdi){
            teller++
            Log.d("????????", "Fant 1 tilfelle av samme orgnr ${restaurant.orgnummer} og DATO: ${restaurant.dato} TELLER: $teller")
            //nyListe.remove
        }
    }
    return liste
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
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview(){
    SmilefjesappTheme {
        InfoCard(
            "Sjøormen Kro",
            "3840",
            "Seljord",
            "Ingrid Slettens veg 20",
            "1",
            "Rutiner og ledelse",
            "1",
            "Lokaler og utstyr",
            "2",
            "Mat-håndtering og tilberedning",
            "0",
            "Merking og sporbarhet",
            "1")
        }
}
/*
// Får ikke vist restaurant-kortene

@Preview(showBackground = true, widthDp = 750)
@Composable
fun RestaurantInfoMediumPreview(){
    SmilefjesappTheme {
        RestaurantInfo(Modifier, vinduBredde = WindowWidthSizeClass.Medium, "Fredrikstad" , null)
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun RestaurantInfoExpandedPreview(){
    SmilefjesappTheme {
        RestaurantInfo(Modifier, vinduBredde = WindowWidthSizeClass.Expanded, "Fredrikstad" , null)
    }
}

 */