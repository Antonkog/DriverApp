package com.abona_erp.driver.app.ui.feature.main;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PageItemDescriptor {
  
  public final int pageItem;
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
    PAGE_BACK,
    PAGE_TASK,
    PAGE_MAP,
    PAGE_CAMERA,
    PAGE_DOCUMENT,
    PAGE_ABOUT,
    PAGE_SETTINGS,
    PAGE_TASK_NOT_FOUND,
    PAGE_DEVICE_REGISTRATED,
    PAGE_NEW_DOCUMENTS,
    PAGE_PROTOCOL,
  })
  public @interface PageItemDef {}
  
  public static final int PAGE_BACK               = 0;
  public static final int PAGE_TASK               = 1;
  public static final int PAGE_MAP                = 2;
  public static final int PAGE_CAMERA             = 3;
  public static final int PAGE_DOCUMENT           = 4;
  public static final int PAGE_ABOUT              = 5;
  public static final int PAGE_SETTINGS           = 6;
  public static final int PAGE_TASK_NOT_FOUND     = 7;
  public static final int PAGE_DEVICE_REGISTRATED = 8;
  public static final int PAGE_NEW_DOCUMENTS      = 9;
  public static final int PAGE_PROTOCOL           = 10;
  
  public PageItemDescriptor(@PageItemDef int pageItem) {
    this.pageItem = pageItem;
  }
}
