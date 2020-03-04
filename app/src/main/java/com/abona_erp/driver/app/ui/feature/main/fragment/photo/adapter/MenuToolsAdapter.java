package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.MenuToolType;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.util.ArrayList;
import java.util.List;

public class MenuToolsAdapter extends RecyclerView.Adapter<MenuToolsAdapter.ViewHolder> {
  
  private List<ToolModel> mToolList = new ArrayList<>();
  private OnItemSelected mOnItemSelected;
  
  public MenuToolsAdapter(OnItemSelected onItemSelected) {
    mOnItemSelected = onItemSelected;
    //mToolList.add(new ToolModel("Save", R.drawable.ic_save, MenuToolType.SAVE));
    mToolList.add(new ToolModel("Camera", R.drawable.ic_camera, MenuToolType.CAMERA));
    //mToolList.add(new ToolModel("Gallery", R.drawable.ic_gallery, MenuToolType.GALLERY));
    //mToolList.add(new ToolModel("Tools", R.drawable.ic_tools, MenuToolType.TOOLS));
    //mToolList.add(new ToolModel("Settings", R.drawable.ic_settings, MenuToolType.SETTINGS));
  }
  
  public interface OnItemSelected {
    void onToolSelected(MenuToolType toolType);
  }
  
  class ToolModel {
    private String mToolName;
    private int mToolIcon;
    private MenuToolType mToolType;
    
    ToolModel(String toolName, int toolIcon, MenuToolType toolType) {
      mToolName = toolName;
      mToolIcon = toolIcon;
      mToolType = toolType;
    }
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.row_menu_tools, parent, false);
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ToolModel item = mToolList.get(position);
    holder.tv_Menu.setText(item.mToolName);
    holder.iv_MenuIcon.setImageResource(item.mToolIcon);
  }
  
  @Override
  public int getItemCount() {
    return mToolList.size();
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    AppCompatImageView iv_MenuIcon;
    AsapTextView tv_Menu;
    
    ViewHolder(View itemView) {
      super(itemView);
      iv_MenuIcon = itemView.findViewById(R.id.iv_menu_icon);
      tv_Menu = itemView.findViewById(R.id.tv_menu);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
        }
      });
    }
  }
}
