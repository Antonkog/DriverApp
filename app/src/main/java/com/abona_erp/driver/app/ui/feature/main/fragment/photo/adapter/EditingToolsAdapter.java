package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.ToolType;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {
  
  private List<ToolModel> mToolList = new ArrayList<>();
  private OnItemSelected mOnItemSelected;
  
  public EditingToolsAdapter(OnItemSelected onItemSelected) {
    mOnItemSelected = onItemSelected;
    mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
    mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
    mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
  }
  
  public interface OnItemSelected {
    void onToolSelected(ToolType toolType);
  }
  
  class ToolModel {
    private String mToolName;
    private int mToolIcon;
    private ToolType mToolType;
    
    ToolModel(String toolName, int toolIcon, ToolType toolType) {
      mToolName = toolName;
      mToolIcon = toolIcon;
      mToolType = toolType;
    }
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.row_editing_tools, parent, false);
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ToolModel item = mToolList.get(position);
    holder.tv_Tool.setText(item.mToolName);
    holder.iv_ToolIcon.setImageResource(item.mToolIcon);
  }
  
  @Override
  public int getItemCount() {
    return mToolList.size();
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    AppCompatImageView iv_ToolIcon;
    AsapTextView tv_Tool;
    
    ViewHolder(View itemView) {
      super(itemView);
      iv_ToolIcon = itemView.findViewById(R.id.iv_tool_icon);
      tv_Tool = itemView.findViewById(R.id.tv_tool);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
        }
      });
    }
  }
}
