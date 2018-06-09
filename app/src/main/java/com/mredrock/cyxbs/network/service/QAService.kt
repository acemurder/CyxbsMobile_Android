package com.mredrock.cyxbs.network.service

import com.mredrock.cyxbs.config.Const
import com.mredrock.cyxbs.model.BaseRedrockApiWrapper
import com.mredrock.cyxbs.model.RedrockApiWrapper
import com.mredrock.cyxbs.model.qa.*
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
                     @Field("answer_id") aid: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.CANCEL_PRAISE_ANSWER)
    fun cancelPraiseAnswer(@Field("stuNum") stuNum: String,
                           @Field("idNum") idNum: String,
                           @Field("answer_id") aid: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.CANCEL_PRAISE_ANSWER)
    fun acceptAnswer(@Field("stuNum") stuNum: String,
                     @Field("idNum") idNum: String,
                     @Field("answer_id") aid: String,
                     @Field("question_id") qid: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.ANSWER_QUESTION)
    fun answerQuestion(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("question_id") qid: String,
                       @Field("content") content: String): Observable<RedrockApiWrapper<String>>

    @Multipart
    @POST()
    fun uploadAnswerPic(@Part parts: List<MultipartBody.Part>): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.CANCEL_QUESTION)
    fun cancelQuestion(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("question_id") qid: String): Observable<BaseRedrockApiWrapper>

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
                      @Field("content") content: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.ADD_DRAFT)
    fun addDraft(@Field("stunum") stuNum: String,
                 @Field("idnum") idNum: String,
                 @Field("type") type: String,
                 @Field("content") content: String,
                 @Field("target_id") targetId: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.REFRESH_DRAFT)
    fun refreshDraft(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("content") content: String,
                     @Field("id") draftId: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.DELETE_DRAFT)
    fun deleteDraft(@Field("stunum") stuNum: String,
                    @Field("idnum") idNum: String,
                    @Field("id") id: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.DRAFT_LIST)
    fun getDraftList(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("page") page: Int,
                     @Field("size") size: Int): Observable<RedrockApiWrapper<List<Draft>>>

    @FormUrlEncoded
    @POST(Const.CHECK_IN)
    fun checkIn(@Field("stunum") stuNum: String,
                @Field("idnum") idNum: String): Observable<BaseRedrockApiWrapper>

    @FormUrlEncoded
    @POST(Const.CHECK_IN_STATUS)
    fun getCheckInStatus(@Field("stunum") stuNum: String,
                         @Field("idnum") idNum: String): Observable<RedrockApiWrapper<CheckInStatus>>

    @FormUrlEncoded
    @POST(Const.RELATE_ME_LIST)
    fun getRelateMeList(@Field("stunum") stuNum: String,
                        @Field("idnum") idNum: String,
                        @Field("page") page: Int,
                        @Field("size") size: Int,
                        @Field("type") type: Int): Observable<RedrockApiWrapper<List<RelateMeItem>>>
}