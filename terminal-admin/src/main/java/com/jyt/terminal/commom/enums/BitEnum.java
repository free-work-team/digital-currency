package com.jyt.terminal.commom.enums;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings({ "rawtypes", "unchecked" })
public class BitEnum {
	private static Logger log = LoggerFactory.getLogger(BitEnum.class);
	public static final Map<String, TreeMap<Object, Object>> HRU_ENUM_MAP = new TreeMap<>();
	static {
		for (Class clazz : BitEnum.class.getDeclaredClasses()) {
			TreeMap<Object, Object> map = new TreeMap<>();
			Object[] objs = clazz.getEnumConstants();
			try {
				Method value = clazz.getDeclaredMethod("getValue");
				Method desc = clazz.getDeclaredMethod("getDesc");
				for (Object obj : objs) {
					map.put(value.invoke(obj), desc.invoke(obj));
				}
				HRU_ENUM_MAP.put(clazz.getSimpleName(), map);
			} catch (Exception e) {
				log.warn("初始化枚举出错", e);
			}

		}
	}

	/**
	 * 设备硬件状态
	 * @author Administrator
	 */
	public enum DeviceStatus {
		NORMAL(1, "正常"), ABNORMAL(-1, "异常");
		private int value;
		private String desc;

		DeviceStatus(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public static String valueOf(Integer code) {
	        if (code == null) {
	            return "";
	        }    
            for (DeviceStatus s : DeviceStatus.values()) {
                if (s.getValue() == code) {
                    return s.getDesc();
                }
            }
            return "";
	    }

	}
	

	/**
	 * 用户类型
	 * @author Administrator
	 */
	public enum UserType {
		ADMIN(1, "管理平台"),TERMINAL(2, "终端用户");
		private int value;
		private String desc;

