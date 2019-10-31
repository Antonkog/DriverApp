package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.ActivityStep;

import java.util.List;

public class ActivityStepAdapter extends RecyclerView.Adapter<ActivityStepAdapter.ViewHolder> {
  
  private static final String TAG = ActivityStepAdapter.class.getSimpleName();
  
  private List<ActivityStep> activityStepList;
  private Context context;
  
  public ActivityStepAdapter(List<ActivityStep> list, Context ctx) {
    activityStepList = list;
    context = ctx;
  }
  
  @Override
  public int getItemCount() {
    return activityStepList.size();
  }
  
  @Override
  public ActivityStepAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
    
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.step_activity, parent, false);
    
    ActivityStepAdapter.ViewHolder viewHolder =
      new ActivityStepAdapter.ViewHolder(view);
    
    return viewHolder;
  }
  
  @Override
  public void onBindViewHolder(ActivityStepAdapter.ViewHolder holder, int position) {
    holder.setIsRecyclable(false);
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public ViewHolder(View view) {
      super(view);
    }
  }
}
