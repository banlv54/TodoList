package com.example.itachi.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.dodola.listview.extlib.ListViewExt;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

public class MainActivity extends AppCompatActivity {
    ListViewExt lv;
    ArrayAdapter arrAdapter;
    SharedPreferences sharedPref;
    Button add;
    String key = "";
    ArrayList<String> todoList = new ArrayList<>();
    ArrayList<String> checkedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListViewExt) findViewById(R.id.view);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        add = (Button) findViewById(R.id.btAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(MainActivity.this);
            }
        });


        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.drawSmallIndicatorForEvents(true);
        compactCalendarView.setLocale(Locale.ENGLISH);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTime(new Date());
//      reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        key = String.valueOf((int) calendar.getTimeInMillis());
        loadTodoList();

//        compactCalendarView.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                key = String.valueOf((int) dateClicked.getTime());
                loadTodoList();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
    }

    public void showCustomDialog(Context context) {
        final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_todo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                String todoName = ((EditText) dialogView.findViewById(R.id.todoName)).getText().toString();
                SharedPreferences.Editor editor = sharedPref.edit();
                Set<String> mySet = new HashSet<>();
                Set<String> myCheckedSet = new HashSet<>();
                mySet.addAll(todoList);
                myCheckedSet.addAll(checkedList);
                int old_leng = mySet.size();
                todoName = old_leng + ". " + todoName;
                mySet.add(todoName);
                if (mySet.size() != old_leng) {
                    todoList.add(todoName);
                    checkedList.add(old_leng + "false");
                    myCheckedSet.add(old_leng + "false");
                    editor.putStringSet(key, mySet);
                    editor.putStringSet(key + "_checked", myCheckedSet);
                    editor.commit();
                    arrAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.show();
    }

    public void loadTodoList() {
        Set<String> mySet = sharedPref.getStringSet(key, new HashSet<String>());
        Set<String> myarrCheckedSetSet = sharedPref.getStringSet(key + "_checked", new HashSet<String>());
        todoList = new ArrayList<String>(mySet);
        checkedList = new ArrayList<String>(myarrCheckedSetSet);
        Collections.sort(todoList);
        Collections.sort(checkedList);

        arrAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_checked, todoList);
        lv.setAdapter(arrAdapter);
        for (int i=0; i < checkedList.size(); i++) {
            lv.setItemChecked(i, (checkedList.get(i).contains("true")));
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lv.setItemChecked(i, checkedList.get(i).contains("false"));
                String checked = checkedList.get(i).contains("false") ? "true" : "false";
                checkedList.set(i, i + checked);
                SharedPreferences.Editor editor = sharedPref.edit();
                Set<String> myCheckedSet = new HashSet<>();
                myCheckedSet.addAll(checkedList);
                editor.putStringSet(key + "_checked", myCheckedSet);
                editor.commit();
            }
        });
    }
}
