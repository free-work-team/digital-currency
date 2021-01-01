/*api接口*/
function init() {
  initMarketBalance();
}

// 获取货币种类
function getCurrency() {
  return window.business.getCurrency();
}
// 获取单双向
function getWay() {
  return window.business.getTranWay() == 2;
}

// 获取已选虚拟币种
function getCoin() {
  var currentCoin = localStorage.getItem("currentCoin");
  return currentCoin?currentCoin:'';

}

// 实时市场价格
function getBalance() {
  var currentCoin = getCoin();
  if (!currentCoin){
    ErrorPage.toError("The coin not found, Please try again later!");
    return;
  }
  var merchantObj = getMerchant();
  var newBalance = merchantObj.cryptoSettings[currentCoin].price;
  var exchangeRate = "";
  if (!newBalance) {
    var resp = JSON.parse(window.front.queryMarketPrice(currentCoin));
    if (resp.code == "00") {
      newBalance = resp.price;
      exchangeRate = resp.exchangeRate;
      newBalance && $('.toUsd').text(formatPrice(newBalance));
      localStorage.setItem("balance", newBalance);
      localStorage.setItem("exchangeRate", exchangeRate);
    } else {
      ErrorPage.toError("The price request failed, Please try again later!");
    }
  } else {
    localStorage.setItem("balance", newBalance);
    localStorage.setItem("exchangeRate", exchangeRate);
    $('.toUsd').text(formatPrice(newBalance));
  }
}

// 初始化市场价
function initMarketBalance() {
  var currentBalance = localStorage.getItem("balance");
  // 汇率赋值
  $('.currency').text(getCurrency());
  //单位
  var btcUnit = '';
  if (getCoin() == 'btc') {
    btcUnit = " BTC";
  }
  if (getCoin() == 'eth') {
    btcUnit = " ETH";
  }
  $('.btcUnit').text(btcUnit);
  if (!currentBalance) {
    getBalance();
  } else {
    $('.toUsd').text(formatPrice(currentBalance));
  }
  // // 延时执行定时
  // setTimeout(function () {
  //   setInterval(function () {
  //     console.log("刷新汇率....");
  //     getBalance();
  //   }, 30000);
  // },30000);
}

// 刷新汇率
function refreshPrice(callback) {
  var currentCoin = getCoin();
  if (!currentCoin){
    ErrorPage.toError("The coin not found, Please try again later!");
    return;
  }
  var merchantObj = getMerchant();
  var newBalance = merchantObj.cryptoSettings[currentCoin].price;
  var exchangeRate = "";
  if (!newBalance) {
    var resp = JSON.parse(window.front.queryMarketPrice(currentCoin));
    if (resp.code == "00") {
      newBalance = resp.price;
      exchangeRate = resp.exchangeRate;
      // console.log("页面之前刷新汇率:" + newBalance);
      if (newBalance) {
        $('.toUsd') && $('.toUsd').text(formatPrice(newBalance));
        localStorage.setItem("balance", newBalance);
        localStorage.setItem("exchangeRate", exchangeRate);
      }
      callback(true);
    } else {
      callback(false);

    }
  } else {
    $('.toUsd') && $('.toUsd').text(formatPrice(newBalance));
    localStorage.setItem("balance", newBalance);
    localStorage.setItem("exchangeRate", exchangeRate);
    callback(true);
  }
}

// 商户获取余额
function merchantBalanceByWalletId(callback, type) {
  var merchantObj = getMerchant();
  var balance = -1;
  resp = JSON.parse(window.front.queryBalance(type,getCoin()));

  if (resp.code == "00") {
    balance = resp.balance;
  }
  callback(balance);
}

// 用户买币
function buyCoin(cash, countAfter, fee, currentPrice,exchangeRate) {
  //  var merchantObj = getMerchant();
  var userWalletAddress = localStorage.getItem("userWalletAddress");

  window.front.buyCoins(userWalletAddress, countAfter, fee, currentPrice, cash, localStorage.getItem("customerId"),exchangeRate,getCoin());

}

