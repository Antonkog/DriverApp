package com.abona_erp.driver.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.remote.NetworkUtil;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.worker.NotifyWorker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForegroundAlarmService extends Service {
    private static final String TAG = ForegroundAlarmService.class.getCanonicalName();

    @Inject
    ApiManager apiManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = prepareNotification(getApplicationContext().getResources().getString(R.string.alarm_check_title),
                getApplicationContext().getResources().getString(R.string.alarm_check_text)).build();
        startForeground(Constants.ALARM_CHECK_JOB_ID, notification);

        if (NetworkUtil.isConnected(getApplicationContext()))
            checkTasks();
        else {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        }
        return START_NOT_STICKY;
    }

    private void checkTasks() {
        apiManager.getTaskApi().getTasksSingle(DeviceUtils.getUniqueIMEI(getApplicationContext()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(resultOfAction -> {
            if (resultOfAction.getAllTask() != null && !resultOfAction.getAllTask().isEmpty()) {
                for (TaskItem taskItem : resultOfAction.getAllTask()) {
                    startNotificationWithDelay(taskItem);
                }
            } else {
                Log.e(TAG, "no alarm tasks");
            }
        });
    }


    private void startNotificationWithDelay(TaskItem taskItem) {
        WorkManager workManager = WorkManager.getInstance(App.getInstance());

        long delay = getAlarmDelay(taskItem);

        if(delay > 0){
            Log.e(TAG, taskItem.getTaskId() + " alarm delay: secs " + delay/1000);
            setOneTimeWork(taskItem.getTaskId(), workManager, delay);
//            setRepeatWork(taskItem, workManager); //don't work properly when kill stop app
        }
//        else {
//            Log.e(TAG, taskItem.getTaskId() + " alarm time overdue " + delay/1000);
//        }
    }

    public static long getAlarmDelay(TaskItem taskItem) {
        return taskItem.getTaskDueDateFinish().getTime() - (Constants.REPEAT_COUNT * Constants.REPEAT_TIME * 60 * 1000) - System.currentTimeMillis();
    }

    private void setRepeatWork(TaskItem taskItem, WorkManager workManager) {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(NotifyWorker.class,
                        Constants.REPEAT_TIME, TimeUnit.MINUTES, Constants.FLEX_TIME, TimeUnit.MINUTES)
                        .setInputData(createInputDataForUri(taskItem.getTaskId()))
                        .addTag(taskItem.getTaskId() + Constants.WORK_TAG_SUFFIX)
                        .build();

        workManager.enqueueUniquePeriodicWork(taskItem.getTaskId() + Constants.WORK_TAG_SUFFIX, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    public static void setOneTimeWork(int taskId, WorkManager workManager, long delay) {
        OneTimeWorkRequest taskAlarmRequest =
                new OneTimeWorkRequest.Builder(NotifyWorker.class)
                        .setInputData(ForegroundAlarmService.createInputDataForUri(taskId))
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();
        workManager.enqueue(taskAlarmRequest);
    }

    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the Image Uri as a String
     */
    public static Data createInputDataForUri(int taskID) {
        Data.Builder builder = new Data.Builder();
        builder.putInt(Constants.EXTRAS_ALARM_TASK_ID, taskID);
        return builder.build();
    }


    private NotificationCompat.Builder prepareNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constants.NOTIFICATION_CHANNEL_ID)
                //   .setContentIntent(getPendingIntent())
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //need to specify channel  on that api
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.PACKAGE_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
        }
        return builder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * providing alarm time based on taskItem
     * see:
     * Constants.REPEAT_COUNT
     * Constants.REPEAT_TIME
     * @param taskItem
     * @return
     */
    @NotNull
    private static Calendar getTaskTime(TaskItem taskItem) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskItem.getTaskDueDateFinish());
        int m = Constants.REPEAT_COUNT;
        calendar.add(Calendar.MINUTE, -(Constants.REPEAT_TIME * Constants.REPEAT_COUNT));
        while (m > 0) {
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.MINUTE, Constants.REPEAT_TIME);
            m--;
        }
//        android.util.Log.d(TAG, "got time for alarm: " + calendar.getTime().toString() );
        return calendar;
    }


    @Subscribe
    public void onMessageEvent(ConnectivityEvent event) {
        if (event.isConnected()) {
            checkTasks();
        }
    }
}