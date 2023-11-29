package mob.smilefjesapp.nettverk

import mob.smilefjesapp.dataklasse.KommuneInfo
import retrofit2.Call
import retrofit2.http.GET

/**
 * Samme som FylkeApi så er dette det samme bare for kommune. Bruker @GET for å bare hente kommune
 */
interface KommuneApiService {
    @GET("kommuneinfo/v1/kommuner")
    fun hentKommune(): Call<List<KommuneInfo>>
}