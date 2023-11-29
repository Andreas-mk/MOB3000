package mob.smilefjesapp.dataklasse

import android.util.Log

/**
 * Dataklasse for samme som fylker, bare her kommer det kommunenavnNorsk, kommunenummer og henter inn fylkeInfo dataklassen
 * for å bruke fylker for å sette kommuner inn under det.
 */
data class KommuneInfo (
    val kommunenavnNorsk: String,
    val kommunenummer: String,
    val fylkeInfo: FylkeInfo
)

/**
 * Funksjon som har liste som parameter, filtrerer listen og gjør at fylkeinfo dataklassen får et valgt objekt
 * returnerer så det valgtefylke objektet og setter det at den henter kommunenummer som starter med fylkeinfo dataklassen
 * sitt fylkesnummer og skriver dette toString som gjør at det er brukbart i koden.
 */
fun getKommuneListeForFylke(allKommuneListe: List<KommuneInfo>, valgtFylkeInfo: FylkeInfo?): List<KommuneInfo> {
    val filtrertListe = allKommuneListe.filter { it.fylkeInfo == valgtFylkeInfo }
    Log.d("FiltrertListe", "Filtrertliste: $filtrertListe")

    return valgtFylkeInfo?.let { fylkeInfo ->
        allKommuneListe.filter { it.kommunenummer.startsWith(fylkeInfo.fylkesnummer.toString())}
    } ?: emptyList()

}