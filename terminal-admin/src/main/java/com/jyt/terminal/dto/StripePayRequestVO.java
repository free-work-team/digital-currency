package com.jyt.terminal.dto;

import java.io.Serializable;
import com.baomidou.mybatisplus.activerecord.Model;

public class StripePayRequestVO extends Model<StripePayRequestVO> {

	
	 /**
     * 支付类型
     */
    private String type;
 
    /**
     * Stripe支付token
     */
    private String token;
 
    /**
     * 用户ID
     */
    private String userId;
 
    /**
     * 套餐ID
     */
    private String productId;
 
 
    private String scene;
 
   
 
    public String getScene() {
        return scene;
    }
 
    public void setScene(String scene) {
        this.scene = scene;
    }
 
 
    public String getUserId() {
        return userId;
    }
 
    public void setUserId(String userId) {
        this.userId = userId;
    }
 
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }
 
    public String getProductId() {
        return productId;
    }
 
    public void setProductId(String productId) {
        this.productId = productId;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	protected Serializable pkVal() {
		// TODO Auto-generated method stub
		return this.userId;
	}
    
    
    
}
