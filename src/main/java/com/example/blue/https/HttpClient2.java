package com.example.blue.https;

import android.annotation.SuppressLint;
import android.util.Log;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class HttpClient2 {
    private final static String TAG = Https_gongyitech.class.getSimpleName();
    private  final static String HTTPS_DEVICESEND="https://openbfapi.gongyitech.com/other/deviceSend";
    private  final static String HTTPS_DEVICERETURN="https://openbfapi.gongyitech.com/other/deviceReturn";
    private  final static String HTTPS_AUTOTESTINGQ="https://openbfapi.gongyitech.com/urine/autotestingq";
    private  final static String HTTPS_CORRESPOND="https://openbfapi.gongyitech.com/urine/correspond";
    private  final static String HTTPS_DEVICECONTROL="https://openbfapi.gongyitech.com/urine/deviceControl";
    private  final static String HTTPS_TESTINGR="https://openbfapi.gongyitech.com/urine/testingr";

    private static String https_arg;
    private static String https_check;
    private static final String https_ticket="unHj2qeiMcH0JLyefFFJx2T6EyRKejukQJ2eCD3DXRHTWZz8Uj/KQ97LX3ApKJWX";

    public  class Https_gongyitech{
        public String Post_deviceSend() throws Exception{
            Map<String,String> params= new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("status","BF");
            InputStream inputStream= sendPOSTRequestForInputStream(HTTPS_DEVICESEND,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }

        public String Post_deviceReturn(String receive) throws Exception{
            Map<String,String> params=new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("receive",receive);
            Log.w(TAG,receive);
            Log.w(TAG,receive);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICERETURN,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            https_arg=Parse_return(getreturn,"msg");
            return Parse_return(getreturn,"msg");
        }

        public String Post_autotestingq(String type) throws Exception{
            Map<String,String> params=new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("type",type);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_AUTOTESTINGQ,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            https_check=Parse_return2(getreturn,"check");
            return Parse_return2(getreturn,"value");
        }

        public String Post_correspond(String instruct) throws Exception{
            Map<String,String> params=new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("check",https_check);
            params.put("instruct",instruct);
            Log.w(TAG,https_ticket);
            Log.w(TAG,https_check);
            Log.w(TAG,https_arg);
            Log.w(TAG,instruct);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_CORRESPOND,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            String m_check=Parse_return2(getreturn,"check");
           if(m_check!=null) {
               https_check =m_check;
           }

            Log.w(TAG,getreturn);
            Log.w(TAG,m_check);

            int code=Parse_getcode(getreturn);
            if(code==0) {
                return "0_"+Parse_return2(getreturn, "value");
            }
            else if(code==1)
            {
                return "1_";
            }
            else if(code==2)
            {
               return "2_"+Parse_return(getreturn,"reslut");
            }
            else if(code==3)
            {
                return "3_";
            }
            else if(code==4)
            {
                return "4_";
            }
            else if(code==5)
            {
                return "5_";
            }
            else
            {
                return "error";
            }

        }

        public String Post_deviceControl(String type,String stauts,String value) throws Exception{
            Map<String,String> params=new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("type",type);
            params.put("stauts",stauts);
            params.put("value",value);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_DEVICECONTROL,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }

        public String Post_testingr(String receive) throws Exception{
            Map<String,String> params=new HashMap<>();
            params.put("ticket",https_ticket);
            params.put("arg",https_arg);
            params.put("receive",receive);
            InputStream inputStream=sendPOSTRequestForInputStream(HTTPS_TESTINGR,params,"UTF-8");
            byte []cha = new byte[1024];
            int len = inputStream.read(cha);
            inputStream.close();
            closeConnection();
            String getreturn=new String(cha,0,len);
            Log.w(TAG,getreturn);
            return Parse_return(getreturn,"msg");
        }

        private String Parse_return(String instr,String key)
        {
            try {
                JSONObject jsonObject = new JSONObject(instr);
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        private int Parse_getcode(String instr)
        {
            try {
                JSONObject jsonObject = new JSONObject(instr);
                return jsonObject.getInt("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;
        }
        private String Parse_return2(String instr,String key)
        {
            try {
                JSONObject jsonObject = new JSONObject(instr);
                jsonObject = jsonObject.getJSONObject("data");
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    @SuppressLint("AllowAllHostnameVerifier")
    private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier ();
    private static final X509TrustManager xtm = new X509TrustManager() {
        @SuppressLint("TrustAllX509TrustManager")
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        @SuppressLint("TrustAllX509TrustManager")
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };
    private static final X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
    private static HttpsURLConnection conn=null;
    public InputStream sendPOSTRequestForInputStream(String path, Map<String, String> params, String encoding) throws Exception{
// 1> 组拼实体数据
//method=save&name=liming&timelength=100
        StringBuilder entityBuilder = new StringBuilder();
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
        if (conn != null) {
            // Trust all certificates
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(new KeyManager[0], xtmArray, new SecureRandom());
            SSLSocketFactory socketFactory = context.getSocketFactory();
            conn.setSSLSocketFactory(socketFactory);
            conn.setHostnameVerifier(HOSTNAME_VERIFIER);
        }
        assert conn != null;
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
