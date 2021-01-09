package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.DeviceMapper;
import com.jyt.terminal.dto.DeviceDTO;
import com.jyt.terminal.model.Device;
import com.jyt.terminal.service.IDeviceService;

/**
 * 
 * @author sunshubin
 * @date 2019年4月29日
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements  IDeviceService{
	
	private Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
	
	@Override
	public List<Map<String, Object>> getDeviceList(Page<Device> page, DeviceDTO deviceDTO) {
		return baseMapper.getDeviceList(page, deviceDTO);
	}
	
}
