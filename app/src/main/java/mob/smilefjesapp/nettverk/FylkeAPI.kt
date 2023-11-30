package mob.smilefjesapp.nettverk

import mob.smilefjesapp.dataklasse.FylkeInfo
import retrofit2.Call
import retrofit2.http.GET


/**
 * API kall for å hente fylker fra geonorge sitt api. Her er bare GET funksjonen
 */
interface FylkeAPI {

    @GET("kommuneinfo/v1/fylker")
    fun hentFylke(): Call<List<FylkeInfo>>
}