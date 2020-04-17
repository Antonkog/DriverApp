package com.abona_erp.driver.app.ui.widget.badges;

public class Badge {
  
  private Integer number;
  private BadgeSpan span;
  private String badgeText;
  private boolean isActual;
  
  private static final String BADGE_WHITESPACE = " ";
  private static final String TEXT_LIMIT = "99+";
  
  public Badge(Integer number, BadgeSpan badgeSpan) {
    this.number = number;
    this.span = badgeSpan;
    
    isActual = false;
    if (number != null && number > 0) {
      badgeText = BADGE_WHITESPACE +
        (number < 10 ? BADGE_WHITESPACE : "") +
        (number < 100 ? String.valueOf(number) : TEXT_LIMIT) +
        (number < 10 ? BADGE_WHITESPACE : "") +
        BADGE_WHITESPACE;
      isActual = true;
    }
  }
  
  public Integer getNumber() {
    return number;
  }
  
  public BadgeSpan getSpan() {
    return span;
  }
  
  public String getBadgeText() {
    return badgeText;
  }
  
  public boolean isActual() {
    return isActual;
  }
}
