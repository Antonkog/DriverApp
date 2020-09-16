package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HistoryFragment extends Fragment {

  private static final String TAG = HistoryFragment.class.getSimpleName();

  private HistoryViewModel historyViewModel;
  private RecyclerView recyclerView;
  private HistoryAdapter historyAdapter;
  private RecyclerView.LayoutManager layoutManager;
  private AppCompatButton btnClearLog;
  private int currentTaskId = 0;
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
      if(currentTaskId!=0) historyViewModel.setLogsWithTaskId(currentTaskId);
      else historyViewModel.setHistoryLogs();
    }
    initComponents(root);
    return root;
  }


  private void initComponents(@NonNull View root) {
    AppCompatImageButton  mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_history_back);
    mBtnBack.setOnClickListener(view -> App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null)));

    recyclerView = root.findViewById(R.id.recyclerView);

    layoutManager = new LinearLayoutManager(getContext());

    recyclerView.setLayoutManager(layoutManager);

    // specify an adapter (see also next example)
    historyAdapter = new HistoryAdapter();

    recyclerView.setAdapter(historyAdapter);

    historyViewModel.getHistoryLogs().observe(getViewLifecycleOwner(), logItems -> historyAdapter.swapData(logItems));

    btnClearLog = root.findViewById(R.id.btn_clear_log);
    btnClearLog.setOnClickListener(v -> historyViewModel.deleteChangeHistory());
  }
}
