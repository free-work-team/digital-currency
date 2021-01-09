package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.DeviceDTO;
import com.jyt.terminal.model.Device;


/**
 * <p>
 * 终端机硬件状态 Mapper 接口
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface DeviceMapper extends BaseMapper<Device> {
	
	int getDeviceListCount(@Param("entity")DeviceDTO deviceDTO);
	
	public List<Map<String,Object>> getDeviceList(@Param("page")Page<Device> page,@Param("entity")DeviceDTO deviceDTO);

}