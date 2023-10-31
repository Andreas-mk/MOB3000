package mob.smilefjesapp.nettverk

import kotlinx.serialization.Serializable
import mob.smilefjesapp.dataklasse.FylkeInfo
import retrofit2.Call
import retrofit2.http.GET


// Interface som i dette tilfellet henter API om fylker i en GET request
// Denne brukes sammen med Retrofit i Fylkeactivity for å sette sammen en hel API url
interface FylkeAPI {

    @GET("kommuneinfo/v1/fylker")
    fun hentFylke(): Call<List<FylkeInfo>>
}