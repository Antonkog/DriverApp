package com.abona_erp.driver.app.ui.feature.main.fragment.specialfunction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.SpecialActivities;
import com.abona_erp.driver.app.data.model.SpecialActivityResult;
import com.abona_erp.driver.app.data.model.SpecialFunction;
import com.abona_erp.driver.app.ui.feature.main.adapter.SpecialFunctionAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.abona_erp.driver.core.base.ContextUtils.getApplicationContext;

public class SFQRCodeDialog extends DialogFragment {
  
  private static final String TAG = SFQRCodeDialog.class.getSimpleName();
  
  // Arguments
  private int mNotifyId;
  private int mActivityIndex;
  
  // Notify
  private CommItem mCommItem;
  
  // Barcode
  private DecoratedBarcodeView barcodeView;
  private BeepManager beepManager;
  private Button btnResume;
  private Button btnPause;
  
  // Special Function List
  //private int mSFIndex;
  private RecyclerView rvSAList;
  
  // Back
  private AppCompatImageButton mBtnBack;
  
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
            storeCodeData(mActivityIndex, res);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
  
  public SFQRCodeDialog() {
  }
  
  public static SFQRCodeDialog newInstance(int notifyId, int activityId) {
    SFQRCodeDialog fragment = new SFQRCodeDialog();
  
    Bundle args = new Bundle();
    args.putInt("id", notifyId);
    args.putInt("actId", activityId);
    fragment.setArguments(args);
    
    return fragment;
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
    return inflater.inflate(R.layout.dialog_fragment_sf_qr, container);
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    initializeBarcodeView(view);
    initializeSpecialFunctionList(view);
    
    mBtnBack = (AppCompatImageButton)view.findViewById(R.id.btn_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        barcodePause();
        dismiss();
      }
    });
  }
  
  private void storeCodeData(int SFPos, String result) {
    
    if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
  
      ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
      if (ai != null && ai.getSpecialActivities() != null) {
  
        // Hole SpecialFunctions...
        List<SpecialActivities> sa = ai.getSpecialActivities();
        
        if (sa.get(0).getSpecialActivityResults() == null) {
          
          // Existiert nicht - neu anlegen.
          List<SpecialActivityResult> specialActivityResults = new ArrayList<SpecialActivityResult>();
          SpecialActivityResult sar = new SpecialActivityResult();
          sar.setSpecialFunctionFinished(new Date());
          sar.setResultString1(result);
          specialActivityResults.add(sar);
          
          mCommItem.getTaskItem().getActivities().get(mActivityIndex).getSpecialActivities()
            .get(0).setSpecialActivityResults(specialActivityResults);
        } else {
          
          SpecialActivityResult sar = new SpecialActivityResult();
          sar.setSpecialFunctionFinished(new Date());
          sar.setResultString1(result);
          sa.get(0).getSpecialActivityResults().add(sar);
        }
      }
      
      updateNotify();
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          dismiss();
        }
      }, 2000);
    }
  }
  
  void loadOrProgressData() {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        
        if (mNotifyId > 0) {
          mNotifyDAO.loadNotifyById(mNotifyId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(@io.reactivex.annotations.NonNull Notify notify) {
                
                mCommItem = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                if (mCommItem != null && mCommItem.getTaskItem() != null && mCommItem.getTaskItem().getActivities() != null) {
                  
                  // Activity holen.
                  ActivityItem ai = mCommItem.getTaskItem().getActivities().get(mActivityIndex);
                  if (ai != null && ai.getSpecialActivities() != null) {
                    
                    // SpecialActivities holen.
                    List<SpecialActivities> sa = ai.getSpecialActivities();
  
                    SpecialFunctionAdapter adapter = new SpecialFunctionAdapter(sa, null);
                  }
                }
              }
  
              @Override
              public void onError(@io.reactivex.annotations.NonNull Throwable e) {
    
              }
            });
        }
      }
    });
  }
  
  void updateNotify() {
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        
        mNotifyDAO.loadNotifyById(mNotifyId)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(@io.reactivex.annotations.NonNull Notify notify) {
              
              notify.setData(App.getInstance().gson.toJson(mCommItem));
              mNotifyDAO.updateNotify(notify);
              
              loadOrProgressData();
            }
  
            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
              loadOrProgressData();
            }
          });
      }
    });
  }
  
  @Override
  public void onResume() {
    super.onResume();
    barcodeResume();
    
    loadOrProgressData();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    barcodePause();
  }
  
  public void barcodePause() {
    barcodeView.pause();
  }
  
  public void barcodeResume() {
    barcodeView.resume();
  }
  
  private void initializeBarcodeView(@NonNull View root) {
    
    barcodeView = root.findViewById(R.id.barcode_scanner);
  
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    barcodeView.initializeFromIntent(requireActivity().getIntent());
    barcodeView.decodeContinuous(callback);
  
    beepManager = new BeepManager(requireActivity());
  
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
  
  private void initializeSpecialFunctionList(@NonNull View root) {
    
    rvSAList = (RecyclerView) root.findViewById(R.id.rvList);
    rvSAList.setLayoutManager(new LinearLayoutManager(getContext()));
  }
}
