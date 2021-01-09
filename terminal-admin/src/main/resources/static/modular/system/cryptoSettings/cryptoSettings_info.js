var inputNotEmpty            = getSingleLanguage('inputNotEmpty')            || "输入不能为空";
var virtualCurrencyNotEmpty  = getSingleLanguage('virtualCurrencyNotEmpty')  || "虚拟币不能为空";
var feeFormatLimit           = getSingleLanguage('feeFormatLimit')           || "format error";
var exchangestrategyNotNo    = getSingleLanguage('exchangestrategyNotNo')    || "exchange error";
var exchangeProWalletCoinbase= getSingleLanguage('exchangeProWalletCoinbase')|| "wallet error";
var virtualCurrencyforPair   = getSingleLanguage('virtualCurrencyforPair')   || "请选取虚拟币种对应交易对";

var CryptoSettingsInfoDlg = {
    paramInfoData: {},
    validateFields: {
    	virtualCurrency: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                }
            }
        },
        name: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                }
            }
        },
        exchangeStrategy: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                }
            }
        },
        exchange: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                callback:{
                	message: exchangestrategyNotNo,
                	callback: function(value, validator){
                    	var exchangeStrategy =$("#exchangeStrategy").val();
                        if(exchangeStrategy != 0 && value && value == 0){
                        	return false;
                        }else{
                        	return true;
                        }
                    },
                }
            }
        },
        proKey: {
            validators: {
                callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    },
                }
            }
        },
        proSecret: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    },
                }
            }
        },
        proPassphrase: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    },
                }
            }
        },
        krakenApiKey: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 2 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        krakenSecret: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 2 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        krakenWithdrawalsAddressName: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 2 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        currencyPair: {
            validators: {
                callback:{
                	message: virtualCurrencyforPair,
                	callback: function(value, validator){
                    	var virtualCurrency =$("#virtualCurrency").val();
                        if(virtualCurrency == 1 && value && (value == 1||value == 2||value == 3)){
                        	return true;
                        }else if(virtualCurrency == 2 && value && (value == 4||value == 5||value == 6||value == 7)){
                        	return true;
                        }else{
                        	return false;
                        }
                    },
                }
            }
        },
        rateSource: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                }
            }
        },
        hotWallet: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                callback:{
                	message: exchangeProWalletCoinbase,
                	callback: function(value, validator){
                    	var exchange =$("#exchange").val();
                        if(exchange == 1 && value && value != 2){
                        	return false;
                        }else{
                        	return true;
                        }
                    },
                }
            }
        },
        walletId: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        walletPassphrase: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        accessToken: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 1 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        apiKey: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 2 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        apiSecret: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 2 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        blockchainWalletId: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 3 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        blockchainPassword: {
            validators: {
            	callback:{
                	message: inputNotEmpty,
                	callback: function(value, validator){
                    	var hotWallet =$("#hotWallet").val();
                        if(hotWallet == 3 && (value.trim() == "" || value == null)){
                        	return false;
                        }else{
                        	return true;
                        }
                    }
                }
            }
        },
        /*price: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: "The format is wrong, the correct format is 0.00"
                }
            }
        },*/
        buySingleFee: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: feeFormatLimit
                }
            }
        },
        buyTransactionFee: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: feeFormatLimit
                }
            }
        },
        minNeedCash: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: feeFormatLimit
                }
            }
        },
        sellSingleFee: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: feeFormatLimit
                }
            }
        },
        sellTransactionFee: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                },
                regexp:{
                	regexp: /^(-?\d+)(\.\d{1,2})?$/,
                	message: feeFormatLimit
                }
            }
        },
        confirmations: {
            validators: {
                notEmpty: {
                    message: inputNotEmpty
                }
            }
        }
        
    }
};

/**
 * 设置对话框中的数据
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CryptoSettingsInfoDlg.set = function (key, value) {
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
            this.paramInfoData[key] = ids;
        }else{
            this.paramInfoData[key]= $("#" + key).val();
        }
    }
    return this;
};

/**
 * 设置对话框中的数据
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CryptoSettingsInfoDlg.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
CryptoSettingsInfoDlg.close = function () {
    parent.layer.close(window.parent.CryptoSettings.layerIndex);
};

/**
 * 验证数据是否为空
 */
CryptoSettingsInfoDlg.validate = function () {
    $('#cryptoSettingsInfoForm').data("bootstrapValidator").resetForm();
    $('#cryptoSettingsInfoForm').bootstrapValidator('validate');
    return $("#cryptoSettingsInfoForm").data('bootstrapValidator').isValid();
};

/**
 * 清除数据
 */
CryptoSettingsInfoDlg.clearData = function () {
    this.paramInfoData = {};
};
/**
 * 收集数据
 */
