package com.mredrock.cyxbs.network.func;

import com.mredrock.cyxbs.MainApp;
import com.redrock.common.account.User;
import com.mredrock.cyxbs.ui.activity.me.EditNickNameActivity;

import io.reactivex.functions.Function;


/**
 * Created by cc on 16/5/6.
 */
public class UserInfoVerifyFunc implements Function<User, User> {

    @Override
    public User apply(User user) throws Exception {
        if (user == null) {
            EditNickNameActivity.start(MainApp.getContext());
        }
        return user;
    }
}
