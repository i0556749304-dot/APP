package com.screenorientation.app;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.graphics.drawable.Icon;

public class OrientationTileService extends TileService {
    @Override public void onStartListening(){ super.onStartListening(); updateTile(); }
    @Override public void onClick(){
        super.onClick();
        int next=OrientationUtils.nextTileMode(this);
        if(next!=OrientationUtils.AUTO && !Settings.System.canWrite(this)){
            try { Intent i=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:"+getPackageName())); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivityAndCollapse(i); } catch(Exception ignored) {}
            return;
        }
        if(OrientationUtils.apply(this,next)) updateTile();
    }
    private void updateTile(){
        Tile t=getQsTile(); if(t==null)return;
        int mode=OrientationUtils.current(this);
        t.setLabel(OrientationUtils.label(mode));
        t.setIcon(Icon.createWithResource(this, iconFor(mode)));
        t.setState(mode==OrientationUtils.AUTO?Tile.STATE_INACTIVE:Tile.STATE_ACTIVE);
        t.updateTile();
    }
    private int iconFor(int mode){
        switch(mode){
            case OrientationUtils.LANDSCAPE:
            case OrientationUtils.LANDSCAPE_REVERSE:return com.screenorientation.app.R.drawable.ic_landscape;
            case OrientationUtils.PORTRAIT:
            case OrientationUtils.PORTRAIT_REVERSE:return com.screenorientation.app.R.drawable.ic_portrait;
            default:return com.screenorientation.app.R.drawable.ic_auto;
        }
    }
}
