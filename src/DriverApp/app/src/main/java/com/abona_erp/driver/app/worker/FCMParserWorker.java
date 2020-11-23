package com.abona_erp.driver.app.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.ActionType;
import com.abona_erp.driver.app.data.entity.ChangeHistoryState;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.VehicleItem;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.service.FCMParser;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DelayReasonUtil;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created on 20.10.2020 by Anton Kogan. Email: akogan777@gmail.com.
 */
public class FCMParserWorker extends Worker implements FCMParser {

    private final String TAG = "FCMParserWorker";

    @Inject
    NotificationManager notificationManager;

    @Inject
    ApiManager apiManager;

    @Inject
    DriverRepository mRepository;

    @Inject
    @Named("GSON_UTC")
    Gson gsonUtc;

    private Context appContext;


    private Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    public FCMParserWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.appContext = context;
        App.getInstance().getApplicationComponent().inject(this);
    }

    /**
     *
     * Worker classes are instantiated at runtime by WorkManager and the doWork() method is called on a pre-specified background thread (see Configuration.getExecutor()).
     * This method is for synchronous processing of your work, meaning that once you return from that method, the Worker is considered to be finished and will be destroyed.
     * If you need to do your work asynchronously or call asynchronous APIs, you should use ListenableWorker.
     * @return
     */
    @Override
    public Result doWork() {

        String extras = getInputData().getString(Constants.EXTRAS_FCM_MESSAGE);

        Log.d(TAG, "FCM parse work started: \n" + System.currentTimeMillis() + " \n" + extras);

        showFCMNotification();

        parseCommonItem(extras);

        Log.d(TAG, "FCM parse work finish: \n" + System.currentTimeMillis() + " \n");

        hideFCMNotification();
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }


    @Override
    public void showFCMNotification() {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.new_message_fcm), appContext.getResources().getString(R.string.new_message_fcm_sync));
        notificationManager.notify(Constants.NOTIFICATION_FMC_MESSAGE, builder.build());
    }

    private NotificationCompat.Builder prepareNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(getPendingIntent())
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(appContext.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //need to specify channel  on that api
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.PACKAGE_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
        }
        return builder;
    }

    @Override
    public void hideFCMNotification() {
        notificationManager.cancel(Constants.NOTIFICATION_NOT_EXIST_ALARM_ID);
    }

    private PendingIntent getPendingIntent() {
        Intent resultIntent = new Intent(appContext, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(appContext);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void saveCommonTaskPercentage(CommItem commItem) {
        // percent - need to check if header exist
        if (commItem.getPercentItem() != null) {
            if (commItem.getPercentItem().getTotalPercentFinished() != null && commItem.getPercentItem().getTotalPercentFinished() >= 0) {
                TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int) Math.round(commItem.getPercentItem().getTotalPercentFinished()));
            }
        }
    }

    @Override
    public void addDocument(CommItem commItem) {
        App.eventBus.post(new DocumentEvent(commItem.getDocumentItem().getMandantId(), commItem.getDocumentItem().getOrderNo()));

    }

    /**
     * removing tasks if no Vehicle - means deactivated.
     * @param commItem
     */
    @Override
    public void removeAllTasks(CommItem commItem) {
        Log.e(TAG,  getApplicationContext().getResources().getString(R.string.registration_number));

        TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                getApplicationContext().getResources().getString(R.string.registration_number));
        TextSecurePreferences.setClientName(getApplicationContext(), "");


        // assuming that we already in background thread
        mRepository.deleteAllLastActivities();
        mRepository.deleteAllNotify();

        DriverDatabase db = DriverDatabase.getDatabase();
        OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
        dao.deleteAll();
    }


    /**
     * as we posting from background - need to catch on main thread
     * @param item
     */
    private void postVehicleEvent(VehicleItem item) {
        TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                getApplicationContext().getResources().getString(R.string.registration_number));
        VehicleRegistrationEvent event = new VehicleRegistrationEvent(item);
        App.eventBus.post(event);
    }

    @Override
    public void addVehicle(CommItem commItem) {
        TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                commItem.getVehicleItem().getRegistrationNumber());

        if (commItem.getVehicleItem().getClientName() != null) {
            TextSecurePreferences.setClientName(getApplicationContext(),
                    commItem.getVehicleItem().getClientName());
        } else {
            TextSecurePreferences.setClientName(getApplicationContext(), "");
        }
    }

    @Override
    public boolean vehicleExist(CommItem commItem) {
        return commItem.getVehicleItem() != null && commItem.getVehicleItem().getRegistrationNumber() != null;
    }


    @Override
    public boolean parseCommonItem(String raw) {
        try {
            CommItem commItem = App.getInstance().gsonUtc.fromJson(raw, CommItem.class);

            switch (commItem.getHeader().getDataType()) {
                case VEHICLE:
                    postVehicleEvent(commItem.getVehicleItem());
                    if (vehicleExist(commItem)) {
                        addVehicle(commItem);
                    } else {
                        commItem.getVehicleItem().setRegistrationNumber(getApplicationContext().getResources().getString(R.string.registration_number));
                        VehicleRegistrationEvent event = new VehicleRegistrationEvent(commItem.getVehicleItem());
                        App.eventBus.post(event);
                        removeAllTasks(commItem);
                    }
                    break;
                case TASK:
                    addTasksAndActivities(commItem, raw);
                    break;
                case DOCUMENT:
                    addDocument(commItem);
                    break;
                case DELAY_REASONS:
                    addDelayReason(commItem);
                    break;
            }

            saveCommonTaskPercentage(commItem);

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "CommonItem model expected. and this is not common item: " + raw + "\n" + e.getMessage());
        }

        return false;
    }

    @Override
    public void addTasksAndActivities(CommItem commItem, String raw) {
        if (commItem.getTaskItem().getMandantId() != null && commItem.getTaskItem().getTaskId() != null) {
            //add alarm
            ForegroundAlarmService.startNotificationWithDelay(commItem.getTaskItem()); //workmanager api used.

            //add task, already we in background thread
            mRepository.getNotifyByMandantTaskId(commItem.getTaskItem().getMandantId(), commItem.getTaskItem().getTaskId())
                    .subscribe(new DisposableSingleObserver<Notify>() {
                        @Override
                        public void onSuccess(Notify notify) {
                            Log.d(TAG, "***** VORHANDEN - UPDATEN *****");
                            notify.setData(raw);
                            updateFoundDbTask(notify, commItem);
                            updateActivities(notify, commItem);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Notify notify = new Notify();
                            notify.setCreatedAt(AppUtils.getCurrentDateTime());
                            notify.setData(raw);
                            insertNewTask(commItem, notify);
                        }
                    });

            //DelayReasonUtil.getDelayReasonsFromService(commItem.getTaskItem().getMandantId());
        } else {
            Log.e(TAG, "Keine gültiger Task Item");
        }
    }

    @Override
    public void insertNewTask(CommItem commItem, Notify notify) {
        Log.i(TAG, "******* TASK NICHT VORHANDEN - HINZUFÜGEN *******");
        notify = Notify.addFromCommonItem(commItem, notify, false);
        mRepository.insert(notify);
    }

    @Override
    public void updateFoundDbTask(Notify notify, CommItem commItem) {
        notify = Notify.addFromCommonItem(commItem, notify, true);
        mRepository.update(notify);
    }

    @Override
    public void updateActivities(Notify notify, CommItem commItem) {
        mRepository.getLastActivityByTaskClientId(commItem.getTaskItem().getTaskId(), commItem.getTaskItem().getMandantId())
                .subscribe(new DisposableSingleObserver<LastActivity>() {
                    @Override
                    public void onSuccess(LastActivity lastActivity) {
                        updateDbActivities(lastActivity, commItem, notify);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "No LastActivity Item");
                    }
                });
    }

    @Override
    public void updateDbActivities(LastActivity lastActivity, CommItem commItem, Notify notify) {
        lastActivity = LastActivity.updateFromCommItem(lastActivity, commItem);

        mRepository.update(lastActivity);

        OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
        offlineConfirmation.setNotifyId(notify.getId());
        offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal());

        postHistoryEvent(notify, offlineConfirmation);

        mRepository.insert(offlineConfirmation);
    }

    @Override
    public void postHistoryEvent(Notify item, OfflineConfirmation offlineConfirmation) {
        EventBus.getDefault().post(new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_fcm), getApplicationContext().getString(R.string.log_task_updated_fcm),
                LogType.FCM, ActionType.UPDATE_TASK, ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
                item.getTaskId(), item.getId(), item.getOrderNo(), item.getMandantId(), offlineConfirmation.getId()));
    }
    
    @Override
    public void addDelayReason(CommItem commItem) {
        mRepository.getNotifyByMandantTaskId(
          commItem.getActivityDelayItem().mandantId,
          commItem.getActivityDelayItem().taskId
        ).observeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
                  
                  CommItem comm = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                  List<ActivityItem> activities = comm.getTaskItem().getActivities();
                  if (activities.size() > 0) {
                      for (int i = 0; i < activities.size(); i++) {
                          if (activities.get(i).getActivityId() == commItem.getActivityDelayItem().getActivityId()) {
                              activities.get(i).setDelayReasonItems(commItem.getActivityDelayItem().getDelayReasonItems());
                          }
                      }
                      notify.setData(App.getInstance().gson.toJson(comm));
                      AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                              mRepository.update(notify);
                          }
                      });
                  }
              }
    
              @Override
              public void onError(Throwable e) {
        
              }
          });
    }
}