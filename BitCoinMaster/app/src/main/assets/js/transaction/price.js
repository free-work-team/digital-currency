//格式化价格为1000.00保留两位小数
function formatPrice(value) {
  // console.log("格式化:" + value);
  if (!value) {
    value = '0';
  }
  value = Math.round(parseFloat(value) * 100) / 100;
  var xsd = value.toString().split(".");
  if (xsd.length === 1) {
    value = value.toString() + ".00";
    return value+" ";
  }
  if (xsd.length > 1) {
    if (xsd[1].length < 2) {
      value = value.toString() + "0";
    }
    return value+" ";
  }
}

// console.log(formatPrice('4654465.45446'));

//格式化bitcoin 聪为单位 的1,000,000 仅适用于整数
function formatSat(n) {
  // console.log("格式化:" + n);
  if (!n) {
    n = '0';
  }
  n = parseInt(n).toString();
  var re = /(\d{1,3})(?=(\d{3})+(?:$|\.))/g;
  var n1 = n.replace(re, "$1,");
  return n1+" ";
}
// 格式化虚拟币
function formatCoin(num1) {
  num1 = num1||0;
  var num2 = 100000000;
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
// console.log(formatSat('4654465.45446'));
