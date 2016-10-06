package hu.battlechicken.remembrall;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Todo list
 * 
 * undo / vagy valami mechanizmus hogy ne vesszen el az adat ha mellé kattintasz
 * közelgő határidő jelzése
 * lock screenre írás
 * az elvégzett cuccok mentése valahova + lehessen elvégzettnek jelölni
 * fontosság beállítás
 * rendezés
 * no minute/hour/deadline
 */

public class MainActivity extends AppCompatActivity {
    
    ScrollView mainContentView;
    FloatingActionButton floatingActionButton;
    Stack<View> viewStack = new Stack<>();
    RemembrallActivityData data = new RemembrallActivityData();
    
    void displayView(View view) {
        mainContentView.removeAllViews();
        
        //LinearLayout linearLayout = new LinearLayout(this);
        //linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1200));
        //linearLayout.addView(view);
        //
        //viewStack.add(linearLayout);
        //mainContentView.addView(linearLayout);
        //
        viewStack.add(view);
        mainContentView.addView(view);
        
        floatingActionButton.hide();
    }
    
    boolean displayPreviousView() {
        if (viewStack.size() < 2) return false;
        viewStack.pop();
        View lastView = viewStack.pop();
        displayView(lastView);
        
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
  
    
        
        mainContentView = new ScrollView(this);
        mainContentView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
        mainLayout.addView(mainContentView);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        data.entries.add(new Entry("lofasz 3", "hosszu lofasz", Calendar.getInstance(), null));
        data.entries.add(new Entry("sirály 3 ", "valami más dummy", Calendar.getInstance(), Calendar.getInstance()));
        data.entries.add(new Entry("fóka 3", "valami 3. dummy", Calendar.getInstance(), null));
        
        // TODO
        loadState();
        
        EntryListView entryListView = new EntryListView(this);
        entryListView.setEntries(data.entries);
        
        displayView(entryListView);
        
        updateWidget();
        
        
    }
    
    void saveState() {
        data.saveState(this);
        
        updateWidget();
    }
    
    void loadState() {
        RemembrallActivityData data = RemembrallActivityData.loadState(this);
        if( data != null ) {
            this.data = data;
        }
    }
    
