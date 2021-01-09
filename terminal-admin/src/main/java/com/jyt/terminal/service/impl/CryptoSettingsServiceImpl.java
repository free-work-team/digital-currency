package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.CryptoSettingsMapper;
import com.jyt.terminal.dto.CryptoSettingsDTO;
import com.jyt.terminal.model.CryptoSettings;
import com.jyt.terminal.service.ICryptoSettingsService;

/**
 * 加密设置
 * @className CryptoSettingsServiceImpl
 * @author wangwei
 * @date 2020年1月16日
 *
 */
@Service
public class CryptoSettingsServiceImpl extends ServiceImpl<CryptoSettingsMapper, CryptoSettings> implements  ICryptoSettingsService{
	private Logger LOGGER = LoggerFactory.getLogger(CryptoSettingsServiceImpl.class);
	
	@Autowired
	private CryptoSettingsMapper cryptoSettingsMapper;

	@Override
	public List<Map<String, Object>> getCryptoSettingsList(Page<Map<String, Object>> page, CryptoSettingsDTO cryptoSettingsDTO) {
		return cryptoSettingsMapper.getCryptoSettingsList(page,cryptoSettingsDTO);
	}

	@Override
	public List<CryptoSettings> cryptoSettingsListByIds(List<Integer> cryptoIds) {
		return cryptoSettingsMapper.cryptoSettingsListByIds(cryptoIds);
	}
}