// 用户卖币
function sellCoin(countAfter, bitFee, currentPrice, usdCount,exchangeRate) {
  window.front.sellCoins(countAfter, bitFee, currentPrice, usdCount, localStorage.getItem("customerId"),exchangeRate,getCoin());
}

function frontResp(result, requestType) {
  switch (requestType) {
    case "buyCoins":
      if (result.code == "00") {
        var resultObj = {
          'date'   : result.transDateTime.split(" ")[0],
          'time'   : result.transDateTime.split(" ")[1],
          'transId': result.transId,
          'amount' : numDiv(result.amount, 100000000).toString(),
          'cash'   : result.cash
        };
        localStorage.setItem("buyInfo", JSON.stringify(resultObj));
        window.location.href = 'success.html';
      } else {
        ErrorPage.toError(result.message);
      }
      break;
    case "sellCoins":
      if (result.code == "00") {
        var sellInfoObj = {
          'transId': result.transId,
          'address': result.address,
          'amount' : numDiv(result.amount, 100000000),
          'date'   : result.transDateTime.split(" ")[0],
          'time'   : result.transDateTime.split(" ")[1],
          'cash'   : result.cash
        };
        localStorage.setItem("sellInfo", JSON.stringify(sellInfoObj));
        hideWait();
        window.location.href = 'finish.html';
      } else {
        ErrorPage.toError(result.message);
      }
      break;
  }
}

// 当前汇率 美元兑换bit
function usdToBit(usdCount) {
  var currentCoin = getCoin();
  if (!currentCoin){
    ErrorPage.toError("The coin not found, Please try again later!");
    return;
  }
  var merchantObj = getMerchant();
  var newBalance = merchantObj.cryptoSettings[currentCoin].price;
  var exchangeRate = "";

  if (!newBalance) {
    var resp = JSON.parse(window.front.queryMarketPrice(currentCoin));
    if (resp.code == "00") {
      newBalance = resp.price;
      exchangeRate = resp.exchangeRate;
    } else {
      ErrorPage.toError("The price request failed, Please try again later!");
    }
  }
  if (newBalance) {
    $('.toUsd') && $('.toUsd').text(formatPrice(newBalance));
    localStorage.setItem("balance", newBalance);
    localStorage.setItem("exchangeRate", exchangeRate);
    //小数点直接进位
    var ceil = Math.ceil(numMulti((numDiv(usdCount, newBalance)), 100000000));
    return numDiv(ceil, 100000000);
  }
}

// 根据现有汇率计算
function myUsdToBit(usdCount) {
  var newBalance = localStorage.getItem("balance");
  if (!newBalance) {
    usdToBit(usdCount);
  } else {
    //小数点直接进位
    var ceil = Math.ceil(numMulti((numDiv(usdCount, newBalance)), 100000000));
    return numDiv(ceil, 100000000);
  }
}

// 当前汇率 bit兑换美元
function bitToUsd(bitCount) {
  var currentCoin = getCoin();
  if (!currentCoin){
    ErrorPage.toError("The coin not found, Please try again later!");
    return;
  }
  var merchantObj = getMerchant();
  var newBalance = merchantObj.cryptoSettings[currentCoin].price;
  var exchangeRate = "";
  if (!newBalance) {
    var resp = JSON.parse(window.front.queryMarketPrice(currentCoin));
    if (resp.code == "00") {
      newBalance = resp.price;
      exchangeRate = resp.exchangeRate;
    } else {
      ErrorPage.toError("The price request failed, Please try again later!");
    }
  }
  if (newBalance) {
    localStorage.setItem("balance", newBalance);
    localStorage.setItem("exchangeRate", exchangeRate);
    return numMulti(parseInt(bitCount), parseInt(newBalance));
  }
}


// 商户查询钱包列表
function merchantWalletList() {
  var merchantObjTem = getTemMerchant();
  return merchantObjTem.accessToken ? queryWallets(merchantObjTem.accessToken) : {};
}


// 获取商户信息
function getMerchant() {
  var localMerchant = localStorage.getItem("merchant");
  if (localMerchant) {
    return JSON.parse(localMerchant);
  }
  return null;
}

