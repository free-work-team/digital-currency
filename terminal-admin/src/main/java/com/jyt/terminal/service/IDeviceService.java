package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.DeviceDTO;
import com.jyt.terminal.model.Device;


/**
 * <p>
 * 终端机硬件状态表 服务类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface IDeviceService extends IService<Device> {
	
	public List<Map<String,Object>> getDeviceList(Page<Device> page,DeviceDTO deviceDTO);

}
