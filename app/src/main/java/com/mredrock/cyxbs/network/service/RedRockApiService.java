package com.mredrock.cyxbs.network.service;

import com.redrock.common.config.Const;
import com.mredrock.cyxbs.model.AboutMe;
import com.mredrock.cyxbs.model.ElectricCharge;
import com.mredrock.cyxbs.model.Empty;
import com.mredrock.cyxbs.model.Exam;
import com.mredrock.cyxbs.model.Food;
import com.mredrock.cyxbs.model.FoodComment;
import com.mredrock.cyxbs.model.FoodDetail;
import com.mredrock.cyxbs.model.Grade;
import com.mredrock.cyxbs.model.PastElectric;
import com.redrock.common.network.RedRockApiWrapper;
import com.mredrock.cyxbs.model.RollerViewInfo;
import com.mredrock.cyxbs.model.SchoolCarLocation;
import com.mredrock.cyxbs.model.Shake;
import com.mredrock.cyxbs.model.StartPage;
import com.mredrock.cyxbs.model.Student;
import com.mredrock.cyxbs.model.UpdateInfo;
import com.mredrock.cyxbs.model.social.BBDDDetail;
import com.mredrock.cyxbs.model.social.BBDDNews;
import com.mredrock.cyxbs.model.social.Comment;
import com.mredrock.cyxbs.model.social.HotNews;
import com.mredrock.cyxbs.model.social.OfficeNews;
import com.mredrock.cyxbs.model.social.PersonInfo;
import com.mredrock.cyxbs.model.social.PersonLatest;
import com.mredrock.cyxbs.model.social.RequestResponse;
import com.mredrock.cyxbs.model.social.Topic;
import com.mredrock.cyxbs.model.social.TopicApiWrapper;
import com.mredrock.cyxbs.model.social.TopicArticle;
import com.mredrock.cyxbs.model.social.UploadImgResponse;
import com.mredrock.cyxbs.network.setting.annotation.XmlApi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import io.reactivex.Observable;

/**
 * Created by cc on 16/1/20.
 */
public interface RedRockApiService {

    @GET(Const.API_UPDATE_OLD)
    @XmlApi
    Observable<UpdateInfo> update();

    @GET(Const.API_SCHOOL_CARS_LOCATION)
    Observable<SchoolCarLocation> schoolcar();

    @FormUrlEncoded
    @POST(Const.API_VERIFY)
    Observable<PersonInfo.UserWrapper> verify(@Field("stuNum") String stuNum, @Field("idNum") String idNum);

    //Explore start
    @FormUrlEncoded
    @POST(Const.API_MAP_PICTURE)
    Observable<RedRockApiWrapper<List<String>>> getMapOverlayImageUrl(@Field("name") String name, @Field("path") String path);

    @GET(Const.API_SHAKE)
    Observable<RedRockApiWrapper<Shake>> getShake();

    @FormUrlEncoded
    @POST(Const.API_FOOD_LIST)
    Observable<RedRockApiWrapper<List<Food>>> getFoodList(@Field("pid") String page);

    @FormUrlEncoded
    @POST(Const.API_FOOD_DETAIL)
    Observable<RedRockApiWrapper<FoodDetail>> getFoodDetail(@Field("id") String id);

    @FormUrlEncoded
    @POST(Const.API_FOOD_COMMENT_LIST)
    Observable<RedRockApiWrapper<List<FoodComment>>> getFoodComments(@Field("shop_id") String id,
                                                                     @Field("pid") String page);

    @FormUrlEncoded
    @POST(Const.API_SEND_FOOD_COMMENT)
    Observable<RedRockApiWrapper<Object>> sendFoodComment(@Field("shop_id") String id,
                                                          @Field("user_number") String userNumber,
                                                          @Field("user_password") String userPassword,
                                                          @Field("comment_content") String commentContent,
                                                          @Field("comment_author_name") String commentAuthoName);
    //Explore end

    @GET(Const.APT_SEARCH_STUDENT)
    Observable<Student.StudentWrapper> getStudent(@Query("stu") String stu);

//    @FormUrlEncoded
//    @POST(Const.API_EMPTYROOM)
//    Observable<Empty> getEmptyRoomList(@Field("buildNum") String buildNum,
//                                       @Field("week") String week,
//                                       @Field("weekdayNum") String weekdayNum,
//                                       @Field("sectionNum") String sectionNum);

