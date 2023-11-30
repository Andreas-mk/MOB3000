package mob.smilefjesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mob.smilefjesapp.dataklasse.FylkeInfo
import mob.smilefjesapp.dataklasse.KommuneInfo
import mob.smilefjesapp.dataklasse.getKommuneListeForFylke
import mob.smilefjesapp.nettverk.KommuneApiService
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Samme som FylkeActivity så er dette onCreate funksjonen og får inn masse deklarerte variabler
 */
class KommuneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * Variabler for å lage mutable objekt som kan brukes til api kall funksjon
                     * Lager også her fylekInfo objekt som er FylkeInfo dataklasse som får "intent.getParcelableExtra("fylkeInfo") som får data fra fylkeActivity
                     * Og lager en variabel som bruker funksjonen som er lagd i KommuneInfo som henter Kommunene inni riktig fylke
                     * Og kaller da Kommunesiden og som setter variabelen som henter kommunene inni riktig fylke
                     */
                    val kommuneInfoListe = remember { mutableStateOf(listOf<KommuneInfo>()) }
                    hentAlleKommuner(kommuneInfoListe)

                    // https://medium.com/the-lazy-coders-journal/easy-parcelable-in-kotlin-the-lazy-coders-way-9683122f4c00
                    val fylkeInfo: FylkeInfo? = intent.getParcelableExtra("fylkeInfo")
                    val allKommuneListe = getKommuneListeForFylke(kommuneInfoListe.value, fylkeInfo)

                    KommuneSiden(allKommuneListe)
                }
            }
        }
    }
}

/**
 * Funksjon som er helt lik som den i FylkeActivity som bruker BASE_URL til geonorge, bygger retrofit og lager api kall
 */
private fun hentAlleKommuner(kommuneInfoList: MutableState<List<KommuneInfo>>) {
    val BASE_URL = "https://ws.geonorge.no/"
    val TAG: String = "CHECK_RESPONSE"

    /**
     * Helt lik som den i FylkeActivity
     */
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KommuneApiService::class.java)

    /**
     * Helt likt som FylkeActivity, bortsett fra at den kaller "hentKommune()" i stedet for "hentFylke()"
     * Her blir det også logget feilmeldinger hvis det er feil
     */
    api.hentKommune().enqueue(object : Callback<List<KommuneInfo>> {
        override fun onResponse(call: Call<List<KommuneInfo>>, response: Response<List<KommuneInfo>>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    kommuneInfoList.value = it
                }
            }
        }
        override fun onFailure(call: Call<List<KommuneInfo>>, t: Throwable) {
            Log.i(TAG, "onFailure: ${t.message}")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**
         * Samme som FylkeActivity, får inn tabell som parameter,
         * Sorterer det som er i parameter og sorterer det på kommunenummer
         * Bruker Scaffold og lager ToppAppBar igjen,
         * Og kaller KommuneListe funksjonen for å legge inn den sorterte listen
         */
fun KommuneSiden(kommuneInfoTabell: List<KommuneInfo>) {

    // Her sorterer vi tabellen på navn
    val sorterKommune = kommuneInfoTabell.sortedBy{it.kommunenummer}

    Scaffold(topBar = { TopAppBarKommune() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KommuneListe(sorterKommune)
        }
    }
}

@Composable
        /**
         * Får liste som innparameter,
         * Lager kommuneContext for å kjøre LocalContext.current
         * Gjør teksten også clickable og bruker intent for å hente ritkige restauranter i den valgte kommunen
         */
fun KommuneListe(kommuneInfoTabell: List<KommuneInfo>) {

    val kommuneContext = LocalContext.current

    LazyColumn(modifier = Modifier) {
        items(kommuneInfoTabell) { kommune ->
            Text(
                text = kommune.kommunenavnNorsk,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable{
                        /**
                         * Her kjører intent RestaurantInfoActivity klassen fordi det er den som lager
                         * kortene til alle restauranter
                         */
                      val intent = Intent(kommuneContext, RestaurantInfoActivity::class.java)
                      intent.putExtra("valgtKommune", kommune.kommunenavnNorsk)
                      kommuneContext.startActivity(intent)
                    }
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
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
        /**
         * Her er det igjen ToppAppBar for kommuner siden, som har tittel "Kommuner" og akkurat samme
         * tilbakeknapp og den slutter activity den går vekk fra.
         */
fun TopAppBarKommune(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Kommuner",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = {(localContext as? ComponentActivity)?.finish()}) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Tilbake"
                )
            }
        },

        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}