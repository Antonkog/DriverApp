package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LastActivityAdapter extends RecyclerView.Adapter<LastActivityAdapter.ViewHolder> {

  private static final String TAG = LastActivityAdapter.class.getSimpleName();

  class ViewHolder extends RecyclerView.ViewHolder {

    private final AsapTextView tv_Status_Name;
    private final AsapTextView tv_Order_No;
    private final AsapTextView tv_Timestamp;

    private ViewHolder(View itemView) {
      super(itemView);

      tv_Status_Name = itemView.findViewById(R.id.tv_last_activity_status_name);
      tv_Order_No = itemView.findViewById(R.id.tv_last_activity_order_no);
      tv_Timestamp = itemView.findViewById(R.id.tv_last_activity_timestamp);
    }
  }

  private final LayoutInflater mInflater;
  private List<LastActivity> mLastActivityItems;

  public LastActivityAdapter(Context ctx) {
    mInflater = LayoutInflater.from(ctx);
  }

  @Override
  public LastActivityAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = mInflater.inflate(R.layout.last_activity_item, parent, false);
    return new LastActivityAdapter.ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(LastActivityAdapter.ViewHolder holder, int position) {
    if (mLastActivityItems != null) {

      LastActivity current = mLastActivityItems.get(position);

      //holder.setIsRecyclable(false);
      holder.tv_Status_Name.setText(current.getStatusName());
      synchronized (this) {
        holder.tv_Order_No.setText(AppUtils.parseOrderNo(current.getOrderNo()));
      }

      synchronized (this) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
          Locale.getDefault());
        String dateTime = sdf.format(current.getCreatedAt());
        holder.tv_Timestamp.setText(dateTime);
      }

      LastActivity lastActivity = mLastActivityItems.get(position);
    } else {
      holder.tv_Status_Name.setText("null");
      holder.tv_Order_No.setText("null");
      holder.tv_Timestamp.setText("null");
    }
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