		UserType(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	
	/**
	 * 用户状态
	 * @author Administrator
	 */
	public enum UserStatus {
		ENABLE(1, "启用","Enable"),
		DISABLE(2, "冻结","Freeze"),
		DELETE(3, "已删除","Deleted"),
		FREEZE(4,"冻结","Freeze");
		private int value;
		private String cndesc;
		private String endesc;

		UserStatus(int value, String cndesc,String endesc) {
			this.value = value;
			this.cndesc = cndesc;
			this.endesc = endesc;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getCndesc() {
			return cndesc;
		}

		public void setCndesc(String cndesc) {
			this.cndesc = cndesc;
		}

		public String getEndesc() {
			return endesc;
		}

		public void setEndesc(String endesc) {
			this.endesc = endesc;
		}
		public static String getName(String type,int key) {  
	        for (UserStatus d : UserStatus.values()) {  
	            if (d.getValue()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getCndesc();
	                else
	                    return  d.getEndesc();
	            }  
	        }  
	        return null; 
	    }

	}
	
	/**
	 * 角色状态
	 * @author Administrator
	 */
	public enum RoleStatus {
		EFFECTIVE(1,"启用", "enable"),
		INVALID(2, "禁用", "disable"),
		;
		private int code;
		private String v1;
		private String v2;

		RoleStatus(int code,String v1, String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		public static String getName(String type,int key) {  
	        for (RoleStatus d : RoleStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }

	}
	
	/**
	 * 比特币卖出状态(赎回)
	 * @author Administrator
	 */
	public enum TradeStatus {
		
		INIT(0 , "初始化" , "init"),
        PENDING(1 , "处理中", "pending"),
        CONFIRM(2 , "已确认", "confirmed"),
        FAIL(3 , "失败" , "fail"),
        Error(4 , "异常" , "error")
		;
		private int code;
		private String v1;
		private String v2;

		TradeStatus(int code, String v1 ,String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}
		public static String getName(String type,int key) {  
	        for (TradeStatus d : TradeStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }

	}
	/**
	 * 比特币买入状态
	 * @author Administrator
	 */
	/*public enum BuyStatus {
		CREATE(0, "初始化","init"),
		CONFIRM(1,"确认", "confirmed"),
		FAIL(2,"失败", "fail")
		;
		private int code;
		private String v1;
		private String v2;

		BuyStatus(int code, String v1 ,String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}
		public static String getName(String type,int key) {  
	        for (BuyStatus d : BuyStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }

	}*/
	/**
	 * 订单交易状态
	 * @author Administrator
	 */
	public enum OrderStatus {
		CREATE(0, "create"),   //创建
		PENDING(1, "pending"),   //处理中
		CONFIRM(2, "confirmed"),   //已确认
		FAIL(3, "fail"),    //失败
		CANCEL(4, "cancel"),    //取消
		;
		private Integer value;
		private String desc;
		
		OrderStatus(Integer value, String desc) {
			this.value = value;
			this.desc = desc;
		}
		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public static String valueOf(Integer code) {
			if (code == null) {
				return "";
			}    
			for (OrderStatus o : OrderStatus.values()) {
				if (o.getValue() == code ) {
					return o.getDesc();
				}
			}
			return "";
		}
		
	}
	
	/**
	 * 比特币出钞状态
	 * @author Administrator
	 */
	public enum RedeemStatus {
		YES(1,"已出钞", "yes"),
		NO(0,"未出钞", "no"),
		;
		private int code;
		private String v1;
		private String v2;

		RedeemStatus(int code, String v1,String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		public static String getName(String type,int key) {  
	        for (RedeemStatus d : RedeemStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }
		public static String getNameByStr(String type,String key) {  
	        for (RedeemStatus d : RedeemStatus.values()) {  
	            if (d.getV2() ==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }

	}
	
	/**
	 * 热钱包
	 */
	public enum HotWallet{
		BITGO(1,"Bitgo"),COINBASE(2,"Coinbase"),BLOCKCHAIN(3,"Blockchain");
		
		private int code;
		private String message;
		
		HotWallet(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (HotWallet s : HotWallet.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
		
		
	}
	/**
	 *交易所
	 */
	public enum Exchange{
		NO(0,"NO"),PRO(1,"Coinbase Pro"),KRAKEN(2,"Kraken");
		
		private int code;
		private String message;
		
		Exchange(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (Exchange s : Exchange.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
		
		
	}
	/**
	 *交易策略
	 */
	public enum ExchangeStrategy{
		STRATEGY0(0,"0. 不使用交易所，从热钱包发送和接收币。","0 - No exchange,Send and receive coins from Hot Wallet."),
		STRATEGY1(1,"1. 热钱包预存币，从热钱包发送和接收币，然后在交易所买卖币，如果购买成功，把这些币发送到热钱包。","1 - Hot Wallet pre-stores coins,Send and receive coins from Hot Wallet,After that,trade coins on the exchange and if the purchase is successful,send this coins to the Hot Wallet."),
		STRATEGY2(2,"2. 热钱包不预存币，直接在交易所买卖币。","2 - Hot Wallet does not pre-store coins,trade coins directly on the exchange.");
		
		private int code;
		private String v1;
		private String v2;
		
		ExchangeStrategy(int code,String v1,String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getV1() {
			return v1;
		}
		public void setV1(String v1) {
			this.v1 = v1;
		}
		public String getV2() {
			return v2;
		}
		public void setV2(String v2) {
			this.v2 = v2;
		}
		public static String getName(String type,int key) {  
	        for (ExchangeStrategy d : ExchangeStrategy.values()) {  
	            if (d.getCode() == key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return d.getV2();
	            }  
	        }  
	        return null; 
	    }
	}
	
	public enum IsSend{
		YES(1,"已发送"),NO(0,"未发送");
		
		private int code;
		private String message;
		
		IsSend(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (IsSend s : IsSend.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
		
		
	}
	
	/**
	 * 币种
	 *
	 */
	public enum Currency{
		USD(1,"USD"),
		EUR(2,"EUR"),
		CNY(3,"CNY"),
		HKD(4,"HKD"),
		TWD(5,"TWD"),
		GBP(6,"GBP"),
		AUD(7,"AUD"),
		KRW(8,"KRW"),
		JPY(9,"JPY"),
		CAD(10,"CAD")
		;
		
		private int code;
		private String message;
		
		Currency(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (Currency s : Currency.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
	}
	
	/**
	 * 赎回方式
	 *
	 */
	public enum SellType{
		SIX_CONF(6,"6-conf"),//6确认
		FIVE_CONF(5,"5-conf"),
		FOUR_CONF(4,"4-conf"),
		THREE_CONF(3,"3-conf"),
		TWO_CONF(2,"2-conf"),
		ONE_CONF(1,"1-conf"),
		//ZERO_CONF(0,"0-conf")
		;
		
		private int code;
		private String message;
		
		SellType(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (SellType s : SellType.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
	}
	/**
	 * 交易类型
	 *
	 */
	public enum TradeType{
		BUY(1,"buy"),SELL(2,"sell");
		
		private int code;
		private String message;
		
		TradeType(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (TradeType s : TradeType.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
		
		
	}

	/**
	 * 证件类型
	 *
	 */
	public enum CardType{
		IDCard(1,"ID Card"),Passport(2,"Passport");

		private int code;
		private String message;

		CardType(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }
            for (CardType s : CardType.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
	}
	
	/**
	 * 
	 * @className CustomerStatus
	 * @author wangwei
	 * @date 2019年8月28日
	 *
	 */
	public enum CustomerStatus{
		AUDIT(0,"待审核", "Pending"),
		AUDIT_PASSED(1,"通过", "Success"),
		AUDIT_NOPASS(2,"不通过", "Fail");
		
		private int code;
		private String v1;
		private String v2;
		
		CustomerStatus(int code, String v1 ,String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		public static String getName(String type,int key) {  
	        for (CustomerStatus d : CustomerStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }
		
	}
	
	/**
	 * 钞箱状态
	 */
	public enum CashBoxStatus {
		TAKE_OUT(0,"拿出", "Removed"),
		PUT_IN(1, "替换", "Replaced");
		private int code;
		private String v1;
		private String v2;

		CashBoxStatus(int code,String v1, String v2) {
			this.code = code;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		public static String getName(String type,int key) {  
	        for (CashBoxStatus d : CashBoxStatus.values()) {  
	            if (d.getCode()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getV1();
	                else
	                    return  d.getV2();
	            }  
	        }  
	        return null; 
	    }

	}
	
	/**
	 * 终端参数设置状态
	 */
	public enum TerminalSettingStatus {
		ENABLE(1, "启用"), DISABLE(2, "冻结"),DELATE(3,"删除");
		private int code;
		private String message;

		TerminalSettingStatus(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (TerminalSettingStatus s : TerminalSettingStatus.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }

	}
	
	/**
	 * 订单类型
	 */
	public enum OrderType {
		MARKET(1, "market"), LIMIT(2, "limit");
		private int code;
		private String message;

		OrderType(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (OrderType s : OrderType.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
	}
	
	/**
	 * 交易对
	 */
	public enum CurrencyPair {
		BTCUSD(1, "BTC-USD"),
		BTCEUR(2, "BTC-EUR"),
		BTCGBP(3, "BTC-GBP"),
		//ETH
//		ETHBTC(4, "ETH-BTC"),
	    ETHUSD(5, "ETH-USD"),
	    ETHEUR(6, "ETH-EUR"),
	    ETHGBP(7, "ETH-GBP")
		;
		private int code;
		private String message;

		CurrencyPair(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (CurrencyPair s : CurrencyPair.values()) {
                if (s.getCode() == value) {
                    return s.getMessage();
                }
            }
            return "";
	    }
	}
	
	/**
	 * 币汇率来源
	 */
	public enum RateSourceEnum {
		COINBASE(1, "Coinbase");

        private int value;
        private String desc;

        RateSourceEnum(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (RateSourceEnum s : RateSourceEnum.values()) {
                if (s.getValue() == value) {
                    return s.getDesc();
                }
            }
            return "";
	    }
	}
	/**
	 * 是否开启KYC
	 */
	public enum KycIsEnable {
		TRUE(1, "true"),
        FALSE(0, "false");

        private int value;
        private String desc;

        KycIsEnable(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public static String valueOf(Integer value) {
	        if (value == null) {
	            return "";
	        }    
            for (KycIsEnable s : KycIsEnable.values()) {
                if (s.getValue() == value) {
                    return s.getDesc();
                }
            }
            return "";
	    }
	}
	/**
	 * 虚拟币
	 */
	public enum VirtualCurrencyEnum{
		BTC(1,"btc","btc"),//比特币
        ETH(2,"eth","eth");//以太坊
		
		private int value;
        private String cndesc;
        private String endesc;

        VirtualCurrencyEnum(int value, String cndesc,String endesc) {
            this.value = value;
            this.cndesc = cndesc;
            this.endesc = endesc;
        }
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getCndesc() {
			return cndesc;
		}
		public void setCndesc(String cndesc) {
			this.cndesc = cndesc;
		}
		public String getEndesc() {
			return endesc;
		}
		public void setEndesc(String endesc) {
			this.endesc = endesc;
		}
		public static String getName(String type,int key) {  
	        for (VirtualCurrencyEnum d : VirtualCurrencyEnum.values()) {  
	            if (d.getValue()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getCndesc();
	                else
	                    return  d.getEndesc();
	            }  
	        }  
	        return null; 
	    }
	}
	/**
	 * 加密设置状态
	 */
	public enum CryptoSettingsStatusEnum{
		ENABLE(1,"启用","enable"),
		DISABLE(2,"禁用","disable");
		
		private int value;
        private String cndesc;
        private String endesc;

        CryptoSettingsStatusEnum(int value, String cndesc,String endesc) {
            this.value = value;
            this.cndesc = cndesc;
            this.endesc = endesc;
        }
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getCndesc() {
			return cndesc;
		}
		public void setCndesc(String cndesc) {
			this.cndesc = cndesc;
		}
		public String getEndesc() {
			return endesc;
		}
		public void setEndesc(String endesc) {
			this.endesc = endesc;
		}
		public static String getName(String type,int key) {  
	        for (CryptoSettingsStatusEnum d : CryptoSettingsStatusEnum.values()) {  
	            if (d.getValue()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getCndesc();
	                else
	                    return  d.getEndesc();
	            }  
	        }  
	        return null; 
	    }
	}
	
	/**
	 * 终端机支持双向(存取)还是单向(接收)设置状态
	 */
	public enum WayEnum{
		ONE(1,"单向","One Way"),
		TWO(2,"双向","Two Way");
		
		private int value;
        private String cndesc;
        private String endesc;

        WayEnum(int value, String cndesc,String endesc) {
            this.value = value;
            this.cndesc = cndesc;
            this.endesc = endesc;
        }
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getCndesc() {
			return cndesc;
		}
		public void setCndesc(String cndesc) {
			this.cndesc = cndesc;
		}
		public String getEndesc() {
			return endesc;
		}
		public void setEndesc(String endesc) {
			this.endesc = endesc;
		}
		public static String getName(String type,int key) {  
	        for (WayEnum d : WayEnum.values()) {  
	            if (d.getValue()==key) {  
	                if(type.equals("lanChinese"))//中文
	                	return d.getCndesc();
	                else
	                    return  d.getEndesc();
	            }  
	        }  
	        return null; 
	    }
	}
}
