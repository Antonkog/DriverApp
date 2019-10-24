package com.abona_erp.driver.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notify {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  private String data;
  private boolean selected;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public boolean getSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
