package com.mredrock.cyxbs.ui.activity.help;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.mredrock.cyxbs.BaseAPP;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.component.multi_image_selector.MultiImageSelectorActivity;
import com.mredrock.cyxbs.component.widget.ninelayout.NineGridlayout;
import com.mredrock.cyxbs.model.User;
import com.mredrock.cyxbs.model.help.Question;
import com.mredrock.cyxbs.model.help.QuestionId;
import com.mredrock.cyxbs.model.social.Image;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.subscriber.SimpleObserver;
import com.mredrock.cyxbs.subscriber.SubscriberListener;
import com.mredrock.cyxbs.ui.activity.BaseActivity;
import com.mredrock.cyxbs.ui.fragment.help.SelectRewardDialog;
import com.mredrock.cyxbs.ui.fragment.help.SelectTagDialog;
import com.mredrock.cyxbs.ui.fragment.help.SelectTimeDialog;
import com.mredrock.cyxbs.util.DialogUtil;
import com.mredrock.cyxbs.util.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by yan on 2018/5/23.
 */

public class PostHelpActivity extends BaseActivity {

    public static final String TAG = "PostHelpActivity";
    private final static String ADD_IMG = "file:///android_asset/add_help.png";
    public static final String EXTRA_TOPIC_ID = "extra_topic_id";
    private final static int REQUEST_IMAGE = 0001;

