package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.model.Advert;

/**
 * 广告
 *
 * @author zengcong
 * @Date 2018/12/24 13:58
 */
public class AdvertResponse extends BaseResponse{
 
  
    private String advertTitle;
    
    private String advertContent;
    

	public AdvertResponse(Integer code,String message,Advert advert) {
    	this.setCode(code);
    	this.setMessage(message);
    	
    	if(advert!=null){
    		this.setAdvertTitle(advert.getAdvertTitle());
    		this.setAdvertContent(advert.getAdvertContent());
    	}
    }

	public String getAdvertTitle() {
		return advertTitle;
	}

	public void setAdvertTitle(String advertTitle) {
		this.advertTitle = advertTitle;
	}

	public String getAdvertContent() {
		return advertContent;
	}

	public void setAdvertContent(String advertContent) {
		this.advertContent = advertContent;
	}
	
}
