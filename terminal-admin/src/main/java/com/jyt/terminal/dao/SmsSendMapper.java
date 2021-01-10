package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.model.SmsSend;


/**
 * <p>
 * 短信发送表 Mapper 接口
 * </p>
 *
 * @author tangfq
 * @since 2021-01-11
 */
public interface SmsSendMapper extends BaseMapper<SmsSend> {

	List<Map<String, Object>> getSMSList(@Param("page")Page<Map<String, Object>> page,@Param("sms")SmsSend querySMSDTO);
	
	/**
     * 修改短信记录
     */
	int setStatus(@Param("entity") SmsSend querySMSDTO);
	
	/**
     * 根据订单号查询短信记录
     */
	SmsSend getByOrderNo(@Param("orderNo") String orderNo);
}
