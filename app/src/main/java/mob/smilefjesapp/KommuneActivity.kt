package mob.smilefjesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.LaunchedEffect
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
import mob.smilefjesapp.nettverk.KommuneApiService
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KommuneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val kommuneInfoList = remember { mutableStateOf(listOf<KommuneInfo>()) }
                    hentAlleKommuner(kommuneInfoList)
                    KommuneSiden(kommuneInfoList.value)
                }
            }
        }
    }
}

// Funksjonen som henter inn kommuner via API kall med Retrofit
// BASE_URL er bare selve nettsiden, også ligger resten av peker til nettside
// inni KommuneApiService filen, der den peker nøyaktig hvor vi henter data fra
private fun hentAlleKommuner(kommuneInfoList: MutableState<List<KommuneInfo>>) {
    val BASE_URL = "https://ws.geonorge.no/"
    val TAG: String = "CHECK_RESPONSE"

    // Bygger variabel for API ved å bruke Retrofit.Builder()
    // legger til BaseURL og legger til Gson converter - som er en oversetter
    // for raw Json data og gjør det om til brukbare kotlin objekter
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KommuneApiService::class.java)

    // Her bruker variablen hentKommune() funksjonen i KommuneApiService og kaller på KommuneInfo
    // dataklassen. Det er også lagt til funksjoner som sjekker om det blir godkjent eller om det feiler
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
fun KommuneSiden(kommuneInfoTabell: List<KommuneInfo>) {

    // Her sorterer vi tabellen på navn
    val sorterKommune = kommuneInfoTabell.sortedBy{it.kommunenavnNorsk}

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
fun KommuneListe(kommuneInfoTabell: List<KommuneInfo>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(kommuneInfoTabell) { kommune ->
            Text(
                text = kommune.kommunenavnNorsk,
                modifier = Modifier
                    .padding(20.dp)
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
            IconButton(onClick = { localContext.startActivity(Intent(localContext, MainActivity::class.java)) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Tilbake"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Søk"
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}