package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.ActivityStepAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DoubleJsonDeserializer;
import com.abona_erp.driver.app.work.ActivityWorkManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {
  
  private static final String TAG = DetailFragment.class.getCanonicalName();
  
  private Gson mGson;
  private Data mData;
  private int mOid;
  
  private List<ActivityStep> mActivityList;
  private RecyclerView mListView;
  
  private AsapTextView mTvCustomerName;
  private AsapTextView mTvKDNo;
  private AsapTextView mTvOrderNo;
  private AsapTextView mTvReference1;
  private AsapTextView mTvReference2;
  private AsapTextView mTvDescription;
  
  private AppCompatButton mBtnBackActivity;
  private AppCompatButton mBtnNextActivity;
  private AppCompatImageButton mBtnBack;
  
  private Notify mNotify;
  private ActivityStepAdapter mAdapter;
  private DetailFragmentViewModel mViewModel;
  
  private WorkManager mWorkManager;
  private SimpleDateFormat mSdf;
  
  public DetailFragment() {
    // Required empty public constructor.
  }
  
  public static DetailFragment newInstance() {
    return new DetailFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
    JsonDeserializer deserializer = new DoubleJsonDeserializer();
    mGson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .registerTypeAdapter(double.class, deserializer)
      .registerTypeAdapter(Double.class, deserializer)
      .create();
  
    mNotify = new Notify();
    mActivityList = new ArrayList<>();
    
    mViewModel = ViewModelProviders.of(this)
      .get(DetailFragmentViewModel.class);
    
    mWorkManager = WorkManager.getInstance(getContext());
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_detail_layout, container, false);
    initComponents(root);

    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    if (getArguments() == null)
      return;
  
    mOid = getArguments().getInt("oid");
    if (mOid < 0)
      return;
    
    mViewModel.getNotifyById(mOid).observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(new DisposableSingleObserver<Notify>() {
        @Override
        public void onSuccess(Notify notify) {
          if (notify != null) {
            mNotify = notify;
          }
          if (mData != null)
            mData = null;
          mData = new Data();
          mData = mGson.fromJson(notify.getData(), Data.class);
  
          applyButtonState();
          mAdapter = new ActivityStepAdapter(getContext());
  
          mActivityList.clear();
          if (mData.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mData.getTaskItem().getActivities().size(); i++) {
              mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(i)));
            }
          }
  
          mAdapter.setActivityStepItems(mActivityList);
          mListView.setAdapter(mAdapter);
  
          if (mData.getTaskItem().getKundenName() != null) {
            mTvCustomerName.setText(mData.getTaskItem().getKundenName());
          }
          mTvKDNo.setText(String.valueOf(mData.getTaskItem().getKundenNr()));
          mTvOrderNo.setText(AppUtils.parseOrderNo(mData.getTaskItem().getOrderNo()));
          if (mData.getTaskItem().getReferenceIdCustomer1() != null) {
            mTvReference1.setText(mData.getTaskItem().getReferenceIdCustomer1());
          }
          if (mData.getTaskItem().getReferenceIdCustomer2() != null) {
            mTvReference2.setText(mData.getTaskItem().getReferenceIdCustomer2());
          }
          if (mData.getTaskItem().getDescription() != null) {
            mTvDescription.setText(mData.getTaskItem().getDescription());
          }
        }
  
        @Override
        public void onError(Throwable e) {
          Toast.makeText(getContext().getApplicationContext(),
            "Fehler beim Laden!", Toast.LENGTH_LONG).show();
        }
      });
  }
  
  private void applyButtonState() {
    if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("START ACTIVITY");
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
      mBtnBackActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
    
      int activityCount = mData.getTaskItem().getActivities().size();
      if (mData.getTaskItem().getActivities().get(activityCount-1).getStatus().equals(ActivityStatus.FINISHED)) {
        mBtnNextActivity.setText("FINISHED");
      } else {
        mBtnNextActivity.setText("NEXT");
      }
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("CMR FINISHED");
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("DELETE");
    }
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  @Override
  public void onPause() {
    super.onPause();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_detail_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new BackEvent());
      }
    });
    
    mBtnBackActivity = (AppCompatButton)root.findViewById(R.id.btn_detail_back_activity);
    mBtnBackActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
          if (mData.getTaskItem().getActivities().size() > 0) {
            for (int i = mData.getTaskItem().getActivities().size()-1; i >= 0; i--) {
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                continue;
              }
  
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.PENDING);
                mNotify.setData(mGson.toJson(mData));
                mViewModel.update(mNotify);
                requestActivityChange(
                  1,
                  mData.getTaskItem().getActivities().get(i).getMandantId(),
                  mData.getTaskItem().getActivities().get(i).getTaskId(),
                  mData.getTaskItem().getActivities().get(i).getActivityId(),
                  mData.getTaskItem().getActivities().get(i).getName(),
                  mData.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getFinished()),
                  mData.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mData.getTaskItem().getActivities().get(i).getSequence()
                );
    
                mActivityList.clear();
                if (mData.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList);
                break;
              }
  
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                mNotify.setData(mGson.toJson(mData));
                mViewModel.update(mNotify);
                requestActivityChange(
                  1,
                  mData.getTaskItem().getActivities().get(i).getMandantId(),
                  mData.getTaskItem().getActivities().get(i).getTaskId(),
                  mData.getTaskItem().getActivities().get(i).getActivityId(),
                  mData.getTaskItem().getActivities().get(i).getName(),
                  mData.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getFinished()),
                  mData.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mData.getTaskItem().getActivities().get(i).getSequence()
                );
    
                mActivityList.clear();
                if (mData.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList);
                if (i == mData.getTaskItem().getActivities().size()-1) {
                  mBtnNextActivity.setText("NEXT");
                }
                break;
              }
            }
          }
        }
      }
    });
    
    mBtnNextActivity = (AppCompatButton)root.findViewById(R.id.btn_detail_next_activity);
    mBtnNextActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        applyButtonState();
        
        if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
          mData.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
          mNotify.setStatus(50);
          mNotify.setData(mGson.toJson(mData));
          mViewModel.update(mNotify);
          applyButtonState();
        } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
          if (mData.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mData.getTaskItem().getActivities().size(); i++) {
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                if (i == mData.getTaskItem().getActivities().size()-1) {
                  mNotify.setStatus(90);
                  mData.getTaskItem().setTaskStatus(TaskStatus.CMR);
                  mNotify.setData(mGson.toJson(mData));
                  mViewModel.update(mNotify);
                  applyButtonState();
                }
                continue;
              }
              
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
                mData.getTaskItem().getActivities().get(i).setFinished(new Date());
                mNotify.setData(mGson.toJson(mData));
                mViewModel.update(mNotify);
                requestActivityChange(
                  0,
                  mData.getTaskItem().getActivities().get(i).getMandantId(),
                  mData.getTaskItem().getActivities().get(i).getTaskId(),
                  mData.getTaskItem().getActivities().get(i).getActivityId(),
                  mData.getTaskItem().getActivities().get(i).getName(),
                  mData.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getFinished()),
                  mData.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mData.getTaskItem().getActivities().get(i).getSequence()
                );
  
                mActivityList.clear();
                if (mData.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList);
  
                if (i == mData.getTaskItem().getActivities().size()-1) {
                  mBtnNextActivity.setText("FINISHED");
                }
                break;
              }
              
              if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                mData.getTaskItem().getActivities().get(i).setStarted(new Date());
                mNotify.setData(mGson.toJson(mData));
                mViewModel.update(mNotify);
                requestActivityChange(
                  0,
                  mData.getTaskItem().getActivities().get(i).getMandantId(),
                  mData.getTaskItem().getActivities().get(i).getTaskId(),
                  mData.getTaskItem().getActivities().get(i).getActivityId(),
                  mData.getTaskItem().getActivities().get(i).getName(),
                  mData.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mData.getTaskItem().getActivities().get(i).getFinished()),
                  mData.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mData.getTaskItem().getActivities().get(i).getSequence()
                );
                
                mActivityList.clear();
                if (mData.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList);
                break;
              }
            }
          }
        } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
          mNotify.setStatus(100);
          mData.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(mGson.toJson(mData));
          mViewModel.update(mNotify);
          App.eventBus.post(new BackEvent());
        } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
          mViewModel.delete(mNotify);
          App.eventBus.post(new BackEvent());
        }
      }
    });
    
    mListView = (RecyclerView)root.findViewById(R.id.lv_activity_step);
    LinearLayoutManager llm = new LinearLayoutManager(getContext(),
      RecyclerView.VERTICAL, false);
    mListView.setLayoutManager(llm);
    
    mTvCustomerName = (AsapTextView)root.findViewById(R.id.tv_activity_step_customer_name);
    mTvKDNo = (AsapTextView)root.findViewById(R.id.tv_activity_step_customer_no);
    mTvOrderNo = (AsapTextView)root.findViewById(R.id.tv_activity_step_order_no);
    mTvReference1 = (AsapTextView)root.findViewById(R.id.tv_activity_step_reference_1);
    mTvReference2 = (AsapTextView)root.findViewById(R.id.tv_activity_step_reference_2);
    mTvDescription = (AsapTextView)root.findViewById(R.id.tv_activity_step_desc);
  }
  
  private androidx.work.Data createInputData(
    int header_type,
    int mandantId,
    int taskId,
    int activityId,
    String name,
    String description,
    String started,
    String finished,
    int status,
    int sequence
  ) {
    androidx.work.Data data = new androidx.work.Data.Builder()
      .putInt("header_type", header_type)
      .putInt("mandant_id", mandantId)
      .putInt("task_id", taskId)
      .putInt("activity_id", activityId)
      .putString("name", name)
      .putString("description", description)
      .putString("started", started)
      .putString("finished", finished)
      .putInt("status", status)
      .putInt("sequence", sequence)
      .build();
    return data;
  }
  
  private void requestActivityChange(
    int header_type,
    int mandantId,
    int taskId,
    int activityId,
    String name,
    String description,
    String started,
    String finished,
    int status,
    int sequence
  ) {
    Constraints mConstraints = new Constraints.Builder()
      .setRequiresCharging(false)
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build();
  
    OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
      .Builder(ActivityWorkManager.class)
      .setConstraints(mConstraints)
      .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MICROSECONDS)
      .setInitialDelay(10, TimeUnit.SECONDS)
      .setInputData(createInputData(header_type, mandantId, taskId, activityId, name, description, started, finished, status, sequence))
      .addTag(UUID.randomUUID().toString())
      .build();
    mWorkManager.enqueue(oneTimeWorkRequest);
  }
}
