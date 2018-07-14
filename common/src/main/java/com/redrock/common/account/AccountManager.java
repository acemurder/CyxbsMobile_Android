package com.redrock.common.account;

import android.util.Log;

import com.google.gson.Gson;
import com.redrock.common.ContextProvider;
import com.redrock.common.config.Const;
import com.redrock.common.network.encrypt.UserInfoEncryption;
import com.redrock.common.util.SPUtils;

import org.apache.commons.lang3.StringUtils;

public class AccountManager {

    private static User mUser;
    private static boolean login;
    private static UserInfoEncryption userInfoEncryption;

    static {
        // Initialize UserInfoEncrypted
        userInfoEncryption = new UserInfoEncryption();
    }

    public static void setUser(User user) {
        String userJson;
        mUser = user;
        if (user == null) {
            AccountManager.setLogin(false);
            userJson = "";
        } else {
            userJson = new Gson().toJson(user);
            AccountManager.setLogin(true);
        }
        String encryptedJson = userInfoEncryption.encrypt(userJson);
        SPUtils.set(ContextProvider.getContext(), Const.SP_KEY_USER, encryptedJson);
    }

    /**
     * @return mUser with stuNum and idNum
     */
    public static User getUser() {
        if (mUser == null) {
            String encryptedJson = (String) SPUtils.get(ContextProvider.getContext(), Const.SP_KEY_USER, "");
            String json = userInfoEncryption.decrypt(encryptedJson);
            Log.d("userinfo", json);
            mUser = new Gson().fromJson(json, User.class);

            if (mUser == null || mUser.stuNum == null || mUser.idNum == null) {
                initializeFakeUser();
            }
        }
        return mUser;
    }

    public static boolean isLogin() {
        if (!login) {
            String encryptedJson = (String) SPUtils.get(ContextProvider.getContext(), Const.SP_KEY_USER, "");
            String json = userInfoEncryption.decrypt(encryptedJson);
            User user = new Gson().fromJson(json, User.class);
            if (user != null && !user.stuNum.equals("0")) {
                return true;
            } else {
                initializeFakeUser();
            }
        }
        return login;
    }

    private static void initializeFakeUser() {
        mUser = new User();
        //  mUser.id = "0";
        mUser.idNum = "0";
        mUser.stuNum = "0";
    }

    public static boolean isFresh() {
        return isLogin() && getUser().stuNum.substring(0, 4).equals("2017");
    }

    public static void setLogin(boolean login) {
        AccountManager.login = login;
    }

    public static boolean hasSetInfo() {
        User user = getUser();
        return user != null && StringUtils.isNotBlank(user.id);
    }

    public static boolean hasNickName() {
        return getUser().nickname != null && !getUser().nickname.equals("");
    }
}
