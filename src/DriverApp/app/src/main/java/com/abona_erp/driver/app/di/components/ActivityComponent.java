package com.abona_erp.driver.app.di.components;

import com.abona_erp.driver.app.di.modules.ActivityModule;
import com.abona_erp.driver.app.di.modules.FragmentModule;
import com.abona_erp.driver.app.di.scopes.ActivityScope;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.ui.feature.login.LoginActivity;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter.DocumentViewAdapter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    FragmentComponent providesFragmentComponent(FragmentModule fragmentModule);

    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(DocumentViewAdapter documentViewAdapter);
    void inject(BackgroundServiceWorker backgroundServiceWorker);
    void inject(PhotoFragment photoFragment);
}
