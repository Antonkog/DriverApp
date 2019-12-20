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
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.feature.main.view_model.CMRViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CMRFragment extends Fragment {

  private RecyclerView listView;
  private CMRViewModel viewModel;
  
  public CMRFragment() {
  }
  
  public static CMRFragment newInstance() {
    return new CMRFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ViewModelProviders.of(this)
      .get(CMRViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_cmr, container, false);

    listView = (RecyclerView) root.findViewById(R.id.lv_cmr_notify);

    LinearLayoutManager lm = new LinearLayoutManager(getContext().getApplicationContext(),
      RecyclerView.VERTICAL, false);
    listView.setLayoutManager(lm);

    DividerItemDecoration dividerItemDecoration =
      new DividerItemDecoration(listView.getContext(),
        lm.getOrientation());
    listView.addItemDecoration(dividerItemDecoration);

    NotifyViewAdapter adapter = new NotifyViewAdapter(getContext());
    adapter.setOnItemListener(new NotifyViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(Notify notify) {
        App.eventBus.post(new TaskDetailEvent(notify));
      }

      @Override
      public void onMapClick(Notify notify) {
        App.eventBus.post(new MapEvent(notify));
      }
      
      @Override
      public void onCameraClick(Notify notify) {
      }
    });

    listView.setAdapter(adapter);
    viewModel.getAllCMRNotifications().observe(this, new Observer<List<Notify>>() {
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
        App.eventBus.post(new BadgeCountEvent(2, notifies.size()));
      }
    });

    return root;
  }
}
