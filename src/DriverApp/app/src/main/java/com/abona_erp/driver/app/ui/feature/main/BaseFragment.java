package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.di.components.ActivityComponent;
import com.abona_erp.driver.app.di.components.FragmentComponent;
import com.abona_erp.driver.app.di.modules.FragmentModule;


public abstract class BaseFragment extends Fragment {

    private FragmentComponent fragmentComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent = getActivityComponent()
                .providesFragmentComponent(new FragmentModule(this));
        injectDependencies();
    }

    public abstract void injectDependencies();

    private ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent();
    }

    public void onBackPressed() {}

    public FragmentComponent getFragmentComponent() {
        return fragmentComponent;
    }
}