// 获取临时商户信息
function getTemMerchant() {
  var localMerchantTem = localStorage.getItem("merchantTem");
  if (localMerchantTem) {
    return JSON.parse(localMerchantTem);
  }
  return null;
}


/**
 * 加法运算，避免数据相加小数点后产生多位数和计算精度损失。
 *
 * @param num1加数1 | num2加数2
 */
function numAdd(num1, num2) {
  var baseNum, baseNum1, baseNum2;
  try {
    baseNum1 = num1.toString().split(".")[1].length;
  } catch (e) {
    baseNum1 = 0;
  }
  try {
    baseNum2 = num2.toString().split(".")[1].length;
  } catch (e) {
    baseNum2 = 0;
  }
  baseNum = Math.pow(10, Math.max(baseNum1, baseNum2));
  return (num1 * baseNum + num2 * baseNum) / baseNum;
}
/**
 * 减法运算，避免数据相减小数点后产生多位数和计算精度损失。
 *
 * @param num1被减数 | num2减数
 */
function numSub(num1, num2) {
  var baseNum, baseNum1, baseNum2;
  var precision;// 精度
  try {
    baseNum1 = num1.toString().split(".")[1].length;
  } catch (e) {
    baseNum1 = 0;
  }
  try {
    baseNum2 = num2.toString().split(".")[1].length;
  } catch (e) {
    baseNum2 = 0;
  }
  baseNum = Math.pow(10, Math.max(baseNum1, baseNum2));
  precision = (baseNum1 >= baseNum2) ? baseNum1 : baseNum2;
  return ((num1 * baseNum - num2 * baseNum) / baseNum).toFixed(precision);
}
/**
 * 乘法运算，避免数据相乘小数点后产生多位数和计算精度损失。
 *
 * @param num1被乘数 | num2乘数
 */
function numMulti(num1, num2) {
  var baseNum = 0;
  try {
    baseNum += num1.toString().split(".")[1].length;
  } catch (e) {
  }
  try {
    baseNum += num2.toString().split(".")[1].length;
  } catch (e) {
  }
  return scientificToNumber(Number(num1.toString().replace(".", "")) * Number(num2.toString().replace(".", "")) / Math.pow(10, baseNum));
}
/**
 * 除法运算，避免数据相除小数点后产生多位数和计算精度损失。
 *
 * @param num1被除数 | num2除数
 */
function numDiv(num1, num2) {
  var baseNum1 = 0, baseNum2 = 0;
  var baseNum3, baseNum4;
  try {
    baseNum1 = num1.toString().split(".")[1].length;
  } catch (e) {
    baseNum1 = 0;
  }
  try {
    baseNum2 = num2.toString().split(".")[1].length;
  } catch (e) {
    baseNum2 = 0;
  }
  with (Math) {
    baseNum3 = Number(num1.toString().replace(".", ""));
    baseNum4 = Number(num2.toString().replace(".", ""));
    return scientificToNumber((baseNum3 / baseNum4) * pow(10, baseNum2 - baseNum1));
  }
}
// 处理科学计数法显示问题
function scientificToNumber(num) {
  if (/\d+\.?\d*e[\+\-]*\d+/i.test(num)) {    //正则匹配科学计数法的数字
    var zero        = '0',                                    //
        parts       = String(num).toLowerCase().split('e'), //拆分成系数和指数
        e           = parts.pop(),//存储指数
        l           = Math.abs(e), //取绝对值，l-1就是0的个数
        sign        = e / l,          //判断正负
        coeff_array = parts[0].split('.');   //将系数按照小数点拆分
    if (sign === -1) {           //如果是小数
      num = zero + '.' + new Array(l).join(zero) + coeff_array.join('');   //拼接字符串，如果是小数，拼接0和小数点
    } else {
      var dec = coeff_array[1];
      if (dec) l = l - dec.length;  //如果是整数，将整数除第一位之外的非零数字计入位数，相应的减少0的个数
      num = coeff_array.join('') + new Array(l + 1).join(zero);    //拼接字符串，如果是整数，不需要拼接小数点
    }
  }
  return num;
}

init();
