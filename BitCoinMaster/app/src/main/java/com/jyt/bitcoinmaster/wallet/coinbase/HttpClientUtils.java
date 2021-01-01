package com.jyt.bitcoinmaster.wallet.coinbase;

import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.statics.ChannnelParam;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HttpClientUtils {

    private static Logger log = Logger.getLogger("BitCoinMaster");
    //coinbaae生产环境地址
    private final static String BASE_URL = "https://api.coinbase.com/";

    private static int timeDiff;

    /**
     * 获取时间差
     *
     * @return
     */
    public static boolean getCoinbaseTimeDiff() {
        String timestamp = String.valueOf(new Date().getTime() / 1000);
        int localTime = Integer.valueOf(timestamp);
        String serviceTime = getTimestamp();
        if (StringUtils.isNotEmpty(serviceTime)) {
            int localDifftime = Integer.valueOf(serviceTime) - localTime;
            log.info("coinbase获取服务器时间差--" + localDifftime + "秒---------------------------------------------------------------------");
            timeDiff = localDifftime;
            return true;
        }
        log.info("coinbase获取服务器时间为--" + serviceTime);
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
    private static String doGet(String url, Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);//设置链接超时
            connection.setReadTimeout(60000);
            connection.setRequestMethod("GET");//设置请求方法
            connection.setRequestProperty("Content-Type", "application/json");
            packageHeader(headers, connection);
            int responseCode = connection.getResponseCode();
            BufferedReader bufReader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                bufReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            String backData = "";
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                backData += line + "\r\n";
            }
            connection.disconnect();
            bufReader.close();
            return backData;
        } catch (Exception e) {
            log.error("doGet Connection failed:", e);
            return "{\"errors\":[{\"id\":\"doGet Connection failed\",\"field\":\"Connection failed\"}]}";
        }
    }

    /**
     * 发送delete请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @return
     * @throws Exception
     */
    private static String doDelete(String url, Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);//设置链接超时
            connection.setReadTimeout(60000);
            connection.setRequestMethod("DELETE");//设置请求方法
            connection.setRequestProperty("Content-Type", "application/json");
            packageHeader(headers, connection);
            int responseCode = connection.getResponseCode();
            BufferedReader bufReader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                bufReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            String backData = "";
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                backData += line + "\r\n";
            }
            connection.disconnect();
            bufReader.close();
            return backData;
        } catch (Exception e) {
            log.error("doDelete Connection failed:", e);
            return "{\"errors\":[{\"id\":\"doDelete Connection failed\",\"field\":\"Connection failed\"}]}";
        }
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
//    private static String doPost(String url, Map<String, String> headers, String json) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setConnectTimeout(10000);//设置链接超时
//        connection.setReadTimeout(60000);
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json");
//        packageHeader(headers, connection);
//        // 往服务器里面发送数据
//        if (json != null && !TextUtils.isEmpty(json)) {
//            byte[] writebytes = json.getBytes();
//            // 设置文件长度
//            connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
//            OutputStream outwritestream = connection.getOutputStream();
//            outwritestream.write(json.getBytes());
//            outwritestream.flush();
//            outwritestream.close();
//        }
//        BufferedReader reader;
//        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
//        } else {
//            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
//        }
//        String backData = "";
//        String line = "";
//        while ((line = reader.readLine()) != null) {
//            backData += line + "\r\n";
//        }
//        connection.disconnect();
//        reader.close();
//        log.info("Coinbase请求结果：\n" + backData);
//        return backData;
//    }


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
     * Description: 封装请求头
     *
     * @param params
     * @param builder
     */
    public static void packageHeader(Map<String, String> params, Request.Builder builder) {
        // 封装请求头
        if (params != null) {
            Set<Entry<String, String>> entrySet = params.entrySet();
            for (Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * 共通请求
     *
     * @param url
     * @param method
     * @param body
     * @return
     * @throws Exception
     */
    public static String commonRequest(String url, String method, String body,String cryptoCurrency) {
        String apiKey = "";
        String secretKey = "";
        if (CoinbaseService.paramMap.get(cryptoCurrency) != null) {
            apiKey = CoinbaseService.paramMap.get(cryptoCurrency).getApiKey();
            secretKey = CoinbaseService.paramMap.get(cryptoCurrency).getApiSecret();
        }
        Map<String, String> headers = new HashMap<String, String>();
        String timestamp = String.valueOf(new Date().getTime() / 1000 + timeDiff);
        headers.put("CB-ACCESS-KEY", apiKey);
        headers.put("CB-ACCESS-SIGN", HMACSHA256(url, method, body, timestamp, secretKey));
        headers.put("CB-ACCESS-TIMESTAMP", timestamp);
        headers.put("CB-VERSION", "2019-06-15");
        String result = null;
//        log.info("Coinbase请求参数：\n url:" + url + " \n method: " + method + " \n body:" + body);
        if (StringUtils.equals(method, "GET")) {
            result = HttpClientUtils.doGet(BASE_URL + url, headers);
        } else if (StringUtils.equals(method, "POST")) {
            result = HttpClientUtils.doPost(BASE_URL + url, headers, body);
        } else if (StringUtils.equals(method, "DELETE")) {
            result = HttpClientUtils.doDelete(BASE_URL + url, headers);
        }
        return result;
    }

    public static String singleRequest(String url, String method, String body) {
        Map<String, String> headers = new HashMap<String, String>();
        String result = null;
        if (StringUtils.equals(method, "GET")) {
//            log.info("Coinbase请求参数：\n url:" + url + " \n method: " + method);
            result = HttpClientUtils.doGet(BASE_URL + url, headers);
        } else if (StringUtils.equals(method, "POST")) {
//            log.info("Coinbase请求参数：\n url:" + url + " \n method: " + method + " \n body:" + body);
            result = HttpClientUtils.doPost(BASE_URL + url, headers, body);
        } else if (StringUtils.equals(method, "DELETE")) {
//            log.info("Coinbase请求参数：\n url:" + url + " \n method: " + method);
            result = HttpClientUtils.doDelete(BASE_URL + url, headers);
        }
        return result;
    }


    private static String HMACSHA256(String requestPath, String method, String body, String timestamp, String secretKey) {
        if (StringUtils.isEmpty(secretKey)) {
            return "";
        }
        String prehash = timestamp + method.toUpperCase() + requestPath + body;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(prehash.getBytes(StandardCharsets.UTF_8));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                String hex = Integer.toHexString(array[i] & 0xFF);
                if (hex.length() < 2) {
                    sb.append(0);
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("HMACSHA256 failed:", e);
            return "";
        }
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    private static String getTimestamp() {
        String url = "v2/time";
        String method = "GET";
        String result = "0";
        String rsponse = singleRequest(url, method, "");
        JSONObject jsonObject = JSONObject.parseObject(rsponse);
        if (jsonObject.containsKey("errors")) {
            log.error("coinbase getTimestamp fail,"+result);
            return result;
        }
        try {
            Map<String, Map<String, String>> map = JSONObject.parseObject(rsponse, Map.class);
            result = String.valueOf(map.get("data").get("epoch"));
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
    public static String doPost(String url, Map<String, String> headers, String json) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, json);
            Request.Builder builder = new Request.Builder().url(url);
            packageHeader(headers, builder);
            Request request = builder.post(body).build();
            Response response = null;
            response = client.newCall(request).execute();
            String result = response.body().string();
//            log.info("Coinbase请求结果：\n" + result);
            return result;
        } catch (Exception e) {
            log.error("doPost Connection failed:", e);
            return "{\"errors\":[{\"id\":\"doPost Connection failed\",\"field\":\"Connection failed\"}]}";
        }
    }

}
