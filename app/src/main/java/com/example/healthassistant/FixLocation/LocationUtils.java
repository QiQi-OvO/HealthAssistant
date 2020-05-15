package com.example.healthassistant.FixLocation;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


public class LocationUtils {
    public static String getCityName(double lon, double lat, String key) throws NoSuchAlgorithmException, KeyManagementException, IOException, JSONException {
        String str = String.format("https://restapi.amap.com/v3/geocode/regeo?output=json&location=%f,%f&key=%s&radius=1000&extensions=all",lon,lat,key);
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new java.security.SecureRandom());

        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        URL url = new URL(str);
        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setSSLSocketFactory(ssf);

        // 设置通用的请求属性
        httpsURLConnection.setRequestProperty("accept", "*/*");
        httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
        httpsURLConnection.setRequestProperty("content-Type", "application/json");
        httpsURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        final BufferedReader[] bufferedReader = {null};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ok");

                    InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
                    bufferedReader[0]= new BufferedReader(inputStreamReader);

                    //bufferedReader[0] = new BufferedReader(
                    //       new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8")
                    // );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        while (thread.isAlive()){

        }
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader[0].readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader[0].close();
        httpsURLConnection.disconnect();

        JSONObject jsonObject1 = new JSONObject(stringBuilder.toString());
        Log.i("andly","---3---");
        String city = jsonObject1.getJSONObject("regeocode").getJSONObject("addressComponent").getString("city");
        Log.i("andly",city);
        return city;
    }
}