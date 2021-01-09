
function dataValidate(id){
	var value = $("#"+id).val();
	var result = true;
	var reg= /^(-?\d+)(\.\d{1,2})?$/;
    var rateReg= /^(-?\d+)(\.\d{1,8})?$/;
    /*if(!value){
    	$("#"+id).css('border-color','red'); //添加css样式
    	$("#"+id+"Tip").empty(); //移除提示信息
    	$("#"+id+"Tip").append(id + "&nbsp;cannot be empty"); //提示信息
    	return;
    }else{
    	$("#"+id).css('border-color','#13ae13'); //添加css样式
    	$("#"+id+"Tip").empty(); //移除提示信息
    }*/
    
    //交易策略
    if(id == "exchangeStrategy"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //交易所
    if(id == "exchange"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	/*var exchangeStrategy = $("#exchangeStrategy").val();
		if(value && exchangeStrategy == 0 && value != 0){
			$("#"+id).css('border-color','red'); //添加css样式
	    	$("#"+id+"Tip").empty(); //移除提示信息
	    	$("#"+id+"Tip").append("When the Exchange Strategy is 0, the Exchange must be NO"); //提示信息
	    	result = false;
		}*/
    	var exchangeStrategy = $("#exchangeStrategy").val();
		if(exchangeStrategy != 0 && value  && value == 0){
			$("#"+id).css('border-color','red'); //添加css样式
	    	$("#"+id+"Tip").empty(); //移除提示信息
	    	$("#"+id+"Tip").append("When the Exchange Strategy is not 0, the Exchange cannot be NO"); //提示信息
	    	result = false;
		}
    }
    //PRO
    if(id == "proKey"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "proSecret"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "proPassphrase"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //KRAKEN
    if(id == "krakenApiKey"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "krakenSecret"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "krakenWithdrawalsAddressName"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //交易对 信息
    if(id == "orderType"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "currencyPair"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    
    //Wallet Information
    if(id == "hotWallet"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	var exchange = $("#exchange").val();
        var hotWallet = $("#hotWallet").val();
        //交易所为coinbase,钱包必须为coinbase
        if(value && exchange == 1 && hotWallet != 2){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("When the exchange is Coinbase Pro, the wallet must be coinbase."); //提示信息
    		return false;
        }
    }
    
    //bitgo
    if(id == "walletId"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "walletPassphrase"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "accessToken"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //coinbase
    if(id == "apiKey"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "apiSecret"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //blockchain
    if(id == "blockchainWalletId"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "blockchainPassword"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    
    //Buy Transaction Information
    if(id == "currency"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "rate"){
    	if(!value){
        	$("#"+id).css('border-color','#aaaaaa'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	if(value && !reg.test(value)){
			$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "minNeedBitcoin"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value && !reg.test(value)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    
    //Buy Transaction Information
    if(id == "buySingleFee"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value && !reg.test(value)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "buyTransactionFee"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	if(value && (0 > buyTransactionFee || buyTransactionFee > 100)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Buy Profit In(%) Error"); //提示信息
        	result = false;
        }
    }
    if(id == "minNeedCash"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value && !reg.test(value)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //Sell Transaction Information
    if(id == "sellSingleFee"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value && !reg.test(value)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "sellTransactionFee"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	if(value && (0 > sellTransactionFee || sellTransactionFee > 100)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Sell Profit In(%) Error"); //提示信息
        	result = false;
        }
    }
    if(id == "sellType"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //AML/KYC
    if(id == "kycEnable"){
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("KYC Enable"); //提示信息
        }
    }
    if(id == "limitCash"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value && !reg.test(value)){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The format is wrong, the correct format is 0.00"); //提示信息
        	result = false;
        }
    	if(value && reg.test(value)){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "kycUrl"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    //Basic Business Information
    if(id == "merchantName"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "hotline"){
    	var hotline = $("input[id='hotline']").val();
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	if (hotline.length > 20) {
    		$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The maximum length of the Hotline does not exceed 20"); //提示信息
        	result = false;
        }
    }
    if(id == "email"){
    	var email = $("input[id='email']").val();
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
    		$("#"+id).css('border','1px solid #13ae13'); //添加css样式
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    	if (value && !validateEmail()) {
    		$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Please enter the correct email"); //提示信息
        	result = false;
        }
        if (email.length > 50) {
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("The maximum length of the mailbox does not exceed 50"); //提示信息
        	result = false;
        }
    }
    if(id == "password"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "rePwd"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if (value && !UserInfoDlg.validatePwd()) {
    		$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Two input password must be consistent"); //提示信息
        	result = false;
        }
    	if(value && UserInfoDlg.validatePwd()){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    if(id == "rateSource"){
    	if(!value){
        	$("#"+id).css('border-color','red'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        	$("#"+id+"Tip").append("Input can not be empty"); //提示信息
        	result = false;
        }
    	if(value){
        	$("#"+id).css('border-color','#13ae13'); //添加css样式
        	$("#"+id+"Tip").empty(); //移除提示信息
        }
    }
    
    return result;
}

function paramValidate(){
	var obj = new Set();
    var hotWallet = $("#hotWallet option:selected").val();
    var exchange = $("#exchange option:selected").val();
    var kycEnable = $("#kycEnable").prop("checked");
    //需考虑先后顺序
    obj.add("exchangeStrategy");
    obj.add("exchange");
    //交易所
	if(exchange == 1){
	    obj.add("proKey");
	    obj.add("proSecret");
	    obj.add("proPassphrase");
	}
	if(exchange == 2){
		obj.add("krakenApiKey");
	    obj.add("krakenSecret");
	    obj.add("krakenWithdrawalsAddressName");
	}
	if(exchange != "" && exchange != null && exchange != 0){
		obj.add("orderType");
		obj.add("currencyPair");
	}
	/*obj.add("currency");*/
	obj.add("hotWallet");
    //热钱包
    if(hotWallet == 1){
    	obj.add("walletId");
        obj.add("walletPassphrase");
        obj.add("accessToken");
	}
	if(hotWallet == 2){
		obj.add("apiKey");
	    obj.add("apiSecret");
	}
	if(hotWallet == 3){
		obj.add("blockchainWalletId");
	    obj.add("blockchainPassword");
	}
	obj.add("rate");
	obj.add("rateSource");
    //Buy Transaction Information
    obj.add("buySingleFee");
    obj.add("buyTransactionFee");
    obj.add("minNeedCash");
    //obj.add("minNeedBitcoin");
    
    //Sell Transaction Information
    obj.add("sellSingleFee");
    obj.add("sellTransactionFee");
    obj.add("sellType");
    //AML/KYC
    obj.add("kycEnable");
    if(kycEnable){
    	obj.add("limitCash");
        obj.add("kycUrl");
    }
    //Basic Business Information
    obj.add("merchantName");
    obj.add("hotline");
    obj.add("email");
    if($("#password").length > 0) {
    	obj.add("password");
    }
    if($("#rePwd").length > 0) {
    	obj.add("rePwd");
    }
    var resultSet = new Set();
    var array = new Array();
    obj.forEach(function (element, sameElement, set) {
    	var resultBo = dataValidate(element);
    	resultSet.add(resultBo);
    	var obj = {};
    	obj['id'] = element;
    	obj['result'] = resultBo;
    	array.push(obj);
    });
    if(resultSet.size > 1){
    	for(var i = 0;i < array.length ; i++){
        	if(!array[i].result){
        		toErrorIndex(array[i].id);
        		return false;
        	}
        }
		return false;
    }else{
		return true;
    }
}
/**
 * 验证邮箱。
 */
function validateEmail() {
	var email_reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	var email = $("input[id='email']").val();
    if(!email_reg.test(email)){
        return false;
    } else {
        return true;
    }
};
/**
 * 验证手机号。
 */
function validatePhone() {
	var phone_reg = /^1[0-9]{10}$/;
    var phone = $("input[id='hotline']").val();
    if(!phone_reg.test(phone)){
        return false;
    } else {
        return true;
    }
};

function toErrorIndex(id){
	var value = $("#"+id).val();
	var $top = $("#" + id).offset().top - 150;
    //滑到未填项 的位置
    $("html,body").animate({scrollTop:$top});
}
