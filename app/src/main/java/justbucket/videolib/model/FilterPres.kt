package justbucket.videolib.model

import android.os.Parcel
import android.os.Parcelable

data class FilterPres(val text: String,
                      val sources: MutableList<Int>,
                      var isAllAnyCheck: Boolean = false,
                      val tags: MutableList<TagPres>) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createIntArray()?.toMutableList() ?: ArrayList(),
            parcel.readByte() != 0.toByte(),
            parcel.createTypedArrayList(TagPres.CREATOR))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeIntArray(sources.toIntArray())
        parcel.writeByte(if (isAllAnyCheck) 1 else 0)
        parcel.writeTypedList(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterPres> {
        override fun createFromParcel(parcel: Parcel): FilterPres {
            return FilterPres(parcel)
        }

        override fun newArray(size: Int): Array<FilterPres?> {
            return arrayOfNulls(size)
        }
    }
}