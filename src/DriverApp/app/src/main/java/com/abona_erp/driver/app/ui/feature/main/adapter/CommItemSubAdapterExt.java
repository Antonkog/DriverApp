package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.DelayReasonDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.ActionType;
import com.abona_erp.driver.app.data.entity.ChangeHistoryState;
import com.abona_erp.driver.app.data.entity.DelayReasonEntity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DelayReasonItem;
import com.abona_erp.driver.app.data.model.SpecialActivities;
import com.abona_erp.driver.app.data.model.SpecialFunction;
import com.abona_erp.driver.app.data.model.SpecialFunctionOperationType;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProgressBarEvent;
import com.abona_erp.driver.app.ui.event.QREvent;
import com.abona_erp.driver.app.ui.event.TabChangeEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.fragment.specialfunction.SFQRCodeDialog;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.CustomDelayReasonDialog;
import com.abona_erp.driver.app.ui.widget.CustomDelayReasonHistory;
import com.abona_erp.driver.app.ui.widget.DelayReasonHistoryAdapter;
import com.abona_erp.driver.app.ui.widget.qrcode.CustomQRDialog;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CommItemSubAdapterExt
  extends RecyclerView.Adapter<CommItemSubAdapterExt.ViewHolder> {
  
  private static final String TAG = CommItemSubAdapterExt.class.getSimpleName();
  
  private static final String DATE_FORMAT_IN = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT_OUT = "dd.MM.yyyy HH:mm:ss";
  
  private static final String NOT_SET_TIMESTAMP_GLYPH = "--.--.---- --:--:--";
  
  private final Context mContext;
  private final LayoutInflater mInflater;
  
  private Notify mData;
  private List<ActivityStep> mDataList;
  
  private final SimpleDateFormat mSdfIn;
  private final SimpleDateFormat mSdfOut;
  private Date mDateMin;
  
  private boolean isPreviousTaskFinished = false;
  
  public CommItemSubAdapterExt(Context ctx) {
    mContext = ctx;
    mInflater = LayoutInflater.from(mContext);
  
    mSdfIn = new SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault());
    mSdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
    try {
      mDateMin = mSdfIn.parse("1970-01-01 01:00:00");
    } catch (ParseException e) {
      if (e.getMessage() != null) Log.e(TAG, e.getMessage());
    }
  }
  
  private void handleNextButton() {
    
    if (mData == null) return;
    
    CommItem commItem = App.getInstance().gsonUtc.fromJson(mData.getData(), CommItem.class);
    
    TaskStatus taskStatus = commItem.getTaskItem().getTaskStatus();
    
    if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)
      && taskStatus != TaskStatus.FINISHED) {
  
      mData.setStatus(100);
      commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
      mData.setData(App.getInstance().gsonUtc.toJson(commItem));
      updateNotify(mData);
  
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
    } else if (taskStatus.equals(TaskStatus.PENDING)) {
  
      boolean fFinished = false;
      for (int i = 0; i < commItem.getTaskItem().getActivities().size(); i++) {
        if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
          if (i == commItem.getTaskItem().getActivities().size() - 1) {
            fFinished = true;
          }
          continue;
        }
      }
  
      if (!fFinished) {
        // Check is previous task available?
        if (commItem.getTaskItem().getPreviousTaskId() != null && commItem.getTaskItem().getPreviousTaskId() > 0) {
      
          NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
          dao.loadNotifyByTaskId(commItem.getTaskItem().getPreviousTaskId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
            
                CommItem prevItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                if (prevItem == null) return;
                if (!prevItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                      alertDialog.setTitle(mContext.getResources().getString(R.string.action_warning_notice))
                        .setMessage(mContext.getResources().getString(R.string.action_previous_task_message))
                              .setPositiveButton(mContext.getResources().getString(R.string.action_ok),
                                      (dialog, which) -> {
                                        isPreviousTaskFinished = false;
                                        dialog.dismiss();
                                      }).show();
                } else {
                  isPreviousTaskFinished = true;
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
        if (!isPreviousTaskFinished) return;
    
        if (commItem.getTaskItem().getActivities().get(0).getStatus().equals(ActivityStatus.RUNNING)) {
          synchronized (this) {
            commItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
            commItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
            commItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
            mData.setStatus(50);
            mData.setData(App.getInstance().gsonUtc.toJson(commItem));
            updateNotify(mData);
  
            App.eventBus.post(new TabChangeEvent());
  
            addOfflineWork(mData.getId(),  0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1); //press start activity
            addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(0), commItem);
          }
        } else {
          boolean startActivity = true;
          // Check SpecialFunction.
          if (commItem != null && commItem.getTaskItem() != null && commItem.getTaskItem().getActivities() != null) {
            int activities = commItem.getTaskItem().getActivities().size();
            if (activities > 0) {
              ActivityItem activity = commItem.getTaskItem().getActivities().get(0);
              if (activity.getStatus().equals(ActivityStatus.PENDING)) {
                if (activity.getSpecialActivities() != null) {
                  int sfCount = activity.getSpecialActivities().size();
                  if (sfCount > 0) {
                    for (int i = 0; i < sfCount; i++) {
    
                      SpecialActivities sa = activity.getSpecialActivities().get(i);
                      if (sa.getSpecialFunction() == SpecialFunction.STANDARD)
                        continue;
                      if (sa.getSpecialActivityResults() == null && sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_START_OF_ACTIVITY)) {
                        if (sa.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
                          App.eventBus.post(new QREvent(mData.getId(), 0, 0));
                        } else if (sa.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                          App.eventBus.post(new QREvent(mData.getId(), 0, 1));
                        } else if (sa.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                          App.eventBus.post(new QREvent(mData.getId(), 0, 1));
                        }
                        startActivity = false;
                        //App.eventBus.post(new QREvent(mData.getId(), i));
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
          
          if (startActivity) {
            // Start Activity?
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle(mContext.getResources().getString(R.string.action_start_order))
              .setMessage(mContext.getResources().getString(R.string.action_start_task_msg))
              .setPositiveButton(mContext.getResources().getString(R.string.action_start), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
        
                  isPreviousTaskFinished = false;
                  synchronized (this) {
                    commItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                    commItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                    commItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                    mData.setStatus(50);
                    mData.setData(App.getInstance().gsonUtc.toJson(commItem));
                    updateNotify(mData);
        
                    App.eventBus.post(new TabChangeEvent());
        
                    addOfflineWork(mData.getId(),  0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1); //press start activity
                    addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(0), commItem);
                  }
                  
                  dialog.dismiss();
                }
              })
              .setNegativeButton(mContext.getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }).show();
          }
        }
        
      } else {
  
        synchronized (this) {
          mData.setStatus(100);
          commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
          mData.setData(App.getInstance().gsonUtc.toJson(commItem));
          updateNotify(mData);
        }
      }
    
    } else if (taskStatus.equals(TaskStatus.RUNNING)) {
      
      int sizeOfActivities = commItem.getTaskItem().getActivities().size();
  
      if (sizeOfActivities > 0) {
        for (int i = 0; i < sizeOfActivities; i++) {
  
          ActivityItem activity = commItem.getTaskItem().getActivities().get(i);
          ActivityStatus activityStatus = activity.getStatus();
          
          if (activityStatus.equals(ActivityStatus.FINISHED)) {
  
            boolean checkValidSF = true;
            if (commItem.getTaskItem().getActivities().get(i).getSpecialActivities() != null) {
              SpecialActivities specialActivities = commItem.getTaskItem().getActivities().get(i).getSpecialActivities().get(0);
              if (specialActivities.getSpecialActivityResults() == null) {
                /*if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING) && specialActivities.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_START_OF_ACTIVITY)) {*/
                  if (specialActivities.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 0));
                    checkValidSF = false;
                  } else if (specialActivities.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                    checkValidSF = false;
                  } else if (specialActivities.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                    checkValidSF = false;
                  }
                /*} else if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED) && specialActivities.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
                  if (specialActivities.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 0));
                    checkValidSF = false;
                  } else if (specialActivities.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                    checkValidSF = false;
                  } else if (specialActivities.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                    checkValidSF = false;
                  }
                }*/
              }
            }
            if (!checkValidSF)
              break;
            
            if (i == commItem.getTaskItem().getActivities().size() - 1) {
  
              synchronized (this) {
                mData.setStatus(100);
                commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
                mData.setData(App.getInstance().gsonUtc.toJson(commItem));
              
                addOfflineWork(mData.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 2);//that is when last next pressed
                addHistoryLog(ActionType.FINISH_ACTIVITY, commItem.getTaskItem().getActivities().get(i), commItem);
              }
              
              // Check next previous task and start it.
              if (commItem.getTaskItem().getNextTaskId() != null && commItem.getTaskItem().getNextTaskId() > 0) {
                
                NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
                dao.loadNotifyByTaskId(commItem.getTaskItem().getNextTaskId())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
                      
                      CommItem nextItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
  
                      synchronized (this) {
                        notify.setStatus(50);
                        nextItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                        nextItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                        nextItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                        notify.setData(App.getInstance().gsonUtc.toJson(nextItem));
                        updateNotify(notify);
                        addOfflineWork(notify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                      }
                    }
  
                    @Override
                    public void onError(Throwable e) {
                      // ignore.
                    }
                  });
              }
              updateNotify(mData);
            }
            continue;
          } else if (activityStatus.equals(ActivityStatus.RUNNING)) {
  
            boolean checkValidSF = true;
            if (activity.getSpecialActivities() != null) {
              SpecialActivities specialActivity = activity.getSpecialActivities().get(0);
              if (specialActivity.getSpecialActivityResults() == null) {
                if (specialActivity.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
                  checkValidSF = false;
                  if (specialActivity.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 0));
                  } else if (specialActivity.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                  } else if (specialActivity.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                  }
                }
              }
            }
            if (!checkValidSF)
              break;
  
            synchronized (this) {
              commItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
              commItem.getTaskItem().getActivities().get(i).setFinished(AppUtils.getCurrentDateTimeUtc());
              mData.setData(App.getInstance().gsonUtc.toJson(commItem));
              updateNotify(mData);
        
              addOfflineWork(mData.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 2);//press next button even if it's last item
              addHistoryLog(ActionType.FINISH_ACTIVITY, commItem.getTaskItem().getActivities().get(i), commItem);
            }
            
            if (i < commItem.getTaskItem().getActivities().size() - 1) {
              if (commItem.getTaskItem().getActivities().get(i+1).getSpecialActivities() != null) {
                int sfCount = commItem.getTaskItem().getActivities().get(i+1).getSpecialActivities().size();
                if (sfCount > 0) {
                  if (commItem.getTaskItem().getActivities().get(i+1).getSpecialActivities().get(0).getSpecialFunction().equals(SpecialFunction.STANDARD)) {
                    commItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                    commItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                    mData.setData(App.getInstance().gsonUtc.toJson(commItem));
                    updateNotify(mData);
  
                    addOfflineWork(mData.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                    addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(i + 1), commItem); //that comes after  1st next button pressed
                  } else if (commItem.getTaskItem().getActivities().get(i+1).getSpecialActivities().get(0).getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
                    commItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                    commItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                    mData.setData(App.getInstance().gsonUtc.toJson(commItem));
                    updateNotify(mData);
  
                    addOfflineWork(mData.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                    addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(i + 1), commItem); //that comes after  1st next button pressed
                  } else {
                  
                  }
                } else {
                  commItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                  commItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                  mData.setData(App.getInstance().gsonUtc.toJson(commItem));
                  updateNotify(mData);
  
                  addOfflineWork(mData.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                  addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(i + 1), commItem); //that comes after  1st next button pressed
                }
              } else {
                synchronized (this) {
                  commItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
                  commItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
                  mData.setData(App.getInstance().gsonUtc.toJson(commItem));
                  updateNotify(mData);
  
                  addOfflineWork(mData.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                  addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(i + 1), commItem); //that comes after  1st next button pressed
                }
              }
            } else if (i == commItem.getTaskItem().getActivities().size() - 1) {
              
              // Special Function.
              
              
              if (commItem.getTaskItem().getNextTaskId() != null && commItem.getTaskItem().getNextTaskId() > 0) {
                NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
                dao.loadNotifyByTaskId(commItem.getTaskItem().getNextTaskId())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
        
                      // I found next task and start it.
                      synchronized (this) {
                        CommItem nextItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                        nextItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                        nextItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                        nextItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                        notify.setStatus(50);
                        notify.setData(App.getInstance().gsonUtc.toJson(nextItem));
                        updateNotify(notify);
        
                        addOfflineWork(notify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
                      }
                    }
      
                    @Override
                    public void onError(Throwable e) {
                      // IGNORE.
                    }
                  });
              }
            }
            break;
          } else if (activityStatus.equals(ActivityStatus.PENDING)) {
            
            boolean checkValidSF = true;
            if (activity.getSpecialActivities() != null) {
              SpecialActivities specialActivity = activity.getSpecialActivities().get(0);
              if (specialActivity.getSpecialActivityResults() == null) {
                if (specialActivity.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_START_OF_ACTIVITY)) {
                  checkValidSF = false;
                  if (specialActivity.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 0));
                  } else if (specialActivity.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                  } else if (specialActivity.getSpecialFunction().equals(SpecialFunction.TAKE_IMAGES_CMR)) {
                    App.eventBus.post(new QREvent(mData.getId(), i, 1));
                  }
                }
              }
            }
            if (!checkValidSF)
              break;
        
            commItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
            commItem.getTaskItem().getActivities().get(i).setStarted(AppUtils.getCurrentDateTimeUtc());
            mData.setData(App.getInstance().gsonUtc.toJson(commItem));
            updateNotify(mData);
        
            if (i != 0){
              addOfflineWork(mData.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal(), 1);
              addHistoryLog(ActionType.START_ACTIVITY, commItem.getTaskItem().getActivities().get(i), commItem);
            }
            break;
          }
        }
      }
    
    } else if (taskStatus.equals(TaskStatus.CMR)) {
      mData.setStatus(100);
      commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
      mData.setData(App.getInstance().gsonUtc.toJson(commItem));
      updateNotify(mData);
    }
  }
  
  @Override
  public void onBindViewHolder(CommItemSubAdapterExt.ViewHolder holder, int position) {
    if (position < 0) return;
    
    holder.setIsRecyclable(false);
    ActivityStep item = mDataList.get(position);
    
    if (position == 0 && mDataList.size() == 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    } else if (position == 0 && mDataList.size() > 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
    } else {
      holder.step_view_top.setVisibility(View.VISIBLE);
      holder.step_view_bottom.setVisibility(View.VISIBLE);
    }
    if (position == mDataList.size() - 1) {
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    }
    holder.tv_activity_step_no.setVisibility(View.GONE);
    holder.iv_activity_step_check_mark.setVisibility(View.GONE);
    
    if (item.getActivityItem().getName() != null) {
      holder.tv_activity_step_name.setText(item.getActivityItem().getName());
    }
    
    if (item.getActivityItem().getStarted() != null) {
      if (item.getActivityItem().getStarted().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_started.setText(mSdfOut.format(item.getActivityItem().getStarted()));
      }
    } else {
      holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (item.getActivityItem().getFinished() != null) {
      if (item.getActivityItem().getFinished().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_finished.setText(mSdfOut.format(item.getActivityItem().getFinished()));
      }
    } else {
      holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (item.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#cccccc"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.pending));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.grey_40));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    
      holder.btn_activity_next.setEnabled(false);
      holder.btn_activity_next.setVisibility(View.INVISIBLE);
    } else if (item.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#2980B9"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.running));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    } else if (item.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#10ac84"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.completed));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
    }
  
    if (item.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
      holder.btn_add_delay_reason.setEnabled(false);
      holder.btn_add_delay_reason.setVisibility(View.INVISIBLE);
    } else {
      holder.btn_add_delay_reason.setEnabled(true);
      holder.btn_add_delay_reason.setVisibility(View.VISIBLE);
    }
  
    boolean isOneItemShown = false;
    if (item.getActivityItem() != null && item.getActivityItem().getDelayReasonItems() != null) {
      if (item.getActivityItem().getDelayReasonItems().size() > 0) {
        holder.btn_delay_reason_history.setVisibility(View.VISIBLE);
      
        int delayInMinutes = 0;
        for (int i = 0; i < item.getActivityItem().getDelayReasonItems().size(); i++) {
          delayInMinutes += item.getActivityItem().getDelayReasonItems().get(i).getDelayInMinutes();
        }
        holder.tv_delay_reason_in_minutes.setText("+" + String.valueOf(delayInMinutes) + " min");
        holder.tv_delay_reason_in_minutes.setTextColor(Color.parseColor("#E30613"));
      } else {
        holder.btn_delay_reason_history.setVisibility(View.INVISIBLE);
      }
    
    } else {
      holder.tv_delay_reason_in_minutes.setText("0 min");
      holder.tv_delay_reason_in_minutes.setTextColor(Color.parseColor("#10ac84"));
    
      if (mDataList.size() > 0) {
        List<ActivityItem> activityItems = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
          activityItems.add(mDataList.get(i).getActivityItem());
        }
        if (activityItems.size() > 0) {
          for (int i = 0; i < activityItems.size(); i++) {
            if (activityItems.get(i).getDelayReasonItems() != null) {
              isOneItemShown = true;
              break;
            }
          }
          if (isOneItemShown) {
            holder.btn_delay_reason_history.setVisibility(View.INVISIBLE);
          }
        }
      }
    }
  
    CommItem commItem = App.getInstance().gsonUtc.fromJson(mData.getData(), CommItem.class);
    if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
  
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#E30613"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.label_deleted));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
      holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
      holder.btn_add_delay_reason.setEnabled(false);
      holder.btn_add_delay_reason.setVisibility(View.GONE);
      if (position == mDataList.size()-1) {
        holder.btn_activity_next.setEnabled(true);
        holder.btn_activity_next.setVisibility(View.VISIBLE);
        holder.btn_activity_next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            handleNextButton();
          }
        });
      } else {
        holder.btn_activity_next.setEnabled(false);
        holder.btn_activity_next.setVisibility(View.INVISIBLE);
      }
      return;
    } else if (item.getTaskStatus().equals(TaskStatus.PENDING)) {
      for (int i = 0; i < mDataList.size(); i++) {
        if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          continue;
        } else if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(ContextUtils.getApplicationContext().getString(R.string.next));
          }
          break;
        } else if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.start));
          }
          break;
        }
      }
    } else if (item.getTaskStatus().equals(TaskStatus.RUNNING)) {
  
      for (int i = 0; i < mDataList.size(); i++) {
        if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.btn_activity_next.setEnabled(false);
          holder.btn_activity_next.setVisibility(View.INVISIBLE);
          //holder.btn_activity_next.setText("Finish");
      
          CommItem currItem = App.getInstance().gsonUtc.fromJson(mData.getData(), CommItem.class);
          
          if (i == mDataList.size() -1) {
            mData.setStatus(100);
            currItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
            mData.setData(App.getInstance().gsonUtc.toJson(currItem));
            updateNotify(mData);
        
            if (currItem.getTaskItem().getNextTaskId() != null && currItem.getTaskItem().getNextTaskId() > 0) {
              NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
              dao.loadNotifyByTaskId(currItem.getTaskItem().getNextTaskId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<Notify>() {
                  @Override
                  public void onSuccess(Notify notify) {
                
                    // I found next task and start it.
                    CommItem nextItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                    //nextItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                    //nextItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                    notify.setStatus(50);
                    notify.setData(App.getInstance().gsonUtc.toJson(nextItem));
                    updateNotify(notify);
                
                    //addOfflineWork(notify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                  }
              
                  @Override
                  public void onError(Throwable e) {
                    // IGNORE.
                  }
                });
              continue;
            }
          }
          continue;
        } else if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.next));
          }
          break;
        } else if (mDataList.get(i).getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.start));
          }
          break;
        }
      }
      
    } else if (item.getTaskStatus().equals(TaskStatus.CMR)) {
    
    } else if (item.getTaskStatus().equals(TaskStatus.FINISHED)) {
  
      holder.btn_activity_next.setEnabled(false);
      holder.btn_activity_next.setVisibility(View.GONE);
  
      holder.btn_add_delay_reason.setEnabled(true);
      holder.btn_add_delay_reason.setVisibility(View.VISIBLE);
    }
  
    holder.btn_add_delay_reason.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      
        DelayReasonDAO dao = DriverDatabase.getDatabase().delayReasonDAO();
        List<DelayReasonEntity> _list = dao.getDelayReasonsByMandantIdAndActivityId(mData.getMandantId(), item.getActivityItem().getActivityId());
        if (_list.size() > 0) {
          CustomDelayReasonDialog dialog = new CustomDelayReasonDialog((Activity)mContext,
            mData, _list, item.getActivityItem().getActivityId(), holder.tv_delay_reason_in_minutes.getText().toString(), 1);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        } else {
          Toast.makeText(mContext, "No delay reason configured for this activity!", Toast.LENGTH_LONG).show();
        }
      }
    });
  
    holder.btn_activity_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /*
        holder.btn_activity_next.setEnabled(false);
        holder.btn_activity_next.postDelayed(new Runnable() {
          @Override
          public void run() {
            holder.btn_activity_next.setEnabled(true);
          }
        }, 5000);*/
        handleNextButton();
      }
    });
  
    holder.btn_delay_reason_history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      
        CommItem currItem = App.getInstance().gsonUtc.fromJson(mData.getData(), CommItem.class);
        if (currItem == null) return;
      
        if (currItem.getTaskItem() != null && currItem.getTaskItem().getActivities() != null) {
          if (currItem.getTaskItem().getActivities().size() > 0) {
            List<DelayReasonItem> delayReasonItems = currItem.getTaskItem()
              .getActivities().get(position).getDelayReasonItems();
          
            DelayReasonHistoryAdapter dataAdapter = new DelayReasonHistoryAdapter(delayReasonItems);
            CustomDelayReasonHistory dialog = new CustomDelayReasonHistory((Activity)mContext,
              dataAdapter, AppUtils.parseOrderNo(currItem.getTaskItem().getOrderNo()), 0);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
          }
        }
      }
    });
    
    // Special Function.

    if (item.getActivityItem().getSpecialActivities() == null) {
      holder.btn_special_func.setVisibility(View.INVISIBLE);
    } else {

      holder.btn_special_func.setVisibility(View.INVISIBLE);

      //holder.btn_activity_next.setEnabled(true);

      int sfCount = item.getActivityItem().getSpecialActivities().size();
      if (sfCount > 0) {
        for (int i = 0; i < sfCount; i++) {
          
          SpecialActivities sa = item.getActivityItem().getSpecialActivities().get(i);
          
          if (sa.getSpecialFunction() == SpecialFunction.STANDARD)
            continue;
  
          holder.btn_special_func.setVisibility(View.VISIBLE);
          /*
          
          if (sa.getSpecialActivityResults() == null && sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_START_OF_ACTIVITY)) {
            holder.btn_activity_next.setEnabled(false);
            break;
          }
          else if (sa.getSpecialActivityResults() == null && sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
            if (item.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
              holder.btn_activity_next.setEnabled(true);
            } else if (item.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
              holder.btn_activity_next.setEnabled(false);
            } else if (item.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
              holder.btn_activity_next.setEnabled(true);
            }
            break;
          }
          else if (sa.getSpecialActivityResults() == null && sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.SPECIAL_FUNCTION_ONLY)) {
            holder.btn_activity_next.setEnabled(false);
            break;
          }
          */
        }
      }
    }
    
    
    holder.btn_special_func.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //App.eventBus.post(new QREvent(mData.getId(), position));
        if (item.getActivityItem().getSpecialActivities() != null) {
          if (item.getActivityItem().getSpecialActivities().get(0) != null && item.getActivityItem().getSpecialActivities().get(0).getSpecialFunction() != null) {
            SpecialFunction specialFunction = item.getActivityItem().getSpecialActivities().get(0).getSpecialFunction();
            if (specialFunction.equals(SpecialFunction.SCAN_BARCODE)) {
              App.eventBus.post(new QREvent(mData.getId(), position, 0));
            } else {
              App.eventBus.post(new QREvent(mData.getId(), position, 1));
            }
          }
        }
      }
    });
  }
  
  @Override
  public CommItemSubAdapterExt.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
  
    View itemView = mInflater.inflate(R.layout.step_activity_with_delay_reasons, parent, false);
    return new CommItemSubAdapterExt.ViewHolder(itemView);
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
  
    final View step_view_top;
    final View step_view_bottom;
  
    final ImageView iv_activity_step_dot;
    final ImageView iv_activity_step_check_mark;
    final AsapTextView tv_activity_step_no;
  
    final LinearLayout ll_activity_status;
    final AsapTextView tv_activity_status_text;
    final AsapTextView tv_activity_step_name;
  
    final AsapTextView tv_activity_step_started;
    final AsapTextView tv_activity_step_finished;
  
    final AppCompatButton btn_activity_next;
    final AppCompatImageButton btn_add_delay_reason;
    final AppCompatImageButton btn_delay_reason_history;
    final AppCompatImageButton btn_special_func;
  
    final AsapTextView tv_delay_reason_in_minutes;
    
    public ViewHolder(View itemView) {
      super(itemView);
  
      step_view_top = itemView.findViewById(R.id.step_view_top);
      step_view_bottom = itemView.findViewById(R.id.step_view_bottom);
      iv_activity_step_dot = itemView.findViewById(R.id.iv_activity_step_dot);
      iv_activity_step_check_mark = itemView.findViewById(R.id.iv_activity_step_check_mark);
      tv_activity_step_no = itemView.findViewById(R.id.tv_activity_step_no);
  
      ll_activity_status = itemView.findViewById(R.id.ll_activity_status);
      tv_activity_status_text = itemView.findViewById(R.id.tv_activity_status_text);
      tv_activity_step_name = itemView.findViewById(R.id.tv_activity_step_name);
  
      tv_activity_step_started = itemView.findViewById(R.id.tv_activity_step_started);
      tv_activity_step_finished = itemView.findViewById(R.id.tv_activity_step_finished);
  
      btn_activity_next = itemView.findViewById(R.id.btn_activity_next);
      btn_add_delay_reason = itemView.findViewById(R.id.btn_add_delay_reason);
      btn_delay_reason_history = itemView.findViewById(R.id.btn_delay_reason_history);
      btn_special_func = itemView.findViewById(R.id.btn_special_func);
      
      tv_delay_reason_in_minutes = itemView.findViewById(R.id.tv_delay_reason_in_minutes);
    }
  }
  
  public void setDataList(Notify data, List<ActivityStep> dataList) {
    mData = data;
    mDataList = dataList;
    notifyDataSetChanged();
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mDataList has not been updated (means initially, it's null,
  // and we can't return null.
  @Override
  public int getItemCount() {
    if (mDataList != null) {
      return mDataList.size();
    } else return 0;
  }
  
  private void updateNotify(Notify notify) {
    if (notify == null) return;
    
    NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
    if (dao == null) return;
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        dao.updateNotify(notify);
      }
    });
  }
  
  // activity Status:
  // 0 - None
  // 1 - Started
  // 2 - Finished
  private void addOfflineWork(int notifyId, int activityId, int confirmationType, int activityStatus) {
    OfflineConfirmationDAO dao = DriverDatabase.getDatabase().offlineConfirmationDAO();

    OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
    offlineConfirmation.setNotifyId(notifyId);
    offlineConfirmation.setActivityId(activityId);
    offlineConfirmation.setConfirmType(confirmationType);
    offlineConfirmation.setActivityStatus(activityStatus);
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        dao.insert(offlineConfirmation);
      }
    });
  }


  private void addHistoryLog(ActionType actionType, ActivityItem actItem, CommItem commItem) {
    try {
      EventBus.getDefault().post(new ProgressBarEvent(true, Constants.DELAY_FOR_CHANGE_HISTORY,  mContext.getString(R.string.progress_bar_placeholder)));
      EventBus.getDefault().post(new ChangeHistoryEvent(mContext.getString(R.string.log_title_activity), actItem.getName(),
              LogType.APP_TO_SERVER, actionType, ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
              commItem.getTaskItem().getTaskId(), actItem.getActivityId(), commItem.getTaskItem().getOrderNo(), commItem.getTaskItem().getMandantId(), 0));
    } catch (Exception e) {
      Log.e(TAG, "not enough data to log event : " + e.getMessage() + " commItem" + commItem.toString());
    }
  }

}
