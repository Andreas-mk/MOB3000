package mob.smilefjesapp

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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

/**
 * Denne activityen håndterer UIet som vises når bruker skal få presentert smilefjestilsyn fra Mattilsynets API.
 */
class RestaurantInfoActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
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
    var restaurantListe by rememberSaveable { // Saveable for at lista skal overleve skjermrotasjon
        mutableStateOf(emptyList<RestaurantInfo>())
    }
    // Starter en korutine som henter restauranter fra Mattilsynets API
    coroutineScope.launch(Dispatchers.IO) {
        val nyListe = hentRestauranter(valgtKommune, tekstSøk)
        restaurantListe = nyListe
    }

    Scaffold (topBar = {TopAppBarInfoCard()}
        ) {
        /*
            Hva brukeren ser kommer an
        */
        when (vinduBredde) {
            WindowWidthSizeClass.Compact -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,

                )
                {
                    // Loading-ikon
                    if(restaurantListe.isEmpty()){
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                        // Animert loading-ikon som vises mens restauranter hentes
                        // KILDE: https://developer.android.com/jetpack/compose/components/progress
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        // Mutable Text som viser antall restauranter bruker kan se.
                        // Vi får antall restauranter som en header i Api response og skulle gjerne vist dette
                        // til bruker mens appen laster, men vi får ikke oppdatert denne verdien med headeren fra APIet.
                        val antRestauranterTekst by remember {mutableStateOf("")}
                        Text(
                            text = antRestauranterTekst,
                            style = MaterialTheme.typography.headlineMedium
                        )
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

                    if(restaurantListe.isEmpty()){
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        FlereKolonner(
                            delListe1, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        FlereKolonner(
                            delListe2, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
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

                    val delListe1: List<RestaurantInfo> = restaurantListe.subList(0, førsteSkille)
                    val delListe2: List<RestaurantInfo> = restaurantListe.subList(førsteSkille, andreSkille)
                    val delListe3: List<RestaurantInfo> = restaurantListe.subList(andreSkille, restaurantListe.size)

                    if(restaurantListe.isEmpty()){
                        CircularProgressIndicator(
                            modifier = Modifier.width(90.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        FlereKolonner(
                            delListe1, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        FlereKolonner(
                            delListe2, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        FlereKolonner(
                            delListe3, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
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
                    modifier = modifier.width(250.dp)
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
                            contentDescription = ("Gult smilefjes, karakter 2")
                        )
                    else if(totalKarakter == "3")
                        Image(
                            painter = painterResource(id = R.drawable.rd),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(10.dp),
                            contentDescription = ("Rødt smilefjes, karakter 3")
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
                    expanded = expanded
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
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = { /* expanded = !expanded */ },
        modifier = modifier,
        enabled = false // Bruker kan trykke hvor som helst på kortet for å utvide, utenom på selve symbolet.
                        // Trykker man på symbolet skjer det ingenting.
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
 * Henter en liste med restauranter basert på fylke/kommune eller søk)
 * og returnerer denne lista hvis vi har fått tak i data, hvis ikke returneres en tom liste
 */
suspend fun hentRestauranter(valgtKommune: String?, tekstSøk: String?): List<RestaurantInfo>{
    try {
        val svar: Response<ApiResponse>
        var alleRestauranter: MutableList<RestaurantInfo> = mutableListOf() // bruker denne til å filtrere ut gamle tilsyn

        if(valgtKommune == null && tekstSøk == ""){
            // Bruker har trykket søk på restaurant uten å skrive inn restaurant.
            // Sender man med en tom string i API-kallet vil vi få 44602 (i nov 2023) restaurant-objekter som svar
            // Det klarer ikke appen vår å håndtere, dessuten ville det vært veldig tungvindt å scrolle
            // gjennom 44602 restaurant-kort for å finne den man vil se.
            return emptyList()
        }
        // I appen er det to muligheter for å se restauranter. Hvis det ikke er den ene så er det den andre
        else if(tekstSøk==null){
            // !! = not null assertion operator :
            if (valgtKommune != "Midt-Telemark")
                svar = RestaurantApi.retrofitService.hentRestauranter(valgtKommune!!,1) // Vi henter alltid page 1 fra APIet først
            else
                svar = RestaurantApi.retrofitService.hentRestauranter("Bø i telemark",1)
        }
        else {
            // Må endre noe MIDT-TELEMARK i hent fra søk
            if (valgtKommune != "Midt-Telemark")
                svar = RestaurantApi.retrofitService.hentMedSøk(tekstSøk,1)
            else
                svar = RestaurantApi.retrofitService.hentMedSøk("Bø i telemark",1)
        }
        if (svar.isSuccessful) {
            val headers = svar.headers()
            val totalPages = headers["X-Datahotel-Total-Pages"]?.toInt() // Henter antall sider fra API headers (Pagination), parse til Int slik at vi kan bruke tallet i en løkke
            val totalPosts = headers["X-Datahotel-Total-Posts"]?.toInt() // "Ditt søk ga $totaltPosts resultater" mens man venter på kortene
            val apiSvar = svar.body()
            if (apiSvar != null) {
                if(totalPosts == 0){ // Ingen restauranter funnet
                    //antRestauranterTekst = "Restauranter funnet: $totalPosts"
                    Log.d("0 svar", "0 svar")
                }
                Log.d("Header Posts", "Antall restauranter: $totalPosts")
                Log.d("Header Pages", "Antall sider: $totalPages")
                val nyListe = behandleSvar(apiSvar, alleRestauranter)
                alleRestauranter = nyListe

                // Paginering. Dersom det er flere enn 1 side i APIet må vi hente resten av sidene
                if(totalPages != null && totalPages > 1){
                    for(i in 2..totalPages){ // starter på 2 siden vi allerede har hentet page 1
                        var nestePage: Response<ApiResponse>
                        if(tekstSøk==null){
                            nestePage = RestaurantApi.retrofitService.hentRestauranter(valgtKommune!!,i) // Vi henter alltid page 1 fra APIet først
                        }else {
                            nestePage = RestaurantApi.retrofitService.hentMedSøk(tekstSøk, i)
                        }
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
        // format på dato fra API: "ddmmyyyy"
        val år: Int = parseInt(restaurant.dato.substring(4))
        val måned: Int = parseInt(restaurant.dato.substring(2,4))
        val dag: Int = parseInt(restaurant.dato.substring(0,2))
        var nyttTilsyn = false

        // If-sjekkene finner ut dette restaurant-tilsynet er det nyeste
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
        if(!nyttTilsyn) { // Kun for testing
            //Log.d("!nyttTilsyn = False", "Ikke nytt tilsyn ${restaurant.dato}")
        }
        if(nyttTilsyn) {
            liste.add(restaurant)
            //Log.d("ADD", "Addet orgnr ${restaurant.orgnummer} med dato ${restaurant.dato}")
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
        navigationIcon = { // Avslutter aktivitet når bruker trykker på tilbakeknapp
            IconButton(onClick = {(localContext as? ComponentActivity)?.finish()}) {
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