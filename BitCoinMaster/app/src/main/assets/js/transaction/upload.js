var InterValObj;
var merchantObj;
var curCount = 300;

function render() {
  initInterValObj();
  initMerchantInfo();
}

function event() {
}

// 初始化自动返回定时器
function initInterValObj() {
  var homeLabel = $(".to_home_label");
  if (homeLabel) {
    homeLabel.text(curCount + " S");
    InterValObj = setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
    //timer处理函数
    function SetRemainTime() {
      if (curCount === 0) {
        clearInterval(InterValObj);
        LanguageManager.resetLanguage();
        toHome(true);
      } else {
        // console.log("定时返回"+curCount);
        curCount--;
        homeLabel.text(curCount + " S");
      }
    }
  }
}

function initMerchantInfo() {
  var localMerchant = localStorage.getItem("merchant");
  if (localMerchant) {
    merchantObj = JSON.parse(localMerchant);
    $(".merchant_hotLine").text(merchantObj.hotline);
    $(".merchant_mail").text(merchantObj.email);
    // $(".merchant_buyTransactionFee").text(merchantObj.buyTransactionFee+" %");
    // $(".merchant_sellTransactionFee").text(merchantObj.sellTransactionFee+" %");
  }
}

render();

// 回首页
function toHome(isStart) {
  console.log("-----------------------------------tohome");
  // 清除sessionstorage中的登录ID
  clearStorage();
  InterValObj && clearInterval(InterValObj);
  window.face.stopCompare();
  // 退到登陆界面
  if (isStart){
    window.location.href = "../index/start.html";
  }else{
    window.location.href = "../common/switch.html";
  }
}

// 清楚缓存
function clearStorage() {
  localStorage.setItem("userWalletAddress", "");
  localStorage.setItem("buyInfo", "");
  localStorage.setItem("sellInfo", "");
  localStorage.setItem("balance", '');
}

// 获取 日期 时间
function getNowDate() {
  var date = new Date();
  var sign1 = "-";
  var sign2 = ":";
  var year = date.getFullYear(); // 年
  var month = date.getMonth() + 1; // 月
  var day = date.getDate(); // 日
  var hour = date.getHours(); // 时
  var minutes = date.getMinutes(); // 分
  var seconds = date.getSeconds(); //秒
  // 给一位数数据前面加 “0”
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }
  if (day >= 0 && day <= 9) {
    day = "0" + day;
  }
  if (hour >= 0 && hour <= 9) {
    hour = "0" + hour;
  }
  if (minutes >= 0 && minutes <= 9) {
    minutes = "0" + minutes;
  }
  if (seconds >= 0 && seconds <= 9) {
    seconds = "0" + seconds;
  }
  return year + sign1 + month + sign1 + day + " " + hour + sign2 + minutes + sign2 + seconds;
}

// 加载中
function waiting(callback) {
  // 取消倒计时回home
  $(".to_home_btn").addClass("hidden");
  curCount = 120;
  // InterValObj && clearInterval(InterValObj);
  // 替换背景为等待中
  $("#waiting_background").addClass("bic");
  $("#waiting_context").html("<div class=\"top-little-title\" style='width: 100%;'>\n" +
    "  <div>" + (localStorage.getItem('LanguageType') === 'Chinese' ? '处理中...' : 'Processing...') + "</div>\n" +
    "</div>");

  // InterValObj&& clearInterval(InterValObj);
  setTimeout(function () {
    callback()
  }, 500);
}


// 生成id
function generateID(length) {
  return Number(Math.random().toString().substr(3, length) + Date.now()).toString(36);
}



