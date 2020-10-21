package com.abona_erp.driver.app.ui.widget.qrcode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.widget.AppCompatButton;

import com.abona_erp.driver.app.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Random;

public class CustomQRDialog extends Dialog implements View.OnClickListener,
  DecoratedBarcodeView.TorchListener {
  
  private CaptureManager capture;
  private DecoratedBarcodeView barcodeScannerView;
  private ViewfinderView viewfinderView;
  
  private AppCompatButton btnClose;
  
  public CustomQRDialog(Context context, int themeResId) {
    super(context, themeResId);
  }
  
  public CustomQRDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  
  public Activity activity;
  public Dialog dialog;
  
  public CustomQRDialog(Activity a) {
    super(a);
    this.activity = a;
    setupLayout();
  }
  
  private void setupLayout() {
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_qr);
    
    btnClose = (AppCompatButton) findViewById(R.id.btn_close);
    btnClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });
  
    barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
    barcodeScannerView.setTorchListener(this);
  
    viewfinderView = findViewById(R.id.zxing_viewfinder_view);
    
    capture = new CaptureManager(activity, barcodeScannerView);
    capture.initializeFromIntent(activity.getIntent(), savedInstanceState);
    capture.setShowMissingCameraPermissionDialog(false);
    capture.decode();
    
    capture.onResume();
  
    changeMaskColor(null);
    changeLaserVisibility(true);
  }
  
  @Override
  public void onClick(View v) {
  }
  
  @Override
  public void onTorchOn() {
  }
  
  @Override
  public void onTorchOff() {
  }
  
  public void changeMaskColor(View view) {
    Random rnd = new Random();
    int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    viewfinderView.setMaskColor(color);
  }
  
  public void changeLaserVisibility(boolean visible) {
    viewfinderView.setLaserVisibility(visible);
  }
}
