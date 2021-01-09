/**
 * 详情对话框（可用于添加和修改对话框）
 */
var success = getSingleLanguage('success');
var fail = getSingleLanguage('fail');

var emailNotEmpty = getSingleLanguage('emailNotEmpty') || "邮箱不能为空";
var phoneNotEmpty = getSingleLanguage('phoneNotEmpty') || "手机号不能为空";
var buyProfitNotEmpty = getSingleLanguage('buyProfitNotEmpty') || "购买手续费(%)不能为空";
var UserInfoDlg = {
    userInfoData: {},
    validateFields: {
    	password: {
            validators: {
                notEmpty: {
                    message: 'Password cannot be empty'
                }
            }
        },
    	terminalNo: {
            validators: {
                notEmpty: {
                    message: 'Terminal number cannot be empty'
                }
            }
        },
        merchantName: {
            validators: {
                notEmpty: {
                    message: 'Merchant Name cannot be empty'
                }
            }
        },
        email: {
            validators: {
                notEmpty: {
                    message: emailNotEmpty
                }
            }
        },
        hotline: {
            validators: {
                notEmpty: {
                    message: phoneNotEmpty
                }
            }
        },
        buyTransactionFee: {
            validators: {
                notEmpty: {
                    message: buyProfitNotEmpty
                }
            }
        },
        buySingleFee: {
            validators: {
                notEmpty: {
                    message: '单笔购买手续费不能为空'
                }
            }
        },
       sellTransactionFee: {
            validators: {
                notEmpty: {
                    message: '出售手续费不能为空'
                }
            }
        },
        sellSingleFee: {
            validators: {
                notEmpty: {
                    message: '单笔出售手续费不能为空'
                }
            }
        },
        /*rate: {
        	validators: {
        		notEmpty: {
        			message: '比特币汇率不能为空'
        		}
        	}
        },*/
        /*accessToken: {
            validators: {
                notEmpty: {
                    message: 'token不能为空'
                }
            }
        }*/
    }
};

/**
 * 清除数据
 */
