package com.example.healthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEt2Name,mEt2Account,mEt2Pwd,mEt2ConfirmPwd,mEt2Phone;
    private Button mBtn2Register;
    private RadioGroup mRg2Grade;
    private SQLiteDatabase mDb;
    private String userName,userAccount,userPassword,userConfirm,userGrade,userPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserDbHelper dbHelper = new UserDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mEt2Name = (EditText)findViewById(R.id.reg_name);
        mEt2Account = (EditText)findViewById(R.id.reg_account);
        mEt2Pwd = (EditText) findViewById(R.id.reg_pwd);
        mEt2ConfirmPwd = (EditText) findViewById(R.id.reg_confirmpwd);
        mEt2Phone = (EditText) findViewById(R.id.reg_phonenumber);
        mRg2Grade = (RadioGroup) findViewById(R.id.reg_radiogroup);
        mRg2Grade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempBtn = (RadioButton) findViewById(checkedId);
                //Toast.makeText(RegisterActivity.this,tempBtn.getText(),Toast.LENGTH_SHORT).show();
                userGrade = tempBtn.getText().toString();
            }
        });
        mBtn2Register = (Button) findViewById(R.id.reg_register_btn);
        mBtn2Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                getRegInfo();
                if (!isExit()) {
                    if (userPassword.equals(userConfirm)) {
                        InsertRegData();
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "请重新检查密码是否统一", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "用户名存在请更换", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void getRegInfo(){
        userName = mEt2Name.getText().toString();
        userAccount = mEt2Account.getText().toString();
        userPassword = mEt2Pwd.getText().toString();
        userConfirm = mEt2ConfirmPwd.getText().toString();
        userPhone = mEt2Phone.getText().toString();
    }
    private void InsertRegData(){
        String tempTemperature = "0";
        int tempFlag = 0;
        String tempLoc = "空";
        int tempTempFlag = 1;
        String tempText = "空";
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USER_NAME,userName);
        values.put(UserContract.UserEntry.COLUMN_ACCOUNT,userAccount);
        values.put(UserContract.UserEntry.COLUMN_GRADE,userGrade);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD,userPassword);
        values.put(UserContract.UserEntry.COLUMN_PHONE,userPhone);
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE,tempTemperature);
        values.put(UserContract.UserEntry.COLUMN_TODAY_FLAG,tempFlag);
        values.put(UserContract.UserEntry.COLUMN_LOCATION,tempLoc);
        values.put(UserContract.UserEntry.COLUMN_TEMPERATURE_FLAG,tempTempFlag);
        values.put(UserContract.UserEntry.COLUMN_TEXT,tempText);
        mDb.beginTransaction();
        mDb.insert(UserContract.UserEntry.TABLE_NAME,null,values);
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }
    private boolean isExit(){
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME,new String[] {UserContract.UserEntry.COLUMN_PASSWORD,UserContract.UserEntry.COLUMN_GRADE},
                UserContract.UserEntry.COLUMN_ACCOUNT+"=?",new String[]{userName}, null, null, null);
        if(cursor.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }
}
