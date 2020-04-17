package com.abona_erp.driver.app.ui.event;

public class ProfileEvent implements BaseEvent {
  
  private String imageUrl;
  
  public ProfileEvent() {
  }
  
  public ProfileEvent(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
}
