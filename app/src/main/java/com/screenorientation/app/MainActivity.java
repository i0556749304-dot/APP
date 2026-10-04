package com.screenorientation.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    private LinearLayout content;
    private final int blue = Color.rgb(20, 104, 232);
    private final int blueDark = Color.rgb(12, 57, 132);
    private final int blueSoft = Color.rgb(82, 126, 190);
    private final int blueLine = Color.rgb(155, 211, 255);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(247, 251, 255));
        getWindow().setNavigationBarColor(Color.rgb(247, 251, 255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildUi();
    }
    @Override protected void onResume() { super.onResume(); if(content!=null) refresh(); }

    private void buildUi(){
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18),dp(18),dp(18),dp(30));
        scroll.addView(content); setContentView(scroll); refresh();
    }

    private void refresh(){
        content.removeAllViews(); addHeader();
        int currentMode=OrientationUtils.current(this);
        LinearLayout current=card(); addTop(current,16); current.setGravity(Gravity.CENTER_HORIZONTAL);
        current.addView(icon(iconForMode(currentMode),50),lp(dp(56),dp(56)));
        TextView ct=text("מצב נוכחי",13,blueSoft,false); addTopTo(current,6); current.addView(ct,lp(-1,-2));
        TextView cm=text(OrientationUtils.currentLabel(this),24,blueDark,true); addTopTo(current,3); current.addView(cm,lp(-1,-2));
        TextView hint=text("שנה את כיוון התצוגה בלחיצה אחת",13,blueSoft,false); addTopTo(current,3); current.addView(hint,lp(-1,-2));

        TextView choose=text("בחר כיוון",20,blue,true); addTop(choose,24);
        addChoice("אוטומטי","המכשיר מתאים את התצוגה לתנועה",R.drawable.ic_auto,OrientationUtils.AUTO);
        addChoice("אנכי","המכשיר עומד לאורך",R.drawable.ic_portrait,OrientationUtils.PORTRAIT);
        addChoice("אופקי","המכשיר שוכב לרוחב",R.drawable.ic_landscape,OrientationUtils.LANDSCAPE);
        addChoice("אנכי הפוך","המכשיר לאורך כשהצד ההפוך למעלה",R.drawable.ic_portrait_reverse,OrientationUtils.PORTRAIT_REVERSE);
        addChoice("אופקי הפוך","המכשיר לרוחב כשהצד ההפוך למעלה",R.drawable.ic_landscape_reverse,OrientationUtils.LANDSCAPE_REVERSE);

        TextView tileTitle=text("הגדרת כפתור ההגדרות המהירות",19,blue,true); addTop(tileTitle,26);
        TextView tileSub=text("בחר מה יקרה בכל לחיצה על האריח",13,blueSoft,false); addTop(tileSub,4);
        addTileMode("אנכי ↔ אופקי","לחיצה מחליפה בין לאורך לרוחב",R.drawable.ic_toggle,OrientationUtils.TILE_TOGGLE);
        addTileMode("כל האפשרויות","מעבר בין כל מצבי התצוגה",R.drawable.ic_cycle,OrientationUtils.TILE_ALL);

        if(!Settings.System.canWrite(this)){
            LinearLayout permission=card();
            TextView p=text("נדרשת הרשאת מערכת",14,blue,true); p.setGravity(Gravity.CENTER); permission.addView(p,lp(-1,-2));
            TextView ps=text("לחץ כאן כדי לאפשר לאפליקציה לשנות את כיוון המסך",12,blueSoft,false); ps.setGravity(Gravity.CENTER); addTopTo(permission,4); permission.addView(ps,lp(-1,-2));
            permission.setOnClickListener(v->requestWritePermission()); addTop(permission,18);
        }
        addCredits();
    }

    private void addHeader(){
        LinearLayout header=new LinearLayout(this); header.setOrientation(LinearLayout.HORIZONTAL); header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18),dp(16),dp(18),dp(16));
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(10,94,218),Color.rgb(27,130,239)});
        bg.setCornerRadius(dp(22)); header.setBackground(bg);
        ImageView iv=icon(R.drawable.ic_launcher,54); header.addView(iv,lp(dp(58),dp(58)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setGravity(Gravity.RIGHT);
        TextView t=text("כיוון מסך",27,Color.WHITE,true); words.addView(t,lp(-1,-2));
        TextView s=text("שליטה חכמה בכיוון התצוגה",13,Color.rgb(225,242,255),false); addTopTo(words,2); words.addView(s,lp(-1,-2));
        LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(0,-2,1); wp.setMargins(dp(12),0,0,0); header.addView(words,wp); addTop(header,0);
    }

    private int iconForMode(int mode){
        switch(mode){ case OrientationUtils.PORTRAIT:return R.drawable.ic_portrait; case OrientationUtils.LANDSCAPE:return R.drawable.ic_landscape; case OrientationUtils.PORTRAIT_REVERSE:return R.drawable.ic_portrait_reverse; case OrientationUtils.LANDSCAPE_REVERSE:return R.drawable.ic_landscape_reverse; default:return R.drawable.ic_auto; }
    }

    private void addChoice(String title,String desc,int iconRes,final int mode){
        LinearLayout row=card(); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(12),dp(10),dp(14),dp(10));
        ImageView iv=icon(iconRes,46); row.addView(iv,lp(dp(54),dp(54)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setGravity(Gravity.RIGHT);
        TextView t=text(title,16,blueDark,true); words.addView(t,lp(-1,-2));
        TextView d=text(desc,12,blueSoft,false); addTopTo(words,3); words.addView(d,lp(-1,-2));
        LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(0,-2,1); wp.setMargins(dp(12),0,0,0); row.addView(words,wp);
        row.setOnClickListener(v->apply(mode)); addTop(row,8);
    }

    private void addTileMode(String title,String desc,int iconRes,final int mode){
        final boolean selected=OrientationUtils.getTileMode(this)==mode;
        LinearLayout row=card(); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(12),dp(11),dp(14),dp(11));
        ImageView iv=icon(iconRes,42); row.addView(iv,lp(dp(50),dp(50)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setGravity(Gravity.RIGHT);
        TextView t=text(title,16,blueDark,true); words.addView(t,lp(-1,-2));
        TextView d=text(desc,12,blueSoft,false); addTopTo(words,3); words.addView(d,lp(-1,-2));
        LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(0,-2,1); wp.setMargins(dp(12),0,0,0); row.addView(words,wp);
        TextView mark=text(selected?"✓":"",22,blue,true); mark.setGravity(Gravity.CENTER); row.addView(mark,lp(dp(28),dp(28)));
        row.setOnClickListener(v->{OrientationUtils.setTileMode(this,mode); refresh();}); addTop(row,8);
    }

    private void addCredits(){
        LinearLayout credit=card(); credit.setGravity(Gravity.CENTER); addTop(credit,24);
        TextView made=text("פותח ע\"י",11,blueSoft,false); made.setGravity(Gravity.CENTER); credit.addView(made,lp(-1,-2));
        TextView cyber=text("הסייבריסט",14,blue,false); cyber.setGravity(Gravity.CENTER); addTopTo(credit,2); credit.addView(cyber,lp(-1,-2));
        cyber.setOnClickListener(v->open("https://mitmachim.top/user/%D7%94%D7%A1%D7%99%D7%99%D7%91%D7%A8%D7%99%D7%A1%D7%98"));
        TextView forum=text("פורום מתמחים טופ",10,blueSoft,false); forum.setGravity(Gravity.CENTER); addTopTo(credit,1); credit.addView(forum,lp(-1,-2));
        forum.setOnClickListener(v->open("https://mitmachim.top/user/%D7%94%D7%A1%D7%99%D7%99%D7%A8%D7%99%D7%A1%D7%98"));
        TextView rights=text("© 2026 · כל הזכויות שמורות",9,blueSoft,false); rights.setGravity(Gravity.CENTER); addTopTo(credit,7); credit.addView(rights,lp(-1,-2));
        TextView source=text("קוד המקור ב-GitHub",10,blueSoft,false); source.setGravity(Gravity.CENTER); addTopTo(credit,6); credit.addView(source,lp(-1,-2));
        source.setOnClickListener(v->open("https://github.com/i0556749304-dot/APP"));
    }

    private void apply(int mode){ if(mode!=OrientationUtils.AUTO && !Settings.System.canWrite(this)){ requestWritePermission(); return; } if(OrientationUtils.apply(this,mode)) refresh(); }
    private void requestWritePermission(){ try{ startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()))); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); } }
    private void open(String url){ try{ startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); }catch(Exception ignored){} }
    private LinearLayout card(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(18),dp(16),dp(18),dp(16)); GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(18)); bg.setStroke(dp(1),blueLine); l.setBackground(bg); return l; }
    private ImageView icon(int res,int size){ ImageView i=new ImageView(this); i.setImageResource(res); i.setScaleType(ImageView.ScaleType.CENTER_INSIDE); return i; }
    private TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.RIGHT); t.setTextDirection(View.TEXT_DIRECTION_RTL); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private void addTop(View v,int margin){ LinearLayout.LayoutParams p=lp(-1,-2); p.topMargin=dp(margin); content.addView(v,p); }
    private void addTopTo(LinearLayout parent,int margin){ View spacer=new View(this); LinearLayout.LayoutParams p=lp(-1,dp(margin)); parent.addView(spacer,p); }
    private LinearLayout.LayoutParams lp(int w,int h){ return new LinearLayout.LayoutParams(w,h); }
    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
}
