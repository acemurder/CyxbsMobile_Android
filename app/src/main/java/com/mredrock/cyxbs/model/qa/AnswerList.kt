package com.mredrock.cyxbs.model.qa

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.util.extensions.toDate

data class AnswerList(val reward: String = "",
                      val title: String = "",
                      val is_self: Int = 0,
                      val answers: List<AnswersItem>?,
                      val description: String = "",
                      @SerializedName("disappear_at")
                      val disappearAt: String = "",
                      @SerializedName("questioner_nickname")
                      val questionerNickname: String = "",
                      @SerializedName("questioner_photo_thumbnail_src")
                      val questionerPhotoThumbnailSrc: String = "",
                      @SerializedName("photo_urls")
                      val photoUrls: List<String>?,
                      val tags: String = "",
                      val kind: String = "",
                      val gender: String = "") {
    var hasAdoptedAnswer = false

    val isSelf get() = (is_self == 1)
    val isFemale get() = (gender == "女")

    fun hasAnswer() = (answers != null && answers.isNotEmpty())
    fun shouldShowGenderIcon() = (kind == "情感")

    fun sortByDefault() {
        answers?.sortedWith(Comparator { o1, o2 -> compareValuesBy(o1, o2, AnswersItem::is_adopted, AnswersItem::hotValue) })
    }

    fun sortByTime() {
        answers?.sortedBy { -it.createdAt.toDate("yyyy-MM-dd HH:mm:ss").time }
    }
}