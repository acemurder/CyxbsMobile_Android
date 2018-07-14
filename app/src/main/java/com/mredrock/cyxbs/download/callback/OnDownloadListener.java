package com.mredrock.cyxbs.download.callback;

/**
 * Created by Stormouble on 15/12/10.
 */
public interface OnDownloadListener {

    void startDownload();

    void downloadSuccess();

    void downloadFailed(String message);

}
