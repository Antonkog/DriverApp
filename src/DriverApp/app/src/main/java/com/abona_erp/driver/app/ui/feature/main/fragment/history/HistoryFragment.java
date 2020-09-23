package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.MovableFloatingActionButton;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.google.android.material.tabs.TabLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends Fragment {

  private static final String TAG = HistoryFragment.class.getSimpleName();

  private HistoryViewModel historyViewModel;
  private RecyclerView recyclerView;
  private HistoryAdapter historyAdapter;
  private RecyclerView.LayoutManager layoutManager;
  private AppCompatButton btnClearLog;
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
    fabEmail = root.findViewById(R.id.fab_mail);
    btnClearLog.setOnClickListener(v -> historyViewModel.deleteChangeHistory());
    fabEmail.setOnClickListener(v -> sendEmail());
  }

  private void sendEmail() {
    historyViewModel.getDeviceProfile().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            result -> sendEmail(historyViewModel.getEmailMessage(getContext()) + " \n " + result.toString()),
            error -> sendEmail(historyViewModel.getEmailMessage(getContext()) + " \n deviceId:" + DeviceUtils.getUniqueID(getContext())));
  }

  private void sendEmail(String message) {
    if (message == null)
      Toast.makeText(getContext(), getResources().getString(R.string.log_message_unknown_error), Toast.LENGTH_LONG).show();
    Intent email = new Intent(Intent.ACTION_SEND);
    email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_support)});
    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.history_email_theme));
    email.putExtra(Intent.EXTRA_TEXT, message);

//need this to prompts email client only
    email.setType("message/rfc822");

    try {
      //Intent.createChooser(email, "Choose an Email client :")
      startActivity(email);
    } catch (ActivityNotFoundException e) {
      Toast.makeText(getContext(), getResources().getString(R.string.error_history_email), Toast.LENGTH_LONG).show();
    }
  }

  private void refreshByOrderNo() {
    if (currentOrderNo != 0) historyViewModel.setHistoryLogsByOrderNumber();
  }

  private void refreshByTaskId() {
    if (currentTaskId != 0) historyViewModel.setLogsWithTaskId();
  }
}
