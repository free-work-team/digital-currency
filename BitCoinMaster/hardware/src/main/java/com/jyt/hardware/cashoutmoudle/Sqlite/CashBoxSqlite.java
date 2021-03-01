package com.jyt.hardware.cashoutmoudle.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import org.apache.log4j.Logger;


public class CashBoxSqlite extends SQLiteOpenHelper {
	private static String DBName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/HungHui/CashBox";
	private static Logger log = Logger.getLogger("BitCoinMaster");
//	private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/HungHui/CashBox";
	//private String table2="cashbox";
	private static int DBVersion = 5;//当前数据库版本
//	private String create_table1="create table log  (_id integer  primary key,type integer,log varchar,date varchar,time varchar)";
//	private String create_table2="create table cashbox  (_id  integer  primary key,boxid varchar,denomination integer,solt integer,remaining_number integer, send_number integer,all_number integer,other1 varchar,other2 integer)";
																					//钞箱id          面额                          槽位               剩余数                                      出钞总数                     总钞票数量                                            回收钞箱数量
	//private String create_table3="create table client_log(_id integer primary key,ucLoglevel integer,format varchar,date varchar,time varchar)";
	//private String create_table4="create table boardkey(_id integer primary key, key varchar)";
	private String create_table5= "create table buy_log(_id integer primary key,trans_id varchar,terminal_no varchar,price varchar,strategy varchar,address varchar,amount varchar,currency varchar,fee varchar,c_fee varchar,cash varchar,ext_id varchar,trans_time varchar,remark varchar,is_upload integer,status varchar,channel varchar,channel_trans_id varchar,exchange_strategy integer,customer_id varchar,exchange_rate varchar,crypto_currency varchar)";
	private String create_table6 = "create table withdraw_log(_id integer primary key,terminal_no varchar,price varchar,strategy varchar,target_address varchar,amount varchar,currency varchar,fee varchar,c_fee varchar,cash varchar,trans_id varchar,ext_id varchar,trans_time varchar,trans_status integer,redeem_status integer,outCount integer,remark varchar,is_upload integer,channel varchar,sell_type varchar,redeem_time varchar,channel_trans_id varchar,exchange_strategy integer,confirm_status integer,customer_id varchar,exchange_rate varchar,crypto_currency varchar,address_id varchar)";
	private String create_table7 = "create table user(_id integer primary key,account varchar,password varchar,name varchar)";
	private String create_table8 = "create table hardware(_id integer primary key,serial_id varchar,terminal_no varchar,hardware_modular_name varchar,status varchar,update_time varchar)";
	private String create_table9 = "create table advert(_id integer primary key,serial_id varchar,terminal_no varchar,address varchar,term_of_validity varchar,upload_time varchar)";
	private String create_table10 = "create table param_setting(_id varchar primary key,web_address varchar,terminal_no varchar,password varchar,merchant_name varchar,hotline varchar,e_mail varchar,buy_transaction_fee varchar,sell_transaction_fee varchar,buy_single_fee varchar,sell_single_fee varchar,rate varchar,channel_param varchar,min_need_bitcoin varchar,min_need_cash varchar,sell_type varchar,currency varchar,online varchar,channel varchar,hot_wallet varchar,limit_cash varchar,kyc_url varchar,exchange_strategy integer,currency_pair varchar,order_type varchar,rate_source integer,kyc_enable integer,crypto_settings varchar,way integer)";
	private String create_table11 = "create table coinbase_order(trans_id varchar primary key,terminal_no varchar,id varchar,price varchar,size varchar,funds varchar,product_id varchar,side varchar,type varchar,time_in_force varchar,created_at varchar,create_time varchar,fill_fees varchar,filled_size varchar,executed_value varchar,status varchar,settled varchar ,message varchar,is_upload integer,crypto_currency varchar,currency varchar)";
	private String create_table12 = "create table hardware_config(hw_key varchar primary key,hw_value varchar,create_time varchar)";
	private String create_table13 = "create table add_notes(_id integer primary key ,amount varchar,create_time varchar)";
	private String create_table14 = "create table empty_notes(_id integer primary key ,add_cash varchar,buy_cash varchar,sell_cash varchar,create_time varchar)";
	private String create_table15 = "create table transfer_log(_id integer primary key,trans_id varchar,tx_id varchar,terminal_no varchar,amount varchar,funds varchar,price varchar,fee varchar,type varchar,wallet integer,exchange integer,address varchar,is_upload integer,status varchar,create_time varchar,update_time varchar,refid varchar,crypto_currency varchar)";

