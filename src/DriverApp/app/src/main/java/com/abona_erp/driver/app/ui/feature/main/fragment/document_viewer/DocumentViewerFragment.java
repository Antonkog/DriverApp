package com.abona_erp.driver.app.ui.feature.main.fragment.document_viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragmentViewModel;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.DocumentViewAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DocumentViewerFragment extends Fragment {
  
  private int               mOid;
  private Notify            mNotify;
  
  private RecyclerView mListView;
  
  private DocumentViewAdapter mDocumentAdapter = new DocumentViewAdapter();
  
  private AppCompatImageButton mBtnBack;
  private AsapTextView         mTvTaskId;
  private AsapTextView         mTvOrderNo;
  private AppCompatImageButton mBtnRefresh;
  
  private MainFragmentViewModel mViewModel;
  
  public DocumentViewerFragment() {
    // Required empty public constructor.
  }
  
  public static DocumentViewerFragment newInstance() {
    return new DocumentViewerFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mNotify = new Notify();
    
    mViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_document_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    if (getArguments() != null) {
      mOid = getArguments().getInt("oid");
      if (mOid > 0) {
        mViewModel.getNotifyById(mOid).observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(Notify notify) {
              if (notify == null) return;
              mNotify = notify;
              
              if (mNotify.getDocumentUrls().size() > 0) {
                mDocumentAdapter.setDocumentItems(mNotify.getDocumentUrls(),
                  mNotify.getMandantId(), mNotify.getOrderNo(), mNotify.getTaskId());
              }
  
              mTvTaskId.setText(String.valueOf(mNotify.getTaskId()));
              mTvOrderNo.setText(AppUtils.parseOrderNo(mNotify.getOrderNo()));
            }
  
            @Override
            public void onError(Throwable e) {
              Toast.makeText(getContext().getApplicationContext(),
                "Fehler beim Laden!", Toast.LENGTH_LONG).show();
            }
          });
      }
    }
  }
  
  @Override
  public void onStart() {
    super.onStart();
  }
  
  @Override
  public void onStop() {
    super.onStop();
  }
  
  private void initComponents(@NonNull View root) {
    
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
    
    mBtnRefresh = (AppCompatImageButton)root.findViewById(R.id.btn_refresh);
    mBtnRefresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new DocumentEvent(mNotify.getMandantId(), mNotify.getOrderNo()));
      }
    });
    
    mTvTaskId = (AsapTextView)root.findViewById(R.id.tv_task_id);
    mTvOrderNo = (AsapTextView)root.findViewById(R.id.tv_order_no);
    
    mListView = (RecyclerView)root.findViewById(R.id.rv_list);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    mListView.setLayoutManager(linearLayoutManager);
    mListView.setAdapter(mDocumentAdapter);
  }
}
