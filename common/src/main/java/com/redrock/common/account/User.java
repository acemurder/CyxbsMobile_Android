package com.redrock.common.account;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;


/**
 * Info of user
 * <p>
 * Created by David on 15/5/15.
 */

public class User implements Parcelable {
    public User() {
    }

    public String id = "";
    public String stuNum = "";
    public String idNum = "";
    public String name = "";
    public String gender = "";
    public String classNum = "";
    public String major = "";
    public String college = "";
    public String grade = "";
    public String stu = "";
    public String photo_thumbnail_src = "";
    public String photo_src = "";
    public String nickname = "";
    public String qq = "";
    public String phone = "";
    public String introduction = "";

    protected User(Parcel in) {
        stuNum = in.readString();
        idNum = in.readString();
        name = in.readString();
        gender = in.readString();
        classNum = in.readString();
        major = in.readString();
        college = in.readString();
        grade = in.readString();
        stu = in.readString();
        photo_thumbnail_src = in.readString();
        photo_src = in.readString();
        nickname = in.readString();
        qq = in.readString();
        phone = in.readString();
        introduction = in.readString();
        id = in.readString();
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }


        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    public String getNickname() {
        return StringUtils.isEmpty(nickname) ? "来自一位没有名字的同学" : nickname;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stuNum);
        dest.writeString(idNum);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(classNum);
        dest.writeString(major);
        dest.writeString(college);
        dest.writeString(grade);
        dest.writeString(stu);
        dest.writeString(photo_thumbnail_src);
        dest.writeString(photo_src);
        dest.writeString(nickname);
        dest.writeString(qq);
        dest.writeString(phone);
        dest.writeString(introduction);
        dest.writeString(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "classNum='" + classNum + '\'' +
                ", stuNum='" + stuNum + '\'' +
                ", idNum='" + idNum + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", major='" + major + '\'' +
                ", college='" + college + '\'' +
                ", grade='" + grade + '\'' +
                ", stu='" + stu + '\'' +
                ", photo_thumbnail_src='" + photo_thumbnail_src + '\'' +
                ", photo_src='" + photo_src + '\'' +
                ", nickname='" + nickname + '\'' +
                ", qq='" + qq + '\'' +
                ", phone='" + phone + '\'' +
                ", introduction='" + introduction + '\'' +
                ",id=" + id + "\'" +
                '}';
    }
}
