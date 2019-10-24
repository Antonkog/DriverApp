package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotifyViewAdapter extends RecyclerView.Adapter<NotifyViewAdapter.ViewHolder> {
  
  private List<Notify> notifyList;
  private Context context;
  
  public NotifyViewAdapter(List<Notify> list, Context ctx) {
    notifyList = list;
    context = ctx;
  }
  
  @Override
  public int getItemCount() {
    return notifyList.size();
  }
  
  @Override
  public NotifyViewAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
    
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.task_item, parent, false);
    
    NotifyViewAdapter.ViewHolder viewHolder =
      new NotifyViewAdapter.ViewHolder(view);
    return viewHolder;
  }
  
  @Override
  public void onBindViewHolder(NotifyViewAdapter.ViewHolder holder, int position) {
    final int itemPos = position;
    final Notify notify = notifyList.get(position);
    
    try {
      // Read content of notify.
      String jsonText = notify.getData();
      JSONObject jsonRoot = new JSONObject(jsonText);
  
      JSONObject jsonTaskItem = jsonRoot.getJSONObject("TaskItem");
      String orderNo = jsonTaskItem.getString("OrderNo");
      String description = jsonTaskItem.getString("Description");
  
      holder.tvOrderNo.setText(orderNo);
      holder.tvDescription.setText(description);
      
    } catch (JSONException ignored) {
    }
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public AsapTextView tvOrderNo;
    public AsapTextView tvDescription;
    
    public ViewHolder(View view) {
      super(view);
      tvOrderNo = (AsapTextView) view.findViewById(R.id.tv_order_no);
      tvDescription = (AsapTextView) view.findViewById(R.id.tv_description);
    }
  }
}