    @BindView(R.id.toolbar_next)
    TextView mNextBtn;
    @BindView(R.id.tv_tag)
    TextView mTag;
    @BindView(R.id.tv_title)
    EditText mTitle;
    @BindView(R.id.tv_title_num)
    TextView mTitleNum;
    @BindView(R.id.tv_content)
    EditText mContent;
    @BindView(R.id.tv_content_num)
    TextView mContentNum;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_ngrid_layout)
    NineGridlayout mNineGridlayout;
    @BindView(R.id.chk_is_anonymous)
    CheckBox mCheckBox;

    private List<Image> mImgList;
    private User mUser;

    private String kind;
    private String tag = "";
    private String disappear_time = "";
    private int reward;


    public static void startActivity(Context context, String kind) {
        Intent intent = new Intent(context, PostHelpActivity.class);
        intent.putExtra("kind", kind);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_help);
        ButterKnife.bind(this);
        mUser = BaseAPP.getUser(this);

        init();
        initToolbar();
        Intent intent = getIntent();
        kind = intent.getStringExtra("kind");
        Utils.toast(this, kind);
    }

    private void init() {
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        RxPermissions rxPermissions = new RxPermissions(this);

        mImgList = new ArrayList<>();
        mImgList.add(new Image(ADD_IMG, Image.TYPE_ADD));
        mNineGridlayout.setImagesData(mImgList);
        mNineGridlayout.setOnAddImagItemClickListener((v, position) ->
                rxPermissions
                        .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                // All requested permissions are granted
                                Intent intent = new Intent(PostHelpActivity.this, MultiImageSelectorActivity.class);
                                // 是否显示调用相机拍照
                                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                                // 最大图片选择数量
                                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
                                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                                // 默认选择图片,回填选项(支持String ArrayList)
                                ArrayList<String> results = new ArrayList<>();
                                for (Image i : mImgList) {
                                    if (i.getType() != Image.TYPE_ADD)
                                        results.add(i.url);
                                }

                                if (mImgList.size() != 0)
                                    intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, results);
                                startActivityForResult(intent, REQUEST_IMAGE);
                            } else {
                                Utils.toast(this, "没有赋予权限哦");
                            }
                        }));

        mNineGridlayout.setOnClickDeletecteListener((v, position) -> {
            mImgList.remove(position);
            mNineGridlayout.setImagesData(mImgList);
        });
    }

    private void initToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle("");
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(
                    v -> this.finish());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (Image image : mImgList) {
                    for (int i = 0; i < pathList.size(); i++) {
                        if (image.url.equals(pathList.get(i))) pathList.remove(i);
                    }
                }

                // 处理你自己的逻辑 ....
                if (mImgList.size() + pathList.size() > 6) {
                    Utils.toast(this, "最多只能选5张图");
                    return;
                }
                Observable.fromIterable(pathList)
                        .map(s -> new Image(s, Image.TYPE_NORMAL))
                        .map(image -> {
                            mImgList.add(mImgList.size() - 1, image);
                            return mImgList;
                        })
                        .subscribe(new SimpleObserver<>(this, new SubscriberListener<List<Image>>() {
                            @Override
                            public void onNext(List<Image> list) {
                                super.onNext(list);
                                mNineGridlayout.setImagesData(list);
                            }
                        }));
            }
        }
    }

    @OnClick(R.id.toolbar_next)
    public void onViewClicked() {
        if (tag == "") {
            Utils.toast(this, "请先选择一个标签~");
            selectTag();
        } else {
            selectTime();
        }
    }

    private void selectTime() {
        Dialog dialog = new SelectTimeDialog(this, R.style.BottomDialogTheme, new SelectTimeDialog.OnTimeChangeListener() {
            @Override
            public void onChange(@NotNull String time) {
                disappear_time = time;
            }

            @Override
            public void onNext(String time) {
                disappear_time = time;
                selectReward();
            }
        });
        DialogUtil.showBottomDialog(dialog);
    }

    private void selectTag() {
        Dialog dialog = new SelectTagDialog(this, R.style.BottomDialogTheme, tag -> {
            this.tag = tag;
            if (tag != "")
                mTag.setText("#" + tag + "#");
            else
                mTag.setText("");
        }, tag);

        DialogUtil.showBottomDialog(dialog);
    }

    private void selectReward() {
        reward = 1;
        Dialog dialog = new SelectRewardDialog(this, R.style.BottomDialogTheme, new SelectRewardDialog.OnRewardChangeListener() {
            @Override
            public void onChange(int r) {
                 reward = r;
                 Utils.toast(PostHelpActivity.this, reward + "");
            }

            @Override
            public void onNext() {
                postNewHelp();
            }
        });
        DialogUtil.showBottomDialog(dialog);
    }

    private void postNewHelp() {
        List<String> files = new ArrayList<>();
        for (Image image :mImgList) {
            files.add(image.url);
        }
        //删除最后一张添加按钮的图片
        files.remove(files.size() - 1);

        String title = mTitle.getText().toString();
        String description = mContent.getText().toString();
        int is_anonymous = mCheckBox.isChecked() ? 1 : 0;


        Question question = new Question();
        question.setKind(kind);
        question.setTags(tag);
        question.setTitle(title);
        question.setDescription(description);
        question.setIs_anonymous(is_anonymous);
        question.setReward(reward);
        question.setDisappear_at(disappear_time);

        RequestManager.getInstance().postNewQuestion(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> strings) {
                if(strings != null);
                for (String str : strings)
                    Utils.toast(PostHelpActivity.this, str);
            }

            @Override
            public void onError(Throwable e) {
                Utils.toast(PostHelpActivity.this, e.toString());
            }

            @Override
            public void onComplete() {

            }
        }, mUser.stuNum, mUser.idNum, question, files);

    }

    @OnTextChanged(value = R.id.tv_title, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editTitleDetailChange(Editable editable) {
        int detailLength = 20 - editable.length();
        boolean islMaxCount = false;
        mTitleNum.setText(Integer.toString(detailLength));
        if (detailLength <= 0) {
            Utils.toast(this, "不能输入更多了");
        }
    }


    @OnTextChanged(value = R.id.tv_content, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editContentDetailChange(Editable editable) {
        int detailLength = 200 - editable.length();
        boolean islMaxCount = false;
        mContentNum.setText(Integer.toString(detailLength));
        if (detailLength <= 0) {
            Utils.toast(this, "不能输入更多了");
        }
    }

    @OnClick(R.id.btn_tag)
    public void btnTagOnclick() {
        selectTag();
    }

}
