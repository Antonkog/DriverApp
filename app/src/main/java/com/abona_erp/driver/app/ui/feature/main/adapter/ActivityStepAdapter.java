package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityStepAdapter extends RecyclerView.Adapter<ActivityStepAdapter.ViewHolder> {
  
  private static final String TAG = ActivityStepAdapter.class.getSimpleName();
  
  private static final String DATE_FORMAT_IN = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT_OUT = "EEE, d MMM yyyy HH:mm:ss";
  
  private static final String NOT_SET_TIMESTAMP_GLYPH = "--.--.---- --:--:--";
  
  private boolean mDeleted = false;

  class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView iv_activity_step_dot;
    private final ImageView iv_activity_step_check_mark;
    private final AsapTextView tv_activity_step_no;

    private final AsapTextView tv_activity_step_started;
    private final AsapTextView tv_activity_step_finished;
    private final AsapTextView tv_activity_step_name;
    private final AsapTextView tv_activity_step_description;
    private final AsapTextView tv_activity_step_status;
    
    private boolean deleted;

    private ViewHolder(View itemView) {
      super(itemView);

      iv_activity_step_dot = itemView.findViewById(R.id.iv_activity_step_dot);
      iv_activity_step_check_mark = itemView.findViewById(R.id.iv_activity_step_check_mark);
      tv_activity_step_no = itemView.findViewById(R.id.tv_activity_step_no);
      tv_activity_step_started = itemView.findViewById(R.id.tv_activity_step_started);
      tv_activity_step_finished = itemView.findViewById(R.id.tv_activity_step_finished);
      tv_activity_step_name = itemView.findViewById(R.id.tv_activity_step_name);
      tv_activity_step_description = itemView.findViewById(R.id.tv_activity_step_description);
      tv_activity_step_status = itemView.findViewById(R.id.tv_activity_step_status);
    }
  }

  private final Context mContext;
  private final LayoutInflater mInflater;
  private List<ActivityStep> mActivityStepItems;
  private final SimpleDateFormat mSdfIn;
  private final SimpleDateFormat mSdfOut;
  private Date mDateMin;
  
  public ActivityStepAdapter(Context ctx) {
    mContext = ctx;
    mInflater = LayoutInflater.from(mContext);
    mSdfIn = new SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault());
    mSdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
    try {
      mDateMin = mSdfIn.parse("0001-01-01 00:00:00");
    } catch (ParseException e) {
      Log.e(TAG, e.getMessage());
    }
  }
  
  @Override
  public ActivityStepAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = mInflater.inflate(R.layout.step_activity, parent, false);
    return new ActivityStepAdapter.ViewHolder(itemView);
  }
  
  @Override
  public void onBindViewHolder(ActivityStepAdapter.ViewHolder holder, int position) {
    Log.d(TAG, "onBindViewHolder()");
    if (mActivityStepItems != null) {

      ActivityStep current = mActivityStepItems.get(position);

      holder.setIsRecyclable(false);
      if (mDeleted) {
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        holder.tv_activity_step_status.setBackground(mContext.getResources()
          .getDrawable(R.drawable.status_deleted_bg));
  
        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }
  
        if (current.getActivityItem().getStarted() != null) {
          holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
        }
        if (current.getActivityItem().getFinished() != null) {
          holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(mContext.getResources().getString(R.string.label_deleted));
      } else if (current.getTaskStatus().equals(TaskStatus.PENDING)) {
        Log.d(TAG, "TaskStatus.PENDING");
  
        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_pending_bg));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_running_bg));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_finished_bg));
        }
        /*
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        holder.iv_activity_step_check_mark.setVisibility(View.GONE);
        holder.tv_activity_step_no.setVisibility(View.VISIBLE);
        holder.tv_activity_step_no.setText(String.valueOf(position+1));
        holder.tv_activity_step_status.setBackground(mContext.getResources()
          .getDrawable(R.drawable.status_pending_bg));
*/
        if (current.getActivityItem().getStarted() != null) {
          if (current.getActivityItem().getStarted().compareTo(mDateMin) == 0) {
            holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
          } else {
            holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
          }
        } else {
          holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
        }
        if (current.getActivityItem().getFinished() != null) {
          if (current.getActivityItem().getFinished().compareTo(mDateMin) == 0) {
            holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
          } else {
            holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
          }
        } else {
          holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.RUNNING)) {
        Log.d(TAG, "TaskStatus.RUNNING");
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        
        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_pending_bg));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_running_bg));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
          holder.tv_activity_step_status.setBackground(mContext.getResources().getDrawable(R.drawable.status_finished_bg));
        }

        if (current.getActivityItem().getStarted() != null) {
          if (current.getActivityItem().getStarted().compareTo(mDateMin) == 0) {
            holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
          } else {
            holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
          }
        } else {
          holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
        }
        if (current.getActivityItem().getFinished() != null) {
          if (current.getActivityItem().getFinished().compareTo(mDateMin) == 0) {
            holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
          } else {
            holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
          }
        } else {
          holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.CMR)) {
        Log.d(TAG, "TaskStatus.CMR");
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        holder.tv_activity_step_status.setBackground(mContext.getResources()
          .getDrawable(R.drawable.status_finished_bg));

        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }

        if (current.getActivityItem().getStarted() != null) {
          holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
        }
        if (current.getActivityItem().getFinished() != null) {
          holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.FINISHED)) {
        Log.d(TAG, "TaskStatus.FINISHED");
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        holder.tv_activity_step_status.setBackground(mContext.getResources()
          .getDrawable(R.drawable.status_finished_bg));

        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }

        if (current.getActivityItem().getStarted() != null) {
          holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
        }
        if (current.getActivityItem().getFinished() != null) {
          holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      }
    } else {

    }
  }

  public void setActivityStepItems(List<ActivityStep> items, boolean deleted) {
    mActivityStepItems = items;
    mDeleted = deleted;
    notifyDataSetChanged();
  }

  // getItemCount() is called many times, and when it is first called,
  // mActivityStepItems has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mActivityStepItems != null)
      return mActivityStepItems.size();
    else return 0;
  }
}
