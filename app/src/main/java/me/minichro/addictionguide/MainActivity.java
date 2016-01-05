package me.minichro.addictionguide;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button myButton;
    private Button addButton;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private TextView dateText;
    private TextView timeText;
    private TextView recentDates;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateTimeFormat;

    private Calendar custDate;

    private TextView nextDateBox;
    private File myFile;
    private Time myTime;
    private int numLevels;
    private int[] levelSpaces;

    private SharedPreferences sharedPref;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    FileOutputStream fos;
    ObjectOutputStream oos;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = this;
        nextDateBox = (TextView) findViewById(R.id.textView);
        myButton = (Button) findViewById(R.id.button);
        addButton = (Button) findViewById(R.id.button2);
        dateText = (TextView) findViewById(R.id.editText);
        timeText = (TextView) findViewById(R.id.editText2);
        recentDates = (TextView) findViewById(R.id.textView3);
        custDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        updateCustDateBox();
        updateCustTimeBox();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setHomeButtonEnabled(true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(listener);

        //getSupportActionBar().setCustomView(R.menu.menu);

        dateText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, datePickerListener, custDate.get(Calendar.YEAR), custDate.get(Calendar.MONTH), custDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, timePickerListener, custDate.get(Calendar.HOUR_OF_DAY), custDate.get(Calendar.MINUTE), true).show();
            }
        });

        nextDateBox.setText("Hello");

        updateNumLevels();
        updateNextTime();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    f = new File(getFilesDir() + "test.txt");


                    if (f.exists() && !f.isDirectory()) {
                        Log.i("AddictionGuide", "Appending");
                        fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                        oos = new AppendingObjectOutputStream(fos);
                    } else {
                        fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                        Log.i("AddictionGuide", "New File");
                        oos = new ObjectOutputStream(fos);
                    }

                    oos.writeObject(custDate);
                    oos.close();
                    updateNextTime();
                    Toast.makeText(MainActivity.this, "Added " + dateTimeFormat.format(custDate.getTime()) + " to Database", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    f = new File(getFilesDir() + "test.txt");


                    if (f.exists() && !f.isDirectory()) {
                        Log.i("AddictionGuide", "Appending");
                        fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                        oos = new AppendingObjectOutputStream(fos);
                    } else {
                        fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                        Log.i("AddictionGuide", "New File");
                        oos = new ObjectOutputStream(fos);
                    }

                    oos.writeObject(Calendar.getInstance());
                    oos.close();
                    updateNextTime();
                    Toast.makeText(MainActivity.this, "Added Current Time to Database", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void updateNumLevels() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        numLevels = Integer.parseInt(sharedPref.getString("numLevels", "3"));
        System.out.println("Number of levels is " + numLevels);
        levelSpaces = new int[numLevels];


        levelSpaces[0] = Integer.parseInt(sharedPref.getString("level_1_hours", "6")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds

        if (numLevels > 1) {
            levelSpaces[1] = Integer.parseInt(sharedPref.getString("level_2_hours", "24")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds
            if (numLevels > 2) {
                levelSpaces[2] = Integer.parseInt(sharedPref.getString("level_3_hours", "48")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds
                if (numLevels > 3) {
                    levelSpaces[3] = Integer.parseInt(sharedPref.getString("level_4_hours", "96")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds
                    if (numLevels > 4) {
                        levelSpaces[4] = Integer.parseInt(sharedPref.getString("level_5_hours", "192")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds
                        if (numLevels > 5) {
                            levelSpaces[5] = Integer.parseInt(sharedPref.getString("level_6_hours", "384")) * 60 * 60 * 1000; //6 hours x 60 minutes x 60 x 1000 milliseconds
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (view.isShown()) {
                custDate.set(Calendar.YEAR, year);
                custDate.set(Calendar.MONTH, month);
                custDate.set(Calendar.DAY_OF_MONTH, day);
                updateCustDateBox();
            }
        }
    };

    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            if (view.isShown()) {
                custDate.set(Calendar.HOUR_OF_DAY, hour);
                custDate.set(Calendar.MINUTE, minute);
                updateCustTimeBox();
            }
        }

    };

    SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    updateNumLevels();
                    updateNextTime();
                }
            };

    public void updateCustDateBox() {
        dateText.setText(dateFormat.format(custDate.getTime()));
    }

    public void updateCustTimeBox() {
        timeText.setText(timeFormat.format(custDate.getTime()));
    }

    protected void updateNextTime() {
        List<Calendar> dateList = new ArrayList<>();
        Calendar nextDate;
        try {
            FileInputStream fis = new FileInputStream(getFilesDir() + "test.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean flag = true;
            Calendar temp;
            int count = 0;

            while (flag) {
                temp = (Calendar) ois.readObject();
                if (temp == null) {
                    flag = false;
                } else {
                    dateList.add(temp);
                }
                System.out.println("Count is : " + count++);
            }
            ois.close();
        } catch (Exception e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
            }
        }

        if (dateList.size() > 0) {
            Collections.sort(dateList);
            /* Recent dates must be update before calling processDateList, since processDateList will modify the list of dates */
            recentDates.setText("");

            for (int i = dateList.size() - 1; i >= 0; i--) {
                recentDates.setText(recentDates.getText().toString() + dateTimeFormat.format(dateList.get(i).getTime()) + "\n");
            }


            Calendar dateToSet = processDateList(dateList);
            nextDateBox.setText(dateTimeFormat.format(dateToSet.getTime()));

        } else {
            nextDateBox.setText("No Data yet");
        }
    }

    private Calendar processDateList(List<Calendar> dateList) {
        Calendar maxDate = Calendar.getInstance();
        Calendar nextDate;

        for (int i = dateList.size() - 1; (i > dateList.size() - 1 - numLevels) && (i >= 0); i--) {
            nextDate = dateList.get(i);
            nextDate.add(Calendar.MILLISECOND, levelSpaces[dateList.size() - i - 1]);

            if (maxDate.before(nextDate)) {
                maxDate = nextDate;
            }
        }
        return maxDate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
        updateNumLevels();
        updateNextTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://me.minichro.addictionguide/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://me.minichro.addictionguide/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
