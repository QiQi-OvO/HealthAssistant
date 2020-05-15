package com.example.healthassistant.DataUtil;
import android.provider.BaseColumns;
public class UserContract {
    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_table";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_ACCOUNT = "account";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_GRADE = "grade";//用户跟管理员进行区分
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_TEMPERATURE= "temperature";
        public static final String COLUMN_TODAY_FLAG = "flag";//判断今天有没有进行报送
        public static final String COLUMN_LOCATION= "location";
        public static final String COLUMN_TEMPERATURE_FLAG= "temperature_flag";
        public static final String COLUMN_TEXT= "text";
    }

}
