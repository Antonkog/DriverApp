package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.ActivityStepAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.util.MiscUtil;
import com.developer.kalert.KAlertDialog;
import com.shuhart.stepview.StepView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {
  
  private static final String TAG = MiscUtil.getTag(DetailFragment.class);
  
  private CommItem mCommItem;
  private int mOid;
  
  private List<ActivityStep> mActivityList;
  private RecyclerView mListView;
  
  private AsapTextView mTvCustomerName;
  private AsapTextView mTvKDNo;
  private AsapTextView mTvOrderNo;
  private AsapTextView mTvReference1;
  private AsapTextView mTvReference2;
  private AsapTextView mTvDescription;
  
  private RelativeLayout mRlBgActionType;
  private AsapTextView mTvActionType;
  
  private AppCompatButton mBtnBackActivity;
  private AppCompatButton mBtnNextActivity;
  private AppCompatImageButton mBtnBack;
  
  private Notify mNotify;
  private ActivityStepAdapter mAdapter;
  private MainFragmentViewModel mViewModel;
  
  private SimpleDateFormat mSdf;
  
  private StepView mDetailStepView;
  
  private int mRunningCount;
  private int mCMRCount;
  private int mCompletedCount;
  private int mRowTaskCount;
  
  private DriverDatabase mDB = DriverDatabase.getDatabase();
  private OfflineConfirmationDAO mOfflineWorkDAO = mDB.offlineConfirmationDAO();
  
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

    mNotify = new Notify();
    mActivityList = new ArrayList<>();
    
    mViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
    
    mViewModel.getAllRunningNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        mRunningCount = notifyList.size();
        //App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
      }
    });
    
    mViewModel.getAllCMRNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        mCMRCount = notifyList.size();
        //App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
      }
    });
    /*
    mViewModel.getAllCompletedNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        mCompletedCount = notifyList.size();
        if (mCompletedCount > 0) {
          App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
        } else {
          App.eventBus.post(new TaskStatusEvent(0));
        }
      }
    });
    */
    /*
    mViewModel.getRowCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        mRowTaskCount = integer.intValue();
        App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
      }
    });*/
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
          if (mCommItem != null)
            mCommItem = null;
          mCommItem = new CommItem();
          mCommItem = App.getGsonUtc().fromJson(notify.getData(), CommItem.class);
  
          applyButtonState();
          mAdapter = new ActivityStepAdapter(getContext());
  
          mActivityList.clear();
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(i)));
            }
          }
  
          mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
          mListView.setAdapter(mAdapter);
  
          if (mCommItem.getTaskItem().getKundenName() != null) {
            mTvCustomerName.setText(mCommItem.getTaskItem().getKundenName());
          }
          mTvKDNo.setText(String.valueOf(mCommItem.getTaskItem().getKundenNr()));
          mTvOrderNo.setText(AppUtils.parseOrderNo(mCommItem.getTaskItem().getOrderNo()));
          if (mCommItem.getTaskItem().getReferenceIdCustomer1() != null) {
            mTvReference1.setText(mCommItem.getTaskItem().getReferenceIdCustomer1());
          }
          if (mCommItem.getTaskItem().getReferenceIdCustomer2() != null) {
            mTvReference2.setText(mCommItem.getTaskItem().getReferenceIdCustomer2());
          }
          if (mCommItem.getTaskItem().getDescription() != null) {
            mTvDescription.setText(mCommItem.getTaskItem().getDescription());
          }
  
          if (mCommItem.getTaskItem().getActionType() != null) {
            mRlBgActionType.setVisibility(View.VISIBLE);
            mTvActionType.setVisibility(View.VISIBLE);
    
            if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.PICK_UP)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrPickUp));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_pick_up));
            } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.DROP_OFF)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrDropOff));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_drop_off));
            } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.TRACTOR_SWAP)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrTractorSwap));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_tractor_swap));
            } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.GENERAL)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrGeneral));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_general));
            } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.DELAY)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrDelay));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_delay));
            } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.UNKNOWN)) {
              mRlBgActionType.setBackgroundColor(getContext().getResources().getColor(R.color.clrUnknown));
              mTvActionType.setText(getContext().getResources().getString(R.string.action_type_unknown));
            }
          } else {
            mRlBgActionType.setVisibility(View.GONE);
            mTvActionType.setVisibility(View.GONE);
          }
        }
  
        @Override
        public void onError(Throwable e) {
          Toast.makeText(getContext().getApplicationContext(),
            "Fehler beim Laden!", Toast.LENGTH_LONG).show();
        }
      });
  }
  /*
  private int calculateTaskStatusPercentage() {
    if (mRowTaskCount == 0) {
      return 0;
    }
    else if (mRowTaskCount == mCompletedCount) {
      return 100;
    } else {
      float percentage = ((100.0f / mRowTaskCount) * ((mRunningCount * 0.5f) + (mCMRCount * 0.9f) + mCompletedCount));
      return (int)Math.round(percentage);
    }
  }
  */
  private void applyButtonState() {
    
    if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) && mCommItem.getTaskItem().getTaskStatus() != TaskStatus.FINISHED) {
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("CLOSE");
      mBtnBackActivity.setVisibility(View.GONE);
    } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
      mDetailStepView.go(0, true);
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("START ACTIVITY");
    } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
      mDetailStepView.go(1, true);
      if (TextSecurePreferences.getActivityBackEnable(getContext())) {
        mBtnBackActivity.setVisibility(View.VISIBLE);
      } else {
        mBtnBackActivity.setVisibility(View.GONE);
      }
      mBtnNextActivity.setVisibility(View.VISIBLE);
    
      int activityCount = mCommItem.getTaskItem().getActivities().size();
      if (mCommItem.getTaskItem().getActivities().get(activityCount-1).getStatus().equals(ActivityStatus.FINISHED)) {
        mBtnNextActivity.setText("FINISHED");
      } else {
        mBtnNextActivity.setText("NEXT");
      }
    } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
      mDetailStepView.go(2, true);
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("CMR FINISHED");
    } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
      mDetailStepView.go(3, false);
      mDetailStepView.done(true);
      mBtnBackActivity.setVisibility(View.VISIBLE);
      mBtnBackActivity.setText("ClOSE");
      mBtnNextActivity.setVisibility(View.GONE);
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
    
    mRlBgActionType = (RelativeLayout)root.findViewById(R.id.rl_bg_action_type);
    mTvActionType = (AsapTextView)root.findViewById(R.id.tv_action_type);
  
    mDetailStepView = (StepView)root.findViewById(R.id.detail_step_view);
    List<String> steps = new ArrayList<>();
    steps.add(getContext().getResources().getString(R.string.pending));
    steps.add(getContext().getResources().getString(R.string.running));
    steps.add(getContext().getResources().getString(R.string.cmr));
    steps.add(getContext().getResources().getString(R.string.completed));
    mDetailStepView.setSteps(steps);
    
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
        if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = mCommItem.getTaskItem().getActivities().size()-1; i >= 0; i--) {
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                continue;
              }
  
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.PENDING);
                mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                mViewModel.update(mNotify);
                
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                break;
              }
  
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                mViewModel.update(mNotify);
                
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mBtnNextActivity.setText("NEXT");
                }
                break;
              }
            }
          }
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
          App.eventBus.post(new BackEvent());
        }
      }
    });
    
    mBtnNextActivity = (AppCompatButton)root.findViewById(R.id.btn_detail_next_activity);
    mBtnNextActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        applyButtonState();
        
        if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) && mCommItem.getTaskItem().getTaskStatus() != TaskStatus.FINISHED) {
          mNotify.setStatus(100);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getGsonUtc().toJson(mCommItem));
          mViewModel.update(mNotify);
          
          mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<LastActivity>() {
              @Override
              public void onSuccess(LastActivity lastActivity) {
                lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                lastActivity.setStatusType(9);
  
                ArrayList<String> _list = lastActivity.getDetailList();
  
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                  Locale.getDefault());
                LastActivityDetails _detail = new LastActivityDetails();
                _detail.setDescription("DELETED");
                _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                _list.add(App.getGson().toJson(_detail));
                lastActivity.setDetailList(_list);
                lastActivity.setVisible(false);
  
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    mViewModel.update(lastActivity);
                  }
                });
              }
  
              @Override
              public void onError(Throwable e) {
    
              }
            });
  
          App.eventBus.post(new BackEvent());
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
          
          mActivityList.clear();
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
              mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),
                mCommItem.getTaskItem().getActionType(),
                mCommItem.getTaskItem().getActivities().get(j)));
            }
            mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem()
              .getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
            
            boolean fFinished = false;
            for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
              if (mCommItem.getTaskItem().getActivities().get(j).getStatus().equals(ActivityStatus.FINISHED)) {
                if (j == mCommItem.getTaskItem().getActivities().size()-1) {
                  fFinished = true;
                  mBtnNextActivity.setText("FINISHED");
                }
                continue;
              }
            }
            
            if (!fFinished) {
              new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
                .setTitleText("Start Activity")
                .setContentText("Möchten Sie den Task starten?")
                .setCancelText(getContext().getResources().getString(R.string.action_cancel))
                .setConfirmText("Task Starten")
                .confirmButtonColor(R.drawable.btn_confirmation_ok)
                .showCancelButton(true)
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                  @Override
                  public void onClick(KAlertDialog sDialog) {
                    
                    ////////////////////////////////////////////////////////////////////////////////
                    // Überprüfen, ob der Task gestartet werden darf?
                    //
                    
                    ////////////////////////////////////////////////////////////////////////////////
                    
                    mCommItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                    mCommItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                    mCommItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                    mNotify.setStatus(50);
                    mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                    mViewModel.update(mNotify);
                    applyButtonState();
  
                    mActivityList.clear();
                    if (mCommItem.getTaskItem().getActivities().size() > 0) {
                      for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                        mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),
                          mCommItem.getTaskItem().getActionType(),
                          mCommItem.getTaskItem().getActivities().get(j)));
                      }
                      mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem()
                        .getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                    }
  /*
                    mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new DisposableSingleObserver<LastActivity>() {
                        @Override
                        public void onSuccess(LastActivity lastActivity) {
                          
                          lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                          lastActivity.setStatusType(2);
        
                          ArrayList<String> _list = lastActivity.getDetailList();
  
                          SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                            Locale.getDefault());
                          LastActivityDetails _detail = new LastActivityDetails();
                          _detail.setDescription("CHANGED");
                          _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                          _list.add(App.getGson().toJson(_detail));
                          lastActivity.setDetailList(_list);
        
                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              mViewModel.update(lastActivity);
                            }
                          });
                        }
      
                        @Override
                        public void onError(Throwable e) {
        
                        }
                      });
                    */
                    setOfflineWork(mNotify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                    sDialog.cancel();
              }
            })
            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
              @Override
              public void onClick(KAlertDialog sDialog) {
                sDialog.cancel();
              }
            })
            .show();
            } else {
              mNotify.setStatus(100);
              mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
              mNotify.setData(App.getGsonUtc().toJson(mCommItem));
              mViewModel.update(mNotify);
              applyButtonState();
  
              mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<LastActivity>() {
                  @Override
                  public void onSuccess(LastActivity lastActivity) {
                    
                    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                    lastActivity.setStatusType(3);
        
                    ArrayList<String> _list = lastActivity.getDetailList();
  
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                      Locale.getDefault());
                    LastActivityDetails _detail = new LastActivityDetails();
                    _detail.setDescription("FINISHED TASK");
                    _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                    _list.add(App.getGson().toJson(_detail));
                    lastActivity.setDetailList(_list);
        
                    AsyncTask.execute(new Runnable() {
                      @Override
                      public void run() {
                        mViewModel.update(lastActivity);
                      }
                    });
                  }
      
                  @Override
                  public void onError(Throwable e) {
        
                  }
                });
            }
          }
          
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
          
          mActivityList.clear();
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),
                mCommItem.getTaskItem().getActionType(),
                mCommItem.getTaskItem().getActivities().get(i)));
            }
            mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem()
              .getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
            
            boolean fFinished = false;
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  fFinished = true;
                  mBtnNextActivity.setText("FINISHED");
                }
                continue;
              }
            }
            
            if (!fFinished) {
              /*
              new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
                .setTitleText("Start Activity")
                .setContentText("Möchten Sie den Task starten?")
                .setCancelText("Abbrechen")
                .setConfirmText("Task Starten")
                .confirmButtonColor(R.drawable.btn_confirmation_ok)
                .showCancelButton(true)
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                  @Override
                  public void onClick(KAlertDialog sDialog) {
                    mCommItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                    mCommItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                    mCommItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                    mNotify.setStatus(50);
                    mNotify.setData(App.getGson().toJson(mCommItem));
                    mViewModel.update(mNotify);
                    applyButtonState();
        
                    mActivityList.clear();
                    if (mCommItem.getTaskItem().getActivities().size() > 0) {
                      for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                        mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),
                          mCommItem.getTaskItem().getActivities().get(j)));
                      }
                      mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem()
                        .getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                    }*/
  /*
                    mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new DisposableSingleObserver<LastActivity>() {
                        @Override
                        public void onSuccess(LastActivity lastActivity) {
                          
                          lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                          lastActivity.setStatusType(2);
        
                          ArrayList<String> _list = lastActivity.getDetailList();
  
                          SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                            Locale.getDefault());
                          LastActivityDetails _detail = new LastActivityDetails();
                          _detail.setDescription("CHANGED");
                          _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                          _list.add(App.getGson().toJson(_detail));
                          lastActivity.setDetailList(_list);
        
                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              mViewModel.update(lastActivity);
                            }
                          });
                        }
      
                        @Override
                        public void onError(Throwable e) {
        
                        }
                      });
                    *//*
                    setOfflineWork(mNotify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                    sDialog.cancel();
                  }
                })
                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                  @Override
                  public void onClick(KAlertDialog sDialog) {
                    sDialog.cancel();
                  }
                })
                .show();
            } else {*//*
              mNotify.setStatus(100);
              mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
              mNotify.setData(App.getGson().toJson(mCommItem));
              mViewModel.update(mNotify);
              applyButtonState();
  
              mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<LastActivity>() {
                  @Override
                  public void onSuccess(LastActivity lastActivity) {
        
                    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                    lastActivity.setStatusType(3);
        
                    ArrayList<String> _list = lastActivity.getDetailList();
        
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                      Locale.getDefault());
                    LastActivityDetails _detail = new LastActivityDetails();
                    _detail.setDescription("FINISHED TASK");
                    _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                    _list.add(App.getGson().toJson(_detail));
                    lastActivity.setDetailList(_list);
        
                    AsyncTask.execute(new Runnable() {
                      @Override
                      public void run() {
                        mViewModel.update(lastActivity);
                      }
                    });
                  }
      
                  @Override
                  public void onError(Throwable e) {
        
                  }
                });*/
            }
          }
        
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mNotify.setStatus(90);
                  mCommItem.getTaskItem().setTaskStatus(TaskStatus.CMR);
                  mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                  mViewModel.update(mNotify);
                  applyButtonState();
                  
                  mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                      @Override
                      public void onSuccess(LastActivity lastActivity) {
                        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                        lastActivity.setStatusType(4);
  
                        ArrayList<String> _list = lastActivity.getDetailList();
  
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                          Locale.getDefault());
                        LastActivityDetails _detail = new LastActivityDetails();
                        _detail.setDescription("ERLEDIGT");
                        _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                        _list.add(App.getGson().toJson(_detail));
                        lastActivity.setDetailList(_list);
                        lastActivity.setVisible(false);
  
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            mViewModel.update(lastActivity);
                          }
                        });
                      }
  
                      @Override
                      public void onError(Throwable e) {
    
                      }
                    });
                }
                continue;
              }
              
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
                mCommItem.getTaskItem().getActivities().get(i).setFinished(AppUtils.getCurrentDateTimeUtc());
                mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                mViewModel.update(mNotify);
  
                setOfflineWork(mNotify.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                
                if (i < mCommItem.getTaskItem().getActivities().size()-1) {
                  mCommItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                  mCommItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                  mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                  mViewModel.update(mNotify);
  
                  setOfflineWork(mNotify.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                }
  
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
  
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mBtnNextActivity.setText("FINISHED");
                }
                break;
              }
  
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                mCommItem.getTaskItem().getActivities().get(i).setStarted(AppUtils.getCurrentDateTimeUtc());
                mNotify.setData(App.getGsonUtc().toJson(mCommItem));
                mViewModel.update(mNotify);
                
                if (i != 0)
                  setOfflineWork(mNotify.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
  
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(),mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                break;
              }
            }
          }
        
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
  
          mNotify.setStatus(100);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getGsonUtc().toJson(mCommItem));
          mViewModel.update(mNotify);
          applyButtonState();
          
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
          mViewModel.delete(mNotify);
  
          mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<LastActivity>() {
              @Override
              public void onSuccess(LastActivity lastActivity) {
        
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    mViewModel.delete(lastActivity);
                  }
                });
              }
      
              @Override
              public void onError(Throwable e) {
        
              }
            });
          
          App.eventBus.post(new BackEvent());
        }
        
