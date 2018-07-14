package com.redrock.schedule.network

import com.redrock.common.config.Const
import com.redrock.common.network.BaseRequestManager
import com.redrock.common.network.RedRockApiWrapperFunc
import com.redrock.common.network.RedrockApiException
import com.redrock.common.network.RequestProvider
import com.redrock.schedule.model.Affair
import com.redrock.schedule.model.Course
import io.reactivex.Observable
import io.reactivex.Observer
import java.io.IOException

object ScheduleRequestManager : BaseRequestManager() {
    private var scheduleApiService: ScheduleApiService =
            RequestProvider.retrofit.create(ScheduleApiService::class.java)

    fun getAffair(observer: Observer<List<Affair>>, stuNum: String, idNum: String) {
        val observable = scheduleApiService.getAffair(stuNum, idNum)
                .map(AffairTransformFunc())
        emitObservable(observable, observer)
    }

    fun getAffair(observer: Observer<List<Affair>>, stuNum: String, idNum: String, week: Int) {
        val observable = scheduleApiService.getAffair(stuNum, idNum)
                .map(AffairTransformFunc())
                .map(AffairWeekFilterFunc(week))
        emitObservable(observable, observer)
    }

    fun addAffair(observer: Observer<Any>, stuNum: String, idNum: String, uid: String, title: String,
                  content: String, date: String, time: Int) {
        val observable = scheduleApiService.addAffair(uid, stuNum, idNum, date, time, title, content)
                .map(RedRockApiWrapperFunc<Any>())
        emitObservable(observable, observer)
    }


    fun editAffair(observer: Observer<Any>, stuNum: String, idNum: String, uid: String, title: String,
                   content: String, date: String, time: Int) {
        val observable = scheduleApiService.editAffair(uid, stuNum, idNum, date, time, title, content)
                .map(RedRockApiWrapperFunc<Any>())
        emitObservable(observable, observer)
    }

    fun deleteAffair(observer: Observer<Any>, stuNum: String, idNum: String, uid: String) {
        val observable = scheduleApiService.deleteAffair(stuNum, idNum, uid)
                .map(RedRockApiWrapperFunc<Any>())
        emitObservable(observable, observer)
    }

    fun getNowWeek(observer: Observer<Int>, stuNum: String, idNum: String) {
        val observable = scheduleApiService.getCourse(stuNum, idNum, "0")
                .map { courseWrapper ->
                    if (courseWrapper.status != Const.RED_ROCK_API_STATUS_SUCCESS) {
                        throw RedrockApiException()
                    }
                    Integer.parseInt(courseWrapper.nowWeek)
                }
        emitObservable(observable, observer)
    }

    fun getCourseList(observer: Observer<List<Course>>, stuNum: String, idNum: String, week: Int, update: Boolean) {
        getCourseList(observer, stuNum, idNum, week, update, true)
    }

    fun getCourseList(observer: Observer<List<Course>>, stuNum: String, idNum: String,
                      week: Int, update: Boolean, forceFetch: Boolean) {
        val observable = CourseListProvider.start(stuNum, idNum, update, forceFetch)
                .map(UserCourseFilterFunc(week))
        emitObservable<List<Course>>(observable, observer)
    }

    fun getCourseList(stuNum: String, idNum: String): Observable<List<Course>> {
        return scheduleApiService.getCourse(stuNum, idNum, "0").map(RedRockApiWrapperFunc<List<Course>>())
    }

    @Throws(IOException::class)
    fun getCourseListSync(stuNum: String, idNum: String, forceFetch: Boolean): List<Course> {
        val response = scheduleApiService.getCourseCall(stuNum, idNum, "0", forceFetch).execute()
        return response.body()!!.data
    }

    fun getPublicCourse(observer: Observer<List<Course>>,
                        stuNumList: List<String>, week: String) {
        val observable = Observable.fromIterable(stuNumList)
                .flatMap { s -> scheduleApiService.getCourse(s, "", week) }
                .map(RedRockApiWrapperFunc<List<Course>>())
        emitObservable<List<Course>>(observable, observer)
    }
}