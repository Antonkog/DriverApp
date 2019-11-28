package com.abona_erp.driver.app.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.abona_erp.driver.app.R;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

@SuppressWarnings({"WeakerAccess", "unchecked"})
public class PowerMenuUtils {
  
  public static PowerMenu getProfilePowerMenu(
    Context context,
    LifecycleOwner lifecycleOwner,
    OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener
  ) {
    return new PowerMenu.Builder(context)
      .setHeaderView(R.layout.item_title_header)
      .addItem(new PowerMenuItem("Settings", false))
      .setLifecycleOwner(lifecycleOwner)
      .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
      .setMenuRadius(10f)
      .setMenuShadow(10f)
      .setTextColor(ContextCompat.getColor(context, R.color.clrFont))
      .setTextGravity(Gravity.CENTER)
      .setMenuColor(Color.WHITE)
      .setSelectedEffect(false)
      .setShowBackground(false)
      .setOnMenuItemClickListener(onMenuItemClickListener)
      .build();
  }
}
