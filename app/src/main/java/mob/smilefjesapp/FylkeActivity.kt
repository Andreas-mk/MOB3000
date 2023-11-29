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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mob.smilefjesapp.dataklasse.FylkeInfo
import mob.smilefjesapp.nettverk.FylkeAPI
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Fylkeactivity klassen, som er bygd opp på samme måte som alle andre activity med onCreate funksjon
 * som setter setContent og setter inn informasjonen der
 */
class FylkeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface contianer using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * Variabler som er mutable slik at det kan endres på, og kaller funksjonen som
                     * er retrofit api kallet som igjen bruker FylkeApi interface for å gjøre api kall
                     * Så kaller den FylkeSiden som setter fylkelister inn og viser dette frem
                     */
                    val fylkeInfoList = remember { mutableStateOf(listOf<FylkeInfo>()) }
                    hentAlleFylker(fylkeInfoList)
                    FylkeSiden(fylkeInfoList.value)
                }
                }
            }
        }
    }

/**
 * Funksjonen som gjør API kall ved hjelp av FylkeApi klassen
 * Bruker retrofit for å gjøre api kall, og bruker gsonconverter for serialisering av json data til kotlin
 * objekt. Dette ble også gjort ved hjelp av retrofit tutorial.
 * https://square.github.io/retrofit/
 * https://www.youtube.com/watch?v=5gFrXGbQsc8
 */
    private fun hentAlleFylker(fylkeInfoList: MutableState<List<FylkeInfo>>) {
        val BASE_URL = "https://ws.geonorge.no/"
        val TAG: String = "CHECK_RESPONSE"

    /**
     * Dette er sånn retrofit build ser ut, der det bygger en retrofitbuilder,
     * legger til baseURL som er hoved url til geonorge som er deklarert over
     * adder en converterfactory
     * så bygger den.
     * Så er det å lage, og da lager den ved hjelp av FylkeAPI klassen.
     */
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FylkeAPI::class.java)

    /**
     * Her bruker api variabelen hentFylke() funksjonen somer lagd i fylkeApi
     * og ved hjelp av å trykke CTRL+SHIFT+SPACEBAR så blir det autogenerert onResponse og onFailure
     * funksjoner som sjekker om responsen blir successful og hvis den ikke blir det så gir den feilmelding
     * i logcat i konsollet.
     */
        api.hentFylke().enqueue(object : Callback<List<FylkeInfo>> {
            override fun onResponse(call: Call<List<FylkeInfo>>, response: Response<List<FylkeInfo>>) {
                if (response.isSuccessful){
                    response.body()?.let {
                            fylkeInfoList.value = it
                        }
                    }
                }

            /**
             * Her er feilmeldingen logget hvis det skjer feil
             */
            override fun onFailure(call: Call<List<FylkeInfo>>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**
         * Funksjonen Fylkesiden er lagd ved hjelp av powerpoint og vi sorterer fylketabellen på nummer
         * Her blir det lagd Scaffold og setter inn ToppAppBar
         * Og kaller FylkeListe funksjonen som er den som faktisk får inn alle fylkene ved hjelp av intent
         * og setter inn sorterFylke som parameteren så det blir sortert.
         */
fun FylkeSiden(fylkeInfoTabell: List<FylkeInfo>) {
    val sorterFylke = fylkeInfoTabell.sortedBy{it.fylkesnummer}

    Scaffold (topBar = {ToppAppBar()}
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            FylkeListe(sorterFylke)
        }

    }
}

@Composable
        /**
         * Funksjonen fylkeliste som får inn en liste som parameter,
         * Lager en context som er LocalContext.current for å kjøre intent
         * Så i Texten så er den clickable så det går an å klikke på de forskjellige fylkene
         * og det er da intenten lages, og kjører i dette tilfellet "kommuneactivity" for å hente kommunene
         * Så brukes intent.putExtra for å sette inn key-value for å få riktige objekt, så brukes context for å starte activity.
         * Ellers er det bare utfylling av padding og farger i Spacer og Divider
         */
fun FylkeListe(fylkeInfoTabell: List<FylkeInfo>, modifier: Modifier = Modifier){
    // Rett fra kommuneactivity som er igjen henter fra powerpoint
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
    ) {
        // dadadadada
        items(fylkeInfoTabell) { fylke ->
            Text(
                text = fylke.fylkesnavn,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable{
                        val intent = Intent(context, KommuneActivity::class.java)
                        intent.putExtra("fylkeInfo",fylke)
                        context.startActivity(intent)
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
                    .clip(MaterialTheme.shapes.small)
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
         * ToppAppBar funksjonen som er sentrert i hovedsiden og alle actvities, her får den "Fylker" overskriften
         * og bruker themes som vi har egendefinert
         * Det blir også lagt til icon får å gå tilbake til forrige activity man var på og stopper den man var før man gikk tilbake
         */
fun ToppAppBar(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Fylker",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        navigationIcon = {
                    IconButton(onClick = {(localContext as? ComponentActivity)?.finish()}) {
                        Icon(imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Tilbake")
                    }
        },
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}