UserInfoDlg.clearData = function () {
    this.userInfoData = {};
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
UserInfoDlg.set = function (key, value) {
    if(typeof value == "undefined"){
        if(typeof $("#" + key).val() =="undefined"){
            var str="";
            var ids="";
            $("input[name='"+key+"']:checkbox").each(function(){
                if(true == $(this).is(':checked')){
                    str+=$(this).val()+",";
                }
            });
            if(str){
                if(str.substr(str.length-1)== ','){
                    ids = str.substr(0,str.length-1);
                }
            }else{
                $("input[name='"+key+"']:radio").each(function(){
                    if(true == $(this).is(':checked')){
                        ids=$(this).val()
                    }
                });
            }
            this.userInfoData[key] = ids;
        }else{
            this.userInfoData[key]= $("#" + key).val();
        }
    }

    return this;
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
UserInfoDlg.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
UserInfoDlg.close = function () {

	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	parent.layer.close(index);  // 关闭layer
//    parent.layer.close(window.parent.layerIndex);
};


/**
 * 收集数据
 */
UserInfoDlg.collectData = function () {
    this.set('id').set('hotWallet').set('terminalNo').set('email').set('merchantName').set('hotline').set('buyTransactionFee')
    .set('buySingleFee').set('sellTransactionFee').set('sellSingleFee').set('rate').set('exchangeStrategy').set('minNeedCash').set('password')
    .set('walletId').set('walletPassphrase').set('apiKey').set('apiSecret').set('accessToken').set('currency').set('sellType').set('exchange')
    .set('limitCash').set('kycUrl').set('krakenApiKey').set('krakenSecret').set('krakenWithdrawalsAddressName').set('minNeedBitcoin')
    .set('blockchainWalletId').set('blockchainPassword').set('rateSource');
};

/**
 * 验证两个密码是否一致
 */
UserInfoDlg.validatePwd = function () {
    var password = this.get("password");
    var rePassword = this.get("rePwd");
    if (password == rePassword) {
        return true;
    } else {
        return false;
    }
};

/**
 * 验证邮箱。
 */
UserInfoDlg.validateEmail = function () {
	var email_reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    var email = this.get("email");
    if(!email_reg.test(email)){
        return false;
    } else {
        return true;
    }
};

/**
 * 验证手机号。
 */
UserInfoDlg.validatePhone = function () {
	var phone_reg = /^1[0-9]{10}$/;
    var phone = this.get("hotline");
    if(!phone_reg.test(phone)){
        return false;
    } else {
        return true;
    }
};

/**
 * 验证数据是否为空
 */
UserInfoDlg.validate = function () {
	var reg= /^(-?\d+)(\.\d{1,2})?$/;
    var rateReg= /^(-?\d+)(\.\d{1,8})?$/;
    
	var merchantName = $("input[id='merchantName']").val();
    var hotline = $("input[id='hotline']").val();
    var email = $("input[id='email']").val();
    var buyTransactionFee = $("input[id='buyTransactionFee']").val();
    var buySingleFee = $("input[id='buySingleFee']").val();
    var sellTransactionFee = $("input[id='sellTransactionFee']").val();
    var sellSingleFee = $("input[id='sellSingleFee']").val();
    var rate = $("input[id='rate']").val();
    var hotWallet = $("#hotWallet option:selected").val();
    var currency = $("#currency option:selected").val();
    var sellType = $("#sellType option:selected").val();
    var exchange = $("#exchange option:selected").val();
    var limitCash = $("input[id='limitCash']").val();
    var kycUrl = $("input[id='kycUrl']").val();
    var exchangeStrategy = $("#exchangeStrategy option:selected").val();
    var minNeedCash = $("input[id='minNeedCash']").val();
   
    
    if(!currency){
    	Feng.error("Currency cannot be empty");
    	 return false;
    }
    /*if(!hotWallet){
    	Feng.error("HotWallet cannot be empty");
    	 return false;
    }*/
    if(rate){
    	if(!reg.test(rate)){
        	Feng.error("The Price format is wrong, the correct format is 0.00");
            return false;
        }
    }
    /*if(!hotWallet){
    	Feng.error("是否热钱包不能为空");
    	 return false;
    }*/
    if(!buySingleFee){
    	Feng.error("Buy Fixed Fee cannot be empty");
    	 return false;
    }
    if(!reg.test(buySingleFee)){
    	Feng.error("The Buy Fixed Fee format is wrong, the correct format is 0.00");
        return false;
    }
    if(!buyTransactionFee){
    	Feng.error("Buy Profit In(%) cannot be empty");
    	 return false;
    }
    if(!exchangeStrategy){
    	Feng.error("Exchange Strategy cannot be empty");
    	return false;
    }
    if(!minNeedCash){
    	Feng.error("Min Transaction Amount cannot be empty");
    	return false;
    }
    if(!reg.test(minNeedCash)){
    	Feng.error("The Min Transaction Amount format is wrong, the correct format is 0.00");
        return false;
    }
    if(!sellSingleFee){
    	Feng.error("Sell Fixed Fee cannot be empty");
    	 return false;
    }
    if(!reg.test(sellSingleFee)){
    	Feng.error("The Sell Fixed Fee format is wrong, the correct format is 0.00");
        return false;
    }
    if(!sellTransactionFee){
    	Feng.error("Sell Profit In(%) cannot be empty");
    	 return false;
    }
    if(!sellType){
    	Feng.error("Confirmations cannot be empty");
    	 return false;
    }
    if(!limitCash){
    	Feng.error("Limit Amount cannot be empty");
    	 return false;
    }
    if(!reg.test(limitCash)){
    	Feng.error("The Limit Amount format is wrong, the correct format is 0.00");
        return false;
    }
    if(!kycUrl){
    	Feng.error("Submitted Url cannot be empty");
    	 return false;
    }
    if(!merchantName){
    	Feng.error("Merchant Name cannot be empty");
    	 return false;
    }
    if ( merchantName.length > 20) {
        Feng.error("The maximum length of the merchant Name is 20");
        return false;
    }
    if(!hotline){
    	Feng.error("Hotline cannot be empty");
    	 return false;
    }
    if (hotline.length > 20) {
        Feng.error("The maximum length of the Hotline does not exceed 20");
        return;
    }
    if(!email){
    	Feng.error("Email cannot be empty");
    	 return false;
    }
    if (!this.validateEmail()) {
        Feng.error("Please enter the correct email");
        return false;
    }
    if (email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return false;
    }
    
    /*if(!rate){
    	Feng.error("比特币汇率不能为空");
    	return false;
    }*/
    /*if(!accessToken){
    	Feng.error("token不能为空");
    	 return false;
    }*/
    
   return true;
};

/**
 * 提交添加用户
 */
UserInfoDlg.addSubmit = function () {
    this.clearData();
    this.collectData();
    /*if(!paramValidate()){
    	return;
    }
    
    var result = getChannelParam();
    if(result){
    	var obj = result;
    }else{
    	return;
    }
    var channelParam = JSON.stringify(obj);*/
    var kycEnable = $("#kycEnable").prop("checked")?1:0;
    var chk_value =[];
    /*$('input[name="cryptoSettings1"]:checked').each(function(){
    	chk_value.push($(this).val());    
    });
    $('input[name="cryptoSettings2"]:checked').each(function(){
    	chk_value.push($(this).val());    
    });*/
    //var cryptoSettings1 = $('input[name="cryptoSettings1"]:checked').val();
    //var cryptoSettings2 = $('input[name="cryptoSettings2"]:checked').val();
    var cryptoSettings1 = $("#cryptoSettings1").val();
    var cryptoSettings2 = $("#cryptoSettings2").val();
    if(cryptoSettings1){
    	chk_value.push(cryptoSettings1);
    }
    if(cryptoSettings2){
    	chk_value.push(cryptoSettings2);
    }
    var cryptoSettings = chk_value;
    if(chk_value.length > 1){
    	cryptoSettings = chk_value.join(",");
    }
    var way = $("#way").prop("checked")?2:1;
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/term/add", function (data) {
    	if(data.code != 200){
            Feng.error(fail + ", " + data.message + " !");
    	}else{
    		Feng.success(success);
    	}
        window.parent.MgrTerm.table.refresh();
        UserInfoDlg.close();
    }, function (data) {
        Feng.error(fail + " " + data.message + " !");
    });
    ajax.set(this.userInfoData);
    //ajax.set("channelParam",channelParam);
    ajax.set("kycEnable",kycEnable);
    ajax.set("cryptoSettings",cryptoSettings.toString());
    ajax.set("way",way);
    ajax.start();
};


