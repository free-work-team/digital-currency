package com.jyt.hardware.cashoutmoudle.Sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.cashoutmoudle.bean.*;
import com.jyt.hardware.cashoutmoudle.enums.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBHelper {

	private static Logger log =  Logger.getLogger("BitCoinMaster");

	private static DBHelper helper ;
	private static SQLiteDatabase db;
	private String table1 = "log";
	private String table2= "cashbox";
	private String table3="client_log";
	private String table4="boardkey";
	private String table5="buy_log";
	private String table6="withdraw_log";
	private String table7="user";
	private String table8="hardware";
	private String table9="advert";
	private String table10="param_setting";
	private String table11="coinbase_order";
	private String table12="hardware_config";
	private String table13="add_notes";
	private String table14="empty_notes";
	private String table15="transfer_log";
	private static CashBoxSqlite sqlite;



	public static DBHelper getInstance(Context context){
		if (null==helper) {
			helper=new DBHelper();
		}
		if(db == null) {
			sqlite = new CashBoxSqlite(context);
			db = sqlite.getWritableDatabase();
		}
		return helper;
	}

	public static DBHelper getHelper(){
		return helper;
	}

	public ArrayList<LogObject> getLog(int type, String date){
		Cursor cursor=db.query( table1, null, "date=?", new String[]{date}, null, null, null, null);
	 ArrayList<LogObject> list=new ArrayList<LogObject>();
		while (null!=cursor&&cursor.moveToNext()) {
			LogObject obj=new LogObject();
			obj._id=cursor.getInt(cursor.getColumnIndex("_id"));
			obj.type=cursor.getInt(cursor.getColumnIndex("type"));
			obj.time=cursor.getString(cursor.getColumnIndex("time"));
			obj.log=cursor.getString(cursor.getColumnIndex("log"));
			obj.date=date;
			list.add(obj);
		}
		if (null!=cursor) cursor.close();
		return list;
	}
	
	/**
	 * 写日志
	 * @param type 日志等级
	 * @param logstr
	 */
	@SuppressLint("SimpleDateFormat")
	public void writeLog(int type,String logstr) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String time = sdf.format(now);
		ContentValues values = new ContentValues();
		values.put("log", logstr);
		values.put("type", type);
		values.put("date", time.substring(0,time.indexOf(" ")));
		values.put("time", time.substring(time.indexOf(" ")+1));
		db.insert(table1, null, values);

	}
	
	@SuppressLint("SimpleDateFormat")
	public void writeClientLog(int ucLoglevel, String Format){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String time = sdf.format(now);
		ContentValues values = new ContentValues();
		values.put("ucLoglevel", ucLoglevel);
		values.put("format", Format);
		values.put("date", time.substring(0,time.indexOf(" ")));
		values.put("time", time.substring(time.indexOf(" ")+1));
		db.insert(table3, null, values);
	}
	/**
	 * 存入秘钥到数据库（加密版使用的）
	 * @param key
	 */
	public void inserBoardKey(String key){
		ContentValues values = new ContentValues();
		values.put("key", key);
		db.insert(table4, null, values);
	}
	/**
	 * 获取加密版秘钥
	 * @return
	 */
	public String getBoardKey(){
		String key=null;
		Cursor cursor=	db.query(   table4, null, null, null, null, null, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			key=cursor.getString(cursor.getColumnIndex("key"));
		}
		return key;
	}
	/**
	 * 加钞  
	 * @param denomination 面额  注：一种面额只能装一个钞箱
	 * @param notesCount  张数
	 */
	public int addCash(int denomination,int notesCount ){
			if(notesCount<0) return -1;
			//加钞
			ContentValues values=new ContentValues();
			values.put("remaining_number", notesCount);//更新剩余数量
			values.put("all_number",notesCount);//总数量
			values.put("send_number", 0);//出钞总数
			values.put("other2", 0);//废钞数量
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			return 0; 
	}
	
	/**
	 * 查询钞箱信息
	 * @param denomination 面额
	 */
	public SqliteCashBoxBean getCashBoxInfo(int denomination){
		SqliteCashBoxBean box = null;
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			box=new SqliteCashBoxBean();
			box.setBoxid(cursor.getString(cursor.getColumnIndex("boxid")));
			box.setSolt(cursor.getInt(cursor.getColumnIndex("solt")));
			box.setDenomination(denomination);
			box.setRemaining_number(cursor.getInt(cursor.getColumnIndex("remaining_number")));//剩余数量
			box.setAll_number(cursor.getInt(cursor.getColumnIndex("all_number")));//加钞总张数
			box.setSend_number(cursor.getInt(cursor.getColumnIndex("send_number")));//出钞总张数
			box.setRecoveryQuantity(cursor.getInt(cursor.getColumnIndex("other2")));//回收钞箱数量
			cursor.close();
		}
		return box;
		
	}

	/**
	 * 准备钞票 减少钞票数量
	 * @param denomination 面额
	 * @param countNumber 缓冲数量
	 * @param  RejectCount 回收张数
	 */
	public boolean outNotes(int denomination,int countNumber,int RejectCount) {
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int remaining_number=cursor.getInt(cursor.getColumnIndex("remaining_number"));//剩余张数
			int rejectCounts=cursor.getInt(cursor.getColumnIndex("other2"));//回收钞箱数量
			ContentValues values=new ContentValues();
			//剩余钞票数量
			remaining_number=remaining_number-countNumber-RejectCount;
			//回收数量
			rejectCounts=rejectCounts+RejectCount;
			if(remaining_number<0) remaining_number=0;
			values.put("remaining_number", remaining_number); 
			values.put("other2", rejectCounts);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			log.info("[DBHelper]:记录本次取款数据,钞箱面额:"+denomination+"，缓冲张数:"+countNumber+"  ,回收张数："+RejectCount+" ,本次取款之后,钞箱剩余张数："+remaining_number+"，回收箱张数："+rejectCounts);
			cursor.close();
			return true;
		}else{
			log.info("[DBHelper]:准备钞票,减少钞票出错");
		}
		return false;
	}
	
	/**
	 * 准备钞票 统计钞箱数量   2016-09-27
	 * @param denomination 面额
	 * @param countNumber 缓冲数量
	 * @param  RejectCount 回收张数
	 */
	public SqliteCashBoxBean prepareOutNotes(int denomination,int countNumber,int RejectCount) {
		SqliteCashBoxBean bean=new SqliteCashBoxBean();
		log.info("[DBHelper] ：准备钞票,回收张数："+RejectCount+",出钞张数："+countNumber);
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int remaining_number=cursor.getInt(cursor.getColumnIndex("remaining_number"));//剩余张数
			int rejectCounts=cursor.getInt(cursor.getColumnIndex("other2"));//回收钞箱数量
			int send_number=cursor.getInt(cursor.getColumnIndex("send_number"));//出钞数
			ContentValues values=new ContentValues();
			//剩余钞票数量
			remaining_number=remaining_number-countNumber-RejectCount;
			//回收数量
			rejectCounts=rejectCounts+RejectCount;
			//出钞数
			send_number=send_number+countNumber;
			if(remaining_number<0) remaining_number=0;
			values.put("remaining_number", remaining_number); 
			values.put("other2", rejectCounts);
			values.put("send_number", send_number);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			log.info("[DBHelper] ：准备钞票,回收总数："+rejectCounts+"剩余钞票张数："+remaining_number+"，总出钞张数:"+send_number);
			cursor.close();
			bean.setRecoveryQuantity(rejectCounts);
			bean.setRemaining_number(remaining_number);
			bean.setSend_number(send_number);
		}else{
			log.error("[DBHelper] ：准备钞票,减少钞票出错");
		}
		return bean;
	}
	
	/**
	 * 送钞成功 减少钞票数量  
	 * @param denomination 面额
	 * @param countNumber 缓冲数量
	 * @param  RejectCount 回收张数
	 */
	public SqliteCashBoxBean outNotesCDM(int denomination,int countNumber,int RejectCount,int CDMType) {
		SqliteCashBoxBean cash=new SqliteCashBoxBean();
		log.info("[DBHelper]:"+denomination+"，缓冲张数:"+countNumber+"回收张数："+RejectCount);
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int all_number=cursor.getInt(cursor.getColumnIndex("all_number"));
			int remaining_number=cursor.getInt(cursor.getColumnIndex("remaining_number"));//剩余张数
			int rejectCounts=cursor.getInt(cursor.getColumnIndex("other2"));//回收钞箱数量
			int send_number=cursor.getInt(cursor.getColumnIndex("send_number"));//出钞数
			ContentValues values=new ContentValues();
			//剩余钞票数量
			remaining_number=remaining_number-countNumber-RejectCount;
			//回收数量
			rejectCounts=rejectCounts+RejectCount;
			//出钞数
			if(CDMType==6240){
				send_number=send_number+countNumber;
			}
			if(remaining_number<0) remaining_number=0;
			values.put("remaining_number", remaining_number); 
			values.put("other2", rejectCounts);
			values.put("send_number", send_number);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			log.info("[DBHelper]:准备钞票之后,剩余钞票："+remaining_number+"，回收箱数量："+rejectCounts);
			cursor.close();
		
			cash.setAll_number(all_number );
			cash.setDenomination(100);
			cash.setRemaining_number(remaining_number);
			cash.setSend_number(send_number);
			cash.setRecoveryQuantity(rejectCounts);
			return cash;
		}else{
			log.info("[DBHelper]:准备钞票,减少钞票出错");
		}
		return null;
	}
	/**
	 * 出钞成功 增加送钞数
	 * @param denomination 面额
	 * @param countNumber 出钞数量
	 */
	public boolean outNotes(int denomination,int countNumber) {
		log.info("[DBHelper] ：记录本次出钞"+countNumber+"张");
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int send_number=cursor.getInt(cursor.getColumnIndex("send_number"));//出钞数
			ContentValues values=new ContentValues();
			//出钞数
			send_number=send_number+countNumber;
			values.put("send_number", send_number);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			log.info("[DBHelper] ：钞箱总出钞总张数："+send_number);
			cursor.close();
			return true;
		}else{
			log.info("[DBHelper] ： 出钞成功， 增加送钞出错");
		}
		return false;
	}
	
	/**
	 * 出钞成功 增加送钞数
	 * @param denomination 面额
	 * @param countNumber 出钞数量
	 */
	public boolean addSendNumber(int denomination,int countNumber) {
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int send_number=cursor.getInt(cursor.getColumnIndex("send_number"));//出钞数
			ContentValues values=new ContentValues();
			//出钞数
			send_number=send_number+countNumber;
			values.put("send_number", send_number);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			log.info("[DBHelper] ：本次出钞张数："+countNumber+",钞箱出钞张数合计:"+send_number);
			cursor.close();
			return true;
		}else{
			log.info("[DBHelper] ： 出钞成功， 增加送钞出错");
		}
		return false;
	}
	/**
	 * 出钞成功，增加总的出钞张数
	 * @param denomination 面额
	 * @param countNumber 出钞张数
	 */
	public void sendOutNotes(int denomination,int countNumber){
		log.info("[DBHelper] :本次出钞   面额："+denomination+"  ，出钞张数："+countNumber);
		Cursor cursor=	db.query(   table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			int sendNumber=cursor.getInt(cursor.getColumnIndex("send_number"));
			ContentValues values=new ContentValues();
			sendNumber=sendNumber+countNumber;
			values.put("send_number", sendNumber);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			cursor.close();
		}
	}
	
	/**
	 * 出钞失败，回收钞票
	 * @param denomination 面额
	 * @param rejectNumber 回收数量
	 */
	 public boolean rejectNotes(int denomination,int rejectNumber){
		 if(rejectNumber<=0) return false;
		 Cursor cursor=	db.query( table2, null, "denomination=? ", new String[]{denomination+""}, null, null, null, null);
		 if(null!=cursor&&cursor.moveToNext()){
			 int rejectCounts=cursor.getInt(cursor.getColumnIndex("other2"));//回收钞箱数量
			 ContentValues values=new ContentValues();
			//回收数量
			rejectCounts=rejectCounts+rejectNumber;
			values.put("other2", rejectCounts);
			db.update(table2, values, "denomination=?", new String[]{denomination+""});
			cursor.close();
			log.info("[DBHelper]:回收钞票："+rejectNumber);
			return true;
		 }
		 return false;
	 }
	/**
	* 添加存款记录（用户买币）
	* @param values
	* @param 
	*/
	@SuppressLint("SimpleDateFormat")
	public void addBuyLog(ContentValues values) {
		db.insert(table5, null, values);
		log.info("[DBHelper] ： 添加买币记录成功");
	}
	/**
	 * 添加取款记录（用户卖币）
	 * @param 
	 * @param 
	 */
	@SuppressLint("SimpleDateFormat")
	public void addWithdrawLog(ContentValues values) {
		db.insert(table6, null, values);
		log.info("[DBHelper] ： 添加卖币记录成功");
	}

	/**
     *查询取款记录
	 * @param
	 * @param
	 */
    public WithdrawLog queryWithdrawEixst(String transId) {
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        String transId = jsonObject.getString("trans_id");
//        String txId = jsonObject.getString("tx_id");
        Cursor cursor = db.query( table6, null, "trans_id=? ", new String[]{transId}, null, null, null, null);
        WithdrawLog withdrawLog = null;
        while (null!=cursor&&cursor.moveToNext()) {
            withdrawLog = new WithdrawLog();
            withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
            withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
            withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
            withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
            withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
            withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
            withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
            withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
            withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
            withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
            withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
            withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
            withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
            withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
            withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
            withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
            withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
            withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
        }
        return withdrawLog;
    }
	/**
	 * 添加订单信息
	 *
	 * @param map
	 */
	public boolean insertOrderInfo(String transId, Map map, String terminalNo,String cryptoCurrency,String currency) {
		boolean result = false;
		ExchangeOrder orderDetail = new ExchangeOrder();
		orderDetail.setTransId(transId);
		orderDetail.setTerminalNo(terminalNo);
		orderDetail.setSize(getMapValue(map, "size"));
		orderDetail.setPrice(getMapValue(map, "price"));
		orderDetail.setProductId(getMapValue(map, "product_id"));
		orderDetail.setSide(getMapValue(map, "side"));
		orderDetail.setType(getMapValue(map, "type"));
		orderDetail.setFunds(getMapValue(map, "funds"));
		orderDetail.setCurrency(currency);
		orderDetail.setCreateTime(getDateFormat());
		orderDetail.setCryptoCurrency(cryptoCurrency);
		if (map.containsKey("id") && StringUtils.isNotEmpty(map.get("id").toString())) {
			orderDetail.setId(getMapValue(map, "id"));
			orderDetail.setTimeInForce(getMapValue(map, "time_in_force"));
			orderDetail.setCreatedAt(TzFormat(getMapValue(map, "created_at")));
			orderDetail.setFillFees(getMapValue(map, "fill_fees"));
			orderDetail.setFilledSize(getMapValue(map, "filled_size"));
			orderDetail.setExecutedValue(getMapValue(map, "executed_value"));
			orderDetail.setStatus(OrderStatusEnum.PRO_PENDING.getValue());
			orderDetail.setSettled(getMapValue(map, "settled"));
			result = true;
		} else {
			orderDetail.setStatus(OrderStatusEnum.PRO_FAIL.getValue());
			orderDetail.setMessage(getMapValue(map, "message"));
		}
		//记录订单
		DBHelper.getHelper().addCoinbaseOrder(orderDetail);
        return result;
	}

	/**
	 * 通过交易所订单id查询交易所
	 * @return
	 */
	public Integer getExchangeByOrderId(String orderId){
		ExchangeOrder orderObj = queryCoinbaseOrderByOrderId(orderId);
		if ("buy".equals(orderObj.getSide()) ){
			return  Integer.valueOf(queryBuyLogByTransId(orderObj.getTransId()).getStrategy());
		}else if ("sell".equals(orderObj.getSide())){
			return  Integer.valueOf(queryWithdrawEixst(orderObj.getTransId()).getStrategy());
		}
		return 0;
	}

	/**
	 * 添加coinbase订单记录
	 */
	@SuppressLint("SimpleDateFormat")
	public boolean addCoinbaseOrder(ExchangeOrder orderInfo) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(orderInfo.toString());
			ContentValues values = new ContentValues();
			Set<String> keySet = jsonObject.keySet();
			for (String key : keySet) {
				values.put(key, jsonObject.getString(key));
			}
			values.put("is_upload", 0);//更新
			db.insert(table11, null, values);
			log.info("[DBHelper] ： 交易所订单 添加数据库记录完成");
			return true;
		} catch (Exception e) {
			log.info("[DBHelper] ：" , e);
			return false;
		}
	}

	/**
	 * 添加转移记录
	 */
    public boolean addTransferLog(TransferLog transferLog) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTime = dateFormat.format(new Date());
            transferLog.setCreateTime(nowTime);
            transferLog.setIsUpload(0);
            transferLog.setStatus(TransferStatusEnum.PENDING.getValue());
            ContentValues values = new ContentValues();
            values.put("tx_id", transferLog.getTxId());
            values.put("trans_id", transferLog.getTransId());
            values.put("refid", transferLog.getRefid());
            values.put("terminal_no", transferLog.getTerminalNo());
            values.put("amount", transferLog.getAmount());
            values.put("funds", transferLog.getFunds());
            values.put("price", transferLog.getPrice());
            values.put("fee", transferLog.getFee());
            values.put("type", transferLog.getType());
            values.put("wallet", transferLog.getWallet());
            values.put("exchange", transferLog.getExchange());
            values.put("address", transferLog.getAddress());
            values.put("status", transferLog.getStatus());
            values.put("is_upload", transferLog.getIsUpload());
            values.put("create_time", transferLog.getCreateTime());
            values.put("update_time", transferLog.getUpdateTime());
            values.put("crypto_currency", transferLog.getCryptoCurrency());
            db.insert(table15, null, values);
            log.info("[DBHelper] ： 钱包交易所转移 添加数据库记录完成");
            return true;
        } catch (Exception e) {
            log.error("[DBHelper] ：", e);
            return false;
        }
	}

	/**
	 * 更新转移记录
	 * @param transferLog
	 */
	public boolean updateTransferLog(TransferLog transferLog) {
		if(transferLog == null || StringUtils.isEmpty(transferLog.getTransId())){
			log.info("--------------钱包交易所转移transferLog，更新失败");
			return false;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = dateFormat.format(new Date());
		ContentValues values = new ContentValues();
		values.put("status",transferLog.getStatus());
		if (StringUtils.isNotEmpty(transferLog.getTxId())) {
			values.put("tx_id", transferLog.getTxId());
		}
		if (StringUtils.isNotEmpty(transferLog.getFee())) {
			values.put("fee", transferLog.getFee());
		}
		if (StringUtils.isNotEmpty(transferLog.getAddress())) {
			values.put("address", transferLog.getAddress());
		}
		values.put("update_time", nowTime);
		values.put("is_upload", 0);
		int result = db.update(table15, values, "trans_id= ?", new String[]{transferLog.getTransId()});
		log.info("--------------钱包交易所转移transferLog完成，更新" + transferLog.getTransId() + "订单状态为：" + transferLog.getStatus() + "------------------");
		return result > 0;
	}

	/**
	 * 查询未完成的转移记录
	 */
    public List<TransferLog> queryTransferLog(String createTime) {
        Cursor cursor = db.query(table15, null, "status =? and create_time >= ? ", new String[]{TransferStatusEnum.PENDING.getValue(),createTime}, null, null, null, null);
        ArrayList<TransferLog> list = new ArrayList<>();
        TransferLog transferLog = null;
        while (null != cursor && cursor.moveToNext()) {
            transferLog = new TransferLog();
            transferLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            transferLog.setTxId(cursor.getString(cursor.getColumnIndex("tx_id")));
            transferLog.setRefid(cursor.getString(cursor.getColumnIndex("refid")));
            transferLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
            transferLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
            transferLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
            transferLog.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
            transferLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
            transferLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
            transferLog.setType(cursor.getString(cursor.getColumnIndex("type")));
            transferLog.setWallet(cursor.getInt(cursor.getColumnIndex("wallet")));
            transferLog.setExchange(cursor.getInt(cursor.getColumnIndex("exchange")));
            transferLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            transferLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            transferLog.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            transferLog.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
            transferLog.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));
            transferLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
            list.add(transferLog);
        }
        if (null != cursor) {
            cursor.close();
        }
        return list;
    }

	/**
	 * 根据transId查询转移记录
	 */
	public TransferLog queryTransferByTransId(String createTime,String transId) {
		Cursor cursor = db.query(table15, null, "status =? and create_time >= ? and trans_id = ?", new String[]{TransferStatusEnum.PENDING.getValue(),createTime,transId}, null, null, null, null);
		TransferLog transferLog =null;
		while (null != cursor && cursor.moveToNext()) {
			transferLog = new TransferLog();
			transferLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			transferLog.setTxId(cursor.getString(cursor.getColumnIndex("tx_id")));
			transferLog.setRefid(cursor.getString(cursor.getColumnIndex("refid")));
			transferLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			transferLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			transferLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			transferLog.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			transferLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			transferLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			transferLog.setType(cursor.getString(cursor.getColumnIndex("type")));
			transferLog.setWallet(cursor.getInt(cursor.getColumnIndex("wallet")));
			transferLog.setExchange(cursor.getInt(cursor.getColumnIndex("exchange")));
			transferLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			transferLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			transferLog.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
			transferLog.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			transferLog.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));
			transferLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
		}
		if (null != cursor) {
			cursor.close();
		}
		return transferLog;
	}

	@SuppressLint("SimpleDateFormat")
	public boolean saveHardwareConfig(List<HardwareConfig> hardwareConfigs) {
		try {
			for (HardwareConfig hardwareConfig : hardwareConfigs) {
				//判断key如果存在就update，否则insert
				String currentKey = hardwareConfig.getHwKey();
				HardwareConfig oldHardwareConfig = queryKeyEixst(currentKey);
				if (oldHardwareConfig != null && StringUtils.isNotEmpty(oldHardwareConfig.getHwKey())) {
					//更新
					updateConfigByKey(hardwareConfig);
				} else {
					//插入
					JSONObject jsonObject = JSONObject.parseObject(hardwareConfig.toString());
					ContentValues values = new ContentValues();
					Set<String> keySet = jsonObject.keySet();
					for (String key : keySet) {
						values.put(key, jsonObject.getString(key));
					}
					db.insert(table12, null, values);
					log.info("[DBHelper] ： hardware config 新增key:" + hardwareConfig.getHwValue() + ",添加数据库记录完成");
				}
			}
			return true;
		} catch (Exception e) {
			log.info("[DBHelper] ：" , e);
			return false;
		}
	}

	/**
	 * 查询hardwareConfig记录
	 *
	 * @param key
	 */
	public HardwareConfig queryKeyEixst(String key) {
		Cursor cursor = db.query(table12, null, "hw_key=? ", new String[]{key}, null, null, null, null);
		HardwareConfig hardwareConfig = new HardwareConfig();
		while (null != cursor && cursor.moveToNext()) {
			hardwareConfig.setHwKey(cursor.getString(cursor.getColumnIndex("hw_key")));
			hardwareConfig.setHwValue(cursor.getString(cursor.getColumnIndex("hw_value")));
			hardwareConfig.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
		}
		return hardwareConfig;
	}
	/**
	 * 查询all hardwareConfig记录
	 */
	public List<HardwareConfig> queryAllKey() {
		Cursor cursor = db.query(table12, null, null, null, null, null, null, null);
		List<HardwareConfig> list = new ArrayList<>();
		while (null != cursor && cursor.moveToNext()) {
			HardwareConfig hardwareConfig = new HardwareConfig();
			hardwareConfig.setHwKey(cursor.getString(cursor.getColumnIndex("hw_key")));
			hardwareConfig.setHwValue(cursor.getString(cursor.getColumnIndex("hw_value")));
			hardwareConfig.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			list.add(hardwareConfig);
		}
		if (null != cursor) cursor.close();
		return list;
	}


	/**
	 * 查询 CashAcceptor  hardwareConfig记录
	 */
	public String getCashAcceptor() {
		Cursor cursor = db.query(table12, null, null, null, null, null, null, null);
		String result = "";
		while (null != cursor && cursor.moveToNext()) {
			String hwKey = cursor.getString(cursor.getColumnIndex("hw_key"));
			if ("CashAcceptor".equals(hwKey)) {
				result = cursor.getString(cursor.getColumnIndex("hw_value"));
			}
		}
		if (null != cursor) {
			cursor.close();
		}
		return result;
	}


	/**
	 * 根据key 更新value
	 * @param hardwareConfig
	 */
	public void updateConfigByKey(HardwareConfig hardwareConfig) {
		ContentValues values = new ContentValues();
		values.put("hw_value", hardwareConfig.getHwValue());//更新
		int result = db.update(table12, values, "hw_key= ?", new String[]{hardwareConfig.getHwKey()});
		log.info("[DBHelper] ： hardware config 更新key:" + hardwareConfig.getHwKey() + " ,value:" + hardwareConfig.getHwValue() + ",添加数据库记录" + (result > 0 ? "成功" : "失败"));
	}

    /**
     * 更新数据库查询coinbase订单状态为done
     * @param id
     * @param status
     */
    public void updateCoinbaseOrder(String id, String price, String status,String settled,String filled_size,String orderFees,String orderValue) {
        ContentValues values = new ContentValues();
        values.put("status", status);//更新
        values.put("price", price);//更新
        values.put("settled", settled);//更新
        values.put("filled_size", filled_size);//更新
        values.put("fill_fees", orderFees);//更新
        values.put("executed_value", orderValue);//更新
        values.put("is_upload", 0);//更新
        int result = db.update(table11, values, "id= ?", new String[]{id});
        log.info("--------------交易所，更新" + id + "订单状态为：" + status + "------------------");
    }

	/**
	 * 更新错误信息
	 *
	 * @param id
	 * @param message
	 */
	public void updateOrderMessage(String id, String message) {
		ContentValues values = new ContentValues();
		values.put("message", message);//更新
		values.put("is_upload", 0);//更新
		int result = db.update(table11, values, "id= ?", new String[]{id});
		log.info("--------------coinbase更新成功" + id + ",message：" + message + "------------------");
	}

    /**
     * 取消订单更新
     * @param id
     * @param message
     * @param status
     */
    public void cancelCoinbaseOrder(String id, String message, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);//更新
        values.put("message", message);//更新
        values.put("is_upload", 0);//更新
        int result = db.update(table11, values, "id= ?", new String[]{id});
        log.info("--------------coinbase取消订单，更新" + id + "订单状态为：" + status + "------------------");
    }
	/**
	 * 查询coinbase订单流水
	 *
	 * @param json
	 */
	public ArrayList<ExchangeOrder> queryCoinbaseOrder(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String transId = "%" + jsonObject.getString("transId") + "%";
			String side = jsonObject.getString("side");
			String cryptoCurrency = jsonObject.getString("crypto_currency");
			String status = jsonObject.getString("status");
			String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			if (StringUtils.isNotEmpty(transId)) {
				conditionArray.add(transId);
				condition += " and trans_id like ? ";
			}
			if (StringUtils.isNotEmpty(side)) {
				conditionArray.add(side);
				condition += " and side = ? ";
			}
			if (StringUtils.isNotEmpty(cryptoCurrency)) {
				conditionArray.add(cryptoCurrency);
				condition += " and crypto_currency = ? ";
			}
			if (StringUtils.isNotEmpty(status)) {
				conditionArray.add(status);
				condition += " and status = ? ";
			}
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and create_time between ? and ? ";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and create_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and create_time < ?";
				}
			}
		}
		Cursor cursor = db.query(table11, null, condition, conditionArray.toArray(new String[conditionArray.size()]), null, null, "create_time desc", null);
		ArrayList<ExchangeOrder> list = new ArrayList<>();
		while (null != cursor && cursor.moveToNext()) {
			ExchangeOrder orderDetail = new ExchangeOrder();
			orderDetail.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderDetail.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderDetail.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderDetail.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderDetail.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderDetail.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderDetail.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderDetail.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderDetail.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderDetail.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderDetail.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderDetail.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderDetail.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderDetail.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderDetail.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			orderDetail.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			orderDetail.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
			list.add(orderDetail);
		}
		if (null != cursor) cursor.close();
		return list;
	}


	/**
	 * 查询coinbase订单记录
	 *
	 * @param transId
	 */
	public ExchangeOrder queryCoinbaseOrderEixst(String transId) {
		Cursor cursor = db.query(table11, null, "trans_id=? ", new String[]{transId}, null, null, null, null);
		ExchangeOrder orderInfo = new ExchangeOrder();
		while (null != cursor && cursor.moveToNext()) {
			orderInfo.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderInfo.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderInfo.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderInfo.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderInfo.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderInfo.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderInfo.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderInfo.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderInfo.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderInfo.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderInfo.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
			orderInfo.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderInfo.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderInfo.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderInfo.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderInfo.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderInfo.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderInfo.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderInfo.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			orderInfo.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			orderInfo.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
		}
		return orderInfo;
	}

	/**
	 * 查询订单记录
	 *
	 * @param orderId
	 */
	public ExchangeOrder queryCoinbaseOrderByOrderId(String orderId) {
		Cursor cursor = db.query(table11, null, "id=? ", new String[]{orderId}, null, null, null, null);
		ExchangeOrder orderInfo = new ExchangeOrder();
		while (null != cursor && cursor.moveToNext()) {
			orderInfo.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderInfo.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderInfo.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderInfo.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderInfo.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderInfo.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderInfo.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderInfo.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderInfo.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderInfo.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderInfo.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
			orderInfo.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderInfo.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderInfo.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderInfo.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderInfo.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderInfo.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderInfo.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderInfo.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			orderInfo.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			orderInfo.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
		}
		return orderInfo;
	}

	/**
	 *查询购买记录
	 * @param
	 * @param
	 */
	public BuyLog queryBuyLog(Integer id) {
		Cursor cursor = db.query( table5, null, "_id=? ", new String[]{id+""}, null, null, null, null);
		BuyLog buyLog = null;
		while (null!=cursor&&cursor.moveToNext()) {
			buyLog = new BuyLog();
			buyLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			buyLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			buyLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			buyLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			buyLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			buyLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			buyLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			buyLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			buyLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			buyLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			buyLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			buyLog.setChannelFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			buyLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			buyLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			buyLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			buyLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			buyLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			buyLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			buyLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			buyLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			buyLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
		}
		return buyLog;
	}

	/**
	 *查询购买记录
	 * @param
	 * @param
	 */
	public BuyLog queryBuyLogByTransId(String transId) {
		Cursor cursor = db.query( table5, null, "trans_id=? ", new String[]{transId}, null, null, null, null);
		BuyLog buyLog = null;
		while (null!=cursor&&cursor.moveToNext()) {
			buyLog = new BuyLog();
			buyLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			buyLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			buyLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			buyLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			buyLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			buyLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			buyLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			buyLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			buyLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			buyLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			buyLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			buyLog.setChannelFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			buyLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			buyLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			buyLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			buyLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			buyLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			buyLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			buyLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			buyLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			buyLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
		}
		return buyLog;
	}

	/**
	 *查询提现记录
	 * @param
	 * @param
	 */
	public WithdrawLog queryWithdrawLog(Integer id) {
		Cursor cursor = db.query( table6, null, "_id=? ", new String[]{id+""}, null, null, null, null);
		WithdrawLog withdrawLog = null;
		while (null!=cursor&&cursor.moveToNext()) {
			withdrawLog = new WithdrawLog();
			withdrawLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
            withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
        }
		return withdrawLog;
	}

	/**
	 * 通过地址查询卖币记录
	 * @param targetAddress
	 * @return
	 */
	public WithdrawLog queryWithdrawByAddress(String targetAddress) {
		Cursor cursor = db.query( table6, null, "target_address=? ", new String[]{targetAddress}, null, null, null, null);
		WithdrawLog withdrawLog = null;
		while (null!=cursor&&cursor.moveToNext()) {
			withdrawLog = new WithdrawLog();
			withdrawLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
		}
		return withdrawLog;
	}
	/**
	 * 通过地址、数量查询卖币记录
	 * @param targetAddress
	 * @return
	 */
	public WithdrawLog queryWithdrawByAddress(String targetAddress,String amount) {
		Cursor cursor = db.query( table6, null, "target_address=? and amount = ? ", new String[]{targetAddress,amount}, null, null, null, null);
		WithdrawLog withdrawLog = null;
		while (null!=cursor&&cursor.moveToNext()) {
			withdrawLog = new WithdrawLog();
			withdrawLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
		}
		return withdrawLog;
	}

	/**
	 * 更新取款记录状态
	 * @param
	 * @param
	 */
	public boolean updateWithdraw(String transId,int redeemStatus,int outCount) {
		ContentValues values=new ContentValues();
		values.put("redeem_status", redeemStatus);
        values.put("is_upload",0);
        values.put("outCount", outCount);
		if(1 == redeemStatus){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dateFormat.format(new Date());
			values.put("redeem_time",time);
		}
		int result = db.update(table6, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： 更新取款记录，取款数量:"+outCount+",成功");
		return result>0;
	}

	/**
	 * 更新备注
	 * @param transId
	 * @param remark
	 */
	public void updateWithdrawRemark(String transId, String remark,int status) {
		ContentValues values = new ContentValues();
		values.put("remark", remark);
		values.put("trans_status", status);
        values.put("is_upload",0);
        int result = db.update(table6, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： " + transId + "更新卖币记录，remark:" + remark);
	}

	/**
	 * 更新渠道手续费
	 * @param transId
	 * @param fee
	 */
	public void updateWithdrawFee(String transId, String fee) {
		ContentValues values = new ContentValues();
		values.put("c_fee", fee);
        values.put("is_upload",0);
        int result = db.update(table6, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： " + transId + "更新卖币记录:c_fee:" + fee);
	}

	/**
	 * 更新备注
	 * @param transId
	 * @param remark
	 * @param status
	 */
	public void updateBuyLogRemarkAndStatus(String transId, String remark,String status) {
		ContentValues values = new ContentValues();
		values.put("remark", remark);
		values.put("status", status);
        values.put("is_upload",0);
		int result = db.update(table5, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： " + transId + "更新买币记录，remark:" + remark);
	}

	/**
	 * 更新取款记录交易状态为已完成
	 * @param
	 * @param
	 */
	public boolean updateWithdrawToConfirm(String transId,int transStatus) {
		ContentValues values=new ContentValues();
		values.put("trans_status",transStatus);
		//重新上传
		values.put("is_upload",0);
		int result = db.update(table6, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： 更新取款记录交易状态为已完成");
		return result>0;
	}

	/**
	 * 更新确认状态
	 * @param transId
	 * @return
	 */
	public boolean updateWithdrawToConfirmStatus(String transId) {
		ContentValues values=new ContentValues();
		values.put("confirm_status", ConfirmStatusEnum.CONFIRMED.getValue());
		//重新上传
		values.put("is_upload",0);
		int result = db.update(table6, values, "trans_id=? ", new String[]{transId});
		log.info("[DBHelper] ： 更新取款记录交易状态为已完成");
		return result>0;
	}
	/**
	 * 更新取款记录状态
	 * 仅用于更新订单为处理中
	 * @param
	 * @param
	 */
	public boolean updateWithdrawNew(String address, String channelTransId, int transStatus, String remark, int redeemStatus, int outCount) {
		ContentValues values = new ContentValues();
		if (StringUtils.isNotEmpty(channelTransId)) {
			values.put("channel_trans_id", channelTransId);
		}
		values.put("trans_status", transStatus);
		if (StringUtils.isNotEmpty(remark)) {
			values.put("remark", remark);
		}
		values.put("redeem_status", redeemStatus);
		values.put("outCount", outCount);
		//需要重新上传
		values.put("is_upload", 0);
		int result = db.update(table6, values, "target_address=? ", new String[]{address});
		log.info("[DBHelper] ： 更新卖币记录成功");
		return result > 0;
	}
	
	 /**
	  * 清除数据
	  */
	public void clearDB(){
		db.execSQL("delete from "+table1);
//		db.execSQL("update sqlite_sequence SET _id = 0 where name = 'log ");
		db.execSQL("delete from "+table3);
//		db.execSQL("update sqlite_sequence SET _id = 0 where name = 'client_log ");
		Cursor cursor=	db.query( table2, null, "denomination=? ", new String[]{"100"}, null, null, null, null);
		if (null!=cursor&&cursor.moveToNext()) {
			ContentValues values=new ContentValues();
			values.put("boxid", "0");
			values.put("denomination", 100);
			values.put("solt", 0);
			values.put("remaining_number", 0);
			values.put("send_number", 0);
			values.put("all_number", 0);
			db.update(table2, values, "denomination=? ",  new String[]{"100"});
			cursor.close();
		}
	}

	public void clearWithdraw(){
		db.execSQL("delete from "+table6);
	}
	
	public void close() {
		if (helper!=null&&db!=null&&db.isOpen()) {
			db.close();
			sqlite.close();
			helper=null;
			log.info("db：close");
		}
	}
	
    /**
     *验证后台用户登录
     * @param
     * @param
     */
    public User queryUserEixst(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String account = jsonObject.getString("account");
        String password = jsonObject.getString("password");
        Cursor cursor = db.query( table7, null, "account=? and password=? ", new String[]{account,password}, null, null, null, null);
        User user = null;
        while (null!=cursor&&cursor.moveToNext()) {
            user = new User();
            user.setAccount(cursor.getString(cursor.getColumnIndex("account")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
        }
        return user;
    }
    
    
    /**
     *查询登录用户信息
     * @param
     * @param
     */
    public User queryUserInfo(String account) {
        Cursor cursor = db.query( table7, null, "account=?", new String[]{account}, null, null, null, null);
        User user = null;
        while (null!=cursor&&cursor.moveToNext()) {
            user = new User();
            user.setAccount(cursor.getString(cursor.getColumnIndex("account")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
        }
        return user;
    }

    /**
     * 修改密码
     * @param
     * @param
     */
    public boolean updatePassword(String json,String account) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        ContentValues values=new ContentValues();
        values.put("password", jsonObject.getString("new_password"));
        db.update(table7, values, "account=?", new String[]{account});
        log.info("[DBHelper] ： 修改密码成功");
        return true;
    }

    /**
     *查询设置参数信息
     * @param
     * @param
     */
    public ParamSetting queryParamInfo() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        while (null!=cursor&&cursor.moveToNext()) {
            ps = new ParamSetting();
            ps.setId(cursor.getString(cursor.getColumnIndex("_id")));
            ps.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
            ps.setWebAddress(cursor.getString(cursor.getColumnIndex("web_address")));
            ps.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            ps.setMerchantName(cursor.getString(cursor.getColumnIndex("merchant_name")));
			ps.setRateSource(cursor.getInt(cursor.getColumnIndex("rate_source")));
			ps.setKycEnable(cursor.getInt(cursor.getColumnIndex("kyc_enable")));
			ps.setHotline(cursor.getString(cursor.getColumnIndex("hotline")));
            ps.setEmail(cursor.getString(cursor.getColumnIndex("e_mail")));
			ps.setOnline(cursor.getString(cursor.getColumnIndex("online")));
            ps.setLimitCash(cursor.getString(cursor.getColumnIndex("limit_cash")));
            ps.setKycUrl(cursor.getString(cursor.getColumnIndex("kyc_url")));
            ps.setWay(cursor.getInt(cursor.getColumnIndex("way")));
			if(StringUtils.isNotBlank(cursor.getString(cursor.getColumnIndex("crypto_settings")))){
				ps.setCryptoSettings(JSON.parseObject(cursor.getString(cursor.getColumnIndex("crypto_settings"))).toJavaObject(Crypto.class));
			}
        }
        return ps;
    }

	public static void main(String[] args) {
		ParamSetting paramSetting = JSON.parseObject("{\"cryptoSettings\":{\"btc\":{\"buySingleFee\":\"123\"},\"eth\":{\"buySingleFee\":\"456\"}},\"password\":\"123456\"}").toJavaObject(ParamSetting.class);
    }
    /**
     *查询设置参数币种信息
     * @param
     * @param
     */
    public String queryParamCurrency() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        String currency ="";
        while (null!=cursor&&cursor.moveToNext()) {
            currency = cursor.getString(cursor.getColumnIndex("currency"));
        }
        return currency;
    }

    /**
     *查询设置参数单双向
     * @param
     * @param
     */
    public int queryParamWay() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        int way = 2;
        while (null!=cursor&&cursor.moveToNext()) {
            way = cursor.getInt(cursor.getColumnIndex("way"));
        }
        return way;
    }

	/**
	 * 查询是否需要kyc
	 * @return
	 */
	public Integer queryKycEnable() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        //默认需要
        Integer kycEnable = 1;
        while (null!=cursor&&cursor.moveToNext()) {
			kycEnable = cursor.getInt(cursor.getColumnIndex("kyc_enable"));
        }
        return kycEnable;
    }

    /**
	 * 查询汇率来源
	 * @return
	 */
	public Integer queryRateSource() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        //默认需要
        Integer rateSource = 1;
        while (null!=cursor&&cursor.moveToNext()) {
			rateSource = cursor.getInt(cursor.getColumnIndex("rate_source"));
        }
        return rateSource;
    }

    /**
     *查询设置参数下单类型
     * @param
     * @param
     */
    public String queryParamOrderType() {
        Cursor cursor = db.query( table10, null, "", new String[]{}, null, null, null, null);
        ParamSetting ps = null;
        String orderType ="";
        while (null!=cursor&&cursor.moveToNext()) {
			orderType = cursor.getString(cursor.getColumnIndex("order_type"));
        }
        return orderType;
    }


	/**
	 * 初始化时设置币种
	 */
	public boolean updateSettingCurrency(String currency) {
		int result = 0;
		ContentValues values = new ContentValues();
		values.put("currency", currency);
		try {
			ParamSetting ps = DBHelper.getHelper().queryParamInfo();
			if (ps == null) {
				result =Long.valueOf( db.insert(table10, null, values)).intValue();
			} else {
				result = db.update(table10, values, "", new String[]{});
			}
			log.info("[DBHelper] ：更新币种设置成功：" + currency);
		}catch (Exception e){
			log.error("更新币种设置失败",e);
		}
		return result > 0;
	}

    /**
     * 保存设置参数信息
     */
    public boolean insertSetting(ParamSetting paramSetting) {
		ContentValues values = new ContentValues();
		values.put("terminal_no", paramSetting.getTerminalNo());
		values.put("merchant_name", paramSetting.getMerchantName());
		values.put("hotline", paramSetting.getHotline());
		values.put("e_mail", paramSetting.getEmail());
		values.put("rate_source", paramSetting.getRateSource());
		values.put("kyc_enable", paramSetting.getKycEnable());
		values.put("limit_cash", paramSetting.getLimitCash());
		values.put("kyc_url", paramSetting.getKycUrl());
		values.put("way", paramSetting.getWay());
		if(StringUtils.isNotBlank(paramSetting.getWebAddress())){
			values.put("web_address", paramSetting.getWebAddress());
		}
		if(StringUtils.isNotBlank(paramSetting.getPassword())){
			values.put("password", paramSetting.getPassword());
		}
		if(StringUtils.isNotBlank(paramSetting.getOnline())){
			values.put("online", paramSetting.getOnline());
		}
		values.put("crypto_settings",JSON.toJSONString(paramSetting.getCryptoSettings()));

        db.insert(table10,null, values);
        log.info("[DBHelper] ：保存参数设置成功");
        return true;
    }

	/**
     * 更新设置参数信息
     */
    public boolean updateSetting(ParamSetting paramSetting) {
		ContentValues values = new ContentValues();
		values.put("terminal_no", paramSetting.getTerminalNo());
		values.put("merchant_name", paramSetting.getMerchantName());
		values.put("hotline", paramSetting.getHotline());
		values.put("e_mail", paramSetting.getEmail());
		values.put("rate_source", paramSetting.getRateSource());
		values.put("kyc_enable", paramSetting.getKycEnable());
		values.put("limit_cash", paramSetting.getLimitCash());
		values.put("kyc_url", paramSetting.getKycUrl());
		values.put("way",paramSetting.getWay());
		if(StringUtils.isNotBlank(paramSetting.getWebAddress())){
			values.put("web_address", paramSetting.getWebAddress());
		}
		if(StringUtils.isNotBlank(paramSetting.getPassword())){
			values.put("password", paramSetting.getPassword());
		}
		if(StringUtils.isNotBlank(paramSetting.getOnline())){
			values.put("online", paramSetting.getOnline());
		}
		values.put("crypto_settings",JSON.toJSONString(paramSetting.getCryptoSettings()));
		int result = db.update(table10, values, "", new String[]{});
		log.info("[DBHelper] ：更新参数设置成功");
		return result > 0;
    }

    /**
     *查询取现流水
     * @param
     * @param
     */
    public ArrayList<WithdrawLog> queryWithdraw(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String targetAddress = jsonObject.containsKey("target_address")?("%" + jsonObject.getString("target_address") + "%"):"";
            String transId = jsonObject.containsKey("trans_id")?("%" + jsonObject.getString("trans_id") + "%"):"";
            String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			String cryptoCurrency = jsonObject.getString("crypto_currency");
			String status = jsonObject.getString("status");
			if (StringUtils.isNotEmpty(status)) {
				conditionArray.add(status);
				condition += " and trans_status = ? ";
			}
			if (StringUtils.isNotEmpty(cryptoCurrency)) {
				conditionArray.add(cryptoCurrency);
				condition += " and crypto_currency = ? ";
			}
            if (StringUtils.isNotEmpty(transId)) {
                conditionArray.add(transId);
                condition += " and trans_id like ? ";
            }
			if (StringUtils.isNotEmpty(targetAddress)) {
				conditionArray.add(targetAddress);
				condition += " and target_address like ? ";
			}
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and trans_time between ? and ?";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and trans_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and trans_time < ?";
				}
			}
		}
		Cursor cursor = db.query( table6, null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, "trans_time desc", null);
		ArrayList<WithdrawLog> list=new ArrayList<WithdrawLog>();
		while (null!=cursor&&cursor.moveToNext()) {
			WithdrawLog withdrawLog = new WithdrawLog();
			withdrawLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
			list.add(withdrawLog);
		}
		if (null!=cursor) cursor.close();
		return list;
	}

	/**
	 *查询取现流水
	 * @param
	 * @param
	 */
	public ArrayList<WithdrawLog> queryWithdrawByRedeemTime(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and redeem_time between ? and ?";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and redeem_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and redeem_time < ?";
				}
			}
		}
		Cursor cursor = db.query( table6, null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, "redeem_time desc", null);
		ArrayList<WithdrawLog> list=new ArrayList<WithdrawLog>();
		while (null!=cursor&&cursor.moveToNext()) {
			WithdrawLog withdrawLog = new WithdrawLog();
			withdrawLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
			list.add(withdrawLog);
		}
		if (null!=cursor) cursor.close();
		return list;
	}


    /**
     *查询购买流水
     * @param
     * @param
     */
    public ArrayList<BuyLog> queryBuyLog(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String address = jsonObject.containsKey("address")?("%" + jsonObject.getString("address") + "%"):"";
			String transId = jsonObject.containsKey("trans_id")?("%" + jsonObject.getString("trans_id") + "%"):"";
			String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			String cryptoCurrency = jsonObject.getString("crypto_currency");
			String status = jsonObject.getString("status");
			if (StringUtils.isNotEmpty(status)) {
				conditionArray.add(status);
				condition += " and status = ? ";
			}
			if (StringUtils.isNotEmpty(cryptoCurrency)) {
				conditionArray.add(cryptoCurrency);
				condition += " and crypto_currency = ? ";
			}
			if (StringUtils.isNotEmpty(transId)) {
				conditionArray.add(transId);
				condition += " and trans_id like ? ";
			}
			if (StringUtils.isNotEmpty(address)) {
				conditionArray.add(address);
				condition += " and address like ? ";
			}
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and trans_time between ? and ?";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and trans_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and trans_time < ?";
				}
			}
		}
        Cursor cursor = db.query( table5, null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, "trans_time desc", null);
        ArrayList<BuyLog> list=new ArrayList<BuyLog>();
        while (null!=cursor&&cursor.moveToNext()) {
            BuyLog buyLog=new BuyLog();
            buyLog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            buyLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
            buyLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
            buyLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            buyLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
            buyLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
            buyLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
            buyLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			buyLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			buyLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
            buyLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
            buyLog.setChannelFee(cursor.getString(cursor.getColumnIndex("c_fee")));
            buyLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
            buyLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
            buyLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
            buyLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			buyLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			buyLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			buyLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			buyLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
            list.add(buyLog);
        }
        if (null!=cursor) cursor.close();
        return list;

    }
    //通过是否上传字段查询购买记录
	public ArrayList<BuyLog> queryBuyLogByIsUpload(String date){
		Cursor cursor=db.query( table5, null, "is_upload=?", new String[]{date}, null, null, null, null);
		ArrayList<BuyLog> list=new ArrayList<BuyLog>();
		while (null!=cursor&&cursor.moveToNext()) {
			BuyLog buyLog=new BuyLog();
			buyLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			buyLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			buyLog.setChannelFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			buyLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			buyLog.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
			buyLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			buyLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			buyLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			buyLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			buyLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			buyLog.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			buyLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			buyLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			buyLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			buyLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			buyLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			buyLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			buyLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			buyLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			buyLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			list.add(buyLog);
		}
		if (null!=cursor) cursor.close();
		return list;
	}

	//通过是否上传字段查询提现记录
	public ArrayList<WithdrawLog> queryWithdrawLogByIsUpload(String date) {
//		String dateCondition = formatDate2Hours(new Date());
		Cursor cursor = db.query(table6, null, "is_upload=? ", new String[]{date}, null, null, null, null);
		ArrayList<WithdrawLog> list = new ArrayList<WithdrawLog>();
		while (null != cursor && cursor.moveToNext()) {
			if(StringUtils.isNotBlank(cursor.getString(cursor.getColumnIndex("trans_id")))){
				WithdrawLog withdrawLog = new WithdrawLog();
				withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
				withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
				withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
				withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
				withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
				withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
				withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
				withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
				withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
				withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
				withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
				withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
				withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
				withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
				withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
				withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
				withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
				withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
				withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
				withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
				withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
				withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
				withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
				withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
				withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
				withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
				list.add(withdrawLog);
			}
		}
		if (null != cursor) {cursor.close();}
		return list;
	}

	//通过交易状态查询购买记录
	public ArrayList<WithdrawLog> queryBuyLogByTransStatus(String status,String date) {
		String conditionStr = "trans_status = ? and trans_time >= ?";
		String[] selectionArgs=new String[]{status,date};
		// 不传时间，只根据状态查询
		if (StringUtils.isEmpty(date)){
			conditionStr = "trans_status = ? ";
			selectionArgs=new String[]{status};
		}
		Cursor cursor = db.query(table6, null, conditionStr, selectionArgs, null, null, null, null);
		ArrayList<WithdrawLog> list = new ArrayList<WithdrawLog>();
		while (null != cursor && cursor.moveToNext()) {
			WithdrawLog withdrawLog = new WithdrawLog();
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
			list.add(withdrawLog);
		}
		if (null != cursor) {cursor.close();}
		return list;
	}

	//通过交易状态查询提现记录
	public ArrayList<WithdrawLog> queryWithdrawLogByTransStatus(String status,String date) {
    	String conditionStr = "trans_status = ? and trans_time >= ?";
		String[] selectionArgs=new String[]{status,date};
		// 不传时间，只根据状态查询
    	if (StringUtils.isEmpty(date)){
			 conditionStr = "trans_status = ? ";
			selectionArgs=new String[]{status};
		}
		Cursor cursor = db.query(table6, null, conditionStr, selectionArgs, null, null, null, null);
		ArrayList<WithdrawLog> list = new ArrayList<WithdrawLog>();
		while (null != cursor && cursor.moveToNext()) {
			WithdrawLog withdrawLog = new WithdrawLog();
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
			list.add(withdrawLog);
		}
		if (null != cursor) {cursor.close();}
		return list;
	}

	//通过是否上传字段查询提现记录
	public ArrayList<WithdrawLog> queryWithdrawLogByConfirmStatus(String confirmStatus,String date) {
    	String conditionStr = "confirm_status = ? and trans_status != ? and trans_time >= ?";
		String[] selectionArgs=new String[]{confirmStatus,Integer.valueOf(TranStatusEnum.INIT.getValue()).toString(),date};
		// 不传时间，只根据状态查询
    	if (StringUtils.isEmpty(date)){
			 conditionStr = "confirm_status = ? and trans_status != ?";
			selectionArgs=new String[]{confirmStatus,Integer.valueOf(TranStatusEnum.INIT.getValue()).toString()};
		}
		Cursor cursor = db.query(table6, null, conditionStr, selectionArgs, null, null, null, null);
		ArrayList<WithdrawLog> list = new ArrayList<WithdrawLog>();
		while (null != cursor && cursor.moveToNext()) {
			WithdrawLog withdrawLog = new WithdrawLog();
			withdrawLog.setTargetAddress(cursor.getString(cursor.getColumnIndex("target_address")));
			withdrawLog.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			withdrawLog.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			withdrawLog.setStrategy(cursor.getString(cursor.getColumnIndex("strategy")));
			withdrawLog.setCustomerId(cursor.getString(cursor.getColumnIndex("customer_id")));
			withdrawLog.setExchangeRate(cursor.getString(cursor.getColumnIndex("exchange_rate")));
			withdrawLog.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			withdrawLog.setFee(cursor.getString(cursor.getColumnIndex("fee")));
			withdrawLog.setcFee(cursor.getString(cursor.getColumnIndex("c_fee")));
			withdrawLog.setCash(cursor.getString(cursor.getColumnIndex("cash")));
			withdrawLog.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			withdrawLog.setExtId(cursor.getString(cursor.getColumnIndex("ext_id")));
			withdrawLog.setTransTime(cursor.getString(cursor.getColumnIndex("trans_time")));
			withdrawLog.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			withdrawLog.setTransStatus(cursor.getInt(cursor.getColumnIndex("trans_status")));
			withdrawLog.setRedeemStatus(cursor.getInt(cursor.getColumnIndex("redeem_status")));
			withdrawLog.setOutCount(cursor.getInt(cursor.getColumnIndex("outCount")));
			withdrawLog.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
			withdrawLog.setSellType(cursor.getString(cursor.getColumnIndex("sell_type")));
			withdrawLog.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			withdrawLog.setRedeemTime(cursor.getString(cursor.getColumnIndex("redeem_time")));
			withdrawLog.setExchangeStrategy(cursor.getInt(cursor.getColumnIndex("exchange_strategy")));
			withdrawLog.setConfirmStatus(cursor.getInt(cursor.getColumnIndex("confirm_status")));
			withdrawLog.setChannelTransId(cursor.getString(cursor.getColumnIndex("channel_trans_id")));
			withdrawLog.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			withdrawLog.setAddressId(cursor.getString(cursor.getColumnIndex("address_id")));
			list.add(withdrawLog);
		}
		if (null != cursor) {cursor.close();}
		return list;
	}


	/**
	 * 通过状态时间查询pro订单
	 * @param status
	 * @param date
	 * @return
	 */
	public ArrayList<ExchangeOrder> queryCoinbaseOrder(String status, String date) {
		Cursor cursor = db.query(table11, null, "status =? and create_time >=?", new String[]{status,date}, null, null, null, null);
		ArrayList<ExchangeOrder> list = new ArrayList<>();
		while (null != cursor && cursor.moveToNext()) {
			ExchangeOrder orderDetail = new ExchangeOrder();
			orderDetail.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderDetail.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderDetail.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderDetail.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderDetail.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderDetail.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderDetail.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderDetail.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderDetail.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderDetail.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
			orderDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderDetail.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderDetail.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderDetail.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderDetail.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderDetail.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderDetail.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			orderDetail.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			list.add(orderDetail);
		}
		if (null != cursor) cursor.close();
		return list;
	}

	public ArrayList<ExchangeOrder> queryCoinbaseOrderByDate(String status, String  beforeTimeOut, String afterTimeOut) {
		Cursor cursor = db.query(table11, null, "status =? and create_time between ? and ?", new String[]{status,beforeTimeOut,afterTimeOut}, null, null, null, null);
		ArrayList<ExchangeOrder> list = new ArrayList<>();
		while (null != cursor && cursor.moveToNext()) {
			ExchangeOrder orderDetail = new ExchangeOrder();
			orderDetail.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderDetail.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderDetail.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderDetail.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderDetail.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderDetail.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderDetail.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderDetail.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderDetail.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderDetail.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
			orderDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderDetail.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderDetail.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderDetail.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderDetail.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderDetail.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderDetail.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			orderDetail.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			list.add(orderDetail);
		}
		if (null != cursor) cursor.close();
		return list;
	}

	/**
	 * 通过是否上传过上传数据
	 * @param isUpload
	 * @return
	 */
	public ArrayList<ExchangeOrder> queryCoinbaseOrderByIsUpload(String isUpload) {
		Cursor cursor = db.query(table11, null, "is_upload =?", new String[]{isUpload}, null, null, null, null);
		ArrayList<ExchangeOrder> list = new ArrayList<>();
		while (null != cursor && cursor.moveToNext()) {
			ExchangeOrder orderDetail = new ExchangeOrder();
			orderDetail.setId(cursor.getString(cursor.getColumnIndex("id")));
			orderDetail.setTransId(cursor.getString(cursor.getColumnIndex("trans_id")));
			orderDetail.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			orderDetail.setSize(cursor.getString(cursor.getColumnIndex("size")));
			orderDetail.setPrice(cursor.getString(cursor.getColumnIndex("price")));
			orderDetail.setFunds(cursor.getString(cursor.getColumnIndex("funds")));
			orderDetail.setProductId(cursor.getString(cursor.getColumnIndex("product_id")));
			orderDetail.setSide(cursor.getString(cursor.getColumnIndex("side")));
			orderDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			orderDetail.setTimeInForce(cursor.getString(cursor.getColumnIndex("time_in_force")));
			orderDetail.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
			orderDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			orderDetail.setFillFees(cursor.getString(cursor.getColumnIndex("fill_fees")));
			orderDetail.setFilledSize(cursor.getString(cursor.getColumnIndex("filled_size")));
			orderDetail.setExecutedValue(cursor.getString(cursor.getColumnIndex("executed_value")));
			orderDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			orderDetail.setSettled(cursor.getString(cursor.getColumnIndex("settled")));
			orderDetail.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			orderDetail.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
			orderDetail.setCryptoCurrency(cursor.getString(cursor.getColumnIndex("crypto_currency")));
			list.add(orderDetail);
		}
		if (null != cursor) cursor.close();
		return list;
	}

	/**
	 * 更新订单是否上传
	 * @param tranIds
	 * @param isUpload
	 */
	public void updateOrderIsUpload(String tranIds, Integer isUpload) {
		ContentValues values = new ContentValues();
		values.put("is_upload", isUpload);//更新
		int result = db.update(table11, values, "trans_id= ?", new String[]{tranIds});
//		log.info("--------------更新coinbase pro 交易记录" + tranIds + " 发送状态为：" + isUpload + "------------------");
	}

	/**
	 * 更新数据库
	 * @param
	 * @return
	 */
	public boolean updateBuyLogByTransId (BuyLog buyLog){
		if(StringUtils.isBlank(buyLog.getTransId())){
			return  false;
		}
		ContentValues values=new ContentValues();
		values.put("status",buyLog.getStatus() );
		if(StringUtils.isNotBlank(buyLog.getChannelFee())){
			values.put("c_fee",buyLog.getChannelFee() );
		}
		if(StringUtils.isNotBlank(buyLog.getChannelTransId())){
			values.put("channel_trans_id",buyLog.getChannelTransId() );
		}
		if(StringUtils.isNotBlank(buyLog.getRemark())){
			values.put("remark",buyLog.getRemark());
		}
		values.put("is_upload", 0);//更新
		db.update(table5, values, "trans_id= ?", new String[]{buyLog.getTransId()});
		log.info("更新购买记录成功,transId:"+buyLog.getTransId()+",status:"+buyLog.getStatus());
		return  true;
	}

	/**
	 * 更新数据库上传状态为1
	 * @param tranIds
	 * @return
	 */
	public void  updateWithdrawLogIsUpload (boolean isBuy,List<String> tranIds){
		ContentValues values=new ContentValues();
		values.put("is_upload", 1);//更新
		String tableName = isBuy?table5:table6;
		for (String tranId:tranIds) {
			int result =db.update(tableName, values, "trans_id= ?", new String[]{tranId});
		}
//		log.info("--------------上传数据到web端后，更新交易记录 发送状态为：已上传------------------");
	}
    /**
     *运行状态管理
     * @param
     * @param
     */
    public ArrayList<RunStatusManage> queryRunManage(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String status = jsonObject.getString("status");
        String hmn = jsonObject.getString("hardware_modular_name");
        String startTime = jsonObject.getString("start_time");
        String endTime = jsonObject.getString("end_time");
        String condition = "";
        List<String> conditionArray = new ArrayList<>();
        if (StringUtils.isNotEmpty(status)) {
            conditionArray.add(status);
            condition += "status=?";
        }
        Cursor cursor = db.query( table8,  null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, null, null);
        ArrayList<RunStatusManage> list=new ArrayList<RunStatusManage>();
        while (null!=cursor&&cursor.moveToNext()) {
            RunStatusManage rsm = new RunStatusManage();
            rsm.setSerialId(cursor.getString(cursor.getColumnIndex("serial_id")));
            rsm.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
            rsm.setHardwareModularName(cursor.getString(cursor.getColumnIndex("hardware_modular_name")));
            rsm.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            rsm.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));
            list.add(rsm);
        }
        if (null!=cursor) cursor.close();
        return list;
    }

	@SuppressLint("SimpleDateFormat")
	public boolean saveRunManage(List<RunStatusManage> runStatusManages) {
		try {
			for (RunStatusManage runStatusManage : runStatusManages) {
				//判断key如果存在就update，否则insert
				RunStatusManage oldRunStatusManage = queryNameEixst(runStatusManage.getHardwareModularName(),runStatusManage.getTerminalNo());
				if (oldRunStatusManage != null && StringUtils.isNotEmpty(oldRunStatusManage.getHardwareModularName())) {
					//更新
					updateHardwareByName(runStatusManage);
				} else {
					//插入
					JSONObject jsonObject = JSONObject.parseObject(runStatusManage.toString());
					ContentValues values = new ContentValues();
					Set<String> keySet = jsonObject.keySet();
					for (String key : keySet) {
						values.put(key, jsonObject.getString(key));
					}
					db.insert(table8, null, values);
					log.info("[DBHelper] ： RunStatusManage 新增name:" + runStatusManage.getHardwareModularName() + ",添加数据库记录完成");
				}
			}
			return true;
		} catch (Exception e) {
			log.info("[DBHelper] ：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 查询hardware记录
	 *
	 * @param name
	 */
	public RunStatusManage queryNameEixst(String name, String terminalNo) {
		Cursor cursor = db.query(table8, null, "hardware_modular_name=? and terminal_no = ? ", new String[]{name, terminalNo}, null, null, null, null);
		RunStatusManage runStatusManage = new RunStatusManage();
		while (null != cursor && cursor.moveToNext()) {
			runStatusManage.setSerialId(cursor.getString(cursor.getColumnIndex("serial_id")));
			runStatusManage.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
			runStatusManage.setHardwareModularName(cursor.getString(cursor.getColumnIndex("hardware_modular_name")));
			runStatusManage.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			runStatusManage.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));
		}
		return runStatusManage;
	}

	/**
	 * 根据name 更新status
	 *
	 * @param runStatusManage
	 */
	public void updateHardwareByName(RunStatusManage runStatusManage) {
		ContentValues values = new ContentValues();
		values.put("status", runStatusManage.getStatus());//更新
		values.put("update_time", runStatusManage.getUpdateTime());//更新
		int result = db.update(table8, values, "hardware_modular_name= ?", new String[]{runStatusManage.getHardwareModularName()});
		log.info("[DBHelper] ： RunStatusManage 更新name:" + runStatusManage.getHardwareModularName() + ",添加数据库记录" + (result > 0 ? "成功" : "失败"));
	}

    /**
     *广告列表
     * @param
     * @param
     */
    public ArrayList<Advert> queryAdvert(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String startTime = jsonObject.getString("start_time");
        String endTime = jsonObject.getString("end_time");
        String condition = "";
        List<String> conditionArray = new ArrayList<>();
        if (!("".equals(startTime) && "".equals(endTime))) {
            conditionArray.add(startTime);
            conditionArray.add(endTime);
            condition += "upload_time between ? and ?";
        }

        Cursor cursor = db.query( table9, null, condition,conditionArray.toArray(new String[conditionArray.size()]), null, null, null, null);
        ArrayList<Advert> list=new ArrayList<Advert>();
        while (null!=cursor&&cursor.moveToNext()) {
            Advert advert = new Advert();
            advert.setSerialId(cursor.getString(cursor.getColumnIndex("serial_id")));
            advert.setTerminalNo(cursor.getString(cursor.getColumnIndex("terminal_no")));
            advert.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            advert.setTermOfValidity(cursor.getString(cursor.getColumnIndex("term_of_validity")));
            advert.setUploadTime(cursor.getString(cursor.getColumnIndex("upload_time")));
            list.add(advert);
        }
        if (null!=cursor) cursor.close();
        return list;
    }

    // 加俩小时后的时间
	private String formatDate2Hours(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, -2);
		Date newDate = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(newDate);
	}

	/**
	 * 添加清机记录
	 * @param
	 * @param
	 */

	public void addEmptyNotes(ContentValues values) {
		try {
			db.insert(table14, null, values);
			log.info("[DBHelper] ： 添加清机记录成功");
		}catch (Exception e){
			log.error(e);
		}

	}
	/**
	 *查询清机表
	 * @param
	 * @param
	 */
	public ArrayList<EmpytNotes> queryEmptyNotesList(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and create_time between ? and ?";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and create_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and create_time < ?";
				}
			}
		}
		Cursor cursor = db.query( table14, null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, "create_time desc", null);
		ArrayList<EmpytNotes> list=new ArrayList<EmpytNotes>();
		while (null!=cursor&&cursor.moveToNext()) {
			EmpytNotes empytNotes = new EmpytNotes();
			empytNotes.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			empytNotes.setAddCash(cursor.getString(cursor.getColumnIndex("add_cash")));
			empytNotes.setBuyCash(cursor.getString(cursor.getColumnIndex("buy_cash")));
			empytNotes.setSellCash(cursor.getString(cursor.getColumnIndex("sell_cash")));
			empytNotes.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			list.add(empytNotes);
		}
		if (null!=cursor) cursor.close();
		return list;
	}

    /**
     * 添加加钞记录
     * @param
     * @param
     */
    @SuppressLint("SimpleDateFormat")
    public void insertAddnotes(ContentValues values) {
        db.insert(table13, null, values);
        log.info("[DBHelper] ： 添加加钞记录成功");
    }

	/**
	 *查询加钞表
	 * @param
	 * @param
	 */
	public ArrayList<AddNotes> queryAddNotesList(String json) {
		String condition = " 1 = 1";
		List<String> conditionArray = new ArrayList<>();
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String startTime = jsonObject.getString("start_time");
			String endTime = jsonObject.getString("end_time");
			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
				conditionArray.add(startTime);
				conditionArray.add(endTime);
				condition += " and create_time between ? and ?";
			} else {
				if (StringUtils.isNotEmpty(startTime)) {
					conditionArray.add(startTime);
					condition += " and create_time > ?";
				}
				if (StringUtils.isNotEmpty(endTime)) {
					conditionArray.add(endTime);
					condition += " and create_time < ?";
				}
			}
		}
		Cursor cursor = db.query( table13, null, condition,conditionArray.toArray(new String[conditionArray.size()]) , null, null, "create_time desc", null);
		ArrayList<AddNotes> list=new ArrayList<AddNotes>();
		while (null!=cursor&&cursor.moveToNext()) {
			AddNotes addNotes = new AddNotes();
			addNotes.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			addNotes.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
			addNotes.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
			list.add(addNotes);
		}
		if (null!=cursor) cursor.close();
		return list;
	}


	/**
	 * map 取值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	private String getMapValue(Map map, String key) {
		return map.containsKey(key) ? map.get(key).toString() : "";
	}

	/**
	 * 格式化日期
	 *
	 * @param oldDate
	 * @return
	 */
	private String TzFormat(String oldDate) {
		if (StringUtils.isEmpty(oldDate)) {
			return "";
		}
		Date date1 = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = df.parse(oldDate);
		} catch (ParseException e) {
			log.error("TzFormat failed:", e);
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	private String getDateFormat() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}


}
