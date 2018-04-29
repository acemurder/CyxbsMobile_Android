package com.mredrock.cyxbs.network.service

import com.mredrock.cyxbs.config.Const
import com.mredrock.cyxbs.model.RedrockApiWrapper
import com.mredrock.cyxbs.model.qa.Answer
import com.mredrock.cyxbs.model.qa.QuestionDetail
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By jay68 on 2018/4/22.
 */
interface QAService {
    @FormUrlEncoded
    @POST(Const.ANSWER_LIST)
    fun getAnswerList(@Field("stuNum") stuNum: String,
                      @Field("idNum") idNum: String,
                      @Field("question_id") qid: String): Observable<RedrockApiWrapper<QuestionDetail>>

    @FormUrlEncoded
    @POST(Const.LOAD_MORE_ANSWER)
    fun loadMoreAnswer(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("question_id") qid: String,
                       @Field("page") page: Int,
                       @Field("size") size: String): Observable<RedrockApiWrapper<List<Answer>>>

    @FormUrlEncoded
    @POST(Const.PRAISE_ANSWER)
    fun praiseAnswer(@Field("stuNum") stuNum: String,
                     @Field("idNum") idNum: String,
                     @Field("answer_id") aid: String): Observable<RedrockApiWrapper<Unit>>

    @FormUrlEncoded
    @POST(Const.CANCEL_PRAISE_ANSWER)
    fun cancelPraiseAnswer(@Field("stuNum") stuNum: String,
                           @Field("idNum") idNum: String,
                           @Field("answer_id") aid: String): Observable<RedrockApiWrapper<Unit>>

    @FormUrlEncoded
    @POST(Const.CANCEL_PRAISE_ANSWER)
    fun acceptAnswer(@Field("stuNum") stuNum: String,
                     @Field("idNum") idNum: String,
                     @Field("answer_id") aid: String,
                     @Field("question_id") qid: String): Observable<RedrockApiWrapper<Unit>>

    @FormUrlEncoded
    @POST(Const.ANSWER_QUESTION)
    fun answerQuestion(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("question_id") qid: String,
                       @Field("content") content: String): Observable<RedrockApiWrapper<String>>

    @Multipart
    @POST()
    fun uploadAnswerPic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiWrapper<Unit>>

    @FormUrlEncoded
    @POST(Const.CANCEL_QUESTION)
    fun cancelQuestion(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("question_id") qid: String): Observable<RedrockApiWrapper<Unit>>

    @FormUrlEncoded
    @POST(Const.ANSWER_COMMENT_LIST)
    fun getAnswerCommentList(@Field("stuNum") stuNum: String,
                             @Field("idNum") idNum: String,
                             @Field("answer_id") aid: String): Observable<RedrockApiWrapper<List<Answer>>>

    @FormUrlEncoded
    @POST(Const.COMMENT_ANSWER)
    fun commentAnswer(@Field("stuNum") stuNum: String,
                      @Field("idNum") idNum: String,
                      @Field("answer_id") aid: String,
                      @Field("content") content: String): Observable<RedrockApiWrapper<Unit>>
}