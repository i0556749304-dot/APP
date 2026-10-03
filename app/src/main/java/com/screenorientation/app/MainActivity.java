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
    private final int primary = Color.rgb(36,88,255);
    private final int ink = Color.rgb(22,25,31);
    private final int muted = Color.rgb(95,100,110);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(247,248,252));
        getWindow().setNavigationBarColor(Color.rgb(247,248,252));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildUi();
    }

    @Override protected void onResume() { super.onResume(); if(content!=null) refresh(); }

    private void buildUi(){
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20),dp(22),dp(20),dp(30));
        scroll.addView(content);
        setContentView(scroll);
        refresh();
    }

    private void refresh(){
        content.removeAllViews();
        TextView title=text("כיוון מסך",30,ink,true);
        content.addView(title,lp(-1,-2));
        TextView sub=text("פשוט. ברור. בשליטה שלך.",16,muted,false);
        addTop(sub,4);

        LinearLayout current=card();
        current.addView(icon(R.drawable.ic_auto,40),lp(-1,dp(40)));
        TextView ct=text("מצב נוכחי",13,muted,true); addTop(current,18); current.addView(ct,lp(-1,-2));
        TextView cm=text(OrientationUtils.currentLabel(this),24,ink,true); addTop(current,6); current.addView(cm,lp(-1,-2));
        TextView hint=text("שנה את הכיוון בלחיצה אחת",13,muted,false); addTop(current,3); current.addView(hint,lp(-1,-2));

        TextView choose=text("בחר כיוון",19,ink,true); addTop(choose,24);
        addChoice("אוטומטי","לפי חיישן התנועה",R.drawable.ic_auto,OrientationUtils.AUTO);
        addChoice("אנכי","רגיל לאורך",R.drawable.ic_portrait,OrientationUtils.PORTRAIT);
        addChoice("אופקי","רגיל לרוחב",R.drawable.ic_landscape,OrientationUtils.LANDSCAPE);
        addChoice("אנכי הפוך","לאורך כשהמכשיר הפוך",R.drawable.ic_portrait,OrientationUtils.PORTRAIT_REVERSE);
        addChoice("אופקי הפוך","לרוחב כשהמכשיר הפוך",R.drawable.ic_landscape,OrientationUtils.LANDSCAPE_REVERSE);

        TextView tileTitle=text("הגדרת כפתור ההגדרות המהירות",19,ink,true); addTop(tileTitle,26);
        TextView tileSub=text("בחר מה יקרה בכל לחיצה על הכפתור",13,muted,false); addTop(tileSub,4);
        addTileMode(OrientationUtils.TILE_TOGGLE,"אנכי ↔ אופקי","לחיצה מחליפה בין כיוון רגיל לאורך ולרוחב",R.drawable.ic_landscape);
        addTileMode(OrientationUtils.TILE_ALL,"כל האפשרויות","אוטומטי → אנכי → אופקי → הפוך → חזרה",R.drawable.ic_auto);

        if(!Settings.System.canWrite(this)){
            TextView permission=text("נדרשת הרשאת מערכת לשינוי כיוון נעול",14,primary,true);
            addTop(permission,18);
            permission.setOnClickListener(v->requestWritePermission());
        }

        addCredits();
    }

    private void addChoice(String title,String desc,int iconRes,final int mode){
        LinearLayout row=card(); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(16),dp(14),dp(16),dp(14));
        ImageView iv=icon(iconRes,34); row.addView(iv,lp(dp(42),dp(42)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setGravity(Gravity.RIGHT);
        TextView t=text(title,16,ink,true); words.addView(t,lp(-1,-2));
        TextView d=text(desc,12,muted,false); addTop(words,3); words.addView(d,lp(-1,-2));
        LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(0,-2,1); wp.setMargins(dp(12),0,0,0); row.addView(words,wp);
        row.setOnClickListener(v->apply(mode));
        addTop(row,9);
    }

    private void addTileMode(String title,String desc,int iconRes,final int mode){
        final boolean selected=OrientationUtils.getTileMode(this)==mode;
        LinearLayout row=card(); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16),dp(15),dp(16),dp(15));
        ImageView iv=icon(iconRes,32); row.addView(iv,lp(dp(40),dp(40)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setGravity(Gravity.RIGHT);
        TextView t=text(title,16,ink,true); words.addView(t,lp(-1,-2));
        TextView d=text(desc,12,muted,false); addTop(words,3); words.addView(d,lp(-1,-2));
        LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(0,-2,1); wp.setMargins(dp(12),0,0,0); row.addView(words,wp);
        TextView mark=text(selected?"✓":"",22,primary,true); mark.setGravity(Gravity.CENTER); row.addView(mark,lp(dp(28),dp(28)));
        row.setOnClickListener(v->{OrientationUtils.setTileMode(this,mode); refresh();});
        addTop(row,9);
    }

    private void addCredits(){
        LinearLayout credit=card(); credit.setGravity(Gravity.CENTER); addTop(credit,26);
        TextView made=text("פותח באהבה עבורכם",13,muted,false); made.setGravity(Gravity.CENTER); credit.addView(made,lp(-1,-2));
        TextView cyber=text("הסייבריסט",18,primary,true); cyber.setGravity(Gravity.CENTER); addTop(credit,4); credit.addView(cyber,lp(-1,-2));
        cyber.setOnClickListener(v->open("https://mitmachim.top/user/%D7%94%D7%A1%D7%99%D7%99%D7%91%D7%A8%D7%99%D7%A1%D7%98"));
        TextView source=text("קוד המקור ב-GitHub",12,muted,false); source.setGravity(Gravity.CENTER); addTop(credit,8); credit.addView(source,lp(-1,-2));
        source.setOnClickListener(v->open("https://github.com/i0556749304-dot/APP"));
    }

    private void apply(int mode){
        if(mode!=OrientationUtils.AUTO && !Settings.System.canWrite(this)){ requestWritePermission(); return; }
        if(OrientationUtils.apply(this,mode)) refresh();
    }

    private void requestWritePermission(){
        try{ startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()))); }
        catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }

    private void open(String url){
        try{ startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); }catch(Exception ignored){}
    }

    private LinearLayout card(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(18),dp(16),dp(18),dp(16));
        GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(18)); bg.setStroke(dp(1),Color.rgb(232,234,240)); l.setBackground(bg); return l;
    }
    private ImageView icon(int res,int size){ ImageView i=new ImageView(this); i.setImageResource(res); i.setScaleType(ImageView.ScaleType.CENTER_INSIDE); return i; }
    private TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.RIGHT); t.setTextDirection(View.TEXT_DIRECTION_RTL); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private void addTop(View v,int margin){ LinearLayout.LayoutParams p=lp(-1,-2); p.topMargin=dp(margin); content.addView(v,p); }
    private void addTop(LinearLayout parent, int margin){ LinearLayout.LayoutParams p=lp(-1,-2); p.topMargin=dp(margin); parent.addView(new View(this),p); }
    private LinearLayout.LayoutParams lp(int w,int h){ return new LinearLayout.LayoutParams(w,h); }
    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
}
