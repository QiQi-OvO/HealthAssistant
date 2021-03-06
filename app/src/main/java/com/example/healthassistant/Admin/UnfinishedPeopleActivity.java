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

import com.example.healthassistant.DataUtil.UserContract;
import com.example.healthassistant.DataUtil.UserDbHelper;
import com.example.healthassistant.R;

import java.util.ArrayList;

public class UnfinishedPeopleActivity extends AppCompatActivity {

    private ListView mLv;
    private ArrayList<String> account_list = new ArrayList<String>();
    private ArrayList<String> name_list = new ArrayList<String>();
    private ArrayList<String> phone_list = new ArrayList<String>();
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unfinished_people);
        mLv = (ListView) findViewById(R.id.unfinished_lv);

        UserDbHelper dbHelper = new UserDbHelper(this);
        mDb = dbHelper.getReadableDatabase();
        getData();
        mLv.setAdapter(new UnfinishedAdapter(UnfinishedPeopleActivity.this,account_list,name_list,phone_list));

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addContact(name_list.get(position),phone_list.get(position));
                Toast.makeText(UnfinishedPeopleActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void getData(){
        //Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME, new String[]{UserContract.UserEntry.COLUMN_USER_NAME, UserContract.UserEntry.COLUMN_ACCOUNT, UserContract.UserEntry.COLUMN_PHONE},
           //     UserContract.UserEntry.COLUMN_GRADE + "=? AND"+ UserContract.UserEntry.COLUMN_TODAY_FLAG+"=?", new String[]{"User","0"}, null, null, null);

        String whereClause = UserContract.UserEntry.COLUMN_GRADE+"=? AND "+UserContract.UserEntry.COLUMN_TODAY_FLAG+"=? ";
        Cursor cursor = mDb.query(UserContract.UserEntry.TABLE_NAME,new String[]{UserContract.UserEntry.COLUMN_USER_NAME, UserContract.UserEntry.COLUMN_ACCOUNT, UserContract.UserEntry.COLUMN_PHONE},whereClause,
                new String[]{"User","0"} ,null,null,null);


        int course_count = cursor.getCount();//数据库行数
        if (course_count == 0) {
            //没有查询到相应的用户名;
        } else {
            cursor.moveToFirst();
            String temp_name = "";
            String temp_account="";
            String temp_phone="";
            for (int i = 0; i < course_count; i++) {
                temp_name = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME));
                temp_account = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT));
                temp_phone = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PHONE));
                account_list.add(temp_account);
                name_list.add(temp_name);
                phone_list.add(temp_phone);
                cursor.moveToNext();
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
