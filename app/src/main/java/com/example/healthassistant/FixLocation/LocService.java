package com.example.healthassistant.FixLocation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.healthassistant.FixLocation.LocationUtils;
import com.example.healthassistant.UserActivity;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class LocService extends Service {
    private String city;
    private MyBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        public String getCity() {
            return city;
        }
    }
    public LocService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            public void run() {
                try {
                    city = LocationUtils.getCityName(UserActivity.lon,UserActivity.lat,"这里放入你的高德Key");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    }

