package com.mredrock.cyxbs.model.social;

import com.redrock.common.network.RedRockApiWrapper;

import java.util.List;

/**
 * Created by mathiasluo on 16-4-12.
 */
public class Comment extends RedRockApiWrapper<List<CommentContent>> {
    public int state;
}
