package com.jyt.terminal.controller.admin;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.enums.BitEnum.DeviceStatus;
import com.jyt.terminal.dao.DeviceMapper;
import com.jyt.terminal.dto.DeviceDTO;
import com.jyt.terminal.model.Device;
import com.jyt.terminal.service.IDeviceService;
import com.jyt.terminal.warpper.DeviceWarpper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/device")
public class DeviceController extends BaseController {

	private static String PREFIX = "/system/device/";

	@Autowired
	public IDeviceService deviceService;

	@Autowired
	public DeviceMapper deviceMapper;

	/**
	 * 跳转到运行状态管理页面
	 */
	@RequestMapping("")
	public String index() {
		return PREFIX + "runManage.html";
	}

	/**
	 * 查询购买流水列表
	 */
	@ApiOperation(value = "查询运行终端机列表", notes = "分页查询运行终端机列表")
	@RequestMapping("/list")
	@ResponseBody
	public Object list(@ApiParam(name = "查询购买流水列表", required = true) DeviceDTO deviceDTO) {
		Page<Device> page = new PageFactory<Device>().defaultPage();
		// queryUserDTO.setMerchantId(ShiroKit.getUser().getDept().getDeptno());
		if (deviceDTO.getStatus() == 0) {
			deviceDTO.setStatus(DeviceStatus.ABNORMAL.getValue());
		}
		int count = this.deviceMapper.getDeviceListCount(deviceDTO);
		List<Map<String, Object>> result = this.deviceService.getDeviceList(page, deviceDTO);
		page.setRecords((List<Device>) new DeviceWarpper(result).warp());
		page.setTotal(count);
		return super.packForBT(page);
	}

}
