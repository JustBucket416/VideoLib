package justbucket.videolib.model

import android.os.Parcel
import android.os.Parcelable

data class TagPres(val id: Long,
                   val text: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagPres> {
        override fun createFromParcel(parcel: Parcel): TagPres {
            return TagPres(parcel)
        }

        override fun newArray(size: Int): Array<TagPres?> {
            return arrayOfNulls(size)
        }
    }
}