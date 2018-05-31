package com.mredrock.cyxbs.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.component.widget.card.JCardView;


/**
 * Created By jay68 on 2018/5/2.
 */
public class UserFragmentItem extends JCardView {
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

        LayoutInflater.from(context).inflate(R.layout.layout_user_fragment_item, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserFragmentItem);
        icon = findViewById(R.id.icon);
        icon.setImageResource(typedArray.getResourceId(R.styleable.UserFragmentItem_itemIcon, 0));
        title = findViewById(R.id.title);
        title.setText(typedArray.getString(R.styleable.UserFragmentItem_itemTitle));
        remindIcon = findViewById(R.id.remindIcon);
        boolean showRemindIcon = typedArray.getBoolean(R.styleable.UserFragmentItem_showRemindIcon, false);
        setRemindIconShowing(showRemindIcon);
        typedArray.recycle();
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
