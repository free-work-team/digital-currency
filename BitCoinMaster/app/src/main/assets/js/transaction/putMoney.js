  var InterValObj;
  var curCount = 60;
  function render() {
    initInterValObj();
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
          curCount--;
          homeLabel.text(curCount + "s");
        }
      }
    }
  }

  function resetInterVal() {
    curCount = 60;
  }
// 回首页
  function toHome() {
    // 清楚缓存
    localStorage.setItem("userWalletAddress", "");
    localStorage.setItem("buyInfo", "");
    localStorage.setItem("sellInfo", "");
    //拒钞
    window.billAcceptor.setEscrowAction(false);
    hideWait();
    showWait("Wait a moment");
    setTimeout(function () {
      window.billAcceptor.setDeviceEnable(false);
      InterValObj && clearInterval(InterValObj);
      window.location.href = "../index/start.html";
    }, 1500);
  }

// 获取 日期 时间
function getNowDate() {
  var date = new Date();
  var sign1 = "-";
  var sign2 = ":";
  var year = date.getFullYear(); // 年
  var month = date.getMonth() + 1; // 月
  var day  = date.getDate(); // 日
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
  initInterValObj();
  // 取消倒计时回home
  $(".to_home_btn").addClass("hidden");
  curCount = 120;
  // 替换背景为等待中
  $("#waiting_background").addClass("bic");
  $("#waiting_context").html("<div class=\"top-little-title\">\n" +
    "  <div>"+ (localStorage.getItem('LanguageType')=== 'Chinese'?'处理中...':'Processing...')+"</div>\n" +
    "</div>");

  // InterValObj&& clearInterval(InterValObj);
  setTimeout(function () {
    callback()
  }, 500);
}

  function destroyTimer() {
    var homeLabel = $(".to_home_label");
    if (homeLabel) {
      homeLabel.text("");
    }
    InterValObj && clearInterval(InterValObj);
  }

render();

