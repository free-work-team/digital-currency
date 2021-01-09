package com.jyt.terminal.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 工具类
 * @author JYT_Zengcong
 * @date 2017-8-28
 */
public class Utils {

	
	/**金额为分的格式 */    
    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";    
    
	public static String getRequestParamString(Map<String, String> requestParam, String coder) {
		if (null == coder || "".equals(coder)) {
			coder = "UTF-8";
		}
		StringBuffer sf = new StringBuffer("");
		String reqstr = "";
		if (null != requestParam && 0 != requestParam.size()) {
			for (Entry<String, String> en : requestParam.entrySet()) {
				try {
					sf.append(en.getKey()
							+ "="
							+ (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder.encode(en.getValue(), coder)) + "&");
				} catch (UnsupportedEncodingException e) {
					
					return "";
				}
			}
			reqstr = sf.substring(0, sf.length() - 1);
		}
		
		return reqstr;
	}
	
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) != null || !"".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.put(en, value);
				}
			}
		}
		return res;
	}
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getAllRequestParams(HttpServletRequest request) {
		Map<String, Object> res = new HashMap<String, Object>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) != null || !"".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.put(en, value);
				}
			}
		}
		return res;
	}
	
	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            需要解析的字符串
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str){

		Map<String, String> map = new HashMap<String, String>();
	try{
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//值里有嵌套
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
				curChar = str.charAt(i);// 取当前字符
				if (isKey) {// 如果当前生成的是key
					
					if (curChar == '=') {// 如果读取到=分隔符 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// 如果当前生成的是value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//如果没开启嵌套
						if(curChar == '{'){//如果碰到，就开启嵌套
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					}else{
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
		return map;
	}
	
	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map) {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}
	
	
	/**
	 * 功能：前台交易构造HTTP POST自动提交表单<br>
	 * @param action 表单提交地址<br>
	 * @param hiddens 以MAP形式存储的表单键值<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return 构造好的HTTP POST交易表单<br>
	 */
	public static String createAutoFormHtml(String reqUrl, Map<String, Object> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, Object>> set = hiddens.entrySet();
			Iterator<Entry<String, Object>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, Object> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue().toString();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}
	
	 /**
  	 * 功能：前台交易构造HTTP POST自动提交表单<br>
  	 * @param action 表单提交地址<br>
  	 * @param hiddens 以MAP形式存储的表单键值<br>
  	 * @param encoding 上送请求报文域encoding字段的值<br>
  	 * @return 构造好的HTTP POST交易表单<br>
  	 */
  	public static String createAutoFormHtml2(String reqUrl, Map<String, String> hiddens,String encoding) {
  		StringBuffer sf = new StringBuffer();
  		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
  		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
  				+ "\" method=\"post\">");
  		if (null != hiddens && 0 != hiddens.size()) {
  			Set<Entry<String, String>> set = hiddens.entrySet();
  			Iterator<Entry<String, String>> it = set.iterator();
  			while (it.hasNext()) {
  				Entry<String, String> ey = it.next();
  				String key = ey.getKey();
  				String value = ey.getValue();
  				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
  						+ key + "\" value=\"" + value + "\"/>");
  			}
  		}
  		sf.append("</form>");
  		sf.append("</body>");
  		sf.append("<script type=\"text/javascript\">");
  		sf.append("document.all.pay_form.submit();");
  		sf.append("</script>");
  		sf.append("</html>");
  		return sf.toString();
  	}
  	
	   
	   /** 
	     * 将分为单位的转换为元 （除100） 
	     *  
	     * @param amount 
	     * @return 
	     * @throws Exception  
	     */  
	    public static String changeF2Y(String amount) throws Exception{  
	        if(!amount.matches(CURRENCY_FEN_REGEX)) {  
	            throw new Exception("金额格式有误");  
	        }   
	        DecimalFormat df = new DecimalFormat("0.00");
	        df.setRoundingMode(RoundingMode.HALF_UP);
	        return df.format(BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)));
	    }  
	      
	    /**   
	     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额  
	     *   
	     * @param amount  
	     * @return  
	     */    
	    public static String changeY2F(String amount){    
	        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
	        int index = currency.indexOf(".");    
	        int length = currency.length();    
	        Long amLong = 0l;    
	        if(index == -1){    
	            amLong = Long.valueOf(currency+"00");    
	        }else if(length - index >= 3){    
	            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));    
	        }else if(length - index == 2){    
	            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
	        }else{    
	            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
	        }    
	        return amLong.toString();    
	    }    
	   
}
