package com.jyt.terminal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jyt.terminal.commom.enums.StripeNumber;
import com.jyt.terminal.controller.pay.StripeController;
import com.jyt.terminal.dto.StripePayRequestVO;
import com.jyt.terminal.service.IStripePayService;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

@Service("stripePayService")
public class StripePayServiceImpl implements IStripePayService{

	private static Logger logger = LoggerFactory.getLogger(StripePayServiceImpl.class);

	@Override
    public String charge(String tokenPay) {

        try {
            StripeNumber.apiKey = "sk_test_51HrvN9C19JnVnOgVdEwcPYZzK1vt8jR6toCSvXdcfRHdS1esTKB9hILdcLcXVq9GDlqba3jVf6sWzq62lP7w4keh00EovG3h6w";
            
            // 业务订单数据，此处省略
    
            //发起支付
            Map<String, Object> payParams = new HashMap<>();
            payParams.put("amount", "2000");//以分为单位.product.getPrice().intValue());
            payParams.put("currency", "hkd");
            payParams.put("description", "Charge for my goods " /*+ user.getEmail()*/);
            payParams.put("source", tokenPay);//tok_visa
            Stripe.apiKey="sk_test_51HrvN9C19JnVnOgVdEwcPYZzK1vt8jR6toCSvXdcfRHdS1esTKB9hILdcLcXVq9GDlqba3jVf6sWzq62lP7w4keh00EovG3h6w";
          
            Charge charge = Charge.create(payParams);

            logger.info("statue:{}",charge.getStatus());
            //charge  支付是同步通知
            if ("succeeded".equals(charge.getStatus())) {
                //交易成功后，需要更新我们的订单表，修改业务参数，此处省略
                return "success";
            } else {
                return "false";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getCardList(StripePayRequestVO stripePayRequestVO) {
        StripeNumber.apiKey = "your_alipay";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", stripePayRequestVO.getUserId());
        /*User user = this.get("from User where id=:userId", params);
        if (null == user) {
            return failure(ResponseEnum.USER_NOT_FOUND_FAILURE);
        }
        List list = new ArrayList<StripeAddCardVO>();*/
        //如果没有这个stripe用户，就返回列表为空
        

            try {
                Map<String, Object> cardParams = new HashMap<String, Object>();
                cardParams.put("limit", 1);
                cardParams.put("object", "card");
                /*List<PaymentSource> cardList = Customer.retrieve(user.getStripeChargeId()).getSources().list(cardParams).getData();
                StripeCardVO stripeCardVO = new StripeCardVO();
                for (PaymentSource p : cardList) {
                    Card c = (Card) p;
                    stripeCardVO.setLast4(c.getLast4());
                    stripeCardVO.setExpYear(c.getExpYear());
                    stripeCardVO.setExpMonth(c.getExpMonth());
                    list.add(stripeCardVO);
                }
                return success(list);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
       return "";
    }

    @Override
    public String addCard(StripePayRequestVO stripePayRequestVO) {

        StripeNumber.apiKey = "your_alipay";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", stripePayRequestVO.getUserId());
        /*User user = this.get("from User where id=:userId", params);
        if (null == user) {
            return failure(ResponseEnum.USER_NOT_FOUND_FAILURE);
        }*/

        //如果没有这个stripe用户，添加卡片就是创建用户
        /*if (user.getStripeChargeId() == null || "".equals(user.getStripeChargeId())) {
            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("description", "Customer for test");
            customerParams.put("source", stripePayRequestVO.getToken());

            Customer c = null;
            try {
                c = Customer.create(customerParams);
                user.setStripeChargeId(c.getId());
                this.saveOrUpdate(user);
                success("添加成功");
            } catch (StripeException e) {
                e.printStackTrace();
            }

        } else {
            //  有这个用户，就是修改他的唯一一张默认卡
            try {
               Customer c = Customer.retrieve(user.getStripeChargeId());
                System.out.println("给客户修改默认卡号");
                Map<String, Object> tokenParam = new HashMap<String, Object>();
                tokenParam.put("source", stripePayRequestVO.getToken());
                c.update(tokenParam);
                return success("修改成功");
            } catch (Exception e) {
                System.out.println("异常了");
                System.out.println(e);
                e.printStackTrace();
            }
        }*/
        
        return "";
    }
}