	public CashBoxSqlite(Context context) {
		super(context, DBName, null, DBVersion);

	}

//	public SQLiteDatabase openDatabase(){
//
//		SQLiteDatabase database=SQLiteDatabase.openOrCreateDatabase(path+DBName,null);
//		return database;
//	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		//arg0.execSQL(create_table1);
		//arg0.execSQL(create_table2);
		//arg0.execSQL(create_table3);
		//arg0.execSQL(create_table4);
		arg0.execSQL(create_table5);
		arg0.execSQL(create_table6);
		arg0.execSQL(create_table7);
		arg0.execSQL(create_table8);
		arg0.execSQL(create_table9);
		arg0.execSQL(create_table10);
		arg0.execSQL(create_table11);
		arg0.execSQL(create_table12);
		arg0.execSQL(create_table13);
		arg0.execSQL(create_table14);
		arg0.execSQL(create_table15);
	/*	ContentValues values1=new ContentValues();
	 
		values1.put("boxid", "0");
		values1.put("denomination", 100);
		values1.put("solt", 0);
		values1.put("remaining_number", 0);
		values1.put("send_number", 0);
		values1.put("all_number", 0);
		arg0.insert(table2, null, values1);*/
		
		/*ContentValues values2=new ContentValues();
	 
		values2.put("boxid", "1");
		values2.put("denomination", 50);
		values2.put("solt", 2);
		values2.put("remaining_number", 0);
		values2.put("send_number", 0);
		values2.put("all_number", 0);
		arg0.insert(table2, null, values2);*/
		
		ContentValues values5=new ContentValues();
		values5.put("account", "admin");
		values5.put("password", "2788@5126");
		arg0.insert("user", null, values5);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
		log.info("数据库旧版本"+oldVersion+"=======数据库新版本"+ newVersion);
		switch (oldVersion) {
                case 2:
                    arg0.execSQL("alter table param_setting add column exchange_strategy integer");// 钱包策略
                    arg0.execSQL("alter table buy_log add column exchange_strategy integer");// 钱包策略
                    arg0.execSQL("alter table withdraw_log add column exchange_strategy integer");// 钱包策略
                    arg0.execSQL("alter table withdraw_log add column confirm_status integer");//确认状态
                    arg0.execSQL("alter table withdraw_log add column channel_trans_id varchar");// 上游渠道交易id
                    arg0.execSQL("alter table buy_log add column channel_trans_id varchar");// 上游渠道交易id
                    // 钱包交易所直接转币记录
                    String create_table15 = "create table transfer_log(_id integer primary key,trans_id varchar,tx_id varchar,terminal_no varchar,amount varchar,funds varchar,price varchar,fee varchar,type varchar,wallet integer,exchange integer,address varchar,is_upload integer,status varchar,create_time varchar,update_time varchar,refid varchar)";
                    arg0.execSQL(create_table15);//确认状态
                    arg0.execSQL("alter table param_setting add column rate_source integer");// 汇率来源
                    arg0.execSQL("alter table param_setting add column kyc_enable integer");// 是否开启kyc
                    arg0.execSQL("alter table withdraw_log add column customer_id varchar");// 联系方式
                    arg0.execSQL("alter table buy_log add column customer_id varchar");// 联系方式
					arg0.execSQL("alter table withdraw_log add column exchange_rate varchar");// 交易所币种价格
					arg0.execSQL("alter table buy_log add column exchange_rate varchar");// 交易所币种价格
			    case 3:
				//后续用
					arg0.execSQL("alter table param_setting add column crypto_settings varchar");// 加密币种参数
					arg0.execSQL("alter table buy_log add column crypto_currency varchar");// 加密币种类型
					arg0.execSQL("alter table withdraw_log add column crypto_currency varchar");// 加密币种类型
					arg0.execSQL("alter table withdraw_log add column address_id varchar");//
					arg0.execSQL("alter table coinbase_order add column crypto_currency varchar");// 加密币种类型
					arg0.execSQL("alter table transfer_log add column crypto_currency varchar");// 加密币种类型
                    arg0.execSQL("alter table coinbase_order add column currency varchar");// 交易法币币种
				case 4:
					arg0.execSQL("alter table param_setting add column way integer");// 单双向参数
            default:
		}
		log.info("数据库更新完成");

	}
}
