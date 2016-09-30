package hu.battlechicken.remembrall;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by Battlechicken on 2016-09-18.
 */
public class RemembrallActivityData implements Serializable {
    
    
    LinkedList<Entry> entries = new LinkedList<>();
    
    
    final static String dataFileName = "saved_data.ser";
    
    void saveState(Context context) {
        try {
            File file = new File(context.getFilesDir(), dataFileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    
    static RemembrallActivityData loadState(Context context) {
        RemembrallActivityData data = null;
        try {
            File file = new File(context.getFilesDir(), dataFileName);
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            data = (RemembrallActivityData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    static String calendarToString(Calendar calendar) {
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // index from 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //int hour = calendar.get(Calendar.HOUR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        
        return String.format("<b>%d.%02d.%02d</b> %02d:%02d", year, month, day, hour, minute);
    }
    
    
    
    String toHtml() {
        
        String html = "";
    
        for(Entry entry : entries) {


            String deadline = entry.deadline != null ? RemembrallActivityData.calendarToString(entry.deadline) : "";

            html += entry.title + "<span align='right' width='100%'>" + deadline + "</span>" + "<br/>";

        }
        return html;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RemembrallActivityData that = (RemembrallActivityData) o;
    
        return !(entries != null ? !entries.equals(that.entries) : that.entries != null);
    
    }
    
    @Override
    public int hashCode() {
        return entries != null ? entries.hashCode() : 0;
    }
}
