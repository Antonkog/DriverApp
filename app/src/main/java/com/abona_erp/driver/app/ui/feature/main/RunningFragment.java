package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;

import java.util.List;

public class RunningFragment extends Fragment {
  
  private RecyclerView listView;
  
  public RunningFragment() {
  }
  
  public static RunningFragment newInstance() {
    return new RunningFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
  
    NotifyRepository.getNotifyDatabase(getContext()).notifyDao().getRunningNotifies()
      .observe(this, new Observer<List<Notify>>() {
        @Override
        public void onChanged(List<Notify> notifies) {
          if (notifies == null) {
            return;
          }
  
          NotifyViewAdapter viewAdapter = new NotifyViewAdapter(notifies, getContext());
          listView.setAdapter(viewAdapter);
          viewAdapter.setOnItemListener(new NotifyViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Notify notify) {
    
            }
  
            @Override
            public void onMapClick() {
    
            }
          });
        }
      });
    
    return root;
  }
}
