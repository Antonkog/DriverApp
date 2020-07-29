package com.abona_erp.driver.app.service;

import com.abona_erp.driver.app.data.model.CommItem;

import io.reactivex.Single;

public interface ProfileUpdater {
    Single<Boolean> updateDeviceProfile(CommItem commItem);
}
