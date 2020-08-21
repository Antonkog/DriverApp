package com.abona_erp.driver.app.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.service.ProfileUpdater;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.core.base.ContextUtils;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeviceProfileWorker extends Worker implements ProfileUpdater {


    private final String TAG = "DeviceProfileWorker";

    @Inject
    public ApiManager apiManager;
    private DriverDatabase mDB = DriverDatabase.getDatabase();
    private DeviceProfileDAO mDeviceProfileDAO = mDB.deviceProfileDAO();

    public DeviceProfileWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        /*App.getInstance().getApplicationComponent().inject(this);*/
    }

    @Override
    public Result doWork() {
        Log.d(TAG, " device update work started: " );

        DeviceProfile profile  = mDeviceProfileDAO.getDeviceProfiles().get(0); //why use db for one unique instance ???????? need change that.
        if (profile != null)
            updateDeviceProfile(getCommItem(profile)).subscribe(succeed -> {
                if (succeed) {
                    Log.d(TAG, " device update work started: " );

                } else {
                    Log.d(TAG, " device update work failed: " );

                }
            });
        else return Result.failure();
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }


    @Override
    public Single<Boolean> updateDeviceProfile(CommItem commItem) {
        return apiManager.getFCMApi().updateDevice(commItem)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(resultOfAction -> {
                    if (resultOfAction.getIsSuccess()) {
                        return true;
                    }
                    return false;
                });
    }


    @NotNull
    private CommItem getCommItem(DeviceProfile profile) {
        CommItem commItem = new CommItem();
        Header header = new Header();
        header.setDataType(DataType.DEVICE_PROFILE);
        header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        commItem.setHeader(header);

        DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
        deviceProfileItem.setInstanceId(profile.getInstanceId());
        deviceProfileItem.setDeviceId(profile.getDeviceId());
        commItem.setDeviceProfileItem(deviceProfileItem);
        return commItem;
    }
}