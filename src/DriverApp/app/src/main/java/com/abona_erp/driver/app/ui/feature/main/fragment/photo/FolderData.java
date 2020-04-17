package com.abona_erp.driver.app.ui.feature.main.fragment.photo;

public class FolderData {
  
  private int mPreviewImageId;
  private String mPreviewPath;
  private int mCount;
  private int mType = -1;
  private String mDisplayName;
  private String mThumbnailPath;
  
  public void increaseCount() {
    mCount++;
  }
  
  public void decreaseCount() {
    mCount--;
  }
  
  public int getCount() {
    return mCount;
  }
  
  public int getPreviewImageId() {
    return mPreviewImageId;
  }
  
  public void setPreviewImageId(final int previewImageId) {
    mPreviewImageId = previewImageId;
  }
  
  public String getPreviewPath() {
    return mPreviewPath;
  }
  
  public void setPreviewPath(final String previewPath) {
    mPreviewPath = previewPath;
  }
  
  public int getType() {
    return mType;
  }
  
  public String getDisplayName() {
    return mDisplayName;
  }
  
  public void setDisplayName(final String displayName) {
    mDisplayName = displayName;
  }
  
  public void setType(final int type) {
    mType = type;
  }
  
  public String getThumbnailPath() {
    return mThumbnailPath;
  }
  
  public void setThumbnailPath(final String thumbnailPath) {
    mThumbnailPath = thumbnailPath;
  }
}
