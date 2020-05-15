package com.example.healthassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText mEt2user_name,mEt2pwd;
    private Button mBtn2login, mBtn2register,mBtn2loginlast;
    private CheckBox mCb2remember;
    private SharedPreferences mSharedPre;
    private SharedPreferences.Editor mEditor;
    private SQLiteDatabase mDb;
    String userName;
    String userPwd;
    private double lat,lon;

    private LocationManager locationManager;
    private String locationProvider;
    private static final int OPEN_SET_REQUEST_CODE = 100;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_CONTACTS
    };//权限数组
    private void initPermissions() {
        if (lacksPermission()) {//判断是否拥有权限
            //请求权限，第二参数权限String数据，第三个参数是请求码便于在onRequestPermissionsResult 方法中根据code进行判断
            ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
        } else {
            //拥有权限执行操作
        }
    }

    //如果返回true表示缺少权限
    public boolean lacksPermission() {
        for (String permission : permissions) {
            //判断是否缺少权限，true=缺少权限
            if(ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){//响应Code
            case OPEN_SET_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this,"未拥有相应权限",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    //拥有权限执行操作
                } else {
                    Toast.makeText(MainActivity.this,"未拥有相应权限",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }














    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEt2user_name = (EditText) findViewById(R.id.user_name);
        mEt2pwd = (EditText) findViewById(R.id.pwd);
        mBtn2login = (Button) findViewById(R.id.login_btn);
        mBtn2register = (Button) findViewById(R.id.register_btn);
        mBtn2loginlast = (Button) findViewById(R.id.loginlast_btn) ;
        mCb2remember = (CheckBox) findViewById(R.id.remember_btn);
        mSharedPre = getSharedPreferences("data",MODE_PRIVATE);
        mEditor = mSharedPre.edit();
        UserDbHelper dbHelper = new UserDbHelper(this);
        mDb = dbHelper.getReadableDatabase();
        //Toast.makeText(mContext,whereClause,Toast.LENGTH_LONG).show();




        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);





        mBtn2login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录按钮
                getUseInfo();
                int passCheck = isExit();
                if (passCheck ==0){
                    Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }
                else if (passCheck ==1){
                    Intent jump2User = new Intent (MainActivity.this,UserActivity.class);
                    jump2User.putExtra("account",userName);
                    jump2User.putExtra("lat",lat);
                    jump2User.putExtra("lon",lon);
                    startActivity(jump2User);
                    //Toast.makeText(MainActivity.this,"用户登录",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent jump2Admin = new Intent (MainActivity.this,AdminActivity.class);
                    jump2Admin.putExtra("account",userName);
                    startActivity(jump2Admin);
                    //Toast.makeText(MainActivity.this,"管理员登录",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mBtn2register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册按钮
                //getUseInfo();
                //Toast.makeText(MainActivity.this,"注册 用户名"+userName+"密码"+userPwd,Toast.LENGTH_SHORT).show();
                Intent toRegister = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(toRegister);
            }
        });
        mBtn2loginlast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录上一次记住帐号按钮
                getUseInfo();
                //Toast.makeText(MainActivity.this,"登录上一次记住帐号 用户名"+userName+"密码"+userPwd,Toast.LENGTH_SHORT).show();
                mEt2user_name.setText(mSharedPre.getString("Username",""));
                mEt2pwd.setText(mSharedPre.getString("Password",""));
            }
        });
        mCb2remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCb2remember.getId()== buttonView.getId() && isChecked == true){
                    getUseInfo();
                    mEditor.putString("Username",userName);
                    mEditor.putString("Password",userPwd);
                    mEditor.apply();
                }
                else{
                    mEditor.clear();
                }
            }
        });
    }



    LocationListener locationListener =new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            lat = location.getLatitude();
            lon = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void getUseInfo(){
        userName = mEt2user_name.getText().toString();
        userPwd = mEt2pwd.getText().toString();
    }
    private int isExit(){
        //判断用户是否存在，如果不存在返回0
        //如果存在用户返回1 管理员返回2
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME,new String[] {UserContract.UserEntry.COLUMN_PASSWORD,UserContract.UserEntry.COLUMN_GRADE},
                UserContract.UserEntry.COLUMN_ACCOUNT+"=?",new String[]{userName}, null, null, null);
        int course_count = cursor.getCount();//数据库行数
        if (course_count ==0 ){
            //没有查询到相应的用户名
            return 0;
        }
        else{
            cursor.moveToFirst();
            String temp_password = "";
            String temp_grade = "";
            for (int i=0;i<course_count;i++){
                temp_password = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD));
                temp_grade = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_GRADE));
                cursor.moveToNext();
            }
            if (temp_password.equals(userPwd)){
                //密码正确判断身份
                if (temp_grade.equals("Admin")){
                    return 2;
                }
                else{
                    return 1;
                }
            }
            else{
                //密码错误
                return 0;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }
}