    void updateWidget() {
        
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, RemembrallWidget.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent update = new Intent();
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        this.sendBroadcast(update);
    }
    
    
    class EntryListView extends LinearLayout {
        private List<Entry> entries;
        private Comparator<Entry> comparator = new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                if (a.deadline != null && b.deadline != null) {
                    return a.deadline.compareTo(b.deadline);
                } else if (a.deadline == null && b.deadline == null) {
                    return a.dateAdded.compareTo(b.dateAdded);
                } else if (a.deadline == null) {
                    return +1;
                } else {
                    return -1;
                }
            }
        };
        
        public EntryListView(Context context) {
            super(context);
            setOrientation(VERTICAL);
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        
        public void setEntries(List<Entry> entries) {
            this.entries = entries;
            update();
        }
        
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            update();
            
            floatingActionButton.show();        
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Entry newEntry = new Entry();
                    EntryEditorView entryEditorView = new EntryEditorView(MainActivity.this, newEntry) {
                        @Override
                        void onOk() {
                            entries.add(newEntry);
                            saveState();
                            displayPreviousView();
                        }
                    };
                    displayView(entryEditorView);
        
                }
            });
        }
    
    
        public void update() {
            
            this.removeAllViews();
            
            Collections.sort(entries, comparator);
            
            
            for (final Entry entry : entries) {
                EntryView entryView = new EntryView(MainActivity.this, entry);
                
                entryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        EntryEditorView entryEditorView = new EntryEditorView(MainActivity.this, entry) {
                            @Override
                            void onOk() {
                                displayPreviousView();
                                saveState();
                            }
                        };
                        
                        displayView(entryEditorView);
                        
                        
                    }
                });
    
                entryView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(EntryListView.this.getContext(), EntryListView.this);
                        Menu menu = popupMenu.getMenu();
                        MenuItem item = menu.add("Delete");
                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                entries.remove(entry);
                                EntryListView.this.update();
                                saveState();
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });
                this.addView(entryView);
            }
        }
        
        
    }
    
    class EntryView extends LinearLayout {
        Entry entry;
        TextView titleView;
        DateView dateView;
        public EntryView(Context context, Entry entry) {
            super(context);
            this.entry = entry;
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            
            titleView = new TextView(context);
            titleView.setTextSize(28);
            this.addView(titleView);
            
            dateView = new DateView(context, entry.deadline);
            dateView.setGravity(Gravity.RIGHT);
            dateView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.addView(dateView);
            
            update();
        }
        
        void update() {
            titleView.setText(entry.title);
            dateView.update();
        }
    }
    
    
    abstract class EntryEditorView extends LinearLayout {
        
        private Entry entry;
        DateView deadlineView;
        
        EntryEditorView(Context context, final Entry entry) {
            super(context);
            this.entry = entry;
            
            
            this.setOrientation(LinearLayout.VERTICAL);
            this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            
            final EditText titleEdit = new EditText(context);
            titleEdit.setText(entry.title);
            titleEdit.setTextSize(28);
            this.addView(titleEdit);
            
            final EditText editText = new EditText(context);
            editText.setText(entry.text);
            this.addView(editText);
            
            
            deadlineView = new DateView(context, entry.deadline);
            deadlineView.setTextSize(28);
            this.addView(deadlineView);
            
            DateView creationDateView = new DateView(context, entry.dateAdded);
            this.addView(creationDateView);
            
            
            Button datePickerButton = new Button(context);
            datePickerButton.setText("Pick date");
            this.addView(datePickerButton);
            datePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = EntryEditorView.this.entry.deadline != null ? (Calendar)EntryEditorView.this.entry.deadline.clone() : Calendar.getInstance();
                    CalendarEditorView calendarEditorView = new CalendarEditorView(MainActivity.this, calendar) {
                        @Override
                        void onDateChosen(Calendar calendar) {
                            displayPreviousView();
                            deadlineView.calendar = calendar;
                            deadlineView.update();
                        }
                    };
                    displayView(calendarEditorView);
                }
            });
    
            
            Button okButton = new Button(context);
            okButton.setText("Ok. ~Rammus");
            this.addView(okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // entry-t frissiteni
                    EntryEditorView.this.entry.text = editText.getText().toString();
                    EntryEditorView.this.entry.title = titleEdit.getText().toString();
                    EntryEditorView.this.entry.deadline = deadlineView.calendar;
                    
                    //visszalépni a main menübe
                    EntryEditorView.this.onOk();
                }
            });
        }
        
        abstract void onOk();
    }
    
    class DateView extends TextView {
        Calendar calendar;
        String prefix;
    
        public DateView(Context context, Calendar calendar) {
            this(context,calendar, "");
        }
                        
        public DateView(Context context, Calendar calendar, String prefix) {
            super(context);
            this.calendar = calendar;
            this.prefix = prefix != null ? prefix : "";
            update();
        }
        
        void update() {
            if( calendar != null ) {
                this.setText(calendarToString(calendar));
            } else {
                this.setText("Not set");
            }
            
        }
    
    
        private String calendarToString(Calendar calendar) {
        
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // index from 0
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //int hour = calendar.get(Calendar.HOUR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
        
        
            return String.format("%d.%02d.%02d %02d:%02d", year, month, day, hour, minute);
        }
    }
    
    
    public abstract class CalendarEditorView extends LinearLayout {
        Calendar calendar;
        DatePicker datePicker;
        TimePicker timePicker;
        public CalendarEditorView(Context context, Calendar calendar) {
            super(context);
            this.calendar = calendar;
            
            this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            //this.setGravity(Gravity.CENTER);
            //this.setOrientation(VERTICAL);
            this.setOrientation(HORIZONTAL);
            //this.setGravity(Gravity.CENTER_HORIZONTAL);
            this.setGravity(Gravity.LEFT);
            //this.setWeightSum(100);
            //this.setScaleX(0.5f);
            //this.setScaleY(0.5f);
            
    
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //params.weight = 50;
            
            
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            datePicker = new DatePicker(context);
            datePicker.setCalendarViewShown(false);
            datePicker.updateDate(year, month, day);
            //datePicker.setClipToPadding(true);
            //datePicker.getLayoutParams().width = 100;
            //datePicker.setScaleX(0.5f);
            //datePicker.setScaleY(0.5f);
            //datePicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //datePicker.setPadding(0,0,0,0);
            datePicker.setLayoutParams(params);
            this.addView(datePicker);
            
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            
            timePicker = new TimePicker(context);
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
            timePicker.setIs24HourView(true);
            //timePicker.setScaleX(0.5f);
            //timePicker.setScaleY(0.5f);
            
            timePicker.setLayoutParams(params);
            this.addView(timePicker);
        }
    
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            floatingActionButton.show();
            floatingActionButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    onDateChosen(calendar);
                }
            });
        }
        
        abstract void onDateChosen(Calendar calendar);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        if (!displayPreviousView()) {
            super.onBackPressed();
        }
    }
    
}
