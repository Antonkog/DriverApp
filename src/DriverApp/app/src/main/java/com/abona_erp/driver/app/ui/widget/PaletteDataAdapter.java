package com.abona_erp.driver.app.ui.widget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.PalletExchange;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.List;

public class PaletteDataAdapter extends RecyclerView.Adapter<PaletteDataAdapter.ViewHolder> {
  
  private List<PalletExchange> mDataset;
  
  public PaletteDataAdapter(List<PalletExchange> items) {
    this.mDataset = items;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.palette_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    if (i % 2 == 1) {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#dcdde1"));
    } else {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#f5f6fa"));
    }
    
    if (mDataset.get(i).getPalletsAmount() != null)
      viewHolder.tv_number_of_paletts.setText(String.valueOf(mDataset.get(i).getPalletsAmount()));
    if (mDataset.get(i).isDPL() != null)
      viewHolder.tv_dpl.setText(mDataset.get(i).isDPL() ? ContextUtils.getApplicationContext().getResources().getString(R.string.yes) : ContextUtils.getApplicationContext().getResources().getString(R.string.no));
  }
  
  @Override
  public int getItemCount() {
    return mDataset.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public AsapTextView tv_number_of_paletts;
    public AsapTextView tv_dpl;
    public LinearLayout ll_root_item;
    
    public ViewHolder(View v) {
      super(v);
      tv_number_of_paletts = (AsapTextView)v.findViewById(R.id.tv_number_of_paletts);
      tv_dpl = (AsapTextView)v.findViewById(R.id.tv_dpl);
      ll_root_item = (LinearLayout)v.findViewById(R.id.ll_root_item);
    }
  }
}
