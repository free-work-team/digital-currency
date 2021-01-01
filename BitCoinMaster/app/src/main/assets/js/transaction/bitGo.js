//var baseUrl = 'https://test.bitgo.com/api';  //测试
// 注意:后端扫描结果处理 bitgo地址也要改
var baseUrl = 'https://www.bitgo.com/api'; //生产
// var serverUrl = 'http://192.168.1.158:3080/api';  //服务器
//var serverUrl = 'http://rh18467772.51mypc.cn/api';  //服务器
var serverUrl = 'http://localhost:3080/api';  //服务器
// var serverUrl = 'http://192.168.2.185:3080/api';  //ssb
//var coin = 'tbtc';//测试比特币
var coin = 'btc';//生产比特币
var buycoinsFlag = "buy",
    sellcoinsFlag = "sell",
    checkTransactionFlag = "checkTransaction",
    userWalletAddress = "",
    buyCountAfter = "",
    buyFee = "",
    buyCash = "",
    merchantWalletAddress= "",
    sellCountAfter= "",
    sellFee= "",
    currentPriceBitgo = "",
    sellCash= "";




//同步查询当前市场价格
function querySyncBalance() {
  var url = baseUrl + '/v1/market/latest?random='+randomParam(10);
  return sendGetBit(url,"");
}
//查询当前市场价格
function queryMarketBalance(callback) {
  var url = baseUrl + '/v1/market/latest?random='+randomParam(10);
  return sendAsyncGetBit(url,"",callback);
}


//查询钱包列表
function queryWallets(token) {
  var url = baseUrl + '/v2/' + coin + '/wallet';
  return sendRequest("", url,token, true);
}

//获取钱包策略
function queryWalletPolicy(walletId,token) {
  var url = baseUrl + '/v1/wallet/' + walletId + '/policy';
  return sendRequest("", url,token, true);
}

//获取钱包策略状态
function queryWalletPolicyStatus(walletId,token) {
  var url = baseUrl + '/v1/wallet/' + walletId + '/policy/status';
  return sendRequest("", url,token, true);
}

//获取钱包地址
function queryWalletAddress(walletId,token) {
  var url = baseUrl + '/v2/' + coin + '/wallet/' + walletId + '/addresses';
  return sendRequest("", url,token, true);
}
//地址查余额
function queryBalanceByAddress(address,token) {
  var url = baseUrl + '/v1/address/' + address;
  return sendRequest("", url,token, true);
}

//地址查钱包
function queryWalletByAddress(address,token) {
  var url = baseUrl + '/v2/' + coin + '/wallet/address/' + address;
  return sendRequest("", url,token, true);
}

//地址查交易明细
function checkTransaction(token,address,amount) {
  var url = baseUrl + '/v1/address/' + address + '/tx';
  window.bitgo.checkTransaction( url, checkTransactionFlag,address,amount);
}

//订单号查交易明细
function queryTxByid(txid) {
  var url = baseUrl + '/v1/tx/' + txid;
  return sendRequest("", url,"",true);
}


//查询钱包余额
function queryBalance(walletId, token) {
  var url = baseUrl + '/v2/' + coin + '/wallet/' + walletId;
  var respDataObj = sendRequest("", url, token, true);
  if (respDataObj.error) {
    return -1;
  }
  return respDataObj;
}

//列出钱包交易记录
function queryTransfer(walletId,token) {
  var url = baseUrl + '/v2/' + coin + '/wallet/' + walletId + '/transfer';
  return sendRequest("", url,token, true);
}



//交易(用户买币：商家的walletId,商家的walletPassphrase,商家的token,用户的address)
function buycoins(walletId, receiveAddress, amount,token, fee, cash, price) {
  userWalletAddress = receiveAddress;
  buyCountAfter = amount;
  buyFee = fee;
  buyCash = cash;
  currentPriceBitgo = price;


  var url = serverUrl+'/v2/' + coin + '/wallet/' + walletId + '/sendcoins';

  window.bitgo.buycoins(reqData, url, false, buycoinsFlag);
}



