package mob.smilefjesapp.nettverk

import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import mob.smilefjesapp.dataklasse.RestaurantInfo
import retrofit2.Response
import retrofit2.http.GET

private const val BASE_URL = "https://hotell.difi.no/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()
interface RestaurantApiService{
    @GET("api/json/mattilsynet/smilefjes/tilsyn?")
    suspend fun getRestauranter(): Response<List<RestaurantInfo>>
}
object RestaurantApi {
    val retrofitService: RestaurantApiService by lazy {
        retrofit.create(RestaurantApiService::class.java)
    }
}