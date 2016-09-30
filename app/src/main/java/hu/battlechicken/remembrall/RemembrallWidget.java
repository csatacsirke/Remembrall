package hu.battlechicken.remembrall;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class RemembrallWidget extends AppWidgetProvider {
    
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        
        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.remembrall_widget);
        
        
        String widgetText = "";
    
        RemembrallActivityData data = RemembrallActivityData.loadState(context);
    
        widgetText = data.toHtml();
    
        views.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, 18);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.appwidget_text, Html.fromHtml(widgetText));
    
    
    
    
    
        //Bitmap bitmap = Bitmap.createBitmap(10, 100, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(bitmap);
        ////canvas.drawColor(Color.WHITE);
        //canvas.drawARGB(255, 255, 255, 255);
        ////views.setBitmap(R.id.appwidget_image, "android.view.View.setBackground", bitmap);
        //Bitmap clone = bitmap.copy(Bitmap.Config.ARGB_4444, false); // workaround
        ////rviews.setImageViewBitmap(R.id.time, clone);
        //
        ////views.setImageViewBitmap(R.id.appwidget_image, clone);
        //views.setImageViewBitmap(R.id.appwidget_image, bitmap);
        //
    
        try {
            
            TextView view = new TextView(context);
            view.setText("lofasz");
            view.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
            //view.setBackground();
            Bitmap bitmap = getBitmapFromView(view);
            //views.setBitmap(R.id.appwidget_image, "android.view.View.setBackground", bitmap);
            views.setImageViewBitmap(R.id.appwidget_image, bitmap);
        
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    
    
    
    
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
    
        appWidgetManager.updateAppWidget(appWidgetId, views);
    
    }
    
    // 2016.09.19
    // http://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
    public static Bitmap getBitmapFromView(View v) {
        //view.forceLayout();
        ////Define a bitmap with the same size as the view
        ////Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bitmap returnedBitmap = Bitmap.createBitmap(100, 100,Bitmap.Config.ARGB_8888);
        ////Bind a canvas to it
        //Canvas canvas = new Canvas(returnedBitmap);
        ////Get the view's background
        //Drawable bgDrawable = view.getBackground();
        ////Drawable bgDrawable = view.getdrawa;
        //if (bgDrawable!=null)
        //    //has background drawable, then draw it on the canvas
        //    bgDrawable.draw(canvas);
        //else
        //    //does not have background drawable, then draw white background on the canvas
        //    canvas.drawColor(Color.BLACK);
        //// draw the view on the canvas
        ////canvas.drawColor(Color.WHITE);
        //view.draw(canvas);
    
        if (v.getMeasuredHeight() <= 0) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap returnedBitmap = Bitmap.createBitmap(100, 100,Bitmap.Config.ARGB_8888);
    
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.BLACK);
        v.draw(canvas);
    
        //return the bitmap
        return returnedBitmap;
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
    
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), RemembrallWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
                    
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);
            //Log.e("finally after a whole day", "working :");
        } else {
            super.onReceive(context, intent);   
        }
    }
    
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    
    
}

