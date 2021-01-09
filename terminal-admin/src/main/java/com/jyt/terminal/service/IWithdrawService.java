package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.SellDTO;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.model.Withdraw;


/**
 * <p>
 * 比特币提现表 服务类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface IWithdrawService extends IService<Withdraw> {
	/**
	 * 分页查询提现流水
	 */
	public List<Map<String,Object>> getWithdrawList(Page<Withdraw> page,SellDTO sellDTO);
	
	/**
	 * 查询提现详情
	 */
	public Map<String, Object> getWithdrawDetail(Integer id);
	
	/**
	 * 卖出流水汇总（定时任务每日0点统计前一天数据）
	 */
	List<TradeStatistics> getWithdrawStatisticsList();
	
	public boolean inserOrUpdateWithdraw(List<Withdraw> withdrawList);

}
