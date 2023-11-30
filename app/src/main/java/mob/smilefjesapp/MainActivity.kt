package mob.smilefjesapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mob.smilefjesapp.dataklasse.GpsKommune
import mob.smilefjesapp.nettverk.FylkerOgKommunerApi
import retrofit2.Response

/**
 * Klassen fungerer som en hovedmeny.
 * Henting av posisjon fra GPS håndteres også her.
 * Kilde brukt:
 * Kvisli, J. (2023). Bruke posisjon [Lysbildepresentasjon]. https://itfag.usn.no/~kvisli/MOB3000/leksjoner/L07/Posisjon.pdf
 */
class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    val locationPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var tillatelserGitt: Boolean = false

    // Sjekker om bruker har gitt tillatelse til å dele posisjon
    val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)   -> {
                    // Nøyaktig posisjon gitt
                    tillatelserGitt=true
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Wifi / mobilnett posisjon gitt
                    tillatelserGitt=true
                }
                else -> tillatelserGitt=false
            }
        }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setContent {
            SmilefjesappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    ) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    sjekkTillatelser()
                    Start(
                        Modifier,
                        windowSizeClass,
                        lokasjonsTillatelserGitt = tillatelserGitt,
                        locationManager = locationManager
                    )
                }

            }
        }
    }

    fun sjekkTillatelser() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Bruker har gitt tillatelse.
                tillatelserGitt=true
            }
            else -> {
                // Spør om tillatelse
                locationPermissionRequest.launch(locationPermissions)
            }
        }
    }
}
 private fun finnSistePosisjon(context: Context, locationManager: LocationManager): Location?
{
    val locationProvider=LocationManager.GPS_PROVIDER
    var minPosisjon: Location? = null
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        == PackageManager.PERMISSION_GRANTED)
    {
        if (locationManager.isProviderEnabled(locationProvider))
            minPosisjon = locationManager.getLastKnownLocation(locationProvider)
        else  Log.d("finnSistePosisjon", "GPS er ikke aktivert.")
    }
    else Log.d("finnSistePosisjon","Ikke tillatelse")
    return minPosisjon
}

private fun lyttPåPosisjon(locationManager: LocationManager?, posisjonState: MutableState<Location?>) : LocationListener?
{
    val TID = 10.toLong() // Mellom hver avlesning av GPSen går det 10ms
    val AVSTAND = 0.toFloat() // Minimum 0 meter mellom hver avlesning av GPS

    val locationProvider = LocationManager.GPS_PROVIDER
    var lytter = false

    // Lytteobjekt/metode for endring i lokasjon
    val locationListener: LocationListener = object: LocationListener {
        override fun onLocationChanged(location: Location) {
            posisjonState.value = location
        }
    }

    // Aktiverer lytting på endring i GPS-posisjon
    if (locationManager!!.isProviderEnabled(locationProvider)) {
        try {
            locationManager.requestLocationUpdates(
                locationProvider,
                TID,
                AVSTAND,
                locationListener
            )
            lytter=true
        } catch (e: SecurityException) {
            e.printStackTrace()
            lytter=false
        }
    }
    if (lytter)
        return locationListener
    else
        return null
}

