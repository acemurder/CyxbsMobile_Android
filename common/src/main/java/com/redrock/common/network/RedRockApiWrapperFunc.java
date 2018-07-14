package com.redrock.common.network;

import com.redrock.common.config.Const;

import io.reactivex.functions.Function;


/**
 * Created by cc on 16/5/6.
 */
public class RedRockApiWrapperFunc<T> implements Function<RedRockApiWrapper<T>, T> {

    @Override
    public T apply(RedRockApiWrapper<T> wrapper) throws Exception {
        if (wrapper.status != Const.RED_ROCK_API_STATUS_SUCCESS) {
            throw new RedrockApiException(wrapper.info);
        }
        return wrapper.data;
    }
}
