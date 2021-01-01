var currentPage  = 1,
    maxPageNum   = 1,
    totalNum     = 0,
    everyPageNum = 10,
    resultList= [];

/**
 * 点击查询取现流水
 */
function queryBuyLog(address,status, startTime, endTime) {
  currentPage  = 1;
  var reqData = JSON.stringify({
    "address"   : address,
    "status"    : status,
    "start_time": startTime,
    "end_time"  : endTime
  });
  resultList = JSON.parse(window.back.queryBuyLogList(reqData));
  // console.log("查询购买记录=====" + JSON.stringify(resultList));
  everyPageNum = parseInt($("#pageSize option:selected").val());

  totalNum = resultList.length;
  maxPageNum = parseInt(totalNum / everyPageNum) + (totalNum % everyPageNum > 0 ? 1 : 0);

  //加载列表数据
  showList();
  renderPage();
}

// 渲染列表
function showList() {
  var html = "";
  if(resultList.length){
    $.each(resultList, function (index, item) {
      if ((currentPage-1)*everyPageNum<=index&& index<currentPage*everyPageNum ) {
        html += '<tr>'
          + '<td style="width: 220px;">' + (item.address || "") + '</td>'
          + '<td style="width: 100px;">' + (formatSat(item.amount)) + '</td>'
          + '<td style="width: 80px;">' + (formatSat(item.fee)) + '</td>'
          + '<td style="width: 80px;">' + ((formatPrice(item.cash)) + item.currency) + '</td>'
          + '<td style="width: 140px;">' + (item.transTime || "") + '</td>'
          + '<td style="width: 100px;">' + (item.status || "") + '</td>'
          + '<td style="width: 70px; "><a onclick="toDetail(' + item.id + ')" >detail</a></td>'
          + '</tr>';
      }
    });
  }else{
    html += '<tr><td colspan = "9" rowspan="2">' +'No data!'+'</td></tr>';
  }
  $('.content-list').html(html);
}
function toDetail(id){
  localStorage.setItem("id",id);
  window.location.href="../../pages/tradeQuery/buyDetail.html";
}

function query(){
  var address = $("#address").val();
  var status = $("#status").val();
  var startTime = $("#startTime").val();
  var endTime = $("#endTime").val();
  if(!startTime&&endTime){
    alert("开始时间不可为空");
    return;
  }
  if (startTime&&!endTime) {
    alert("结束时间不可为空");
    return;
  }
  if(startTime > endTime){
    alert("开始时间不可大于结束时间");
    return;
  }
  queryBuyLog(address,status,startTime,endTime);
}

function renderPage() {
  $("#page").paging({
    pageNum: currentPage, // 当前页面
    totalNum: maxPageNum, // 总页码
    totalList: totalNum, // 记录总数量
    callback: function (num) { //回调函数
      currentPage= num;
      showList();
      $("#pageSize").val(everyPageNum);
    }
  });
  $("#pageSize").val(everyPageNum);
}
