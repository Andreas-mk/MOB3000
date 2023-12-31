package mob.smilefjesapp.dataklasse

import android.os.Parcel
import android.os.Parcelable
/**
 * Her er dataklassen for info om Fylker, og inndataen er fylkesnavn og fylkesnummer
 * Derfor er eneste variabler.
 * De er her gjort "Parcelable" som gjør at de kan sendes fra activity til activity
 * Dette gjør det mulig å bruke " intent.putExtra() og sende med key-value internt i activity.
 * Dette er gjort ved hjelp av
 * https://medium.com/the-lazy-coders-journal/easy-parcelable-in-kotlin-the-lazy-coders-way-9683122f4c00 hentet den 02/11/2023 - 21.30, Zahidur Rahman Faisal
 */
data class FylkeInfo(
    val fylkesnavn: String,
    val fylkesnummer: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fylkesnavn)
        parcel.writeInt(fylkesnummer)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<FylkeInfo> {
        override fun createFromParcel(parcel: Parcel): FylkeInfo {
            return FylkeInfo(parcel)
        }

        override fun newArray(size: Int): Array<FylkeInfo?> {
            return arrayOfNulls(size)
        }
    }
}
