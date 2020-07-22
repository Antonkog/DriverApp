package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.DueInCounterRunnable;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.adapter.ActivityStepAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;

import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {
  
  private static final String TAG = MiscUtil.getTag(DetailFragment.class);
  
  private Handler _handler = new Handler();
  private DueInCounterRunnable dueInCounter;
  
  private int                   mOid;
  private CommItem              mCommItem;
  private Notify                mNotify;
  private MainFragmentViewModel mViewModel;
  
  private RecyclerView mListView;
  private ActivityStepAdapter mAdapter;
  private List<ActivityStep> mActivityList = new ArrayList<>();
  
  private AppCompatButton mBtnBackActivity;
  private AppCompatButton mBtnNextActivity;
  private AppCompatImageButton mBtnBack;
  
  private AppCompatImageView iv_warning_icon;
  private AsapTextView       tv_task_finish;
  private AsapTextView       tv_due_in;
  
  SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
    Locale.getDefault());
  
  public DetailFragment() {
    // Required empty public constructor.
  }
  
  public static DetailFragment newInstance() {
    return new DetailFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_detail_layout_2, container, false);
    initComponents(root);
    dueInCounter = new DueInCounterRunnable(_handler, getContext(), tv_due_in, iv_warning_icon, null, new Date());
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
    if (getArguments() == null) return;
    mOid = getArguments().getInt("oid");
    if (mOid < 0) return;
    
    mViewModel.getNotifyById(mOid).observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(new DisposableSingleObserver<Notify>() {
        @Override
        public void onSuccess(Notify notify) {
          if (notify == null) return;
          mNotify = notify;
          
          if (mCommItem != null)
            mCommItem = null;
          mCommItem = new CommItem();
          mCommItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
          
          fetchButtonState();
          mAdapter = new ActivityStepAdapter(getContext(), notify);
          
          mActivityList.clear();
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(i)));
            }
          }
  
          mAdapter.setActivityStepItems(mActivityList, mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
          mListView.setAdapter(mAdapter);
          
          if (tv_task_finish != null) {
            if (mCommItem.getTaskItem().getTaskDueDateFinish() != null) {
              tv_task_finish.setText(sdf.format(mCommItem.getTaskItem().getTaskDueDateFinish()));
              if (mCommItem.getTaskItem().getTaskStatus() != null) {
                if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING) || mCommItem.getTaskItem().equals(TaskStatus.RUNNING)) {
                  enableDueInTimer(true, tv_due_in, iv_warning_icon, mCommItem.getTaskItem().getTaskDueDateFinish());
                } else {
                  enableDueInTimer(false, tv_due_in, iv_warning_icon, null);
                }
              }
            }
          }
        }
  
        @Override
        public void onError(Throwable e) {
          Toast.makeText(ContextUtils.getApplicationContext(),
            "Fehler beim Laden!", Toast.LENGTH_LONG).show();
        }
      });
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
  
  private void fetchButtonState() {
    if (mCommItem == null) return;
    if (mCommItem.getTaskItem() == null) return;
    if (mCommItem.getTaskItem().getChangeReason() == null) return;
    if (mCommItem.getTaskItem().getTaskStatus() == null) return;
    if (mCommItem.getTaskItem().getActivities() == null) return;
  
    TaskChangeReason changeReason = mCommItem.getTaskItem().getChangeReason();
    TaskStatus taskStatus = mCommItem.getTaskItem().getTaskStatus();
    List<ActivityItem> activityItems = mCommItem.getTaskItem().getActivities();
    
    int activityCount = activityItems.size();
    
    if (changeReason.equals(TaskChangeReason.DELETED) && taskStatus != TaskStatus.FINISHED) {
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText(getResources().getString(R.string.action_close));
      mBtnBackActivity.setVisibility(View.GONE);
    } else if (taskStatus.equals(TaskStatus.PENDING)) {
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText(getResources().getString(R.string.action_start_activity));
    } else if (taskStatus.equals(TaskStatus.RUNNING)) {
      if (TextSecurePreferences.getActivityBackEnable(getContext())) {
        mBtnBackActivity.setVisibility(View.VISIBLE);
      } else {
        mBtnBackActivity.setVisibility(View.GONE);
      }
      mBtnNextActivity.setVisibility(View.VISIBLE);
      
      if (activityCount > 0) {
        if (activityItems.get(activityCount-1).getStatus().equals(ActivityStatus.FINISHED)) {
          mBtnNextActivity.setText(getResources().getString(R.string.action_finished));
        } else {
          mBtnNextActivity.setText(getResources().getString(R.string.action_next));
        }
      }
    } else if (taskStatus.equals(TaskStatus.CMR)) {
      mBtnBackActivity.setVisibility(View.GONE);
      mBtnNextActivity.setVisibility(View.VISIBLE);
      mBtnNextActivity.setText("CMR FINISHED");
    } else if (taskStatus.equals(TaskStatus.FINISHED)) {
      mBtnBackActivity.setVisibility(View.VISIBLE);
      mBtnBackActivity.setText(getResources().getString(R.string.action_close));
      mBtnNextActivity.setVisibility(View.GONE);
      mBtnNextActivity.setText("DELETE");
    }
  }
  
  private void enableDueInTimer(boolean enable, AsapTextView dueIn, AppCompatImageView ivWarning, Date finishDate) {
    if (enable) {
      _handler.removeCallbacks(dueInCounter);
      dueInCounter.tv_DueIn = dueIn;
      dueInCounter.iv_Warning = ivWarning;
      dueInCounter.ll_Background = null;
      dueInCounter.endDate = finishDate;
      _handler.postDelayed(dueInCounter, 100);
    } else {
      _handler.removeCallbacks(dueInCounter);
      
      if (finishDate == null) return;
      if (mCommItem == null || mCommItem.getTaskItem() == null || mCommItem.getTaskItem().getActivities() == null) return;
      if (mCommItem.getTaskItem().getActivities().size() <= 0) return;
      int lastIdx = mCommItem.getTaskItem().getActivities().size() -1;
      if (mCommItem.getTaskItem().getActivities().get(lastIdx).getFinished() == null) return;
      Calendar endTaskCalendar = Calendar.getInstance();
      endTaskCalendar.setTime(mCommItem.getTaskItem().getActivities().get(lastIdx).getFinished());
  
      Calendar finishCalendar = Calendar.getInstance();
      finishCalendar.setTime(finishDate);
  
      long diff = (finishCalendar.getTimeInMillis() - endTaskCalendar.getTimeInMillis()) / 1000 / 60;
      long hours = diff / 60;
  
      long days = hours / 24;
      String d = diff < 0 ? "-" : "";
      d += String.valueOf(Math.abs(days));
      dueInCounter.tv_DueIn.setText(d + "d " + String.format("%02d", Math.abs(hours % 24)) + "h " + String.format("%02d", Math.abs(diff % 60)) + "min");
  
      if (diff < 0) {
        dueInCounter.iv_Warning.setVisibility(View.VISIBLE);
        //dueInCounter.ll_Background.setBackground(context.getResources().getDrawable(R.drawable.warning_header_bg));
      } else {
        dueInCounter.iv_Warning.setVisibility(View.GONE);
        //dueInCounter.ll_Background.setBackground(context.getResources().getDrawable(R.drawable.header_bg));
      }
    }
  }
  
  private void initComponents(@NonNull View root) {
    
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_detail_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
  
    mBtnBackActivity = (AppCompatButton)root.findViewById(R.id.btn_detail_back_activity);
    mBtnNextActivity = (AppCompatButton)root.findViewById(R.id.btn_detail_next_activity);
    mBtnNextActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        if (disableButtonTwoSecs()) return;
        
        
        applyButtonState();
        
        if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) && mCommItem.getTaskItem().getTaskStatus() != TaskStatus.FINISHED) {
          mNotify.setStatus(TaskStatus.FINISHED);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
          mViewModel.update(mNotify);
          
          mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<LastActivity>() {
              @Override
              public void onSuccess(LastActivity lastActivity) {
                lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                lastActivity.setStatusType(LastActivity.DELETED);
  
                ArrayList<String> _list = lastActivity.getDetailList();
  
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                  Locale.getDefault());
                LastActivityDetails _detail = new LastActivityDetails();
                _detail.setDescription("DELETED");
                _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                _list.add(App.getInstance().gson.toJson(_detail));
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
  
          App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
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
                  mBtnNextActivity.setText(getContext().getResources().getString(R.string.action_finished));
                }
                continue;
              }
            }
            
            if (!fFinished) {
              // -----------------------------------------------------------------------------------
              // Check is Previous Task available?
              if (mCommItem.getTaskItem().getPreviousTaskId() != null && mCommItem.getTaskItem().getPreviousTaskId() > 0) {
                mViewModel.getNotifyByTaskId(mCommItem.getTaskItem().getPreviousTaskId()).observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
                      CommItem item = new CommItem();
                      item  = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                      if (item != null) {
                        if (!item.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              MessageDialog.build((AppCompatActivity)getContext())
                                .setStyle(DialogSettings.STYLE.STYLE_IOS)
                                .setTheme(DialogSettings.THEME.LIGHT)
                                .setTitle(getContext().getResources().getString(R.string.action_warning_notice))
                                .setMessage(getContext().getResources().getString(R.string.action_previous_task_message))
                                .setOkButton(getContext().getResources().getString(R.string.action_ok),
                                  new OnDialogButtonClickListener() {
                                    @Override
                                    public boolean onClick(BaseDialog baseDialog, View v) {
                                      isPreviousTaskFinished = false;
                                      return false;
                                    }
                                  })
                                .show();
                            }
                          });
                        } else {
                          isPreviousTaskFinished = true;
                        }
                      }
                    }
  
                    @Override
                    public void onError(Throwable e) {
                      isPreviousTaskFinished = true;
                    }
                  });
              } else {
                isPreviousTaskFinished = true;
              }
              if (!isPreviousTaskFinished) {
                return;
              }
              // -----------------------------------------------------------------------------------
              MessageDialog.build((AppCompatActivity)getContext())
                .setStyle(DialogSettings.STYLE.STYLE_IOS)
                .setTheme(DialogSettings.THEME.LIGHT)
                .setTitle(getContext().getResources().getString(R.string.action_start_order))
                .setMessage(getContext().getResources().getString(R.string.action_start_task_msg))
                .setOkButton(getContext().getResources().getString(R.string.action_start),
                  new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                      isPreviousTaskFinished = false;
                      mCommItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                      mCommItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                      mCommItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                      mNotify.setStatus(TaskStatus.RUNNING);
                      mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
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
  
                      setOfflineWork(mNotify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                      return false;
                    }
                  })
                .setCancelButton(getContext().getResources().getString(R.string.action_cancel),
                  new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                      return false;
                    }
                  })
                .show();
            } else {
              mNotify.setStatus(TaskStatus.FINISHED);
              mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
              mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
              mViewModel.update(mNotify);
              applyButtonState();
  
              mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<LastActivity>() {
                  @Override
                  public void onSuccess(LastActivity lastActivity) {
                    
                    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                    lastActivity.setStatusType(LastActivity.FINISHED_TASK);
        
                    ArrayList<String> _list = lastActivity.getDetailList();
  
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                      Locale.getDefault());
                    LastActivityDetails _detail = new LastActivityDetails();
                    _detail.setDescription("FINISHED TASK");
                    _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                    _list.add(App.getInstance().gson.toJson(_detail));
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
          }
        
          if (mCommItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
              if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                if (i == mCommItem.getTaskItem().getActivities().size()-1) {
                  mNotify.setStatus(TaskStatus.FINISHED);
                  mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
                  mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
                  mViewModel.update(mNotify);
                  applyButtonState();
                  
                  mViewModel.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                      @Override
                      public void onSuccess(LastActivity lastActivity) {
                        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                        lastActivity.setStatusType(LastActivity.ERLEDIGT);
  
                        ArrayList<String> _list = lastActivity.getDetailList();
  
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                          Locale.getDefault());
                        LastActivityDetails _detail = new LastActivityDetails();
                        _detail.setDescription("ERLEDIGT");
                        _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                        _list.add(App.getInstance().gson.toJson(_detail));
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
                mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
                mViewModel.update(mNotify);
  
                setOfflineWork(mNotify.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                
                if (i < mCommItem.getTaskItem().getActivities().size()-1) {
                  mCommItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                  mCommItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                  mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
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
                mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
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
  
          mNotify.setStatus(TaskStatus.FINISHED);
          mCommItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mNotify.setData(App.getInstance().gsonUtc.toJson(mCommItem));
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
  
          App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
        }
  
        applyButtonState();
      }
    });  
    mListView = (RecyclerView)root.findViewById(R.id.lv_activity_step);
    LinearLayoutManager llm = new LinearLayoutManager(getContext(),
      RecyclerView.VERTICAL, false);
    mListView.setLayoutManager(llm);
  
    iv_warning_icon = (AppCompatImageView)root.findViewById(R.id.iv_warning_icon);
    tv_task_finish = (AsapTextView)root.findViewById(R.id.tv_task_finish);
    tv_due_in = (AsapTextView)root.findViewById(R.id.tv_due_in);
  }
}
