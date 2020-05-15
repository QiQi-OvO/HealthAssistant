package com.example.healthassistant.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.healthassistant.DataUtil.InfoContract;
import com.example.healthassistant.DataUtil.InfoDbHelper;
import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;
import com.example.healthassistant.R;

import java.util.ArrayList;

public class TempAbnormalActivity extends AppCompatActivity {


    private ListView mLv;
    private ArrayList<String> account_list = new ArrayList<String>();
    private ArrayList<String> name_list = new ArrayList<String>();
    private ArrayList<String> phone_list = new ArrayList<String>();
    private ArrayList<String> temp_list = new ArrayList<String>();
    private ArrayList<String> time_list = new ArrayList<String>();
    private ArrayList<String> transport_list = new ArrayList<String>();
    private ArrayList<String> remark_list = new ArrayList<String>();
    private SQLiteDatabase mUserDb,mInfoDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_abnormal);
        mLv = (ListView)findViewById(R.id.abnormal_lv);
        UserDbHelper UserdbHelper = new UserDbHelper(this);
        InfoDbHelper InfodbHelper = new InfoDbHelper(this);
        mUserDb = UserdbHelper.getReadableDatabase();
        mInfoDb = InfodbHelper.getReadableDatabase();
        getUserData();
        getInfoData();
        mLv.setAdapter(new AbnormalAdapter(TempAbnormalActivity.this,account_list,name_list,phone_list,temp_list,time_list,transport_list,remark_list));
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addContact(name_list.get(position),phone_list.get(position));
                Toast.makeText(TempAbnormalActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
            }
        });


    }



    private void getUserData(){
        String whereClause = UserContract.UserEntry.COLUMN_GRADE+"=? AND "+UserContract.UserEntry.COLUMN_TEMPERATURE_FLAG+"=? ";
        Cursor cursor = mUserDb.query(UserContract.UserEntry.TABLE_NAME,new String[]{UserContract.UserEntry.COLUMN_USER_NAME, UserContract.UserEntry.COLUMN_ACCOUNT, UserContract.UserEntry.COLUMN_PHONE, UserContract.UserEntry.COLUMN_TEMPERATURE},whereClause,
                new String[]{"User","0"} ,null,null,null);


        int course_count = cursor.getCount();//数据库行数
        if (course_count == 0) {
            //没有查询到相应的用户名;
            account_list.add("没有异常出现");
            name_list.add("没有异常出现");
            phone_list.add("没有异常出现");
            temp_list.add("没有异常出现");
        } else {
            cursor.moveToFirst();
            String temp_name = "";
            String temp_account="";
            String temp_phone="";
            String temp_temp = "";
            for (int i = 0; i < course_count; i++) {
                temp_name = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME));
                temp_account = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT));
                temp_phone = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PHONE));
                temp_temp = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_TEMPERATURE));
                account_list.add(temp_account);
                name_list.add(temp_name);
                phone_list.add(temp_phone);
                temp_list.add(temp_temp);
                cursor.moveToNext();
            }

        }

    }
    private void getInfoData(){
        for(int i =0 ; i<account_list.size();i++){
            String whereClause = InfoContract.COLUMN_ACCOUNT +"=?";
            Cursor cursor = mInfoDb.query(InfoContract.TABLE_NAME,new String[]{InfoContract.COLUMN_TIME,InfoContract.COLUMN_TRANSPORT,InfoContract.COLUMN_REMARKS},whereClause,
                    new String[]{account_list.get(i)},null,null,null);
            int course_count = cursor.getCount();//数据库行数
            if (course_count == 0) {
                //没有查询到相应的用户名;
                time_list.add("没有异常出现");
                transport_list.add("没有异常出现");
                remark_list.add("没有异常出现");
            } else {
                cursor.moveToFirst();
                String temp_time = "";
                String temp_transport="";
                String temp_remarks="";
                for (int j = 0; j < course_count; j++) {
                    temp_time = cursor.getString(cursor.getColumnIndex(InfoContract.COLUMN_TIME));
                    temp_transport = cursor.getString(cursor.getColumnIndex(InfoContract.COLUMN_TRANSPORT));
                    temp_remarks = cursor.getString(cursor.getColumnIndex(InfoContract.COLUMN_REMARKS));
                    cursor.moveToNext();
                }
                time_list.add(temp_time);
                transport_list.add(temp_transport);
                remark_list.add(temp_remarks);
            }
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
