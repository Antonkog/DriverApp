package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import io.reactivex.Flowable;

public class HistoryViewModel extends AndroidViewModel {
  private static final String TAG = "HistoryViewModel";
  private DriverRepository _repo;
  private MutableLiveData<List<ChangeHistory>> filteredLogs = new MutableLiveData<>();
  private List<ChangeHistory> logsByTaskId = new ArrayList<>();
  private List<ChangeHistory> logsByOrderNum = new ArrayList<>();
  private LiveData<List<ChangeHistory>> allLogs;
  private boolean filterByTaskNotOrder = true;

  public HistoryViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
  }

  public void setHistoryLogs(){
    allLogs =  _repo.getChangeHistoryDao().getLogs();
  }

  public LiveData<List<ChangeHistory>> getHistoryLogs() {
    return allLogs;
  }

  public MutableLiveData<List<ChangeHistory>> getFilteredLogs() {
    return filteredLogs;
  }

  public void deleteChangeHistory() {
    _repo.deleteChangeHistory();
  }

  public void setHistoryLogsByOrderNumber() {
    filterByTaskNotOrder = false;
    filteredLogs.postValue(logsByOrderNum);
  }

  public void setLogsWithTaskId() {
    filterByTaskNotOrder = true;
    filteredLogs.postValue(logsByTaskId);
  }

  public void refreshLogs(List<ChangeHistory> logItems, int currentTaskId, int currentOrderNum) {

    filterByOrderNum(logItems, currentOrderNum);

    filterByTaskId(logItems, currentTaskId);

    if (filterByTaskNotOrder)
      filteredLogs.postValue(logsByTaskId);
    else
      filteredLogs.postValue(logsByOrderNum);
  }

  private void filterByTaskId(List<ChangeHistory> logItems, int currentTaskId) {
    if(logItems!= null && !logItems.isEmpty()){
      logsByTaskId.clear();
      for (int i = 0; i <logItems.size() ; i++) {
        if(logItems.get(i).getTaskId() == currentTaskId)
          logsByTaskId.add(logItems.get(i));
      }
    }
  }

  private void filterByOrderNum(List<ChangeHistory> logItems, int currentOrderNum) {
    if(logItems!= null && !logItems.isEmpty()){
      logsByOrderNum.clear();
      for (int i = 0; i <logItems.size() ; i++) {
        if(logItems.get(i).getOrderNumber() == currentOrderNum)
          logsByOrderNum.add(logItems.get(i));
      }
    }
  }

  /**
   * method to send email based on device profile and action history
   * author: Anton Kogan
   * @param context
   * @param deviceProfileString
   * @param callback =  MaildroidX.onCompleteCallback
   */
  public void sendEmailSmtp(Context context, String deviceProfileString, MaildroidX.onCompleteCallback callback) {
       sendEmailSmtp(context, deviceProfileString, logsByOrderNum, callback);
  }

  public static void sendEmailSmtp(Context context, String deviceProfileString, List<ChangeHistory> logsByOrderNum,MaildroidX.onCompleteCallback callback){
    if (logsByOrderNum.isEmpty())  callback.onFail("ChangeHistory list empty");
    try {
      AppUtils.removeLogFile(context);
      AppUtils.appendLogsInFile(context, logsByOrderNum);
    }catch (IOException e){
      callback.onFail("can't append log file, " + e.getMessage());
    }

    new MaildroidX.Builder()
            .smtp("smtp.omc-mail.de")
            .port("25")
            .smtpUsername("syst8514@systemhaus-alber.de")
            .smtpPassword("syst8514")
            .type((MaildroidXType.HTML))
            .from("logging@abona-erp.com")
            .to("logging@abona-erp.com")
            .subject(context.getString(R.string.history_email_theme))
            .body(deviceProfileString)
            .attachment(AppUtils.getLogFile(context).getAbsolutePath())
            .onCompleteCallback(callback).send();
  }

  /**method to send email based on device profile and action history
   * author: Anton Kogan
   * @param context
   * @param deviceProfileString
   */
  public void sendEmailIntentMessage(Context context, String deviceProfileString) {
    try {
      AppUtils.sendEmailIntent(context, deviceProfileString, logsByOrderNum);
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
      e.printStackTrace();
    }
  }
  /**
   * Get the DeviceProfile
   *
   * @return a {@link Flowable} DeviceProfile.
   */
  public Flowable<DeviceProfile> getDeviceProfile() {
    return  _repo.getmDeviceProfileDAO().getDeviceProfile();
  }
}
