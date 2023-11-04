package mob.smilefjesapp.dataklasse

import android.util.Log

data class KommuneInfo (
    val kommunenavnNorsk: String,
    val kommunenummer: String,
    val fylkeInfo: FylkeInfo
)

fun getKommuneListeForFylke(allKommuneListe: List<KommuneInfo>, valgtFylkeInfo: FylkeInfo?): List<KommuneInfo> {
    val filtrertListe = allKommuneListe.filter { it.fylkeInfo == valgtFylkeInfo }
    Log.d("FiltrertListe", "Filtrertliste: $filtrertListe")

    return valgtFylkeInfo?.let { fylkeInfo ->
        allKommuneListe.filter { it.kommunenummer.startsWith(fylkeInfo.fylkesnummer.toString())}
    } ?: emptyList()

}