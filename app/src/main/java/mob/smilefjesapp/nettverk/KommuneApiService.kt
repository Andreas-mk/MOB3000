package mob.smilefjesapp.nettverk


import mob.smilefjesapp.dataklasse.KommuneInfo

import retrofit2.Call

import retrofit2.http.GET


interface KommuneApiService {
    @GET("kommuneinfo/v1/kommuner")
    fun hentKommune(): Call<List<KommuneInfo>>
}
