package mob.smilefjesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mob.smilefjesapp.ui.theme.SmilefjesappTheme

class FavoritterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmilefjesappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FavoritterListe()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritterListe() {
    Scaffold(topBar = { TopAppBarFavoritter() }
    ) {
        Column( // Row funker visst ikke med LazyColumn
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
                //horizontalArrangement = Arrangement.SpaceEvenly

            ){
                Text(
                    text = "Nellys Kebab",
                    modifier = Modifier
                        .padding(10.dp)
                        .wrapContentWidth(Alignment.Start),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 28.sp,
                )
                Icon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                        .padding(10.dp),
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favoritt-ikon"
                )

            }
            // Dummykode for å vise på demo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
                //horizontalArrangement = Arrangement.SpaceEvenly

            ){
                Text(
                    text = "Stasjonsbua",
                    modifier = Modifier
                        .padding(10.dp)
                        .wrapContentWidth(Alignment.Start),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 28.sp,
                )
                Icon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                        .padding(10.dp),
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favoritt-ikon"
                )

            }
            Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
            //horizontalArrangement = Arrangement.SpaceEvenly

            ){
            Text(
                text = "Aasmundsen Bakeri",
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentWidth(Alignment.Start),
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 28.sp,
            )
            Icon(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(10.dp),
                imageVector = Icons.Filled.Star,
                contentDescription = "Favoritt-ikon"
            )

        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFavoritter(modifier: Modifier = Modifier){
    val localContext = LocalContext.current
    // Top App Bar for enkel oversikt og navigasjon
    CenterAlignedTopAppBar(
        title = {
            Text( text = "Favoritter",
                modifier = Modifier.fillMaxWidth(),
                style=MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = { localContext.startActivity(Intent(localContext, MainActivity::class.java)) }) { /* Gå tilbake til forrige activity i stakken*/
                Icon(imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Tilbake"
                ) }
        },
        // Søkeknapp lar brukeren raskt søke blant kommunene
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(imageVector = Icons.Filled.Search,
                    contentDescription = "Søk"
                ) }
        },
        // Fjerner TopAppBars hvite bakgrunn
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        modifier = modifier
    )
}