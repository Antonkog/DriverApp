package com.abona_erp.driver.app.ui.event;

import java.util.ArrayList;

public class ExtImageEvent implements BaseEvent {
  
  private ArrayList<String> mPhotos;
  
  public ExtImageEvent(ArrayList<String> photos) {
    mPhotos = photos;
  }
  
  public ArrayList<String> getPhotos() {
    return mPhotos;
  }
  
  public void setPhotos(ArrayList<String> photos) {
    this.mPhotos = photos;
  }
}
