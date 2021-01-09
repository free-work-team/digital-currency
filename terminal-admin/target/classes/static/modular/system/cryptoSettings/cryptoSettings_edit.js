$(function(){
	var hotWallet = $("#hotWallet").val();
	var exchange = $("#exchange").val();
	if(hotWallet == 1){
		$(".walletDiv").attr("style","display:block;");//显示div
		$(".apiDiv").attr("style","display:none;");//隐藏div
		$(".blockchainDiv").attr("style","display:none;");//隐藏div
	}
	if(hotWallet == 2){
		$(".walletDiv").attr("style","display:none");
		$(".apiDiv").attr("style","display:block;");
		$(".blockchainDiv").attr("style","display:none;");
	}
	if(hotWallet == 3){
		$(".walletDiv").attr("style","display:none");
		$(".apiDiv").attr("style","display:none;");
		$(".blockchainDiv").attr("style","display:block;");
	}
	if(exchange == 0){
		$(".proDiv").attr("style","display:none;");
		$(".krakenDiv").attr("style","display:none;");
		$(".exchangeParamDiv").attr("style","display:none;");
	}
	if(exchange == 1){
		$(".proDiv").attr("style","display:block;");
		$(".krakenDiv").attr("style","display:none;");
		$(".exchangeParamDiv").attr("style","display:block;");
	}
	if(exchange == 2){
		$(".proDiv").attr("style","display:none;");
		$(".krakenDiv").attr("style","display:block;");
		$(".exchangeParamDiv").attr("style","display:block;");
	}
	
	//kyc
	var kycEnable = $("#kycEnable").prop("checked");
	if(kycEnable){
		var html = '<div class="kycEnableTip">KYC Enabled</div>';
    	$(".kycEnableTip").remove();
    	$("#kycEnable").after(html);
		$(".kycDiv").attr("style","display:block;");
	}else{
		$(".kycEnableTip").remove();
		$(".kycDiv").attr("style","display:none;");
	}
	$("#kycEnable").on("change", function () {
	    var that = $(this);
	    if (that.prop("checked")) {
	    	var html = '<div class="kycEnableTip">KYC Enabled</div>';
	        	$(".kycEnableTip").remove();
	        	$("#kycEnable").after(html);
			$(".kycDiv").attr("style","display:block;");
	    } else {
	    	$(".kycEnableTip").remove();
			$(".kycDiv").attr("style","display:none;");
	    }
	});
	
});

$("#hotWallet").on("change",function(){
    pID = $("option:selected",this).val();//需求主键
    choose();
});
$("#exchange").on("change",function(){
    pID = $("option:selected",this).val();//需求主键
    choose();
});

//交易所、钱包-焦点事件
function inFocus(id){
	$("#"+id).val("");
}
function outBlur(id){
	var value = $("#"+id).val();
	if(value == "" || value == null || value == "undefined"){
		$("#"+id).val("******");
	}else{
		$("#"+id).val(value);
	}
}