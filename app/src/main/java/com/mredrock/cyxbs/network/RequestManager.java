package com.mredrock.cyxbs.network;

import android.content.Context;

import com.mredrock.cyxbs.component.remind_service.Reminder;
import com.mredrock.cyxbs.component.remind_service.func.BaseRemindFunc;
import com.redrock.common.network.RedRockApiWrapperFunc;
import com.redrock.common.ContextProvider;
import com.redrock.common.account.AccountManager;
import com.redrock.common.config.Const;
import com.redrock.common.account.AskLoginEvent;
import com.mredrock.cyxbs.model.AboutMe;
import com.mredrock.cyxbs.model.Affair;
import com.mredrock.cyxbs.model.Course;
import com.mredrock.cyxbs.model.ElectricCharge;
import com.mredrock.cyxbs.model.Empty;
import com.mredrock.cyxbs.model.EmptyRoom;
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
import com.mredrock.cyxbs.model.UpdateInfo;
import com.redrock.common.account.User;
import com.mredrock.cyxbs.model.VolunteerTime;
import com.mredrock.cyxbs.model.lost.Lost;
import com.mredrock.cyxbs.model.lost.LostDetail;
import com.mredrock.cyxbs.model.lost.LostStatus;
import com.mredrock.cyxbs.model.lost.LostWrapper;
import com.mredrock.cyxbs.model.social.BBDDNewsContent;
import com.mredrock.cyxbs.model.social.CommentContent;
import com.mredrock.cyxbs.model.social.HotNews;
import com.mredrock.cyxbs.model.social.OfficeNewsContent;
import com.mredrock.cyxbs.model.social.PersonInfo;
import com.mredrock.cyxbs.model.social.PersonLatest;
import com.mredrock.cyxbs.model.social.Topic;
import com.mredrock.cyxbs.model.social.TopicArticle;
import com.mredrock.cyxbs.model.social.UploadImgResponse;
import com.redrock.common.network.RedrockApiException;
import com.mredrock.cyxbs.network.func.AffairTransformFunc;
import com.mredrock.cyxbs.network.func.AffairWeekFilterFunc;
import com.mredrock.cyxbs.network.func.ElectricQueryFunc;
import com.mredrock.cyxbs.network.func.StartPageFunc;
import com.mredrock.cyxbs.network.func.UpdateVerifyFunc;
import com.mredrock.cyxbs.network.func.UserCourseFilterFunc;
import com.mredrock.cyxbs.network.func.UserInfoVerifyFunc;
import com.mredrock.cyxbs.network.service.RedRockApiService;
import com.mredrock.cyxbs.network.observable.CourseListProvider;
import com.mredrock.cyxbs.network.observable.EmptyRoomListProvider;
import com.mredrock.cyxbs.network.service.LostApiService;
import com.mredrock.cyxbs.network.service.VolunteerService;
import com.mredrock.cyxbs.network.setting.CacheProviders;
import com.mredrock.cyxbs.ui.activity.lost.LostActivity;
import com.mredrock.cyxbs.ui.fragment.social.TopicFragment;
import com.mredrock.cyxbs.util.BitmapUtil;
import com.mredrock.cyxbs.util.SchoolCalendar;
import com.redrock.common.utils.Utils;
import com.redrock.common.network.RequestProvider;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.JacksonSpeaker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;


public enum RequestManager {

    INSTANCE;

    private RedRockApiService redRockApiService;
    private LostApiService lostApiService;
    private VolunteerService volunteerService;
    private CacheProviders cacheProviders;

    RequestManager() {
        Retrofit retrofit = RequestProvider.INSTANCE.getRetrofit();
        redRockApiService = retrofit.create(RedRockApiService.class);
        lostApiService = retrofit.create(LostApiService.class);
        volunteerService = retrofit.create(VolunteerService.class);
        cacheProviders = new RxCache.Builder()
                .persistence(ContextProvider.getContext().getFilesDir(), new JacksonSpeaker())
                .using(CacheProviders.class);
    }

    public static RequestManager getInstance() {
        return INSTANCE;
    }

