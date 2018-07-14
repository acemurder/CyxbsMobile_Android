package com.redrock.account.network

import com.redrock.common.account.User
import com.redrock.common.network.RedRockApiWrapper
import com.redrock.common.network.RedRockApiWrapperFunc
import com.redrock.common.network.RequestProvider
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

object AccountRequestManager {

    private var accountApiService: AccountApiService =
            RequestProvider.retrofit.create(AccountApiService::class.java)

    fun login(observer: Observer<User>, stuNum: String, idNum: String) {
        val observable = accountApiService.verify(stuNum, idNum)
                .map(RedRockApiWrapperFunc<User>())
                .zipWith(accountApiService.getPersonInfo(stuNum, idNum)
                        .map(RedRockApiWrapperFunc<User>()),
                        BiFunction<User, User, User> { userOrigin, userCloned ->
                            User.cloneFromUserInfo(userOrigin, userCloned)
                        })

        emitObservable(observable, observer)
    }

    private fun <T> emitObservable(o: Observable<T>, s: Observer<T>) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s)
    }

}