package com.abona_erp.driver.app.ui.feature.main.fragment.qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QRFragment extends Fragment {
  
  private static final String TAG = QRFragment.class.getSimpleName();
  
  private AppCompatImageButton mBtnBack;
  private DecoratedBarcodeView barcodeView;
  private BeepManager beepManager;
  private Button btnResume;
  private Button btnPause;
  
  private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
      beepManager.playBeepSoundAndVibrate();
      
      switch (result.getBarcodeFormat()) {
        case QR_CODE:
          Toast.makeText(getContext(), "QR CODE", Toast.LENGTH_SHORT).show();
          break;
        case AZTEC:
          Toast.makeText(getContext(), "AZTEC", Toast.LENGTH_SHORT).show();
          break;
        case DATA_MATRIX:
          Toast.makeText(getContext(), "DATA_MATRIX", Toast.LENGTH_SHORT).show();
          break;
        case EAN_8:
          Toast.makeText(getContext(), "EAN 8", Toast.LENGTH_SHORT).show();
          break;
        case EAN_13:
          Toast.makeText(getContext(), "EAN 13", Toast.LENGTH_SHORT).show();
          break;
        case CODE_39:
          Toast.makeText(getContext(), "CODE 39", Toast.LENGTH_SHORT).show();
          break;
        case CODE_93:
          Toast.makeText(getContext(), "CODE 93", Toast.LENGTH_SHORT).show();
          break;
        case CODE_128:
          Toast.makeText(getContext(), "CODE 128", Toast.LENGTH_SHORT).show();
          break;
        case CODABAR:
          Toast.makeText(getContext(), "CODEBAR", Toast.LENGTH_SHORT).show();
          break;
        case ITF:
          Toast.makeText(getContext(), "ITF", Toast.LENGTH_SHORT).show();
          break;
        case UPC_EAN_EXTENSION:
          Toast.makeText(getContext(), "UPC_EAN_EXTENSION", Toast.LENGTH_SHORT).show();
          break;
        case UPC_E:
          Toast.makeText(getContext(), "UPC_E", Toast.LENGTH_SHORT).show();
          break;
        case UPC_A:
          Toast.makeText(getContext(), "UPC_A", Toast.LENGTH_SHORT).show();
          break;
        case PDF_417:
          Toast.makeText(getContext(), "PDF_417", Toast.LENGTH_SHORT).show();
          break;
      }
    }
    
    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    
    }
  };
  
  public QRFragment() {
    // Required empty public constructor.
  }
  
  public static QRFragment newInstance() {
    return new QRFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    View root = inflater.inflate(R.layout.fragment_qr, container, false);
    initComponents(root);
    
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    barcodeView.resume();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    barcodeView.pause();
  }
  
  public void pause(View view) {
    barcodeView.pause();
  }
  
  public void resume(View view) {
    barcodeView.resume();
  }
  
  private void initComponents(@NonNull View root) {
  
    mBtnBack = (AppCompatImageButton) root.findViewById(R.id.btn_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodeView.pause();
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
    
    barcodeView = root.findViewById(R.id.barcode_scanner);
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    barcodeView.initializeFromIntent(getActivity().getIntent());
    barcodeView.decodeContinuous(callback);
    
    beepManager = new BeepManager(getActivity());
    
    btnPause = (Button) root.findViewById(R.id.btn_pause);
    btnPause.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodeView.pause();
      }
    });
    
    btnResume = (Button) root.findViewById(R.id.btn_resume);
    btnResume.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodeView.resume();
      }
    });
  }
}
