package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.view_model.RunningViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RunningFragment extends Fragment {
  
  private RecyclerView listView;
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_running, container, false);
    
    listView = (RecyclerView)root.findViewById(R.id.lv_running_notify);
  
    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getContext().getApplicationContext());
    listView.setLayoutManager(recyclerLayoutManager);
  
    DividerItemDecoration dividerItemDecoration =
      new DividerItemDecoration(listView.getContext(),
        recyclerLayoutManager.getOrientation());
    listView.addItemDecoration(dividerItemDecoration);

    NotifyViewAdapter adapter = new NotifyViewAdapter(getContext());
    adapter.setOnItemListener(new NotifyViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(Notify notify) {
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
    });

    listView.setAdapter(adapter);
    viewModel.getAllRunningNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies == null)
          return;
  
        Collections.sort(notifies, new Comparator<Notify>() {
          @Override
          public int compare(Notify notify, Notify t1) {
            return Integer.valueOf(notify.getOrderNo()).compareTo(t1.getOrderNo());
          }
        });
  
        Collections.sort(notifies, new Comparator<Notify>() {
          @Override
          public int compare(Notify notify, Notify t1) {
            return Integer.valueOf(notify.getTaskId()).compareTo(t1.getTaskId());
          }
        });

        adapter.setNotifyList(notifies);
        App.eventBus.post(new BadgeCountEvent(0, notifies.size()));
      }
    });

    return root;
  }
}
