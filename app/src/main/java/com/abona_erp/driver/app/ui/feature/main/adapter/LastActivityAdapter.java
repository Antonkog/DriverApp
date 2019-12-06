package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LastActivityAdapter extends RecyclerView.Adapter<LastActivityAdapter.ViewHolder> {

  private static final String TAG = LastActivityAdapter.class.getSimpleName();

  class ViewHolder extends RecyclerView.ViewHolder {
    
    final AsapTextView tv_Customer;
    final AsapTextView tv_OrderNo;
    final AsapTextView tv_Timestamp;
    final AsapTextView tv_StatusType;
    final AsapTextView tv_TaskId;
    
    final AppCompatImageView iv_icon;
    final AppCompatImageView iv_done_all;
    
    private ViewHolder(View itemView) {
      super(itemView);
      
      tv_Customer = itemView.findViewById(R.id.tv_last_activity_customer);
      tv_OrderNo  = itemView.findViewById(R.id.tv_last_activity_order_no);
      tv_Timestamp = itemView.findViewById(R.id.tv_last_activity_timestamp);
      tv_StatusType = itemView.findViewById(R.id.tv_last_activity_status_type);
      tv_TaskId = itemView.findViewById(R.id.tv_last_activity_task_id);
      
      iv_icon = itemView.findViewById(R.id.iv_last_activity_icon);
      iv_done_all = itemView.findViewById(R.id.iv_last_activity_done_all);
    }
  
    public void bind(int mandantID, int taskID, final OnItemClickListener listener) {
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onItemClick(mandantID, taskID);
        }
      });
    }
  }

  private final LayoutInflater mInflater;
  private List<LastActivity> mLastActivityItems;
  private OnItemClickListener listener;
  private Context mContext;
  
  public interface OnItemClickListener {
    void onItemClick(int mandantID, int taskId);
  }

  public LastActivityAdapter(Context ctx) {
    mContext = ctx;
    mInflater = LayoutInflater.from(ctx);
  }

  @Override
  public LastActivityAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = mInflater.inflate(R.layout.item_last_activity, parent, false);
    return new LastActivityAdapter.ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(LastActivityAdapter.ViewHolder holder, int position) {
    if (mLastActivityItems != null) {

      LastActivity current = mLastActivityItems.get(position);

      holder.setIsRecyclable(false);
      holder.tv_Customer.setText(current.getCustomer());
      holder.tv_OrderNo.setText(current.getOrderNo());
  
      if (current.getConfirmStatus() == 0) {
        // NOT CONFIRMED:
        holder.iv_done_all.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_40));
      } else if (current.getConfirmStatus() == 1) {
        // CONFIRMED BY DEVICE:
        holder.iv_done_all.setColorFilter(ContextCompat.getColor(mContext, R.color.clrLabelChanged));
      } else if (current.getConfirmStatus() == 2) {
        // CONFIRMED BY USER:
        holder.iv_done_all.setColorFilter(ContextCompat.getColor(mContext, R.color.clrTaskFinished));
      }
      
      if (current.getStatusType() == 0) {
        holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_new));
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_new));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_notifications));
      } else if (current.getStatusType() == 1) {
        holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_updated));
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_updated));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_down_arrow));
      } else if (current.getStatusType() == 2) {
        holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_changed));
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_changed));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_star));
      } else if (current.getStatusType() == 3) {
        holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_done));
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_deleted));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_done));
      } else if (current.getStatusType() == 5) {
        holder.tv_StatusType.setText("TASK CHANGED BY ABONA");
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_changed_by_abona));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_star));
      } else if (current.getStatusType() == 6) {
        holder.tv_StatusType.setText("ACTIVITY CHANGED BY ABONA");
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_changed_by_abona));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_star));
      } else if (current.getStatusType() == 9) {
        // DELETED:
        holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_deleted));
        holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_deleted));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_delete));
      }
      
      synchronized (this) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        if (current.getModifiedAt() != null)
          holder.tv_Timestamp.setText(sdf.format(current.getModifiedAt()));
      }
      if (current.getTaskId() > 0) {
        holder.tv_TaskId.setText(String.valueOf(current.getTaskId()));
      }
      
/*
      holder.bind(current.getMandantOid(), current.getTaskOid(), listener);
      
      if (current.getStatusType() == 0) {
        holder.tv_Status_Name.setTextColor(ContextUtils.getApplicationContext().getResources().getColor(R.color.clrLabelNew));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_notifications));
        holder.tv_Status_Name.setText(mContext.getResources().getString(R.string.label_new));
      } else if (current.getStatusType() == 1) {
        // CHANGED:
        holder.tv_Status_Name.setTextColor(ContextUtils.getApplicationContext().getResources().getColor(R.color.clrLabelChanged));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_star));
        holder.tv_Status_Name.setText(mContext.getResources().getString(R.string.label_changed));
      } else if (current.getStatusType() == 2) {
        // UPDATED:
        holder.tv_Status_Name.setTextColor(ContextUtils.getApplicationContext().getResources().getColor(R.color.clrLabelUpdated));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_down_arrow));
        holder.tv_Status_Name.setText(mContext.getResources().getString(R.string.label_updated));
      } else if (current.getStatusType() == 9) {
        // DELETED:
        holder.tv_Status_Name.setTextColor(ContextUtils.getApplicationContext().getResources().getColor(R.color.clrLabelDeleted));
        holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_delete));
        holder.tv_Status_Name.setText(mContext.getResources().getString(R.string.label_deleted));
      }
 
      if (current.getDescription() != null) {
        holder.tv_Description.setVisibility(View.VISIBLE);
        holder.tv_Description.setText(current.getDescription());
      } else {
        holder.tv_Description.setVisibility(View.GONE);
      }
      synchronized (this) {
        holder.tv_Order_No.setText(AppUtils.parseOrderNo(current.getOrderNo()));
      }
      
      if (current.getCreatedAt() != null) {
        synchronized (this) {
          SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
            Locale.getDefault());
          String dateTime = sdf.format(current.getCreatedAt());
          holder.tv_Timestamp.setText(dateTime);
        }
      }
      
      LastActivity lastActivity = mLastActivityItems.get(position);*/
    }
  }
  
  public void setOnItemListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  public void setLastActivityItems(List<LastActivity> items) {
    mLastActivityItems = items;
    notifyDataSetChanged();
  }

  // getItemCount() is called many times, and when it is first called,
  // mLastActivityItems has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mLastActivityItems != null)
      return mLastActivityItems.size();
    else return 0;
  }
}
