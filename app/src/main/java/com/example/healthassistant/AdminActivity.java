package com.example.healthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthassistant.Admin.AllUserActivity;
import com.example.healthassistant.Admin.SimulationActivity;
import com.example.healthassistant.Admin.TempAbnormalActivity;
import com.example.healthassistant.Admin.UnfinishedPeopleActivity;
import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;

public class AdminActivity extends AppCompatActivity {
    private TextView mTv;
    private String userAccount;
    private Button mBtn2Unfinished,mBtn2Abnormal,mBtn2Alluser,mBtn2SubmbitTips,mBtn2Fnish;
    private EditText mEt2Tips;
    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mTv = (TextView) findViewById(R.id.admin_account);
        Intent intent=getIntent();
        userAccount = intent.getStringExtra("account");
        mTv.setText(userAccount);
        UserDbHelper dbHelper = new UserDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mDb = dbHelper.getReadableDatabase();

        mBtn2Unfinished = (Button) findViewById(R.id.admin_btn1);
        mBtn2Abnormal = (Button) findViewById(R.id.admin_btn2);
        mBtn2Alluser = (Button) findViewById(R.id.admin_btn3);
        mBtn2SubmbitTips = (Button) findViewById(R.id.admin_submit_tips);
        mBtn2Fnish = (Button) findViewById(R.id.admin_finsh);
        mBtn2Fnish.setEnabled(false);
        mEt2Tips = (EditText) findViewById(R.id.admin_tips);

        mBtn2Unfinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUn = new Intent(AdminActivity.this, UnfinishedPeopleActivity.class);
                startActivity(toUn);
            }
        });
        mBtn2Abnormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAb = new Intent(AdminActivity.this, TempAbnormalActivity.class);
                startActivity(toAb);
            }
        });
        mBtn2Alluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAll = new Intent(AdminActivity.this, AllUserActivity.class);
                startActivity(toAll);
            }
        });



        mBtn2SubmbitTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tips = mEt2Tips.getText().toString();
                ContentValues values = new ContentValues();
                values.put(UserContract.UserEntry.COLUMN_TEXT,tips);
                mDb.update(UserContract.UserEntry.TABLE_NAME,values, UserContract.UserEntry.COLUMN_ACCOUNT+"=?",new String[]{userAccount});
                Toast.makeText(AdminActivity.this,"成功发布",Toast.LENGTH_SHORT).show();
            }
        });
        mBtn2Fnish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSimulation = new Intent(AdminActivity.this, SimulationActivity.class);
                startActivity(toSimulation);
            }
        });

    }

}
