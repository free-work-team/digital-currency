package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.CryptoSettingsDTO;
import com.jyt.terminal.model.CryptoSettings;
/**
 * 加密设置
 * @className CryptoSettingsMapper
 * @author wangwei
 * @date 2020年1月17日
 *
 */
public interface CryptoSettingsMapper extends BaseMapper<CryptoSettings> {
	/**
	 * 分页查询
	 */
	List<Map<String, Object>> getCryptoSettingsList(@Param("page")Page<Map<String, Object>> page,@Param("entity")CryptoSettingsDTO cryptoSettingsDTO);
	/**
	 * 根据ids查询加密设置列表
	 * @param list
	 * @return
	 */
	List<CryptoSettings> cryptoSettingsListByIds(@Param("list")List<Integer> cryptoIds);

}
