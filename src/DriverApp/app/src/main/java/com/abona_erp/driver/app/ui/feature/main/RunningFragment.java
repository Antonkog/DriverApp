package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommItemAdapter;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommonItemClickListener;
import com.abona_erp.driver.app.ui.feature.main.view_model.RunningViewModel;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableRecyclerView;

import java.util.List;

public class RunningFragment extends Fragment implements CommonItemClickListener<Notify> {
  
  CommItemAdapter adapter;
  private ExpandableRecyclerView listView;
  private RunningViewModel viewModel;
  
  public RunningFragment() {
  }
  
  public static RunningFragment newInstance() {
    return new RunningFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ViewModelProviders.of(this)
      .get(RunningViewModel.class);
  }
  
  @Override
  public void onStart() {
    super.onStart();
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_running, container, false);
  
    listView = (ExpandableRecyclerView)root.findViewById(R.id.rv_list);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    listView.setLayoutManager(llm);
    adapter = new CommItemAdapter(getContext(), this);

    listView.setAdapter(adapter);
  
    viewModel.getAllRunningNotifications().observe(getViewLifecycleOwner(),
      new Observer<List<Notify>>() {
        @Override
        public void onChanged(List<Notify> notifies) {
          if (notifies == null) return;
          adapter.setList(notifies);
          App.eventBus.post(new BadgeCountEvent(1, notifies.size()));
        }
      });
    
    return root;
  }
  
  @Override
  public void onClick(View view, int position, Notify notify, boolean selected) {
    notify.setCurrentlySelected(selected);
    viewModel.update(notify);
  }
  
  @Override
  public void onDblClick(View view, int position, Notify notify) {
    if (notify != null) {
      viewModel.update(notify);
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
    }
  }
  @Override
  public void onProgressItemClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
  }

  @Override
  public void onMapClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_MAP), notify));
  }

  @Override
  public void onCameraClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_CAMERA), notify));
  }

  @Override
  public void onDocumentClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), notify));
  }
}
