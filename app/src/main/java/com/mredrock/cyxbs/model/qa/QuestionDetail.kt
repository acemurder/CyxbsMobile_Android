package com.mredrock.cyxbs.model.qa

import android.text.Html
import android.text.Spanned
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.util.extensions.toDate

data class QuestionDetail(var id: String,
                          @SerializedName("reward")
                          val reward: String = "",
                          @SerializedName("title")
                          val title: String = "",
                          @SerializedName("answers")
                          val answers: List<Answer>?,
                          @SerializedName("description")
                          val description: String = "",
                          @SerializedName("disappear_at")
                          val disappearAt: String = "",
                          @SerializedName("questioner_nickname")
                          val questionerNickname: String = "",
                          @SerializedName("questioner_photo_thumbnail_src")
                          val questionerPhotoThumbnailSrc: String = "",
                          @SerializedName("photo_urls")
                          val photoUrls: List<String>?,
                          @SerializedName("tags")
                          val tags: String = "",
                          @SerializedName("is_self")
                          val is_self: Int = 0,
                          @SerializedName("kind")
                          val kind: String = "",
                          @SerializedName("gender")
                          val gender: String = "") {
    var hasAdoptedAnswer = false

    val isSelf get() = (is_self == 1)
    val isFemale get() = (gender == "女")
    val spannableDescription: Spanned
        get() = if (tags.isBlank()) {
            Html.fromHtml(description)
        } else {
            val html = "<font color=\"#7195fa\">#$tags#</font> $description"
            Html.fromHtml(html)
        }

    fun hasAnswer() = (answers != null && answers.isNotEmpty())
    fun shouldShowGenderIcon() = (kind == "情感")

    fun sortByDefault() {
        answers?.sortedWith(Comparator { o1, o2 -> compareValuesBy(o1, o2, Answer::isAdopted, Answer::hotValue) })
    }

    fun sortByTime() {
        answers?.sortedBy { -it.createdAt.toDate("yyyy-MM-dd HH:mm:ss").time }
    }
}