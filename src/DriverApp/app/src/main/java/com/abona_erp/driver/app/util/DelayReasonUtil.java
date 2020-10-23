package com.abona_erp.driver.app.util;

import android.os.AsyncTask;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DelayReasonDAO;
import com.abona_erp.driver.app.data.dao.OfflineDelayReasonDAO;
import com.abona_erp.driver.app.data.entity.DelayReasonEntity;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.app.data.model.DelayReasonItem;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DelayReasonUtil {
  
  public static void getDelayReasonsFromService(int mandantId) {
    
    if (mandantId <= 0) return;

    Call<ResultOfAction> call = App.getInstance().apiManager.getDelayReasonApi()
      .getDelayReasons(mandantId, TextSecurePreferences
        .getLanguage(ContextUtils.getApplicationContext())
        .replace('_', '-'));
    
    call.enqueue(new Callback<ResultOfAction>() {
      @Override
      public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
        if (response.isSuccessful()) {
          if (response.body() == null) return;
          if (response.body().getIsSuccess() && !response.body().getIsException()) {
  
            if (response.body().getDelayReasonItems() == null) return;
  
            ResultOfAction resultOfAction = response.body();
            final List<DelayReasonItem> items = resultOfAction.getDelayReasonItems();
  
            if (items.size() > 0) {
              //Log.i(TAG, "Count of Items : " + items.size());
    
              DriverDatabase db = DriverDatabase.getDatabase();
              DelayReasonDAO dao = db.delayReasonDAO();
              for (int i = 0; i < items.size(); i++) {
      
                DelayReasonItem item = items.get(i);
                if (item != null) {
                  dao.getDelayReasonByMandantId(mandantId, item.getActivityId(), item.getWaitingReasongId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<DelayReasonEntity>() {
                      @Override
                      public void onSuccess(DelayReasonEntity delayReasonEntity) {
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            // UPDATE:
                            delayReasonEntity.setWaitingReasonId(item.getWaitingReasongId());
                            delayReasonEntity.setReasonText(item.getReasonText());
                            delayReasonEntity.setTranslatedReasonText(item.getTranslatedReasonText());
                            delayReasonEntity.setCode(item.getCode());
                            delayReasonEntity.setSubCode(item.getSubCode());
                            delayReasonEntity.setModifiedAt(new Date());
                            dao.update(delayReasonEntity);
                          }
                        });
                      }
            
                      @Override
                      public void onError(Throwable e) {
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            // INSERT:
                            DelayReasonEntity delayReasonEntity = new DelayReasonEntity();
                            delayReasonEntity.setMandantId(mandantId);
                            delayReasonEntity.setActivityId(item.getActivityId());
                            delayReasonEntity.setWaitingReasonId(item.getWaitingReasongId());
                            delayReasonEntity.setReasonText(item.getReasonText());
                            delayReasonEntity.setTranslatedReasonText(item.getTranslatedReasonText());
                            delayReasonEntity.setCode(item.getCode());
                            delayReasonEntity.setSubCode(item.getSubCode());
                            delayReasonEntity.setCreatedAt(new Date());
                            delayReasonEntity.setModifiedAt(new Date());
                            dao.insert(delayReasonEntity);
                          }
                        });
                      }
                    });
                } else {
                  // TODO: MessageBox
                }
              }
            }
            
            TextSecurePreferences.setUpdateDelayReason(false);
          } else {
            // Ignore.
          }
        }
      }
  
      @Override
      public void onFailure(Call<ResultOfAction> call, Throwable t) {
    
      }
    });
  }
  
  public static void addDelayReasonToSendServer(int notifyId, int waitingReasonId, int activityId, int mandantId, int taskId, int delayInMinutes, int delaySource, String comment) {
  
    OfflineDelayReasonDAO dao = DriverDatabase.getDatabase().offlineDelayReasonDAO();
    
    OfflineDelayReasonEntity entity = new OfflineDelayReasonEntity();
    entity.setNotifyId(notifyId);
    entity.setWaitingReasonId(waitingReasonId);
    entity.setWaitingReasonAppId(UUID.randomUUID().toString());
    entity.setActivityId(activityId);
    entity.setMandantId(mandantId);
    entity.setTaskId(taskId);
    entity.setDelayInMinutes(delayInMinutes);
    entity.setDelaySource(delaySource);
    entity.setComment(comment);
    entity.setTimestamp(new Date());
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        dao.insert(entity);
      }
    });
  }
}
