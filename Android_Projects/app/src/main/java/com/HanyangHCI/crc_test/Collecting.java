package com.HanyangHCI.crc_test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Collecting extends AppCompatActivity{

    public String mode;
    CSVWrite cw;
    Calendar calendar;
    public static PowerManager.WakeLock sCpuWakeLock;
    Button doneButton;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private IMyAidlInterface binder;
    List<String[]> mainData = new ArrayList<String[]>();
    ThreadTest test;
    private int remain = 0;
    private boolean stopFlag = false;

    Handler handler;
    boolean done=false;
    RestartService restartService;
    String location, other;
    //BackGroundCollecting backGroundCollecting;

    private Intent serviceIntent;

    private TextView timeRemain;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_collecting);


        Intent intent2 = getIntent();
        mode = intent2.getExtras().getString("mode");
        location =  intent2.getExtras().getString("location");


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
        callPermission();
        //remain = 900;

        timeRemain = (TextView)findViewById(R.id.TimeRemaining);
        doneButton = (Button)findViewById(R.id.doneButton);
        setButton();

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                    setText();
            }
        };
        restartService = new RestartService();
        IntentFilter filter = new IntentFilter();
        filter.addAction("mode");
        registerReceiver(restartService, filter);

        Intent intent = new Intent("mode");
        intent.putExtra("mode", mode);
        sendBroadcast(intent);

        serviceIntent =  new Intent(getApplicationContext(), BackGroundCollecting.class);
        serviceIntent.setPackage("com.HanyangHCI.crc_test");
        serviceIntent.putExtra("mode", mode);
        serviceIntent.putExtra("location", location);
        serviceIntent.putExtra("other", other);
        startForegroundService(serviceIntent);
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);


        //Intent bindIntent = new Intent(Collecting.this, BackGroundCollecting.class);
        //bindService(bindIntent, connection, BIND_AUTO_CREATE);
        new Thread(new ThreadTest()).start();
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ( event.getAction() == KeyEvent.ACTION_DOWN ){
            if ( keyCode == KeyEvent.KEYCODE_BACK ){
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
                alert_confirm.setMessage("프로그램을 종료 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("종료",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 'YES'
                                        finish();
                                        //System.runFinalization();
                                    }
                                }).setNeutralButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        }).setNegativeButton("백그라운드",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                System.out.println("백그라운드 상태로 동작합니다");
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                startActivity(intent);
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
            if ( keyCode == KeyEvent.KEYCODE_HOME ){

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected  void onStop(){
        super.onStop();
        //calendar = new GregorianCalendar(Locale.KOREA);
        //cw.writeCsv(binder.getMainData(), "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + "MainData.csv");
        //cw.writeCsv(binder.GPSData(), "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" +"GPSData.csv");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(done==false) {
            Finish();
        }
    }

    private void setButton()
    {
        doneButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                done = true;
                //Finish();
                return true;
            }
        });
    }

    public void Finish()
    {
        List<String[]> GPSData = new ArrayList<String[]>();
        String[] firstSensor, firstGps;

        done = true;
        try {
            binder.setStop(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        PowerManager manager = (PowerManager)getSystemService(Context.POWER_SERVICE);

        boolean bScreenOn = manager.isScreenOn();

        stopService(serviceIntent);
        unbindService(connection);
        unregisterReceiver(restartService);
        restartService = null;


        if(bScreenOn){
            Intent Survay = new Intent(getApplicationContext(), Survay2.class);
            Survay.putExtra("mode", mode);
            Survay.putExtra("location", location);
            startActivity(Survay);
            finish();
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
        else
        {
            Intent popup = new Intent(getApplicationContext(), PopUp.class);
            popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            popup.putExtra("mode", mode);
            popup.putExtra("location", location);
            startActivity(popup);
            finish();
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void setText(){
        try {
            remain = binder.getRemained();
        }catch (RemoteException e) {
            e.printStackTrace();
        }

        timeRemain.setText(remain + " Second");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    private class ThreadTest extends Thread{

        @Override
        public void run()
        {
            int second = 0;

            while(!stopFlag)
            {
                //second++;
                if(binder == null) { continue; }
                handler.sendEmptyMessage(0);
                try{
                    if(done == true)
                    {
                        stopFlag = true;
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        ringtone.play();
                        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        vib.vibrate(1500);
                        Finish();
                        //startSurvay();
                        //handler2.sendEmptyMessage(0);
                    }
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                }
            }
        }

        public void Stop(){
            stopFlag = true;
        }
    }
}
