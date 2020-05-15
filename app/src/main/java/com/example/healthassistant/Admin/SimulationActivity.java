package com.example.healthassistant.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;
import com.example.healthassistant.R;

public class SimulationActivity extends AppCompatActivity {
    //由于没有服务器端，所以专门建立一个Activity 模拟接受系统广播
    private TextView mTv;

    private IntentFilter intentFilter;

    private TimeChangeReceiver timeChangeReceiver;


    public static SQLiteDatabase mUserDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);


        mTv = (TextView) findViewById(R.id.simulation_tv);

        UserDbHelper dbHelper = new UserDbHelper(this);
        mUserDb = dbHelper.getWritableDatabase();

        //接受系统广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间

        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);


    }


    public static void  UpdateTodayDb(){

        String tempTemp = "0";
        String tempText = "0";
        int tempTodayFlag = 0;
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE,tempTemp);
        values.put(UserContract.UserEntry.COLUMN_TODAY_FLAG,tempTodayFlag);
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE_FLAG,0);
        values.put(UserContract.UserEntry.COLUMN_TEXT,tempText);
        mUserDb.update(UserContract.UserEntry.TABLE_NAME,values, UserContract.UserEntry.COLUMN_GRADE+"=?",new String[]{"User"});


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeChangeReceiver);
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    //每过一分钟 触发
                    mTv.setText("1 min passed");
                    //SimulationActivity.UpdateTodayDb();
                    Toast.makeText(context, "1 min passed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    mTv.setText("今日数据已清理");
                    SimulationActivity.UpdateTodayDb();
                    Toast.makeText(context, "今日数据已清理", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    mTv.setText("system time zone changed");
                    //SimulationActivity.UpdateTodayDb();
                    Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
