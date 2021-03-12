package com.abona_erp.driver.app.ui.feature.main.fragment.specialfunction;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.SpecialActivities;
import com.abona_erp.driver.app.data.model.SpecialActivityResult;
import com.abona_erp.driver.app.data.model.SpecialFunction;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.ExtGalleryAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Hdr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SFCameraDialog extends DialogFragment implements GalleryListener {
  
  private static final String TAG = SFCameraDialog.class.getSimpleName();
  
  private static final String APP_FOLDER = "/DriverApp/";
  private static final String PATH_SLASH = "/";
  
  // Arguments
  private int mNotifyId;
  private int mActivityIndex;
  
  // Notify
  private CommItem mCommItem;
  
  // Camera
  private CameraView camera;
  private RecyclerView rvGallery;
  private ArrayList<String> mPhotoUrls = new ArrayList<String>();
  private ExtGalleryAdapter mGalleryViewAdapter = new ExtGalleryAdapter(this);
  private AppCompatImageView takePhoto;
  private AppCompatImageButton btnCameraBack;
  private AsapTextView tvCamDocumentTitle;
  
  // Results
  private ArrayList<String> mResults = new ArrayList<String>();
  
  // Database
  private DriverDatabase mDB = DriverDatabase.getDatabase();
  private NotifyDao mNotifyDAO = mDB.notifyDao();
  
  private class CamListener extends CameraListener {
    
    @Override
    public void onPictureTaken(@NonNull PictureResult result) {
      
      result.toBitmap(1600, 1200, new BitmapCallback() {
        @Override
        public void onBitmapReady(@androidx.annotation.Nullable Bitmap bitmap) {
    
          if (bitmap == null) return;
          
          File photoFile = null;
          try {
            photoFile = saveImage(bitmap);
          } catch (IOException e) {
            e.printStackTrace();
          }
          
          if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(ContextUtils.getApplicationContext(),
              BuildConfig.APPLICATION_ID + ".provider", photoFile);
            
            ContentValues image = getImageContent(photoFile);
            getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            
            mPhotoUrls.add(photoFile.getAbsolutePath());
            mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
          }
        }
      });
    }
  }
  
  public void SFCameraDialog() {
  }
  
  public static SFCameraDialog newInstance(int notifyId, int activityId) {
    SFCameraDialog fragment = new SFCameraDialog();
  
    Bundle args = new Bundle();
    args.putInt("id", notifyId);
    args.putInt("actId", activityId);
    fragment.setArguments(args);
  
    File createFile = new File(ContextUtils.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
      + File.separator
      + "DriverApp"
      + File.separator
      + "temp");
    if (!createFile.exists() && !createFile.isDirectory()) {
      createFile.mkdirs();
    }
    
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    if (getArguments() != null) {
      mNotifyId = getArguments().getInt("id", -1);
      mActivityIndex = getArguments().getInt("actId", -1);
    }
    
    loadOrProgressData();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_fragment_sf_camera, container);
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    initializeCameraView(view);
  }
  
  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }
  
  @Override
  public void onGallerySelected(int position) {
  }
  
  private void storeCamData(int SFPos, List<String> results) {
  
    if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
      
      ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
      if (ai != null && ai.getSpecialActivities() != null) {
        
        // Hole SpecialFunctions
        List<SpecialActivities> sa = ai.getSpecialActivities();
        
        if (sa.get(0).getSpecialActivityResults() == null) {
        
          // Existiert nicht - neu anlegen.
          List<SpecialActivityResult> specialActivityResults = new ArrayList<SpecialActivityResult>();
          for (int i = 0; i < results.size(); i++) {
            SpecialActivityResult sar = new SpecialActivityResult();
            sar.setResultString1(results.get(i));
            specialActivityResults.add(sar);
          }
          mCommItem.getTaskItem().getActivities().get(mActivityIndex).getSpecialActivities()
            .get(0).setSpecialActivityResults(specialActivityResults);
        } else {
          
          for (int i = 0; i < results.size(); i++) {
  
            SpecialActivityResult sar = new SpecialActivityResult();
            sar.setResultString1(results.get(i));
            sa.get(0).getSpecialActivityResults().add(sar);
          }
        }
      }
      
      updateNotify();
    }
  }
  
  private void loadOrProgressData() {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        mResults.clear();
        mPhotoUrls.clear();
        
        // Task laden.
        if (mNotifyId > 0) {
          mNotifyDAO.loadNotifyById(mNotifyId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(@io.reactivex.annotations.NonNull Notify notify) {
    
                mCommItem = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
                  
                  // Activity holen.
                  ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
                  if (ai != null && ai.getSpecialActivities() != null) {
                    
                    // Special activities holen.
                    List<SpecialActivities> sa = ai.getSpecialActivities();
                    if (sa.get(0).getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                      tvCamDocumentTitle.setText("CMR");
                    } else if (sa.get(0).getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                      tvCamDocumentTitle.setText("Shipment");
                    }
                  }
                }
              }
  
              @Override
              public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                // Notify not exists!
              }
            });
        }
      }
    });
  }
  
  private void updateNotify() {
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        mNotifyDAO.loadNotifyById(mNotifyId)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(@io.reactivex.annotations.NonNull Notify notify) {
    
              notify.setData(App.getInstance().gson.toJson(mCommItem));
              mNotifyDAO.updateNotify(notify);
              
              loadOrProgressData();
            }
  
            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
              loadOrProgressData();
            }
          });
      }
    });
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
    File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    File image = File.createTempFile(mFileName, ".jpg", storageDir);
    return image;
  }
  
  private void initializeCameraView(@NonNull View root) {
  
    tvCamDocumentTitle = root.findViewById(R.id.tvCamDocumentTitle);
    
    rvGallery = (RecyclerView)root.findViewById(R.id.rvGalleryView);
    LinearLayoutManager llmGallery = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    rvGallery.setLayoutManager(llmGallery);
    rvGallery.setAdapter(mGalleryViewAdapter);
    
    takePhoto = (AppCompatImageView)root.findViewById(R.id.takePhoto);
    takePhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        camera.takePicture();
      }
    });
    
    camera = (CameraView)root.findViewById(R.id.camera);
    camera.setLifecycleOwner(this);
    camera.addCameraListener(new CamListener());
    camera.open();
    
    btnCameraBack = (AppCompatImageButton)root.findViewById(R.id.btnCameraBack);
    btnCameraBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        storeCamData(mActivityIndex, mPhotoUrls);
        
        camera.close();
        dismiss();
      }
    });
  }
}
