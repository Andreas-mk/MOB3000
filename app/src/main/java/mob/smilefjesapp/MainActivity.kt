package mob.smilefjesapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.material.search.SearchBar
import mob.smilefjesapp.ui.theme.SmilefjesappTheme
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat

//
class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager

    public var minPosisjon: Location? = null

    public val locationPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var tillatelserGitt: Boolean = false

    // ActivityResultLauncher med callback-funksjon
    // Brukes i sjekkTillatelser() for å starte systemdialogen som ber bruker gi rettigheter.
    val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)   -> {
                    // Precise location access granted.
                    tillatelserGitt=true
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    tillatelserGitt=true
                }
                // No location access granted.
                else -> tillatelserGitt=false
            }
        }


    public fun sjekkTillatelser() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                tillatelserGitt=true
            }
            else -> {
                // Overlat til Andoroid OS å spørre etter tillatelse.
                // ActivityResultCallback mottar og behandler resultatet.
                locationPermissionRequest.launch(locationPermissions)
            }
        }
    }
    // --------------------------------------
     public fun finnSistePosisjon(context: Context, locationManager: LocationManager): Location?
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
            /*
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            // Got last known location. In some rare situations this can be null.
                            posisjon = location
                        }

         */
            else  Log.d("finnSistePosisjon", "GPS er ikke aktivert.")
        }
        else Log.d("finnSistePosisjon","Ikke tillatelse")
        return minPosisjon
    }
    // ------------------------
    public fun lyttPåPosisjon(locationManager: LocationManager?, posisjonState: MutableState<Location?>) : LocationListener?
    {
        val TID = 1000.toLong() // Tid mellom hver GPS avlesning i ms
        val AVSTAND = 0.toFloat() // Minste avstand mellom hver GPS avlesning i meter

        val locationProvider = LocationManager.GPS_PROVIDER
        var lytter: Boolean = false

        // Lytteobjekt/metode for endring i lokasjon
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                posisjonState.value = location
            }
        }

        // Aktiver lytting på endring i GPS-posisjon
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

    // -------------------------------------

    public fun stoppGpsLytting(locationManager: LocationManager, locationListener: LocationListener)
    {
        val locationProvider = LocationManager.GPS_PROVIDER
        if (locationManager.isProviderEnabled(locationProvider)) {
            locationManager.removeUpdates(locationListener)
        }
    }



    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                ) {

                    val windowSizeClass = calculateWindowSizeClass(this)
                    sjekkTillatelser()
                    //val pos = finnSistePosisjon()





                    Start(Modifier, windowSizeClass)
                }

            }
        }
    }

    // må prøve å Hente ut min posisijon for å se hva som kommer ut av den
    @Composable
    fun GPS_Demo(
        modifier: Modifier = Modifier,
        lokasjonsTillatelserGitt: Boolean,
        locationManager: LocationManager?
    ) {
        val context = LocalContext.current

        val minPosisjonState: MutableState<Location?> = remember {  mutableStateOf(null)  }
        val lytterPåGpsState = remember {  mutableStateOf(false)  }
        val locationListenerState: MutableState<LocationListener?> = remember{ mutableStateOf(null)}

        if (lokasjonsTillatelserGitt && locationManager!=null) {
            minPosisjonState.value = finnSistePosisjon(context, locationManager)
            //lytter.value = lyttPåPosisjon(locationManager, minPosisjon)
        }

        Log.d("", minPosisjonState.value.toString())
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Start(modifier: Modifier = Modifier, windowSizeClass : WindowSizeClass, ) {
    val localContext = LocalContext.current
    val vinduBredde = windowSizeClass.widthSizeClass

    // GPS TEST




    // Text i textfield (søkefelt)
    var text by rememberSaveable { mutableStateOf("") } // alternativt?
    Scaffold(topBar = { TopAppBar() }
    ) {
        when(vinduBredde){
            WindowWidthSizeClass.Compact -> {
                Column(
                    modifier = Modifier
                        //.fillMaxSize()
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
                    // Elevated button? Ser kanskje litt finere ut
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
                    //Søk()
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Restaurantsøk") }
                        //colors = ContainerColor.toColor() // Bakgrunnsfarge for søkeboksen (funker ikke)
                        //containerColor: Color = FilledTextFieldTokens.ContainerColor.toColor(),
                    )
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(270.dp, 65.dp),
                        onClick = {
                            //localContext.startActivity(Intent(localContext, KommuneActivity::class.java))
                            // Hent info fra Textfield -> Send rett til info side om riktig restaurant
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
                            //localContext.startActivity(Intent(localContext, RestaurantInfoActivity::class.java))
                            Log.d("", "$")

                            /* hvor skal kallet på metoden flyttesv
                            GPS_Demo(
                                modifier=Modifier,
                                lokasjonsTillatelserGitt = false,
                                locationManager = null
                            )*/


                        }
                    ) {
                        Text(
                            text = "GPS Posisjon",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }else -> {
                Column(
                    modifier = Modifier
                        //.fillMaxSize()
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
                            //colors = ContainerColor.toColor() // Bakgrunnsfarge for søkeboksen (funker ikke)
                            //containerColor: Color = FilledTextFieldTokens.ContainerColor.toColor(),
                        )
                    }
                    Row (
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        //Søk()
                        // Endre plasseringen? Slik at Textfield er over søkeknapp (som på stående skjerm)
                        Button(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(65.dp)
                                .weight(1f),
                            onClick = {
                                /*localContext.startActivity(
                                    Intent(localContext, FylkeActivity::class.java) // BYTT UT MED RestaurantInfoActivity og intent
                                )*/

                                // Må kanskje lage en aktivitet som på de andre, og lage en klasse som henter posisjonen kanskje?

                            }
                        ) {
                            Text(
                                text = "GPS posisjon",
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



fun finnSistePosisjon() {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier){
    val localContext = LocalContext.current
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





