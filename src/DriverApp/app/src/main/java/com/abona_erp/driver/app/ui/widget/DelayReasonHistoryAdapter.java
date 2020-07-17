package com.abona_erp.driver.app.ui.widget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.DelayReasonItem;
import com.abona_erp.driver.app.data.model.DelaySource;

import java.util.List;

public class DelayReasonHistoryAdapter extends
  RecyclerView.Adapter<DelayReasonHistoryAdapter.ViewHolder> {
  
  private List<DelayReasonItem> mDataset;
  
  public DelayReasonHistoryAdapter(List<DelayReasonItem> items) {
    this.mDataset = items;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delay_reason_history_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    if (i % 2 == 1) {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#f5f6fa"));
    } else {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#dcdde1"));
    }
    
    DelayReasonItem item = mDataset.get(i);
    
    if (item.getCode() != null && item.getCode() >= 0) {
      viewHolder.tv_delay_reason_code.setText(String.valueOf(item.getCode()));
    }
    if (item.getDelaySource() != null) {
      if (item.getDelaySource().equals(DelaySource.DISPATCHER)) {
        viewHolder.tv_delay_reason_source.setText("Dispatcher");
      } else if (item.getDelaySource().equals(DelaySource.CUSTOMER)) {
        viewHolder.tv_delay_reason_source.setText("Customer");
      } else if (item.getDelaySource().equals(DelaySource.DRIVER)) {
        viewHolder.tv_delay_reason_source.setText("Driver");
      } else if (item.getDelaySource().equals(DelaySource.NA)) {
        viewHolder.tv_delay_reason_source.setText("NA");
      }
    }
    if (item.getTranslatedReasonText() != null) {
      viewHolder.tv_delay_reason_text.setText(item.getTranslatedReasonText());
    } else {
      if (item.getReasonText() != null) {
        viewHolder.tv_delay_reason_text.setText(item.getReasonText());
      }
    }
    if (item.getDelayInMinutes() != null && item.getDelayInMinutes() >= 0) {
      viewHolder.tv_delay_reason_in_minutes.setText(String.valueOf(item.getDelayInMinutes()));
    }
    if (item.getComment() != null) {
      viewHolder.tv_delay_reason_comment.setText(item.getComment());
    }
  }
  
  @Override
  public int getItemCount() {
    return mDataset.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public final AsapTextView tv_delay_reason_code;
    public final AsapTextView tv_delay_reason_source;
    public final AsapTextView tv_delay_reason_text;
    public final AsapTextView tv_delay_reason_in_minutes;
    public final AsapTextView tv_delay_reason_comment;
    
    public LinearLayout ll_root_item;
    
    public ViewHolder(View v) {
      super(v);
      tv_delay_reason_code = (AsapTextView)v.findViewById(R.id.tv_delay_reason_code);
      tv_delay_reason_source = (AsapTextView)v.findViewById(R.id.tv_delay_reason_source);
      tv_delay_reason_text = (AsapTextView)v.findViewById(R.id.tv_delay_reason_text);
      tv_delay_reason_in_minutes = (AsapTextView)v.findViewById(R.id.tv_delay_reason_in_minutes);
      tv_delay_reason_comment = (AsapTextView)v.findViewById(R.id.tv_delay_reason_comment);
      ll_root_item = (LinearLayout)v.findViewById(R.id.ll_root_item);
    }
  }
}
