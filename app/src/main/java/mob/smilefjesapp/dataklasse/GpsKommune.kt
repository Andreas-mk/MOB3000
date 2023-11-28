package mob.smilefjesapp.dataklasse

import kotlinx.serialization.Serializable
@Serializable
data class GpsKommune (
    val kommunenavn: String,
    val kommunenummer: String
)