/*
        if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) && mCommItem.getTaskItem().getTaskStatus() != TaskStatus.FINISHED) {
          mNotify.setStatus(100);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getGson().toJson(mCommItem));
          mViewModel.update(mNotify);
  
          LastActivity lastActivity = new LastActivity();
          lastActivity.setMandantOid(mCommItem.getTaskItem().getMandantId());
          lastActivity.setTaskOid(mCommItem.getTaskItem().getTaskId());
          lastActivity.setOrderNo(mCommItem.getTaskItem().getOrderNo());
          lastActivity.setStatusType(9);
          lastActivity.setCreatedAt(new Date());
          mViewModel.insert(lastActivity);
  
          App.eventBus.post(new BackEvent());
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
  
          new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
            .setTitleText("Start Activity")
            .setContentText("Möchten Sie den Task starten?")
            .setCancelText("Abbrechen")
            .setConfirmText("Task Starten")
            .confirmButtonColor(R.drawable.btn_confirmation_ok)
            .showCancelButton(true)
            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
              @Override
              public void onClick(KAlertDialog sDialog) {
                mCommItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                mCommItem.getTaskItem().getActivities().get(0).setStarted(new Date());
                mCommItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                mNotify.setStatus(50);
                mNotify.setData(App.getGson().toJson(mCommItem));
                mViewModel.update(mNotify);
                applyButtonState();*/
  /*
                DriverDatabase db = DriverDatabase.getDatabase();
                OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
                
                OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
                offlineConfirmation.setNotifyId((int)mNotify.getId());
                offlineConfirmation.setConfirmType(ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    dao.insert(offlineConfirmation);
                  }
                });
  *//*
                LastActivity lastActivity = new LastActivity();
                lastActivity.setMandantOid(mCommItem.getTaskItem().getMandantId());
                lastActivity.setTaskOid(mCommItem.getTaskItem().getTaskId());
                lastActivity.setOrderNo(mCommItem.getTaskItem().getOrderNo());
                lastActivity.setStatusType(1);
                lastActivity.setDescription(getContext().getResources().getString(R.string.pending) + " -> " + getContext().getResources().getString(R.string.running));
                lastActivity.setCreatedAt(new Date());
                mViewModel.insert(lastActivity);
                sDialog.cancel();
  
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
              }
            })
            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
              @Override
              public void onClick(KAlertDialog sDialog) {
                sDialog.cancel();
              }
            })
            .show();
          
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mNotify.setStatus(90);
                  mCommItem.getTaskItem().setTaskStatus(TaskStatus.CMR);
                  mNotify.setData(App.getGson().toJson(mCommItem));
                  mViewModel.update(mNotify);
                  applyButtonState();
  
                  LastActivity lastActivity = new LastActivity();
                  lastActivity.setMandantOid(mCommItem.getTaskItem().getMandantId());
                  lastActivity.setTaskOid(mCommItem.getTaskItem().getTaskId());
                  lastActivity.setOrderNo(mCommItem.getTaskItem().getOrderNo());
                  lastActivity.setStatusType(1);
                  lastActivity.setDescription(getContext().getResources().getString(R.string.running) + " -> " + getContext().getResources().getString(R.string.cmr));
                  lastActivity.setCreatedAt(new Date());
                  mViewModel.insert(lastActivity);
                }
                continue;
              }
              
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
                mCommItem.getTaskItem().getActivities().get(i).setFinished(new Date());
                mNotify.setData(App.getGson().toJson(mCommItem));
                mViewModel.update(mNotify);
                requestActivityChange(
                  0,
                  mCommItem.getTaskItem().getActivities().get(i).getMandantId(),
                  mCommItem.getTaskItem().getActivities().get(i).getTaskId(),
                  mCommItem.getTaskItem().getActivities().get(i).getActivityId(),
                  mCommItem.getTaskItem().getActivities().get(i).getName(),
                  mCommItem.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mCommItem.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mCommItem.getTaskItem().getActivities().get(i).getFinished()),
                  mCommItem.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mCommItem.getTaskItem().getActivities().get(i).getSequence()
                );
  
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
  
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mBtnNextActivity.setText("FINISHED");
                }
                break;
              }
              
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                mCommItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                mCommItem.getTaskItem().getActivities().get(i).setStarted(new Date());
                mNotify.setData(App.getGson().toJson(mCommItem));
                mViewModel.update(mNotify);
                requestActivityChange(
                  0,
                  mCommItem.getTaskItem().getActivities().get(i).getMandantId(),
                  mCommItem.getTaskItem().getActivities().get(i).getTaskId(),
                  mCommItem.getTaskItem().getActivities().get(i).getActivityId(),
                  mCommItem.getTaskItem().getActivities().get(i).getName(),
                  mCommItem.getTaskItem().getActivities().get(i).getDescription(),
                  mSdf.format(mCommItem.getTaskItem().getActivities().get(i).getStarted()),
                  mSdf.format(mCommItem.getTaskItem().getActivities().get(i).getFinished()),
                  mCommItem.getTaskItem().getActivities().get(i).getStatus().ordinal(),
                  mCommItem.getTaskItem().getActivities().get(i).getSequence()
                );
                
                mActivityList.clear();
                if (mCommItem.getTaskItem().getActivities().size() > 0) {
                  for (int j = 0; j < mCommItem.getTaskItem().getActivities().size(); j++) {
                    mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActivities().get(j)));
                  }
                }
                mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
                break;
              }
            }
          }
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
          mNotify.setStatus(100);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getGson().toJson(mCommItem));
          mViewModel.update(mNotify);
          applyButtonState();
  
          LastActivity lastActivity = new LastActivity();
          lastActivity.setMandantOid(mCommItem.getTaskItem().getMandantId());
          lastActivity.setTaskOid(mCommItem.getTaskItem().getTaskId());
          lastActivity.setOrderNo(mCommItem.getTaskItem().getOrderNo());
          lastActivity.setStatusType(1);
          lastActivity.setDescription(getContext().getResources().getString(R.string.cmr) + " -> " + getContext().getResources().getString(R.string.completed));
          lastActivity.setCreatedAt(new Date());
          mViewModel.insert(lastActivity);
        } else if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
          mViewModel.delete(mNotify);
          App.eventBus.post(new BackEvent());
        }*/
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

  private void setOfflineWork(int notifyId, int activityId, int confirmationType) {
    OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
    offlineConfirmation.setNotifyId(notifyId);
    offlineConfirmation.setActivityId(activityId);
    offlineConfirmation.setConfirmType(confirmationType);
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        mOfflineWorkDAO.insert(offlineConfirmation);
      }
    });
  }
}
