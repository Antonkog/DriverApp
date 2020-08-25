package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommItemSubAdapterExt
  extends RecyclerView.Adapter<CommItemSubAdapterExt.ViewHolder> {
  
  private static final String TAG = CommItemSubAdapterExt.class.getSimpleName();
  
  private static final String DATE_FORMAT_IN = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT_OUT = "dd.MM.yyyy HH:mm:ss";
  
  private static final String NOT_SET_TIMESTAMP_GLYPH = "--.--.---- --:--:--";
  
  private final Context mContext;
  private final Resources mResources;
  private final LayoutInflater mInflater;
  
  private Notify mData;
  private List<ActivityStep> mDataList;
  
  private final SimpleDateFormat mSdfIn;
  private final SimpleDateFormat mSdfOut;
  private Date mDateMin;
  
  public CommItemSubAdapterExt(Context ctx) {
    mContext = ctx;
    mResources = mContext.getResources();
    mInflater = LayoutInflater.from(mContext);
  
    mSdfIn = new SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault());
    mSdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
    try {
      mDateMin = mSdfIn.parse("1970-01-01 01:00:00");
    } catch (ParseException e) {
      if (e.getMessage() != null) Log.e(TAG, e.getMessage());
    }
  }
  
  @Override
  public void onBindViewHolder(CommItemSubAdapterExt.ViewHolder holder, int position) {
    if (position < 0) return;
    
    holder.setIsRecyclable(false);
    ActivityStep item = mDataList.get(position);
    
    if (position == 0 && mDataList.size() == 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    } else if (position == 0 && mDataList.size() > 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
    } else {
      holder.step_view_top.setVisibility(View.VISIBLE);
      holder.step_view_bottom.setVisibility(View.VISIBLE);
    }
    if (position == mDataList.size() - 1) {
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    }
    holder.tv_activity_step_no.setVisibility(View.GONE);
    holder.iv_activity_step_check_mark.setVisibility(View.GONE);
    
    if (item.getActivityItem().getName() != null) {
      holder.tv_activity_step_name.setText(item.getActivityItem().getName());
    }
    
    if (item.getActivityItem().getStarted() != null) {
      if (item.getActivityItem().getStarted().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_started.setText(mSdfOut.format(item.getActivityItem().getStarted()));
      }
    } else {
      holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (item.getActivityItem().getFinished() != null) {
      if (item.getActivityItem().getFinished().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_finished.setText(mSdfOut.format(item.getActivityItem().getFinished()));
      }
    } else {
      holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (item.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#cccccc"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.pending));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.grey_40));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    
      holder.btn_activity_next.setEnabled(false);
      holder.btn_activity_next.setVisibility(View.INVISIBLE);
    } else if (item.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#2980B9"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.running));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    } else if (item.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#10ac84"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.completed));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
    }
  }
  
  @Override
  public CommItemSubAdapterExt.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
  
    View itemView = mInflater.inflate(R.layout.step_activity_with_delay_reasons, parent, false);
    return new CommItemSubAdapterExt.ViewHolder(itemView);
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
  
    final View step_view_top;
    final View step_view_bottom;
  
    final ImageView iv_activity_step_dot;
    final ImageView iv_activity_step_check_mark;
    final AsapTextView tv_activity_step_no;
  
    final LinearLayout ll_activity_status;
    final AsapTextView tv_activity_status_text;
    final AsapTextView tv_activity_step_name;
  
    final AsapTextView tv_activity_step_started;
    final AsapTextView tv_activity_step_finished;
  
    final AppCompatButton btn_activity_next;
    final AppCompatImageButton btn_add_delay_reason;
    final AppCompatImageButton btn_delay_reason_history;
    
    public ViewHolder(View itemView) {
      super(itemView);
  
      step_view_top = itemView.findViewById(R.id.step_view_top);
      step_view_bottom = itemView.findViewById(R.id.step_view_bottom);
      iv_activity_step_dot = itemView.findViewById(R.id.iv_activity_step_dot);
      iv_activity_step_check_mark = itemView.findViewById(R.id.iv_activity_step_check_mark);
      tv_activity_step_no = itemView.findViewById(R.id.tv_activity_step_no);
  
      ll_activity_status = itemView.findViewById(R.id.ll_activity_status);
      tv_activity_status_text = itemView.findViewById(R.id.tv_activity_status_text);
      tv_activity_step_name = itemView.findViewById(R.id.tv_activity_step_name);
  
      tv_activity_step_started = itemView.findViewById(R.id.tv_activity_step_started);
      tv_activity_step_finished = itemView.findViewById(R.id.tv_activity_step_finished);
  
      btn_activity_next = itemView.findViewById(R.id.btn_activity_next);
      btn_add_delay_reason = itemView.findViewById(R.id.btn_add_delay_reason);
      btn_delay_reason_history = itemView.findViewById(R.id.btn_delay_reason_history);
    }
  }
  
  public void setDataList(Notify data, List<ActivityStep> dataList) {
    mData = data;
    mDataList = dataList;
    notifyDataSetChanged();
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mDataList has not been updated (means initially, it's null,
  // and we can't return null.
  @Override
  public int getItemCount() {
    if (mDataList != null) {
      return mDataList.size();
    } else return 0;
  }
}
