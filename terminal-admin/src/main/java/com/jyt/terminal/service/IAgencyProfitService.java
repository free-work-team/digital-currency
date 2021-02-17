package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.QueryAgencyProfitDTO;
import com.jyt.terminal.model.AgencyProfitDetail;

public interface IAgencyProfitService extends IService<AgencyProfitDetail>{

	/**
	 * 
	 * @param page
	 * @param queryAgencyDTO
	 * @return
	 */
	public List<Map<String, Object>> getAgencyProfitList(Page<Map<String, Object>> page, QueryAgencyProfitDTO queryAgencyDTO);
	
}
