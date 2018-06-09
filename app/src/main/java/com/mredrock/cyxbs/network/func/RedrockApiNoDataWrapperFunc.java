package com.mredrock.cyxbs.network.func;

import com.mredrock.cyxbs.config.Const;
import com.mredrock.cyxbs.model.BaseRedrockApiWrapper;
import com.mredrock.cyxbs.network.exception.RedrockApiException;

import io.reactivex.functions.Function;
import kotlin.Unit;

/**
 * Created By jay68 on 2018/2/28.
 */

public class RedrockApiNoDataWrapperFunc implements Function<BaseRedrockApiWrapper, Unit> {
    @Override
    public Unit apply(BaseRedrockApiWrapper wrapper) {
        if (wrapper.status != Const.REDROCK_API_STATUS_SUCCESS) {
            throw new RedrockApiException(wrapper.info);
        }
        return Unit.INSTANCE;
    }
}
