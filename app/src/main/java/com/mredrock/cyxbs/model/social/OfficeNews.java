package com.mredrock.cyxbs.model.social;

import com.redrock.common.network.RedRockApiWrapper;

import java.util.List;

/**
 * Created by mathiasluo on 16-4-22.
 */
public class OfficeNews extends RedRockApiWrapper<List<OfficeNewsContent>> {
    public String page;
}