    public void checkUpdate(Observer<UpdateInfo> observer, int versionCode) {

        Observable<UpdateInfo> observable = redRockApiService.update()
                .map(new UpdateVerifyFunc(versionCode));

        emitObservable(observable, observer);
    }

    public void login(Observer<User> observer, String stuNum, String idNum) {
        Observable<User> observable = redRockApiService.verify(stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>())
                .zipWith(redRockApiService.getPersonInfo(stuNum, idNum)
                        .map(new RedRockApiWrapperFunc<>()), User::cloneFromUserInfo);

        emitObservable(observable, observer);
    }

    public void getNowWeek(Observer<Integer> observer, String stuNum, String idNum) {
        Observable<Integer> observable = redRockApiService.getCourse(stuNum, idNum, "0")
                .map(courseWrapper -> {
                    if (courseWrapper.status != Const.RED_ROCK_API_STATUS_SUCCESS) {
                        throw new RedrockApiException();
                    }
                    return Integer.parseInt(courseWrapper.nowWeek);
                });
         emitObservable(observable, observer);
    }

    public void getVolunteer(Observer<VolunteerTime> subscriber, String account, String password) {
        Observable<VolunteerTime> observable = volunteerService.getVolunteerUseLogin(account, password);
        emitObservable(observable, subscriber);
    }

    public void getVolunteerTime(Observer<VolunteerTime.DataBean> subscriber, String uid) {
        Observable<VolunteerTime.DataBean> observable = volunteerService.getVolunteerUseUid(uid)
                .map(VolunteerTime::getData);
        emitObservable(observable, subscriber);
    }

    public void getSchoolCarLocation(Observer<SchoolCarLocation> subscriber) {
        Observable<SchoolCarLocation> observable = redRockApiService.schoolcar();
        emitObservable(observable, subscriber);
    }

    public void getCourseList(Observer<List<Course>> observer, String stuNum, String idNum, int week, boolean update) {
//        Observable<List<Course>> observable = CourseListProvider.start(stuNum, idNum, update,false)
//                .map(new UserCourseFilterFunc(week));

        getCourseList(observer, stuNum, idNum, week, update, true);
    }

    public void getCourseList(Observer<List<Course>> observer, String stuNum, String idNum,
                                             int week, boolean update, boolean forceFetch) {
        Observable<List<Course>> observable = CourseListProvider.start(stuNum, idNum, update, forceFetch)
                .map(new UserCourseFilterFunc(week));
         emitObservable(observable, observer);
    }

    public void getRemindableList(Observer<List<Reminder>> observer, Context context, BaseRemindFunc remindFunc) {
        Observable<List<Reminder>> observable = CourseListProvider.start(AccountManager.getUser().stuNum, AccountManager.getUser().idNum, false, false)
                .map(new UserCourseFilterFunc(new SchoolCalendar()
                        .getWeekOfTerm()))
                .map(remindFunc);
         emitObservable(observable, observer);
    }

    public Observable<List<Course>> getCourseList(String stuNum, String idNum) {
        return redRockApiService.getCourse(stuNum, idNum, "0").map(new RedRockApiWrapperFunc<>());
    }

    public List<Course> getCourseListSync(String stuNum, String idNum, boolean forceFetch) throws IOException {
        Response<Course.CourseWrapper> response = redRockApiService.getCourseCall(stuNum, idNum, "0", forceFetch).execute();
        return response.body().data;
    }

//    public CompositeDisposable getMapOverlayImageUrl(Observer<String> observer, String name, String path) {
//        Observable<String> observable = redRockApiService.getMapOverlayImageUrl(name, path)
//                .map(wrapper -> {
//                    if (wrapper.status != 204) {
//                        throw new RedrockApiException(wrapper.info);
//                    } else {
//                        return wrapper.data;
//                    }
//                })
//                .flatMap(urlList -> Observable.just(urlList.get(0)));
//        return emitObservable(observable, observer);
//    }

    public Disposable getShake(DisposableObserver<Shake> observer) {
        Observable<Shake> observable = redRockApiService.getShake()
                .map(new RedRockApiWrapperFunc<>());

        return emitObservable(observable, observer);
    }

