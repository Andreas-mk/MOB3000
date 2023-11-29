package mob.smilefjesapp.nettverk

import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import mob.smilefjesapp.dataklasse.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Standard Retrofit ApiService oppsett med funksjoner for API kall med poststed og restaurant-navn
 */
// Det finnes flere Keys i datasettet vi ikke trenger (feks de samme kategoriene, men på nynorsk), derfor ignorerer vi disse.
val format = Json {ignoreUnknownKeys = true} // Kilde https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#ignoring-unknown-keys / https://github.com/Kotlin/kotlinx.serialization/blob/master/guide/example/example-json-03.kt APA 7 ???
private const val BASE_URL = "https://hotell.difi.no/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(format.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()
interface RestaurantApiService{
    // Henter restauranter basert på brukerens valgte kommune
    @GET("api/json/mattilsynet/smilefjes/tilsyn?")
    // Legger til query for å hente poststed og bruker dette for å få riktig restaurant i kommune
    suspend fun hentRestauranter(
        @Query("poststed")poststed: String,
        @Query("page")page: Int
    ): Response<ApiResponse>

    // Brukes når bruker søker på restaurant i søkefeltet
    @GET("api/json/mattilsynet/smilefjes/tilsyn?")
    suspend fun hentMedSøk(
        @Query("navn")navn: String,
        @Query("page")page: Int
    ): Response<ApiResponse>
}
object RestaurantApi {
    val retrofitService: RestaurantApiService by lazy {
        retrofit.create(RestaurantApiService::class.java)
    }
}