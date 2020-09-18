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
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.ActionType;
import com.abona_erp.driver.app.data.entity.ChangeHistoryState;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.LogEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommItemAdapterExt;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommonItemClickListener;
import com.abona_erp.driver.app.ui.feature.main.view_model.PendingViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements CommonItemClickListener<Notify> {
  
  private RecyclerView listView;
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
    
    listView = (RecyclerView)root.findViewById(R.id.rv_list);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    listView.setLayoutManager(llm);
    listView.setNestedScrollingEnabled(true);
    CommItemAdapterExt adapter = new CommItemAdapterExt(getContext(), this);
    listView.setAdapter(adapter);
    
    viewModel.getAllPendingNotifications().observe(getViewLifecycleOwner(),
      new Observer<List<Notify>>() {

      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies != null && notifies.size() > 0) {
          adapter.setDataList(notifies);
          App.eventBus.post(new BadgeCountEvent(0, notifies.size()));
        } else {
          adapter.setDataList(new ArrayList<>());
        }
      }
    });

    return root;
  }
  
  @Override
  public void onClick(View view, int position, Notify item, boolean selected) {
    App.selectedTaskPos = position;
      item.setCurrentlySelected(selected);
      if (!item.getRead()) {
        item.setRead(true);
        viewModel.update(item);
        DriverDatabase db = DriverDatabase.getDatabase();
        OfflineConfirmationDAO dao = db.offlineConfirmationDAO();

        OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
        offlineConfirmation.setNotifyId((int)item.getId());
        offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal());
        postHistoryEvent(item, offlineConfirmation);
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            dao.insert(offlineConfirmation);
          }
        });
      }
  }
  
  @Override
  public void onDblClick(View view, int position, Notify item) {
    if (item != null) {
      if (!item.getRead()) {
        item.setRead(true);
  
        DriverDatabase db = DriverDatabase.getDatabase();
        OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
  
        OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
        offlineConfirmation.setNotifyId((int)item.getId());
        offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal());
        postHistoryEvent(item, offlineConfirmation);
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            dao.insert(offlineConfirmation);
          }
        });
      }
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), item));
    }
  }

  private void postHistoryEvent(Notify item, OfflineConfirmation offlineConfirmation) {
    EventBus.getDefault().post(new ChangeHistoryEvent(getContext().getString(R.string.log_title_fcm), getContext().getString(R.string.log_confirm_open),
            LogType.FCM, ActionType.UPDATE_TASK, ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
            item.getTaskId(), item.getId(), item.getOrderNo(), item.getMandantId(), offlineConfirmation.getId()));
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
