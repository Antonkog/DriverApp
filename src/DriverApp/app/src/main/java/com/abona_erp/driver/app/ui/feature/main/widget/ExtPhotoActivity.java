package com.abona_erp.driver.app.ui.feature.main.widget;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.ExtImageEvent;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.ExtGalleryAdapter;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Hdr;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExtPhotoActivity extends AppCompatActivity
  implements GalleryListener {
  
  private static final String APP_FOLDER = "/DriverApp/";
  private static final String PATH_SLASH = "/";
  
  private AppCompatImageButton btnBack;
  private AppCompatImageView takePhoto;
  private RecyclerView rvGallery;
  
  private CameraView camera;
  
  private ArrayList<String> mPhotoUrls = new ArrayList<String>();
  private ExtGalleryAdapter mGalleryViewAdapter = new ExtGalleryAdapter(this);
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ext_activity_photo_layout);
    
    File createFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
      + File.separator
      + "DriverApp"
      + File.separator
      + "temp");
    if (!createFile.exists() && !createFile.isDirectory()) {
      createFile.mkdirs();
    }
    
    initializeComponents();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    boolean fValid = true;
    for (int grantResult : grantResults) {
      fValid = fValid && grantResult == PackageManager.PERMISSION_GRANTED;
    }
    if (fValid && !camera.isOpened()) {
      camera.open();
    }
  }
  
  private void initializeComponents() {
  
    btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
  
        //App.eventBus.post(new ExtImageEvent(mGalleryViewAdapter.getData()));
        EventBus.getDefault().post(new ExtImageEvent(mGalleryViewAdapter.getData()));
  
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
      }
    });
  
    rvGallery = (RecyclerView)findViewById(R.id.rvGalleryView);
    LinearLayoutManager llmGallery = new LinearLayoutManager(getApplicationContext(),
      LinearLayoutManager.HORIZONTAL, false);
    rvGallery.setLayoutManager(llmGallery);
    rvGallery.setAdapter(mGalleryViewAdapter);
  
    camera = (CameraView)findViewById(R.id.camera);
    camera.setLifecycleOwner(this);
    camera.addCameraListener(new Listener());
  
    camera.setAutoFocusResetDelay(0);
    camera.setHdr(Hdr.OFF);
    camera.setFlash(Flash.OFF);
    camera.open();
    
    takePhoto = (AppCompatImageView)findViewById(R.id.takePhoto);
    takePhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        camera.takePicture();
      }
    });
  }
  
  @Override
  public void onGallerySelected(int position) {
  
  }
  
  private class Listener extends CameraListener {
  
    @Override
    public void onPictureTaken(@NonNull PictureResult result) {
      
      result.toBitmap(1600, 1200, new BitmapCallback() {
        @Override
        public void onBitmapReady(@Nullable Bitmap bitmap) {
    
          if (bitmap == null) return;
  
          File photoFile = null;
          try {
            photoFile = saveImage(bitmap);
          } catch (IOException e) {
            e.printStackTrace();
          }
  
          if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
              BuildConfig.APPLICATION_ID + ".provider",
              photoFile);
  
            ContentValues image = getImageContent(photoFile);
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            
            // Add to list.
            //photoFile.getAbsolutePath().
            Log.i("*******", "PHOTO URL " + photoFile.getAbsolutePath());
            mPhotoUrls.add(photoFile.getAbsolutePath());
            mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
          }
        }
      });
    }
  }
  
  public ContentValues getImageContent(File parent) {
    ContentValues image = new ContentValues();
    image.put(MediaStore.Images.Media.TITLE, R.string.app_name);
    image.put(MediaStore.Images.Media.DISPLAY_NAME, parent.getName());
    image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
    image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
    image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
    return image;
  }
  
  private File saveImage(Bitmap bmp) throws IOException {
  
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
  
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    File f = createImageFile();
  
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(bytes.toByteArray());
    fos.close();
    return f;
  }
  
  private File createImageFile() throws IOException {
  
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
      Locale.getDefault()).format(new Date());
  
    String mFileName = timeStamp;
    File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    File image = File.createTempFile(mFileName, ".jpg", storageDir);
    return image;
  }
}
