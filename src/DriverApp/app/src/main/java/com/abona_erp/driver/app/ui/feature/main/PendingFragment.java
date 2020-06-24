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
import com.abona_erp.driver.app.ui.feature.main.view_model.PendingViewModel;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableRecyclerView;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommonItemClickListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PendingFragment extends Fragment implements CommonItemClickListener<Notify> {
  
  private ExpandableRecyclerView listView;
  private PendingViewModel viewModel;

  public PendingFragment() {
  }

  public static PendingFragment newInstance() {
    return new PendingFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ViewModelProviders.of(this)
      .get(PendingViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_pending, container, false);
    
    listView = (ExpandableRecyclerView)root.findViewById(R.id.rv_list);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    listView.setLayoutManager(llm);
    listView.setNestedScrollingEnabled(true);
    CommItemAdapter adapter = new CommItemAdapter(getContext(), this);
    listView.setAdapter(adapter);
    
    viewModel.getAllPendingNotifications().observe(getViewLifecycleOwner(),
      new Observer<List<Notify>>() {
      
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies == null) return;
        
        Collections.sort(notifies, new Comparator<Notify>() {
          @Override
          public int compare(Notify notify, Notify t1) {
            if (notify == null || t1 == null) return 0;
            return Integer.valueOf(notify.getOrderNo()).compareTo(t1.getOrderNo());
          }
        });
        
        Collections.sort(notifies, new Comparator<Notify>() {
          @Override
          public int compare(Notify notify, Notify t1) {
            if (notify == null || t1 == null) return 0;
            return Integer.valueOf(notify.getTaskId()).compareTo(t1.getTaskId());
          }
        });
        
        adapter.setList(notifies);
        App.eventBus.post(new BadgeCountEvent(0, notifies.size()));
      }
    });

    return root;
  }
  
  @Override
  public void onClick(View view, int position, Notify item, boolean selected) {
      item.setCurrentlySelected(selected);
      viewModel.update(item);
  }
  
  @Override
  public void onDblClick(View view, int position, Notify item) {
    if (item != null) {
      viewModel.update(item);
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), item));
    }
  }
  @Override
  public void onProgressItemClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
    viewModel.update(notify);
  }

  @Override
  public void onMapClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_MAP), notify));
    viewModel.update(notify);
  }

  @Override
  public void onCameraClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_CAMERA), notify));
    viewModel.update(notify);
  }

  @Override
  public void onDocumentClick(Notify notify) {
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), notify));
    viewModel.update(notify);
  }
}
