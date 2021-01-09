package com.jyt.terminal.controller.admin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.OrderStatus;
import com.jyt.terminal.commom.enums.BitEnum.TradeType;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.WithdrawMapper;
import com.jyt.terminal.dto.OrderDTO;
import com.jyt.terminal.model.Order;
import com.jyt.terminal.service.IOrderService;
import com.jyt.terminal.util.Tip;
import com.jyt.terminal.util.ToolUtil;
import com.jyt.terminal.warpper.OrderWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/order")
public class QueryOrderController extends BaseController{

private static String PREFIX = "/system/trade/";
	
	@Autowired
	public IOrderService orderService;
	@Resource
	public WithdrawMapper withdrawMapper;
	/**
     * 跳转到比特币订单页面
     */
	@RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
	public String index(Model model) {
    	//交易类型（买或卖）枚举
    	List<Map<String,Object>> tradeType = new ArrayList<Map<String,Object>>();
  		for(TradeType param:TradeType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			tradeType.add(value);
  		}
  		model.addAttribute("tradeType",tradeType);
    	//订单交易状态枚举
    	List<Map<String,Object>> orderStatus = new ArrayList<Map<String,Object>>();
  		for(OrderStatus param:OrderStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("message", param.getDesc());
  			orderStatus.add(value);
  		}
  		model.addAttribute("orderStatus",orderStatus);
  		//加密币种枚举
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("endesc", param.getEndesc());
  			value.put("cndesc", param.getCndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
        return PREFIX + "orderList.html";
    }
    
    /**
     * 查询订单流水列表
     */
    @ApiOperation(value="查询订单流水列表",notes="分页查询订单流水列表")
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object list(@ApiParam(name="查询购买流水列表",required=true) OrderDTO orderDTO) {
        Page<Order> page = new PageFactory<Order>().defaultPage();
        List<Map<String,Object>> result = this.orderService.getOrderList(page, orderDTO);
        page.setRecords((List<Order>) new OrderWarpper(result).warp());
        
        //查询交易手续费统计(买和卖)
        /*Double profitSum = 0d;
        Map<String,Object> map = new HashMap<String,Object>();
        if(ToolUtil.isNotEmpty(orderDTO.getStatus()) && !orderDTO.getStatus().equals(OrderStatus.CONFIRM.getValue().toString())){
        	BigDecimal dec = new BigDecimal(profitSum);
            map.put("handlingFeeSum", dec.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            PageInfoBT<Order> pageInfoBT = super.packForBT(page);
            pageInfoBT.setResult(map);
            return pageInfoBT;
        }
        if(ToolUtil.isEmpty(orderDTO.getStatus())){
        	orderDTO.setStatus(OrderStatus.CONFIRM.getValue().toString());
        	
        }
        List<Order> list = orderService.getProfitSum(orderDTO);
        for(Order order:list){
        	Double buyProfit = 0d;
        	Double sellProfit = 0d;
        	Double funds = Double.valueOf(order.getFunds());    //交易金额
        	Double executedValue = Double.valueOf(order.getExecutedValue());    //实际交易金额
        	Double fillFees = Double.valueOf(order.getFillFees());    //手续费
        	//买的交易手续费
        	if(order.getSide().equals(TradeType.BUY.getMessage())){
            	buyProfit += (fillFees - (funds - executedValue));
        	}
        	//卖的交易手续费
        	if(order.getSide().equals(TradeType.SELL.getMessage())){
            	sellProfit += (fillFees - (executedValue - funds));
        	}
        	profitSum += (buyProfit + sellProfit);
        }
        BigDecimal dec = new BigDecimal(profitSum);
        map.put("handlingFeeSum", dec.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        PageInfoBT<Order> pageInfoBT = super.packForBT(page);
        pageInfoBT.setResult(map);
        return pageInfoBT;*/
        return super.packForBT(page);
    }
	
    /**
	 * 跳转到订单详情页面
     * @throws ParseException 
	 */
	@RequestMapping("/detail/{transId}")
	@Permission
	public String sellFlowDetail(@PathVariable String transId, Model model) throws ParseException {
		if (ToolUtil.isEmpty(transId)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Order order = this.orderService.selectById(transId);
		order.setStatus( OrderStatus.valueOf(Integer.valueOf((String) order.getStatus())));
		String currency = order.getCurrency();
		if(StringUtils.isEmpty(currency)) {
			currency = "";
		}
		order.setFillFees(order.getFillFees() + currency);
		order.setPrice(order.getPrice() + currency);
		order.setExecutedValue(order.getExecutedValue() + currency);
		if(StringUtils.isNotBlank(order.getFunds())){
			order.setFunds(new BigDecimal(order.getFunds()).setScale(2).toString()+currency);
		}else{
			order.setFunds("0.00"+currency);
		}
		String size = order.getSize();
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(order.getCryptoCurrency())) {
			order.setSize(size+"BTC");
			order.setFilledSize(order.getFilledSize()+"BTC");
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(order.getCryptoCurrency())) {
			order.setSize(size+"ETH");
			order.setFilledSize(order.getFilledSize()+"ETH");
		}
		model.addAttribute("order",order);
		String lastTime = "";
		if(ToolUtil.isNotEmpty(order.getUpdateTime())){
			lastTime = sdf.format(order.getUpdateTime());
		}
		model.addAttribute("updateTime",lastTime);
		return PREFIX + "order_detail.html";
	}
	
	@RequestMapping("/detailEmpty/{transId}")
	@ResponseBody
	public Tip detailEmpty(@PathVariable String transId) {
		Order order = this.orderService.selectById(transId);
		if(ToolUtil.isEmpty(order)){
			SUCCESS_TIP.setCode(500);
		}else{
			SUCCESS_TIP.setCode(200);
		}
		return SUCCESS_TIP;
	}
}