/**
 * 提交修改
 */
UserInfoDlg.editSubmit = function () {

    this.clearData();
    this.collectData();
    /*if(!paramValidate()){
    	return;
    }
    var result = getChannelParam();
    if(result){
    	var obj = result;
    }else{
    	return;
    }
    var channelParam = JSON.stringify(obj);*/
    var kycEnable = $("#kycEnable").prop("checked")?1:0;
    var chk_value =[];
    /*$('input[name="cryptoSettings"]:checked').each(function(){
    	chk_value.push($(this).val());
    });*/
    //var cryptoSettings1 = $('input[name="cryptoSettings1"]:checked').val();
    //var cryptoSettings2 = $('input[name="cryptoSettings2"]:checked').val();
    var cryptoSettings1 = $("#cryptoSettings1").val();
    var cryptoSettings2 = $("#cryptoSettings2").val();
    if(cryptoSettings1){
    	chk_value.push(cryptoSettings1);
    }
    if(cryptoSettings2){
    	chk_value.push(cryptoSettings2);
    }
    var cryptoSettings = chk_value;
    if(chk_value.length > 1){
    	cryptoSettings = chk_value.join(",");
    }
    var way = $("#way").prop("checked")?2:1;
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/term/edit", function (data) {
    	if(data.code != 200){
            Feng.error(fail + ", " + data.message + " !");
    	}else{
    		Feng.success(success);
    	}
    	window.parent.MgrTerm.table.refresh();
        UserInfoDlg.close();
    }, function (data) {
        Feng.error(fail + " " + data.message + " !");
    });
    ajax.set(this.userInfoData);
    //ajax.set("channelParam",channelParam);
    ajax.set("kycEnable",kycEnable);
    ajax.set("cryptoSettings",cryptoSettings.toString());
    ajax.set("way",way);
    ajax.start();
};