    @FormUrlEncoded
    @POST(Const.API_EMPTYROOM)
    Call<Empty> getEmptyRoomListCall(@Field("week") int week,
                                     @Field("weekdayNum") int weekdayNum,
                                     @Field("buildNum") int buildNum,
                                     @Field("sectionNum") int sectionNum);

    @FormUrlEncoded
    @POST(Const.API_SCORE)
    Observable<Grade.GradeWrapper> getGrade(@Field("stuNum") String stuNum,
                                            @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_EXAM_SCHEDULE)
    Observable<Exam.ExamWapper> getExam(@Field("stuNum") String stu);

    @FormUrlEncoded
    @POST(Const.API_REEXAM_SCHEDULE)
    Observable<Exam.ExamWapper> getReExam(@Field("stu") String stu);

    @FormUrlEncoded
    @POST(Const.API_EDIT_INFO)
    Observable<RedRockApiWrapper> setPersonInfo(@Field("stuNum") String stuNum,
                                                @Field("idNum") String idNum,
                                                @Field("photo_thumbnail_src") String photo_thumbnail_src,
                                                @Field("photo_src") String photo_src);

    @FormUrlEncoded
    @POST(Const.API_EDIT_INFO)
    Observable<RedRockApiWrapper<Object>> setPersonNickName(@Field("stuNum") String stuNum,
                                                            @Field("idNum") String idNum,
                                                            @Field("nickname") String nickname);

    @FormUrlEncoded
    @POST(Const.API_EDIT_INFO)
    Observable<RedRockApiWrapper<Object>> setPersonQQ(@Field("stuNum") String stuNum,
                                                      @Field("idNum") String idNum,
                                                      @Field("qq") String qq);

    @FormUrlEncoded
    @POST(Const.API_EDIT_INFO)
    Observable<RedRockApiWrapper<Object>> setPersonPhone(@Field("stuNum") String stuNum,
                                                         @Field("idNum") String idNum,
                                                         @Field("phone") String phone);

    @FormUrlEncoded
    @POST(Const.API_EDIT_INFO)
    Observable<RedRockApiWrapper<Object>> setPersonIntroduction(@Field("stuNum") String stuNum,
                                                                @Field("idNum") String idNum,
                                                                @Field("introduction") String introduction);

    @FormUrlEncoded
    @POST(Const.API_GET_INFO)
    Observable<PersonInfo.UserWrapper> getPersonInfo(@Field("stuNum") String stuNum,
                                               @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_ABOUT_ME)
    Observable<AboutMe.AboutMeWapper> getAboutMe(@Field("stuNum") String
                                                         stuNum,
                                                 @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_TREND_DETAIL)
    Observable<BBDDDetail.BBDDDetailWrapper> getTrendDetail(@Field("type_id") int type_id,
                                                            @Field("article_id") String article_id);

    @FormUrlEncoded
    @POST(Const.API_SEARCH_ARTICLE)
    Observable<BBDDDetail.BBDDDetailWrapper> searchTrends(@Field("stuNum") String stuNum,
                                                          @Field("idNum") String idNum);

