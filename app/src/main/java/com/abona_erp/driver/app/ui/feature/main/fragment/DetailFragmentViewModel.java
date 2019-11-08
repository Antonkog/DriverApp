package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import io.reactivex.Single;

public class DetailFragmentViewModel extends AndroidViewModel {
  
  private DriverRepository mRepository;
  
  public DetailFragmentViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
  }
  
  Single<Notify> getNotifyById(int id) {
    return mRepository.getNotifyById(id);
  }
  
  void update(Notify notify) {
    mRepository.update(notify);
  }
  
  void delete(Notify notify) {
    mRepository.delete(notify);
  }
}
