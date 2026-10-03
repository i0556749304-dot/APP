package com.screenorientation.app;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class OrientationTileService extends TileService {
    @Override public void onStartListening(){ super.onStartListening(); updateTile(); }
    @Override public void onClick(){
        super.onClick();
        int current=OrientationUtils.current(this);
        int next;
        switch(current){case OrientationUtils.AUTO:next=OrientationUtils.PORTRAIT;break;case OrientationUtils.PORTRAIT:next=OrientationUtils.LANDSCAPE;break;case OrientationUtils.LANDSCAPE:next=OrientationUtils.PORTRAIT_REVERSE;break;case OrientationUtils.PORTRAIT_REVERSE:next=OrientationUtils.LANDSCAPE_REVERSE;break;default:next=OrientationUtils.AUTO;}
        if(next!=OrientationUtils.AUTO && !Settings.System.canWrite(this)){
            try { Intent i=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:"+getPackageName())); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivityAndCollapse(i); } catch(Exception ignored) {}
            return;
        }
        OrientationUtils.apply(this,next); updateTile();
    }
    private void updateTile(){ Tile t=getQsTile(); if(t==null)return; int mode=OrientationUtils.current(this); t.setLabel(OrientationUtils.label(mode)); t.setState(mode==OrientationUtils.AUTO?Tile.STATE_INACTIVE:Tile.STATE_ACTIVE); t.updateTile(); }
}
