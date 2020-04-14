package com.abona_erp.driver.app.util;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.core.base.ContextUtils;

public class RingtoneUtils implements MediaPlayer.OnPreparedListener {
  
  private static final String TAG = RingtoneUtils.class.getSimpleName();
  
  private MediaPlayer _mediaPlayer;
  
  public RingtoneUtils() {
    _mediaPlayer = new MediaPlayer();
    setRingtonePlayer();
  }
  
  public synchronized void playNotificationTone() {
  
    Uri ringtoneUri = RingtoneManager
      .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    
    try {
      _mediaPlayer.reset();
      _mediaPlayer.setDataSource(ContextUtils.getApplicationContext(),
        ringtoneUri);
      _mediaPlayer.prepareAsync();
      _mediaPlayer.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener() {
        
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
          _mediaPlayer.stop();
          _mediaPlayer.release();
          _mediaPlayer = null;
        }
      });
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }
  
  private void setRingtonePlayer() {
    _mediaPlayer.setWakeMode(ContextUtils.getApplicationContext(),
      PowerManager.PARTIAL_WAKE_LOCK);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      _mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build());
    } else {
      _mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
    }
    _mediaPlayer.setOnPreparedListener(this);
  }
  
  @Override
  public void onPrepared(MediaPlayer mediaPlayer) {
    mediaPlayer.start();
  }
}
