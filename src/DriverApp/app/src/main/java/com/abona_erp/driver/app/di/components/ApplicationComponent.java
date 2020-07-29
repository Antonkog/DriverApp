package com.abona_erp.driver.app.di.components;


import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.di.modules.ActivityModule;
import com.abona_erp.driver.app.di.modules.ApplicationModule;
import com.abona_erp.driver.app.di.scopes.ApplicationScope;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.FcmService;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.feature.main.CompletedFragment;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.ui.feature.main.NotifyViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommItemAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.DocumentViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;
import com.abona_erp.driver.app.worker.DeviceProfileWorker;
import com.abona_erp.driver.app.worker.NotifyWorker;

import dagger.Component;

@ApplicationScope
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    ActivityComponent provideActivityComponent(ActivityModule activityModule);

    void inject(App app);// just for now to avoid big merge - in future need to inject where we need.
    void inject(MainActivity mainActivity);

    void inject(DriverRepository driverRepository);
    void inject(BackgroundServiceWorker backgroundServiceWorker);

    void inject(DocumentViewAdapter documentViewAdapter);
    void inject(NotifyViewAdapter notifyViewAdapter);
    void inject(GalleryViewAdapter galleryViewAdapter);
    void inject(CommItemAdapter commItemAdapter);

    void inject(FcmService service);
    void inject(ForegroundAlarmService foregroundAlarmService);
    void inject(NotifyWorker notifyWorker); //new api
    void inject(DeviceProfileWorker profileWorker); //new api

    void inject(CompletedFragment completedFragment);
    void inject(DetailFragment detailFragment);
    void inject(PhotoFragment photoFragment);
}
