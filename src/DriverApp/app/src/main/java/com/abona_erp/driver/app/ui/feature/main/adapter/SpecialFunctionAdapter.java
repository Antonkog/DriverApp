package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.SpecialActivities;
import com.abona_erp.driver.app.data.model.SpecialFunction;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.util.List;

public class SpecialFunctionAdapter extends RecyclerView.Adapter<SpecialFunctionAdapter.ViewHolder> {
  
  // Store a member variable for the Special Functions.
  private List<SpecialActivities> mSpecialActivities;
  
  private SpecialFunctionListener mListener;
  
  public SpecialFunctionAdapter(List<SpecialActivities> specialActivities, SpecialFunctionListener listener) {
    mSpecialActivities = specialActivities;
    mListener = listener;
  }
  
  @Override
  public SpecialFunctionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    
    View itemView = inflater.inflate(R.layout.item_special_function, parent, false);
    
    ViewHolder viewHolder = new ViewHolder(itemView);
    return viewHolder;
  }
  
  @Override
  public void onBindViewHolder(SpecialFunctionAdapter.ViewHolder holder, int position) {
  
    SpecialActivities item = mSpecialActivities.get(position);
    
    if (item.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
      holder.tvTaskName.setText("SCAN BARCODE");
    } else if (item.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
      holder.tvTaskName.setText("TAKE CMR IMAGE");
    } else if (item.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
      holder.tvTaskName.setText("TAKE SHIPMENT IMAGE");
    }
    
    if (item.getSpecialActivityResults() != null && item.getSpecialActivityResults().size() > 0) {
      holder.ivDone.setVisibility(View.VISIBLE);
      holder.btnDelete.setVisibility(View.VISIBLE);
      if (item.getSpecialActivityResults().size() > 0) {
        if (item.getSpecialActivityResults().get(0).getResultString1() != null) {
          holder.tvResult.setText(item.getSpecialActivityResults().get(0).getResultString1());
        }
      }
    } else {
      
      holder.btnDelete.setVisibility(View.INVISIBLE);
      holder.tvTaskName.setTextColor(Color.parseColor("#1F1C2C"));
      for (int i = 0; i < mSpecialActivities.size(); i++) {
        if (mSpecialActivities.get(i).getSpecialActivityResults() == null) {
          holder.tvTaskName.setTextColor(Color.parseColor("#E30613"));
          if (mSpecialActivities.get(i).getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
            if (mListener != null) {
              mListener.changeScreen(0, i);
            }
          } else {
            if (mListener != null) {
              mListener.changeScreen(1, i);
            }
          }
        } else if (mSpecialActivities.get(i).getSpecialActivityResults().size() > 0 && mSpecialActivities.get(i).getSpecialActivityResults().get(0).getResultString1() == null) {
          holder.tvTaskName.setTextColor(Color.parseColor("#E30613"));
          if (mSpecialActivities.get(i).getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
            if (mListener != null) {
              mListener.changeScreen(0, i);
            }
          } else {
            if (mListener != null) {
              mListener.changeScreen(1, i);
            }
          }
        } else if (mSpecialActivities.get(i).getSpecialActivityResults().size() > 0 && mSpecialActivities.get(i).getSpecialActivityResults().get(0).getResultString1().length() == 0) {
          holder.tvTaskName.setTextColor(Color.parseColor("#E30613"));
          if (mSpecialActivities.get(i).getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
            if (mListener != null) {
              mListener.changeScreen(0, i);
            }
          } else {
            if (mListener != null) {
              mListener.changeScreen(1, i);
            }
          }
        }
      }
      
      holder.ivDone.setVisibility(View.INVISIBLE);
      holder.tvResult.setText("");
    }
  }
  
  @Override
  public int getItemCount() {
    return mSpecialActivities.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    private AsapTextView tvTaskName;
    private AppCompatImageView ivDone;
    private AsapTextView tvResult;
    private AppCompatImageButton btnDelete;
    
    public ViewHolder(View itemView) {
      super(itemView);
      
      tvTaskName = (AsapTextView)itemView.findViewById(R.id.tvTaskName);
      ivDone = (AppCompatImageView)itemView.findViewById(R.id.ivDone);
      tvResult = (AsapTextView)itemView.findViewById(R.id.tvResult);
      btnDelete = (AppCompatImageButton)itemView.findViewById(R.id.btnDelete);
    }
  }
  
  public interface SpecialFunctionListener {
    
    void changeScreen(int type, int position);
  }
}
