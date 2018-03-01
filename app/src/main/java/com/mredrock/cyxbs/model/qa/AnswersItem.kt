package com.mredrock.cyxbs.model.qa

import com.google.gson.annotations.SerializedName

data class AnswersItem(@SerializedName("comment_num")
                       val commentNum: String = "",
                       @SerializedName("photo_thumbnail_src")
                       val photoThumbnailSrc: String = "",
                       val is_adopted: String = "",
                       @SerializedName("praise_num")
                       var praiseNum: String = "",
                       val nickname: String = "",
                       @SerializedName("created_at")
                       val createdAt: String = "",
                       val id: String = "",
                       @SerializedName("photo_url")
                       val photoUrl: List<String>?,
                       val content: String = "",
                       val gender: String = "",
                       var is_praised: String = "") {
    val isAdopted get() = (is_adopted == "1")
    val isFemale get() = (gender == "女")
    val isPraised get() = (is_praised == "1")
    val praiseNumInt get() = praiseNum.toInt()
    val commentNumInt get() = commentNum.toInt()
    val hotValue get() = praiseNumInt + commentNumInt
}