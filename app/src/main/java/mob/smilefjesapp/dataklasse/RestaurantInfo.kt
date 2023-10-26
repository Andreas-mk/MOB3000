package mob.smilefjesapp.dataklasse

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Dataklasse med verdiene som hentes fra JSON-data (fra APIet).
 * Dette er all dataen vi presenterer om restaurantene.
 * Fra APIet får vi et "entries" objekt som inneholder liste med restaurantobjekter.
 */

@Serializable
data class ApiResponse(
    val entries: List<RestaurantInfo>
)

@Serializable
data class RestaurantInfo(
    val navn: String,
    val adrlinje1: String,
    val postnr: String,
    val poststed: String,
    @SerialName(value = "total_karakter")
    val totalKarakter: String,
    @SerialName(value = "tema1_no")
    val tema1: String,
    val karakter1: String,
    @SerialName(value = "tema2_no")
    val tema2: String,
    val karakter2: String,
    @SerialName(value = "tema3_no")
    val tema3: String,
    val karakter3: String,
    @SerialName(value = "tema4_no")
    val tema4: String,
    val karakter4: String
)