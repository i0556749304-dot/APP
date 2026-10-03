package com.screenorientation.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends android.app.Activity {
    private LinearLayout content;
    private int primary = Color.rgb(36, 88, 255);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(247, 248, 252));
        getWindow().setNavigationBarColor(Color.rgb(247, 248, 252));
        buildUi();
    }

    @Override protected void onResume() { super.onResume(); if (content != null) refresh(); }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(24), dp(22), dp(28));
        scroll.addView(content);
        setContentView(scroll);
        refresh();
    }

    private void refresh() {
        content.removeAllViews();
        TextView title = text("כיוון מסך", 32, Color.rgb(22,25,31), true);
        content.addView(title, lp(-1, -2));
        TextView sub = text("שליטה מהירה וברורה בכיוון התצוגה", 16, Color.rgb(95,100,110), false);
        LinearLayout.LayoutParams sp = lp(-1,-2); sp.topMargin=dp(5); content.addView(sub, sp);

        LinearLayout status = card();
        TextView statusTitle = text("מצב נוכחי", 14, Color.rgb(105,110,120), true);
        status.addView(statusTitle, lp(-1,-2));
        TextView mode = text(OrientationUtils.currentLabel(this), 24, Color.rgb(22,25,31), true);
        LinearLayout.LayoutParams mp=lp(-1,-2); mp.topMargin=dp(8); status.addView(mode,mp);
        LinearLayout.LayoutParams cp=lp(-1,-2); cp.topMargin=dp(22); content.addView(status,cp);

        TextView choose = text("בחר כיוון", 19, Color.rgb(22,25,31), true);
        LinearLayout.LayoutParams ch=lp(-1,-2); ch.topMargin=dp(26); content.addView(choose,ch);

        addChoice("אוטומטי", "המסך מסתובב לפי חיישן התנועה", OrientationUtils.AUTO);
        addChoice("אנכי", "כיוון רגיל לאורך", OrientationUtils.PORTRAIT);
        addChoice("אופקי", "כיוון לרוחב", OrientationUtils.LANDSCAPE);
        addChoice("אנכי הפוך", "אנכי כשהמכשיר הפוך", OrientationUtils.PORTRAIT_REVERSE);
        addChoice("אופקי הפוך", "אופקי כשהמכשיר הפוך", OrientationUtils.LANDSCAPE_REVERSE);

        if (!Settings.System.canWrite(this)) {
            TextView permission = text("נדרשת הרשאת מערכת כדי לנעול את הכיוון. לחץ כאן לאישור.", 14, primary, true);
            LinearLayout.LayoutParams pp=lp(-1,-2); pp.topMargin=dp(18); content.addView(permission,pp);
            permission.setOnClickListener(v -> requestWritePermission());
        }
    }

    private void addChoice(String title, String desc, final int mode) {
        LinearLayout row = card();
        TextView t=text(title,17,Color.rgb(25,28,34),true);
        row.addView(t,lp(-1,-2));
        TextView d=text(desc,13,Color.rgb(105,110,120),false); LinearLayout.LayoutParams dp=lp(-1,-2);dp.topMargin=dp(4);row.addView(d,dp);
        row.setOnClickListener(v -> apply(mode));
        LinearLayout.LayoutParams rp=lp(-1,-2);rp.topMargin=dp(10);content.addView(row,rp);
    }

    private void apply(int mode) {
        if (mode != OrientationUtils.AUTO && !Settings.System.canWrite(this)) { requestWritePermission(); return; }
        if (OrientationUtils.apply(this, mode)) { Toast.makeText(this, OrientationUtils.label(mode), Toast.LENGTH_SHORT).show(); refresh(); }
        else Toast.makeText(this, "לא ניתן לשנות את הכיוון במכשיר זה", Toast.LENGTH_SHORT).show();
    }

    private void requestWritePermission() {
        try { startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()))); }
        catch (Exception e) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }

    private LinearLayout card() {
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(18),dp(16),dp(18),dp(16));
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(18)); bg.setStroke(dp(1),Color.rgb(232,234,240)); l.setBackground(bg); l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.RIGHT);t.setTextDirection(View.TEXT_DIRECTION_RTL);t.setFontFeatureSettings("kern");if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t; }
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h);}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
}
