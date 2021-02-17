package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.QueryAgencyProfitDTO;
import com.jyt.terminal.model.AgencyProfitDetail;


/**
 * <p>
 * 代理商分润流水表 Mapper 接口
 * </p>
 *
 * @author tangfq
 * @since 2021-02-07
 */
public interface AgencyProfitDetailMapper extends BaseMapper<AgencyProfitDetail>{

	List<Map<String, Object>> getAgencyProfitList(@Param("page")Page<Map<String, Object>> page,@Param("agencyProfit")QueryAgencyProfitDTO queryAgencyTradeDTO);
	
}
