package com.abona_erp.driver.app.ui.event;

import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;

public class PageEvent implements BaseEvent {
  
  private final Notify mNotify;
  private final PageItemDescriptor mPageItem;
  private int documentOrderNo;

  public PageEvent(PageItemDescriptor pageItem, Notify notify) {
    this.mPageItem = pageItem;
    this.mNotify   = notify;
  }
  public PageEvent  addDocumentOrderNo(int documentOrderNo) {
    this.documentOrderNo = documentOrderNo;
    return this;
  }
  
  public PageItemDescriptor getPageItem() {
    return mPageItem;
  }
  
  public Notify getNotify() {
    return mNotify;
  }

  public int getDocumentOrderNo() {
    return documentOrderNo;
  }
}
