package com.abona_erp.driver.app.ui.feature.main.fragment.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.data.model.UploadResult;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.ImageEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragmentViewModel;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.EditingToolsAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.MenuToolsAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageCameraFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageEditFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageGalleryFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageSettingsFragment;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.photolib.PhotoEditorView;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoFragment extends Fragment
  implements EditingToolsAdapter.OnItemSelected,
    MenuToolsAdapter.OnItemSelected,
    GalleryListener {
  
  private int mOid;
  private Notify mNotify;
  private ArrayList<String> mPhotoUrls = new ArrayList<>();
  
  private PhotoEditorView mPhotoEditorView;
  private RecyclerView mRvGalleryView;
  private RecyclerView mRvTools;
  private RecyclerView mRvMenu;
  private MenuToolsAdapter mMenuToolsAdapter = new MenuToolsAdapter(this);
  private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
  private GalleryViewAdapter mGalleryViewAdapter = new GalleryViewAdapter(this);
  private AppCompatImageButton mBtnBack;
  
  private MainFragmentViewModel mViewModel;
  
  public PhotoFragment() {
    // Required empty public constructor.
  }
  
  public static PhotoFragment newInstance() {
    return new PhotoFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
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
    
    if (getArguments() != null) {
      mOid = getArguments().getInt("oid");
      if (mOid > 0) {
        mViewModel.getNotifyById(mOid).observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            
            @Override
            public void onSuccess(Notify notify) {
              if (notify != null) {
                mNotify = notify;
              }
              
              mPhotoUrls = mNotify.getPhotoUrls();
              if (mPhotoUrls.size() > 0) {
                ImageEditFragment.newInstance().setBitmap(null);
                for (int i = 0; i < mPhotoUrls.size(); i++) {
                  UploadItem uploadItem = App.getGson().fromJson(mPhotoUrls.get(i), UploadItem.class);
                  if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
    
                    if (uploadItem.getUploaded()) continue;
                    
                    File file = new File(uploadItem.getUri());
                    if (file.exists()) {
                      Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
                      ImageEditFragment.newInstance().setBitmap(preview);
                      break;
                    }
                  }
                }
             
                Log.d("PhotoFragment", "Task Bilder vorhanden!");
                
                mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
              } else {
                Log.d("PhotoFragment", "Kein Bild vorhanden!");
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
  }
  
  @Override
  public void onGallerySelected(int position) {
    loadFragment(ImageEditFragment.newInstance());
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (position >= 0) {
          UploadItem uploadItem = App.getGson()
            .fromJson(mPhotoUrls.get(position), UploadItem.class);
          
          if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
            File file = new File(uploadItem.getUri());
            if (file.exists()) {
              Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
              ImageEditFragment.newInstance().setBitmap(preview);
            } else {
              ImageEditFragment.newInstance().setBitmap(null);
            }
          } else {
            ImageEditFragment.newInstance().setBitmap(null);
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
      case CAMERA:
        loadFragment(ImageCameraFragment.newInstance());
        break;
      case GALLERY:
        loadFragment(ImageGalleryFragment.newInstance());
        break;
      case TOOLS:
        //showOrHideTools();
        break;
      case SETTINGS:
        loadFragment(ImageSettingsFragment.newInstance());
        break;
    }
  }
  
  @Override
  public void onToolSelected(ToolType toolType) {
    switch (toolType) {
      case BRUSH:
        break;
      case TEXT:
        break;
      case ERASER:
        break;
    }
  }
  
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(ImageEvent event) {
    
    if (event.getPhotoUrl() == null) {
      // -------------------------------------------------------------------------------------------
      // Delete Photo:
      //
      if (event.getPosition() >= 0) {
        UploadItem uploadItem = App.getGson().fromJson(mPhotoUrls
          .get(event.getPosition()), UploadItem.class);
        
        if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
          File file = new File(uploadItem.getUri());
          if (file.exists()) {
            boolean check = file.delete();
            Log.w(">>>>>>>", "File deleted: " + file.getAbsolutePath() + " " + check);
            
            mPhotoUrls.remove(event.getPosition());
          } else {
            mPhotoUrls.remove(event.getPosition());
            MessageDialog.build((AppCompatActivity)getActivity())
              .setStyle(DialogSettings.STYLE.STYLE_IOS)
              .setTheme(DialogSettings.THEME.LIGHT)
              .setTitle(getContext().getResources().getString(R.string.action_warning))
              .setMessage(getContext().getResources().getString(R.string.action_photo_no_longer_exists))
              .setOkButton(getContext().getResources().getString(R.string.action_ok),
                new OnDialogButtonClickListener() {
        
                  @Override
                  public boolean onClick(BaseDialog baseDialog, View v) {
                    return false;
                  }
                })
              .show();
          }
        } else {
          mPhotoUrls.remove(event.getPosition());
          MessageDialog.build((AppCompatActivity)getActivity())
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(getContext().getResources().getString(R.string.action_warning))
            .setMessage(getContext().getResources().getString(R.string.action_photo_damaged))
            .setOkButton(getContext().getResources().getString(R.string.action_ok),
              new OnDialogButtonClickListener() {
        
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                  return false;
                }
              })
            .show();
        }
      }
  
      mNotify.setPhotoUrls(mPhotoUrls);
      mViewModel.update(mNotify);
  
      mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
      mGalleryViewAdapter.notifyDataSetChanged();
      
      if (mPhotoUrls.size() > 0) {
        UploadItem uploadItem = App.getGson().fromJson(mPhotoUrls.get(0),
          UploadItem.class);
        
        if (uploadItem != null && uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {
          File file = new File(uploadItem.getUri());
          if (file.exists()) {
            Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageEditFragment.newInstance().setBitmap(preview);
          } else {
            ImageEditFragment.newInstance().setBitmap(null);
          }
        } else {
          ImageEditFragment.newInstance().setBitmap(null);
        }
      } else {
        ImageEditFragment.newInstance().setBitmap(null);
      }

    } else {
      // -------------------------------------------------------------------------------------------
      // Add New Photo:
      //
      UploadItem uploadItem = new UploadItem();
      uploadItem.setUri(event.getPhotoUrl());
      uploadItem.setUploaded(false);
      Date currentTimestamp = new Date();
      uploadItem.setCreated(currentTimestamp);
      uploadItem.setModifiedAt(currentTimestamp);
      mPhotoUrls.add(App.getGson().toJson(uploadItem));
      
      if (BuildConfig.DEBUG) {
        if (mPhotoUrls.size() > 0) {
          for (int i = 0; i < mPhotoUrls.size(); i++) {
            Log.i(">>>>>", mPhotoUrls.get(i));
          }
        }
      }
  
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
  
  private void showOrHideTools() {
    if (mRvTools.getVisibility() == View.VISIBLE) {
      mRvTools.setVisibility(View.GONE);
    } else {
      mRvTools.setVisibility(View.VISIBLE);
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
        
        if (mNotify != null && mNotify.getPhotoUrls() != null) {
          boolean uploadFiles = false;
          for (int i = 0; i < mPhotoUrls.size(); i++) {
            UploadItem uploadItem = App.getGson().fromJson(mPhotoUrls.get(i), UploadItem.class);
            if (uploadItem.getUploaded() == false) {
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
                    
                    if (mPhotoUrls.size() > 0) {
                      
                      for (int i = 0; i < mPhotoUrls.size(); i++) {
                        UploadItem uploadItem = App.getGson()
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
  
                          final int j = i;
                          Call<UploadResult> call = App.apiManager.getFileUploadApi()
                            .upload(mandantId,orderNo,taskId,driverNo, body);
                          call.enqueue(new Callback<UploadResult>() {
                            @Override
                            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
      
                              if (response.isSuccessful()) {
                                uploadItem.setUploaded(true);
                                mPhotoUrls.set(j, App.getGson().toJson(uploadItem));
                                mNotify.setPhotoUrls(mPhotoUrls);
                                mViewModel.update(mNotify);
                                
                                if (j >= mPhotoUrls.size()-1) {
                                  WaitDialog.dismiss(1000);
                                  App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                                }
        
                                //if (response.body() != null && response.body().getFileName() != null) {
                                //  Log.i("*****", response.body().getFileName());
                                //}
                                
                                App.eventBus.post(new DocumentEvent(mNotify.getMandantId(), mNotify.getOrderNo()));
                              } else {
        
                                switch (response.code()) {
                                  case 401:
                                    handleAccessToken();
                                    break;
                                  default:
                                    if (j == mPhotoUrls.size()-1) {
                                      WaitDialog.dismiss(1000);
                                      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
                                    }
                                    break;
                                }
                              }
                            }
    
                            @Override
                            public void onFailure(Call<UploadResult> call, Throwable t) {
                              if (j == mPhotoUrls.size()-1) {
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
                    
                    //App.eventBus.post(new BackEvent());
                    return false;
                  }
                })
              .setCancelButton(getContext().getString(R.string.action_cancel),
                new OnDialogButtonClickListener() {
      
                  @Override
                  public boolean onClick(BaseDialog baseDialog, View v) {
                    return false;
                  }
                })
              .show();
          } else {
            App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
          }
        }
      }
    });

    //mPhotoEditorView = (PhotoEditorView)root.findViewById(R.id.photoEditorView);
    mRvGalleryView = (RecyclerView)root.findViewById(R.id.rvGalleryView);
    //mRvTools = (RecyclerView)root.findViewById(R.id.rv_gallery_tools);
    mRvMenu = (RecyclerView)root.findViewById(R.id.rv_gallery_menu);

    LinearLayoutManager llmMenu = new LinearLayoutManager(getContext(),
      LinearLayoutManager.VERTICAL, false);
    mRvMenu.setLayoutManager(llmMenu);
    mRvMenu.setAdapter(mMenuToolsAdapter);
/*
    LinearLayoutManager llmTools = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    mRvTools.setLayoutManager(llmTools);
    mRvTools.setAdapter(mEditingToolsAdapter);
 */
    LinearLayoutManager llmGallery = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    mRvGalleryView.setLayoutManager(llmGallery);
    mRvGalleryView.setAdapter(mGalleryViewAdapter);
  }
  
  private void handleAccessToken() {
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=manyvehicles%40abona-erp.com&password=1234qwerQWER%2C.-");
    
    Request request = new Request.Builder()
      .url(TextSecurePreferences.getServerIpAddress() + ":" + TextSecurePreferences.getServerPort() + "/authentication")
      .post(body)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Content-Length", "84")
      .addHeader("Connection", "keep-alive")
      .addHeader("cache-control", "no-cache")
      .build();
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        
        getClient().newCall(request).enqueue(new okhttp3.Callback() {
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
          
          }
          
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                //Log.i(TAG, "ACCESS_TOKEN " + jobject.getString("access_token"));
                String access_token = jobject.getString("access_token");
                if (!TextUtils.isEmpty(access_token)) {
                  TextSecurePreferences.setAccessToken(getContext(), access_token);
                }
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
    });
  }
  
  OkHttpClient mClient = null;
  private OkHttpClient getClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    if (mClient == null) {
      synchronized (BackgroundServiceWorker.class) {
        if (mClient == null) {
          mClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier(new HostnameVerifier() {
              @Override
              public boolean verify(String s, SSLSession sslSession) {
                return true;
              }
            })
            .sslSocketFactory(getSslSocket())
            .addInterceptor(logging)
            .build();
        }
      }
    }
    return mClient;
  }
  
  private SSLSocketFactory getSslSocket() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      X509TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      };
      sslContext.init(null, new TrustManager[]{tm}, null);
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
