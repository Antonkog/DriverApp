package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotesItem {
  
  @SerializedName("NoteType")
  @Expose
  private EnumNoteType noteType;
  
  @SerializedName("Note")
  @Expose
  private String note;
  
  public EnumNoteType getNoteType() {
    return noteType;
  }
  
  public void setNoteType(EnumNoteType noteType) {
    this.noteType = noteType;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
}
