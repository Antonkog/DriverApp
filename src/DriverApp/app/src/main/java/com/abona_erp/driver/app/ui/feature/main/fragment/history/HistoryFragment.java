package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.MovableFloatingActionButton;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import co.nedim.maildroidx.MaildroidX;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends Fragment {

  private static final String TAG = HistoryFragment.class.getSimpleName();

  private HistoryViewModel historyViewModel;
  private RecyclerView recyclerView;
  private HistoryAdapter historyAdapter;
  private RecyclerView.LayoutManager layoutManager;
  private AppCompatButton btnClearLog;
  private ProgressBar progressBar;
  private MovableFloatingActionButton fabEmail;
  private int currentTaskId = 0, currentOrderNo = 0;
  private TabLayout tabLayout;
  private final int tabTaskPosition = 0, tabOrdersPosition = 1;
  private Handler smtpHandler = new Handler(Looper.getMainLooper());

  private CompositeDisposable historyDisposibles = new CompositeDisposable();

  public HistoryFragment() {
    // Required empty public constructor.
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_history, container, false);
    if (getArguments() != null) {
      currentTaskId = getArguments().getInt(getResources().getString(R.string.key_taskId));
      currentOrderNo = getArguments().getInt(getResources().getString(R.string.key_orderNo));
    }
    historyViewModel.setHistoryLogs();
    initComponents(root);
    return root;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(!historyDisposibles.isDisposed()) historyDisposibles.dispose();
  }


  private void initComponents(@NonNull View root) {
    AppCompatImageButton  mBtnBack = root.findViewById(R.id.btn_history_back);
    mBtnBack.setOnClickListener(v -> App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null)));

    recyclerView = root.findViewById(R.id.recyclerView);

    layoutManager = new LinearLayoutManager(getContext());

    recyclerView.setLayoutManager(layoutManager);

    // specify an adapter (see also next example)
    historyAdapter = new HistoryAdapter();

    recyclerView.setAdapter(historyAdapter);

    historyViewModel.getHistoryLogs().observe(getViewLifecycleOwner(), logItems -> historyViewModel.refreshLogs(logItems, currentTaskId, currentOrderNo));
    historyViewModel.getFilteredLogs().observe(getViewLifecycleOwner(), logItems -> historyAdapter.swapData(logItems));


    try {
      tabLayout = root.findViewById(R.id.tabs);
      if (currentTaskId != 0)
        tabLayout.getTabAt(tabTaskPosition).setText(getResources().getString(R.string.task) + " " + currentTaskId);
      if (currentOrderNo != 0)
        tabLayout.getTabAt(tabOrdersPosition).setText(getResources().getString(R.string.order) + " " + AppUtils.parseOrderNo(currentOrderNo));
    } catch (NullPointerException e){
      Log.e(TAG, "wrong tab position" + e.getMessage());
    }

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
          case tabTaskPosition:
            refreshByTaskId();
            break;
          case tabOrdersPosition:
            refreshByOrderNo();
            break;
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    btnClearLog = root.findViewById(R.id.btn_clear_log);
    progressBar = root.findViewById(R.id.progress_circular);
    fabEmail = root.findViewById(R.id.fab_mail);
    btnClearLog.setOnClickListener(v -> historyViewModel.deleteChangeHistory());
    fabEmail.setOnClickListener(v -> getDeviceInfoAndSendEmail());
  }

  private void getDeviceInfoAndSendEmail() {
    historyDisposibles.add(
      historyViewModel.getDeviceProfile().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
              result -> sendEmailWithMessage(result.toString()),
              error -> sendEmailWithMessage(" \n deviceId:" + DeviceUtils.getUniqueIMEI(getContext())))
    );
  }

  private void sendEmailWithMessage(String message){
   MaildroidX.onCompleteCallback callback =  new MaildroidX.onCompleteCallback() {
      @Override
      public void onSuccess() {
        hideProgressBar();
      }

      @Override
      public void onFail(@NotNull String s) {
        android.util.Log.e(TAG, "email: failed to send" );
        hideProgressBar();
        historyViewModel.sendEmailIntentMessage(getContext(), message);
      }

      @Override
      public long getTimeout() {
        return Constants.TIMEOUT_SMTP_SEND;
      }
    };

    showProgressBar();
    historyViewModel.sendEmailSmtp(getContext(), message, callback);
    // no callback from library, need to check
    smtpHandler.postDelayed(() -> {
      if(progressBar.getVisibility() == View.VISIBLE){// no callback from lib on timeout and thet's why progress visible.
        android.util.Log.e(TAG, "email was n't send in 5 seconds" );
        hideProgressBar();
        historyViewModel.sendEmailIntentMessage(getContext(), message);
      }
    }, Constants.TIMEOUT_SMTP_SEND +100);//100 millisec. after timeout
  }

  private void refreshByOrderNo() {
    if (currentOrderNo != 0) historyViewModel.setHistoryLogsByOrderNumber();
  }

  private void refreshByTaskId() {
    if (currentTaskId != 0) historyViewModel.setLogsWithTaskId();
  }

  public void showProgressBar(){
    progressBar.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
  }

  public void hideProgressBar(){
    progressBar.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
  }
}
