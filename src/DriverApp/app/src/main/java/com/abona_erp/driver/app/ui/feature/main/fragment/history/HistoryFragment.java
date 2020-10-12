package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.os.Bundle;
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
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.MovableFloatingActionButton;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.google.android.material.tabs.TabLayout;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends Fragment {

  private static final String TAG = HistoryFragment.class.getSimpleName();

  private CompositeDisposable disposables = new CompositeDisposable();
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
    disposables.clear();
  }


  private void initComponents(@NonNull View root) {
    AppCompatImageButton  mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_history_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });

    recyclerView = root.findViewById(R.id.recyclerView);

    layoutManager = new LinearLayoutManager(getContext());

    recyclerView.setLayoutManager(layoutManager);

    // specify an adapter (see also next example)
    historyAdapter = new HistoryAdapter();

    recyclerView.setAdapter(historyAdapter);

    historyViewModel.getHistoryLogs().observe(getViewLifecycleOwner(), logItems -> historyViewModel.refreshLogs(logItems, currentTaskId, currentOrderNo));
    historyViewModel.getFilteredLogs().observe(getViewLifecycleOwner(), logItems -> historyAdapter.swapData(logItems));


    tabLayout = root.findViewById(R.id.tabs);
    if (currentTaskId != 0)
      tabLayout.getTabAt(tabTaskPosition).setText(getResources().getString(R.string.task) + " " + currentTaskId);
    if (currentOrderNo != 0)
      tabLayout.getTabAt(tabOrdersPosition).setText(getResources().getString(R.string.order) + " " + AppUtils.parseOrderNo(currentOrderNo));

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
    historyViewModel.getDeviceProfile().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            result -> sendEmailWithMessage(result.toString()),
            error -> sendEmailWithMessage(" \n deviceId:" + DeviceUtils.getUniqueID(getContext())));
  }

  private void sendEmailWithMessage(String message){
      historyViewModel.subscribeEmailSent(getContext(), message)
     .subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new CompletableObserver() {
              @Override
              public void onSubscribe(Disposable d) {
                disposables.add(d);
                showProgressBar();
              }

              @Override
              public void onComplete() {
                hideProgressBar();
              }

              @Override
              public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                hideProgressBar();
                historyViewModel.sendEmailMessage(getContext(), message);
              }
            });
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
