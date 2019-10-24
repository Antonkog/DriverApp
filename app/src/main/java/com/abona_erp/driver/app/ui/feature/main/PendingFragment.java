package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;

import java.util.List;

public class PendingFragment extends Fragment {
  
  private RecyclerView listView;
  
  public PendingFragment() {
  }
  
  public static PendingFragment newInstance() {
    return new PendingFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_pending, container, false);
    
    listView = (RecyclerView)root.findViewById(R.id.lv_pending_notify);
  
    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getContext().getApplicationContext());
    listView.setLayoutManager(recyclerLayoutManager);
  
    DividerItemDecoration dividerItemDecoration =
      new DividerItemDecoration(listView.getContext(),
        recyclerLayoutManager.getOrientation());
    listView.addItemDecoration(dividerItemDecoration);
  
    NotifyRepository.getNotifyDatabase(getContext()).notifyDao().getNotifies()
      .observe(this, new Observer<List<Notify>>() {
        @Override
        public void onChanged(@Nullable List<Notify> notifies) {
          if (notifies == null) {
            return;
          }
          
          NotifyViewAdapter viewAdapter = new NotifyViewAdapter(notifies,
            getContext());
          listView.setAdapter(viewAdapter);
        }
      });
    
    return root;
  }
}
