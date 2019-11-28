package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.ImageEvent;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryViewAdapter extends RecyclerView.Adapter<GalleryViewAdapter.ViewHolder> {
  
  private static final String TAG = GalleryViewAdapter.class.getSimpleName();
  
  private Context mContext;
  private GalleryListener mGalleryListener;
  private List<String> mList = new ArrayList<>();
  
  public GalleryViewAdapter(Context context, GalleryListener galleryListener) {
    mContext = context;
    mGalleryListener = galleryListener;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image_view, parent, false);
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Log.d(TAG, "onBindViewHolder()");
    if (mList == null)
      return;
    
    String current = mList.get(position);
    holder.setIsRecyclable(false);
  
    File imgFile = new File(current);
    if (imgFile.exists()) {
      Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
      holder.iv_gallery_image_view.setImageBitmap(bmp);
    }
  }
  
  private Bitmap getBitmapFromAsset(Context context, String strName) {
    AssetManager assetManager = context.getAssets();
    InputStream inputStream = null;
    try {
      inputStream = assetManager.open(strName);
      return BitmapFactory.decodeStream(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    private final AppCompatImageView iv_gallery_image_view;
    private final AppCompatImageView iv_gallery_delete_image;
    
    private Bitmap mBitmap;
    
    ViewHolder(View itemView) {
      super(itemView);
      
      iv_gallery_image_view = itemView.findViewById(R.id.iv_gallery_image_view);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mGalleryListener.onGallerySelected(getLayoutPosition());
        }
      });
      
      iv_gallery_delete_image = itemView.findViewById(R.id.iv_gallery_delete_image);
      iv_gallery_delete_image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          ImageEvent event = new ImageEvent(null);
          event.setPosition(getLayoutPosition());
          App.eventBus.post(event);
        }
      });
    }
  }
  
  public void setPhotoItems(List<String> items) {
    mList = items;
    notifyDataSetChanged();
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mList has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mList != null) {
      return mList.size();
    } else {
      return 0;
    }
  }
}
