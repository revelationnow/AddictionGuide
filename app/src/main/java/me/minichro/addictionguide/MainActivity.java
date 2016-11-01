package me.minichro.addictionguide;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addCurrentDateButton;
    private Button addSelectedDateButton;

    private TextView dateText;
    private TextView timeText;
    private TextView recentDates;
    private TextView currLevel;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateTimeFormat;

    private Calendar custDate;

    private TextView nextDateBox;

    private int levelSpaces;
    private int currUserLevel;

    private SharedPreferences sharedPref;
    Context context;

    Handler updateDatesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            String recentDatesString = bundle.getString("recentDates");
            String levelUpDate = bundle.getString("levelUpDate");
            String currentLevel = bundle.getString("currentLevel");
            recentDates.setText(recentDatesString);
            nextDateBox.setText(levelUpDate);
            currLevel.setText("CURRENT LEVEL : " + currentLevel);


        }


    };

    private static final int MAX_LEVELS = 20;
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
        addCurrentDateButton = (Button) findViewById(R.id.button);
        addSelectedDateButton = (Button) findViewById(R.id.button2);
        dateText = (TextView) findViewById(R.id.editText);
        timeText = (TextView) findViewById(R.id.editText2);
        recentDates = (TextView) findViewById(R.id.textView3);
        currLevel = (TextView) findViewById(R.id.textView5);
        custDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        updateCustDateBox();
        updateCustTimeBox();

        getSupportActionBar().show();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(listener);

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

        addSelectedDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setView(getLayoutInflater().inflate(R.layout.content_alert, null))
                        .setCustomTitle(getLayoutInflater().inflate(R.layout.title_alert, null))
                        .setTitle("Add")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        addCurrentDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setView(getLayoutInflater().inflate(R.layout.content_alert, null))
                        .setCustomTitle(getLayoutInflater().inflate(R.layout.title_alert, null))
                        .setTitle("Add")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void updateNumLevels() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        levelSpaces = Integer.parseInt(sharedPref.getString("level_1_hours", "12"));

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

        Runnable runnable = new Runnable() {

            public void run() {
                List<Calendar> dateList = new ArrayList<>();
                try {
                    FileInputStream fis = new FileInputStream(getFilesDir() + "test.txt");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    boolean flag = true;
                    Calendar temp;

                    while (flag) {
                        temp = (Calendar) ois.readObject();
                        if (temp == null) {
                            flag = false;
                        } else {
                            dateList.add(temp);
                        }
                    }
                    ois.close();
                } catch (Exception e) {
                    if (!(e instanceof EOFException)) {
                        e.printStackTrace();
                    }
                }

                Bundle bundle = new Bundle();
                if (dateList.size() > 0) {
                    Collections.sort(dateList);
                    /* Recent dates must be update before calling processDateList, since processDateList will modify the list of dates */

                    String textToSet = "";

                    for (int i = dateList.size() - 1; i >= 0; i--) {
                        textToSet += dateTimeFormat.format(dateList.get(i).getTime()) + "\n";
                    }






                    Calendar dateToSet = processDateList(dateList);
                    currUserLevel = updateUserLevel(dateList);

                    bundle.putString("recentDates",textToSet);
                    bundle.putString("currentLevel", "" + currUserLevel);
                    bundle.putString("levelUpDate",dateTimeFormat.format(dateToSet.getTime()));

                    //recentDates.setText(textToSet);
                    //currLevel.setText("CURRENT LEVEL : " + currUserLevel);
                    //nextDateBox.setText(dateTimeFormat.format(dateToSet.getTime()));

                } else {
                    //nextDateBox.setText("No Data yet");
                    bundle.putString("recentDates","No Data yet");
                    bundle.putString("levelUpDate","No Data yet");
                    bundle.putString("currentLevel", "-");
                }
                Message msg = updateDatesHandler.obtainMessage();
                msg.setData(bundle);
                updateDatesHandler.sendMessage(msg);
            }
        };

        Thread updateThread = new Thread(runnable);
        updateThread.start();

    }

    /* Here dateList will get updated by the levelspaces, so all further use must consider that
     *
     */
    private Calendar processDateList(List<Calendar> dateList) {
        Calendar maxDate = Calendar.getInstance();
        Calendar nextDate;
        boolean found = false;

        for (int i = dateList.size() - 1; (i >= 0); i--) {
            nextDate = dateList.get(i);
            nextDate.add(Calendar.HOUR, (int) (levelSpaces * Math.pow(2.0, (dateList.size() - i - 1))));
            Log.i("Addiction Guide", dateTimeFormat.format(nextDate.getTime()) + " : " + (6 * Math.pow(2.0, (dateList.size() - i - 1))));

            if (maxDate.before(nextDate)) {
                if (found == false) {
                    maxDate = nextDate;
                    found = true;
                }
            }
        }
        return maxDate;
    }

    /* dateList already has levelSpaces added to it, so no need to add levelSpaces to get the cutoff date for each level
     *
     */
    private int updateUserLevel(List<Calendar> dateList) {
        Calendar maxDate = Calendar.getInstance();
        Calendar nextDate;
        int retLevel = 20;

        for (int i = dateList.size() - 1; i >= 0; i--) {
            nextDate = dateList.get(i);

            if (maxDate.before(nextDate)) {
                retLevel = dateList.size() - i;
                break;
            }
        }
        return retLevel;
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
