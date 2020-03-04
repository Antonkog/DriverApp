package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DocumentViewAdapter extends RecyclerView.Adapter<DocumentViewAdapter.ViewHolder> {
  
  private static final String TAG = DocumentViewAdapter.class.getSimpleName();
  
  private Context mContext;
  private GalleryListener mGalleryListener;
  private SparseArray mPhotos = new SparseArray();
  
  public DocumentViewAdapter(GalleryListener galleryListener) {
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
    Log.d(TAG, "onBindViewHolder");
    if (mPhotos == null) return;
    
    holder.setIsRecyclable(false);
    int key = mPhotos.keyAt(position);
    Log.i(">>>>>>>", "KEY : " + key + " URI : " + mPhotos.get(key));
    
    UploadItem uploadItem = App.getGson().fromJson((String)mPhotos.get(key),
      UploadItem.class);
    
    if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
      if (uploadItem.getUploaded()) {
        holder.cl_gallery_root.setVisibility(View.VISIBLE);
      } else {
        holder.cl_gallery_root.setVisibility(View.GONE);
        return;
      }
      File file = new File(uploadItem.getUri());
      if (file.exists()) {
        Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
        holder.iv_gallery_image_view.setImageBitmap(thumbnail);
        if (uploadItem.getModifiedAt() != null) {
          SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMMM d, yyyy h:mm:ss",
            Locale.getDefault());
          holder.tv_gallery_file_info.setText(sdf.format(uploadItem.getModifiedAt()));
        }
      } else {
        // TODO:
      }
    } else {
      // TODO:
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
  
  public void setPhotoItems(List<String> items) {
    
    if (items.size() > 0) {
      for (int i = 0; i < items.size(); i++) {
        UploadItem uploadItem = App.getGson().fromJson(items.get(i), UploadItem.class);
        if (uploadItem.getUploaded()) {
          Log.i(">>>>>>>", "PUT : " + String.valueOf(i) + " URI : " + uploadItem.getUri());
          mPhotos.put(i, App.getGson().toJson(uploadItem));
        }
      }
      notifyDataSetChanged();
    }
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mList has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mPhotos != null) {
      return mPhotos.size();
    } else {
      return 0;
    }
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
  
    private final AppCompatImageView iv_gallery_image_view;
    private final AppCompatImageView iv_gallery_delete_image;
    private final AsapTextView tv_gallery_file_info;
  
    private final ConstraintLayout cl_gallery_root;
  
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
      iv_gallery_delete_image.setVisibility(View.GONE);
  
      tv_gallery_file_info = itemView.findViewById(R.id.tv_gallery_file_info);
      cl_gallery_root = itemView.findViewById(R.id.cl_gallery_root);
    }
  }
}
