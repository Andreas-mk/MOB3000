package mob.smilefjesapp.dataklasse

import kotlinx.serialization.Serializable

/**
 * Dataklasse som brukes når vi henter kommune med koordinater
 */
@Serializable
data class GpsKommune (
    val kommunenavn: String,
    val kommunenummer: String
)
