package com.jyt.bitcoinmaster.exchange.coinbasePro;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;


import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import android.util.Base64;

/**
 * coinbasePro 请求类
 */
public class HttpClientUtils {

    private static Logger log = Logger.getLogger("BitCoinMaster");
    //coinbase pro produce url
    private final static String BASE_URL = "https://api.pro.coinbase.com";
    //coinbase pro Test url
    //private final static String BASE_URL = "https://api-public.sandbox.pro.coinbase.com";
    private static int timeDiff;

    /**
     * 获取时间差
     *
     * @return
     */
    public static boolean getProTimeDiff() {
        String timestamp = String.valueOf(new Date().getTime() / 1000);
        int localTime = Integer.valueOf(timestamp);
        String serviceTime = getTimestamp();
        if (StringUtils.isNotEmpty(serviceTime)) {
            int localDifftime = Double.valueOf(serviceTime).intValue() - localTime;
            log.info("coinbasePro获取服务器时间差--" + localDifftime + "秒---------------------------------------------------------------------");
            timeDiff = localDifftime;
            return true;
        }
        log.info("coinbasePro获取服务器时间为--" + serviceTime);
        timeDiff = 0;
        return false;
    }

    /**
     * 发送get请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @return
     * @throws Exception
     */
    private static MyResponse doGet(String url, Map<String, String> headers) {
        MyResponse myResponse = new MyResponse();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);//设置链接超时
            connection.setReadTimeout(60000);
            connection.setRequestMethod("GET");//设置请求方法
            connection.setRequestProperty("Content-Type", "application/json");
            packageHeader(headers, connection);
            int responseCode = connection.getResponseCode();
            BufferedReader bufReader;
            myResponse.setCode(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                myResponse.setSuccess(true);
            } else {
                bufReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                myResponse.setSuccess(false);
            }
            String backData = "";
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                backData += line + "\r\n";
            }
            connection.disconnect();
            bufReader.close();
            log.info("url:" + url + "\nCoinbasePro请求结果：\n" + backData);
            myResponse.setMessage(backData);
        } catch (Exception e) {
            myResponse.setSuccess(false);
            myResponse.setMessage("doGet Connection failed");
            log.error("doGet Connection failed:", e);
        }
        return myResponse;
    }

    /**
     * 发送delete请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @return
     * @throws Exception
     */
    private static MyResponse doDelete(String url, Map<String, String> headers) {
        MyResponse myResponse = new MyResponse();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);//设置链接超时
            connection.setReadTimeout(60000);
            connection.setRequestMethod("DELETE");//设置请求方法
            connection.setRequestProperty("Content-Type", "application/json");
            packageHeader(headers, connection);
            int responseCode = connection.getResponseCode();
            BufferedReader bufReader;
            myResponse.setCode(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                myResponse.setSuccess(true);
            } else {
                bufReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                myResponse.setSuccess(false);
            }
            String backData = "";
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                backData += line + "\r\n";
            }
            connection.disconnect();
            bufReader.close();
            log.info("url:" + url + "\nCoinbasePro请求结果：\n" + backData);
            myResponse.setMessage(backData);
        } catch (Exception e) {
            myResponse.setSuccess(false);
            myResponse.setMessage("doDelete Connection failed");
            log.error("doDelete Connection failed:", e);
        }
        return myResponse;
    }


    /**
     * 发送post请求；带请求头和请求参数
     *
     * @param url
     * @param headers
     * @param json
     * @return
     * @throws IOException
     */
    private static MyResponse doPost(String url, Map<String, String> headers, String json) {
        MyResponse myResponse = new MyResponse();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);//设置链接超时
            connection.setReadTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            packageHeader(headers, connection);
            // 往服务器里面发送数据
            if (json != null && !TextUtils.isEmpty(json)) {
                byte[] writebytes = json.getBytes();
                // 设置文件长度
                connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = connection.getOutputStream();
                outwritestream.write(json.getBytes());
                outwritestream.flush();
                outwritestream.close();
            }
            BufferedReader reader;
            myResponse.setCode(connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                myResponse.setSuccess(true);
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                myResponse.setSuccess(false);
            }
            String backData = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                backData += line + "\r\n";
            }
            connection.disconnect();
            reader.close();
            log.info("url:" + url + "\nCoinbasePro请求结果：\n" + backData);
            myResponse.setMessage(backData);
        } catch (Exception e) {
            myResponse.setSuccess(false);
            myResponse.setMessage("doPost Connection failed");
            log.error("doPost Connection failed:", e);
        }
        return myResponse;
    }


    /**
     * Description: 封装请求头
     *
     * @param params
     * @param connection
     */
    private static void packageHeader(Map<String, String> params, HttpURLConnection connection) {
        // 封装请求头
        if (params != null) {
            Set<Entry<String, String>> entrySet = params.entrySet();
            for (Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * 共通请求
     *
     * @param url
     * @param method
     * @param jsonBody
     * @return
     * @throws Exception
     */
    public static MyResponse commonRequest(String url, String method, String jsonBody,CoinbaseProParam coinbaseProParam) {
        Map<String, String> headers = new HashMap<String, String>();
        String timestamp = String.valueOf(new Date().getTime() / 1000 + timeDiff);
        headers.put("accept", "application/json");
        headers.put("content-type", "application/json");
        headers.put("User-Agent", "gdax-java unofficial coinbase pro api library");
        headers.put("CB-ACCESS-KEY", coinbaseProParam.getProKey());
        headers.put("CB-ACCESS-SIGN", generate(url, method, jsonBody, timestamp, coinbaseProParam.getProSecret()));
        headers.put("CB-ACCESS-TIMESTAMP", timestamp);
        headers.put("CB-ACCESS-PASSPHRASE", coinbaseProParam.getProPassphrase());
        MyResponse result = new MyResponse();
//        log.info("CoinbasePro请求参数：\n url:" + url + " \n method: " + method + " \n body:" + jsonBody);
        if (StringUtils.equals(method, "GET")) {
            result = HttpClientUtils.doGet(BASE_URL + url, headers);
        } else if (StringUtils.equals(method, "POST")) {
            result = HttpClientUtils.doPost(BASE_URL + url, headers, jsonBody);
        } else if (StringUtils.equals(method, "DELETE")) {
            result = HttpClientUtils.doDelete(BASE_URL + url, headers);
        }
        return result;
    }

    public static MyResponse singleRequest(String url, String method, String body) {
        Map<String, String> headers = new HashMap<String, String>();
        MyResponse result = new MyResponse();
        if (StringUtils.equals(method, "GET")) {
            log.info("CoinbasePro请求参数：\n url:" + url + " \n method: " + method);
            result = HttpClientUtils.doGet(BASE_URL + url, headers);
        } else if (StringUtils.equals(method, "POST")) {
            log.info("CoinbasePro请求参数：\n url:" + url + " \n method: " + method + " \n body:" + body);
            result = HttpClientUtils.doPost(BASE_URL + url, headers, body);
        } else if (StringUtils.equals(method, "DELETE")) {
            log.info("CoinbasePro请求参数：\n url:" + url + " \n method: " + method);
            result = HttpClientUtils.doDelete(BASE_URL + url, headers);
        }
        return result;
    }


    private static String generate(String requestPath, String method, String body, String timestamp, String secretKey) {
        if (StringUtils.isEmpty(secretKey)) {
            return "";
        }
        try {
            String prehash = timestamp + method.toUpperCase() + requestPath + body;
            byte[] secretDecoded = Base64.decode(secretKey, Base64.DEFAULT);
            SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, CoinbaseProConstants.SHARED_MAC.getAlgorithm());
            Mac sha256 = CoinbaseProConstants.SHARED_MAC;
            sha256.init(keyspec);
            return Base64.encodeToString(sha256.doFinal(prehash.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            log.error("generate secretKey pro failed:", e);
            return "";
        }
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    private static String getTimestamp() {
        String url = "/time";
        String method = "GET";
        String result = "0";
        try {
            MyResponse myResponse = singleRequest(url, method, "");
            if (myResponse.isSuccess()) {
                String resultMessage = myResponse.getMessage();
                Map<String, Map<String, String>> map = JSONObject.parseObject(resultMessage, Map.class);
                result = String.valueOf(map.get("epoch"));
            }
        } catch (Exception e) {
            log.error("getTimestamp failed:", e);
        }
        return result;
    }


    /**
     * okhttp写法
     */
//    public static String doGet(String url, Map<String, String> headers) throws IOException {
//        Request.Builder builder = new Request.Builder().url(url);
//        builder.addHeader("Content-Type", "application/json");
//        packageHeader(headers, builder);
//        Request request = builder.get().build();
//        Response response = client.newCall(request).execute();
//        if (response.isSuccessful()) {
//            return response.body().string();
//        } else {
//
//            throw new IOException("Unexpected code " + response);
//        }
//    }
//    public static String doDelete(String url, Map<String, String> headers) throws Exception {
//        Request.Builder builder = new Request.Builder().url(url);
//        builder.addHeader("Content-Type", "application/json");
//        packageHeader(headers, builder);
//        Request request = builder.delete().build();
//        Response response = client.newCall(request).execute();
//        if (response.isSuccessful()) {
//            return response.body().string();
//        } else {
//            throw new IOException("Unexpected code " + response);
//        }
//    }
//
//    public static String doPost(String url, Map<String, String> headers, String json) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request.Builder builder = new Request.Builder().url(url);
//        packageHeader(headers, builder);
//        Request request = builder.post(body).build();
//        Response response = client.newCall(request).execute();
//        if (response.isSuccessful()) {
//            return response.body().string();
//        } else {
//            throw new IOException("Unexpected code " + response);
//        }
//    }

}