function getChannelParam(){
	var hotWallet = $('#hotWallet option:selected') .val();
	var exchange = $('#exchange option:selected') .val();
	if(hotWallet == 0){
    	var walletId = "";
        var walletPassphrase = "";
        var accessToken = "";
    }
    if(hotWallet == 1){
    	var walletId = $("#walletId").val();
        var walletPassphrase = $("input[id='walletPassphrase']").val();
        var accessToken = $("input[id='accessToken']").val();
    }
    if(hotWallet == 2){
	    var apiKey = $("input[id='apiKey']").val();
	    var apiSecret = $("input[id='apiSecret']").val();
    	
    }
    if(hotWallet == 3){
	    var blockchainWalletId = $("#blockchainWalletId").val();
	    var blockchainPassword = $("#blockchainPassword").val();
    }
    if(exchange == 0){
	    var proKey = "";
	    var proSecret = "";
	    var proPassphrase = "";
    }
    if(exchange == 1){
	    var proKey = $("input[id='proKey']").val();
	    var proSecret = $("input[id='proSecret']").val();
	    var proPassphrase = $("input[id='proPassphrase']").val();
    }
    if(exchange == 2){
	    var krakenApiKey = $("input[id='krakenApiKey']").val();
	    var krakenSecret = $("input[id='krakenSecret']").val();
	    var krakenWithdrawalsAddressName = $("input[id='krakenWithdrawalsAddressName']").val();
    }
    
    var obj = {};
    var wallet = {};//钱包
    var exchange = {};//交易所
    //钱包
    if((walletId != null && $.trim(walletId) != '' && walletPassphrase != null && $.trim(walletPassphrase) != '' && accessToken != null && $.trim(accessToken) != '')){
    	wallet['wallet_id'] = walletId;
    	wallet['wallet_passphrase'] = walletPassphrase;
    	wallet['access_token'] = accessToken;
    }
    if((apiKey != null && $.trim(apiKey) != '' && apiSecret != null && $.trim(apiSecret) != '')){
    	wallet['api_key'] = apiKey;
    	wallet['api_secret'] = apiSecret;
    }
    if((blockchainWalletId != null && $.trim(blockchainWalletId) != '' && blockchainPassword != null && $.trim(blockchainPassword) != '')){
    	wallet['wallet_id'] = blockchainWalletId;
    	wallet['password'] = blockchainPassword;
    }
    
    //交易所
    if((proKey != null || $.trim(proKey) != '') && (proSecret != null && $.trim(proSecret) != '') && (proPassphrase != null && $.trim(proPassphrase) != '' )){
    	exchange['pro_key'] = proKey;
    	exchange['pro_secret'] = proSecret;
    	exchange['pro_passphrase'] = proPassphrase;
    }
    if((krakenApiKey != null || $.trim(krakenApiKey) != '') && (krakenSecret != null && $.trim(krakenSecret) != '') && (krakenWithdrawalsAddressName != null && $.trim(krakenWithdrawalsAddressName) != '' )){
    	exchange['kraken_api_key'] = krakenApiKey;
    	exchange['kraken_secret'] = krakenSecret;
    	exchange['kraken_withdrawals_address_name'] = krakenWithdrawalsAddressName;
    }
    
    var orderType = $("#orderType") .val();
	var currencyPair = $("#currencyPair") .val();
	exchange['order_type'] = orderType;
	exchange['currency_pair'] = currencyPair;
    obj['wallet'] = wallet;
    obj['exchange'] = exchange;
    return obj;
}

/**
 * 修改密码
 */
UserInfoDlg.chPwd = function () {
	
	
	 if (!this.validatePwd()) {
	        Feng.error("Two input password must be consistent");
	        return;
	    }
    var ajax = new $ax(Feng.ctxPath + "/term/changePwd", function (data) {
    	if(data.code == 200){
    		Feng.success("Successful password modification");
   		 	if (window.parent.MgrTerm != undefined) {
           	 UserInfoDlg.close();
               window.parent.MgrTerm.table.refresh();
   		 	}
    	}else{
    		Feng.error(data.message);
    	}
    }, function (data) {
        Feng.error(fail + " " + data.responseJSON.message + "!");
    });
   
    ajax.set("oldPwd");
    ajax.set("password");
    ajax.set("id");
    ajax.start();

};

/**
 * 
 */
function choose(){
	var a = $(".channelParam").length;
	if(a > 1){
		$(".channelParam").eq(a-1).remove();
	}
	
	var exchange = $("#exchange option:selected").val();
	var hotWallet = $("#hotWallet option:selected").val();
	//热钱包
	if(hotWallet == "" || hotWallet == null || hotWallet == 0){
		$(".walletDiv").attr("style","display:none;");//隐藏div
		$(".apiDiv").attr("style","display:none;");//隐藏div
		$(".blockchainDiv").attr("style","display:none;");//隐藏div
	}else{
		if(hotWallet == 1){
			$(".walletDiv").attr("style","display:block;");
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 2){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:block;");
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 3){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:block;");
		}
	}
	
	//交易所
	if(exchange == '' || exchange == null || exchange == 0){
		$(".proDiv").attr("style","display:none;");//隐藏div
		$(".krakenDiv").attr("style","display:none;");
		$(".exchangeParamDiv").attr("style","display:none;");
	}else{
		if(exchange == 1){
			$(".proDiv").attr("style","display:block;");
			$(".krakenDiv").attr("style","display:none;");
		}
		if(exchange == 2){
			$(".proDiv").attr("style","display:none;");
			$(".krakenDiv").attr("style","display:block;");
		}
		//交易对信息
		$(".exchangeParamDiv").attr("style","display:block;");
	}
	
}

function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
            event.target).parents("#menuContent").length > 0)) {
        UserInfoDlg.hideDeptSelectTree();
    }
}

