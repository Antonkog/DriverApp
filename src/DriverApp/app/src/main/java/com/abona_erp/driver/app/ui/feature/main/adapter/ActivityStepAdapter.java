package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogLevel;
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
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.LogEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.CustomDelayReasonDialog;
import com.abona_erp.driver.app.ui.widget.CustomDelayReasonHistory;
import com.abona_erp.driver.app.ui.widget.DelayReasonHistoryAdapter;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;

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

public class ActivityStepAdapter extends RecyclerView.Adapter<ActivityStepAdapter.ViewHolder> {
  
  private static final String TAG = ActivityStepAdapter.class.getSimpleName();
  
  private static final String DATE_FORMAT_IN = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT_OUT = "dd.MM.yyyy HH:mm:ss";
  
  private static final String NOT_SET_TIMESTAMP_GLYPH = "--.--.---- --:--:--";

  private boolean mDeleted = false;

  class ViewHolder extends RecyclerView.ViewHolder {
    
    private final LinearLayout ll_activity_status;
    private final AsapTextView tv_activity_status_text;
    private final AsapTextView tv_activity_step_name;
    
    private final View step_view_top;
    private final View step_view_bottom;

    private final ImageView iv_activity_step_dot;
    private final ImageView iv_activity_step_check_mark;
    private final AsapTextView tv_activity_step_no;

    private final AsapTextView tv_activity_step_started;
    private final AsapTextView tv_activity_step_finished;
    
    private final AsapTextView tv_delay_reason_in_minutes;
    
    private final AppCompatButton btn_activity_next;
    private final AppCompatImageButton btn_add_delay_reason;
    private final AppCompatImageButton btn_delay_reason_history;

    private ViewHolder(View itemView) {
      super(itemView);
      
      ll_activity_status = itemView.findViewById(R.id.ll_activity_status);
      tv_activity_status_text = itemView.findViewById(R.id.tv_activity_status_text);
      tv_activity_step_name = itemView.findViewById(R.id.tv_activity_step_name);
      
      step_view_top = itemView.findViewById(R.id.step_view_top);
      step_view_bottom = itemView.findViewById(R.id.step_view_bottom);
      iv_activity_step_dot = itemView.findViewById(R.id.iv_activity_step_dot);
      iv_activity_step_check_mark = itemView.findViewById(R.id.iv_activity_step_check_mark);
      tv_activity_step_no = itemView.findViewById(R.id.tv_activity_step_no);

      tv_activity_step_started = itemView.findViewById(R.id.tv_activity_step_started);
      tv_activity_step_finished = itemView.findViewById(R.id.tv_activity_step_finished);
      
      tv_delay_reason_in_minutes = itemView.findViewById(R.id.tv_delay_reason_in_minutes);
      
      btn_activity_next = itemView.findViewById(R.id.btn_activity_next);
      btn_add_delay_reason = itemView.findViewById(R.id.btn_add_delay_reason);
      btn_delay_reason_history = itemView.findViewById(R.id.btn_delay_reason_history);
    }
  }
  
  private final Context mContext;
  private final Notify mNotify;
  private final LayoutInflater mInflater;
  private List<ActivityStep> mActivityStepItems;

  private final SimpleDateFormat mSdfIn;
  private final SimpleDateFormat mSdfOut;
  private Date mDateMin;
  
  private boolean isPreviousTaskFinished = false;
  
  //private List<String> mDelayReasonTextList = new ArrayList<>();

  public ActivityStepAdapter(Context ctx, Notify notify) {
    
    mContext = ctx;
    mNotify = notify;
    mInflater = LayoutInflater.from(mContext);

    mSdfIn = new SimpleDateFormat(DATE_FORMAT_IN, Locale.getDefault());
    mSdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
    try {
      mDateMin = mSdfIn.parse("1970-01-01 01:00:00");
    } catch (ParseException e) {
      if (e.getMessage() != null) Log.e(TAG, e.getMessage());
    }
  }
  
