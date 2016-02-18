package com.weilun.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by 胡伟强 on 2015/12/28.
 */
public class HttpUtil {

    public static String getJSONStringByMethod(String method,String objectId){

        String result="";
        String urlStr= "http://cloud.bmob.cn/0a23ebe5d579a9f2/"+method;

        URL url=null;
        try{
            url=new URL(urlStr);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Charset","utf-8");

            urlConnection.connect();

            DataOutputStream dataOutputStream=new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes("objectId="+ URLEncoder.encode(objectId,"utf-8"));
            dataOutputStream.flush();
            dataOutputStream.close();

            BufferedReader bufferedReader=new BufferedReader
                    (new InputStreamReader(urlConnection.getInputStream()));
            String readLine=null;
            while ((readLine=bufferedReader.readLine())!=null){
                result+=readLine;
            }
            bufferedReader.close();
            urlConnection.disconnect();
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getJSONStringByMethod(String method,String belongUserMobileNo,String targetUserMobileNo,int start){

        String result="";
        String urlStr= "http://cloud.bmob.cn/0a23ebe5d579a9f2/"+method;

        URL url=null;
        try{
            url=new URL(urlStr);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Charset","utf-8");

            urlConnection.connect();

            DataOutputStream dataOutputStream=new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes("belongUserMobileNo="+ URLEncoder.encode(belongUserMobileNo,"utf-8")
                    +"&targetUserMobileNo="+URLEncoder.encode(targetUserMobileNo,"utf-8")
                    +"&start="+URLEncoder.encode(String.valueOf(start),"utf-8"));
            dataOutputStream.flush();
            dataOutputStream.close();

            BufferedReader bufferedReader=new BufferedReader
                    (new InputStreamReader(urlConnection.getInputStream()));
            String readLine=null;
            while ((readLine=bufferedReader.readLine())!=null){
                result+=readLine;
            }
            bufferedReader.close();
            urlConnection.disconnect();
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
