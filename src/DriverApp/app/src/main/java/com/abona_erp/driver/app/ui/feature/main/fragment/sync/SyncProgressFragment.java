package com.abona_erp.driver.app.ui.feature.main.fragment.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;

import io.reactivex.disposables.CompositeDisposable;

public class SyncProgressFragment extends Fragment {

  private static final String TAG = SyncProgressFragment.class.getSimpleName();

  private CompositeDisposable disposables = new CompositeDisposable();
  private SyncProgressViewModel syncProgressViewModel;
  private RecyclerView rvConfirmations, rvDelays;
  private SyncProgressAdapter syncProgressAdapter;
  private SyncProgressAdapterDelays syncProgressAdapterDelays;
  private RecyclerView.LayoutManager layoutManagerConfirmations, layoutManagerDelays;

  public SyncProgressFragment() {
    // Required empty public constructor.
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    syncProgressViewModel = ViewModelProviders.of(this).get(SyncProgressViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_sync_progress, container, false);
    initComponents(root);
    return root;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    disposables.clear();
  }


  private void initComponents(@NonNull View root) {
    AppCompatImageButton  mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_sync_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });

    rvConfirmations = root.findViewById(R.id.recyclerConfirmations);
    rvDelays = root.findViewById(R.id.recyclerDelays);

    layoutManagerConfirmations = new LinearLayoutManager(getContext());
    layoutManagerDelays = new LinearLayoutManager(getContext());

    rvConfirmations.setLayoutManager(layoutManagerConfirmations);
    rvDelays.setLayoutManager(layoutManagerDelays);

    // specify an adapter (see also next example)
    syncProgressAdapter = new SyncProgressAdapter();
    syncProgressAdapterDelays = new SyncProgressAdapterDelays();

    rvConfirmations.setAdapter(syncProgressAdapter);
    rvDelays.setAdapter(syncProgressAdapterDelays);

    syncProgressViewModel.setConfirmations();
    syncProgressViewModel.getAllOfflineConfirmations().observe(getViewLifecycleOwner(), offlineConfirmations -> {
      syncProgressAdapter.swapData(offlineConfirmations);
      root.findViewById(R.id.text_confirm_header).setVisibility((offlineConfirmations.size() == 0) ? View.GONE : View.VISIBLE);
    });

    syncProgressViewModel.getAllDelayConfirmations().observe(getViewLifecycleOwner(), offlineDelayReasonEntityList -> {
      syncProgressAdapterDelays.swapData(offlineDelayReasonEntityList);
      root.findViewById(R.id.text_delay_header).setVisibility((offlineDelayReasonEntityList.size() == 0) ? View.GONE : View.VISIBLE);
    });

  }


}
