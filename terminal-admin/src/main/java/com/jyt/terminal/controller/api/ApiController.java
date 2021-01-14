package com.jyt.terminal.controller.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.BitEnum.CustomerStatus;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.controller.api.dto.AdvertResponse;
import com.jyt.terminal.controller.api.dto.ChangePwdRequest;
import com.jyt.terminal.controller.api.dto.CustomerKycRequest;
import com.jyt.terminal.controller.api.dto.DownFileResponse;
import com.jyt.terminal.controller.api.dto.KycRequest;
import com.jyt.terminal.controller.api.dto.KycResponse;
import com.jyt.terminal.controller.api.dto.TerminalSettingResponse;
import com.jyt.terminal.controller.api.dto.UploadBuyRecordRequest;
import com.jyt.terminal.controller.api.dto.UploadDeviceInfoRequest;
import com.jyt.terminal.controller.api.dto.UploadOrderRecordRequest;
import com.jyt.terminal.controller.api.dto.UploadWithdrawRecordRequest;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.Advert;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.CashBox;
import com.jyt.terminal.model.CryptoSettings;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.Device;
import com.jyt.terminal.model.Order;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.model.Withdraw;
import com.jyt.terminal.service.IAdvertService;
import com.jyt.terminal.service.IBuyService;
import com.jyt.terminal.service.ICashBoxService;
import com.jyt.terminal.service.ICryptoSettingsService;
import com.jyt.terminal.service.ICustomerService;
import com.jyt.terminal.service.IDeviceService;
import com.jyt.terminal.service.IOrderService;
import com.jyt.terminal.service.ITerminalSettingService;
import com.jyt.terminal.service.IWithdrawService;
import com.jyt.terminal.util.Downloadimg;
import com.jyt.terminal.util.ToolUtil;



/**
 * 终端机调用
 * @author zengcong
 * @date 2018年10月16日
 */
@RestController
@RequestMapping(value="/api")
public class ApiController {

    private Logger log = LoggerFactory.getLogger(ApiController.class);
    
	@Value("${upload-path}")
    private String path;
   
    @Autowired
    private IBuyService buyService;
    
    @Autowired
    private IWithdrawService withdrawService;
    
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private IDeviceService deviceService;
    
    @Autowired
    private ITerminalSettingService terminalSettingService;
    @Autowired
    private ICryptoSettingsService cryptoSettingsService;
    @Autowired
    private IAdvertService advertService;
    
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ICashBoxService cashBoxService;
    
