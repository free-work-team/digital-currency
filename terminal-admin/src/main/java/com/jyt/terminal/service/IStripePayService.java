package com.jyt.terminal.service;

import org.apache.coyote.Response;
import com.jyt.terminal.dto.StripePayRequestVO;

public interface IStripePayService {

	
	String charge(String tokenPay);

	String getCardList(StripePayRequestVO stripePayRequestVO);

	String addCard(StripePayRequestVO stripePayRequestVO);
    
    
}
