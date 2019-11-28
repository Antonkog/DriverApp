package com.abona_erp.driver.app.ui.feature.main.fragment.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.event.ImageEvent;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragmentViewModel;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.EditingToolsAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.MenuToolsAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageCameraFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageEditFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageGalleryFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageSettingsFragment;
import com.abona_erp.driver.photolib.PhotoEditorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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
  private GalleryViewAdapter mGalleryViewAdapter = new GalleryViewAdapter(getContext(), this);
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
                File imgFile = new File(mPhotoUrls.get(0));
                if (imgFile.exists()) {
                  Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                  ImageEditFragment.newInstance().setBitmap(bmp);
                }
                Log.d("PhotoFragment", "Bilder zum Task vorhanden!");
                mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
              } else {
                Log.d("PhotoFragment", "Keine Bilder vorhanden!");
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
    
    File imgFile = new File(mPhotoUrls.get(position));
    if (imgFile.exists()) {
      Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
      ImageEditFragment.newInstance().setBitmap(bmp);
    }
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
      if (event.getPosition() >= 0) {
        mPhotoUrls.remove(event.getPosition());
      }
    } else {
      mPhotoUrls.add(event.getPhotoUrl());
    }
    
    mGalleryViewAdapter.setPhotoItems(mPhotoUrls);
    
    mNotify.setPhotoUrls(mPhotoUrls);
    mViewModel.update(mNotify);
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
        App.eventBus.post(new BackEvent());
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
}
