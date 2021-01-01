var buycoinsFlag      = "buy",
    sellcoinsFlag     = "sell",
    buyFee            = "",
    userWalletAddress = "",
    buyCountAfter     = "",
    currentPriceCoinbase      = "",
    buyCash           = "";

//查询Coinbase当前市场价格
function queryMarketCoinbase(callback) {
  var url = 'https://api.coinbase.com/v2/exchange-rates?currency=BTC&&random='+randomParam(10);
  return sendAsyncGetBase(url,callback);
}

//同步查询Coinbase当前市场价格
function querySyncBalanceCoinbase() {
  var url = 'https://api.coinbase.com/v2/exchange-rates?currency=BTC&&random='+randomParam(10);
  return sendGetBase(url);
}

function sendGetBase(reqUrl) {
  var respData = "";
  var xhr = $.ajax({
    type    : "GET",
    url     : reqUrl,
    async   : false,
    timeout : 10000,
    success : function (data) {
      respData = data;
    },
    error   : function (data) {
      console.log("sendGetBase fail :" + data);
      return respData;
    },
    complete: function (XMLHttpRequest, status) {
      if (status == 'timeout') {
        xhr.abort();    // 超时后中断请求
        console.log("sendGetBase time out");
        return respData;
      }
    }
  });
  return respData;
}

function sendAsyncGetBase(reqUrl, callback) {
  var xhr = $.ajax({
    type    : "GET",
    url     : reqUrl,
    async   : true,
    timeout : 10000,
    success : function (data) {
      callback(data);
    },
    error   : function (data) {
      console.log("sendAsyncGetBase fail:" + data);
      callback("");
    },
    complete: function (XMLHttpRequest, status) {
      if (status == 'timeout') {
        xhr.abort();    // 超时后中断请求
        console.log("sendAsyncGetBase time out");
        callback("");
      }
    }
  });
}

function coinbaseQueryBalance() {
/*  console.log("BTC accountId = c130ebdd-c7fe-5df2-b302-a6d03f35c9a7");
  var localMerchant = getMerchant();
  localMerchant['accountId'] = "c130ebdd-c7fe-5df2-b302-a6d03f35c9a7";
  localStorage.setItem("merchant", JSON.stringify(localMerchant));
  console.log("BTC balance = 0.00000164");
  return "0.00000164";*/
  var account = window.coinbase.accounts();
  // console.log("查询账户信息========================" + account);
  account  = JSON.parse(account);
  if (account.errors) {
        // console.log("------------------------查询账户信息失败------------------------------------");
        return -1;
   }
   return account.balance;

}

function coinbaseBuycoins(receiveAddress, amount, fee, cash, price) {
  userWalletAddress = receiveAddress;
  buyCountAfter = amount;
  buyFee = fee;
  buyCash = cash;
  currentPriceCoinbase = price;
  window.coinbase.sendMoney(receiveAddress,numDiv(amount, 100000000).toString());
}

function coinbaseResp(result, requestType) {
  // console.log(" coinbaseCallback===============================" + requestType);
  // console.log(" coinbase===============================" + JSON.stringify(result));
  //-----------------------------------------------买币回调-------------------------------------------------------------
  if (requestType === 'sendMoney') {
    var transDateTime = getNowDate();
    if (result.errors) {
      addBuyLog("", userWalletAddress, buyCountAfter, buyFee, "", "", buyCash, transDateTime, result.errors[0].message, currentPriceCoinbase);
      ErrorPage.toError(result.errors[0].message);
      return;
    }
    console.log("------------------------买币成功------------------------------------");
    result = result.data;
    var currentTransId = result.id;
    var coinbaseFee = numMulti(result.network.transaction_fee.amount,100000000);
    // console.log("transfer.id =============================================== " + currentTransId);
    addBuyLog("", userWalletAddress, buyCountAfter, buyFee,  coinbaseFee, currentTransId, buyCash, transDateTime, "", currentPriceCoinbase);
    // 非热钱包，pro进行操作
    var localMerchant = getMerchant();
    if (localMerchant.hotWallet == '1' && buyCash) {
      //加矿工费
      var buySize = numDiv(parseInt(coinbaseFee) + parseInt(buyCountAfter), 100000000);
      window.coinbasePro.buyOrder(currentTransId, buyCash.toString(), buySize.toString(), currentPriceCoinbase, localMerchant.terminalNo);
    }
    var resultObj = {
      'date'   : transDateTime.split(" ")[0],
      'time'   : transDateTime.split(" ")[1],
      'transId': result.id,
      'amount' : numDiv(buyCountAfter, 100000000),
      'cash'   : buyCash
    };
    localStorage.setItem("buyInfo", JSON.stringify(resultObj));
    window.location.href = 'success.html';
  }

  //-------------------------------------------------监听是否生成订单回调-------------------------------------------------
  // if (requestType === 'checkTransaction') {
  //   if (result.errors) {
  //     ErrorPage.toError("Order information request failed");
  //   }
  //   var sellInfoObj = JSON.parse(localStorage.getItem("sellInfo"));
  //   console.log("------------------------卖币下单成功------------------------------------");
  //   //获取订单信息
  //   $.each(result, function (index, item) {
  //   var transId = item.id;
  //   console.log("transId:"+ transId);
  //   console.log("amount:"+ item.amount.amount);
  //     if (item.amount.amount&&(parseFloat(item.amount.amount) == sellInfoObj.amount)) {
  //       // 购买信息同步保存，用于打印
  //       sellInfoObj['transId'] = transId;
  //       localStorage.setItem("sellInfo", JSON.stringify(sellInfoObj));
  //       // 更新数据库
  //       var result = window.cdm.updateWithdrawNew(sellInfoObj.address, transId, item.id, 1, 'pending', 0, 0);
  //       if (!result){
  //         console.log("更新失败，请勿重复更新数据库!");
  //         return;
  //       }
  //       // 非热钱包，pro进行操作
  //       var localMerchant = getMerchant();
  //       if (localMerchant.hotWallet == '0') {
  //         window.coinbasePro.sellOrder(transId,parseFloat(item.amount.amount));
  //       }
  //       // 判断是否需要及时出款
  //       if (merchantObj.sellType === '2') {
  //         window.location.href = 'finish2.html?tranId='+transId;
  //       }else{
  //         window.location.href = 'finish.html';
  //       }
  //     }
  //   });
  // }
}

//创建地址
function coinbaseCreateAddress() {
  var addressResult = window.coinbase.createaddresses();
  var addressObj = JSON.parse(addressResult);
  if (addressObj.errors) {
    return '';
  }
  // console.log("coinbase创建地址：" + addressObj.data.address);
  return addressObj.data && addressObj.data.address ? addressObj.data.address : '';
}

function randomParam(n) {
  if (n > 21) return null;
  return parseInt((Math.random() + 1) * Math.pow(10,n-1))
}
