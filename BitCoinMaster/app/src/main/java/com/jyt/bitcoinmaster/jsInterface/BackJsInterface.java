package com.jyt.bitcoinmaster.jsInterface;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.config.Config;
import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.bitcoinmaster.email.SendMailUtil;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.timer.UploadTimer;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class BackJsInterface {
    private Context context;
    private WebView webView;

    private static final int PREPARENOTE = 1;

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PREPARENOTE) {
                String data = (String) msg.obj;
//                data= data.replaceAll("'", "&#39;");
//                data= data.replaceAll("\"", "&quot;");
                webView.evaluateJavascript("javascript:resultPrepareNoteInfo('" + data + "')",null);
            }
        }
    };

    public BackJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
        Config config = ((MyApp) context.getApplicationContext()).getConfig();
    }

    /**
     * 后台登录
     */
    @JavascriptInterface
    public boolean login(String req) {
        User user = DBHelper.getHelper().queryUserEixst(req);
        return user != null;
    }

    /**
     * 退出应用
     */
    @JavascriptInterface
    public void closeApp() {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert mActivityManager != null;
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
            if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 修改密码
     */
    @JavascriptInterface
    public boolean updatePassword(String req, String account) {
        return DBHelper.getHelper().updatePassword(req,account);
    }

    /**
     * 验证原密码
     */
    @JavascriptInterface
    public boolean verifyPassword(String oldPassword, String account) {
        User user = DBHelper.getHelper().queryUserInfo(account);
        return oldPassword.equals(user.getPassword());
    }

    /**
     * 用户参数查询
     */
    @JavascriptInterface
    public String queryParam() {
        ParamSetting ps = DBHelper.getHelper().queryParamInfo();
        if (ps != null) {
            if (StringUtils.isEmpty(ps.getTerminalNo())){
                ps.setTerminalNo("t00000001");
            }
            paramToStatic(ps);
        }
        return JSONArray.toJSONString(ps);
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
        Setting.cryptoSettings = ps.getCryptoSettings();
        Setting.way = ps.getWay();
    }
    /**
     * 参数设置
     */
    @JavascriptInterface
    public boolean parameterSetting(String req) {
        boolean result;
        ParamSetting oldPs = DBHelper.getHelper().queryParamInfo();
        ParamSetting newPs = JSON.toJavaObject(JSONObject.parseObject(req),ParamSetting.class);
        if(oldPs == null ){
            result= DBHelper.getHelper().insertSetting(newPs);
        }else{
            result= DBHelper.getHelper().updateSetting(newPs);
        }
        if (result){
            ParamSetting newPs2 = JSON.toJavaObject(JSONObject.parseObject(req),ParamSetting.class);
            paramToStatic(newPs2);
        }
        return  result;
    }

    /**
     * 取现流水
     */
    @JavascriptInterface
    public String queryWithdrawLogList(String req) {
        List<WithdrawLog> withdrawlogList = DBHelper.getHelper().queryWithdraw(req);
        return JSONArray.toJSONString(withdrawlogList);
    }

    /**
     * 购买流水
     */
    @JavascriptInterface
    public String queryBuyLogList(String req) {
        List<BuyLog> buyLogList = DBHelper.getHelper().queryBuyLog(req);
        return JSONArray.toJSONString(buyLogList);
    }

    /**
     * 购买流水详情
     */
    @JavascriptInterface
    public String queryBuyDetail(String req) {
        JSONObject jsonObject = JSONObject.parseObject(req);
        BuyLog buyLog = DBHelper.getHelper().queryBuyLog(Integer.valueOf(jsonObject.getString("id")));
        return JSONArray.toJSONString(buyLog);
    }

    /**
     * 提现流水详情
     */
    @JavascriptInterface
    public String queryWithdrawDetail(String req) {
        JSONObject jsonObject = JSONObject.parseObject(req);
        WithdrawLog withdrawLog = DBHelper.getHelper().queryWithdrawLog(Integer.valueOf(jsonObject.getString("id")));
        return JSONArray.toJSONString(withdrawLog);
    }

    /**
     * 运行状态管理
     */
    @JavascriptInterface
    public String queryRunStatusList(String req) {
        List<RunStatusManage> rsmList = DBHelper.getHelper().queryRunManage(req);
        return JSONArray.toJSONString(rsmList);
    }

    /**
     * 广告列表
     */
    @JavascriptInterface
    public String queryAdvertList(String req) {
        List<Advert> advertList = DBHelper.getHelper().queryAdvert(req);
        return JSONArray.toJSONString(advertList);
    }

    /**
     * 清机统计
     */
    @JavascriptInterface
    @SuppressLint("SimpleDateFormat")
    public String emptyNotesStatistics() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = dateFormat.format(new Date());
        log.info("清机时间nowTime============="+nowTime);

        //查询上次清机时间
        ArrayList<EmpytNotes> empytNotesList = DBHelper.getHelper().queryEmptyNotesList(null);
        String startTime = "";
        if(empytNotesList.size()>0){
            startTime = empytNotesList.get(0).getCreateTime();
        }
        log.info("上次清机时间startTime============="+startTime);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start_time",startTime);
        jsonObject.put("end_time",nowTime);

        EmpytNotes empytNotes = new EmpytNotes();
        empytNotes.setCreateTime(nowTime);
        empytNotes.setLastTime(startTime);
        //查询加钞数据
        ArrayList<AddNotes> addNotesList = DBHelper.getHelper().queryAddNotesList(jsonObject.toJSONString());

        BigDecimal addCash = new BigDecimal(0);
        for(AddNotes addNotes :addNotesList){
            String cash = addNotes.getAmount();
            BigDecimal bigDecimal = new BigDecimal(cash);
            addCash = addCash.add(bigDecimal);
        }
        empytNotes.setAddCash(addCash.toString());
        //查询购买记录加钞数据
        List<BuyLog> buyLogList = DBHelper.getHelper().queryBuyLog(jsonObject.toJSONString());
        BigDecimal buyCash = new BigDecimal(0);
        for(BuyLog buyLog :buyLogList){
            String cash = buyLog.getCash();
            BigDecimal bigDecimal = new BigDecimal(cash);
            buyCash = buyCash.add(bigDecimal);
        }
        empytNotes.setBuyCash(buyCash.toString());
        //查询提现记录出钞数据
        List<WithdrawLog> sellLogList = DBHelper.getHelper().queryWithdrawByRedeemTime(jsonObject.toJSONString());
        BigDecimal sellCash = new BigDecimal(0);
        for(WithdrawLog withdrawLog :sellLogList){
            if(1 == withdrawLog.getRedeemStatus()){
                String cash = withdrawLog.getCash();
                BigDecimal bigDecimal = new BigDecimal(cash);
                sellCash = sellCash.add(bigDecimal);
            }
        }
        empytNotes.setSellCash(sellCash.toString());
        //插入清机表
        log.info("清机数据入库============="+empytNotes);
        ContentValues values = new ContentValues();
        values.put("add_cash",empytNotes.getAddCash());
        values.put("buy_cash",empytNotes.getBuyCash());
        values.put("sell_cash",empytNotes.getSellCash());
        values.put("create_time",empytNotes.getCreateTime());
        DBHelper.getHelper().addEmptyNotes(values);
        return empytNotes.toString();
    }

    /**
     * 清机统计列表查询
     */
    @JavascriptInterface
    public String queryEmptyNotesList(String req) {
        return JSONArray.toJSONString(DBHelper.getHelper().queryEmptyNotesList(req));

    }

    /**
     * 交易所订单
     *
     * @param req
     * @return
     */
    @JavascriptInterface
    public String queryOrderList(String req) {
        List<ExchangeOrder> orderList = DBHelper.getHelper().queryCoinbaseOrder(req);
        return JSONArray.toJSONString(orderList);
    }

    /**
     * 订单详情
     */
    @JavascriptInterface
    public String queryOrderDetail(String transId) {
        ExchangeOrder order = DBHelper.getHelper().queryCoinbaseOrderEixst(transId);
        return JSONArray.toJSONString(order);
    }

    /**
     * 发送日志文件到email
     *
     * @param email
     * @param date
     * @return
     */
    @JavascriptInterface
    public boolean sendLogs(String email, String date) {
        List<EntityFile> allFileList = new ArrayList<>();
        File directoryVideo = Environment.getExternalStoragePublicDirectory("JYT/Logs");
        getSDCardFile(allFileList, directoryVideo, ".log");// 获得日志文件
        try {
            List<EntityFile> resultLogs = getLogFile(allFileList, date, 30);
            if (resultLogs.size() == 0) {
                log.info(date + " 相关的日志未找到!");
                return false;
            }
            // 找到的日志文件进行压缩
            List<File> srcfile = new ArrayList<>();
            for (EntityFile entityFile : resultLogs) {
                srcfile.add(new File(entityFile.getPath()));
            }
            File zip = new File(Environment.getExternalStorageDirectory(), "JYT/Logs/" + date + ".zip");// 压缩文件
            ZipFiles(srcfile, zip);
            return SendMailUtil.send(zip, email, "LOG", "The log of " + date);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(date + e.getMessage());
        }
        return false;
    }

    /**
     * 递归查询的日志
     * 如当前找不到，往前推一天,最多推count天,否则死循环
     *
     * @param allFileList 日志文件
     * @param date        匹配日期
     * @param count       往前推一天的次数
     * @return
     * @throws Exception
     */
    private List<EntityFile> getLogFile(final List<EntityFile> allFileList, String date, int count) throws Exception {
        List<EntityFile> resultList = new ArrayList<>();
        //  循环查找文件
        for (EntityFile entityFile : allFileList) {
            if (entityFile.getThumbPath().contains(date)) {
                resultList.add(entityFile);
            }
        }
        if (resultList.size() == 0) {
            count--;
            if (count > 0) {
                resultList = getLogFile(allFileList, getYesterday(date), count);
            }
        }
        return resultList;
    }

    /**
     * 文件压缩
     *
     * @param srcFiles
     * @param zipFile
     * @throws Exception
     */
    private void ZipFiles(List<File> srcFiles, File zipFile) throws Exception {
        // 创建 FileOutputStream 对象
        FileOutputStream fileOutputStream = null;
        // 创建 ZipOutputStream
        ZipOutputStream zipOutputStream = null;
        // 创建 FileInputStream 对象
        FileInputStream fileInputStream = null;
        try {
            // 实例化 FileOutputStream 对象
            fileOutputStream = new FileOutputStream(zipFile);
            // 实例化 ZipOutputStream 对象
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            // 创建 ZipEntry 对象
            ZipEntry zipEntry = null;
            // 遍历源文件数组
            for (File currentFile : srcFiles) {
                // 将源文件数组中的当前文件读入 FileInputStream 流中
                fileInputStream = new FileInputStream(currentFile);
                // 实例化 ZipEntry 对象，源文件数组中的当前文件
                zipEntry = new ZipEntry(currentFile.getName());
                zipOutputStream.putNextEntry(zipEntry);
                // 该变量记录每次真正读的字节个数
                int len;
                // 定义每次读取的字节数组
                byte[] buffer = new byte[1024];
                while ((len = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取前一天
     *
     * @param dateStr
     * @return
     * @throws Exception
     */
    private String getYesterday(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date transTime = sdf.parse(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(transTime);
        c.add(Calendar.DAY_OF_YEAR, -1);
        return sdf.format(c.getTime());
    }

    /**
     * 获取sdcard上文件
     *
     * @param list
     * @param file
     * @param fileType
     */
    private void getSDCardFile(final List<EntityFile> list, File file, final String fileType) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(fileType) || name.contains(fileType)) {
                        EntityFile vi = new EntityFile();
                        vi.setThumbPath(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        list.add(vi);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getSDCardFile(list, file, fileType);
                }
                return false;
            }
        });
    }


    /**
     * 记录加钞信息
     *
     * @param amount
     */
    @JavascriptInterface
    public void saveAddnotes(String amount) {
        ContentValues values = getAddLog(amount);
        DBHelper.getHelper().insertAddnotes(values);
    }

    private ContentValues getAddLog(String amount) {

        ContentValues values = new ContentValues();
        values.put("amount", amount);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        values.put("create_time", time);
        return values;
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    @JavascriptInterface
    public String getCurrentVersion() {
        String defaultVersion = UploadTimer.DEFAULT_VERSION;
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
     * 保存硬件信息
     */
    @JavascriptInterface
    public String saveConfig(String configStr) {
        List<HardwareConfig> configs = JSONObject.parseArray(configStr, HardwareConfig.class);
        boolean result = DBHelper.getHelper().saveHardwareConfig(configs);
        if (result) {
            //设置硬件通讯参数
            ((MyApp) context.getApplicationContext()).setConfig(getMyConfig(DBHelper.getHelper().queryAllKey()));
            return "success";
        } else {
            return "";
        }

    }

    //组装config
    private Config getMyConfig(List<HardwareConfig> list) {
        Config config = new Config();
        for (HardwareConfig hConfig : list) {
            if ("PrinterComType".equals(hConfig.getHwKey())) {
                config.setPrintComm(hConfig.getHwValue());
            } else if ("PrinterDev".equals(hConfig.getHwKey())) {
                config.setPrintCom(hConfig.getHwValue());
            } else if ("Baudrate".equals(hConfig.getHwKey())) {
                config.setPrintBaudrate(hConfig.getHwValue());
            } else if ("LEDDev".equals(hConfig.getHwKey())) {
                config.setLEDCOM(hConfig.getHwValue());
            } else if ("LEDId".equals(hConfig.getHwKey()) && StringUtils.isNotBlank(hConfig.getHwValue())) {
                config.setLedId(Integer.parseInt(hConfig.getHwValue()));
            }else if ("LEDBussiness".equals(hConfig.getHwKey()) && StringUtils.isNotBlank(hConfig.getHwValue())) {
                config.setLedVendor(Integer.parseInt(hConfig.getHwValue()));
            }else if ("CPIDev".equals(hConfig.getHwKey())) {
                config.setCPIdev(hConfig.getHwValue());
            }
        }
        return config;
    }

    /**
     * 查看硬件信息
     */
    @JavascriptInterface
    public String queryConfig() {
        List<HardwareConfig> list=  DBHelper.getHelper().queryAllKey();
        return JSONObject.toJSONString(list);
    }

}