CryptoSettingsInfoDlg.collectData = function () {
    this.set('id').set('virtualCurrency').set('name').set('exchangeStrategy').set('exchange').set('proKey').set('proSecret').set('proPassphrase')
    .set('krakenApiKey').set('krakenSecret').set('krakenWithdrawalsAddressName').set('orderType').set('currencyPair')
    .set('rateSource').set('hotWallet').set('walletId').set('walletPassphrase').set('accessToken').set('apiKey').set('apiSecret')
    .set('blockchainWalletId').set('blockchainPassword').set('price').set('buySingleFee').set('buyTransactionFee').set('minNeedCash')
    .set('sellSingleFee').set('sellTransactionFee').set('confirmations');
};

/**
 * 提交添加
 */
CryptoSettingsInfoDlg.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var result = getChannelParam();
    if(result){
    	var channelParam = result;
    }else{
    	return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/cryptoSettings/add", function (data) {
    	if(data.code != 200){
    		var fail = getSingleLanguage('fail')||"失败!";
            Feng.error(fail + ", " + data.message + " !");
    	}else{
    		Feng.success(getSingleLanguage('success')||"成功!");
    	}
        window.parent.CryptoSettings.table.refresh();
        CryptoSettingsInfoDlg.close();
    }, function (data) {
    	var fail = getSingleLanguage('fail')||"失败!";
        Feng.error(fail + ", " + data.responseJSON.message + " !");
    });
    ajax.set(this.paramInfoData);
    ajax.set("channelParam",JSON.stringify(channelParam));
    ajax.start();
};
/**
 * 提交修改
 */
CryptoSettingsInfoDlg.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var result = getChannelParam();
    if(result){
    	var channelParam = result;
    }else{
    	return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/cryptoSettings/edit", function (data) {
        Feng.success(getSingleLanguage('success')||"成功!");
        window.parent.CryptoSettings.table.refresh();
        CryptoSettingsInfoDlg.close();
    }, function (data) {
    	var fail = getSingleLanguage('fail')||"失败!";
        Feng.error(fail + ", " + data.responseJSON.message + " !");
    });
    ajax.set(this.paramInfoData);
    ajax.set("channelParam",JSON.stringify(channelParam));
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
			$('.walletDiv input').val('');
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 2){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:block;");
			$('.apiDiv input').val('');
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 3){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:block;");
			$('.blockchainDiv input').val('');
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
			$('.proDiv input').val('');
			$(".krakenDiv").attr("style","display:none;");
		}
		if(exchange == 2){
			$(".proDiv").attr("style","display:none;");
			$(".krakenDiv").attr("style","display:block;");
			$('.krakenDiv input').val('');
		}
		//交易对信息
		$(".exchangeParamDiv").attr("style","display:block;");
	}
	
}
function chooseExchange(){
	var exchange = $("#exchange option:selected").val();
	if(exchange == '' || exchange == null || exchange == 0){
		$(".proDiv").attr("style","display:none;");//隐藏div
		$(".krakenDiv").attr("style","display:none;");
		$(".exchangeParamDiv").attr("style","display:none;");
	}else{
		if(exchange == 1){
			$(".proDiv").attr("style","display:block;");
			$('.proDiv input').val('');
			$(".krakenDiv").attr("style","display:none;");
		}
		if(exchange == 2){
			$(".proDiv").attr("style","display:none;");
			$(".krakenDiv").attr("style","display:block;");
			$('.krakenDiv input').val('');
		}
		//交易对信息
		$(".exchangeParamDiv").attr("style","display:block;");
	}
	
}
function chooseWallet(){
	var hotWallet = $("#hotWallet option:selected").val();
	if(hotWallet == "" || hotWallet == null || hotWallet == 0){
		$(".walletDiv").attr("style","display:none;");//隐藏div
		$(".apiDiv").attr("style","display:none;");//隐藏div
		$(".blockchainDiv").attr("style","display:none;");//隐藏div
	}else{
		if(hotWallet == 1){
			$(".walletDiv").attr("style","display:block;");
			$('.walletDiv input').val('');
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 2){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:block;");
			$('.apiDiv input').val('');
			$(".blockchainDiv").attr("style","display:none;");
		}
		if(hotWallet == 3){
			$(".walletDiv").attr("style","display:none;");
			$(".apiDiv").attr("style","display:none;");
			$(".blockchainDiv").attr("style","display:block;");
			$('.blockchainDiv input').val('');
		}
	}
}
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
$(function () {
    Feng.initValidator("cryptoSettingsInfoForm", CryptoSettingsInfoDlg.validateFields);
    
    /*bootstrap-select start*/
	//初始化selectpicker
    $('#exchangeStrategy').selectpicker();
    $(".optionmouse").each(function(index){
        //移入事件
    	$(this).mouseenter(function(obj) {
    	   $(this).attr("style", "background-color:#1E90FF");
    	   $(this).find("span:first").attr("style", "color: #fff !important");
    	});
    });
    $(".optionmouse").each(function(index){
        //移出事件
    	$(this).mouseleave(function(obj) {
    	   $(this).attr("style","background-color:#fff");
    	   $(this).find("span:first").attr("style", "color: #777F83 !important");
    	});
    });
    /*bootstrap-select end*/
    
    $("#hotWallet").on("change",function(){
    	chooseWallet();
    });
    $("#exchange").on("change",function(){
    	chooseExchange();
    });
});