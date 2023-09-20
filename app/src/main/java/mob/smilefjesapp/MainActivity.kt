package mob.smilefjesapp

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun Start(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            modifier = Modifier
                .padding(5.dp)
            ,
            onClick = {
                localContext.startActivity(Intent(localContext, FylkeActivity::class.java))
            }
        ){
            Text(
                text = "Vis fylker",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Button(
            modifier = Modifier
                //.fillMaxSize()
                .padding(5.dp)
            ,
            onClick = {
                localContext.startActivity(Intent(localContext, KommuneActivity::class.java))
            }
        ) {
            Text(
                text="Vis kommuner",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
/*
Kan kanskje brukes, men vet ikke hvordan i såfall, har kommentert ut men ligger i koden
i tilfelle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HovedMeny(modifier: Modifier = Modifier){

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "SmilefjesApp",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        navigationIcon = {
                IconButton(onClick = {/*Noe her*/}) {
                    Icon(imageVector = Icons.Rounded.Menu,
                        contentDescription = "Meny"
                    ) }
        },
        modifier = modifier
    )
}

 */

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmilefjesappTheme {
        Start()
    }
}