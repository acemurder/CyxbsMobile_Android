package com.mredrock.cyxbs.model;

/**
 * Created by David on 15/5/17.
 * Edited by jay68 on 2018/06/09.
 *
 * note: 接口返回的json中没有data字段时，请使用{@link BaseRedrockApiWrapper }
 */
public class RedrockApiWrapper<T> extends BaseRedrockApiWrapper {
    public T data;
}
