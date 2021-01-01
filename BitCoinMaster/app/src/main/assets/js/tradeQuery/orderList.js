var currentPage  = 1,
    maxPageNum   = 1,
    totalNum     = 0,
    everyPageNum = 10,
    resultList   = [];

/**
 * 点击查询取现流水
 */
function queryOrderLog(transId, side, startTime, endTime) {
  currentPage = 1;
  var reqData = JSON.stringify({
    "transId"   : transId,
    "side"      : side,
    "start_time": startTime,
    "end_time"  : endTime
  });
  resultList = JSON.parse(window.back.queryOrderList(reqData));
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
  if (resultList.length) {
    $.each(resultList, function (index, item) {
      if ((currentPage - 1) * everyPageNum <= index && index < currentPage * everyPageNum) {
        html += '<tr>'
          + '<td style="">' + (item.transId || "") + '</td>'
          + '<td style="width: 80px;">' + (item.side || "") + '</td>'
          + '<td style="width: 80px;">' + (item.size && item.size + 'BTC') + '</td>'
          + '<td style="width: 80px;">' + (item.funds) + '</td>'
          + '<td style="width: 100px;">' + (item.productId || "") + '</td>'
          + '<td style="width: 100px;">' + (item.createdAt || "") + '</td>'
          + '<td style="width: 80px;">' + getStatusLabel(item.status) + '</td>'
          + '<td style="width: 70px; "><a onclick="toDetail(\'' + item.transId + '\')">detail</a></td>'
          + '</tr>';
      }
    });
  } else {
    html += '<tr><td colspan = "9" rowspan="2">' + 'No data!' + '</td></tr>';
  }
  $('.content-list').html(html);
}

function getStatusLabel(status) {
  switch (status) {
    case '3':
      return 'fail';
    case '0':
      return 'create';
    case '1':
      return 'pending';
    case '2':
      return 'confirm';
    default:
      return ''
  }
}



function toDetail(id) {
  localStorage.setItem("id", id);
  window.location.href = "../../pages/tradeQuery/orderDetail.html";
}

function query() {
  var address = $("#transId").val();
  var side = $("#side").val();
  var startTime = $("#startTime").val();
  var endTime = $("#endTime").val();
  if (!startTime && endTime) {
    alert("开始时间不可为空");
    return;
  }
  if (startTime && !endTime) {
    alert("结束时间不可为空");
    return;
  }
  if (startTime > endTime) {
    alert("开始时间不可大于结束时间");
    return;
  }
  queryOrderLog(address, side, startTime, endTime);
}

function renderPage() {
  $("#page").paging({
    pageNum  : currentPage, // 当前页面
    totalNum : maxPageNum, // 总页码
    totalList: totalNum, // 记录总数量
    callback : function (num) { //回调函数
      currentPage = num;
      showList();
      $("#pageSize").val(everyPageNum);
    }
  });
  $("#pageSize").val(everyPageNum);
}
