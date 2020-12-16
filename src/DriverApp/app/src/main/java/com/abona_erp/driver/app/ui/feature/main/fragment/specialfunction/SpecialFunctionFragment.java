package com.abona_erp.driver.app.ui.feature.main.fragment.specialfunction;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.adapter.SpecialFunctionAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.ExtGalleryAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.abona_erp.driver.core.base.ContextUtils.getApplicationContext;

public class SpecialFunctionFragment extends Fragment
  implements SpecialFunctionAdapter.SpecialFunctionListener,
  GalleryListener {
  
  private static final String TAG = SpecialFunctionFragment.class.getSimpleName();
  
  private static final String APP_FOLDER = "/DriverApp/";
  private static final String PATH_SLASH = "/";
  
  // Arguments
  private int mNotifyId;
  private int mActivityIndex;
  
  // Barcode
  private DecoratedBarcodeView barcodeView;
  private BeepManager beepManager;
  private Button btnResume;
  private Button btnPause;
  private LinearLayout llScanScreen;
  
  // Camera
  private BottomSheetBehavior sheetBehavior;
  private LinearLayout bottom_sheet;
  private CameraView camera;
  private RecyclerView rvGallery;
  private ArrayList<String> mPhotoUrls = new ArrayList<String>();
  private ExtGalleryAdapter mGalleryViewAdapter = new ExtGalleryAdapter(this);
  private AppCompatImageView takePhoto;
  private AppCompatImageButton btnCameraBack;
  private AsapTextView tvCamDocumentTitle;
  
  // Special Function List
  private int mSFIndex;
  private RecyclerView rvSAList;
  
  // Back
  private AppCompatImageButton mBtnBack;
  
  // Notify
  private CommItem mCommItem;
  
  // Results
  private ArrayList<String> mResults = new ArrayList<String>();
  
  // Database
  private DriverDatabase mDB = DriverDatabase.getDatabase();
  private NotifyDao mNotifyDAO = mDB.notifyDao();
  
  private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
      try {
        beepManager.playBeepSoundAndVibrate();
        
        if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
          
          String res = result.getText();
          if (!TextUtils.isEmpty(res) && res.length() > 0) {
            barcodePause();
            llScanScreen.setVisibility(View.GONE);
            storeCodeData(mSFIndex, res);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
  
  private class CamListener extends CameraListener {
    
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
              BuildConfig.APPLICATION_ID + ".provider", photoFile);
             
            ContentValues image = getImageContent(photoFile);
            getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            
            // Add to list.
            Log.i("******", "PHOTO URL " + photoFile.getAbsolutePath());
            mPhotoUrls.add(photoFile.getAbsolutePath());
            mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
          }
        }
      });
    }
  }
  
  @Override
  public void changeScreen(int type, int position) {
  
    mSFIndex = position;
    
    if (type == 0) {
      
      // Barcode Scan.
      barcodeResume();
      llScanScreen.setVisibility(View.VISIBLE);
      
    } else if (type == 1) {
  
      // Camera screen.
      llScanScreen.setVisibility(View.GONE);
      
      sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      
      mPhotoUrls.clear();
      mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
      camera.open();
    }
  }
  
  public static SpecialFunctionFragment newInstance(int notifyId, int activityId) {
    SpecialFunctionFragment fragment = new SpecialFunctionFragment();
  
    Bundle args = new Bundle();
    args.putInt("id", notifyId);
    args.putInt("actId", activityId);
    fragment.setArguments(args);
    
    File createFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
      + File.separator
      + "DriverApp"
      + File.separator
      + "temp");
    if (!createFile.exists() && !createFile.isDirectory()) {
      createFile.mkdirs();
    }
  
    return fragment;
  }
 
  public SpecialFunctionFragment() {
    // Required empty public constructor.
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
    View root = inflater.inflate(R.layout.fragment_special_function, container, false);
    initComponents(root);
    barcodePause();
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }
  
  @Override
  public void onGallerySelected(int position) {
  }
  
  @Override
  public void onResume() {
    super.onResume();
    //barcodeResume();
    
    loadOrProgressData();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    barcodePause();
    camera.close();
  }
  
  public void barcodePause() {
    barcodeView.pause();
  }
  
  public void barcodeResume() {
    barcodeView.resume();
  }
  
  private void storeCodeData(int SFPos, String result) {
    
    if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
      
      ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
      if (ai != null && ai.getSpecialActivities() != null) {
      
        // Hole SpecialFunctions...
        List<SpecialActivities> sa = ai.getSpecialActivities();
        
        if (sa.get(SFPos).getSpecialActivityResults() == null) {
          
          // Existiert nicht - neu anlegen.
          List<SpecialActivityResult> specialActivityResults = new ArrayList<SpecialActivityResult>();
          SpecialActivityResult sar = new SpecialActivityResult();
          sar.setSpecialFunctionFinished(new Date());
          sar.setResultString1(result);
          specialActivityResults.add(sar);
          
          mCommItem.getTaskItem().getActivities().get(mActivityIndex).getSpecialActivities()
            .get(SFPos).setSpecialActivityResults(specialActivityResults);
        } else {
  
          SpecialActivityResult sar = new SpecialActivityResult();
          sar.setSpecialFunctionFinished(new Date());
          sar.setResultString1(result);
          sa.get(SFPos).getSpecialActivityResults().add(sar);
        }
      }
      
      updateNotify();
    }
  }
  
  private void storeCamData(int SFPos, List<String> results) {
  
    if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
  
      ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
      if (ai != null && ai.getSpecialActivities() != null) {
  
        // Hole SpecialFunctions...
        List<SpecialActivities> sa = ai.getSpecialActivities();
  
        if (sa.get(SFPos).getSpecialActivityResults() == null) {
  
          // Existiert nicht - neu anlegen.
          List<SpecialActivityResult> specialActivityResults = new ArrayList<SpecialActivityResult>();
          for (int i = 0; i < results.size(); i++) {
            SpecialActivityResult sar = new SpecialActivityResult();
            //sar.setSpecialFunctionFinished(new Date());
            sar.setResultString1(results.get(i));
            specialActivityResults.add(sar);
          }
          mCommItem.getTaskItem().getActivities().get(mActivityIndex).getSpecialActivities()
            .get(SFPos).setSpecialActivityResults(specialActivityResults);
        } else {
        
          for (int i = 0; i < results.size(); i++) {
  
            SpecialActivityResult sar = new SpecialActivityResult();
            //sar.setSpecialFunctionFinished(new Date());
            sar.setResultString1(results.get(i));
            sa.get(SFPos).getSpecialActivityResults().add(sar);
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
    
        // Array leeren.
        mResults.clear();
        mPhotoUrls.clear();
        
        // Task laden.
        if (mNotifyId > 0) {
          mNotifyDAO.loadNotifyById(mNotifyId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
    
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
                    // Special activities laden.
                    SpecialFunctionAdapter adapter = new SpecialFunctionAdapter(sa, SpecialFunctionFragment.this);
                    rvSAList.setAdapter(adapter);
                  }
                }
              }
  
              @Override
              public void onError(Throwable e) {
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
            public void onSuccess(Notify notify) {
            
              notify.setData(App.getInstance().gson.toJson(mCommItem));
              mNotifyDAO.updateNotify(notify);
  
              loadOrProgressData();
            }
          
            @Override
            public void onError(Throwable e) {
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
    File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    File image = File.createTempFile(mFileName, ".jpg", storageDir);
    return image;
  }
  
  private void initComponents(@NonNull View root) {
  
    initializeBarcodeView(root);
    initializeBackButton(root);
    initializeSpecialFunctionList(root);
    initializeCameraView(root);
  }
  
  private void initializeSpecialFunctionList(@NonNull View root) {
  
    rvSAList = (RecyclerView) root.findViewById(R.id.rvList);
    rvSAList.setLayoutManager(new LinearLayoutManager(getContext()));
  }
  
  private void initializeBackButton(@NonNull View root) {
  
    mBtnBack = (AppCompatImageButton) root.findViewById(R.id.btn_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodePause();
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
  }
  
  private void initializeBarcodeView(@NonNull View root) {

    barcodeView = root.findViewById(R.id.barcode_scanner);
    llScanScreen = (LinearLayout)root.findViewById(R.id.llScanScreen);
  
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    barcodeView.initializeFromIntent(getActivity().getIntent());
    barcodeView.decodeContinuous(callback);
  
    beepManager = new BeepManager(getActivity());
  
    btnPause = (Button) root.findViewById(R.id.btn_pause);
    btnPause.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodePause();
      }
    });
  
    btnResume = (Button) root.findViewById(R.id.btn_resume);
    btnResume.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodeResume();
      }
    });
  }
  
  private void initializeCameraView(@NonNull View root) {
  
    bottom_sheet = root.findViewById(R.id.camera_bottom_sheet);
    sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
    
    tvCamDocumentTitle = root.findViewById(R.id.tvCamDocumentTitle);
  
    rvGallery = (RecyclerView)root.findViewById(R.id.rvGalleryView);
    LinearLayoutManager llmGallery = new LinearLayoutManager(getApplicationContext(),
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
    
    camera.setAutoFocusResetDelay(0);
    camera.setHdr(Hdr.OFF);
    camera.setFlash(Flash.OFF);
    camera.close();
    
    btnCameraBack = (AppCompatImageButton)root.findViewById(R.id.btnCameraBack);
    btnCameraBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        storeCamData(mSFIndex, mPhotoUrls);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
      }
    });
  }
}