    public Disposable getFoodList(DisposableObserver<List<Food>> observer, String page, String defaultIntro) {
        Observable<List<Food>> observable = redRockApiService.getFoodList(page)
                .map(new RedRockApiWrapperFunc<>())
                .filter(Utils::checkNotNullAndNotEmpty)
                .flatMap(foodList -> {
                    for (Food food : foodList) {
                        redRockApiService.getFoodDetail(food.id)
                                .map(new RedRockApiWrapperFunc<>())
                                .filter(foodDetail -> foodDetail != null)
                                .onErrorReturn(throwable -> {
                                    FoodDetail defaultFoodDetail = new FoodDetail();
                                    defaultFoodDetail.shop_content = defaultIntro;
                                    return defaultFoodDetail;
                                })
                                .doOnNext(foodDetail -> foodDetail.shop_content =
                                        foodDetail.shop_content
                                                .replaceAll("\t", "")
                                                .replaceAll("\r\n", ""))
                                .subscribe(foodDetail -> {
                                    food.introduction = foodDetail.shop_content;
                                });
                    }

                    return Observable.just(foodList);
                });

        return emitObservable(observable, observer);
    }


    public Disposable getFoodAndCommentList(DisposableObserver<FoodDetail> observer, String shopId, String page) {

        Observable<FoodDetail> observable = redRockApiService.getFoodDetail(shopId)
                .map(new RedRockApiWrapperFunc<>())
                .filter(foodDetail -> foodDetail != null)
                .doOnNext(foodDetail -> {
                    foodDetail.shop_content = foodDetail.shop_content.replaceAll("\t", "").replaceAll("\r\n", "");
                    foodDetail.shop_tel = foodDetail.shop_tel.trim();
                })
                .flatMap(foodDetail -> {
                    redRockApiService.getFoodComments(shopId, page)
                            .map(new RedRockApiWrapperFunc<>())
                            .filter(Utils::checkNotNullAndNotEmpty)
                            .onErrorReturn(throwable -> new ArrayList<>())
                            .flatMap(Observable::fromIterable)
                            .toSortedList()
                            .subscribe(foodCommentList -> {
                                foodDetail.foodComments = foodCommentList;
                            });
                    return Observable.just(foodDetail);
                });

        return emitObservable(observable, observer);
    }

    public Disposable getFood(DisposableObserver<FoodDetail> observer, String restaurantKey) {
        Observable<FoodDetail> observable = redRockApiService.getFoodDetail(restaurantKey)
                .map(new RedRockApiWrapperFunc<>());
        return emitObservable(observable, observer);
    }

    public Disposable sendCommentAndRefresh(DisposableObserver<List<FoodComment>> observer, String shopId, String userId, String userPassword, String content, String authorName) {
        Observable<RedRockApiWrapper<Object>> sendObservable = redRockApiService.sendFoodComment(shopId, userId, userPassword, content, authorName);
        Observable<List<FoodComment>> foodCommentObservable =
                redRockApiService.getFoodComments(shopId, "1").map(new RedRockApiWrapperFunc<>())
                        .filter(Utils::checkNotNullAndNotEmpty)
                        .flatMap(Observable::fromIterable)
                        .toSortedList()
                        .toObservable();
        Observable<List<FoodComment>> observable = Observable.zip(sendObservable, foodCommentObservable,
                (wrapper, foodCommentList) -> {
                    if (wrapper.status == Const.RED_ROCK_API_STATUS_SUCCESS) {
                        return foodCommentList;
                    } else {
                        return null;
                    }
                });

        return emitObservable(observable, observer);
    }

    public Disposable getFoodCommentList(DisposableObserver<List<FoodComment>> observer
            , String shopId, String page) {
        Observable<List<FoodComment>> observable =
                redRockApiService.getFoodComments(shopId, page)
                        .map(new RedRockApiWrapperFunc<>())
                        .filter(Utils::checkNotNullAndNotEmpty)
                        .flatMap(Observable::fromIterable)
                        .toSortedList()
                        .toObservable();

        return emitObservable(observable, observer);
    }

