package com.abona_erp.driver.app.ui.event;

public class ImageEvent implements BaseEvent {
  
  private String photoUrl;
  private int position;
  
  public ImageEvent(String photoUrl) {
    this.photoUrl = photoUrl;
  }
  
  public String getPhotoUrl() {
    return photoUrl;
  }
  
  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
  
  public int getPosition() {
    return position;
  }
  
  public void setPosition(int position) {
    this.position = position;
  }
}
