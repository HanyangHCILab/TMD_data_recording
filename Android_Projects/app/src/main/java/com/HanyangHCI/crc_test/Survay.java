package com.HanyangHCI.crc_test;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Survay extends Activity {

    private RadioGroup group1;
    private Button doneButton;
    private LinearLayout layout2;
    String mode;

    int q1 = -1;
    int q2 = 0;
    int done = 1;
    CSVWrite cw;
    Collecting collecting;
    Calendar calendar;

    List<String[]> mainData = new ArrayList<String[]>();

    String location, other;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survay);

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);*/

        mode = getIntent().getExtras().getString("mode");
        //layout1 = (LinearLayout)findViewById(R.id.Layout2);
        layout2 = (LinearLayout)findViewById(R.id.Layout1);

        group1 = (RadioGroup)findViewById(R.id.R1);

        doneButton = (Button)findViewById(R.id.Donebutton);
        cw = new CSVWrite();
        collecting = new Collecting();
        calendar = new GregorianCalendar(Locale.KOREA);

        RadioButtonSetting();
        doneButtonSetting();

        //layout1.setEnabled(false);
        //layout2.setEnabled(false);
    }

    private void RadioButtonSetting()
    {
        RadioGroup.OnCheckedChangeListener g1GroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                if(i == R.id.ArmButton)
                    q1 = 0;
                //else if(i == R.id.ChestButton)
                    //q1 = 1;
                else if(i == R.id.BagButton)
                    q1 = 2;
                else if(i == R.id.HandButton)
                    q1 = 3;
                else if(i == R.id.PocketButton)
                    q1 = 4;
            }
        };

       /* RadioGroup.OnCheckedChangeListener g2GroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                if(i == R.id.YesButton)
                    q2 = 0;
                else if(i == R.id.NoButton)
                    q2 = 1;
            }
        };*/

        group1.setOnCheckedChangeListener(g1GroupButtonChangeListener);
        //group2.setOnCheckedChangeListener(g2GroupButtonChangeListener);
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

    private void doneButtonSetting()
    {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(done==0 && q2!=-1)
                {
                    done=1;
                }
                else if(done==1 && q1!=-1)
                {
                    mainData.add(new String[] {"Q1", "Q2"});
                    if(q1==0)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Etc", "Yes"});
                            location = "Etc";
                            other = "Yes";
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Etc", "No"});
                            location = "Etc";
                            other = "No";
                        }
                    }
                    else if(q1==1)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Chest", "Yes"});
                            location = "Chest";
                            other = "Yes";
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Chest", "No"});
                            location = "Chest";
                            other = "No";
                        }
                    }
                    else if(q1==2)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Bag", "Yes"});
                            location = "Bag";
                            other = "Yes";
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Bag", "No"});
                            location = "Bag";
                            other = "No";
                        }
                    }
                    else if(q1==3)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Hand", "Yes"});
                            location = "Hand";
                            other = "Yes";
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Hand", "No"});
                            location = "Hand";
                            other = "No";
                        }
                    }
                    else if(q1==4)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Pocket", "Yes"});
                            location = "Pocket";
                            other = "Yes";
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Pocket", "No"});
                            location = "Pocket";
                            other = "No";
                        }
                    }
                    //cw.writeCsv(mainData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" + "SurveyData.csv");
                    Intent intent = new Intent(Survay.this, Collecting.class);
                    intent.putExtra("mode", mode);
                    intent.putExtra("location", location);
                    //intent.putExtra("other", other);
                    startActivity(intent);
                    finish();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
                /*if(q1!=-1 && q2 != -2) {
                    mainData.add(new String[] {"Q1", "Q2"});
                    if(q1==0)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Arm", "Yes"});
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Arm", "No"});
                        }
                    }
                    else if(q1==1)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Chest", "Yes"});
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Chest", "No"});
                        }
                    }
                    else if(q1==2)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Bag", "Yes"});
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Bag", "No"});
                        }
                    }
                    else if(q1==3)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Hand", "Yes"});
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Hand", "No"});
                        }
                    }
                    else if(q1==4)
                    {
                        if(q2 ==0) {
                            mainData.add(new String[]{"Pocket", "Yes"});
                        }
                        else if(q2==1)
                        {
                            mainData.add(new String[]{"Pocket", "No"});
                        }
                    }
                    cw.writeCsv(mainData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" + "SurveyData.csv");
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Survay.this);
                    alert_confirm.setMessage("데이터 수집 완료").setCancelable(true);
                    finish();
                }*/
            }
        });

    }
}
