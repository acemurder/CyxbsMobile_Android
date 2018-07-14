package com.mredrock.cyxbs.model.social;

import com.redrock.common.network.RedRockApiWrapper;

import java.util.List;

/**
 * Created by mathiasluo on 16-4-22.
 */
public class BBDDNews extends RedRockApiWrapper<List<BBDDNewsContent>> {
    public static final int BBDD = 5;
    public static final int TOPIC_ARTICLE = 7;
    public String page;
}