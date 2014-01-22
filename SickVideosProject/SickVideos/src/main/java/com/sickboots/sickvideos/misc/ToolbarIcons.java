package com.sickboots.sickvideos.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.sickboots.iconicdroid.IconicFontDrawable;
import com.sickboots.iconicdroid.icon.FontAwesomeIcon;
import com.sickboots.iconicdroid.icon.Icon;
import com.sickboots.sickvideos.R;

public class ToolbarIcons {

  public static enum IconID {SOUND, STEP_FORWARD, STEP_BACK, FULLSCREEN, LIST, CLOSE, OVERFLOW, VIDEO_PLAY, ABOUT, UPLOADS, PLAYLISTS}

  ;

  public static Drawable icon(Context context, IconID iconID, int iconColor, int sizeInDP) {
    Icon icon = null;

    switch (iconID) {
      case SOUND:
        icon = FontAwesomeIcon.VOLUME_UP;
        break;
      case STEP_BACK:
        icon = FontAwesomeIcon.STEP_BACKWARD;
        break;
      case STEP_FORWARD:
        icon = FontAwesomeIcon.STEP_FORWARD;
        break;
      case CLOSE:
        icon = FontAwesomeIcon.TIMES_CIRCLE;
        break;
      case FULLSCREEN:
        icon = FontAwesomeIcon.ARROWS_ALT;
        break;
      case VIDEO_PLAY:
        icon = FontAwesomeIcon.PLAY_CIRCLE_O;
        break;
      case LIST:
        icon = FontAwesomeIcon.LIST_UL;
        break;
      case OVERFLOW:
        icon = FontAwesomeIcon.ELLIPSIS_V;
        break;
      case ABOUT:
        icon = FontAwesomeIcon.HOME;
        break;
      case PLAYLISTS:
        icon = FontAwesomeIcon.FILM;
        break;
      case UPLOADS:
        icon = FontAwesomeIcon.UPLOAD;
        break;
      default:
        break;
    }

    Drawable pressed = null;
    Drawable normal = null;

    if (icon != null) {
      IconicFontDrawable fpressed = new IconicFontDrawable(context);
      fpressed.setIcon(icon);
      fpressed.setIconColor(context.getResources().getColor(R.color.holo_blue));
      fpressed.setContour(Color.GRAY, 1);
      fpressed.setIconPadding(8);

      IconicFontDrawable fnormal = new IconicFontDrawable(context);
      fnormal.setIcon(icon);
      fnormal.setIconColor(iconColor);
      fnormal.setContour(Color.GRAY, 1);
      fnormal.setIconPadding(8);

      int size = (int) Utils.dpToPx(sizeInDP, context);
      fnormal.setIntrinsicWidth(size);
      fnormal.setIntrinsicHeight(size);
      fpressed.setIntrinsicWidth(size);
      fpressed.setIntrinsicHeight(size);

      normal = fnormal;
      pressed = fpressed;
    }

    StateListDrawable states = new StateListDrawable();
    states.addState(new int[]{android.R.attr.state_pressed}, pressed);
    states.addState(new int[]{}, normal);

    return states;
  }

  // this doesn't have the states like above. used to convert an icon to a simple bitmap, assuming things like animations will be faster with a bitmap over a text based drawable
  public static BitmapDrawable iconBitmap(Context context, IconID iconID, int iconColor, int sizeInDP) {
    BitmapDrawable result = null;

    Drawable iconDrawable = icon(context, iconID, iconColor, sizeInDP);

    int size = (int) Utils.dpToPx(sizeInDP, context);

    Bitmap map = Utils.drawableToBitmap(iconDrawable, size);

    return new BitmapDrawable(context.getResources(), map);
  }

}
