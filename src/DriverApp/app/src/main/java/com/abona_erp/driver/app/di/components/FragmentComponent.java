package com.abona_erp.driver.app.di.components;


import com.abona_erp.driver.app.di.modules.FragmentModule;
import com.abona_erp.driver.app.di.scopes.FragmentScope;
import com.abona_erp.driver.app.ui.feature.main.CMRFragment;
import com.abona_erp.driver.app.ui.feature.main.CompletedFragment;
import com.abona_erp.driver.app.ui.feature.main.PendingFragment;
import com.abona_erp.driver.app.ui.feature.main.RunningFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.about.SoftwareAboutFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.document_viewer.DocumentViewerFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.MapFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageCameraFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageEditFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageGalleryFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment.ImageSettingsFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.workers.CacheRetainer;
import com.abona_erp.driver.app.ui.feature.main.fragment.protocol.ProtocolFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.registration.DeviceNotRegistratedFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.settings.SettingsFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(DetailFragment fragment);
    void inject(CompletedFragment fragment);
    void inject(MainFragment fragment);
    void inject(CMRFragment fragment);
    void inject(RunningFragment fragment);
    void inject(PendingFragment fragment);
    void inject(SoftwareAboutFragment fragment);
    void inject(DocumentViewerFragment fragment);
    void inject(MapFragment fragment);
    void inject(ImageCameraFragment fragment);
    void inject(ImageGalleryFragment fragment);
    void inject(ImageSettingsFragment fragment);
    void inject(ImageEditFragment fragment);
    void inject(PhotoFragment fragment);
    void inject(CacheRetainer fragment);
    void inject(ProtocolFragment fragment);
    void inject(DeviceNotRegistratedFragment fragment);
    void inject(SettingsFragment fragment);

}
