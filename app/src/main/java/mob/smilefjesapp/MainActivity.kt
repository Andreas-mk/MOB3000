package mob.smilefjesapp

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.search.SearchBar
import mob.smilefjesapp.ui.theme.SmilefjesappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Start()
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Start(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    // Text i textfield (søkefelt)
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7))) // Stringen var example før, kan fjernes??
    }
    Scaffold(topBar = { TopAppBar() }
    ) {
        Column(
            modifier = Modifier
                //.fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.smilefjes),
                contentDescription = ("Smilefjes-logo")
            )
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp)
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
            )
            Button(
                modifier = Modifier
                    .padding(5.dp),
                onClick = {
                    localContext.startActivity(Intent(localContext, FylkeActivity::class.java))
                }
            ) {
                Text(
                    text = "Vis fylker",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Button(
                modifier = Modifier
                    //.fillMaxSize()
                    .padding(5.dp),
                onClick = {
                    localContext.startActivity(Intent(localContext, KommuneActivity::class.java))
                }
            ) {
                Text(
                    text = "Vis kommuner",
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
            )
            Button(
                modifier = Modifier
                    //.fillMaxSize()
                    .padding(5.dp),
                onClick = {
                    //localContext.startActivity(Intent(localContext, KommuneActivity::class.java))
                    // Hent info fra Textfield -> Send rett til info side om riktig restaurant
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