    Observable<BBDDDetail.BBDDDetailWrapper> searchOtherTrends(@Field("stuNum") String stuNum,
                                                               @Field("idNum") String idNum,
                                                               @Field("stunum_other") String stunum_other);

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_OFFICIAL_NEWS_LIST)
    Observable<OfficeNews> getSocialOfficialNewsList(@Field("size") int size,
                                                     @Field("page") int page);

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_HOT_LIST)
    Observable<List<HotNews>> getSocialHotList(@Field("size") int size,
                                               @Field("page") int page);

    @FormUrlEncoded
    //哔哔叨叨(或者其他的)接口：POST
    @POST(Const.API_SOCIAL_BBDD_LIST)
    Observable<BBDDNews> getSocialBBDDList(@Field("type_id") int type_id,
                                           @Field("size") int size,
                                           @Field("page") int page);


    @Multipart
    @POST(Const.API_SOCIAL_IMG_UPLOAD)
    Observable<UploadImgResponse> uploadSocialImg(@Part("stunum") RequestBody stunum,
                                                  @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST(Const.API_SOCIAL_ARTICLE_ADD)
    Observable<RequestResponse> sendDynamic(@Field("type_id") int type_id,
                                            @Field("title") String title,
                                            @Field("user_id") String user_id,
                                            @Field("content") String content,
                                            @Field("thumbnail_src") String thumbnail_src,
                                            @Field("photo_src") String photo_src,
                                            @Field("stuNum") String stuNum,
                                            @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_COMMENT_LIST)
    Observable<Comment> getSocialCommentList(@Field("article_id") String article_id,
                                             @Field("type_id") int type_id);


    @FormUrlEncoded
    @POST(Const.API_SOCIAL_COMMENT_ADD)
    Observable<RequestResponse> addSocialComment(@Field("article_id") String article_id,
                                                 @Field("type_id") int type_id,
                                                 @Field("content") String content,
                                                 @Field("user_id") String user_id,
                                                 @Field("stuNum") String stuNum,
                                                 @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_LIKE)
    Observable<RequestResponse> socialLike(@Field("article_id") String article_id,
                                           @Field("type_id") int type_id,
                                           @Field("stuNum") String stuNum,
                                           @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_UNLIKE)
    Observable<RequestResponse> socialUnlike(@Field("article_id") String article_id,
                                             @Field("type_id") int type_id,
                                             @Field("stuNum") String stuNum,
                                             @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_GET_PERSON_INFO)
    Observable<RedRockApiWrapper<PersonInfo>> getPersonInfo(@Field("stunum_other") String otherStuNum,
                                                            @Field("stuNum") String stuNum,
                                                            @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_GET_PERSON_LATEST)
    Observable<RedRockApiWrapper<List<PersonLatest>>> getPersonLatestList(@Field("stunum_other") String otherStuNum);


    @POST(Const.API_START_PAGE)
    Observable<RedRockApiWrapper<List<StartPage>>> startPage();

    @FormUrlEncoded
    @POST(Const.API_ELECTRIC_CHARGE)
    Observable<ElectricCharge.ElectricChargeWrapper> queryElectricCharge(@Field("building") String building, @Field("room") String room);

    @FormUrlEncoded
    @POST(Const.API_BIND_DORMITORY)
    Observable<RedRockApiWrapper<Object>> bindDormitory(@Field("stuNum")String stuNum, @Field("idNum") String idNum, @Field("room") String room);

    @FormUrlEncoded
    @POST(Const.API_ELECTRIC_QUERY_STUNUM)
    Observable<RedRockApiWrapper<PastElectric.PastElectricResultWrapper>> getPastElectricCharge(@Field("stuNum")String stuNum, @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_ALL_TOPIC_LIST)
    Observable<TopicApiWrapper<List<Topic>>> getAllTopicList(@Field("stuNum") String stuNum,
                                                             @Field("idNum") String idNum,
                                                             @Field("size") int size,
                                                             @Field("page") int page);

    @FormUrlEncoded
    @POST(Const.API_MY_TOPIC_LIST)
    Observable<TopicApiWrapper<List<Topic>>> getMyTopicList(@Field("stuNum") String stuNum,
                                                            @Field("idNum") String idNum,
                                                            @Field("size") int size,
                                                            @Field("page") int page);

    @FormUrlEncoded
    @POST(Const.API_ALL_TOPIC_LIST)
    Observable<TopicApiWrapper<List<Topic>>> searchTopic(@Field("stuNum") String stuNum,
                                                         @Field("idNum") String idNum,
                                                         @Field("size") int size,
                                                         @Field("page") int page,
                                                         @Field("searchKeyword") String searchKeyword);

    @FormUrlEncoded
    @POST(Const.API_TOPIC_ARTICLE)
    Observable<TopicApiWrapper<TopicArticle>> getTopicArticle(@Field("stuNum") String stuNum,
                                                              @Field("idNum") String idNum,
                                                              @Field("size") int size,
                                                              @Field("page") int page,
                                                              @Field("topic_id") int topicId);

    @FormUrlEncoded
    @POST(Const.API_ADD_TOPIC_ARTICLE)
    Observable<RequestResponse> sendTopicArticle(@Field("topic_id") int topic_id,
                                                 @Field("title") String title,
                                                 @Field("content") String content,
                                                 @Field("thumbnail_src") String thumbnail_src,
                                                 @Field("photo_src") String photo_src,
                                                 @Field("stuNum") String stuNum,
                                                 @Field("idNum") String idNum,
                                                 @Field("official") boolean official);
    @FormUrlEncoded
    @POST(Const.API_ROLLER_VIEW)
    Observable<RedRockApiWrapper<List<RollerViewInfo>>> getRollerViewInfo(@Field("pic_num") String pic_num);
}