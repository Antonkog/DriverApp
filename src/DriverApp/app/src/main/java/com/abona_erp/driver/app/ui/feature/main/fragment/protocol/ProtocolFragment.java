package com.abona_erp.driver.app.ui.feature.main.fragment.protocol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.util.CustomDialogFragment;

import java.util.List;

public class ProtocolFragment extends Fragment  {
  
  private static final String TAG = ProtocolFragment.class.getSimpleName();
  
  private AppCompatImageButton mBtnBack;
  private AppCompatImageButton mBtnClearAllLog;
  private RecyclerView mListView;
  
  private ProtocolViewAdapter mProtocolAdapter = new ProtocolViewAdapter();
  
  private ProtocolViewModel viewModel;
  
  public ProtocolFragment() {
    // Required empty public constructor.
  }
  
  public static ProtocolFragment newInstance() {
    return new ProtocolFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_protocol_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
    
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_protocol_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
    
    mListView = (RecyclerView)root.findViewById(R.id.rv_list);
    mListView.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    llm.setReverseLayout(true);
    mListView.setLayoutManager(llm);
    mListView.setItemAnimator(null);
    mListView.addItemDecoration(new DividerItemDecoration(getContext(), llm.getOrientation()));
    mListView.setAdapter(mProtocolAdapter);
    
    mBtnClearAllLog = (AppCompatImageButton)root.findViewById(R.id.btn_protocol_clear);
    mBtnClearAllLog.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CustomDialogFragment fragment = CustomDialogFragment.newInstance(CustomDialogFragment.DialogType.PROTOCOL);
        fragment.show(getActivity().getSupportFragmentManager(), CustomDialogFragment.DialogType.PROTOCOL.name());
      }
    });
  
    viewModel = ViewModelProviders.of(this)
      .get(ProtocolViewModel.class);
    
    viewModel.getAllLogs()
      .observe(getViewLifecycleOwner(), new Observer<List<LogItem>>() {
        @Override
        public void onChanged(List<LogItem> logItems) {
          mProtocolAdapter.setItems(logItems);
          mListView.scrollToPosition(logItems.size()-1);
          //mListView.smoothScrollToPosition(logItems.size()-1);
        }
      });

              /*
    LogItem logItem = new LogItem();
    logItem.setLevel(LogLevel.DEBUG);
    logItem.setType(LogType.INFO);
    logItem.setTitle("Debug");
    logItem.setMessage("Dies ist ein Test!");
    logItem.setCreatedAt(new Date());
    viewModel.insert(logItem);
    */
    
  }
}
