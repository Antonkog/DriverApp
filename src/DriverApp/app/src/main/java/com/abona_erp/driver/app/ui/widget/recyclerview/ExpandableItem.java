package com.abona_erp.driver.app.ui.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.abona_erp.driver.app.R;

public class ExpandableItem extends RelativeLayout {

  static final String TAG = ExpandableItem.class.getCanonicalName();

  @NonNull private final ViewGroup contentLayout;
  @NonNull private final ViewGroup headerLayout;
  private boolean isOpened = false;

  public ExpandableItem(Context context) {
    this(context, null);
  }

  public ExpandableItem(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.expandableItemStyle);
  }

  public ExpandableItem(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    final LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.widget_expandableitem, this, true);
    headerLayout = findViewById(R.id.widget_expandableitem_headerlayout);
    contentLayout = findViewById(R.id.widget_expandableitem_contentlayout);

    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableItem, defStyleAttr, 0);
    final int headerId = a.getResourceId(R.styleable.ExpandableItem_layoutHeader, -1);
    final int contentId = a.getResourceId(R.styleable.ExpandableItem_layoutContent, -1);
    a.recycle();

    if (headerId == -1 || contentId == -1) {
      throw new IllegalArgumentException("HeaderLayout and ContentLayout cannot be null!");
    }
    if (isInEditMode()) {
      return;
    }

    final View headerView = inflater.inflate(headerId, headerLayout, false);
    headerView.setLayoutParams(new ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    headerLayout.addView(headerView);

    final View contentView = inflater.inflate(contentId, contentLayout, false);
    contentView.setLayoutParams(new ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT));
    contentLayout.addView(contentView);
    contentLayout.setVisibility(GONE);

    setTag(TAG);
  }

  @UiThread
  public void hideNow() {
    isOpened = false;
    contentLayout.setVisibility(GONE);
    contentLayout.getLayoutParams().height = 0;
    contentLayout.invalidate();
  }

  @UiThread
  public void showNow() {
      isOpened = true;
      contentLayout.setVisibility(VISIBLE);
      contentLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
      contentLayout.invalidate();
  }

  @NonNull
  public ViewGroup getHeaderLayout() {
    return headerLayout;
  }

  @NonNull
  public ViewGroup getContentLayout() {
    return contentLayout;
  }

  public boolean isOpened() {
    return isOpened;
  }
}
