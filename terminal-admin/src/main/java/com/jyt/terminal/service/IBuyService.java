package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.BuyDTO;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.model.Withdraw;


/**
 * <p>
 * 比特币购买表 服务类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface IBuyService extends IService<Buy> {
	
	public List<Map<String,Object>> getBuyList(Page<Buy> page,BuyDTO buyDTO);
	
	public Map<String,Object> getBuyDetail(Integer id);
	
	/**
	 * 购买流水汇总（定时任务每日0点统计前一天数据）
	 */
	List<TradeStatistics> getBuyStatisticsList();
	
	/**
	 * 上传买入记录
	 */
	public boolean inserOrUpdateBuy(List<Buy> buyList);

}
