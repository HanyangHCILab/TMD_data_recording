package com.HanyangHCI.crc_test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton stillButton, walkingButton, motorizedButton, metroButton, carButton, busButton, nonMotorizedButton;

    Collecting collecting;
    String mode;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    0 );
        }
        else if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.READ_EXTERNAL_STORAGE },
                    0 );
        }
        else if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.READ_EXTERNAL_STORAGE },
                    0 );
        }

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }
       /*else if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION },
                    0 );
        }
        else if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.INTERNET },
                    0 );
        }*/

       collecting = new Collecting();
       stillButton = (ImageButton)findViewById(R.id.StillButton);
       walkingButton = (ImageButton)findViewById(R.id.WalkingButton);
       motorizedButton = (ImageButton)findViewById(R.id.MotorizedButton);
       metroButton = (ImageButton) findViewById(R.id.MetroButton);
       busButton = (ImageButton) findViewById(R.id.BusButton);
       carButton = (ImageButton) findViewById(R.id.CarButton);
       nonMotorizedButton = (ImageButton) findViewById(R.id.NonMotorizedButton);

       setButton();
    }
    private void setButton()
    {
        stillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("still");
                //collecting.mode = "still";
                mode = "still";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        nonMotorizedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("still");
                //collecting.mode = "still";
                mode = "manualChar";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });


        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("walking");
                //collecting.mode = "walking";
                mode = "walking";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        motorizedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("motorizedChair");
                //collecting.mode = "motorizedChar";
                mode = "powerChar";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        metroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("metro");
                //collecting.mode = "metro";
                mode = "metro";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("bus");
                //collecting.mode = "bus";
                mode = "bus";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("car");
                //collecting.mode = "car";
                mode = "car";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });

        nonMotorizedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting.setMode("car");
                //collecting.mode = "car";
                mode = "manualChar";
                CreatePhoto(mode);
                //StartSurvay(mode);
                /*Intent intent = new Intent(MainActivity.this, Survay.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
                finish();*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        StartSurvay(mode);
        finish();
    }

    private void StartSurvay(String getMode){
        Intent intent = new Intent(MainActivity.this, Survay.class);
        intent.putExtra("mode", getMode);
        startActivity(intent);
        finish();
    }

    private void CreatePhoto(String getMode){
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        String filename = "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + getMode + ".png";
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.hanyangHCI.crc_test.fileprovider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }
}
