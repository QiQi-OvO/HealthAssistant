package com.example.healthassistant.DataUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.healthassistant.DataUtil.InfoContract;


public class InfoDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "e_info.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public InfoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_INFOTABLE_TABLE = "CREATE TABLE " + InfoContract.TABLE_NAME + " (" +
                InfoContract._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InfoContract.COLUMN_ACCOUNT+" TEXT, "+
                InfoContract.COLUMN_TIME+" TEXT,"+
                InfoContract.COLUMN_TRANSPORT+" TEXT,"+
                InfoContract.COLUMN_REMARKS+" TEXT"+
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_INFOTABLE_TABLE);
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
