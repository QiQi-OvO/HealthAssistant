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

import com.example.healthassistant.DataUtil.InfoContract;
import com.example.healthassistant.DataUtil.InfoDbHelper;

public class TravelRecordsActivity extends AppCompatActivity {
    private TextView mTv;
    private EditText mEt2Time,mEt2Transport,mEt2Remarks;
    private Button mBtn2Summit;
    private String userAccount;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_records);

        mTv = (TextView) findViewById(R.id.records_account);
        mEt2Time = (EditText) findViewById(R.id.records_time);
        mEt2Transport = (EditText) findViewById(R.id.records_transport);
        mEt2Remarks = (EditText) findViewById(R.id.records_transport_remarks);
        mBtn2Summit = (Button) findViewById(R.id.records_submit);

        InfoDbHelper dbHelper = new InfoDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Intent getAccount = getIntent();
        userAccount = getAccount.getStringExtra("account");
        mTv.setText(userAccount);

        mBtn2Summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = mEt2Time.getText().toString();
                String transport = mEt2Transport.getText().toString();
                String remarks = mEt2Remarks.getText().toString();
                insertRecordsData(time,transport,remarks);
                Toast.makeText(TravelRecordsActivity.this,"谢谢配合",Toast.LENGTH_SHORT).show();

            }
        });








    }

    private void insertRecordsData(String time, String transport , String remarks){
        ContentValues values = new ContentValues();
        values.put(InfoContract.COLUMN_ACCOUNT,userAccount);
        values.put(InfoContract.COLUMN_TIME,time);
        values.put(InfoContract.COLUMN_TRANSPORT,transport);
        values.put(InfoContract.COLUMN_REMARKS,remarks);
        mDb.beginTransaction();
        mDb.insert(InfoContract.TABLE_NAME,null,values);
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }
}
