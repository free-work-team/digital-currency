package com.jyt.terminal.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.service.IBuyService;
import com.jyt.terminal.service.ITradeStatisticsService;
import com.jyt.terminal.service.IWithdrawService;
import com.jyt.terminal.util.ToolUtil;


@Component
@EnableScheduling
public class StatisticsTask {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IBuyService buyService;
	@Autowired
	private IWithdrawService withdrawService;
	@Autowired
	private ITradeStatisticsService tradeStatisticsService;
	
	/**
	 * 交易流水统计(比特币买和卖)
	 */
    @Scheduled(cron = "0 0 2 * * ?")
	//@Scheduled(cron = "0 */1 * * * ?")
    public void tradeStatistics() {
		 try {			 
			 List<TradeStatistics> buyStatistics = this.buyService.getBuyStatisticsList();
			 List<TradeStatistics> withdrawStatistics = this.withdrawService.getWithdrawStatisticsList();
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			 Date date=new Date();
			 Calendar calendar=Calendar.getInstance();
			 calendar.setTime(date);
			 calendar.add(Calendar.DAY_OF_MONTH, -1);
			 String data = sdf.format(calendar.getTime());
			 List<TradeStatistics> req = tradeStatisticsService.selectList(new EntityWrapper<TradeStatistics>().eq("date", data));
			 if(ToolUtil.isEmpty(req)){
				 if(ToolUtil.isNotEmpty(buyStatistics)){
					 tradeStatisticsService.insertBatch(buyStatistics);
				 }
				 if(ToolUtil.isNotEmpty(withdrawStatistics)){
					 tradeStatisticsService.insertBatch(withdrawStatistics);
				 }
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		 
    }


}
