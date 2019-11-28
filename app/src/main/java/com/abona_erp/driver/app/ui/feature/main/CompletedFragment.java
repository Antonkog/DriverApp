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
import com.abona_erp.driver.app.ui.feature.main.view_model.CompletedViewModel;

import java.util.List;

public class CompletedFragment extends Fragment {

  private RecyclerView listView;
  private CompletedViewModel viewModel;
  
  public CompletedFragment() {
  }
  
  public static CompletedFragment newInstance() {
    return new CompletedFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ViewModelProviders.of(this)
      .get(CompletedViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_completed, container, false);

    listView = (RecyclerView)root.findViewById(R.id.lv_completed_notify);

    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getContext().getApplicationContext(),
        RecyclerView.VERTICAL, false);
    listView.setLayoutManager(recyclerLayoutManager);

    DividerItemDecoration dividerItemDecoration =
      new DividerItemDecoration(listView.getContext(),
        recyclerLayoutManager.getOrientation());
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
    viewModel.getAllCompletedNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies == null)
          return;

        adapter.setNotifyList(notifies);
        App.eventBus.post(new BadgeCountEvent(3, notifies.size()));
      }
    });

    return root;
  }
}
