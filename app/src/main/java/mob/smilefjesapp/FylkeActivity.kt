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
import androidx.compose.ui.platform.LocalContext



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
                    val fylkeInfoList = remember { mutableStateOf(listOf<FylkeInfo>()) }
                    hentAlleFylker(fylkeInfoList)
                    FylkeSiden(fylkeInfoList.value)
                }
                }
            }
        }
    }

// Funksjonen som henter alle fylker via API kall med Retrofit
// Lager en funksjon BASE_URL som er første del av API, mens resten hentes via FylkeAPI filen og GET request
    private fun hentAlleFylker(fylkeInfoList: MutableState<List<FylkeInfo>>) {
        val BASE_URL = "https://ws.geonorge.no/"
        val TAG: String = "CHECK_RESPONSE"

    // Her kommer Retrofit i aksjon, og bruker for å bygge en funksjon "api"
    // Som bruker en .create() som bruker da FylkeAPI klassen til å hente resten av api kallet
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FylkeAPI::class.java)

    // Her kommer funksjoner for onResponse og onFailure for å sjekke om den feiler
        api.hentFylke().enqueue(object : Callback<List<FylkeInfo>> {
            override fun onResponse(call: Call<List<FylkeInfo>>, response: Response<List<FylkeInfo>>) {
                if (response.isSuccessful){
                    response.body()?.let {
                            fylkeInfoList.value = it
                        }
                    }
                }
            // Denne gir oss feilmelding og ossen type feilmelding
            override fun onFailure(call: Call<List<FylkeInfo>>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FylkeSiden(fylkeInfoTabell: List<FylkeInfo>, modifier: Modifier = Modifier) {
    // Rett fra kommunesiden og powerpoint
    val sorterFylke = fylkeInfoTabell.sortedBy{it.fylkesnavn}

    Scaffold (topBar = {ToppAppBar()}
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
                //.background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            FylkeListe(sorterFylke)
        }
    }
}

@Composable
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
                    IconButton(onClick = {localContext.startActivity(Intent(localContext, MainActivity::class.java))}) {
                        Icon(imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Tilbake")
                    }
        },
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}
