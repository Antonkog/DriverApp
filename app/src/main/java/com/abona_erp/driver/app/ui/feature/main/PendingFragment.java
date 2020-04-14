package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
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
import com.abona_erp.driver.app.ui.feature.main.view_model.PendingViewModel;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PendingFragment extends Fragment {

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

    listView = (RecyclerView) root.findViewById(R.id.lv_pending_notify);

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
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
      }

      @Override
      public void onMapClick(Notify notify) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_MAP), notify));
      }
      
      @Override
      public void onCameraClick(Notify notify) {
        MessageDialog.build((AppCompatActivity)getActivity())
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle(App.getInstance().getApplicationContext().getResources().getString(R.string.order_documents))
          .setMessage(App.getInstance().getApplicationContext().getResources().getString(R.string.order_documents_message))
          .setOkButton(getContext().getResources().getString(R.string.action_ok),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
                return false;
              }
            })
          .show();
      }
      
      @Override
      public void onDocumentClick(Notify notify) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), notify));
      }
    });

    listView.setAdapter(adapter);
    viewModel.getAllPendingNotifications().observe(this, new Observer<List<Notify>>() {
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
        App.eventBus.post(new BadgeCountEvent(1, notifies.size()));
      }
    });

    return root;
  }
}
