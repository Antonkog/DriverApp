package com.abona_erp.driver.app.ui.event;

public class PatchEvent implements BaseEvent {
  
  private String patchText;
  private int randomNumber;
  private boolean visibility;
  
  public PatchEvent(int randomNumber, String patchText, boolean visibility) {
    this.randomNumber = randomNumber;
    this.visibility = visibility;
    this.patchText = patchText;
  }
  
  public int getRandomNumber() {
    return randomNumber;
  }
  
  public void setRandomNumber(int randomNumber) {
    this.randomNumber = randomNumber;
  }
  
  public String getPatchText() {
    return patchText;
  }
  
  public void setPatchText(String patchText) {
    this.patchText = patchText;
  }
  
  public boolean getVisibility() {
    return visibility;
  }
  
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }
}
