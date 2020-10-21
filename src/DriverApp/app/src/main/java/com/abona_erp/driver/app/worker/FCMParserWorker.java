package com.abona_erp.driver.app.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
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
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.service.FCMParser;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.ProfileEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DelayReasonUtil;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created on 20.10.2020 by Anton Kogan. Email: akogan777@gmail.com.
 */
public class FCMParserWorker extends Worker implements FCMParser, MediaPlayer.OnPreparedListener {


    private final String TAG = "FCMParserWorker";

    @Inject
    public NotificationManager notificationManager;

    @Inject
    public ApiManager apiManager;

    @Inject
    DriverRepository mRepository;

    private Context appContext;



    CommItem commItem = new CommItem();



    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private MediaPlayer mMediaPlayer;


    public FCMParserWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.appContext = context;
        App.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public Result doWork() {

        setRingtonePlayer();

        String extras = getInputData().getString(Constants.EXTRAS_FCM_MESSAGE);

        Log.d(TAG, "FCM parse work started: " + extras);
        showFCMNotification();

        parseCommonItem(extras);

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    public void showFCMNotification() {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.new_message_fcm), appContext.getResources().getString(R.string.alarm_check_failed_reason));
        notificationManager.notify(Constants.NOTIFICATION_NOT_EXIST_ALARM_ID, builder.build());
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

