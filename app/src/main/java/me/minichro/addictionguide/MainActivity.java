package me.minichro.addictionguide;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
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
import java.io.OutputStream;
import java.security.acl.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
        public synchronized void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String recentDatesString = bundle.getString("recentDates");
            String levelUpDate = bundle.getString("levelUpDate");
            String currentLevel = bundle.getString("currentLevel");
            recentDates.setText(recentDatesString);
            nextDateBox.setText(levelUpDate);
            currLevel.setText("CURRENT LEVEL : " + currentLevel);


        }


    };

    private ReentrantLock lock = new ReentrantLock();
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
                new DatePickerDialog(MainActivity.this,R.style.DialogTheme, datePickerListener, custDate.get(Calendar.YEAR), custDate.get(Calendar.MONTH), custDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this,R.style.DialogTheme, timePickerListener, custDate.get(Calendar.HOUR_OF_DAY), custDate.get(Calendar.MINUTE), true).show();
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

    protected synchronized void updateNextTime() {

        Runnable runnable = new Runnable() {

            public void run() {
                List<Calendar> dateList = new ArrayList<>();
                lock.lock();

                    try {
                        Log.i("Addiction Guide", getFilesDir() + "test.txt");
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
                        fis.close();
                    } catch (Exception e) {
                        if (!(e instanceof EOFException)) {
                            e.printStackTrace();
                        }
                    }


                    Bundle bundle = new Bundle();
                    if (dateList.size() > 0) {
                        Collections.sort(dateList);
                        /* Recent dates must be update before calling processDateList, since processDateList will modify the list of dates */

                        StringBuilder textToSet = new StringBuilder();

                        for (int i = dateList.size() - 1; i >= 0; i--) {
                            textToSet.append(dateTimeFormat.format(dateList.get(i).getTime())).append("\n");
                        }

                        Calendar dateToSet = processDateList(dateList);
                        currUserLevel = updateUserLevel(dateList);

                        bundle.putString("recentDates", textToSet.toString());
                        bundle.putString("currentLevel", "" + currUserLevel);
                        bundle.putString("levelUpDate", dateTimeFormat.format(dateToSet.getTime()));

                        //recentDates.setText(textToSet);
                        //currLevel.setText("CURRENT LEVEL : " + currUserLevel);
                        //nextDateBox.setText(dateTimeFormat.format(dateToSet.getTime()));

                    } else {
                        //nextDateBox.setText("No Data yet");
                        bundle.putString("recentDates", "No Data yet");
                        bundle.putString("levelUpDate", "No Data yet");
                        bundle.putString("currentLevel", "-");
                    }
                    Message msg = updateDatesHandler.obtainMessage();
                    msg.setData(bundle);
                    updateDatesHandler.sendMessage(msg);

                lock.unlock();
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
            //Log.i("Addiction Guide", dateTimeFormat.format(nextDate.getTime()) + " : " + (6 * Math.pow(2.0, (dateList.size() - i - 1))));

            if (maxDate.before(nextDate)) {
                if (found == false) {
                    maxDate = nextDate;
                    found = true;
                }
            }
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(getFilesDir() + "test.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Calendar temp;
            do {
                temp = (Calendar)ois.readObject();
                if(temp != null) {
                    //Log.i("Addiction Guide", temp.getTime().toLocaleString());
                }
            }while(temp != null);
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if(id == R.id.action_export) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, 42);
        }
        else if(id == R.id.action_import) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 43);
        }
        return super.onOptionsItemSelected(item);
    }

    public void writeExportedData(Intent resultData) {
        Uri treeUri = resultData.getData();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

        // Create a new file and write into it
        String filename = "addGuideExport.dB";
        ObjectOutput out = null;
        try {
            //Log.i("AddictionGuide", treeUri.toString() + " -- " + treeUri.getPath());
            DocumentFile newFile = pickedDir.createFile("application/octet-stream", filename);

            //String filepath = newFile.getUri().toFile();
            //Log.i("Addition Guide", filepath);

            //out = new ObjectOutputStream(new FileOutputStream(new File(filepath)));

            out = new ObjectOutputStream(getContentResolver().openOutputStream(newFile.getUri()));

            FileInputStream fis;
            try {
                fis = new FileInputStream(getFilesDir() + "test.txt");
                ObjectInputStream ois = new ObjectInputStream(fis);
                Calendar temp;
                do {
                    temp = (Calendar) ois.readObject();

                    if(temp != null) {
                        Log.i("Addiction Guide", "Exporting time " + temp.getTime().toLocaleString());
                    }
                    else {
                        Log.i("Addiction Guide","Adding null object to indicate EOF");
                    }
                    out.writeObject(temp);
                } while (temp != null);

                ois.close();
                fis.close();
            } catch (Exception e) {
                if (!(e instanceof EOFException)) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportData(Intent resultData) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.i("permission", "permission denied to Write to storage - requesting it");
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 2);

                }
                else {
                    Log.i("AddictionGuide", "Going to export data now");
                    writeExportedData(resultData);
                }
            }
            else {
                Log.i("AddictionGuide", "Going to export data now");
                writeExportedData(resultData);
            }
    }

    public void addImportedData(Intent resultData) {
        Uri fileUri = resultData.getData();
        try {
            FileInputStream fis = new FileInputStream(fileUri.getPath());
            ObjectInputStream ois = new ObjectInputStream(fis);
            f = new File(getFilesDir() + "test.txt");
            if (f.exists() && !f.isDirectory()) {
                Log.i("AddictionGuide", "Appending");
                fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                oos = new AppendingObjectOutputStream(fos);
            } else {
                Log.i("AddictionGuide", "New File");
                fos = new FileOutputStream(getFilesDir() + "test.txt", true);
                oos = new ObjectOutputStream(fos);
            }
            Calendar temp;
            do {
                temp = (Calendar) ois.readObject();
                if (temp != null) {
                    Log.i("Addiction Guide", "Importing time " + temp.getTime().toLocaleString());
                    oos.writeObject(temp);
                }
            } while (temp != null);
            oos.close();
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
            }
        }
        updateNextTime();
    }

    public void importData(Intent resultData) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                Log.i("permission", "permission denied to Read Storage - requesting it");
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                requestPermissions(permissions, 1);

            }
            else {
                addImportedData(resultData);
            }
        }
        else {
            addImportedData(resultData);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 42) {
                exportData(resultData);
            } else if (requestCode == 43) {
                importData(resultData);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 2:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Toast.makeText(MainActivity.this, "Export permission granted, please try to export again", Toast.LENGTH_SHORT).show();

                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(MainActivity.this, "Export permission refused, please provide permission to perform export", Toast.LENGTH_SHORT).show();
                }
                return;
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Import permission granted, please try to import again", Toast.LENGTH_SHORT).show();
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(MainActivity.this, "Import permission refused, please provide permission to perform import", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}