    public void getPublicCourse(Observer<List<Course>> observer,
                                List<String> stuNumList, String week) {
        Observable<List<Course>> observable = Observable.fromIterable(stuNumList)
                .flatMap(s -> redRockApiService.getCourse(s, "", week))
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }


    public void getStudent(Observer<List<com.mredrock.cyxbs.model.Student>> observer,
                           String stu) {
        Observable<List<com.mredrock.cyxbs.model.Student>> observable = redRockApiService.getStudent(stu)
                .map(studentWrapper -> studentWrapper.data);
        emitObservable(observable, observer);
    }

//    public void getEmptyRoomList(Subscriber<List<String>> subscriber, String
//            buildNum, String week, String weekdayNum, String sectionNum) {
//        Observable<List<String>> observable = redRockApiService
//                .getEmptyRoomList(buildNum, week, weekdayNum, sectionNum)
//                .map(new RedRockApiWrapperFunc<>());
//        emitObservable(observable, subscriber);
//    }

    public void queryEmptyRoomList(Observer<List<EmptyRoom>> observer, int week, int weekday, int build, int[] sections) {
        Observable<List<EmptyRoom>> observable = EmptyRoomListProvider.INSTANCE
                .createObservable(week, weekday, build, sections);
        emitObservable(observable, observer);
    }

    public List<String> getEmptyRoomListSync(int week, int weekday, int build, int section) throws IOException {
        Response<Empty> response = redRockApiService.getEmptyRoomListCall(week, weekday, build, section).execute();
        return response.body().data;
    }

    public void getGradeList(Observer<List<Grade>> observer, String
            stuNum, String stuId, boolean update) {
        Observable<List<Grade>> observable = redRockApiService.getGrade(stuNum, stuId)
                .map(new RedRockApiWrapperFunc<>());
        cacheProviders.getCachedGradeList(observable, new DynamicKey
                (stuNum), new EvictDynamicKey(update))
                .map(Reply::getData);
        emitObservable(observable, observer);
    }

    public void getExamList(Observer<List<Exam>> observer, String
            stu, boolean update) {
        Observable<List<Exam>> observable = redRockApiService.getExam(stu).map(
                examWapper -> examWapper.data);
        cacheProviders.getCachedExamList(observable, new DynamicKey(stu), new
                EvictDynamicKey(update))
                .map(Reply::getData);
        emitObservable(observable, observer);
    }

    public void getReExamList(Observer<List<Exam>> observer, String
            stu, boolean update) {
        Observable<List<Exam>> observable = redRockApiService.getReExam(stu).map(
                examWapper -> examWapper.data);
        cacheProviders.getCachedExamList(observable, new DynamicKey(stu), new
                EvictDynamicKey(update))
                .map(Reply::getData);
        emitObservable(observable, observer);
    }

