package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;

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
    
    listView = (RecyclerView) root.findViewById(R.id.lv_pending_notify);
  
    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getContext().getApplicationContext(),
        RecyclerView.VERTICAL, false);
    listView.setLayoutManager(recyclerLayoutManager);
  
    DividerItemDecoration dividerItemDecoration =
      new DividerItemDecoration(listView.getContext(),
        recyclerLayoutManager.getOrientation());
    listView.addItemDecoration(dividerItemDecoration);
  
    NotifyRepository.getNotifyDatabase(getContext()).notifyDao().getPendingNotifies()
      .observe(this, new Observer<List<Notify>>() {
        @Override
        public void onChanged(@Nullable List<Notify> notifies) {
          if (notifies == null) {
            return;
          }
          
          //listView.addItemDecoration(getSectionCallback(notifies));
          
          NotifyViewAdapter viewAdapter = new NotifyViewAdapter(notifies, getContext());
          listView.setAdapter(viewAdapter);
          viewAdapter.setOnItemListener(new NotifyViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Notify notify) {
              App.eventBus.post(new TaskDetailEvent(notify));
            }
            
            @Override
            public void onMapClick() {
              App.eventBus.post(new MapEvent());
            }
          });
        }
      });
    
    return root;
  }
  
  /*
  private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Notify> notifyList) {
    return new RecyclerSectionItemDecoration.SectionCallback() {
    
      @Nullable
      @Override
      public SectionInfo getSectionHeader(int position) {
        Notify notify = notifyList.get(position);
        
        return new SectionInfo(notify.getCreatedAt().toString(), "TEST", AppCompatResources.getDrawable(getContext(), R.drawable.ic_circle));
      }
      
      @Override
      public boolean isSection(int position) {
        return !notifyList.get(position).getCreatedAt().toString().equals(notifyList.get(position - 1).getCreatedAt().toString());
      }
    };
  }
 
   */
}
