package com.abona_erp.driver.app.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.service.AlarmChecker;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.util.DeviceUtils;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotifyWorker extends Worker implements AlarmChecker {


    private final String TAG = "NotifyWorker";

    @Inject
    public NotificationManager notificationManager;

    @Inject
    public ApiManager apiManager;

    private Context appContext;

    private Boolean taskTimeFinish = false;

    public NotifyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.appContext = context;
        App.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public Result doWork() {

        int taskId = getInputData().getInt(Constants.EXTRAS_ALARM_TASK_ID, 0);

        Log.d(TAG, " alarm work started: " + taskId);
        showCheckingNotification();


        removeCheckingNotification();
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    public void showCheckingNotification() {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.alarm_check_title), appContext.getResources().getString(R.string.alarm_check_text));
        notificationManager.notify(Constants.NOTIFICATION_CHECK_ALARM_ID, builder.build());
    }

    public void showExistNotification(int notificationId) {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.alarm_title), appContext.getResources().getString(R.string.alarm_text));
        notificationManager.notify(notificationId, builder.build());
    }

    public void showNotExistNotification() {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.alarm_check_failed), appContext.getResources().getString(R.string.alarm_check_failed_reason));
        notificationManager.notify(Constants.NOTIFICATION_NOT_EXIST_ALARM_ID, builder.build());
    }

    public void showTimeOverdueNotification() {
        NotificationCompat.Builder builder = prepareNotification(appContext.getResources().getString(R.string.alarm_check_failed), appContext.getResources().getString(R.string.alarm_check_failed_reason));
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

    public void removeCheckingNotification() {
        notificationManager.cancel(Constants.NOTIFICATION_CHECK_ALARM_ID);
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
    public Single<Boolean> checkTaskExist(int taskId) {
        return apiManager.getTaskApi().getTasksSingle(DeviceUtils.getUniqueIMEI(appContext))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(resultOfAction -> {
                    if (resultOfAction.getIsSuccess() && !resultOfAction.getAllTask().isEmpty()) {
                        taskTimeFinish = false;
                        for (TaskItem taskItem : resultOfAction.getAllTask()) {
                            if (checkTaskDelayed(taskItem)) {
                                taskTimeFinish = true;
                                return false;
                            }
                            if (taskId == taskItem.getTaskId()) return true;
                        }
                    }
                    return false;
                });
    }

    private boolean checkTaskDelayed(TaskItem taskItem) {
       return taskItem.getTaskDueDateFinish().getTime() < System.currentTimeMillis();
    }
}