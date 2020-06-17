package com.abona_erp.driver.app.ui.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PropertiesBSFragment extends BottomSheetDialogFragment
  implements SeekBar.OnSeekBarChangeListener {
  
  public PropertiesBSFragment() {
    // Required empty public constructor.
  }
  
  private Properties mProperties;
  
  public interface Properties {
    void onColorChanged(int colorCode);
    void onBrushSizeChanged(int brushSize);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false);
  }
  
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        BottomSheetDialog dialog = (BottomSheetDialog)getDialog();
        FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(0);
      }
    });
    
    RecyclerView rvColor = view.findViewById(R.id.rvColors);
    SeekBar sbBrushSize = view.findViewById(R.id.sbSize);
    
    sbBrushSize.setOnSeekBarChangeListener(this);
  
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
      LinearLayoutManager.HORIZONTAL, false);
    rvColor.setLayoutManager(layoutManager);
    rvColor.setHasFixedSize(true);
    ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
    colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
      @Override
      public void onColorPickerClickListener(int colorCode) {
        if (mProperties != null) {
          dismiss();
          mProperties.onColorChanged(colorCode);
        }
      }
    });
    rvColor.setAdapter(colorPickerAdapter);
  }
  
  public void setPropertiesChangeListener(Properties properties) {
    mProperties = properties;
  }
  
  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    switch (seekBar.getId()) {
      case R.id.sbSize:
        if (mProperties != null) {
          mProperties.onBrushSizeChanged(i);
        }
        break;
    }
  }
  
  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
  }
  
  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
  }
}
