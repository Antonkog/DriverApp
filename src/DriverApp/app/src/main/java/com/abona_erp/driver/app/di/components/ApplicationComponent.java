package com.abona_erp.driver.app.di.components;


import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.di.modules.ActivityModule;
import com.abona_erp.driver.app.di.modules.ApplicationModule;
import com.abona_erp.driver.app.di.scopes.ApplicationScope;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.FcmService;
import com.abona_erp.driver.app.service.NotificationService;
import com.abona_erp.driver.app.ui.feature.main.CompletedFragment;
import com.abona_erp.driver.app.ui.feature.main.NotifyViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.adapter.CommItemAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.DocumentViewAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.GalleryViewAdapter;

import dagger.Component;

@ApplicationScope
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    ActivityComponent provideActivityComponent(ActivityModule activityModule);

    void inject(DriverRepository driverRepository);
    void inject(BackgroundServiceWorker backgroundServiceWorker);

    void inject(DocumentViewAdapter documentViewAdapter);
    void inject(NotifyViewAdapter notifyViewAdapter);
    void inject(GalleryViewAdapter galleryViewAdapter);
    void inject(CommItemAdapter commItemAdapter);

    void inject(FcmService service);
    void inject(NotificationService notificationService);

    void inject(CompletedFragment completedFragment);
    void inject(DetailFragment detailFragment);
    void inject(PhotoFragment photoFragment);
}
