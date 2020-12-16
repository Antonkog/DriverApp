package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpecialActivities {
  
  @SerializedName("SpecialFunction")
  @Expose
  private SpecialFunction specialFunction;
  
  @SerializedName("SpecialFunctionOperationType")
  @Expose
  private SpecialFunctionOperationType specialFunctionOperationType;
  
  @SerializedName("SpecialActivityResults")
  @Expose
  private List<SpecialActivityResult> specialActivityResults;
  
  public SpecialFunction getSpecialFunction() {
    return specialFunction;
  }
  
  public void setSpecialFunction(SpecialFunction specialFunction) {
    this.specialFunction = specialFunction;
  }
  
  public SpecialFunctionOperationType getSpecialFunctionOperationType() {
    return specialFunctionOperationType;
  }
  
  public void setSpecialFunctionOperationType(SpecialFunctionOperationType specialFunctionOperationType) {
    this.specialFunctionOperationType = specialFunctionOperationType;
  }
  
  public List<SpecialActivityResult> getSpecialActivityResults() {
    return specialActivityResults;
  }
  
  public void setSpecialActivityResults(List<SpecialActivityResult> specialActivityResults) {
    this.specialActivityResults = specialActivityResults;
  }
}
