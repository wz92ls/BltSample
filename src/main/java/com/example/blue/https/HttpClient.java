package com.example.blue.https;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import android.util.Log;
import com.example.blue.ble.BLE_DeviceControlActivity;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier ;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpClient {
    public static class Https_gongyitech{
        private final static String TAG = Https_gongyitech.class.getSimpleName();

        static private String HTTPS_DEVICESEND="https://openbfapi.gongyitech.com/other/deviceSend";
        static private String HTTPS_DEVICERETURN="https://openbfapi.gongyitech.com/other/deviceReturn";
        static private String HTTPS_AUTOTESTINGQ="https://openbfapi.gongyitech.com/urine/autotestingq";
        static private String HTTPS_CORRESPOND="https://openbfapi.gongyitech.com/urine/correspond";
        static private String HTTPS_DEVICECONTROL="https://openbfapi.gongyitech.com/urine/deviceControl";
        static private String HTTPS_TESTINGR="https://openbfapi.gongyitech.com/urine/testingr";

        static private String https_arg;
        static private String https_check;
        static private String https_ticket="unHj2qeiMcH0JLyefFFJx2T6EyRKejukQJ2eCD3DXRHTWZz8Uj/KQ97LX3ApKJWX";

        public static  String Post_deviceSend() throws Exception{
            Map<String,String> params=new HashMap<String,String>();;
            params.put("ticket",https_ticket);
            params.put("status","BF");
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICESEND,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }

        public static  String Post_deviceReturn(String receive) throws Exception{
            Map<String,String> params=new HashMap<String,String>();
            params.put("ticket",https_ticket);
            params.put("receive",receive);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            https_arg=Parse_return(getreturn,"msg");
            return Parse_return(getreturn,"msg");
        }

        public static  String Post_autotestingq(String type) throws Exception{
            Map<String,String> params=new HashMap<String,String>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("check",https_check);
            params.put("type",type);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"value");
        }

        public static  String Post_correspond(String instruct) throws Exception{
            Map<String,String> params=new HashMap<String,String>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("check",https_check);
            params.put("instruct",instruct);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"reslut");
        }

        public static  String Post_deviceControl(String type,String stauts,String value) throws Exception{
            Map<String,String> params=new HashMap<String,String>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("type",type);
            params.put("stauts",stauts);
            params.put("value",value);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }

        public static  String Post_testingr(String receive) throws Exception{
            Map<String,String> params=new HashMap<String,String>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("receive",receive);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }


        private static String Parse_return(String instr,String key)
        {
            try {
                JSONObject jsonObject = new JSONObject(instr);
                String value = jsonObject.getString(key);
                return value;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier ();
    private static X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };
    private static   X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
    private static HttpsURLConnection conn=null;
    public static InputStream sendPOSTRequestForInputStream(String path, Map<String, String> params, String encoding) throws Exception{
// 1> 组拼实体数据
//method=save&name=liming&timelength=100
        StringBuilder entityBuilder = new StringBuilder("");
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                entityBuilder.append(entry.getKey()).append('=');
                entityBuilder.append(URLEncoder.encode(entry.getValue(), encoding));
                entityBuilder.append('&');
            }
            entityBuilder.deleteCharAt(entityBuilder.length() - 1);
        }
        byte[] entity = entityBuilder.toString().getBytes();
        URL url = new URL(path);
        conn = (HttpsURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            // Trust all certificates
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(new KeyManager[0], xtmArray, new SecureRandom());
            SSLSocketFactory socketFactory = context.getSocketFactory();
            ((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);
        }
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);//允许输出数据
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
        OutputStream outStream = conn.getOutputStream();
        outStream.write(entity);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode() == 200){
            return conn.getInputStream();
        }
        return conn.getInputStream();
    }
    public static void closeConnection(){
        if (conn!=null)
            conn.disconnect();
    }
}
