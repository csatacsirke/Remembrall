package hu.battlechicken.remembrall;

import java.io.Serializable;
import java.util.Calendar;

public class Entry implements Serializable {
    // not random, generated from the first version of the class
    private static final long serialVersionUID = 7478467986558520238L;
    
    public Entry(String title, String text, Calendar dateAdded, Calendar deadline) {
        this.title = title;
        this.text = text;
        this.dateAdded = dateAdded;
        this.deadline = deadline;
    }
    
    public Entry() {
    }
    
    
    public String title = "Title";
    public String text = "Long desctiption";
    public Calendar dateAdded = Calendar.getInstance();
    public Calendar deadline;
    
    // v2
    public boolean useTime = false;
    public boolean useDate = false;
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Entry entry = (Entry) o;
        
        if (!title.equals(entry.title)) return false;
        if (!text.equals(entry.text)) return false;
        if (!dateAdded.equals(entry.dateAdded)) return false;
        return !(deadline != null ? !deadline.equals(entry.deadline) : entry.deadline != null);
    
    }
    
    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + dateAdded.hashCode();
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        return result;
    }
}