function bitgoResp(result,requestType) {
  // console.log("bitgoResp===============================" +JSON.stringify(result) );
  // console.log("requestType===============================" +requestType);

  if (result.error) {
    switch (requestType) {
        case buycoinsFlag:
            console.log("------------------------买币失败------------------------------------");
            var transDateTime = getNowDate();
            addBuyLog("", userWalletAddress, buyCountAfter, buyFee, "", "", buyCash, transDateTime, result.error, currentPriceBitgo);
          break;
        // case sellcoinsFlag:
        //     console.log("------------------------卖币失败------------------------------------");
        //     var transDateTime = getNowDate();
        //     addWithdrawLog("", merchantWalletAddress, sellCountAfter, sellFee, "","", sellCash, transDateTime);
        //   break;
      }
    ErrorPage.toError(result.error);
  }
  switch (requestType) {
    case buycoinsFlag:
      if (result.status === "signed") {
        console.log("------------------------买币成功------------------------------------");
        // console.log("result.transfer.id =============================================== " + result.transfer.id);
        // console.log("result.transfer.txid ================================================ " + result.transfer.txid);
        var transDateTime = getNowDate();
        addBuyLog(result.transfer.id, userWalletAddress, buyCountAfter, buyFee, result.transfer.feeString, result.transfer.txid, buyCash, transDateTime, "", currentPriceBitgo);
        var resultObj = {
          'date'   : transDateTime.split(" ")[0],
          'time'   : transDateTime.split(" ")[1],
          'transId': result.transfer.txid,
          'amount' : numDiv(buyCountAfter, 100000000).toString(),
          'cash'   : buyCash
        };
        localStorage.setItem("buyInfo", JSON.stringify(resultObj));
        window.location.href = 'success.html';
      }
      break;
    // case checkTransactionFlag:
    //   var sellInfoObj = JSON.parse(localStorage.getItem("sellInfo"));
    //   if (result.errors) {
    //     ErrorPage.toError("Order information request failed");
    //   }
    //   console.log("卖币下单成功 ================================================ " + JSON.stringify(result));
    //   // 购买信息同步保存，用于打印
    //   sellInfoObj['transId'] = result.transId;
    //   localStorage.setItem("sellInfo", JSON.stringify(sellInfoObj));
    //   // 更新数据库
    //   var result =window.cdm.updateWithdrawNew(sellInfoObj.address, result.transId, '', 1, 'pending', 0, 0);
    //   if (!result){
    //     console.log("更新失败，请勿重复更新数据库!");
    //     return;
    //   }
    //   // 判断是否需要及时出款
    //   if (merchantObj.sellType === '2') {
    //     window.location.href = 'finish2.html?tranId='+result.transId;
    //   }else{
    //     window.location.href = 'finish.html';
    //   }
    //   break;

  }
}

//创建地址
function createAddress(walletId, token) {
  var url = baseUrl + '/v2/' + coin + '/wallet/' + walletId + '/address';
  var reqData = JSON.stringify({
    "chain": 0,
    "label": getNowDate()+"Address",
  });
  var addressResult = sendRequest(reqData, url, token, false);
  return addressResult.address || '';
}

//consolidateunspents
function consolidateunspents(walletId, walletPassphrase, token) {
  var url = 'http://192.168.1.158:3080/api/v2/' + coin + '/wallet/' + walletId + '/consolidateunspents';
  var reqData = JSON.stringify({
    "walletPassphrase" : walletPassphrase,
    "numUnspentsToMake": 5
  });
  return sendRequest(reqData, url, token, false);
}

//同步请求后台！
function sendRequest(reqData,url,token,isGet) {
  var respData = window.bitgo.connectBitGo(reqData, url, isGet);
  var respDataObj = JSON.parse(respData);
  // console.log("bitgo 返回==="+respData);
  if (respDataObj.error) {
    // ErrorPage.toError(respDataObj.error);
    return respDataObj;
  }
  return respDataObj;
}
//异步请求后台！（回调bitgoResp方法）
function sendAsynRequest(reqData,url,token,isGet){
  window.bitgo.connectAsynBitGo(reqData, url, isGet);
}

function sendAsyncGetBit(reqUrl, token, callback) {
  var xhr = $.ajax({
    type    : "GET",
    url     : reqUrl,
    async   : true,
    timeout : 10000,
    headers : {'Authorization': 'Bearer ' + token},
    success : function (data) {
      callback(data);
    },
    error   : function (data) {
      console.log("sendAsyncGetBit error:" + data);
      callback("");
    },
    complete: function (XMLHttpRequest, status) {
      if (status == 'timeout') {
        xhr.abort();    // 超时后中断请求
        console.log("sendAsyncGetBit time out");
        callback("");
      }
    }
  });
}


function sendGetBit(reqUrl, token) {
  var respData = "";
  var xhr = $.ajax({
    type    : "GET",
    url     : reqUrl,
    async   : false,
    headers : {'Authorization': 'Bearer ' + token},
    timeout : 10000,
    success : function (data) {
      respData = data;
    },
    error   : function (data) {
      console.log("sendGetBit error:" + data);
      return "";
    },
    complete: function (XMLHttpRequest, status) {
      if (status == 'timeout') {
        xhr.abort();    // 超时后中断请求
        console.log("sendGetBit time out");
        return "";
      }
    }
  });
  return respData;
}


function queryWithdrawExit(transId) {
  // var reqData = JSON.stringify({
  //   "trans_id": transId,
  //   "tx_id"   : tx_id
  // });
  return window.back.queryWithdrawEixst(transId);
}

// function updateWithdraw(transId, tx_id, remark, status) {
//   var reqData = JSON.stringify({
//     "trans_id": transId,
//     "tx_id"   : tx_id,
//     "status"  : status,
//     "remark"  : remark
//   });
//   window.cdm.updateWithdraw(reqData);
// }

function randomParam(n) {
  if (n > 21) return null;
  return parseInt((Math.random() + 1) * Math.pow(10,n-1))
}

