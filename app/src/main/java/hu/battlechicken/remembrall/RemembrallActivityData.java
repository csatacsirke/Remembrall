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
    static final long serialVersionUID =6859374837929453487L;
    
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