fun stoppGpsLytting(locationManager: LocationManager, locationListener: LocationListener)
{
    val locationProvider = LocationManager.GPS_PROVIDER
    if (locationManager.isProviderEnabled(locationProvider)) {
        locationManager.removeUpdates(locationListener)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Start(
    modifier: Modifier = Modifier,
    windowSizeClass : WindowSizeClass,
    lokasjonsTillatelserGitt: Boolean,
    locationManager: LocationManager?
) {
    val localContext = LocalContext.current
    val gpsContext = LocalContext.current
    val vinduBredde = windowSizeClass.widthSizeClass

    // GPS TEST
    val minPosisjonState: MutableState<Location?> = remember {  mutableStateOf(null)  }
    val lytterPåGpsState = remember {  mutableStateOf(false)  }
    val locationListenerState: MutableState<LocationListener?> = remember{ mutableStateOf(null)}
    if (lokasjonsTillatelserGitt && locationManager!=null) {
        minPosisjonState.value = finnSistePosisjon(localContext, locationManager)
    }

    var gpsSvar: Response<GpsKommune>
    val coroutineScope = rememberCoroutineScope()
    // Text i textfield (søkefelt)
    var text by rememberSaveable { mutableStateOf("") } // alternativt?

    Scaffold(topBar = { TopAppBar() }
    ) {
        when(vinduBredde){
            WindowWidthSizeClass.Compact -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Divider(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.smilefjes),
                        contentDescription = ("Smilefjes-logo"),
                        Modifier.size(600.dp, 200.dp)
                    )
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(270.dp, 65.dp),
                        onClick = {
                            localContext.startActivity(Intent(localContext, FylkeActivity::class.java))
                        }
                    ) {
                        Text(
                            text = "Vis fylker",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Restaurantsøk") }
                    )
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(270.dp, 65.dp),
                        onClick = {
                            // Hent info fra Textfield -> Send rett til info side om riktig restaurant
                            val intent = Intent(localContext, RestaurantInfoActivity::class.java)
                            intent.putExtra("navn", text) // text er fra textfield
                            localContext.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = "Søk på restaurant",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(270.dp, 65.dp),
                        onClick = {
                            // Starter og avslutter lytting
                            lytterPåGpsState.value=false // funker når denne er plasert her
                            if (locationManager!=null) {
                                if (!lytterPåGpsState.value) {
                                    // Start lytting på posisjon
                                    locationListenerState.value=lyttPåPosisjon(locationManager, minPosisjonState)
                                    lytterPåGpsState.value =  (locationListenerState.value != null)
                                }
                                else {
                                    // Stopp lytting på posisjon
                                    if (locationListenerState.value != null) {
                                        stoppGpsLytting(
                                            locationManager,
                                            locationListenerState.value as LocationListener
                                        )

                                    }
                                }
                            }
                            val minPosisjon = minPosisjonState.value
                            val lat = minPosisjon?.latitude?.toString()
                            val long = minPosisjon?.longitude?.toString()

                            // Gjør et API kall som finner hvilken kommune de oppgitte koordinatene befinner seg i
                            if (lat != null && long != null){
                                coroutineScope.launch(Dispatchers.IO) {
                                    // 4258 er SRID for koordinatsystemet som passer til koordinatene våre (Oppgitt av Kartverket)
                                    gpsSvar = FylkerOgKommunerApi.retrofitService.foKSammen(lat,long,"4258")
                                    if (gpsSvar.isSuccessful) {
                                        val body = gpsSvar.body()!!
                                        val intent = Intent(gpsContext, RestaurantInfoActivity::class.java)
                                        intent.putExtra("valgtKommune", body.kommunenavn)
                                        gpsContext.startActivity(intent)
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Din posisjon",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }else -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.smilefjes),
                        contentDescription = ("Smilefjes-logo")
                    )
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceEvenly

                    ) {
                        Button(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(65.dp)
                                .weight(1f),
                            onClick = {
                                localContext.startActivity(
                                    Intent(localContext, FylkeActivity::class.java)
                                )
                            }
                        ) {
                            Text(
                                text = "Vis fylker",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        TextField(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(65.dp)
                                .weight(1f),
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Restaurantsøk") }
                        )
                    }
                    Row (
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        Button(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(65.dp)
                                .weight(1f),
                            onClick = {
                                /* Det samme skal skje dersom man trykker på "Din posijon" i stående/liggende format.
                                   Derfor er de to onClick parameterne det samme. Dette burde være i en egen funksjon for
                                   å spare linjer med koder, men vi fikk problemer da det er verdier i
                                   Start() vi ikke fikk oppdatert i den nye funksjonen.
                                 */

                                lytterPåGpsState.value=false
                                if (locationManager!=null) {
                                    if (!lytterPåGpsState.value) {
                                        locationListenerState.value=lyttPåPosisjon(locationManager, minPosisjonState)
                                        lytterPåGpsState.value =  (locationListenerState.value != null)
                                    }
                                    else {
                                        if (locationListenerState.value != null) {
                                            stoppGpsLytting(
                                                locationManager,
                                                locationListenerState.value as LocationListener
                                            )
                                        }
                                    }
                                }
                                val minPosisjon = minPosisjonState.value
                                val lat = minPosisjon?.latitude?.toString()
                                val long = minPosisjon?.longitude?.toString()

                                if (lat != null && long != null){
                                    coroutineScope.launch(Dispatchers.IO) {
                                        gpsSvar = FylkerOgKommunerApi.retrofitService.foKSammen(lat,long,"4258")
                                        if (gpsSvar.isSuccessful) {
                                            val body = gpsSvar.body()!!
                                            val intent = Intent(gpsContext, RestaurantInfoActivity::class.java)
                                            intent.putExtra("valgtKommune", body.kommunenavn)
                                            gpsContext.startActivity(intent)
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Din posisjon",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        Button(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(65.dp)
                                .weight(1f),
                            onClick = {
                                val intent = Intent(localContext, RestaurantInfoActivity::class.java)
                                intent.putExtra("navn", text)/*TEKST FRA TEXTFIELD*/
                                localContext.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Søk på restaurant",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier){
    // Top App Bar for enkel oversikt og navigasjon
    CenterAlignedTopAppBar(
        title = {
            Text( text = "Smilefjesappen",
                modifier = Modifier.fillMaxWidth(),
                style=MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}

