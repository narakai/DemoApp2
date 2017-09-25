package com.clem.ipocadonation1.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.color.CircleView;
import com.clem.ipocadonation1.core.preferences.UserPreferences;

/**
 * Creator: vbarad
 * Date: 2016-12-03
 * Project: AntennaPod
 */

public class SplashActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    int primaryPreselect = UserPreferences.getPrefColor();
    ColorDrawable drawable = new ColorDrawable(primaryPreselect);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setBackgroundDrawable(drawable);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(CircleView.shiftColorDown(primaryPreselect));
      getWindow().setNavigationBarColor(primaryPreselect);
    }
    finish();
  }
}
