package com.jyt.bitcoinmaster.timer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Xml;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.utils.Base64Utils;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.wallet.bitgo.HttpUtils;
import com.jyt.bitcoinmaster.utils.AesUtils;
import com.jyt.bitcoinmaster.utils.MD5Util;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.*;

import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadTimer extends Activity {

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private Context context;
    private WebView webView;

    public final static String DEFAULT_VERSION = "1.6"; // 当前包默认版本号

    private static String reqUrl = "";
    private static String terminalNo = "";
    private static String password = "";

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static String randomKey;//随机秘钥

    private static String token;

    private static Map<String, Object> reqParams;//请求参数

    private static Map<String, String> authParams;//登录请求参数

   // private final int OUT_TIME_SECOND = 1800000;//强制更新时间 30min

    private static boolean  isDowning = false; //是否下载中

    private ProgressDialog progressDialog;

    private final OkHttpClient okHttpClient = new OkHttpClient();

    private JSONObject kycInfo = new JSONObject();



    public final static String UPDATE_PATH = "/update.xml";// 服务器参数文件
    private final static String FILE_PATH = "/sdcard/Download/btc-v"; // 下载到本地的位置
    private final static String FILE_PATH_EXT = ".apk";// 拓展名
    private final static String DOWN_URL = "/btc-v";//服务器文件路径拼接

    public UploadTimer(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String) msg.obj;
            int what = msg.what;
            if (what == 1){
                //            log.info("[UploadTimer]: callResult:" + data);
                webView.evaluateJavascript("javascript:uploadTimerCallBack(" + data + ")",null);
            }
            if (what == 2){
                log.info("[获取kyc回调]: callResult:" + data);
                webView.evaluateJavascript("javascript:getKycCallBack('" + data + "')",null);
            } if (what == 3){
                log.info("[发送短信回调]: callResult:" + data);
                webView.evaluateJavascript("javascript:getsendMsgCallBack('" + data + "')",null);
            }

        }
    };


    /**
     * 闲时启动
     * 同步交易记录
     * 同步设备信息
     * 从网站端获取参数设置
     *
     * @return
     */
    @JavascriptInterface
    public void getSettingFromWeb(String deviceListStr, String localOnlineStr) {
        OnlineUser onlineUser = JSON.parseObject(localOnlineStr).toJavaObject(OnlineUser.class);
        reqUrl = onlineUser.getWebAddress() + "/";
        terminalNo = onlineUser.getTerminalNo();
        password = onlineUser.getPassword();
//        JSONArray deviceList = JSONArray.parseArray(deviceListStr);
//        List<DeviceInfo> deviceInfoList = deviceList.toJavaList(DeviceInfo.class);
        //保存Runstatus
//        saveRunstatus(deviceInfoList);
        Message msg = Message.obtain();
        String result = null;
        try {
            if (StringUtils.isBlank(token)) {
                getToken();
            }
            if (StringUtils.isNotBlank(token)) {
                uploadAllList();
                // 同步设备信息
//                reqParams = new HashMap<>();
//                reqParams.put("deviceList", deviceInfoList);
//                result = requestWeb("api/upload/deviceInfo");
                // 获取参数
                reqParams = new HashMap<>();
                result = requestWeb("api/queryTermSet");
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getInteger("code") == 0) {
                    //ParamSetting ps = JSON.parseObject(result, new TypeReference<ParamSetting>() {});
                    jsonObject.put("cryptoSettings",JSONObject.parseObject(jsonObject.getString("cryptoSettings")));
                    ParamSetting ps = JSON.toJavaObject(jsonObject,ParamSetting.class);
                    ps.setOnline("1");
                    if(ps.getCryptoSettings().getBtc() != null){
                        //解密
                        String btcChannelParam = AesUtils.aesDecrypt(ps.getCryptoSettings().getBtc().getChannelParam());
                        ps.getCryptoSettings().getBtc().setChannelParam(btcChannelParam);
                    }
                    if(ps.getCryptoSettings().getEth() != null){
                        //解密
                        String ethChannelParam = AesUtils.aesDecrypt(ps.getCryptoSettings().getEth().getChannelParam());
                        ps.getCryptoSettings().getEth().setChannelParam(ethChannelParam);
                    }
                    paramToStatic(ps);//静态变量
                    ParamSetting db = DBHelper.getHelper().queryParamInfo();
                    if(db == null ){
                        DBHelper.getHelper().insertSetting(ps);
                    }else{
                        DBHelper.getHelper().updateSetting(ps);
                    }
                    JSONObject jsonObject1 = JSONObject.parseObject(JSON.toJSONString(ps));
                    jsonObject1.put("code",0);
                    result = jsonObject1.toJSONString();
                } else if(jsonObject.getInteger("code") == 200){
                    ParamSetting ps = DBHelper.getHelper().queryParamInfo();
                    if(ps!=null){
                        paramToStatic(ps);
                    }
                }
            }
        } catch (Exception e) {
            log.error("同步数据系统异常",e);
            result = "";
        }
        //检查更新
        checkUpdate();
        msg.obj = result;
        msg.what=1;

        //test
        // 分销
        //UploadTimer.agencyProfit("161089002430700000001","sell");
        //UploadTimer.agencyProfit("161088318850600000001","buy");

        handler.sendMessage(msg);
    }

    private void paramToStatic(ParamSetting ps){
        Setting.webAddress = ps.getWebAddress();
        Setting.terminalNo = ps.getTerminalNo();
        Setting.password = ps.getPassword();
        Setting.merchantName = ps.getMerchantName();
        Setting.kycEnable = ps.getKycEnable();
        Setting.hotline = ps.getHotline();
        Setting.email = ps.getEmail();
        Setting.limitCash = ps.getLimitCash();
        Setting.kycUrl = ps.getKycUrl();
        Setting.way = ps.getWay();
        Setting.cryptoSettings = ps.getCryptoSettings();
    }

    /**
     * 上传所有订单
     */
    private void uploadAllList() {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 同步交易记录
                try {
                    uploadBuyInfo();
                } catch (Exception e) {
                    log.error(e);
                }

            }
        });
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 同步交易记录
                try {
                    uploadSellInfo();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 同步交易记录
                    uploadOrderInfo();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }

    /**
     *
     */
    @JavascriptInterface
    public String queryTermSet(String webAddress, String term, String password2) {
        if (StringUtils.isBlank(token) || !terminalNo.equals(term) || !reqUrl.equals(webAddress) || !password.equals(password2)) {
            reqUrl = webAddress + "/";
            terminalNo = term;
            password = password2;
            String resp = getToken();
            JSONObject jsonResult = JSONObject.parseObject(resp);
            if (0 != jsonResult.getInteger("code")) {
                //登录失败
                return resp;
            }
        }
        // 获取参数
        reqParams = new HashMap<>();
        String result = "";
        ParamSetting ps = null;
        try {
            result = requestWeb("api/queryTermSet");
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger("code") == 0) {
                jsonObject.put("cryptoSettings",JSONObject.parseObject(jsonObject.getString("cryptoSettings")));
                ps = JSON.toJavaObject(jsonObject,ParamSetting.class);
                ps.setWebAddress(webAddress);
                ps.setPassword(password2);
                ps.setOnline("1");
                //ParamSetting ps = JSON.parseObject(result, new TypeReference<ParamSetting>() {});
                if(ps.getCryptoSettings().getBtc() != null){
                    //解密
                    String btcChannelParam = AesUtils.aesDecrypt(ps.getCryptoSettings().getBtc().getChannelParam());
                    ps.getCryptoSettings().getBtc().setChannelParam(btcChannelParam);
                }
                if(ps.getCryptoSettings().getEth() != null){
                    //解密
                    String ethChannelParam = AesUtils.aesDecrypt(ps.getCryptoSettings().getEth().getChannelParam());
                    ps.getCryptoSettings().getEth().setChannelParam(ethChannelParam);
                }
                paramToStatic(ps);//静态变量
                ParamSetting db = DBHelper.getHelper().queryParamInfo();
                if(db == null ){
                    DBHelper.getHelper().insertSetting(ps);
                }else{
                    DBHelper.getHelper().updateSetting(ps);
                }
            } else if(jsonObject.getInteger("code") == 200){
                ps = DBHelper.getHelper().queryParamInfo();
                if(ps!=null){
                    paramToStatic(ps);
                }
            }else{
                log.error("连接服务器返回异常:"+jsonObject.getString("message"));
                return jsonObject.toJSONString();
            }
        } catch (Exception e) {
            log.error(e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 500);
            jsonObject.put("message", "Connection fail");
            return jsonObject.toJSONString();
        }
        JSONObject jsonObject1 = JSONObject.parseObject(JSON.toJSONString(ps));
        jsonObject1.put("code",0);
        return jsonObject1.toJSONString();
    }

    /**
     * 共通请求
     *
     * @param method
     * @return
     * @throws IOException
     */
    private static String requestWeb(String method) throws Exception {
        HttpUtils httpUtils = HttpUtils.getInstance();
        String respData = null;
        if ("auth".equals(method)) {
            respData = httpUtils.postForm(reqUrl + method, authParams);
            JSONObject json = JSONObject.parseObject(respData);
            randomKey = json.getString("randomKey");
            token = json.getString("token");
        } else {
            String jsonString = JSON.toJSONString(reqParams);
            String encode = replaceBlank(Base64.encodeToString(jsonString.getBytes(), Base64.DEFAULT));
            String md5 = MD5Util.encrypt(encode + randomKey);
            Map<String, Object> base = new HashMap<String, Object>();
            base.put("object", encode);
            base.put("sign", md5);
            String reqData = JSON.toJSONString(base);
            respData = httpUtils.postJson(reqUrl + method, reqData, token);
            JSONObject resultJson = JSONObject.parseObject(respData);
            if (700 == resultJson.getInteger("code")) {
                //token验证失败,重新获取
                getToken();

                // 重新请求
                md5 = MD5Util.encrypt(encode + randomKey);
                base = new HashMap<String, Object>();
                base.put("object", encode);
                base.put("sign", md5);
                reqData = JSON.toJSONString(base);
                respData = httpUtils.postJson(reqUrl + method, reqData, token);
                resultJson = JSONObject.parseObject(respData);
            }
        }
        return respData;
    }

    // 获取token
    private static String getToken() {
//        if (StringUtils.isEmpty(randomKey) || StringUtils.isEmpty(token)) {
        String method = "auth";
        authParams = new HashMap<String, String>();
        authParams.put("userName", terminalNo);//用户名
        authParams.put("password", password);//密码
        String result = "";
        try {
            result = requestWeb(method);
        } catch (Exception e) {
            log.error(e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 500);
            jsonObject.put("error", "Connection fail");
            result = jsonObject.toJSONString();
        }
        return result;
//        }
    }

    /**
     * 上传买币信息
     */
    private void uploadBuyInfo() {
        if (StringUtils.isBlank(token)) {
            return;
        }
        reqParams = new HashMap<String, Object>();
        String method = "api/upload/buyRecord";
        //查询未上传数据
        ArrayList<BuyLog> needUploadList = DBHelper.getHelper().queryBuyLogByIsUpload("0");
        log.info("上传买币记录，须上传记录 ===========" + needUploadList.size() + "条");
        if (needUploadList.size() == 0) {
            return;
        }
        JSONArray buyList = new JSONArray();
        List<String> transIds = new ArrayList<>();
        for (BuyLog buylog : needUploadList) {
            JSONObject buy = new JSONObject();
            buy.put("amount", buylog.getAmount());
            buy.put("cash", buylog.getCash());
            if(StringUtils.isBlank(buylog.getChannelFee())){
                buylog.setChannelFee("0");
            }
            buy.put("channelFee", buylog.getChannelFee());
            buy.put("fee", buylog.getFee());
            buy.put("status", buylog.getStatus());
            buy.put("remark", buylog.getRemark());
            buy.put("terminalNo", buylog.getTerminalNo());
            buy.put("transId", buylog.getTransId());
            buy.put("transTime", buylog.getTransTime());
           // buy.put("extId", buylog.getExtId());
            buy.put("address", buylog.getAddress());
            buy.put("strategy", buylog.getStrategy());
            buy.put("price", buylog.getPrice());
            buy.put("exchangeStrategy", buylog.getExchangeStrategy());
            buy.put("customerId", buylog.getCustomerId());
            buy.put("exchangeRate", buylog.getExchangeRate());
            buy.put("cryptoCurrency", buylog.getCryptoCurrency());
            buy.put("channel",buylog.getChannel());
            buy.put("currency", buylog.getCurrency());
            buyList.add(buy);
            transIds.add(buylog.getTransId());
        }
        reqParams.put("buyList", buyList);
        //发送数据
        String resp = "";
        try {
            resp = requestWeb(method);
        } catch (Exception e) {
            log.error("uploadBuyInfo fail===", e);
        }
        JSONObject respJSON = JSONObject.parseObject(resp);
        if (0 == respJSON.getInteger("code")) {
            log.info("上传买币记录成功"+buyList.size()+"条");
            //更新数据库
            DBHelper.getHelper().updateWithdrawLogIsUpload(true, transIds);
        }
    }

    /**
     * 查询Kyc客户
     */
    @JavascriptInterface
    public String queryKycResult(String email,boolean isExceedQuota) {
        if (StringUtils.isBlank(token)) {
            getToken();
        }
        reqParams = new HashMap<String, Object>();
        String method = "api/queryKycResult";

        log.info("查询kyc客户，查询邮箱 ===========" + email);
        reqParams.put("email", email);
        reqParams.put("isExceedQuota", isExceedQuota);
        //发送数据
        String resp = "";
        try {
            resp = requestWeb(method);
        } catch (Exception e) {
            log.error("queryKycResult fail===", e);
        }
        return resp;
    }

    /**
     * 上传卖币信息
     */
    private void uploadSellInfo() {
        if (StringUtils.isBlank(token)) {
            return;
        }
        reqParams = new HashMap<String, Object>();
        String method = "api/upload/withdrawRecord";
        //查询未上传数据
        ArrayList<WithdrawLog> needUploadList = DBHelper.getHelper().queryWithdrawLogByIsUpload("0");
        log.info("上传卖币记录，须上传记录 ===========" + needUploadList.size() + "条");
        if (needUploadList.size() == 0) {
            return;
        }
        JSONArray withdrawList = new JSONArray();
        List<String> transIds = new ArrayList<>();
        for (WithdrawLog withdrawlog : needUploadList) {
            JSONObject withdraw = new JSONObject();
            withdraw.put("targetAddress", withdrawlog.getTargetAddress());
            withdraw.put("amount", withdrawlog.getAmount());
            withdraw.put("terminalNo", withdrawlog.getTerminalNo());
            withdraw.put("fee", withdrawlog.getFee());
            if(StringUtils.isBlank(withdrawlog.getcFee())){
                withdrawlog.setcFee("0");
            }
            withdraw.put("cFee", withdrawlog.getcFee());
            withdraw.put("cash", withdrawlog.getCash());
            withdraw.put("transId", withdrawlog.getTransId());
            //withdraw.put("extId", withdrawlog.getExtId());
            withdraw.put("transTime", withdrawlog.getTransTime());
            withdraw.put("remark", withdrawlog.getRemark());
            withdraw.put("transStatus", withdrawlog.getTransStatus());
            withdraw.put("redeemStatus", withdrawlog.getRedeemStatus());
            withdraw.put("outCount", withdrawlog.getOutCount());
            withdraw.put("strategy", withdrawlog.getStrategy());
            withdraw.put("price", withdrawlog.getPrice());
            withdraw.put("exchangeStrategy", withdrawlog.getExchangeStrategy());
            withdraw.put("customerId", withdrawlog.getCustomerId());
            withdraw.put("exchangeRate", withdrawlog.getExchangeRate());
            withdraw.put("cryptoCurrency", withdrawlog.getCryptoCurrency());
            withdraw.put("channel", withdrawlog.getChannel());
            withdraw.put("sellType", withdrawlog.getSellType());
            withdraw.put("currency", withdrawlog.getCurrency());
            withdrawList.add(withdraw);
            transIds.add(withdrawlog.getTransId());
        }
        log.info("上传卖币记录，上传记录 ===========" + withdrawList.toJSONString());
        reqParams.put("withdrawList", withdrawList);
        //发送数据
        String resp = "";
        try {
            resp = requestWeb(method);
        } catch (Exception e) {
            log.error("uploadSellInfo fail===", e);
        }
        JSONObject respJSON = JSONObject.parseObject(resp);
        if (0 == respJSON.getInteger("code")) {
            log.info("上传卖币记录成功"+withdrawList.size()+"条");
            //更新数据库
            DBHelper.getHelper().updateWithdrawLogIsUpload(false, transIds);
        }
    }


    /**
     * 上传coinbasePro订单信息
     */
    private void uploadOrderInfo() {
        if ("".equals(token) || token == null) {
            return;
        }
        reqParams = new HashMap<>();
        String method = "api/upload/orderInfos";
        //查询未上传数据
        List<ExchangeOrder> needUploadList = DBHelper.getHelper().queryCoinbaseOrderByIsUpload("0");
        log.info("上传交易所记录，须上传记录 ===========" + needUploadList.size() + "条");
        if (needUploadList.size() == 0) {
            return;
        }
        JSONArray orderList = new JSONArray();
        List<String> transIds = new ArrayList<>();
        for (ExchangeOrder orderDetail : needUploadList) {
            JSONObject coinbaseJson = new JSONObject();
            coinbaseJson.put("transId", orderDetail.getTransId()); //终端交易流水id
            coinbaseJson.put("id", orderDetail.getId()); //平台交易id
            coinbaseJson.put("size", orderDetail.getSize()); // btc数量
            coinbaseJson.put("productId", orderDetail.getProductId()); // 产品id BTC-USD
            coinbaseJson.put("side", orderDetail.getSide()); // 买或 卖
            coinbaseJson.put("type", orderDetail.getType()); // limit / market 限价还是实时价格
            coinbaseJson.put("createdAt", orderDetail.getCreatedAt()); //创建时间
            coinbaseJson.put("fillFees", orderDetail.getFillFees()); //手续费单位USD
            coinbaseJson.put("filledSize", orderDetail.getFilledSize()); // 购买的数量BTC
            coinbaseJson.put("executedValue", orderDetail.getExecutedValue()); // 购买的实际金额USD
            coinbaseJson.put("status", orderDetail.getStatus()); //状态
            coinbaseJson.put("message", orderDetail.getMessage()); //错误信息
            coinbaseJson.put("funds", orderDetail.getFunds()); //购买的USD数量
            coinbaseJson.put("terminalNo", orderDetail.getTerminalNo()); //终端号
            coinbaseJson.put("price", orderDetail.getPrice()); //当时汇率
            coinbaseJson.put("createTime", orderDetail.getCreateTime()); //当时汇率
            coinbaseJson.put("cryptoCurrency", orderDetail.getCryptoCurrency());
            coinbaseJson.put("currency", orderDetail.getCurrency());
            orderList.add(coinbaseJson);
            transIds.add(orderDetail.getTransId());
        }
        log.info("上传交易所记录，上传记录 ===========" + orderList.toJSONString());
        reqParams.put("orderList", orderList);
        //发送数据
        String resp = "";
        try {
            resp = requestWeb(method);
        } catch (Exception e) {
            log.error("uploadOrderInfo fail===", e);
        }
        JSONObject respJSON = JSONObject.parseObject(resp);
        if (0 == respJSON.getInteger("code")) {
            log.info("上传交易所记录成功"+orderList.size()+"条");
            for (String transId : transIds) {
                //更新数据库
                DBHelper.getHelper().updateOrderIsUpload(transId, 1);
            }
        }
    }

    /**
     * 上传钞箱记录
     *
     * @param cashBoxHistory
     */
    public static void uploadCashBoxInfo(CashBoxHistory cashBoxHistory) throws Exception {
        if (StringUtils.isBlank(token)) {
            log.info("token为空,跳过上传钞箱操作");
            return;
        }
        reqParams = new HashMap<>();
        String method = "api/upload/cashBoxRecord";

        reqParams.put("terminalNo", cashBoxHistory.getTerminalNo());
        reqParams.put("status", cashBoxHistory.getStatus());
        reqParams.put("createTime", cashBoxHistory.getCreateTime());
        //发送数据
        String resp = requestWeb(method);
    }

    /**
     * 去掉字符串里面的空格，换行，回车；
     *
     * @param str
     * @return
     */
    private static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    private void saveRunstatus(List<DeviceInfo> deviceInfos) {
        List<RunStatusManage> list = new ArrayList<>();
        RunStatusManage runStatusManage;
        for (DeviceInfo device : deviceInfos) {
            runStatusManage = new RunStatusManage();
            runStatusManage.setHardwareModularName(device.getDeviceName());
            runStatusManage.setStatus(String.valueOf(device.getStatus()));
            runStatusManage.setTerminalNo(device.getTerminalNo());
            runStatusManage.setUpdateTime(dateFormat(new Date()));
            list.add(runStatusManage);
        }
        DBHelper.getHelper().saveRunManage(list);
    }

    private String dateFormat(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

//    /**
//     * 上传买币信息
//     */
//    public void uploadBuy() {
//        //创建一个定时器对象
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            //定时上传交易数据
//            @Override
//            public void run() {
//                uploadBuyInfo();
////            }
////        }, 20, 6000000);
//            }
//        }, 15, 150000);
//    }

//    /**
//     * 上传卖币信息
//     */
//    public void uploadSell() {
//        //创建一个定时器对象
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            //定时上传交易数据
//            @Override
//            public void run() {
//                uploadSellInfo();
////            }
////        }, 40, 7200000);//2h
//            }
//        }, 20, 20000);
//
//    }

//    // 模拟登录获取token
//    public void login() {
//        //创建一个定时器对象
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                String result = getToken();
//            }
//        }, 0, 10080000);//7天
//
//    }

    /**
     * objToMap
     *
     * @param obj
     * @return
     */
    private Map<String, Object> objToMap(Object obj) {
        Map<String, Object> map = new HashMap<>(20);
        // 获取f对象对应类中的所有属性域
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(obj);
                if (o == null) {
                    o = "";
                }
                map.put(varName, o.toString());
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (Exception ex) {
                log.info("upload->objToMap:", ex);
            }
        }
        return map;
    }

    /**
     * 检查更新
     */

    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //包装成url的对象
                try {
                    String version = getUpdataInfo();
                    String currentVersion = getCurrentVersion();
                    log.info("本机当前版本: " + currentVersion);
                    if (version.equals(currentVersion)) {
                        log.info("已经是最新版本: " + version);
                    } else {
                        log.info("版本号不同 ,即将升级");
                        downFile(getSettingUrl() + DOWN_URL + version + FILE_PATH_EXT, new File(FILE_PATH + version + FILE_PATH_EXT));
                    }
                } catch (Exception e) {
                    //log.error("UploadTimer checkUpdate:"+e.getMessage());
                }
            }
        }).start();
    }

    /*
     * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
     */
    private String getUpdataInfo() throws Exception {
        URL url = new URL(getSettingUrl() + UPDATE_PATH);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        InputStream is = conn.getInputStream();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        String version = "";
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("version".equals(parser.getName())) {
                        version = parser.nextText();    //获取版本号
                    }
                    break;
            }
            type = parser.next();
        }
        return version;
    }

    /**
     * 文件下载
     *
     * @param url
     * @param file
     */
    private void downFile(String url, File file) {
        log.info("准备下载 " + url);
        // 文件是否存在 或者超过30min,强行更新

        if(isDowning){
            log.info("app正在下载" );
            return;
        }

        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                log.error("delete apk fail", e);
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("FOUND NEW VERSION, UPDATING");
        progressDialog.setMessage("please wait...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCancelable(false);
        isDowning = true;//正在下载
        download(url, file);
    }

    private void download(String url, final File file) {
        // 父目录是否存在
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                isDowning = false;
                log.error("UploadTimer", e);
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
//              //下载失败监听回调
                progressDialog.dismiss();
                isDowning = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                // 获取文件总长度
                long totalLength = body.contentLength();
                try {
                    InputStream is = body.byteStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[4096];
                    int length = 0;
                    int sum = 0;
                    while ((length = is.read(buf, 0, buf.length)) != -1) {
                        fos.write(buf, 0, length);
                        sum += length;
                        int progress = (int) (sum * 1.0f / totalLength * 100);
                        // 下载中更新进度条
                        progressDialog.setProgress(progress);
                        //下载完成，安装
                        if (progress == 100) {
                            updateVersion();
                            install(file.getPath());
                            progressDialog.dismiss();
                            isDowning = false;
                        }
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    progressDialog.dismiss();
                    isDowning = false;
                    log.error("UploadTimer", e);
                }

            }
        });
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    private static void install(String apkPath) {
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            String command = "pm install -r " + apkPath + "\n";
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            dataOutputStream.writeBytes(command);
            //dataOutputStream.writeBytes("am restart\n");
            dataOutputStream.writeBytes("am start -n com.jyt.bitcoinmaster/com.jyt.bitcoinmaster.activity.MainActivity\n");


            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }

            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {

            } else {
                log.info("安装失败");
            }
        } catch (Exception e) {
            log.error("安装APK", e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                log.error("安装APK,IOE", e);
            }
        }
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    private String getCurrentVersion() {
        String defaultVersion = DEFAULT_VERSION;
        try {
            HardwareConfig version = DBHelper.getHelper().queryKeyEixst("version");
            if (version == null || StringUtils.isEmpty(version.getHwValue())) {
                return defaultVersion;
            } else {
                return version.getHwValue();
            }
        } catch (Exception e) {
            log.error("获取本机版本号失败, {}", e);
            return defaultVersion;
        }
    }

    /**
     * 获取本机联网地址
     *
     * @return
     */
    private String getSettingUrl() {
        String webAddress = "";
        try {
            ParamSetting paramSetting = DBHelper.getHelper().queryParamInfo();
            if (paramSetting == null || StringUtils.isEmpty(paramSetting.getWebAddress())) {
                return webAddress;
            } else {
                return getIP(paramSetting.getWebAddress());
            }
        } catch (Exception e) {
            log.error("获取本机webAddress失败, {}", e);
            return webAddress;
        }
    }

    /**
     * 更新版本号
     *
     * @return
     */
    private void updateVersion() {
        try {
            List<HardwareConfig> versionObjList = new ArrayList<>();
            HardwareConfig versionObj = new HardwareConfig();
            versionObj.setHwKey("version");
            versionObj.setHwValue(getUpdataInfo());
            versionObj.setCreateTime(new Date().toString());
            versionObjList.add(versionObj);
            DBHelper.getHelper().saveHardwareConfig(versionObjList);
        } catch (Exception e) {
            log.error("更新本机版本号失败, {}", e);
        }
    }

    /**
     * 过滤，只获取ip
     *
     * @param url
     * @return
     */
    private String getIP(String url) {
        //使用正则表达式过滤，
        String re = "((http|ftp|https)://)(([a-zA-Z0-9._-]+)|([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}))(([a-zA-Z]{2,6})?)";
        String str = "";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(re);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            str = url;
        } else {
            String[] split2 = url.split(re);
            if (split2.length > 1) {
                String substring = url.substring(0, url.length() - split2[1].length());
                str = substring;
            } else {
                str = split2[0];
            }
        }
        return str;
    }


    /**
     * 人脸识别请求图片回来并校验
     *
     * @param kycId
     * @return
     */
    @JavascriptInterface
    public void getPicFile(String kycId) {
        Message msg = Message.obtain();
        msg.what=2;
        msg.obj =  "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isBlank(token) || StringUtils.isBlank(kycId)) {
                    log.error("token为空,人脸识别失败");
                    handler.sendMessage(msg);
                    return;
                }
                reqParams = new HashMap<>();
                String method = "/api/downloadImg";
                reqParams.put("kycId", kycId);
                //发送数据
                String resp = null;
                try {
                    log.info("------------请求kyc:"+JSONObject.toJSONString(reqParams));
                    resp = requestWeb(method);
                    JSONObject jsonObject= JSONObject.parseObject(resp);
                    log.info("------------获取kyc结果:"+jsonObject.getString("code"));
                    if ("0".equals(jsonObject.getString("code")) && StringUtils.isNotBlank( jsonObject.getString("fileStr"))){
                        String baseImg = jsonObject.getString("fileStr");
                        kycInfo.put("fileStr",baseImg);
                        kycInfo.put("kycId",kycId);
                        kycInfo.put("cardType",jsonObject.getString("cardType"));
                        kycInfo.put("ocrContent",jsonObject.getJSONObject("ocrContent"));
                        msg.obj =  Base64Utils.base642File(baseImg);
                        handler.sendMessage(msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("获取kyc结果 error",e);
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 上传人脸识别成功图片
     * @param kycId
     * @param base64Img
     */
    @JavascriptInterface
    public void uploadFaceImage(String kycId,String base64Img) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isBlank(token) || StringUtils.isBlank(kycId)) {
                    log.error("token为空,人脸识别图片上传失败");
                    return;
                }
                reqParams = new HashMap<>();
                String method = "/api/uploadImg";
                reqParams.put("kycId", kycId);
                reqParams.put("picContent", base64Img);
                //发送数据
                String resp = null;
                try {
                    resp = requestWeb(method);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("token为空,人脸识别图片上传失败",e);
                }
            }
        }).start();
    }

    /**
     * ocr信息
     * @param kycId
     * @return
     */
    @JavascriptInterface
    public String getKycInfo(String kycId) {
        if (kycId.equals(kycInfo.getString("kycId"))) {
            return kycInfo.toJSONString();
        }
        return "";
    }

    /**
     * 发送短信验证码
     *
     * @param phone
     * @return
     */
    @JavascriptInterface
    public void sendSMSCode(String phone) {
        Message msg = Message.obtain();
        msg.what = 3;
        msg.obj = "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = "";
                try {
                    if (StringUtils.isBlank(token)) {
                        getToken();
                    }
                    reqParams = new HashMap<String, Object>();
                    String method = "api/sendSMS";
                    log.info("发短信interface ===========" + phone);
                    reqParams.put("phone", phone);
                    //发送数据
                    resp = requestWeb(method);
                } catch (Exception e) {
                    log.error("sendSMSCode fail===", e);
                    handler.sendMessage(msg);
                    return;
                }
                if (StringUtils.isNotBlank(resp)) {
                    JSONObject jsonObject = JSONObject.parseObject(resp);
                    log.info("发短信interface ===========" + resp);
                    if (jsonObject.getInteger("code") == 0) {
                        msg.obj = jsonObject.getString("verificationCode");
                        handler.sendMessage(msg);
                    }else {
                        handler.sendMessage(msg);
                    }
                } else {
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }


    /**
     * 上传分销数据
     * @param transId
     * @param type
     */
    public static void agencyProfit(final String transId,final String type) {
        log.info("上传分销订单transId="+transId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isBlank(token)) {
                    getToken();
                }
                reqParams = new HashMap<>();
                String method = "/api/touchAgencyProfit";
                reqParams.put("transId", transId);
                reqParams.put("transType", type);
                //发送数据
                try {
                    String resp = requestWeb(method);
                } catch (Exception e) {
                    log.error("上传分销数据失败", e);
                }
            }
        }).start();
    }
}
