package com.blikoon.qrcodescannerlibrary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "QRCScanner-MainActivity";
    public boolean wasRackScanned = false;

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

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Scan");
        alertDialog.setMessage("Please scan a rack");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();



    }

    public void writeToFile(String data)
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

    }

    public boolean rackScanned(String data)
    {

        Toast.makeText(getApplicationContext(), data.split(" ", 2)[0],
                Toast.LENGTH_LONG).show();

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

        Toast.makeText(getApplicationContext(), data.split(" ", 2)[0],
                Toast.LENGTH_LONG).show();

        if(data.split(" ", 2)[0].equals("Painting:"))
        {
            return true;
        }
        else
        {
            return false;
        }

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

            if(rackScanned(result))
            {
                writeToFile(result);
                retrieveFromFile();

                AlertDialog alertScanPainting = new AlertDialog.Builder(MainActivity.this).create();
                alertScanPainting.setTitle("Summary");
                alertScanPainting.setMessage(result + " Was Scanned \n" + "Please Scan Painting");
                alertScanPainting.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertScanPainting.show();

            }

            if(paintingScanned(result))
            {
                writeToFile(result);
                retrieveFromFile();

                AlertDialog alertScanPainting = new AlertDialog.Builder(MainActivity.this).create();
                alertScanPainting.setTitle("Summary");
                alertScanPainting.setMessage(result + " Was Scanned");
                alertScanPainting.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertScanPainting.show();
            }

        }
    }
}
