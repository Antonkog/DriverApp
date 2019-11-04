package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ActivityStepAdapter extends RecyclerView.Adapter<ActivityStepAdapter.ViewHolder> {
  
  private static final String TAG = ActivityStepAdapter.class.getSimpleName();

  class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView iv_activity_step_dot;
    private final ImageView iv_activity_step_check_mark;
    private final AsapTextView tv_activity_step_no;

    private final AsapTextView tv_activity_step_started;
    private final AsapTextView tv_activity_step_finished;
    private final AsapTextView tv_activity_step_name;
    private final AsapTextView tv_activity_step_description;
    private final AsapTextView tv_activity_step_status;

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
  
  public ActivityStepAdapter(Context ctx) {
    mContext = ctx;
    mInflater = LayoutInflater.from(mContext);
  }
  
  @Override
  public ActivityStepAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = mInflater.inflate(R.layout.step_activity, parent, false);
    return new ActivityStepAdapter.ViewHolder(itemView);
  }
  
  @Override
  public void onBindViewHolder(ActivityStepAdapter.ViewHolder holder, int position) {
    if (mActivityStepItems != null) {

      ActivityStep current = mActivityStepItems.get(position);

      holder.setIsRecyclable(false);
      if (current.getTaskStatus().equals(TaskStatus.PENDING)) {
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);
        holder.iv_activity_step_check_mark.setVisibility(View.GONE);
        holder.tv_activity_step_no.setVisibility(View.VISIBLE);
        holder.tv_activity_step_no.setText(String.valueOf(position+1));

        if (current.getActivityItem().getStarted() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getStarted());
            holder.tv_activity_step_started.setText(dateTime);
          }
        }
        if (current.getActivityItem().getFinished() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getFinished());
            holder.tv_activity_step_finished.setText(dateTime);
          }
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.RUNNING)) {
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);

        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }

        if (current.getActivityItem().getStarted() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getStarted());
            holder.tv_activity_step_started.setText(dateTime);
          }
        }
        if (current.getActivityItem().getFinished() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getFinished());
            holder.tv_activity_step_finished.setText(dateTime);
          }
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.CMR)) {
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);

        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }

        if (current.getActivityItem().getStarted() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getStarted());
            holder.tv_activity_step_started.setText(dateTime);
          }
        }
        if (current.getActivityItem().getFinished() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getFinished());
            holder.tv_activity_step_finished.setText(dateTime);
          }
        }
        if (current.getActivityItem().getName() != null)
          holder.tv_activity_step_name.setText(current.getActivityItem().getName());
        if (current.getActivityItem().getDescription() != null)
          holder.tv_activity_step_description.setText(current.getActivityItem().getDescription());
        holder.tv_activity_step_status.setText(current.getActivityItem().getStatus().toString());
      } else if (current.getTaskStatus().equals(TaskStatus.FINISHED)) {
        holder.iv_activity_step_dot.setVisibility(View.VISIBLE);

        if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING) || current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          holder.iv_activity_step_check_mark.setVisibility(View.GONE);
          holder.tv_activity_step_no.setVisibility(View.VISIBLE);
          holder.tv_activity_step_no.setText(String.valueOf(position+1));
        } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.tv_activity_step_no.setVisibility(View.GONE);
          holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
        }

        if (current.getActivityItem().getStarted() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getStarted());
            holder.tv_activity_step_started.setText(dateTime);
          }
        }
        if (current.getActivityItem().getFinished() != null) {
          synchronized (this) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
            String dateTime = sdf.format(current.getActivityItem().getFinished());
            holder.tv_activity_step_finished.setText(dateTime);
          }
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

  public void setActivityStepItems(List<ActivityStep> items) {
    mActivityStepItems = items;
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
