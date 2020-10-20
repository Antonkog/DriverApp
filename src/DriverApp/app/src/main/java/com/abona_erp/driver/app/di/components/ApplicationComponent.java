package com.abona_erp.driver.app.di.components;


import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.di.modules.ActivityModule;
import com.abona_erp.driver.app.di.modules.ApplicationModule;
import com.abona_erp.driver.app.di.scopes.ApplicationScope;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.worker.FCMParserWorker;
import com.abona_erp.driver.app.worker.NotifyWorker;

import dagger.Component;

@ApplicationScope
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    ActivityComponent provideActivityComponent(ActivityModule activityModule);

    void inject(App app);// just for now to avoid big merge - in future need to inject where we need.
    void inject(MainActivity mainActivity);
    void inject(NotifyWorker notifyWorker);
    /*void inject(DeviceProfileWorker profileWorker);*/

    void inject(ForegroundAlarmService foregroundAlarmService);
    void inject(FCMParserWorker FCMParserWorker);
}