    public void getAboutMeList(Observer<List<AboutMe>> observer, String stuNum, String idNum) {
        Observable<List<AboutMe>> observable = redRockApiService.getAboutMe(stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void getTrendDetail(Observer<List<HotNews>> observer, int type_id, String article_id) {
        List<HotNews> newsList = new ArrayList<>();
        Observable<List<HotNews>> observable = redRockApiService.getTrendDetail(type_id, article_id)
                .flatMap(bbddDetailWrapper -> Observable.fromIterable(bbddDetailWrapper.data))
                .map(bbddDetail -> {
                    HotNews news = new HotNews(bbddDetail);
                    newsList.add(news);
                    return newsList;
                });
        emitObservable(observable, observer);
    }

    public void getMyTrend(Observer<List<HotNews>> observer, String stuNum, String idNum) {
        List<HotNews> newsList = new ArrayList<>();
        Observable<List<HotNews>> observable = redRockApiService.searchTrends(stuNum, idNum)
                .flatMap(mDetailWrapper -> Observable.fromIterable(mDetailWrapper.data))
                .map(mDetail -> {
                    HotNews news = new HotNews(mDetail);
                    newsList.add(news);
                    return newsList;
                })
                .map(hotNewses -> {
                    for (HotNews h : hotNewses) {
                        h.data.nickName = AccountManager.getUser().getNickname();
                        h.data.userHead = AccountManager.getUser().photo_thumbnail_src;
                    }
                    return hotNewses;
                });
        emitObservable(observable, observer);
    }

    /**
     * api
     */
    public Observable<UploadImgResponse.Response> uploadNewsImg(String stuNum, String filePath) {
        File file = new File(filePath);
        try {
            file = BitmapUtil.decodeBitmapFromRes(ContextProvider.getContext(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part file_body = MultipartBody.Part.createFormData("fold", file.getName(), requestFile);
        RequestBody stuNum_body = RequestBody.create(MediaType.parse("multipart/form-data"), stuNum);
        return redRockApiService.uploadSocialImg(stuNum_body, file_body)
                .map(new RedRockApiWrapperFunc<>());
    }

    public void getHotArticle(Observer<List<HotNews>> observer, int size, int page) {
        Observable<List<HotNews>> observable = redRockApiService.getSocialHotList(size, page);
        emitObservable(observable, observer);
    }

    public void getListNews(Observer<List<HotNews>> observer, int size, int page) {
        Observable<List<HotNews>> observable = redRockApiService.getSocialOfficialNewsList(size, page)
                .map(new RedRockApiWrapperFunc<>())
                .map(officeNewsContentList -> {
                    List<HotNews> aNews = new ArrayList<>();
                    for (OfficeNewsContent officeNewsContent : officeNewsContentList)
                        aNews.add(new HotNews(officeNewsContent));
                    return aNews;
                });
        emitObservable(observable, observer);
    }

    public void getListArticle(Observer<List<HotNews>> observer, int type_id, int size, int page) {
        Observable<List<HotNews>> observable = redRockApiService.getSocialBBDDList(type_id, size, page)
                .map(new RedRockApiWrapperFunc<>())
                .flatMap(bbdd -> Observable.just(bbdd)
                        .map(mBBDD -> {
                            List<HotNews> aNews = new ArrayList<>();
                            for (BBDDNewsContent bbddNewsContent : mBBDD)
                                aNews.add(new HotNews(bbddNewsContent));
                            return aNews;
                        }));
        emitObservable(observable, observer);
    }

    public Observable<String> sendDynamic(int type_id,
                                          String title,
                                          String content,
                                          String thumbnail_src,
                                          String photo_src,
                                          String user_id,
                                          String stuNum,
                                          String idNum) {

        return redRockApiService.sendDynamic(type_id, title, user_id, content, thumbnail_src, photo_src, stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> sendTopicArticle(int topicId,
                                               String title,
                                               String content,
                                               String thumbnailSrc,
                                               String photoSrc,
                                               String stuNum,
                                               String idNum
    ) {
        return redRockApiService.sendTopicArticle(topicId, title, content, thumbnailSrc, photoSrc, stuNum, idNum, false)
                .map(new RedRockApiWrapperFunc<>()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void getRemarks(Observer<List<CommentContent>> observer,
                           String article_id,
                           int type_id) {
        Observable<List<CommentContent>> observable = redRockApiService.getSocialCommentList(article_id, type_id)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }


    public void postReMarks(Observer<String> observer,
                            String article_id,
                            int type_id,
                            String content,
                            String user_id,
                            String stuNum,
                            String idNum) {
        if (!checkWithUserId("没有完善信息,还想发回复？")) return;
        Observable<String> observable = redRockApiService.addSocialComment(article_id, type_id, content, user_id, stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void addThumbsUp(Observer<String> observer,
                            String article_id,
                            int type_id,
                            String stuNum,
                            String idNum) {
        if (!checkWithUserId("没有完善信息,肯定不让你点赞呀")) return;
        Observable<String> observable = redRockApiService.socialLike(article_id, type_id, stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void cancelThumbsUp(Observer<String> observer,
                               String article_id,
                               int type_id,
                               String stuNum,
                               String idNum) {
        if (!checkWithUserId("没有完善信息,肯定不让你点赞呀")) return;
        Observable<String> observable = redRockApiService.socialUnlike(article_id, type_id, stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    @SuppressWarnings("unchecked")
    public Observable<RedRockApiWrapper> setPersonInfo(String stuNum, String idNum, String photo_thumbnail_src, String photo_src) {

        return redRockApiService.setPersonInfo(stuNum, idNum, photo_thumbnail_src, photo_src);
    }

    @SuppressWarnings("unchecked")
    public void setPersonNickName(Observer<RedRockApiWrapper<Object>> observer, String stuNum, String idNum, String nickName) {
        Observable<RedRockApiWrapper<Object>> observable = redRockApiService.setPersonNickName(stuNum, idNum, nickName);
        emitObservable(observable, observer);
    }


    public void getPersonInfo(Observer<PersonInfo> observer, String otherStuNum, String stuNum, String idNum) {
        Observable<PersonInfo> observable = redRockApiService.getPersonInfo(otherStuNum, stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void getPersonLatestList(Observer<List<HotNews>> observer, String otherStuNum, String userName, String userHead) {
        Observable<List<HotNews>> observable = redRockApiService.getPersonLatestList(otherStuNum)
                .map(new RedRockApiWrapperFunc<>())
                .map(latestsList -> {
                    List<HotNews> aNews = new ArrayList<>();
                    for (PersonLatest personLatest : latestsList)
                        aNews.add(new HotNews(personLatest, otherStuNum, userName, userHead));
                    return aNews;
                });
        emitObservable(observable, observer);
    }

    @SuppressWarnings("unchecked")
    public void setPersonIntroduction(Observer<RedRockApiWrapper<Object>> observer, String stuNum, String idNum, String introduction) {

        Observable<RedRockApiWrapper<Object>> observable = redRockApiService.setPersonIntroduction(stuNum, idNum, introduction);

        emitObservable(observable, observer);
    }

    @SuppressWarnings("unchecked")
    public void setPersonQQ(Observer<RedRockApiWrapper<Object>> observer, String stuNum, String idNum, String qq) {

        Observable<RedRockApiWrapper<Object>> observable = redRockApiService.setPersonQQ(stuNum, idNum, qq)
                .map(new RedRockApiWrapperFunc());

        emitObservable(observable, observer);
    }

    @SuppressWarnings("unchecked")
    public void setPersonPhone(Observer<RedRockApiWrapper<Object>> observer, String stuNum, String idNum, String phone) {

        Observable<RedRockApiWrapper<Object>> observable = redRockApiService.setPersonPhone(stuNum, idNum, phone)
                .map(new RedRockApiWrapperFunc());

        emitObservable(observable, observer);
    }

    public void getPersonInfo(Observer<User> observer, String stuNum, String idNum) {
        Observable<User> observable = redRockApiService.getPersonInfo(stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>())
                .map(new UserInfoVerifyFunc());
        emitObservable(observable, observer);
    }

    public void getAffair(Observer<List<Affair>> observer, String stuNum, String idNum) {
        Observable<List<Affair>> observable = redRockApiService.getAffair(stuNum, idNum)
                .map(new AffairTransformFunc());
        emitObservable(observable, observer);
    }

    public void getAffair(Observer<List<Affair>> observer, String stuNum, String idNum, int week) {
        Observable<List<Affair>> observable = redRockApiService.getAffair(stuNum, idNum)
                .map(new AffairTransformFunc())
                .map(new AffairWeekFilterFunc(week));
        emitObservable(observable, observer);
    }

    public void addAffair(Observer<Object> observer, String stuNum, String idNum, String uid, String title,
                          String content, String date, int time) {
        Observable<Object> observable = redRockApiService.addAffair(uid, stuNum, idNum, date, time, title, content)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }


    public void editAffair(Observer<Object> observer, String stuNum, String idNum, String uid, String title,
                           String content, String date, int time) {
        Observable<Object> observable = redRockApiService.editAffair(uid, stuNum, idNum, date, time, title, content)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void deleteAffair(Observer<Object> observer, String stuNum, String idNum, String uid) {
        Observable<Object> observable = redRockApiService.deleteAffair(stuNum, idNum, uid)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

    public void getStartPage(Observer<StartPage> observer) {
        Observable<StartPage> observable = redRockApiService.startPage()
                .map(new RedRockApiWrapperFunc<>())
                .map(new StartPageFunc());
        emitObservable(observable, observer);
    }

    public void queryElectricCharge(Observer<ElectricCharge> observer, String building, String room) {
        if (!checkWithUserId("需要先登录才能发送失物招领信息哦")) return;
        Observable<ElectricCharge> observable = redRockApiService.queryElectricCharge(building, room)
                .map(new ElectricQueryFunc());
        emitObservable(observable, observer);
    }


    public void bindDormitory(String stuNum, String idNum, String room, Observer<Object> subscriber) {
        if (!checkWithUserId("需要先登录才能查询绑定寝室哦"))
            return;
        Observable<Object> observable = redRockApiService.bindDormitory(stuNum, idNum, room)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, subscriber);
    }

    public void queryPastElectricCharge(String stuNum, String idNum, Observer<PastElectric.PastElectricResultWrapper> subscriber) {
        if (!checkWithUserId("需要先登录才能查询绑定寝室哦"))
            return;
        Observable<PastElectric.PastElectricResultWrapper> observable = redRockApiService.getPastElectricCharge(stuNum, idNum)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, subscriber);
    }

    public void getLostList(Observer<LostWrapper<List<Lost>>> observer, int theme, String category, int page) {
        String themeString;
        if (theme == LostActivity.THEME_LOST) {
            themeString = "lost";
        } else {
            themeString = "found";
        }
        Observable<LostWrapper<List<Lost>>> observable = lostApiService.getLostList(themeString, category, page);
        emitObservable(observable, observer);
    }

    public void getLostDetail(Observer<LostDetail> observer, Lost origin) {
        Observable<LostDetail> observable = lostApiService.getLostDetial(origin.id)
                .map(lostDetail -> lostDetail.mergeLost(origin));
        emitObservable(observable, observer);
    }

    public void createLost(Observer<LostStatus> observer, LostDetail detail, int theme) {
        if (!checkWithUserId("需要先登录才能发送失物招领信息哦")) return;
        User user = AccountManager.getUser();
        String themeString;
        if (theme == LostActivity.THEME_LOST) {
            themeString = "寻物启事";
        } else {
            themeString = "失物招领";
        }
        Observable<LostStatus> observable = lostApiService.create(user.stuNum,
                user.idNum,
                themeString,
                detail.category,
                detail.description,
                detail.time,
                detail.place,
                detail.connectPhone,
                detail.connectWx);
        emitObservable(observable, observer);
    }

    public void getTopicList(Observer<List<Topic>> observer, int size, int page, String stuNum, String idNum, String type) {
        Observable<List<Topic>> observable;
        switch (type) {
            case TopicFragment.TopicType.MY_TOPIC:
                observable = redRockApiService.getMyTopicList(stuNum, idNum, size, page)
                        .map(new RedRockApiWrapperFunc<>());
                break;
            case TopicFragment.TopicType.ALL_TOPIC:
                observable = redRockApiService.getAllTopicList(stuNum, idNum, size, page)
                        .map(new RedRockApiWrapperFunc<>());
                break;
            default:
                observable = redRockApiService.searchTopic(stuNum, idNum, size, page, type)
                        .map(new RedRockApiWrapperFunc<>());
                break;
        }
        emitObservable(observable, observer);
    }

    public void getTopicArticle(Observer<TopicArticle> observer, int size, int page, String stuNum, String idNum, int topicId) {
        Observable<TopicArticle> observable = redRockApiService.getTopicArticle(stuNum, idNum, size, page, topicId)
                .map(new RedRockApiWrapperFunc<>());

        emitObservable(observable, observer);
    }

    private <T> Disposable emitObservable(Observable<T> o, DisposableObserver<T> s) {
        return o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(s);
    }

    private <T> void emitObservable(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


    public boolean checkWithUserId(String s) {
        if (!AccountManager.isLogin()) {
            EventBus.getDefault().post(new AskLoginEvent(s));
            return false;
        } else {
            return true;
        }

    }

    public void getRollerViewInfo(Observer<List<RollerViewInfo>> observer, String pic_num) {
        Observable<List<RollerViewInfo>> observable = redRockApiService.getRollerViewInfo(pic_num)
                .map(new RedRockApiWrapperFunc<>());
        emitObservable(observable, observer);
    }

}

