package justbucket.videolib.model

import android.os.Parcel
import android.os.Parcelable

data class VideoPres(val id: Long,
                     val title: String,
                     val videoPath: String,
                     val thumbPath: String,
                     val source: Int,
                     var tags: MutableList<TagPres>,
                     var selected: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.createTypedArrayList(TagPres.CREATOR),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(videoPath)
        parcel.writeString(thumbPath)
        parcel.writeInt(source)
        parcel.writeTypedList(tags)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoPres> {
        override fun createFromParcel(parcel: Parcel): VideoPres {
            return VideoPres(parcel)
        }

        override fun newArray(size: Int): Array<VideoPres?> {
            return arrayOfNulls(size)
        }
    }
}


