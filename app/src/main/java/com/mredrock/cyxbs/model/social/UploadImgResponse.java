package com.mredrock.cyxbs.model.social;

import com.google.gson.annotations.SerializedName;
import com.redrock.common.network.RedRockApiWrapper;

/**
 * Created by mathiasluo on 16-4-12.
 */
public class UploadImgResponse extends RedRockApiWrapper<UploadImgResponse.Response> {

    public static class Response {

        public int state;
        public String date;

        @SerializedName("stunum")
        public String stuNum;
        @SerializedName("photosrc")
        public String photoSrc;
        @SerializedName("thumbnail_src")
        public String thumbnailSrc;

    }

}