    public void removeParseNotification() {
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


    /**
     * that is old method to parse CommonItem, implemented not by me, from NotificationService.java that was removed.
     * @param raw strng from FirebaseMessagingService
     * @return true if parsed, but need to rewrite this method.
     */
    @Override
    public boolean parseCommonItem(String raw) {

        commItem = App.getInstance().gsonUtc.fromJson(raw, CommItem.class);

        // CHECK VEHICLE REGISTRATION NUMBER:
        if (commItem.getHeader().getDataType().equals(DataType.VEHICLE)) {
            VehicleRegistrationEvent event = new VehicleRegistrationEvent();
            if (commItem.getVehicleItem() != null) {
                if (commItem.getVehicleItem().getRegistrationNumber() != null) {
                    TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                            commItem.getVehicleItem().getRegistrationNumber());
                } else {
                    TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.registration_number));

                    // RESET ALL ITEMS:
                    event.setDeleteAll(true);
                    mRepository.deleteAllNotify();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            mRepository.deleteAllLastActivities();
                            DriverDatabase db = DriverDatabase.getDatabase();
                            OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
                            dao.deleteAll();
                        }
                    });
                }
                if (commItem.getVehicleItem().getClientName() != null) {
                    TextSecurePreferences.setClientName(getApplicationContext(),
                            commItem.getVehicleItem().getClientName());
                } else {
                    TextSecurePreferences.setClientName(getApplicationContext(), "");
                }

                // Drivers
                if (commItem.getVehicleItem().getDrivers() != null) {
                    if (commItem.getVehicleItem().getDrivers().size() > 0) {
                        if (commItem.getVehicleItem().getDrivers().get(0).getImageUrl() != null) {
                            // First Driver
                            App.eventBus.post(new ProfileEvent(commItem.getVehicleItem().getDrivers().get(0).getImageUrl()));
                        }
                    }
                }
            } else {
                TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
                        getApplicationContext().getResources().getString(R.string.registration_number));
                TextSecurePreferences.setClientName(getApplicationContext(), "");
            }

            App.eventBus.post(event);
            startRingtone(notification);
            return true;
        } else if (commItem.getHeader().getDataType().equals(DataType.DOCUMENT)) {
            App.eventBus.post(new DocumentEvent(commItem.getDocumentItem().getMandantId(), commItem.getDocumentItem().getOrderNo()));
            return true;
        }

        if (commItem.getPercentItem() != null) {
            if (commItem.getPercentItem().getTotalPercentFinished() != null && commItem.getPercentItem().getTotalPercentFinished() >= 0) {
                TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(commItem.getPercentItem().getTotalPercentFinished()));
            }
        }

        if (commItem.getTaskItem().getMandantId() != null && commItem.getTaskItem().getTaskId() != null) {
            ForegroundAlarmService.startNotificationWithDelay(commItem.getTaskItem());
            mRepository.getNotifyByMandantTaskId(commItem.getTaskItem().getMandantId(), commItem.getTaskItem().getTaskId()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<Notify>() {
                        @Override
                        public void onSuccess(Notify notify) {
                            Log.d(TAG, "***** VORHANDEN - UPDATEN *****");

                            notify.setData(raw);
                            notify.setRead(false);
                            if (commItem.getPercentItem() != null) {
                                if (commItem.getPercentItem().getPercentFinished() != null && commItem.getPercentItem().getPercentFinished() >= 0) {
                                    notify.setPercentFinished((int)Math.round(commItem.getPercentItem().getPercentFinished()));
                                }
                            }
                            if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                notify.setStatus(0);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                notify.setStatus(50);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                notify.setStatus(90);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                notify.setStatus(100);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                notify.setStatus(51);
                            }
                            notify.setTaskDueFinish(commItem.getTaskItem().getTaskDueDateFinish());
                            notify.setOrderNo(commItem.getTaskItem().getOrderNo());
                            notify.setModifiedAt(AppUtils.getCurrentDateTime());

                            mRepository.update(notify);
                            startRingtone(notification);

                            mRepository.getLastActivityByTaskClientId(commItem.getTaskItem().getTaskId(), commItem.getTaskItem().getMandantId()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                                        @Override
                                        public void onSuccess(LastActivity lastActivity) {
                                            lastActivity.setCustomer(commItem.getTaskItem().getKundenName());
                                            lastActivity.setOrderNo(AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()));
                                            lastActivity.setStatusType(1);
                                            lastActivity.setConfirmStatus(0);
                                            lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());

                                            if (commItem.getTaskItem().getActionType().equals(TaskActionType.PICK_UP)) {
                                                lastActivity.setTaskActionType(0);
                                            } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.DROP_OFF)) {
                                                lastActivity.setTaskActionType(1);
                                            } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.TRACTOR_SWAP)) {
                                                lastActivity.setTaskActionType(3);
                                            } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.GENERAL)) {
                                                lastActivity.setTaskActionType(2);
                                            } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.DELAY)) {
                                                lastActivity.setTaskActionType(4);
                                            } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.UNKNOWN)) {
                                                lastActivity.setTaskActionType(100);
                                            }

                  /*
                  SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                    Locale.getDefault());
                  Date timestamp = new Date();
                  lastActivity.setModifiedAt(sdf.format(timestamp));
                   */
                                            ArrayList<String> _list = lastActivity.getDetailList();

                                            LastActivityDetails _detail = new LastActivityDetails();
                                            _detail.setDescription("UPDATE");
                                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                                    Locale.getDefault());
                                            _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                            _list.add(App.getInstance().gson.toJson(_detail));
                                            lastActivity.setDetailList(_list);
                                            if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING) || commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                                lastActivity.setVisible(true);
                                            } else {
                                                lastActivity.setVisible(false);
                                            }
                                            mRepository.update(lastActivity);

                                            DriverDatabase db = DriverDatabase.getDatabase();
                                            OfflineConfirmationDAO dao = db.offlineConfirmationDAO();

                                            OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
                                            offlineConfirmation.setNotifyId(notify.getId());
                                            offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal());
                                            postHistoryEvent(notify, offlineConfirmation);

                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dao.insert(offlineConfirmation);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "******* TASK NICHT VORHANDEN - HINZUFÜGEN *******");

                            Notify notify = new Notify();
                            notify.setMandantId(commItem.getTaskItem().getMandantId());
                            notify.setTaskId(commItem.getTaskItem().getTaskId());
                            if (commItem.getPercentItem() != null) {
                                if (commItem.getPercentItem().getPercentFinished() != null && commItem.getPercentItem().getPercentFinished() >= 0) {
                                    notify.setPercentFinished((int)Math.round(commItem.getPercentItem().getPercentFinished()));
                                }
                            }
                            notify.setData(raw);
                            notify.setRead(false);
                            if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                notify.setStatus(0);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                notify.setStatus(50);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                notify.setStatus(90);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                notify.setStatus(100);
                            } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                notify.setStatus(51);
                            }
                            notify.setTaskDueFinish(commItem.getTaskItem().getTaskDueDateFinish());
                            notify.setOrderNo(commItem.getTaskItem().getOrderNo());
                            notify.setCreatedAt(AppUtils.getCurrentDateTime());
                            notify.setModifiedAt(AppUtils.getCurrentDateTime());
                            mRepository.insert(notify);
                            startRingtone(notification);
                        }
                    });

            DelayReasonUtil.getDelayReasonsFromService(commItem.getTaskItem().getMandantId());
        } else {
            Log.w(TAG, "Keine gültiger Task Item");
        }
        return false;
    }

     @Override
     public void startRingtone(Uri uri) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(ContextUtils.getApplicationContext(), uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postHistoryEvent(Notify item, OfflineConfirmation offlineConfirmation) {
        EventBus.getDefault().post(new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_fcm), getApplicationContext().getString(R.string.log_task_updated_fcm),
                LogType.FCM, ActionType.UPDATE_TASK, ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
                item.getTaskId(), item.getId(), item.getOrderNo(), item.getMandantId(), offlineConfirmation.getId()));
    }



    @Override
    public void setRingtonePlayer() {
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setWakeMode(ContextUtils.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        }
        mMediaPlayer.setOnPreparedListener(this);
    }




    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}