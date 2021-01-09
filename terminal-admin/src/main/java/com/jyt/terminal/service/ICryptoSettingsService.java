package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.CryptoSettingsDTO;
import com.jyt.terminal.model.CryptoSettings;

/**
 * 加密设置 服务类
 * @className ICryptoSettingsService
 * @author wangwei
 * @date 2020年1月16日
 *
 */
public interface ICryptoSettingsService extends IService<CryptoSettings> {
	
	/**
	 * 分页查询
	 */
	List<Map<String,Object>> getCryptoSettingsList(Page<Map<String, Object>> page,CryptoSettingsDTO cryptoSettingsDTO);
	/**
	 * 根据ids查询加密设置列表
	 * @param list
	 * @return
	 */
	List<CryptoSettings> cryptoSettingsListByIds(List<Integer> cryptoIds);
	
}
