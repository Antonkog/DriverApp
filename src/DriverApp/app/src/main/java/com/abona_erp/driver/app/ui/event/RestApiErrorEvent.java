package com.abona_erp.driver.app.ui.event;

public class RestApiErrorEvent implements BaseEvent {
  
  private String message;
  
  public RestApiErrorEvent() {
  }
  
  public RestApiErrorEvent(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
}
