package mob.smilefjesapp

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    Start(Modifier, windowSizeClass)
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Start(modifier: Modifier = Modifier, windowSizeClass : WindowSizeClass) {
    val localContext = LocalContext.current
    val vinduBredde = windowSizeClass.widthSizeClass
    // Text i textfield (søkefelt)
    //var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
    //    mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    //}
    var text by remember { mutableStateOf("") } // alternativt?
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
                            localContext.startActivity(Intent(localContext, FavoritterActivity::class.java)) // HUSK BYTT FAVORITTER
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
                                localContext.startActivity(
                                    Intent(localContext, FylkeActivity::class.java) // BYTT UT MED RestaurantInfoActivity og intent
                                )
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

// Mulig vi lager egen searchbar med TextField og knapp
@Composable
fun Søk(){
    //TextField(value = "test", onValueChange = )
    /*
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("example", TextRange(0, 7)))
    }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") }
    )

     */
}





