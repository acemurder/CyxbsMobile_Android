package com.redrock.account.network

import com.redrock.common.account.User
import com.redrock.common.network.BaseRequestManager
import com.redrock.common.network.RedRockApiWrapperFunc
import com.redrock.common.network.RequestProvider
import io.reactivex.Observer
import io.reactivex.functions.BiFunction

object AccountRequestManager : BaseRequestManager() {

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
}