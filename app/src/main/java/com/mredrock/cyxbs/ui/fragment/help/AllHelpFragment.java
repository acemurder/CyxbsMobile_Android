package com.mredrock.cyxbs.ui.fragment.help;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.help.Question;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.subscriber.SimpleObserver;
import com.mredrock.cyxbs.subscriber.SubscriberListener;
import com.mredrock.cyxbs.ui.adapter.HelpAdapter;

import java.util.List;

import io.reactivex.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllHelpFragment extends BaseHelpFragment {

    @Override
    void provideData(SimpleObserver<List<Question>> observer, int size, int page) {
        RequestManager.getInstance().getAllQuestion(observer, Integer.toString(page), Integer.toString(size), "全部");
    }

}
