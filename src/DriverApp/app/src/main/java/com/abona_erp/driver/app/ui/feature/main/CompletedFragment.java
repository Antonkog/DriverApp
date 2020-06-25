package com.abona_erp.driver.app.ui.feature.main;

import android.os.AsyncTask;
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
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommItemAdapter;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommonItemClickListener;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableRecyclerView;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CompletedFragment extends Fragment implements CommonItemClickListener<Notify> {

  CommItemAdapter adapter;
  private ExpandableRecyclerView listView;
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

    listView = (ExpandableRecyclerView) root.findViewById(R.id.rv_list);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    listView.setLayoutManager(llm);
    adapter = new CommItemAdapter(getContext(),this);
    listView.setAdapter(adapter);
    
    viewModel.getAllCompletedNotifications().observe(getViewLifecycleOwner(), new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies == null) return;

        synchronized (CompletedFragment.this) {
          if (notifies.size() > 0) {
            long now = System.currentTimeMillis() - (7200 * 60 * 1000);
            for (int i = 0; i < notifies.size(); i++) {
              // Check older tasks:
              CommItem commItem = new CommItem();
              commItem = App.getInstance().gsonUtc.fromJson(notifies.get(i).getData(), CommItem.class);
              
              final int k = i;
              if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    viewModel.delete(notifies.get(k));
                  }
                });
                
                viewModel.getLastActivityByTaskClientId(commItem.getTaskItem().getTaskId(),
                  commItem.getTaskItem().getMandantId())
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
                continue;
              }
  
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
        
        adapter.setList(notifies);
        App.eventBus.post(new BadgeCountEvent(3, notifies.size()));
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
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), item));
    }
  }
  @Override
  public void onProgressItemClick(Notify notify) {
    viewModel.update(notify);
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
  }

  @Override
  public void onMapClick(Notify notify) {
    viewModel.update(notify);
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_MAP), notify));
  }

  @Override
  public void onCameraClick(Notify notify) {
    viewModel.update(notify);
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_CAMERA), notify));
  }

  @Override
  public void onDocumentClick(Notify notify) {
    viewModel.update(notify);
    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), notify));
  }
}