  @Override
  public ActivityStepAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView = mInflater.inflate(R.layout.step_activity_with_delay_reasons, parent, false);
    return new ActivityStepAdapter.ViewHolder(itemView);
  }
  
  @Override
  public void onBindViewHolder(ActivityStepAdapter.ViewHolder holder, int position) {
    
    if (mActivityStepItems == null) return;
    if (position < 0) return;
  
    holder.setIsRecyclable(false);
    ActivityStep current = mActivityStepItems.get(position);
    if (current == null) return;
  
    if (position == 0 && mActivityStepItems.size() == 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    } else if (position == 0 && mActivityStepItems.size() > 1) {
      holder.step_view_top.setVisibility(View.INVISIBLE);
    } else {
      holder.step_view_top.setVisibility(View.VISIBLE);
      holder.step_view_bottom.setVisibility(View.VISIBLE);
    }
    if (position == mActivityStepItems.size() - 1) {
      holder.step_view_bottom.setVisibility(View.INVISIBLE);
    }
    holder.tv_activity_step_no.setVisibility(View.GONE);
    holder.iv_activity_step_check_mark.setVisibility(View.GONE);
  
    if (current.getActivityItem().getName() != null) {
      holder.tv_activity_step_name.setText(current.getActivityItem().getName());
    }
  
    if (current.getActivityItem().getStarted() != null) {
      if (current.getActivityItem().getStarted().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_started.setText(mSdfOut.format(current.getActivityItem().getStarted()));
      }
    } else {
      holder.tv_activity_step_started.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (current.getActivityItem().getFinished() != null) {
      if (current.getActivityItem().getFinished().compareTo(mDateMin) < 0) {
        holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
      } else {
        holder.tv_activity_step_finished.setText(mSdfOut.format(current.getActivityItem().getFinished()));
      }
    } else {
      holder.tv_activity_step_finished.setText(NOT_SET_TIMESTAMP_GLYPH);
    }
  
    if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#cccccc"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.pending));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.grey_40));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    
      holder.btn_activity_next.setEnabled(false);
      holder.btn_activity_next.setVisibility(View.INVISIBLE);
    } else if (current.getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#2980B9"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.running));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.tv_activity_step_no.setVisibility(View.VISIBLE);
      holder.tv_activity_step_no.setText(String.valueOf(position + 1));
    } else if (current.getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#10ac84"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.completed));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    
      holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
    }
  
    if (current.getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
      holder.btn_add_delay_reason.setEnabled(false);
      holder.btn_add_delay_reason.setVisibility(View.INVISIBLE);
    } else {
      holder.btn_add_delay_reason.setEnabled(true);
      holder.btn_add_delay_reason.setVisibility(View.VISIBLE);
    }
    
    boolean isOneItemShown = false;
    if (current.getActivityItem() != null && current.getActivityItem().getDelayReasonItems() != null) {
      if (current.getActivityItem().getDelayReasonItems().size() > 0) {
        holder.btn_delay_reason_history.setVisibility(View.VISIBLE);
        
        int delayInMinutes = 0;
        for (int i = 0; i < current.getActivityItem().getDelayReasonItems().size(); i++) {
          delayInMinutes += current.getActivityItem().getDelayReasonItems().get(i).getDelayInMinutes();
        }
        holder.tv_delay_reason_in_minutes.setText("+" + String.valueOf(delayInMinutes) + " min");
        holder.tv_delay_reason_in_minutes.setTextColor(Color.parseColor("#E30613"));
      } else {
        holder.btn_delay_reason_history.setVisibility(View.INVISIBLE);
      }
      
    } else {
      holder.tv_delay_reason_in_minutes.setText("0 min");
      holder.tv_delay_reason_in_minutes.setTextColor(Color.parseColor("#10ac84"));
      
      if (mActivityStepItems.size() > 0) {
        List<ActivityItem> activityItems = new ArrayList<>();
        for (int i = 0; i < mActivityStepItems.size(); i++) {
          activityItems.add(mActivityStepItems.get(i).getActivityItem());
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
    
    if (mDeleted) {
      holder.ll_activity_status.setBackgroundColor(Color.parseColor("#E30613"));
      holder.tv_activity_status_text.setText(ContextUtils.getApplicationContext().getString(R.string.label_deleted));
      holder.tv_activity_status_text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
      holder.iv_activity_step_check_mark.setVisibility(View.VISIBLE);
      holder.btn_add_delay_reason.setEnabled(false);
      holder.btn_add_delay_reason.setVisibility(View.GONE);
      if (position == mActivityStepItems.size()-1) {
        holder.btn_activity_next.setEnabled(true);
        holder.btn_activity_next.setVisibility(View.VISIBLE);
      } else {
        holder.btn_activity_next.setEnabled(false);
        holder.btn_activity_next.setVisibility(View.INVISIBLE);
      }
      return;
    } else if (current.getTaskStatus().equals(TaskStatus.PENDING)) {
      for (int i = 0; i < mActivityStepItems.size(); i++) {
        if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          continue;
        } else if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(ContextUtils.getApplicationContext().getString(R.string.next));
          }
          break;
        } else if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.start));
          }
          break;
        }
      }
    } else if (current.getTaskStatus().equals(TaskStatus.RUNNING)) {
      for (int i = 0; i < mActivityStepItems.size(); i++) {
        if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.FINISHED)) {
          holder.btn_activity_next.setEnabled(false);
          holder.btn_activity_next.setVisibility(View.INVISIBLE);
          //holder.btn_activity_next.setText("Finish");
          
          CommItem currItem = App.getInstance().gsonUtc.fromJson(mNotify.getData(), CommItem.class);
          if (i == mActivityStepItems.size() -1) {
            mNotify.setStatus(100);
            currItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
            mNotify.setData(App.getInstance().gsonUtc.toJson(currItem));
            updateNotify(mNotify);
            
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
                    nextItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                    nextItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                    notify.setStatus(50);
                    notify.setData(App.getInstance().gsonUtc.toJson(nextItem));
                    updateNotify(notify);

                    addOfflineWork(currItem, notify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                  }
  
                  @Override
                  public void onError(Throwable e) {
                    // IGNORE.
                  }
                });
            }
          }
          continue;
        } else if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.RUNNING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.next));
          }
          break;
        } else if (mActivityStepItems.get(i).getActivityItem().getStatus().equals(ActivityStatus.PENDING)) {
          if (position == i) {
            holder.btn_activity_next.setEnabled(true);
            holder.btn_activity_next.setVisibility(View.VISIBLE);
            holder.btn_activity_next.setText(mContext.getResources().getString(R.string.start));
          }
          break;
        }
      }
    } else if (current.getTaskStatus().equals(TaskStatus.CMR)) {
    
    } else if (current.getTaskStatus().equals(TaskStatus.FINISHED)) {
      holder.btn_activity_next.setEnabled(false);
      holder.btn_activity_next.setVisibility(View.GONE);
      
      holder.btn_add_delay_reason.setEnabled(false);
      holder.btn_add_delay_reason.setVisibility(View.GONE);
    }
  
    holder.btn_add_delay_reason.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        DelayReasonDAO dao = DriverDatabase.getDatabase().delayReasonDAO();
        List<DelayReasonEntity> _list = dao.getDelayReasonsByMandantIdAndActivityId(mNotify.getMandantId(), current.getActivityItem().getActivityId());
        if (_list.size() > 0) {
          CustomDelayReasonDialog dialog = new CustomDelayReasonDialog((Activity)mContext,
            mNotify, _list, current.getActivityItem().getActivityId(), holder.tv_delay_reason_in_minutes.getText().toString(), 1);
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
        handleNextButton();
      }
    });
    
    holder.btn_delay_reason_history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        CommItem currItem = App.getInstance().gsonUtc.fromJson(mNotify.getData(), CommItem.class);
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
  }
  
  private void handleNextButton() {

    if (mNotify == null) return;
    
    CommItem commItem = App.getInstance().gsonUtc.fromJson(mNotify.getData(), CommItem.class);
    
    if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)
      && commItem.getTaskItem().getTaskStatus() != TaskStatus.FINISHED) {
      
      mNotify.setStatus(100);
      commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
      mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
      updateNotify(mNotify);
  
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
      
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
                  AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                      MessageDialog.build((AppCompatActivity)mContext)
                        .setStyle(DialogSettings.STYLE.STYLE_IOS)
                        .setTheme(DialogSettings.THEME.LIGHT)
                        .setTitle(mContext.getResources().getString(R.string.action_warning_notice))
                        .setMessage(mContext.getResources().getString(R.string.action_previous_task_message))
                        .setOkButton(mContext.getResources().getString(R.string.action_ok),
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
  
              @Override
              public void onError(Throwable e) {
                isPreviousTaskFinished = true;
              }
            });
        } else {
          isPreviousTaskFinished = true;
        }
        if (!isPreviousTaskFinished) return;
  
        // Start Activity?
        MessageDialog.build((AppCompatActivity)mContext)
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle(mContext.getResources().getString(R.string.action_start_order))
          .setMessage(mContext.getResources().getString(R.string.action_start_task_msg))
          .setOkButton(mContext.getResources().getString(R.string.action_start),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
  
                isPreviousTaskFinished = false;
                commItem.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
                commItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                commItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                mNotify.setStatus(50);
                mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
                updateNotify(mNotify);

                OfflineConfirmationDAO dao = DriverDatabase.getDatabase().offlineConfirmationDAO();

                OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
                offlineConfirmation.setNotifyId(mNotify.getId());
                offlineConfirmation.setActivityId(0);
                offlineConfirmation.setConfirmType(ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());

                addOfflineWork(commItem, mNotify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                return false;
              }
            })
          .setCancelButton(mContext.getResources().getString(R.string.action_cancel),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
                return false;
              }
            })
          .show();
      } else {

        mNotify.setStatus(100);
        commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
        mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
        updateNotify(mNotify);
      }
      
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
      
      if (commItem.getTaskItem().getActivities().size() > 0) {
        for (int i = 0; i < commItem.getTaskItem().getActivities().size(); i++) {
          if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
            if (i == commItem.getTaskItem().getActivities().size() - 1) {
              mNotify.setStatus(100);
              commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
              mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
              updateNotify(mNotify);
  
              //CommItem currItem = App.getInstance().gsonUtc.fromJson(mNotify.getData(), CommItem.class);
              if (i == mActivityStepItems.size() -1) {
                
                if (commItem.getTaskItem().getNextTaskId() != null && commItem.getTaskItem().getNextTaskId() > 0) {
                  NotifyDao dao = DriverDatabase.getDatabase().notifyDao();
                  dao.loadNotifyByTaskId(commItem.getTaskItem().getNextTaskId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<Notify>() {
                      @Override
                      public void onSuccess(Notify notify) {
            
                        // I found next task and start it.
                        CommItem nextItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                        nextItem.getTaskItem().getActivities().get(0).setStarted(AppUtils.getCurrentDateTimeUtc());
                        nextItem.getTaskItem().getActivities().get(0).setStatus(ActivityStatus.RUNNING);
                        notify.setStatus(50);
                        notify.setData(App.getInstance().gsonUtc.toJson(nextItem));
                        updateNotify(notify);
            
                        addOfflineWork(commItem, notify.getId(), 0, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
                      }
          
                      @Override
                      public void onError(Throwable e) {
                        // IGNORE.
                      }
                    });
                }
              }
            }
            continue;
          }
          
          if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
            commItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
            commItem.getTaskItem().getActivities().get(i).setFinished(AppUtils.getCurrentDateTimeUtc());
            mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
            updateNotify(mNotify);
  
            addOfflineWork(commItem, mNotify.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
            
            if (i < commItem.getTaskItem().getActivities().size() - 1) {
              commItem.getTaskItem().getActivities().get(i+1).setStatus(ActivityStatus.RUNNING);
              commItem.getTaskItem().getActivities().get(i+1).setStarted(AppUtils.getCurrentDateTimeUtc());
              mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
              updateNotify(mNotify);
              
              addOfflineWork(commItem, mNotify.getId(), i+1, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
            }
            break;
          }
          
          if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
            
            commItem.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
            commItem.getTaskItem().getActivities().get(i).setStarted(AppUtils.getCurrentDateTimeUtc());
            mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
            updateNotify(mNotify);
            
            if (i != 0)
              addOfflineWork(commItem, mNotify.getId(), i, ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal());
            break;
          }
        }
      }
    
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
    
      mNotify.setStatus(100);
      commItem.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
      mNotify.setData(App.getInstance().gsonUtc.toJson(commItem));
      updateNotify(mNotify);
      
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
    
    }
  }
  
  private void openDelayReasonDialog(DelayReasonItem delayReasonItem) {
  
  }
  
  private void addOfflineWork(CommItem commItem, int notifyId, int activityId, int confirmationType) {
    OfflineConfirmationDAO dao = DriverDatabase.getDatabase().offlineConfirmationDAO();
    
    OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
    offlineConfirmation.setNotifyId(notifyId);
    offlineConfirmation.setActivityId(activityId);
    offlineConfirmation.setConfirmType(confirmationType);

    try {
      EventBus.getDefault().post(new ChangeHistoryEvent(mContext.getString(R.string.log_title_open_confirm), mContext.getString(R.string.log_activity_start_pressed),
              LogType.APP_TO_SERVER, ActionType.START_ACTIVITY, ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
              commItem.getTaskItem().getTaskId(), 0, commItem.getTaskItem().getOrderNo(), commItem.getTaskItem().getMandantId(), offlineConfirmation.getId()));
    } catch (Exception e) {
      Log.e(TAG, "not enough data to log event : " + e.getMessage() + " commitem" + commItem.toString());
    }

    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        dao.insert(offlineConfirmation);
      }
    });
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

  public void setActivityStepItems(List<ActivityStep> items, boolean deleted) {
    mActivityStepItems = items;
    mDeleted = deleted;
    notifyDataSetChanged();
  }

  // getItemCount() is called many times, and when it is first called,
  // mActivityStepItems has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mActivityStepItems != null)
      return mActivityStepItems.size();
    else return 0;
  }
}
