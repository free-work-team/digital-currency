package com.jyt.terminal.commom.enums;


public class SmsEnum {

	/**
	 * 短信状态
	 * @author tfq
	 * @date 2020-1-1
	 */
	public enum SmsStatus {
		SUCCESS(1, "成功","success"),
		FAILURE(2, "失败","Failure"),
		SENDING(3, "发送中","Sending");
		private int code;
		private String cndesc;
		private String endesc;

		SmsStatus(int value, String cndesc,String endesc) {
			this.code = value;
			this.cndesc = cndesc;
			this.endesc = endesc;
		}

		
		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
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
	        for (SmsStatus d : SmsStatus.values()) {  
	            if (d.getCode()==key) {  
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
