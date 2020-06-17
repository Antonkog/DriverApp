package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.ImageEvent;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Hdr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageCameraFragment extends Fragment
  implements View.OnClickListener {
  
  private static final String TAG = ImageCameraFragment.class.getSimpleName();
  
  private CameraView camera;
  private ViewGroup controlPanel;
  private long mCaptureTime;
  
  private int mMandantID;
  private int mOrderNo;
  private int mTaskID;
  
  private static boolean bFlash = false;
  private static boolean bInformation = false;
  private AppCompatImageView iv_capture_image;
  private AppCompatImageView iv_capture_flash;
  private AppCompatImageView iv_capture_information;
  
  private static final String APP_FOLDER = "/DriverApp/";
  private static final String PATH_SLASH = "/";
  
  public ImageCameraFragment() {
    // Required empty public constructor.
  }
  
  public static ImageCameraFragment newInstance() {
    return new ImageCameraFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.sub_fragment_image_camera_view, container, false);
    initComponents(root);
    
    if (getArguments() != null) {
      mMandantID = getArguments().getInt("mandant_id");
      mOrderNo = getArguments().getInt("order_no");
      mTaskID = getArguments().getInt("task_id");
  
      File createFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + File.separator
        + "DriverApp"
        + File.separator
        + mMandantID
        + File.separator
        + mOrderNo);
      if (!createFile.exists() && !createFile.isDirectory()) {
        createFile.mkdirs();
      }
    }
    return root;
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (camera == null) {
      camera = (CameraView)view.findViewById(R.id.camera);
    }
    camera.open();
  }
  
  @Override
  public void onClick(View view) {
  }
  
  @Override
  public void onStart() {
    super.onStart();
  }
  
  @Override
  public void onStop() {
    super.onStop();
  }
  
  @Override
  public void onResume() {
    super.onResume();
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
  
  private class Listener extends CameraListener {
  
    @Override
    public void onPictureTaken(@NonNull PictureResult result) {
      super.onPictureTaken(result);
      
      result.toBitmap(1600, 1200, new BitmapCallback() {
        @Override
        public void onBitmapReady(@Nullable Bitmap bitmap) {
          
          if (bitmap == null) return;
          
          File photoFile = null;
          try {
            photoFile = saveImage(bitmap, String.valueOf(mMandantID), String.valueOf(mOrderNo), String.valueOf(mTaskID));
          } catch (IOException ex) {
            ex.printStackTrace();
          }
          if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
              BuildConfig.APPLICATION_ID + ".provider",
              photoFile);
            
            try {
              MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                photoFile.getAbsolutePath(), photoFile.getName(), photoFile.getName());
              
              App.eventBus.post(new ImageEvent(photoFile.getAbsolutePath()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      });
    }
  }
  
  private File saveImage(Bitmap bmp, String mandantId, String orderNo, String taskId) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
    File f = createImageFile(mandantId, orderNo, taskId);
  
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(bytes.toByteArray());
    fos.close();
    return f;
  }
  
  private File createImageFile(String mandantId, String orderNo, String taskId) throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
      Locale.getDefault()).format(new Date());
    String mFileName = timeStamp + "_" + mandantId + "_" + orderNo + "_" + taskId + ".jpg";
    
    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
      + APP_FOLDER + mandantId + PATH_SLASH + orderNo, mFileName);
    
    return storageDir;
  }
  
  private void initComponents(@NonNull View root) {
    
    camera = (CameraView)root.findViewById(R.id.camera);
    camera.setLifecycleOwner(this);
    camera.addCameraListener(new Listener());
    
    camera.setAutoFocusResetDelay(0);
    camera.setHdr(Hdr.OFF);
    camera.setFlash(Flash.OFF);
    
    iv_capture_image = (AppCompatImageView)root.findViewById(R.id.iv_capture_image);
    iv_capture_image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        camera.takePicture();
      }
    });
    
    iv_capture_flash = (AppCompatImageView)root.findViewById(R.id.iv_capture_flash);
    iv_capture_flash.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!bFlash) {
          camera.setFlash(Flash.ON);
          iv_capture_flash.setImageResource(R.drawable.bd_ocr_light_on);
          bFlash = true;
        } else {
          camera.setFlash(Flash.OFF);
          iv_capture_flash.setImageResource(R.drawable.bd_ocr_light_off);
          bFlash = false;
        }
      }
    });
    
    iv_capture_information = (AppCompatImageView)root.findViewById(R.id.iv_capture_information);
    iv_capture_information.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!bInformation) {
          bInformation = true;
        } else {
          bInformation = false;
        }
      }
    });
  }
}