    /**
     * 修改密码
     * @param request
     * @return
     */
    @RequestMapping(value = "/changepwd",method = RequestMethod.POST)
    public ResponseEntity<?> changePwd(@RequestBody ChangePwdRequest request) {
    	
    	int  flat  =  terminalSettingService.changePwd(request.getTermNo(),request.getOldPwd(),request.getNewPwd());
        if (flat>0) {
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    }
    
    /**
     * 批量上传比特币购买流水
     * @param buyRecord
     * @return
     */
    @RequestMapping(value = "/upload/buyRecord",method = RequestMethod.POST)
    public ResponseEntity<?> uploadBuyRecord(@RequestBody UploadBuyRecordRequest buyRecord ) {
    	log.info("终端机号{}开始批量上传比特币购买流水记录",buyRecord.getTermNo());
    	if(buyRecord.getBuyList().size() <=0){
   		 return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	//boolean flag = buyService.insertBatch(buyRecord.getBuyList());
    	List<Buy> buyList = buyRecord.getBuyList();
    	boolean flag = true;
    	try {
    		flag = buyService.inserOrUpdateBuy(buyList);
		} catch (TerminalException e) {
			flag = false;
		}
    	if (flag) {
    		log.info("终端机号{}批量上传比特币购买流水记录成功",buyRecord.getTermNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	log.info("终端机号{}批量上传比特币购买流水记录失败",buyRecord.getTermNo());
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    }
    
    /**
     * 批量上传比特币提现流水
     * @param withdrawRecord
     * @return
     */
    @RequestMapping(value = "/upload/withdrawRecord",method = RequestMethod.POST)
    public ResponseEntity<?> uploadWithdrawRecord(@RequestBody UploadWithdrawRecordRequest withdrawRecord ) {
    	log.info("终端机号{}开始批量上传比特币出售流水记录",withdrawRecord.getTermNo());
    	if(withdrawRecord.getWithdrawList().size() <=0){
    		 return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	List<Withdraw> withdrawList = withdrawRecord.getWithdrawList();
    	boolean flag = true;
    	try {
    		flag = withdrawService.inserOrUpdateWithdraw(withdrawList);
		} catch (TerminalException e) {
			flag = false;
		}
    	if (flag) {
    		log.info("终端机号{}批量上传比特币出售流水记录成功",withdrawRecord.getTermNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	log.info("终端机号{}批量上传比特币出售流水记录失败",withdrawRecord.getTermNo());
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    }
    
    /**
     * 批量上传比特币订单流水
     * @param orderRecord
     * @return
     */
    @RequestMapping(value = "/upload/orderInfos",method = RequestMethod.POST)
    public ResponseEntity<?> uploadOrderInfos(@RequestBody UploadOrderRecordRequest orderRecord ) {
    	log.info("终端机号{}开始批量上传比特币订单记录",orderRecord.getTermNo());
    	if(orderRecord.getOrderList().size() <=0){
    		 return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	List<Order> orderList =orderRecord.getOrderList();
    	boolean flag = true;
    	try {
    		flag = orderService.inserOrUpdateOrder(orderList);
		} catch (TerminalException e) {
			flag = false;
		}
    	if (flag) {
    		log.info("终端机号{}批量上传比特币订单记录成功",orderRecord.getTermNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	log.info("终端机号{}批量上传比特币订单记录失败",orderRecord.getTermNo());
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    }
 
    /**
     * 批量上传硬件信息
     * @param deviceRecord
     * @return
     */
    @RequestMapping(value = "/upload/deviceInfo",method = RequestMethod.POST)
    public ResponseEntity<?> uploadDeviceInfo(@RequestBody UploadDeviceInfoRequest deviceRecord ) {
    	log.info("终端机号{}开始批量上传设备硬件信息",deviceRecord.getTermNo());
    	List<Device> deviceList = deviceRecord.getDeviceList();
    	if(deviceList.size()<=0 || StringUtils.isBlank(deviceList.get(0).getTerminalNo())){
    		return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	List<Device> selectList = deviceService.selectList(new EntityWrapper<Device>().eq("terminal_no", deviceList.get(0).getTerminalNo()));
    	boolean flag = false;
    	if(selectList.size()>0){
    		for(int i=0;i<selectList.size();i++){
        		for(int j=0;j<deviceList.size();j++){
        			if(selectList.get(i).getDeviceName().equals(deviceList.get(j).getDeviceName())){
        				deviceList.get(j).setId(selectList.get(i).getId());
        				break;
        			}
        		}
        	}
    		flag = deviceService.updateBatchById(deviceList);
    	}else{
    		flag = deviceService.insertBatch(deviceList);
    	}
    	
    	if (flag) {
    		log.info("终端机号{}更新设备硬件信息成功",deviceRecord.getTermNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	log.info("终端机号{}更新设备硬件信息失败",deviceRecord.getTermNo());
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    	
    }
    
    /**
     * 获取终端机设置参数
     * @param request
     * @returns
     */
    @RequestMapping(value = "/queryTermSet",method = RequestMethod.POST)
    public ResponseEntity<?> queryTermSet(@RequestBody BaseRequest request) {
    	log.info("终端机号{}获取终端机设置参数",request.getTermNo());
    	Advert advert = advertService.selectOne(new EntityWrapper<>());
    	try {
    		//查询终端参数
    		TerminalSetting setting = terminalSettingService.getByTermNo(request.getTermNo());
    		//查询虚拟币加密设置参数
    		String[] cryStr = setting.getCryptoSettings().split(",");
    		List<Integer> cryptoIds = new ArrayList<Integer>();
    		for(String str:cryStr) {
    			cryptoIds.add(Integer.valueOf(str));
    		}
    		List<CryptoSettings> cryptoSettingsList = cryptoSettingsService.cryptoSettingsListByIds(cryptoIds);
    		return ResponseEntity.ok(new TerminalSettingResponse(BizExceptionEnum.SUCCESS, setting,cryptoSettingsList,advert));
		} catch (TerminalException e) {
			return ResponseEntity.ok(new AdvertResponse(e.getCode(),e.getMessage(),advert));
		}
    }
    
    /**
     * 获取KYC审核状态返回
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryKycResult",method = RequestMethod.POST)
    public ResponseEntity<?> queryKycResult(@RequestBody CustomerKycRequest request ) {
    	log.info("终端机号{}发送邮件",request.getTermNo());
    	if(ToolUtil.isEmpty(request.getEmail()) && ToolUtil.isEmpty(request.isExceedQuota())){
    		 return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	//未超过限额(false)直接发送邮件
    	if(!request.isExceedQuota()){
    		log.info("终端机号{}未超过限额发送邮件",request.getTermNo());
    		Customer customer = new Customer();
    		customer.setEmail(request.getEmail());
            String verificationCode  = customerService.sendCustomerEmail(customer);
            return ResponseEntity.ok(new KycResponse(BizExceptionEnum.SUCCESS, verificationCode));
    	}
    	
    	Customer customer = customerService.selectOne(new EntityWrapper<Customer>().eq("e_mail", request.getEmail()));
    	if (ToolUtil.isEmpty(customer)) {
    		log.info("终端机号{}客户KYC审核信息不存在",request.getTermNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.INFO_NOT_EXISTED));
        }
    	
    	if(!customer.getStatus().equals(CustomerStatus.AUDIT_PASSED.getCode())){
    		log.info("终端机号{}客户KYC审核信息未审核成功",request.getTermNo());
    		return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.STATUS_NOT_ILLEGAL));
    	}
    	
        log.info("终端机号{}客户KYC审核信息通过，开始发送验证码",request.getTermNo());
        String verificationCode  = customerService.sendCustomerEmail(customer);
        
    	return ResponseEntity.ok(new KycResponse(BizExceptionEnum.SUCCESS, verificationCode));
    	
    }
    
    /**
     * 上传钞箱记录
     */
    @RequestMapping(value = "/upload/cashBoxRecord",method = RequestMethod.POST)
    public ResponseEntity<?> uploadCashBoxRecord(@RequestBody CashBox cashBox ) {
    	log.info("终端机号开始上传比特币钞箱记录,上传参数:{}",JSONObject.toJSONString(cashBox));
    	if(ToolUtil.isEmpty(cashBox)){
   		 return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.UPLOAD_NO_PARAM));
    	}
    	boolean flag = cashBoxService.insert(cashBox);
    	if (flag) {
    		log.info("终端机号{}上传比特币钞箱记录成功",cashBox.getTerminalNo());
            return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SUCCESS));
        } else {
        	log.info("终端机号{}上传比特币钞箱记录失败",cashBox.getTerminalNo());
        	return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
        }
    }


	@RequestMapping(value = "/downloadImg",method = RequestMethod.POST)
    public ResponseEntity<?> downloadImg(@RequestBody KycRequest request) {
    	log.info("请求接口/api/downloadImg,终端机号：{},kcyId：{}",request.getTermNo(),request.getKycId());
    	
    	if(ToolUtil.isEmpty(request.getTermNo())){
    		log.info("终端机号为空");
    		return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
    	}
    	
    	if(ToolUtil.isEmpty(request.getKycId())){
    		log.info("kycId为空");
    		return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.FAIL));
    	}

    	//从服务器获取图片路径
    	String picturePath="";
    	//向终端写图片数据
    	Downloadimg dl=new Downloadimg();
    	String fileContent="";
    	try {
    		fileContent=dl.download1();
		} catch (IOException e) {
			log.info("报错:{}",e.getMessage());
		}
    	
    	//log.info("请求终端接口/api/downloadImg返回数据：{}",fileContent);
    	
    	return ResponseEntity.ok(new DownFileResponse(BizExceptionEnum.SUCCESS,fileContent));        
	}
	
    
}
