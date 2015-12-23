package me.minichro.addictionguide;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private Button myButton;
    private TextView nextDateBox;
    private File myFile;
    private Time myTime;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        nextDateBox = (TextView) findViewById(R.id.textView);
        myButton = (Button) findViewById(R.id.button);
        nextDateBox.setText("Hello");
        updateNextTime();

        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    f = new File(getFilesDir() + "test.txt");



                    if(f.exists() && !f.isDirectory()) {
                        Log.i("AddictionGuide", "Appending");
                        fos  = new FileOutputStream(getFilesDir() + "test.txt", true);
                        oos = new AppendingObjectOutputStream(fos);
                    }
                    else
                    {
                        fos  = new FileOutputStream(getFilesDir() + "test.txt", true);
                        Log.i("AddictionGuide", "New File");
                        oos = new ObjectOutputStream(fos);
                    }

                    oos.writeObject(Calendar.getInstance());
                    oos.close();
                    updateNextTime();


                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    protected void updateNextTime()
    {
        List<Calendar> dateList = new ArrayList<>();
        Calendar nextDate;
        try{
            FileInputStream fis = new FileInputStream(getFilesDir() + "test.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean flag = true;
            Calendar temp;
            int count = 0;

            while(flag)
            {
                temp = (Calendar)ois.readObject();
                if(temp == null)
                {
                    flag = false;
                }
                else
                {
                    dateList.add(temp);
                }
                System.out.println("Count is : " + count++);
            }
            ois.close();
        }
        catch (Exception e)
        {
            if(!(e instanceof EOFException))
            {
                e.printStackTrace();
            }
        }

        if(dateList.size() == 0)
        {
            nextDateBox.setText("No Data yet");
        }
        else if(dateList.size() == 1)
        {
            nextDate = dateList.get(0);
            nextDate.add(Calendar.HOUR,6);
            nextDateBox.setText("" + nextDate.get(Calendar.DAY_OF_MONTH) + "/" + (1+nextDate.get(Calendar.MONTH)) + "/" + nextDate.get(Calendar.YEAR) + "  " + nextDate.get(Calendar.HOUR) + ":" + nextDate.get(Calendar.MINUTE));
        }
        else if(dateList.size() >= 2)
        {
            Calendar start = dateList.get(0);
            Calendar end = dateList.get(1);
            long diff = Math.abs(start.getTimeInMillis() - end.getTimeInMillis());
            if(diff > 18 * 60 * 60 * 1000)
            {
                nextDate = end;
                nextDate.add(Calendar.HOUR,6);
            }
            else
            {
                nextDate = start;
                nextDate.add(Calendar.HOUR,24);
            }
            nextDateBox.setText("" + nextDate.get(Calendar.DAY_OF_MONTH) + "/" + (1 + nextDate.get(Calendar.MONTH)) + "/" + nextDate.get(Calendar.YEAR) + "  " + nextDate.get(Calendar.HOUR) + ":" + nextDate.get(Calendar.MINUTE));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNextTime();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
}
