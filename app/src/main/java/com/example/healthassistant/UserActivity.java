package com.example.healthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;
import com.example.healthassistant.FixLocation.LocService;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    private TextView mTv;
    private EditText mTv2Loc,mEt2Temp,mEt2Text;
    private String userAccount;//用户名
    public static double lat,lon;
    private Button mBtn2GetLoc,mBtn2Submit,mBtn2Text,mBtn2Phone;
    private Intent getLoc;
    private RadioGroup mRg2TempFlag;
    private int TempFlag;
    private SQLiteDatabase mDb;
    private LocService.MyBinder binder;
    private ServiceConnection conn = new ServiceConnection() {

        //Activity与Service断开连接时回调该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("service", " ------Service DisConnected-------");
        }

        //Activity与Service连接成功时回调该方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("service", " ------Service Connected------ - ");
            binder = (LocService.MyBinder) service;
        }
    };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        UserDbHelper dbHelper = new UserDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mDb = dbHelper.getReadableDatabase();

        mTv = (TextView) findViewById(R.id.user_account);
        mTv2Loc = (EditText) findViewById(R.id.user_location_edit);
        mBtn2GetLoc = (Button) findViewById(R.id.user_locationbtn);
        mBtn2Submit = (Button) findViewById(R.id.user_submit);
        mBtn2Text = (Button) findViewById(R.id.user_message_btn);
        mBtn2Phone = (Button) findViewById(R.id.user_phone_button);
        mRg2TempFlag = (RadioGroup) findViewById(R.id.user_radiogroup);
        mEt2Temp = (EditText)findViewById(R.id.user_temperature);
        mEt2Text = (EditText) findViewById(R.id.user_tips);




        mRg2TempFlag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempBtn = (RadioButton) findViewById(checkedId);
                //Toast.makeText(UserActivity.this,tempBtn.getText().toString(),Toast.LENGTH_SHORT).show();
                if (tempBtn.getText().toString() .equals("体温正常") ){
                    TempFlag = 1;
                }
                else{
                    TempFlag = 0;
                }
            }
        });



        getLoc=getIntent();
        lat = getLoc.getDoubleExtra("lat",0);
        lon = getLoc.getDoubleExtra("lon",0);
        userAccount = getLoc.getStringExtra("account");
        mTv.setText(userAccount);

        final Intent intent = new Intent(UserActivity.this, LocService.class);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        mBtn2GetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binder.getCity();
                mTv2Loc.setText(city);
            }
        });
        mBtn2Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int passInfo = UpdateTodayDb();
                if (passInfo ==0){
                    Toast.makeText(UserActivity.this,"体温过低，重新填写",Toast.LENGTH_SHORT).show();
                }
                else if(passInfo ==1){
                    Toast.makeText(UserActivity.this,"今日已提交成功",Toast.LENGTH_SHORT).show();
                    mBtn2Submit.setEnabled(false);
                    mBtn2Submit.setText("已完成今日报送");
                }
                else{
                    Toast.makeText(UserActivity.this,"体温异常，请继续填写相关内容",Toast.LENGTH_SHORT).show();
                    mBtn2Submit.setEnabled(false);
                    mBtn2Submit.setText("已完成今日报送");
                    Intent toRecords = new Intent(UserActivity.this,TravelRecordsActivity.class);
                    toRecords.putExtra("account",userAccount);
                    startActivity(toRecords);
                }

            }
        });
        mBtn2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tips = getAdminTips();
                Toast.makeText(UserActivity.this,tips,Toast.LENGTH_LONG).show();


            }
        });
        mBtn2Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = getAdminPhone();
                String adminName = getAdminName();
                //Toast.makeText(UserActivity.this,phoneNum,Toast.LENGTH_SHORT).show();
                addContact(adminName,phoneNum);
            }
        });
        setSubmitBtn();
    }



    private void setSubmitBtn(){
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME,new String[] {UserContract.UserEntry.COLUMN_TODAY_FLAG},
                UserContract.UserEntry.COLUMN_ACCOUNT+"=?",new String[]{userAccount}, null, null, null);
        int course_count = cursor.getCount();//数据库行数
        cursor.moveToFirst();
        int tempFlag = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_TODAY_FLAG));
        if (tempFlag ==1){
            mBtn2Submit.setEnabled(false);
            mBtn2Submit.setText("已完成今日报送");
        }
    }
    private int UpdateTodayDb(){
        //0  体温数据有误
        //1正常
        // 2异常
        String tempTemp = mEt2Temp.getText().toString();
        String tempLoc = mTv2Loc.getText().toString();
        String tempText = mEt2Text.getText().toString();
        int passInfo = 1;
        int tempTodayFlag = 1;

        //防止高于37度的体温选择了体温是否正常增加一次判断。判断是否体温异常正常。
        double Temp = Double.valueOf(tempTemp);
        if (Temp>37){
            TempFlag = 0;
            passInfo =2;
        }
        else if (Temp<35){
            return 0;
        }
        else{
            TempFlag = 1;
        }
        //Toast.makeText(UserActivity.this,TempFlag+"",Toast.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE,tempTemp);
        values.put(UserContract.UserEntry.COLUMN_TODAY_FLAG,tempTodayFlag);
        values.put(UserContract.UserEntry.COLUMN_LOCATION,tempLoc);
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE_FLAG,TempFlag);
        values.put(UserContract.UserEntry.COLUMN_TEXT,tempText);
        mDb.update(UserContract.UserEntry.TABLE_NAME,values, UserContract.UserEntry.COLUMN_ACCOUNT+"=?",new String[]{userAccount});
        return passInfo;
    }

    private String getAdminPhone() {
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME, new String[]{UserContract.UserEntry.COLUMN_PHONE},
                UserContract.UserEntry.COLUMN_GRADE + "=?", new String[]{"Admin"}, null, null, null);
        int course_count = cursor.getCount();//数据库行数
        if (course_count == 0) {
            //没有查询到相应的用户名
            return "暂无管理员";
        } else {
            cursor.moveToFirst();
            String temp_phone = "";
            for (int i = 0; i < course_count; i++) {
                temp_phone = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PHONE));
                cursor.moveToNext();
            }
            return temp_phone;
        }
    }
    private String getAdminName() {
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME, new String[]{UserContract.UserEntry.COLUMN_USER_NAME},
                UserContract.UserEntry.COLUMN_GRADE + "=?", new String[]{"Admin"}, null, null, null);
        int course_count = cursor.getCount();//数据库行数
        if (course_count == 0) {
            //没有查询到相应的用户名
            return "暂无管理员";
        } else {
            cursor.moveToFirst();
            String temp_name = "";
            for (int i = 0; i < course_count; i++) {
                temp_name = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME));
                cursor.moveToNext();
            }
            return temp_name;
        }
    }
    private String getAdminTips() {
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME, new String[]{UserContract.UserEntry.COLUMN_TEXT},
                UserContract.UserEntry.COLUMN_GRADE + "=?", new String[]{"Admin"}, null, null, null);
        int course_count = cursor.getCount();//数据库行数
        if (course_count == 0) {
            //没有查询到相应的用户名
            return "暂无管理员";
        } else {
            cursor.moveToFirst();
            String temp_text = "";
            for (int i = 0; i < course_count; i++) {
                temp_text = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_TEXT));
                cursor.moveToNext();
            }
            return temp_text;
        }
    }



    private void addContact(String name,String phone) {
        try {
            //使用事务添加联系人
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            Uri dataUri = Uri.parse("content://com.android.contacts/data");

            ContentResolver resolver = getContentResolver();
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            //为了便于Android中进行批量数据库操作时效率更高，Android中推荐使用ContentProviderOperation
            ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
                    .withValue("account_name", null)
                    .build();
            operations.add(op1);

            //依次是姓名，号码，邮编
            ContentProviderOperation op2 = ContentProviderOperation.newInsert(dataUri)
                    .withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/name")
                    .withValue("data2", name)
                    .build();
            operations.add(op2);

            ContentProviderOperation op3 = ContentProviderOperation.newInsert(dataUri)
                    .withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                    .withValue("data1", phone)
                    .withValue("data2", "2")
                    .build();
            operations.add(op3);

            //将上述内容添加到手机联系人中~
            resolver.applyBatch("com.android.contacts", operations);
            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("provider", e.getMessage());
        }
    }



}
