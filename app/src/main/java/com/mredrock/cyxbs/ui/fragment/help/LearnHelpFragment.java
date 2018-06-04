package com.mredrock.cyxbs.ui.fragment.help;

import com.mredrock.cyxbs.model.help.Question;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.subscriber.SimpleObserver;
import com.mredrock.cyxbs.ui.adapter.HelpAdapter;

import java.util.List;

/**
 * Created by yan on 2018/5/31.
 */

public class LearnHelpFragment extends BaseHelpFragment {

    @Override
    void provideData(SimpleObserver<List<Question>> observer, int size, int page) {
        RequestManager.getInstance().getAllQuestion(observer, Integer.toString(page), Integer.toString(size), "学习");
    }
}
