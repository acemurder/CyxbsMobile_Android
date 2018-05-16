package com.mredrock.cyxbs.subscriber;

import com.mredrock.cyxbs.network.error.ErrorHandler;

/**
 * Created by cc on 16/3/20.
 * Edited by Jay86 on 18/4/22.
 */
public abstract class SubscriberListener<T> {
    private ErrorHandler mErrorHandler;

    public SubscriberListener() {}

    public SubscriberListener(ErrorHandler errorHandler) {
        mErrorHandler = errorHandler;
    }

    public void onStart() {
    }

     public void onComplete() {
    }

    /**
     * 异常分发机制<br>
     *     默认会被传入的异常处理器{@link ErrorHandler#handle(Throwable)}处理(如果有)
     *     如果子类覆写了此方法且处理了这个异常，返回 true ，否则返回 false ，异常将被上层 {@link SimpleObserver#onError(Throwable)} 处理<br>
     *     请注意上层基本上只处理简单的网络异常，未知的异常会直接被上层 Toast 出来，请尽可能在子类处理可能遇到的异常<br>
     *     不需要管的异常直接返回 true 就好啦
     * @param e 发生的异常
     * @return 该异常是否被子类处理
     */
    public boolean onError(Throwable e) {
        return mErrorHandler != null && mErrorHandler.handle(e);
    }

    public void onNext(T t) {
    }

}
