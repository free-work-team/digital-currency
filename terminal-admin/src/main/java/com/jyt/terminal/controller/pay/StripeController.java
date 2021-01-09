package com.jyt.terminal.controller.pay;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.jyt.terminal.controller.admin.BaseController;
import com.jyt.terminal.dto.StripePayRequestVO;
import com.jyt.terminal.service.IStripePayService;
import com.jyt.terminal.util.ShiroKit;

import io.swagger.annotations.ApiParam;

/**
 * 
 * @author tangfq
 * @date   2020年11月24日 下午11:39:25
 * @version V1.0
 */
@Controller
@RequestMapping("/stripe")
public class StripeController {
	
	@Resource
    private IStripePayService stripePayService;
 
    private static Logger logger = LoggerFactory.getLogger(StripeController.class);

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/testStripe", method = RequestMethod.GET)
    public String login() {       
       return "/testStripe.html";       
    }
    
    /**
     * 获取用户卡片列表
     *
     * @return
     */
    @RequestMapping(value = "/getCardList", method = RequestMethod.POST)
    @ResponseBody
    public String getCardList(@RequestBody @Valid StripePayRequestVO stripePayRequestVO, BindingResult result) {
       
        return stripePayService.getCardList(stripePayRequestVO);
    }
 
    /**
     * 添加用户卡片
     * @return
     */
    @RequestMapping(value = "/addCard", method = RequestMethod.POST)
    @ResponseBody
    public String addCard(@RequestBody @Valid StripePayRequestVO stripePayRequestVO, BindingResult result) {
        logger.debug("购买套餐请求参数 {} = ", JSON.toJSON(stripePayRequestVO));

        return stripePayService.addCard(stripePayRequestVO);
    }

    /**
     * 发起支付
     * @return
     */
    @RequestMapping(value = "/charge", method = RequestMethod.POST)
    @ResponseBody
    public String aliPay(HttpServletRequest request) {
    	String stripeToken=request.getParameter("stripeToken");
    	logger.info("token:{}",stripeToken);
    	
    	return stripePayService.charge(stripeToken);
    }
	
}
