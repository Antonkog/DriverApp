package com.abona_erp.driver.app.ui.feature.main.fragment.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.DMSDocumentType;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.data.model.UploadResult;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.ImageEvent;
import com.abona_erp.driver.app.ui.event.LogEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.RefreshUiEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragmentViewModel;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.MenuToolsAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageCameraFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageEditFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageSettingsFragment;
import com.abona_erp.driver.app.ui.widget.CustomDMSDocumentTypeDialog;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment
  implements MenuToolsAdapter.OnItemSelected,
    GalleryListener, CustomDMSDocumentTypeDialog.OnSelectedTypeEventListener {
  
  public static final int ACTION_REQUEST_PHOTO_EDITOR = 777;
  
  private int mOid;
  private Notify mNotify;
  private ArrayList<String> mPhotoUrls = new ArrayList<>();
  private int mPreviewIndex = -1;
  
  private DMSDocumentType mDocumentType;
  
  private RecyclerView mRvGalleryView;
  private RecyclerView mRvMenu;
  private MenuToolsAdapter mMenuToolsAdapter = new MenuToolsAdapter(this);
  private GalleryViewAdapter mGalleryViewAdapter = new GalleryViewAdapter(this);
  private AppCompatImageButton mBtnBack;
  
  private File mEditFile = null;
  
  private MainFragmentViewModel mViewModel;
  private Context context;
  
  public PhotoFragment() {
    // Required empty public constructor.
  }
  
  public static PhotoFragment newInstance() {
    return new PhotoFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getActivity().getBaseContext();
    mDocumentType = DMSDocumentType.NA;
    mNotify = new Notify();
    
    mViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    View root = inflater.inflate(R.layout.activity_photo_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  
    loadFragment(ImageEditFragment.newInstance());
    if (getArguments() == null) return;
    mOid = getArguments().getInt("oid");
  
    if (mOid > 0) {
      mViewModel.getNotifyById(mOid).observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new DisposableSingleObserver<Notify>() {
  
          @Override
          public void onSuccess(Notify notify) {
            if (notify == null) return;
            mNotify = notify;
    
            if (notify.getPhotoUrls() == null) return;
            if (notify.getPhotoUrls().size() <= 0) return;
  
            mPhotoUrls.clear();
            mPhotoUrls = mNotify.getPhotoUrls();
            
            ArrayList<String> notUploadedFiles = new ArrayList<String>();
            boolean updateNow = false;
            boolean deleteAll = true;
            for (int i = 0; i < mPhotoUrls.size(); i++) {
              UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(i), UploadItem.class);
              if (uploadItem.getUploaded()) continue;
              deleteAll = false;
              updateNow = true;
              notUploadedFiles.add(App.getInstance().gson.toJson(uploadItem));
            }
    
            if (deleteAll) {
              mPhotoUrls.clear();
              mNotify.setPhotoUrls(mPhotoUrls);
              mViewModel.update(mNotify);
            }
            if (updateNow) {
              mPhotoUrls.clear();
              mPhotoUrls = notUploadedFiles;
              mNotify.setPhotoUrls(mPhotoUrls);
              mViewModel.update(mNotify);
            }
            
            
    
            mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
    
            ImageEditFragment.newInstance().setBitmap(null, null);
            for (int i = 0; i < mPhotoUrls.size(); i++) {
              UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(i), UploadItem.class);
              if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
  
                if (uploadItem.getUploaded()) continue;
  
                File file = new File(uploadItem.getUri());
                if (file.exists()) {
                  mPreviewIndex = i;
                  mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
                  if (uploadItem.getDocumentType() != null)
                    mDocumentType = uploadItem.getDocumentType();
                  Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
                  ImageEditFragment.newInstance().setBitmap(preview, file);
                  break;
                }
              }
            }
          }
  
          @Override
          public void onError(Throwable e) {
            Toast.makeText(getContext().getApplicationContext(),
              "Fehler beim Laden!", Toast.LENGTH_LONG).show();
          }
        });
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case ACTION_REQUEST_PHOTO_EDITOR:
          handleEditorImage(data);
          break;
      }
    }
  }
  
  private void handleEditorImage(Intent data){
    if (data == null) return;
    Uri editedImage = data.getData();
    try {
      Bitmap bmp = MediaStore.Images.Media.getBitmap(getContext()
        .getContentResolver(), editedImage);
      saveBitmap(bmp, mEditFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onGallerySelected(int position) {
    loadFragment(ImageEditFragment.newInstance());
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (position >= 0) {
          UploadItem uploadItem = App.getInstance().gson
            .fromJson(mPhotoUrls.get(position), UploadItem.class);
          
          if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
            File file = new File(uploadItem.getUri());
            if (file.exists()) {
              mPreviewIndex = position;
              mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
              mDocumentType = uploadItem.getDocumentType();
              Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
              ImageEditFragment.newInstance().setBitmap(preview, file);
            } else {
              mPreviewIndex = -1;
              mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
              ImageEditFragment.newInstance().setBitmap(null, null);
            }
          } else {
            mPreviewIndex = -1;
            mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
            ImageEditFragment.newInstance().setBitmap(null, null);
          }
        }
      }
    }, 250);
  }
  
  @Override
  public void onToolSelected(MenuToolType toolType) {
    switch (toolType) {
      case SAVE:
        break;
      case CAMERA: {
        mPreviewIndex = -1;
        mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
        CustomDMSDocumentTypeDialog dialog = new CustomDMSDocumentTypeDialog((Activity)getContext(),
          AppUtils.parseOrderNo(mNotify.getOrderNo()), mDocumentType, this);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        loadFragment(ImageCameraFragment.newInstance());
      } break;
      case CATEGORY:
        CustomDMSDocumentTypeDialog dialog = new CustomDMSDocumentTypeDialog((Activity)getContext(),
          AppUtils.parseOrderNo(mNotify.getOrderNo()), mDocumentType, this);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        break;
      case TOOLS:

        if (mPreviewIndex < 0) return;
        UploadItem uploadItem = App.getInstance().gson
          .fromJson(mPhotoUrls.get(mPreviewIndex), UploadItem.class);
        if (mEditFile != null)
          mEditFile = null;
        mEditFile = new File(uploadItem.getUri());

        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        Uri uri = getImageUri(getContext(), BitmapFactory.decodeFile(mEditFile.getAbsolutePath()));
        editIntent.setDataAndType(uri, "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(editIntent, null), 777);
        break;
      case SETTINGS:
        loadFragment(ImageSettingsFragment.newInstance());
        break;
    }
  }
  
  private Uri getImageUri(Context context, Bitmap bitmap) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String url = MediaStore.Images.Media.insertImage(context.getContentResolver(),
      bitmap, "Title", null);
    return Uri.parse(url);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(ImageEvent event) {
    
    if (event.getPhotoUrl() == null) {
      // -------------------------------------------------------------------------------------------
      // Delete Photo:
      //
      int pos = event.getPosition();
      if (pos < 0) return;
      
      UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(pos), UploadItem.class);
      if (uploadItem == null) return;
      File file = new File(uploadItem.getUri());
      if (file.exists()) {
        
        mPhotoUrls.remove(pos);
        mNotify.setPhotoUrls(mPhotoUrls);
        mViewModel.update(mNotify);
        mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
  
        if (mPhotoUrls.size() > 0) {
          UploadItem ui = App.getInstance().gson.fromJson(mPhotoUrls.get(0),
            UploadItem.class);
    
          if (ui != null && ui.getUri() != null && !TextUtils.isEmpty(ui.getUri()) && ui.getUri().length() > 0) {
            File f = new File(ui.getUri());
            if (f.exists()) {
              mPreviewIndex = 0;
              mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
              if (uploadItem.getDocumentType() != null)
                mDocumentType = uploadItem.getDocumentType();
              Bitmap preview = BitmapFactory.decodeFile(f.getAbsolutePath());
              ImageEditFragment.newInstance().setBitmap(preview, f);
            } else {
              mPreviewIndex = -1;
              mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
              ImageEditFragment.newInstance().setBitmap(null, null);
            }
          } else {
            mPreviewIndex = -1;
            mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
            ImageEditFragment.newInstance().setBitmap(null, null);
          }
        } else {
          mPreviewIndex = -1;
          mGalleryViewAdapter.setSelectedIndex(mPreviewIndex);
          ImageEditFragment.newInstance().setBitmap(null, null);
        }
        
        file.delete();
      }

    } else {
      // -------------------------------------------------------------------------------------------
      // Add New Photo:
      //
      UploadItem uploadItem = new UploadItem();
      uploadItem.setUri(event.getPhotoUrl());
      uploadItem.setUploaded(false);
      uploadItem.setDocumentType(mDocumentType);
      Date currentTimestamp = new Date();
      uploadItem.setCreated(currentTimestamp);
      uploadItem.setModifiedAt(currentTimestamp);
      mPhotoUrls.add(App.getInstance().gson.toJson(uploadItem));
  
      mNotify.setPhotoUrls(mPhotoUrls);
      mViewModel.update(mNotify);
  
      mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
    }
  }
  
  @Override
  public void onStart() {
    super.onStart();
    App.eventBus.register(this);
  }
  
  @Override
  public void onStop() {
    super.onStop();
    App.eventBus.unregister(this);
  }
  
  @Subscribe
  public void onMessageEvent(RefreshUiEvent event) {
    mGalleryViewAdapter.notifyDataSetChanged();
    
    UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(mPreviewIndex), UploadItem.class);
    if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
      File file = new File(uploadItem.getUri());
      Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
      ImageEditFragment.newInstance().setBitmap(preview, file);
    }
  }
  
  private void loadFragment(Fragment fragment) {
    if (fragment instanceof ImageCameraFragment) {
      Bundle bundle = new Bundle();
      bundle.putInt("mandant_id", mNotify.getMandantId());
      bundle.putInt("order_no", mNotify.getOrderNo());
      bundle.putInt("task_id", mNotify.getTaskId());
      fragment.setArguments(bundle);
    }
    getChildFragmentManager()
      .beginTransaction()
      .replace(R.id.fragment_gallery, fragment)
      .addToBackStack(null)
      .commit();
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_photo_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View view) {
        
        if (mNotify == null || mNotify.getPhotoUrls() == null) return;
        
        int photoSize = mPhotoUrls.size();
  
        boolean uploadFiles = false;
        for (int i = 0; i < photoSize; i++) {
          UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(i), UploadItem.class);
          if (!uploadItem.getUploaded()) {
            uploadFiles = true;
          }
        }
        if (uploadFiles) {
          MessageDialog.build((AppCompatActivity) getContext())
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(getContext().getResources().getString(R.string.action_upload_photos))
            .setMessage(getContext().getResources().getString(R.string.action_upload_photos_message))
            .setOkButton(getContext().getResources().getString(R.string.action_upload),
              new OnDialogButtonClickListener() {
          
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
            
                  // Wait Dialog - for Uploading...
                  WaitDialog.show((AppCompatActivity)getActivity(), getContext().getResources().getString(R.string.action_uploading_photos))
                    .setTheme(DialogSettings.THEME.LIGHT);
            
                  if (photoSize > 0) {
              
                    for (int i = 0; i < photoSize; i++) {
                      UploadItem uploadItem = App.getInstance().gson
                        .fromJson(mPhotoUrls.get(i), UploadItem.class);
                
                      if (uploadItem.getUploaded()) continue;
                
                      if (uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
                        File file = new File(uploadItem.getUri());
                  
                        RequestBody requestFile = RequestBody
                          .create(MediaType.parse("multipart/form-data"), file);
                  
                        MultipartBody.Part body =
                          MultipartBody.Part.createFormData("",
                            file.getName(), requestFile);
                  
                        RequestBody mandantId = RequestBody.create(MediaType
                          .parse("multipart/form-data"), String.valueOf(mNotify.getMandantId()));
                        RequestBody orderNo = RequestBody.create(MediaType
                          .parse("multipart/form-data"), String.valueOf(mNotify.getOrderNo()));
                        RequestBody taskId = RequestBody.create(MediaType
                          .parse("multipart/form-data"), String.valueOf(mNotify.getTaskId()));
                  
                        RequestBody driverNo = RequestBody.create(MediaType
                          .parse("multipart/form-data"), String.valueOf(-1));
                        
                        String dmsType = "0";
                        if (uploadItem.getDocumentType().equals(DMSDocumentType.NA)) {
                          dmsType = "0";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.POD_CMR)) {
                          dmsType = "24";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.PALLETS_NOTE)) {
                          dmsType = "26";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.SAFETY_CERTIFICATE)) {
                          dmsType = "27";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.SHIPMENT_IMAGE)) {
                          dmsType = "28";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.DAMAGED_SHIPMENT_IMAGE)) {
                          dmsType = "29";
                        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.DAMAGED_VEHICLE_IMAGE)) {
                          dmsType = "30";
                        }
                  
                        RequestBody documentType = RequestBody.create(MediaType
                          .parse("multipart/form-data"), dmsType);
                  
                        final int j = i;
                        Call<UploadResult> call = App.getInstance().apiManager.getFileUploadApi()
                          .upload(mandantId,orderNo,taskId,driverNo,documentType, body);
                        call.enqueue(new Callback<UploadResult>() {
                          @Override
                          public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                      
                            if (response.isSuccessful()) {
                              EventBus.getDefault().post(new LogEvent(context.getString(R.string.log_document_upload),
                                      LogType.SERVER_TO_APP, LogLevel.INFO, context.getString(R.string.log_title_docs), mNotify.getTaskId()));

                              uploadItem.setUploaded(true);
                              mPhotoUrls.set(j, App.getInstance().gson.toJson(uploadItem));
                              mNotify.setPhotoUrls(mPhotoUrls);
                              mViewModel.update(mNotify);
                        
                              if (j >= photoSize-1) {
                                WaitDialog.dismiss(1000);
                                App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                              }
                        
                              App.eventBus.post(new DocumentEvent(mNotify.getMandantId(), mNotify.getOrderNo()));
                            } else {
                        
                              WaitDialog.dismiss(1000);
                              App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                        
                              switch (response.code()) {
                                case 401:
                                  handleAccessToken();
                                  break;
                                default:
                                  break;
                              }
                            }
                          }
                    
                          @Override
                          public void onFailure(Call<UploadResult> call, Throwable t) {
                            if (j == photoSize-1) {
                              WaitDialog.dismiss(1000);
                              App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                            }
                          }
                        });
                      }
                    }
              
                  } else {
                    WaitDialog.dismiss(500);
                  }
                  return false;
                }
              })
            .setCancelButton(getContext().getString(R.string.action_later),
              new OnDialogButtonClickListener() {
          
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                  App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                  return false;
                }
              })
            .show();
        } else {
          App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
        }
      }
    });
    
    mRvGalleryView = (RecyclerView)root.findViewById(R.id.rvGalleryView);
    mRvMenu = (RecyclerView)root.findViewById(R.id.rv_gallery_menu);

    LinearLayoutManager llmMenu = new LinearLayoutManager(getContext(),
      LinearLayoutManager.VERTICAL, false);
    mRvMenu.setLayoutManager(llmMenu);
    mRvMenu.setAdapter(mMenuToolsAdapter);

    LinearLayoutManager llmGallery = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    mRvGalleryView.setLayoutManager(llmGallery);
    mRvGalleryView.setAdapter(mGalleryViewAdapter);
  }
  
  private void handleAccessToken() {
    App.getInstance().apiManager.provideAuthClient().newCall(App.getInstance().apiManager.provideAuthRequest()).enqueue(new okhttp3.Callback() {
      @Override
      public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
      
      }
    
      @Override
      public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
        if (response.isSuccessful()) {
          try {
            String jsonData = response.body().string().toString();
            JSONObject jobject = new JSONObject(jsonData);
            String access_token = jobject.getString("access_token");
            if (!TextUtils.isEmpty(access_token)) {
              TextSecurePreferences.setAccessToken(getContext(), access_token);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });
  }
  
  @Override
  public void selectedEvent(DMSDocumentType documentType) {
    mDocumentType = documentType;
    
    if (mPreviewIndex != -1) {
      UploadItem uploadItem = App.getInstance().gson.fromJson(mPhotoUrls.get(mPreviewIndex), UploadItem.class);
      if (uploadItem == null) return;
      if (uploadItem.getUploaded()) return;
      
      uploadItem.setDocumentType(mDocumentType);
      uploadItem.setModifiedAt(new Date());
      
      mPhotoUrls.set(mPreviewIndex, App.getInstance().gson.toJson(uploadItem));
      mNotify.setPhotoUrls(mPhotoUrls);
      mViewModel.update(mNotify);
      
      mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
      mGalleryViewAdapter.notifyDataSetChanged();
    }
  }

  private void saveBitmap(Bitmap bitmap, File file) {
    if (bitmap == null) return;
    if (file == null) return;
    
    try {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (fos != null) {
            fos.flush();
            fos.close();
          }
          
          ImageEditFragment.newInstance().setBitmap(bitmap, file);
          App.eventBus.post(new RefreshUiEvent());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
