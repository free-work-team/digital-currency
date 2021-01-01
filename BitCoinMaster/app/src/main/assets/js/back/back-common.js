// 回首页
function toHome() {
  // 清除sessionstorage中的登录ID
  clearStorage();
  // 退到登陆界面
  // window.billAcceptor.closeDevice();
  window.location.href = "init.html";
}

// 清楚缓存
function clearStorage() {
  localStorage.setItem("user", "");
  localStorage.setItem("merchantTem", "");
}


//返回首页
function goIndex() {
  window.location.href = "../index.html";
}
// 获取商户信息
function getMerchant() {
  var localMerchant = localStorage.getItem("merchant");
  if (localMerchant) {
    return JSON.parse(localMerchant);
  }
  return null;
}

// 获取货币种类
function getCurrency() {
  var getCurrency = window.business.getCurrency();
  return getCurrency || '';
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


