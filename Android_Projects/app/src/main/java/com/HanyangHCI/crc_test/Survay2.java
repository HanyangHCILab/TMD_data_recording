package com.HanyangHCI.crc_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Survay2 extends Activity {

    private TextView question;

    private LinearLayout layout;
    private Button doneButton;
    private RadioGroup group;
    String location, mode;
    String wo;
    Calendar calendar;
    CSVWrite cw;

    int q1 = -1;

    List<String[]> mainData = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survay2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        Intent intent2 = getIntent();
        mode = intent2.getExtras().getString("mode");
        location =  intent2.getExtras().getString("location");

        layout = (LinearLayout)findViewById(R.id.Layout3);
        doneButton = (Button)findViewById(R.id.Donebutton2);
        group = (RadioGroup)findViewById(R.id.R3);
        question = (TextView)findViewById(R.id.Q3);
        cw = new CSVWrite();

        startSurvay();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }


    private void startSurvay()
    {
        if(mode.equals("still"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 가만히 계셨으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 가만히 계셨으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 가만히 계셨으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 가만히 계셨으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("manualChar"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 수동 휠체어를 이용했으며 핸드폰을 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 수동 휠체어를 이용했으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 수동 휠체어를 이용했으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 수동 휠체어를 이용했으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("powerChar"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 전동 휠체어를 이용했으며 핸드폰을 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 전동 휠체어를 이용했으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 전동 휠체어를 이용했으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 전동 휠체어를 이용했으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("walking"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 계속 걸었으며 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 계속 걸었으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 계속 걸었으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 계속 걸었으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("metro"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 지하철을 이용하셨으며 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 지하철을 이용하셨으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 지하철을 이용하셨으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 지하철을 이용하셨으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("car"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 차를 이용하셨으며 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 차를 이용하셨으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 차를 이용하셨으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 차를 이용하셨으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        else if(mode.equals("bus"))
        {
            if(location.equals("Bag"))
            {
                question.setText("피험자분께서 데이터 수집동안 버스를 이용하셨으며 가방에 위치하셨습니까?");
            }
            else if(location.equals("Hand"))
            {
                question.setText("피험자분께서 데이터 수집동안 버스를 이용하셨으며 핸드폰을 손에 위치하셨습니까?");
            }
            else if(location.equals("Pocket"))
            {
                question.setText("피험자분께서 데이터 수집동안 버스를 이용하셨으며 핸드폰을 주머니에 위치하셨습니까?");
            }
            else if (location.equals("Etc"))
            {
                question.setText("피험자분께서 데이터 수집동안 버스를 이용하셨으며 핸드폰을 같은 곳에 위치하셨습니까?");
            }
        }
        RadioButtonSetting();
        doneButtonSetting();
    }

    private void RadioButtonSetting()
    {
        RadioGroup.OnCheckedChangeListener g2GroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                if(i == R.id.YesButton)
                    q1 = 0;
                else if(i == R.id.NoButton)
                    q1 = 1;
            }
        };

        group.setOnCheckedChangeListener(g2GroupButtonChangeListener);
    }
    private void doneButtonSetting()
    {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(q1 == 0 || q1 == 1)
                {
                    if(q1 ==0) {
                        wo = "Yes";
                    }
                    else if(q1 ==1)
                    {
                        wo = "No";
                    }
                    mainData.add(new String[]{"Survay3"});
                    mainData.add(new String[]{wo});
                    calendar = new GregorianCalendar(Locale.KOREA);
                    cw.writeCsv(mainData, "" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DATE) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND) + "_" + mode + "_" + "Confirm.csv");
                    finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }
}
