var InterValObj;
var curCount = 30;
var merchantObj;

function render() {
  initInterValObj();
  initMerchantInfo();
}

// 初始化自动返回定时器
function initInterValObj() {
  var homeLabel = $(".to_home_label");
  if (homeLabel) {
    homeLabel.text(curCount + "s");
    InterValObj = setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
    //timer处理函数
    function SetRemainTime() {
      if (curCount === 0) {
        clearInterval(InterValObj);
        LanguageManager.resetLanguage();
        toHome();
      } else {
        // console.log("定时返回" + curCount);
        curCount--;
        homeLabel.text(curCount + "s");
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
  }
}

render();

// 回首页
function toHome() {
  // 清除sessionstorage中的登录ID
  clearStorage();
  InterValObj && clearInterval(InterValObj);
  // 退到登陆界面
  window.location.href = "../index/start.html";
}

// 清楚缓存
function clearStorage() {
  localStorage.setItem("userWalletAddress", "");
  localStorage.setItem("buyInfo", "");
  localStorage.setItem("sellInfo", "");
}

function dispensingCount() {
  curCount = 30;
  $(".to_home_label").text(curCount + "s");
}


