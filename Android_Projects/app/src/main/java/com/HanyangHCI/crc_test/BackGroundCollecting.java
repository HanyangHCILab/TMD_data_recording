package com.HanyangHCI.crc_test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.app.Notification;
import android.app.NotificationManager;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class BackGroundCollecting extends Service {

    //Using the Gyroscope & Accelometer
    private SensorManager mSensormanager = null;

    //Using the Accelometer
    private SensorEventListener mGraLis;
    private Sensor mGravitySensor = null;
    private SensorEventListener mLinearAccLis;
    private Sensor mLinearAccSensor = null;
    //Using the Gyro
    private SensorEventListener mGyroLis;
    private Sensor mGyroSensor = null;
    //Using the baro
    //private SensorEventListener mBaroLis;
    //private Sensor mBaroSensor = null;
    //Using the Proxim
    private SensorEventListener mProxi;
    private Sensor mProxiSensor = null;
    //Using Magnetic
    private SensorEventListener mMagnetic;
    private Sensor mMagneticSensor = null;
    //Using RGB
    private SensorEventListener mLight;
    private Sensor mLightSensor = null;

    //Using RGB
    private SensorEventListener mStep;
    private Sensor mStepSensor = null;
    //Using GPS
    GPSTracker gps = null;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

    public float graX, linearAccX, gyroX, roll;
    public float graY, linearAccY, gyroY, pitch;
    public float graZ, linearAccZ, gyroZ, yaw;
    public float presure, height;
    public float proximity;
    public float magneticX, magneticY, magneticZ;
    public float lightLux;
    public float step;
    public double latitude, longitude;

    //timestampe and dt
    public float timestamp;
    public float dt;
    boolean stop = false;
    int remainedTime = 0;
    boolean first = true;

    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    Thread collectingThread;
    //GPSThread gpsThread;
    //Thread t;

    int mainFirst = 0;
    int GPSFirst = 0;
    boolean running;

    Calendar calendar;
    CSVWrite cw;
    String mode;
    String[] firstGps;
    String[] firstSensor;

    List<String[]> mainData = new ArrayList<String[]>();
    List<String[]> GPSData = new ArrayList<String[]>();
    List<String[]> GPSData2 = new ArrayList<String[]>();
    List<String[]> GPSData3 = new ArrayList<String[]>();

    public double startTime;
    Notification noti;
    Intent bService;
    PendingIntent sender;
    AlarmManager.AlarmClockInfo ac;
    AlarmManager am;
    String location, other;
    SimpleDateFormat change_format;
    Intent power;
    String q1;
    IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {
        @Override
        public int getRemained() throws RemoteException {
            return remainedTime;
        }
        @Override
        public void setStop(boolean i) throws RemoteException {
            stop = i;
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override public boolean onUnbind(Intent intent) {
        running = false;
        stop = true;
        return super.onUnbind(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(first==true) {
            first = false;
            power = intent;
            mode = intent.getStringExtra("mode");
            location = intent.getStringExtra("location");
            q1 = intent.getStringExtra("Q3");
        }
        //intent.putExtra("mode", mode);
        /*if(intent == null){
            return Service.START_STICKY;
        }else {
            mode = intent.getStringExtra("mode");
        }*/
        if(stop==false && mode!=null) {
            Calendar restart = Calendar.getInstance();
            restart.setTimeInMillis(System.currentTimeMillis());
            restart.add(Calendar.MILLISECOND, 16);

            bService.setAction(RestartService.ACTION_RESTART_SERVICE);
            //intent2.putExtra("mode", mode);
            sender = PendingIntent.getBroadcast(BackGroundCollecting.this, 0, bService, 0);
            //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, sender);
            ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), sender);
            am.setAlarmClock(ac, sender);
            //setServiceWatchdogTimer(true, 1000);


        /*if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);


            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("데이터 수집중")
                    .setContentText("수집중" + " " + remainedTime)
                    .setTicker("Running")

                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .build();
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            startForeground(1, notification);
        }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StartForground();
            }
            //setAlarmTimer();
            if(stop==true)
            {
                //am.cancel(sender);
            }
        }
        return START_NOT_STICKY;
    }



    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        //Using the Gyroscope & Accelometer
        mSensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer Sensor
        mGravitySensor = mSensormanager.getDefaultSensor((Sensor.TYPE_GRAVITY));
        //mAccLis = new SensorListener();
        mGraLis = new GravityListener();
        mLinearAccSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //mLinearAccLis = new SensorListener();
        mLinearAccLis = new LinearAccelometerListener();
        //Using the Gyroscope Sensor
        mGyroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mGyroLis = new SensorListener();
        mGyroLis = new GyroscopeListener();
        //Unsig Baro
        //mBaroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //mBaroLis = new SensorListener();
        //mBaroLis = new BarometerListener();
        //Using proxi
        mProxiSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //mProxi = new SensorListener();
        mProxi = new ProximityListener();
        //Using Magn
        mMagneticSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //mMagnetic = new SensorListener();
        mMagnetic = new MagneticListener();
        //Using RGB
        mLightSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //mLight = new SensorListener();
        mLight = new LightListener();
        //Step
        mStepSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mStep = new StepListener();

        calendar = new GregorianCalendar(Locale.KOREA);

        //Acc
        mSensormanager.registerListener(mGraLis, mGravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensormanager.registerListener(mLinearAccLis, mLinearAccSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Gyro
        mSensormanager.registerListener(mGyroLis, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Barometer
        //mSensormanager.registerListener(mBaroLis, mBaroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Proximity
        mSensormanager.registerListener(mProxi, mProxiSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Magnetic
        mSensormanager.registerListener(mMagnetic, mMagneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //RGB
        mSensormanager.registerListener(mLight, mLightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Step
        mSensormanager.registerListener(mStep, mStepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        startTime = System.currentTimeMillis();

        gps = new GPSTracker(BackGroundCollecting.this);
        cw = new CSVWrite();

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
        bService = new Intent(this, RestartService.class);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //remainedTime = 900;

        //StartForground();

        /*if(mainFirst == true && GPSFirst == true) {
            collectingThread = new Thread(new CollectingThread());
            collectingThread.start();
        }*/
        change_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensormanager.unregisterListener(mGraLis);
        mSensormanager.unregisterListener(mLinearAccLis);
        mSensormanager.unregisterListener(mGyroLis);
        //mSensormanager.unregisterListener(mBaroLis);
        mSensormanager.unregisterListener(mProxi);
        mSensormanager.unregisterListener(mMagnetic);
        mSensormanager.unregisterListener(mLight);
        mSensormanager.unregisterListener(mStep);
        //collectingThread.isInterrupted();
        gps.stopUsingGPS();
        stop = true;
        //bService = null;
        calendar = new GregorianCalendar(Locale.KOREA);
        cw.writeCsv(GPSData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" +"GPSData.csv");
        cw.writeCsv(mainData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" + "SensorData.csv");

        //cw.writeCsv(GPSData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" +"GPSData.csv");
        //cw.writeCsv(GPSData2, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" +"GPSData2.csv");
        //cw.writeCsv(GPSData3, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" +"GPSData3.csv");

        Intent intent = new Intent(BackGroundCollecting.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(BackGroundCollecting.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
        stopForeground(true);
        delayedEnd();
    }

    private void delayedEnd()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 10000);
    }

    public void StartForground() {

        /*Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(false)
                .setSmallIcon(android.R.color.transparent)

                //.setSmallIcon(R.drawable.picture)
                .build();
        startForeground(1,  notification);*/
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0});



            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("데이터 수집중")
                    .setContentText("수집중" + " " + remainedTime)
                    .setTicker("Running")
                    .setWhen(System.currentTimeMillis())
                    .setVibrate(new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0})
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .build();
            startForeground(1, noti);
        }

        if(mainFirst == 0 && GPSFirst == 0) {
            getMainData();
            getGPSData();
            collectingThread = new Thread(new CollectingThread());
            collectingThread.start();
        }

    }

    public void setServiceWatchdogTimer(boolean set, int timeout)
    {
        Intent intent;
        PendingIntent alarmIntent;
        intent = new Intent(); // forms and creates appropriate Intent and pass it to AlarmManager
        intent.setAction(RestartService.ACTION_RESTART_SERVICE);
        intent.setClass(this, RestartService.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(set)
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeout, alarmIntent);
        else
            am.cancel(alarmIntent);
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snwodeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new Builder(this, CHANNEL_ID);
        } else {
            builder = new Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("데이터 수집중")
                .setContentText("수집중" + " " + remainedTime)
                .setTicker("Running")
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }

    protected void setAlarmTimer(){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, RestartService.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StartForground();
        }
    }
    private void getMainData(){
        if(mainFirst == 0){
            mainFirst = 1;
            mainData.add(new String[] {"Time", "Year", "Month", "Day", "Hour", "Min", "Sec", "GraX", "GraY", "GraZ", "LAccX", "LAccY", "LAccZ", "GyroX", "GyroY", "GyroZ", "Height", "Proxi", "MagX", "MagY", "MagZ", "Light", "Step" , "Mode", "Survay1"});
        }
        else if(mainFirst == 1)
        {
            mainFirst = 2;
            calendar = new GregorianCalendar(Locale.KOREA);
            //firstSensor = new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + graX, "" + graY, "" + graZ, "" + linearAccX, "" + linearAccY, "" + linearAccZ, "" + gyroX, "" + gyroY, "" + gyroZ, "" + height, "" + proximity, "" + magneticX, "" + magneticY, "" + magneticZ, "" + lightLux, "" + step, mode, location, other};
            mainData.add( new String[] {"" + ((System.currentTimeMillis() - startTime)/1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + graX, "" + graY, "" + graZ, "" + linearAccX, "" + linearAccY, "" + linearAccZ, "" + gyroX, "" + gyroY, "" + gyroZ, "" + height, "" + proximity, "" + magneticX, "" + magneticY, "" + magneticZ, "" + lightLux, "" + step , mode, location});
        }
        else
        {
            calendar = new GregorianCalendar(Locale.KOREA);
            mainData.add( new String[] {"" + ((System.currentTimeMillis() - startTime)/1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + graX, "" + graY, "" + graZ, "" + linearAccX, "" + linearAccY, "" + linearAccZ, "" + gyroX, "" + gyroY, "" + gyroZ,  "" + height, "" + proximity, "" + magneticX, "" + magneticY, "" + magneticZ, "" + lightLux, "" + step,"",""});
        }
    }

    private void getGPSData(){

        if(GPSFirst == 0){
            GPSFirst = 1;
            GPSData.add(new String[]{"Time", "Year", "Month", "Day", "Hour", "Min", "Sec", "Latitude", "Longitude", "Provider", "Accuracy", "Altitude", "Bearing",  "Extra", "Speed", "WorldTime", "Mode", "Survay1"});
            //GPSData2.add(new String[]{"Time", "Year", "Month", "Day", "Hour", "Min", "Sec", "Latitude", "Longitude", "Provider", "Accuracy", "Altitude", "Bearing",  "Extra", "Speed", "WorldTime" , "Mode", "Survay1", "Survay2"});
            //GPSData3.add(new String[]{"Time", "Year", "Month", "Day", "Hour", "Min", "Sec", "Latitude", "Longitude", "Provider", "Accuracy", "Altitude", "Bearing",  "Extra", "Speed", "WorldTime" , "Mode", "Survay1", "Survay2"});
        }
        else if(GPSFirst == 1)
        {
            if(gps.isGetLocation())
            {
                GPSFirst = 2;
                calendar = new GregorianCalendar(Locale.KOREA);
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                /*if(gps.getLocation4()!=null) {
                    GPSData.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + calendar.get(Calendar.MONTH), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation4().getLatitude(), "" + gps.getLocation4().getLongitude(), "" + gps.getLocation4().getProvider(), "" + gps.getLocation4().getAccuracy(), "" + gps.getLocation4().getAltitude(), "" + gps.getLocation4().getBearing(), "" + gps.getLocation4().getExtras().getInt("satellites"), "" + gps.getLocation4().getSpeed(), "" + gps.getLocation4().getTime(), mode, location, other});
                }*/
                if(gps.getLocation()!=null) {
                    //firstGps = new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation().getLatitude(), "" + gps.getLocation().getLongitude(), "" + gps.getLocation().getProvider(), "" + gps.getLocation().getAccuracy(), "" + gps.getLocation().getAltitude(), "" + gps.getLocation().getBearing(), "" + gps.getLocation().getExtras().getInt("satellites"), "" + gps.getLocation().getSpeed(), change_format.format(gps.getLocation().getTime()), mode, location, other};
                    GPSData.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation().getLatitude(), "" + gps.getLocation().getLongitude(), "" + gps.getLocation().getProvider(), "" + gps.getLocation().getAccuracy(), "" + gps.getLocation().getAltitude(), "" + gps.getLocation().getBearing(), "" + gps.getLocation().getExtras().getInt("satellites"), "" + gps.getLocation().getSpeed(), change_format.format(gps.getLocation().getTime()), mode, location});
                }
                else
                {
                    GPSData.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "", "", "", "", "" , "" , "" , "", "", mode, location});
                }
                /*if(gps.getLocation2()!=null) {
                    GPSData2.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation2().getLatitude(), "" + gps.getLocation2().getLongitude(), "" + gps.getLocation2().getProvider(), "" + gps.getLocation2().getAccuracy(), "" + gps.getLocation2().getAltitude(), "" + gps.getLocation2().getBearing(), "" + gps.getLocation2().getExtras().getInt("satellites"), "" + gps.getLocation2().getSpeed(), change_format.format(gps.getLocation2().getTime()), mode, location, other});
                }
                else
                {
                    GPSData2.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "", "", "", "", "" , "" , "" , "", "", mode, location, other});
                }
                if(gps.getLocation3()!=null) {
                    GPSData3.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation3().getLatitude(), "" + gps.getLocation3().getLongitude(), "" + gps.getLocation3().getProvider(), "" + gps.getLocation3().getAccuracy(), "" + gps.getLocation3().getAltitude(), "" + gps.getLocation3().getBearing(), "" + gps.getLocation3().getExtras().getInt("satellites"), "" + gps.getLocation3().getSpeed(), change_format.format(gps.getLocation3().getTime()), mode, location, other});
                }
                else
                {
                    GPSData3.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "", "", "", "", "" , "" , "" , "", "", mode, location, other});
                }*/
            }
        }
        else
        {
            if(gps.isGetLocation())
            {
                calendar = new GregorianCalendar(Locale.KOREA);
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                /*if(gps.getLocation4()!=null) {
                    GPSData.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + calendar.get(Calendar.MONTH), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation4().getLatitude(), "" + gps.getLocation4().getLongitude(), "" + gps.getLocation4().getProvider(), "" + gps.getLocation4().getAccuracy(), "" + gps.getLocation4().getAltitude(), "" + gps.getLocation4().getBearing(), "" + gps.getLocation4().getExtras().getInt("satellites"), "" + gps.getLocation4().getSpeed(), "" + gps.getLocation4().getTime(), "", "", ""});
                }*/
                if(gps.getLocation()!=null) {
                    GPSData.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation().getLatitude(), "" + gps.getLocation().getLongitude(), "" + gps.getLocation().getProvider(), "" + gps.getLocation().getAccuracy(), "" + gps.getLocation().getAltitude(), "" + gps.getLocation().getBearing(), "" + gps.getLocation().getExtras().getInt("satellites"), "" + gps.getLocation().getSpeed(), "" + change_format.format(gps.getLocation().getTime()), "", ""});
                }
                /*if(gps.getLocation2()!=null) {
                    GPSData2.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation2().getLatitude(), "" + gps.getLocation2().getLongitude(), "" + gps.getLocation2().getProvider(), "" + gps.getLocation2().getAccuracy(), "" + gps.getLocation2().getAltitude(), "" + gps.getLocation2().getBearing(), "" + gps.getLocation2().getExtras().getInt("satellites"), "" + gps.getLocation2().getSpeed(), "" + change_format.format(gps.getLocation2().getTime()), "", "", ""});
                }
                if(gps.getLocation3()!=null) {
                    GPSData3.add(new String[]{"" + ((System.currentTimeMillis() - startTime) / 1000), "" + calendar.get(Calendar.YEAR), "" + (calendar.get(Calendar.MONTH) + 1), "" + calendar.get(Calendar.DAY_OF_MONTH), "" + calendar.get(Calendar.HOUR_OF_DAY), "" + calendar.get(Calendar.MINUTE), "" + calendar.get(Calendar.SECOND), "" + gps.getLocation3().getLatitude(), "" + gps.getLocation3().getLongitude(), "" + gps.getLocation3().getProvider(), "" + gps.getLocation3().getAccuracy(), "" + gps.getLocation3().getAltitude(), "" + gps.getLocation3().getBearing(), "" + gps.getLocation3().getExtras().getInt("satellites"), "" + gps.getLocation3().getSpeed(), "" + change_format.format(gps.getLocation3().getTime()), "", "", ""});
                }*/
            }
        }
    }

    public class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_GRAVITY)
            {
                graX = event.values[0];
                graY = event.values[1];
                graZ = event.values[2];
            }
            else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
            {
                linearAccX = event.values[0];
                linearAccY = event.values[1];
                linearAccZ = event.values[2];
            }
            else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                gyroX = event.values[0];
                gyroY = event.values[1];
                gyroZ = event.values[2];

                dt = (event.timestamp - timestamp) * NS2S;
                timestamp = event.timestamp;

                if(dt - timestamp*NS2S != 0){
                    pitch = pitch + gyroY*dt;
                    roll = roll + gyroX*dt;
                    yaw = yaw + gyroZ*dt;
                }
            }
            else if(event.sensor.getType() == Sensor.TYPE_PRESSURE)
            {
                presure = event.values[0];
                height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, presure);
            }
            else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
            {
                proximity = event.values[0];
            }
            else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                magneticX = event.values[0];
                magneticY = event.values[1];
                magneticZ = event.values[2];
            }
            else if(event.sensor.getType() == Sensor.TYPE_LIGHT)
            {
                lightLux = event.values[0];
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public class GravityListener extends Service  implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            graX = event.values[0];
            graY = event.values[1];
            graZ = event.values[2];


        /*Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", accX)
                + "           [Y]:" + String.format("%.4f", accY)
                + "           [Z]:" + String.format("%.4f", accZ));*/
            //TextView textAccX = (TextView)findViewById(R.id.AccX);
            //TextView textAccY = (TextView)findViewById(R.id.AccY);
            //TextView textAccZ = (TextView)findViewById(R.id.AccZ);

            //textAccX.setText("" + accX);
            //textAccY.setText("" + accY);
            //textAccZ.setText("" + accZ);
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class LinearAccelometerListener extends Service implements  SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            linearAccX = event.values[0];
            linearAccY = event.values[1];
            linearAccZ = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class GyroscopeListener extends Service implements  SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];

            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            if(dt - timestamp*NS2S != 0){
                pitch = pitch + gyroY*dt;
                roll = roll + gyroX*dt;
                yaw = yaw + gyroZ*dt;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class BarometerListener extends Service implements  SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            presure = event.values[0];
            height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, presure);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
    public class StepListener extends Service implements  SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            step = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


    public class ProximityListener extends Service implements  SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            proximity = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class MagneticListener extends Service implements  SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            magneticX = event.values[0];
            magneticY = event.values[1];
            magneticZ = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class LightListener extends Service implements  SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            lightLux = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    private class CollectingThread implements Runnable{

        @TargetApi(Build.VERSION_CODES.N)
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run()
        {
            int second = 0;
            int GPSsecond = 0;

            while(!stop)
            {
                second++;

                try{
                    getMainData();
                    if(second==60)
                    {
                        //Log.e("Finsh", ""+remainedTime);
                        second = 0;
                        GPSsecond++;
                        remainedTime++;
                        StartForground();
                        if(GPSsecond==5) {
                            if(stop==true)
                            {
                                am.cancel(sender);
                            }
                            getGPSData();
                            GPSsecond=0;
                        }
                        //collecting.handler.sendEmptyMessage(0);
                    }
                    Thread.sleep(16);
                }catch (InterruptedException e){
                    //getMainData();
                }
            }
        }

        public void Stop(){
            stop = true;
        }
    }
}
