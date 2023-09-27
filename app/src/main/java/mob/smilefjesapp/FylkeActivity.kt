package mob.smilefjesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme


class FylkeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val fylkeTabell = resources.getStringArray(R.array.fylker).asList()
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface contianer using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FylkeSiden(fylkeTabell)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FylkeSiden(fylkeTabell: List<String>, modifier: Modifier = Modifier) {
    // Rett fra kommunesiden og powerpoint

    Scaffold (topBar = {ToppAppBar()}
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            FylkeListe(fylkeTabell)
        }
    }
}

@Composable
fun FylkeListe(fylkeTabell: List<String>, modifier: Modifier = Modifier){
    // Rett fra kommuneactivity som er igjen henter fra powerpoint

    LazyColumn(
        modifier = modifier
    ) {
        // dadadadada
        items(fylkeTabell) {fylke ->
            Text(
                text = fylke,
                modifier = Modifier
                    .padding(20.dp)
                    /*.clickable(
                        enabled = true,
                        onClick = {/*Skal sende videre men lagde den bare nå*/}
                    )
                     */
                    .fillMaxWidth(),
                color = Color.Blue,
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
                color = Color.Black,
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
        /* Trenger kanskje ikke å søke på fylker fordi det er så få fylker i Norge
        * Fjern helt????*/
        actions = {
            IconButton(onClick = { /* Noe inni her*/}) {
                Icon(imageVector = Icons.Filled.Search,
                    contentDescription = "Søk"
                ) }
        },
        modifier = modifier
    )
}
