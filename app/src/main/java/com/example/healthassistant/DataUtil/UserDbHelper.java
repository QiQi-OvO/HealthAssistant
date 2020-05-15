package com.example.healthassistant.DataUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.healthassistant.DataUtil.UserContract;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "e_user.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_USERRTABLE_TABLE = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.COLUMN_USER_NAME + " TEXT, "+
                UserContract.UserEntry.COLUMN_ACCOUNT+" TEXT, "+
                UserContract.UserEntry.COLUMN_GRADE+" TEXT, "+
                UserContract.UserEntry.COLUMN_PASSWORD+" TEXT, "+
                UserContract.UserEntry.COLUMN_PHONE+" TEXT, "+
                UserContract.UserEntry.COLUMN_TEMPERATURE+" TEXT, "+
                UserContract.UserEntry.COLUMN_LOCATION+" TEXT, "+
                UserContract.UserEntry.COLUMN_TEMPERATURE_FLAG+" INTEGER, "+
                UserContract.UserEntry.COLUMN_TEXT+" TEXT, "+
                UserContract.UserEntry.COLUMN_TODAY_FLAG+" INTEGER"+
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_USERRTABLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
