package com.mredrock.cyxbs.network.exception;

import com.redrock.common.network.RedrockApiException;

/**
 * Created by cc on 16/5/6.
 */
public class UnsetUserInfoException extends RedrockApiException {
    public UnsetUserInfoException() {
    }

    public UnsetUserInfoException(String detailMessage) {
        super(detailMessage);
    }

    public UnsetUserInfoException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UnsetUserInfoException(Throwable throwable) {
        super(throwable);
    }
}
