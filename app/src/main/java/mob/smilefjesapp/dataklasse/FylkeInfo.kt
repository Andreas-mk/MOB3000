package mob.smilefjesapp.dataklasse

import android.os.Parcel
import android.os.Parcelable

// Denne brukes til hvordan json fil fra API ser ut og kan da brukes
data class FylkeInfo(
    val fylkesnavn: String,
    val fylkesnummer: Int
) : Parcelable {
    // https://medium.com/the-lazy-coders-journal/easy-parcelable-in-kotlin-the-lazy-coders-way-9683122f4c00 11/02/2023 - 21.30
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
