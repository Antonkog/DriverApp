package com.abona_erp.driver.app.ui.feature.main;

import android.os.AsyncTask;
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
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.feature.main.view_model.CompletedViewModel;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CompletedFragment extends Fragment {

  private RecyclerView listView;
  private MainViewModel viewModel;
  
  public CompletedFragment() {
  }
  
  public static CompletedFragment newInstance() {
    return new CompletedFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = ViewModelProviders.of(this)
      .get(MainViewModel.class);
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
        
        synchronized (CompletedFragment.this) {
          if (notifies.size() > 0) {
            long now = System.currentTimeMillis() - (7200 * 60 * 1000);
            for (int i = 0; i < notifies.size(); i++) {
              // Check older tasks:
              CommItem commItem = new CommItem();
              commItem = App.getGson().fromJson(notifies.get(i).getData(), CommItem.class);
      
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(commItem.getTaskItem().getActivities().get(commItem.getTaskItem().getActivities().size()-1).getFinished());
      
              if (calendar.getTimeInMillis() < now) {
                // Older than 2 minutes.
                Log.i("CompletedFragment", "Older than 5 days...");
                Log.i("CompletedFragment", "++++++++++++++++++++++++++++++++++++++++++++");
  
                viewModel.getLastActivityByTaskClientId(commItem.getTaskItem().getTaskId(), commItem.getTaskItem().getMandantId())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<LastActivity>() {
                    @Override
                    public void onSuccess(LastActivity lastActivity) {
                      AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                          viewModel.delete(lastActivity);
                        }
                      });
                    }
      
                    @Override
                    public void onError(Throwable e) {
        
                    }
                  });
        
                final int k = i;
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    viewModel.delete(notifies.get(k));
                  }
                });
              }
            }
          }
        }

        adapter.setNotifyList(notifies);
        App.eventBus.post(new BadgeCountEvent(3, notifies.size()));
      }
    });

    return root;
  }
}
