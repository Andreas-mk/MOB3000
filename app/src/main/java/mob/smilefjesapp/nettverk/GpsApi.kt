package mob.smilefjesapp.nettverk

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mob.smilefjesapp.dataklasse.GpsKommune
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

val formatGPS = Json {ignoreUnknownKeys = true}
private const val BASE_URL = "https://ws.geonorge.no/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(formatGPS.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface FylkerOgKommuner {
    @GET("kommuneinfo/v1/punkt?")
    suspend fun foKSammen(@Query("nord")longitude: String,
                  @Query("ost")latitude: String,
                  @Query("koordsys")koordinatSys: String
    ): Response<GpsKommune>
}

object FylkerOgKommunerApi {
    val retrofitService: FylkerOgKommuner by lazy {
        retrofit.create(FylkerOgKommuner::class.java)
    }
}