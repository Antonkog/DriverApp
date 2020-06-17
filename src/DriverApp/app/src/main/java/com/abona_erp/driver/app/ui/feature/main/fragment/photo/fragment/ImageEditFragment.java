package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.EditingDeleteEvent;
import com.abona_erp.driver.app.ui.event.EditingSaveEvent;
import com.abona_erp.driver.app.ui.event.EditingToolsEvent;
import com.abona_erp.driver.app.ui.event.RefreshUiEvent;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.ToolType;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.EditingToolsAdapter;
import com.abona_erp.driver.app.ui.widget.PropertiesBSFragment;
import com.abona_erp.driver.app.ui.widget.TextEditorDialogFragment;
import com.abona_erp.driver.photolib.OnPhotoEditorListener;
import com.abona_erp.driver.photolib.PhotoEditor;
import com.abona_erp.driver.photolib.PhotoEditorView;
import com.abona_erp.driver.photolib.SaveSettings;
import com.abona_erp.driver.photolib.TextStyleBuilder;
import com.abona_erp.driver.photolib.ViewType;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

public class ImageEditFragment extends Fragment
  implements EditingToolsAdapter.OnItemSelected,
  PropertiesBSFragment.Properties,
  OnPhotoEditorListener {
  
  private static final String TAG = ImageEditFragment.class.getSimpleName();
  
  private static ImageEditFragment sINSTANCE = null;
  
  private PhotoEditor mPhotoEditor;
  private PhotoEditorView mPhotoEditorView;
  
  private File mEditFile = null;
  
  private PropertiesBSFragment mPropertiesBSFragment;
  
  private RecyclerView mRvEditingTools;
  private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
  
  public ImageEditFragment() {
    // Required empty public constructor.
  }
  
  public static ImageEditFragment newInstance() {
    if (sINSTANCE == null) {
      sINSTANCE = new ImageEditFragment();
    }
    return sINSTANCE;
  }
  
  @Subscribe
  public void onMessageEvent(EditingToolsEvent event) {
    showOrHideTools();
  }
  
  @Subscribe
  public void onMessageEvent(EditingSaveEvent event) {
    saveImage();
  }
  
  @Subscribe
  public void onMessageEvent(EditingDeleteEvent event) {
    mPhotoEditor.clearAllViews();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_image_edit_view, container, false);
    initComponents(root);
    return root;
  }
  
  public void setBitmap(Bitmap bitmap, File file) {
    try {
      if (getContext() != null && bitmap == null) {
        mEditFile = null;
        mPhotoEditorView.getSource().setImageBitmap(drawableToBitmap(getContext().getResources().getDrawable(R.drawable.no_image_rect)));
      } else {
        mEditFile = file;
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
      }
    } catch (NullPointerException e) {
      mEditFile = null;
      Log.e(TAG, e.getMessage());
    }
  }
  
  @SuppressLint("MissingPermission")
  private void saveImage() {
    SaveSettings saveSettings = new SaveSettings.Builder()
      .setClearViewsEnabled(true)
      .setTransparencyEnabled(false)
      .build();
    mPhotoEditor.saveAsFile(mEditFile.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
      @Override
      public void onSuccess(@NonNull String imagePath) {
        App.eventBus.post(new RefreshUiEvent());
      }
  
      @Override
      public void onFailure(@NonNull Exception exception) {
      
      }
    });
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
  
  private void initComponents(@NonNull View root) {
    mPhotoEditorView = (PhotoEditorView)root.findViewById(R.id.photo_editor_view);
    
    mRvEditingTools = (RecyclerView)root.findViewById(R.id.rv_editing_tools);
    LinearLayoutManager llmEditingTools = new LinearLayoutManager(getContext(),
      LinearLayoutManager.HORIZONTAL, false);
    mRvEditingTools.setLayoutManager(llmEditingTools);
    mRvEditingTools.setAdapter(mEditingToolsAdapter);
    
    mPropertiesBSFragment = new PropertiesBSFragment();
    mPropertiesBSFragment.setPropertiesChangeListener(this);
    
    mPhotoEditor = new PhotoEditor.Builder(getContext(), mPhotoEditorView)
      .setPinchTextScalable(true)
      .build();
    mPhotoEditor.setOnPhotoEditorListener(this);
  }
  
  public static Bitmap drawableToBitmap (Drawable drawable) {
    Bitmap bitmap = null;
    
    if (drawable instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if(bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }
    
    if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    } else {
      bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }
    
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }
  
  @Override
  public void onToolSelected(ToolType toolType) {
    switch (toolType) {
      case BRUSH:
        mPhotoEditor.setBrushDrawingMode(true);
        mPropertiesBSFragment.show(getActivity().getSupportFragmentManager(),
          mPropertiesBSFragment.getTag());
        break;
      case TEXT:
        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment
          .show((AppCompatActivity)getActivity());
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
          @Override
          public void onDone(String inputText, int colorCode) {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(colorCode);
  
            mPhotoEditor.addText(inputText, styleBuilder);
          }
        });
        break;
      case ERASER:
        mPhotoEditor.brushEraser();
        break;
      case UNDO:
        mPhotoEditor.undo();
        break;
      case REDO:
        mPhotoEditor.redo();
        break;
    }
  }
  
  public boolean isCacheEmpty() {
    return mPhotoEditor.isCacheEmpty();
  }
  
  private void showOrHideTools() {
    if (mRvEditingTools == null) return;
    if (mRvEditingTools.getVisibility() == View.VISIBLE) {
      mRvEditingTools.setVisibility(View.GONE);
      mPhotoEditor.setBrushDrawingMode(false);
    } else {
      mRvEditingTools.setVisibility(View.VISIBLE);
    }
  }
  
  @Override
  public void onEditTextChangeListener(View rootView, String text, int colorCode) {
    TextEditorDialogFragment textEditorDialogFragment =
      TextEditorDialogFragment.show((AppCompatActivity)getActivity(), text, colorCode);
    textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
      @Override
      public void onDone(String inputText, int colorCode) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
      
        mPhotoEditor.editText(rootView, inputText, styleBuilder);
      }
    });
  }
  
  @Override
  public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
  }
  
  @Override
  public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
  }
  
  @Override
  public void onStartViewChangeListener(ViewType viewType) {
  }
  
  @Override
  public void onStopViewChangeListener(ViewType viewType) {
  }
  
  @Override
  public void onColorChanged(int colorCode) {
    mPhotoEditor.setBrushColor(colorCode);
  }
  
  @Override
  public void onBrushSizeChanged(int brushSize) {
    mPhotoEditor.setBrushSize(brushSize);
  }
}
