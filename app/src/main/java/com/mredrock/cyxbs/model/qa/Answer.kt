package com.mredrock.cyxbs.model.qa

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.util.extensions.toDate

data class Answer(@SerializedName("comment_num")
                  var commentNum: String = "",
                  @SerializedName("photo_thumbnail_src")
                  val photoThumbnailSrc: String = "",
                  @SerializedName("praise_num")
                  var praiseNum: String = "",
                  @SerializedName("nickname")
                  val nickname: String = "",
                  @SerializedName("created_at")
                  val createdAt: String = "",
                  @SerializedName("id")
                  val id: String = "",
                  @SerializedName("photo_url")
                  val photoUrl: List<String>?,
                  @SerializedName("content")
                  val content: String = "",
                  @SerializedName("user_id")
                  val userId: String?,
                  @SerializedName("is_adopted")
                  val is_adopted: String = "",
                  @SerializedName("gender")
                  val gender: String = "",
                  @SerializedName("is_praised")
                  var is_praised: String = "") : Parcelable, Comparable<Answer> {
    val isAdopted get() = (is_adopted == "1")
    val isFemale get() = (gender == "女")
    val isPraised get() = (is_praised == "1")
    val praiseNumInt get() = praiseNum.toInt()
    val commentNumInt get() = commentNum.toInt()
    val hotValue get() = praiseNumInt + commentNumInt
    val time get() = createdAt.toDate("yyyy-MM-dd HH:mm:ss").time

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentNum)
        parcel.writeString(photoThumbnailSrc)
        parcel.writeString(praiseNum)
        parcel.writeString(nickname)
        parcel.writeString(createdAt)
        parcel.writeString(id)
        parcel.writeStringList(photoUrl)
        parcel.writeString(content)
        parcel.writeString(userId)
        parcel.writeString(is_adopted)
        parcel.writeString(gender)
        parcel.writeString(is_praised)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer {
            return Answer(parcel)
        }

        override fun newArray(size: Int): Array<Answer?> {
            return arrayOfNulls(size)
        }
    }

    override fun compareTo(other: Answer) = this.id.toInt() - other.id.toInt()
}