package com.redrock.schedule.network

import com.redrock.common.config.Const
import com.redrock.common.network.RedRockApiWrapper
import com.redrock.schedule.model.AffairApi
import com.redrock.schedule.model.Course

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ScheduleApiService {
    @FormUrlEncoded
    @POST(Const.API_GET_AFFAIR)
    fun getAffair(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<AffairApi<List<AffairApi.AffairItem>>>

    @FormUrlEncoded
    @POST(Const.API_ADD_AFFAIR)
    fun addAffair(@Field("id") id: String, @Field("stuNum") stuNum: String, @Field("idNum") idNum: String,
                  @Field("date") date: String, @Field("time") time: Int, @Field("title") title: String,
                  @Field("content") content: String): Observable<RedRockApiWrapper<Any>>

    @FormUrlEncoded
    @POST(Const.API_EDIT_AFFAIR)
    fun editAffair(@Field("id") id: String, @Field("stuNum") stuNum: String, @Field("idNum") idNum: String,
                   @Field("date") date: String, @Field("time") time: Int, @Field("title") title: String,
                   @Field("content") content: String): Observable<RedRockApiWrapper<Any>>

    @FormUrlEncoded
    @POST(Const.API_DELETE_AFFAIR)
    fun deleteAffair(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String, @Field("id") id: String): Observable<RedRockApiWrapper<Any>>


    @FormUrlEncoded
    @Headers("API_APP: android")
    @POST(Const.API_PERSON_SCHEDULE)
    fun getCourse(@Field("stuNum") stuNum: String,
                           @Field("idNum") idNum: String,
                           @Field("week") week: String): Observable<Course.CourseWrapper>

    @FormUrlEncoded
    @Headers("API_APP: android")
    @POST(Const.API_PERSON_SCHEDULE)
    fun getCourseCall(@Field("stuNum") stuNum: String,
                               @Field("idNum") idNum: String,
                               @Field("week") week: String,
                               @Field("forceFetch") forceFetch: Boolean): retrofit2.Call<Course.CourseWrapper>
}
