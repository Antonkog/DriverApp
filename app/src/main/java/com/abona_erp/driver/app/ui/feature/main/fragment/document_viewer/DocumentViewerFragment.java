package com.abona_erp.driver.app.ui.feature.main.fragment.document_viewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragmentViewModel;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.GalleryListener;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.DocumentViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageEditFragment;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DocumentViewerFragment extends Fragment implements GalleryListener {
  
  private int               mOid;
  private Notify            mNotify;
  private ArrayList<String> mPhotoUrls = new ArrayList<>();
  
  private RecyclerView mRvGalleryView;
  
  private DocumentViewAdapter mGalleryViewAdapter = new DocumentViewAdapter(this);
  
  private AppCompatImageButton mBtnBack;
  
  private MainFragmentViewModel mViewModel;
  
  public DocumentViewerFragment() {
    // Required empty public constructor.
  }
  
  public static DocumentViewerFragment newInstance() {
    return new DocumentViewerFragment();
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mNotify = new Notify();
    
    mViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_document_layout, container, false);
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
                    
                    if (!uploadItem.getUploaded()) continue;
  
                    File file = new File(uploadItem.getUri());
                    if (file.exists()) {
                      Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
                      ImageEditFragment.newInstance().setBitmap(preview);
                      break;
                    }
                  }
                }
  
                Log.d("DocumentViewerFragment", "Task Bilder vorhanden!");
                mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
              } else {
                Log.d("DocumentViewerFragment", "Kein Bild vorhanden!");
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
  public void onStart() {
    super.onStart();
  }
  
  @Override
  public void onStop() {
    super.onStop();
  }
  
  private void loadFragment(Fragment fragment) {
    getChildFragmentManager()
      .beginTransaction()
      .replace(R.id.fragment_gallery, fragment)
      .addToBackStack(null)
      .commit();
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
    
    mRvGalleryView = (RecyclerView)root.findViewById(R.id.rvGalleryView);
    LinearLayoutManager llmGallery = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    mRvGalleryView.setLayoutManager(llmGallery);
    mRvGalleryView.setAdapter(mGalleryViewAdapter);
  }
}
