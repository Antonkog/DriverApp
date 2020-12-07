package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;

import java.io.File;
import java.util.ArrayList;

public class ExtGalleryAdapter extends RecyclerView.Adapter<ExtGalleryAdapter.ViewHolder> {
  
  private static final String TAG = ExtGalleryAdapter.class.getSimpleName();
  
  private Context mContext;
  private GalleryListener mGalleryListener;
  private ArrayList<String> mData = new ArrayList<String>();
  
  public ExtGalleryAdapter(GalleryListener galleryListener) {
    mGalleryListener = galleryListener;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image_view, parent, false);
    
    mContext = parent.getContext();
    
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
  
    holder.setIsRecyclable(false);
  
    File file = new File(mData.get(position));
    if (file.exists()) {
      Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
      holder.iv_gallery_image_view.setImageBitmap(thumbnail);
    }
  }
  
  public void setPhotoItems(ArrayList<String> items) {
    
    mData = items;
    notifyDataSetChanged();
  }
  
  public ArrayList<String> getData() {
    return mData;
  }
  
  @Override
  public int getItemCount() {
    if (mData != null) {
      return mData.size();
    } else {
      return 0;
    }
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
  
    private final AppCompatImageView iv_gallery_image_view;
    private final AppCompatImageView iv_gallery_delete_image;
    
    ViewHolder(View itemView) {
      super(itemView);
  
      iv_gallery_image_view = itemView.findViewById(R.id.iv_gallery_image_view);
      iv_gallery_delete_image = itemView.findViewById(R.id.iv_gallery_delete_image);
      iv_gallery_delete_image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
          alertDialog.setTitle(mContext.getResources().getString(R.string.action_delete_photo))
            .setMessage(mContext.getResources().getString(R.string.action_delete_photo))
            .setPositiveButton(mContext.getResources().getString(R.string.action_delete_photo_message),
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                  mData.remove(getLayoutPosition());
                  notifyDataSetChanged();
                  dialog.dismiss();
                }
              })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
              }
            })
            .show();
        }
      });
    }
  }
}
