package com.jyt.bitcoinmaster.http;

import java.util.concurrent.TimeUnit;

import okhttp3.*;



/**
 * http请求网络类
 *
 * @author LiuGongKui
 */
public class RestTemplateUtils {
    public static final String HTTPREQUESTURL = "https://mobile.huirongunion.com.cn/";//正式环境
//    public static final String HTTPREQUESTURL = "http://202.104.122.163:8004/";  //测试环境

    public static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");

    private RestTemplateUtils() {

    }


    /**
     * post  发送数据 多个数据
     *
     * @param reqData
     * @param url
     * @param token
     * @return
     */
    public static void post(String reqData, String url, String token, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        String encoding = "Bearer " + token;
        RequestBody body = RequestBody.create(JSONTYPE, reqData);
        final Request request = new Request.Builder()
                .addHeader("Authorization", encoding)
                .url(url)//请求的url
                .post(body).build();
        //创建/Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }


    /**
     * get
     *
     * @param url
     * @param token
     * @param callback
     */
    public static void get(String url, String token, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        String encoding = "Bearer " + token;
        final Request request = new Request.Builder()
                .addHeader("Authorization", encoding)
                .url(url)//请求的url
                .get().build();
        //创建/Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

//    /**
//     * 获得HttpPost对象（只适用与登录接口）
//     *
//     * @param url
//     *            请求地址
//     * @param callback
//     *            登录回调
//     * @param userName
//     *            用户名
//     * @param password
//     *            密码
//     *
//     * @return HttpPost对象
//     * @throws IOException
//     */
//    public static void login(String url, String userName, String password, okhttp3.Callback callback) {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder() .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30,TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//        //post方式提交的数据
//        FormBody formBody = new FormBody.Builder()
//                .add("userName",userName )
//                .add("password",password)
//                .build();
//        final Request request = new Request.Builder()
//                .url(url)//请求的url
//                .post(formBody) .build();
//        //创建/Call
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(callback);
//
//    }


}
