package com.mredrock.cyxbs.network.func;

import com.alibaba.android.arouter.launcher.ARouter;
import com.redrock.common.account.User;

import io.reactivex.functions.Function;


/**
 * Created by cc on 16/5/6.
 */
public class UserInfoVerifyFunc implements Function<User, User> {

    @Override
    public User apply(User user) {
        if (user == null) {
            ARouter.getInstance().build("/app/EditNickNameActivity").navigation();
        }
        return user;
    }
}
