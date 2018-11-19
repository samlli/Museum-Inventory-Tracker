package com.blikoon.qrcodescannerlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Button sendMail;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "QRCScanner-MainActivity";
    public int scanState = 0;
    public ArrayList<LogEntry> log = new ArrayList<>();
    public String currentRack = "";
    public String currentPainting = "";
    //public static final int EMAIL_REQUEST = 999;
    private String LOG_FILE = "log_file";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button_start_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);
            }
        });

        sendMail = (Button) findViewById(R.id.send_email);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                sendMail();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Summary");
        alertDialog.setMessage("To make a new entry, please scan a rack");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        //ask how to add yes and no button


    }

    /////////////
    public void writeFile(String fileContents) {
        ArrayList<String> oldContents = readLogFile();

        try{
            File file = new File(LOG_FILE);
            file.delete();
            file = new File(this.getFilesDir(), LOG_FILE);

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            if(!oldContents.isEmpty()){
                oldContents.add(fileContents);
                oldContents.set(0, oldContents.size()-1 +" scanned");
                for(String line : oldContents){
                    bw.write(line);
                    bw.newLine();
                }
            }else{
                bw.write("1 scanned");
                bw.newLine();
                bw.write(fileContents);
            }

            //bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        readLogFile();
    }

    public ArrayList<String> readLogFile(){
        FileInputStream fileInputStream = null;
        ArrayList<String> readContents = new ArrayList<String>();
        try{
            fileInputStream = openFileInput(LOG_FILE);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                readContents.add(line);
            }
            bufferedReader.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException eio){
            eio.printStackTrace();
        }

        return readContents;
    }

    public String readFile(String fileName){
        FileInputStream fileInputStream = null;
        String readString = "";
        try{
            fileInputStream = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            readString = sb.toString();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException eio){
            eio.printStackTrace();
        }

        return readString;
    }
    //////////////

    /*public void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
        String filePath = getApplicationContext().getFilesDir() + "/";
        File path = new File(filePath);

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void retrieveFromFile()
    {
        try
        {
            //BufferedReader in = new BufferedReader(new FileReader(Environment.DIRECTORY_DCIM + "/YourFolder/config.txt"));
            BufferedReader in = new BufferedReader(new FileReader(getApplicationContext().getFilesDir() + "/config.txt"));
            String line;

            while((line = in.readLine()) != null)
            {
                Toast.makeText(getApplicationContext(), line,
                        Toast.LENGTH_LONG).show();

            }
            in.close();
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }*/

    public boolean rackScanned(String data)
    {

        if(data.split(" ", 2)[0].equals("Rack"))
        {
            return true;
        }
        else
        {
           return false;
        }

    }

    public boolean paintingScanned(String data)
    {

        if(data.split(" ", 2)[0].equals("Painting:"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private void sendMail()
    {
        ArrayList<String> totalLogArray = readLogFile();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"jay18@duke.edu"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Log Update");
        i.putExtra(Intent.EXTRA_TEXT, "See Attached");
        File file = getTempFile(this, totalLogArray);
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file));

        if(i.resolveActivity(getPackageManager()) != null)
        {
            startActivity(i);
            //startActivityForResult(i, EMAIL_REQUEST);
            //file.delete();
        }


    }

    private File getTempFile(Context context, ArrayList<String> totalLogArray)
    {
        File file = null;
        try{
            String fileName = "Log_File";
            file = File.createTempFile(fileName, ".txt", context.getCacheDir());

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(String line : totalLogArray){
                bw.write(line);
                bw.newLine();
            }

            bw.close();

            deleteLogFile();
        }
        catch (IOException e)
        {
           //Error while creating file
        }

        return file;
    }

    private Boolean deleteLogFile(){
        File dir = getFilesDir();
        File file = new File(dir, LOG_FILE);
        boolean deleted = file.delete();

        return deleted;
    }

    public String getTotalLog(ArrayList<LogEntry> currentLog)
    {
        String entries = "";
        for(int i = 0; i < currentLog.size(); i++)
        {
            entries += currentLog.get(i).toString() + "\n";
        }

        return entries;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            /*AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();*/

            if(scanState == 0)
            {
                if(rackScanned(result))
                {
                    //writeToFile(result);
                    //retrieveFromFile();
                    currentRack = result;

                    AlertDialog successfulRackScan = new AlertDialog.Builder(MainActivity.this).create();
                    successfulRackScan.setTitle("Summary");
                    successfulRackScan.setMessage(result + " Was Scanned \n" + "Please Scan a Painting");
                    successfulRackScan.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    successfulRackScan.show();
                    scanState++;
                }
                else
                {
                    AlertDialog failedRackScan = new AlertDialog.Builder(MainActivity.this).create();
                    failedRackScan.setTitle("Summary");
                    failedRackScan.setMessage("Oops, that was not a rack, please try again!");
                    failedRackScan.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    failedRackScan.show();
                }

                Toast.makeText(getApplicationContext(), Integer.toString(scanState),
                        Toast.LENGTH_LONG).show();

            }

            else if(scanState == 1)
            {
                if(paintingScanned(result))
                {
                    //writeToFile(result);
                    //retrieveFromFile();
                    currentPainting = result;
                    String time = Calendar.getInstance().getTime().toString();
                    final LogEntry entry = new LogEntry(currentRack, currentPainting, time);

                    ///////
                    writeFile(currentRack+" "+currentPainting);

                    AlertDialog successfulPaintingScan = new AlertDialog.Builder(MainActivity.this).create();
                    successfulPaintingScan.setTitle("Summary");
                    successfulPaintingScan.setMessage(entry.toString() + "\n\nIs this correct?");
                    successfulPaintingScan.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    log.add(entry);

                                }
                            });

                    successfulPaintingScan.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AlertDialog failedPartner = new AlertDialog.Builder(MainActivity.this).create();
                                    failedPartner.setTitle("Summary");
                                    failedPartner.setMessage("We'll try again, please scan a rack");
                                    failedPartner.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    failedPartner.show();
                                }
                            });

                    successfulPaintingScan.show();

                    scanState--;

                    //only put up new alert dialogue after pressing ok
                }
                else
                {
                    AlertDialog failedPaintingScan = new AlertDialog.Builder(MainActivity.this).create();
                    failedPaintingScan.setTitle("Summary");
                    failedPaintingScan.setMessage("Oops, that was not a painting, please try again!");
                    failedPaintingScan.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    failedPaintingScan.show();
                }
                Toast.makeText(getApplicationContext(), Integer.toString(scanState),
                        Toast.LENGTH_LONG).show();

            }



        }
    }
}
