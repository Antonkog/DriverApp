package com.abona_erp.driver.app.ui.widget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.EnumNoteType;
import com.abona_erp.driver.app.data.model.NotesItem;

import java.util.List;

public class NotesDataAdapter extends RecyclerView.Adapter<NotesDataAdapter.ViewHolder> {
  
  private List<NotesItem> mDataset;
  
  public NotesDataAdapter(List<NotesItem> myDataset) {
    this.mDataset = myDataset;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    if (i % 2 == 1) {
      // regular row/item:
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#f5f6fa"));
    } else {
      // alternate row/item:
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#dcdde1"));
    }
    viewHolder.tv_note.setText(mDataset.get(i).getNote());
    if (mDataset.get(i).getNoteType().equals(EnumNoteType.HIGH)) {
      viewHolder.iv_priority.setVisibility(View.VISIBLE);
    } else {
      viewHolder.iv_priority.setVisibility(View.GONE);
    }
  }
  
  @Override
  public int getItemCount() {
    return mDataset.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public AsapTextView tv_note;
    public AppCompatImageView iv_priority;
    public LinearLayout ll_root_item;
    
    public ViewHolder(View v) {
      super(v);
      tv_note = (AsapTextView)v.findViewById(R.id.tv_note);
      iv_priority = (AppCompatImageView)v.findViewById(R.id.iv_priority);
      ll_root_item = (LinearLayout)v.findViewById(R.id.ll_root_item);
    }
  }
}
