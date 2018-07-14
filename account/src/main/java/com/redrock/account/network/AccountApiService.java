package com.redrock.account.network;

import com.redrock.common.account.User;
import com.redrock.common.config.Const;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AccountApiService {

    @FormUrlEncoded
    @POST(Const.API_VERIFY)
    Observable<User.UserWrapper> verify(@Field("stuNum") String stuNum, @Field("idNum") String idNum);

    @FormUrlEncoded
    @POST(Const.API_GET_INFO)
    Observable<User.UserWrapper> getPersonInfo(@Field("stuNum") String stuNum,
                                                     @Field("idNum") String idNum);
}
