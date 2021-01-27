/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.service;

import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.model.SmsSend;

/**
 * 此处应有类说明<br/>
 *
 * @author tangfq
 * @Date 2021年1月17日 下午12:02:59
 * @since jdk 1.8
 *  
 */
public interface IOcrService{

	/**
	 * 调用华为云OCR识别
	 */
	public String callOcr(int certificateType,String path);
	
}
