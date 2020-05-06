package com.abona_erp.driver.app.ui.feature.main.fragment.protocol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProtocolViewAdapter extends RecyclerView.Adapter<ProtocolViewAdapter.ViewHolder> {
  
  private List<LogItem> mLogItems = new ArrayList<>();
  
  private static final String UTC = "UTC";
  
  private int clrVerbose = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_verbose);
  private int clrSilent = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_silent);
  private int clrInfo = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_info);
  private int clrDebug = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_debug);
  private int clrWarning = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_warning);
  private int clrError = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_error);
  private int clrFatal = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_fatal);
  private int clrAssert = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.priority_assert);
  
  private SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
  private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
  
  public ProtocolViewAdapter() {
    sdfDate.setTimeZone(TimeZone.getTimeZone(UTC));
    sdfTime.setTimeZone(TimeZone.getTimeZone(UTC));
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.item_log_view, parent, false);
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
  
    holder.setIsRecyclable(false);
    
    LogItem item = mLogItems.get(position);
    holder.tv_lvl.setBackgroundColor(getLevelColor(item.getLevel()));
    holder.tv_lvl.setText(getLevelText(item.getLevel()));
    holder.tv_title.setText(item.getTitle());
    holder.tv_message.setText(item.getMessage());
    
    if (item.getCreatedAt() != null) {
      holder.tv_date.setText(sdfDate.format(item.getCreatedAt()));
      holder.tv_time.setText(sdfTime.format(item.getCreatedAt()) + " " + UTC);
    }
  }
  
  public void setItems(List<LogItem> items) {
    clear();
    addItems(items);
  }
  
  @Override
  public int getItemCount() {
    if (mLogItems != null) {
      return mLogItems.size();
    } else {
      return 0;
    }
  }
  
  private void addItems(List<LogItem> items) {
    int startPosition = mLogItems.size();
    mLogItems = items;
    notifyItemRangeInserted(startPosition, items.size());
  }
  
  private void clear() {
    int count = mLogItems.size();
    mLogItems.clear();
    notifyItemRangeRemoved(0, count);
  }
  
  private String getLevelText(LogLevel level) {
    switch (level) {
      case VERBOSE: return "V";
      case SILENT:  return "S";
      case INFO:    return "I";
      case DEBUG:   return "D";
      case WARNING: return "W";
      case ERROR:   return "E";
      case FATAL:   return "F";
      case ASSERT:  return "A";
      default: return "U";
    }
  }
  
  private int getLevelColor(LogLevel level) {
    switch (level) {
      case VERBOSE: return clrVerbose;
      case SILENT:  return clrSilent;
      case INFO:    return clrInfo;
      case DEBUG:   return clrDebug;
      case WARNING: return clrWarning;
      case ERROR:   return clrError;
      case FATAL:   return clrFatal;
      case ASSERT:  return clrAssert;
      default: return clrDebug;
    }
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    private final AsapTextView tv_lvl;
    private final AsapTextView tv_title;
    private final AsapTextView tv_message;
    private final AsapTextView tv_date;
    private final AsapTextView tv_time;
    
    ViewHolder(View itemView) {
      super(itemView);
      
      tv_lvl = itemView.findViewById(R.id.tv_lvl);
      tv_title = itemView.findViewById(R.id.tv_title);
      tv_message = itemView.findViewById(R.id.tv_message);
      tv_date = itemView.findViewById(R.id.tv_date);
      tv_time = itemView.findViewById(R.id.tv_time);
    }
  }
}
