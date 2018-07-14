package com.mredrock.cyxbs.model.social;

import com.google.gson.annotations.SerializedName;
import com.redrock.common.network.RedRockApiWrapper;
import com.redrock.common.account.User;

/**
 * Created by mathiasluo on 16-5-6.
 */
public class PersonInfo {

    public String phone;
    public String qq;
    public String gender;
    public String introduction;
    public String id;

    @SerializedName("stunum")
    public String stuNum;
    @SerializedName("username")
    public String userName;
    @SerializedName("nickname")
    public String nickName;
    @SerializedName("photo_thumbnail_src")
    public String photoThumbnail;
    @SerializedName("photo_src")
    public String photo;
    @SerializedName("updated_time")
    public String updatedTime;

    public String getIntroduction() {
        return introduction.equals("") ? "简介: " + "ta很懒,没有留下个人简介" : "简介： " + introduction;
    }

    public static User cloneFromUserInfo(User userOrigin, User userCloned) {
        if (userCloned != null) {
            userOrigin.stu = userCloned.stu;
            userOrigin.photo_thumbnail_src = userCloned.photo_thumbnail_src;
            userOrigin.photo_src = userCloned.photo_src;
            userOrigin.nickname = userCloned.nickname;
            userOrigin.qq = userCloned.qq;
            userOrigin.phone = userCloned.phone;
            userOrigin.introduction = userCloned.introduction;
            userOrigin.id = userCloned.id;
        }
        return userOrigin;
    }

    public static User cloneFromUserInfo(User userOrigin, PersonInfo userCloned) {
        if (userCloned != null) {
            userOrigin.stu = userCloned.stuNum;
            userOrigin.photo_thumbnail_src = userCloned.photoThumbnail;
            userOrigin.photo_src = userCloned.photo;
            userOrigin.nickname = userCloned.nickName;
            userOrigin.qq = userCloned.qq;
            userOrigin.phone = userCloned.phone;
            userOrigin.introduction = userCloned.introduction;
            userOrigin.id = userCloned.id;
        }
        return userOrigin;
    }

    public static class UserWrapper extends RedRockApiWrapper<User> {
    }

}
