package com.screenorientation.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.Surface;

public final class OrientationUtils {
    public static final int AUTO=0, PORTRAIT=1, LANDSCAPE=2, PORTRAIT_REVERSE=3, LANDSCAPE_REVERSE=4;
    public static final int TILE_TOGGLE=0, TILE_ALL=1;
    private static final String PREFS="orientation_prefs";
    private static final String KEY_TILE_MODE="tile_mode";
    private OrientationUtils() {}

    public static boolean apply(Context c,int mode){
        try {
            if(mode==AUTO){
                return Settings.System.putInt(c.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,1);
            }
            if(!Settings.System.canWrite(c)) return false;
            int rotation=Surface.ROTATION_0;
            if(mode==LANDSCAPE) rotation=Surface.ROTATION_90;
            else if(mode==PORTRAIT_REVERSE) rotation=Surface.ROTATION_180;
            else if(mode==LANDSCAPE_REVERSE) rotation=Surface.ROTATION_270;
            Settings.System.putInt(c.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,0);
            return Settings.System.putInt(c.getContentResolver(),Settings.System.USER_ROTATION,rotation);
        } catch(Exception e){ return false; }
    }

    public static int current(Context c){
        try {
            int auto=Settings.System.getInt(c.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,1);
            if(auto==1) return AUTO;
            int r=Settings.System.getInt(c.getContentResolver(),Settings.System.USER_ROTATION,Surface.ROTATION_0);
            switch(r){case Surface.ROTATION_90:return LANDSCAPE;case Surface.ROTATION_180:return PORTRAIT_REVERSE;case Surface.ROTATION_270:return LANDSCAPE_REVERSE;default:return PORTRAIT;}
        } catch(Exception e){return AUTO;}
    }

    public static String label(int mode){
        switch(mode){case PORTRAIT:return "אנכי";case LANDSCAPE:return "אופקי";case PORTRAIT_REVERSE:return "אנכי הפוך";case LANDSCAPE_REVERSE:return "אופקי הפוך";default:return "אוטומטי";}
    }
    public static String currentLabel(Context c){return label(current(c));}

    public static int getTileMode(Context c){
        return c.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getInt(KEY_TILE_MODE,TILE_TOGGLE);
    }
    public static void setTileMode(Context c,int mode){
        c.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putInt(KEY_TILE_MODE,mode).apply();
    }

    public static int nextTileMode(Context c){
        int current=current(c);
        if(getTileMode(c)==TILE_TOGGLE){
            return current==LANDSCAPE ? PORTRAIT : LANDSCAPE;
        }
        switch(current){
            case AUTO:return PORTRAIT;
            case PORTRAIT:return LANDSCAPE;
            case LANDSCAPE:return PORTRAIT_REVERSE;
            case PORTRAIT_REVERSE:return LANDSCAPE_REVERSE;
            default:return AUTO;
        }
    }
}
