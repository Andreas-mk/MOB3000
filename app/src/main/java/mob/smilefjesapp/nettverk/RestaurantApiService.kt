package mob.smilefjesapp.nettverk

import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import mob.smilefjesapp.dataklasse.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Standard Retrofit ApiService oppsett (mars photo codelaben)
 */

// Det finnes flere Keys i datasettet vi ikke trenger (feks de samme kategoriene, men på nynorsk), derfor ignorerer vi disse.
val format = Json {ignoreUnknownKeys = true} // Kilde https://github.com/Kotlin/kotlinx.serialization/blob/master/guide/example/example-json-03.kt APA 7 ???
private const val BASE_URL = "https://hotell.difi.no/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(format.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()
interface RestaurantApiService{
    @GET("api/json/mattilsynet/smilefjes/tilsyn?")
    suspend fun hentRestauranter(): Response<ApiResponse>
}
object RestaurantApi {
    val retrofitService: RestaurantApiService by lazy {
        retrofit.create(RestaurantApiService::class.java)
    }
}