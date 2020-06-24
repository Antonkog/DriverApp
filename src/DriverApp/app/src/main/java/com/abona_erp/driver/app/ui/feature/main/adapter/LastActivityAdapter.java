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
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.NotifyTapEvent;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class LastActivityAdapter extends RecyclerView.Adapter<LastActivityAdapter.ViewHolder> {

  private static final String TAG = LastActivityAdapter.class.getSimpleName();

  class ViewHolder extends RecyclerView.ViewHolder {
    
    final AsapTextView tv_Customer;
    final AsapTextView tv_OrderNo;
    final AsapTextView tv_Timestamp;
    final AsapTextView tv_StatusType;
//    final AsapTextView tv_TaskId;
    final AsapTextView tv_TaskActionType;
    
    final AppCompatImageView iv_icon;
    final AppCompatImageView iv_done_all;

    final View subItem;
    private ViewHolder(View itemView) {
      super(itemView);
      
      tv_Customer = itemView.findViewById(R.id.tv_last_activity_customer);
      tv_OrderNo  = itemView.findViewById(R.id.tv_last_activity_order_no);
      tv_Timestamp = itemView.findViewById(R.id.tv_last_activity_timestamp);
      tv_StatusType = itemView.findViewById(R.id.tv_last_activity_status_type);
//      tv_TaskId = itemView.findViewById(R.id.tv_last_activity_task_id);
      tv_TaskActionType = itemView.findViewById(R.id.tv_last_activity_action_type);
      subItem = itemView.findViewById(R.id.sub_item);
      iv_icon = itemView.findViewById(R.id.iv_last_activity_icon);
      iv_done_all = itemView.findViewById(R.id.iv_last_activity_done_all);
    }
  }

  private final LayoutInflater mInflater;
  private List<LastActivity> mLastActivityItems;
  private LastActClickListener listener;
  private Context mContext;

  public LastActivityAdapter(Context ctx, LastActClickListener listener) {
    mContext = ctx;
    this.listener = listener;
    mInflater = LayoutInflater.from(ctx);
  }

  @Override
  public LastActivityAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = mInflater.inflate(R.layout.item_task_status, parent, false);
    return new LastActivityAdapter.ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(LastActivityAdapter.ViewHolder holder, int position) {
    if (mLastActivityItems != null) {
      bind(holder, position, mLastActivityItems.get(position));
    }
  }

  private void bind(ViewHolder holder, int position, LastActivity current) {
    holder.subItem.setVisibility(current.isCurrentlySelected()? View.VISIBLE : View.GONE);
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

    if (current.getTaskActionType() == 0) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_pick_up));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_pick_up));
    } else if (current.getTaskActionType() == 1) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_drop_off));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_drop_off));
    } else if (current.getTaskActionType() == 3) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_tractor_swap));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_tractor_swap));
    } else if (current.getTaskActionType() == 4) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_delay));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_delay));
    } else if (current.getTaskActionType() == 2) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_general));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_general));
    } else if (current.getTaskActionType() == 100) {
      holder.tv_TaskActionType.setText(mContext.getResources().getString(R.string.action_type_unknown));
      holder.tv_TaskActionType.setBackground(mContext.getResources().getDrawable(R.drawable.bg_action_type_unknown));
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
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_autorenew_24px));
    } else if (current.getStatusType() == 3) {
      holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_done));
      holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_deleted));
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_done));
    } else if (current.getStatusType() == 4) {
      holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_done));
      holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_done));
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_done));
      holder.iv_done_all.setColorFilter(ContextCompat.getColor(mContext, R.color.clrTaskFinished));
    } else if (current.getStatusType() == 5) {
      holder.tv_StatusType.setText("CHANGED BY ABONA");
      holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_changed_by_abona));
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.abona_36x36));
    } else if (current.getStatusType() == 6) {
      holder.tv_StatusType.setText("CHANGED BY ABONA");
      holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_changed_by_abona));
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.abona_36x36));
    } else if (current.getStatusType() == 9) {
      // DELETED:
      holder.tv_StatusType.setText(mContext.getResources().getString(R.string.label_deleted));
      holder.tv_StatusType.setBackground(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.bg_deleted));
      holder.iv_icon.setBackgroundDrawable(ContextUtils.getApplicationContext().getResources().getDrawable(R.drawable.ic_delete));
    }

    holder.itemView.setOnClickListener(v -> {
      current.setCurrentlySelected(!current.isCurrentlySelected());
      notifyItemChanged(position);
      listener.onItemClick(mLastActivityItems.get(position).getId(), mLastActivityItems.get(position).getTaskId());
    });

    synchronized (this) {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
      if (current.getModifiedAt() != null)
        holder.tv_Timestamp.setText(sdf.format(current.getModifiedAt()));
    }

//      if (current.getTaskId() > 0) {
//        holder.tv_TaskId.setText(String.valueOf(current.getTaskId()));
//      }
  }

  public void setLastActivityItems(List<LastActivity> items) {
    mLastActivityItems = items;
    notifyDataSetChanged();
  }


  public void onItemTap(NotifyTapEvent event) {
    LinkedList <LastActivity> refreshLisst = new LinkedList<>();
    Log.e(this.getClass().getCanonicalName(), " onItemTap " + event.toString());
    for(LastActivity task : mLastActivityItems){ //todo refresh only one item
      if(task.getTaskId() == event.getTaskId()){
        refreshLisst.add(task.setSelectedAndReturn(event.isOpen()));
      } else {
        refreshLisst.add(task);
      }
    }
   mLastActivityItems.clear();
    mLastActivityItems.addAll(refreshLisst);
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
