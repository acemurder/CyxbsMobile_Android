package com.mredrock.cyxbs.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mredrock.cyxbs.R;

import org.jetbrains.anko.DimensionsKt;


/**
 * Created By jay68 on 2018/5/2.
 */
public class UserFragmentItem extends FrameLayout {
    private View remindIcon;
    private TextView title;
    private ImageView icon;

    public UserFragmentItem(Context context) {
        this(context, null);
    }

    public UserFragmentItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserFragmentItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        LayoutInflater.from(context).inflate(R.layout.layout_user_fragment_item, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserFragmentItem);
        icon = findViewById(R.id.icon);
        icon.setImageResource(typedArray.getResourceId(R.styleable.UserFragmentItem_itemIcon, 0));
        title = findViewById(R.id.title);
        title.setText(typedArray.getString(R.styleable.UserFragmentItem_itemTitle));
        remindIcon = findViewById(R.id.remindIcon);
        boolean showRemindIcon = typedArray.getBoolean(R.styleable.UserFragmentItem_showRemindIcon, false);
        setRemindIconShowing(showRemindIcon);

        //Drawable attr
        final int dp9 = DimensionsKt.dip(this, 9);
        Rect radius = new Rect();
        radius.left = typedArray.getDimensionPixelSize(R.styleable.UserFragmentItem_leftRadius, dp9);
        radius.top = typedArray.getDimensionPixelSize(R.styleable.UserFragmentItem_topRadius, dp9);
        radius.right = typedArray.getDimensionPixelSize(R.styleable.UserFragmentItem_rightRadius, dp9);
        radius.bottom = typedArray.getDimensionPixelSize(R.styleable.UserFragmentItem_bottomRadius, dp9);
        boolean[] hideShadow = new boolean[4];
        hideShadow[0] = typedArray.getBoolean(R.styleable.UserFragmentItem_hideLeftShadow, false);
        hideShadow[1] = typedArray.getBoolean(R.styleable.UserFragmentItem_hideTopShadow, false);
        hideShadow[2] = typedArray.getBoolean(R.styleable.UserFragmentItem_hideRightShadow, false);
        hideShadow[3] = typedArray.getBoolean(R.styleable.UserFragmentItem_hideBottomShadow, false);
        int backgroundColor = typedArray.getColor(R.styleable.UserFragmentItem_backgroundColor, Color.parseColor("#fefefe"));
        int shadowColor = typedArray.getColor(R.styleable.UserFragmentItem_shadowColor, Color.parseColor("#0c000000"));
        int shadowRadius = typedArray.getDimensionPixelSize(R.styleable.UserFragmentItem_shadowRadius, DimensionsKt.dip(this, 10));
        typedArray.recycle();

        int left = shadowRadius;
        int top = shadowRadius;
        int right = shadowRadius;
        int bottom = shadowRadius;
        if (hideShadow[0]) left = 0;
        if (hideShadow[1]) top = 0;
        if (hideShadow[2]) right = 0;
        if (hideShadow[3]) bottom = 0;
        setPadding(left, top, right, bottom);   //给阴影留出空间
        RectShadowDrawable drawable = new RectShadowDrawable(radius, backgroundColor, shadowColor,
                shadowRadius, 0, 0, hideShadow, this);
        ViewCompat.setBackground(this, drawable);
    }

    public boolean isRemindIconShowing() {
        return remindIcon.getVisibility() == VISIBLE;
    }

    public void setRemindIconShowing(boolean remindIconShowing) {
        if (remindIconShowing) remindIcon.setVisibility(VISIBLE);
        else remindIcon.setVisibility(GONE);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setIcon(Drawable icon) {
        this.icon.setImageDrawable(icon);
    }
}
