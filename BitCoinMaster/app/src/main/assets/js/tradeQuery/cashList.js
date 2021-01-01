var currentPage  = 1,
    maxPageNum   = 1,
    totalNum     = 0,
    everyPageNum = 10,
    resultList= [];
/**
 * 点击查询取现流水
 */
function queryWithdraw(transId, startTime, endTime) {
  currentPage  = 1;
  var reqData = JSON.stringify({
    "trans_id"  : transId,
    "start_time": startTime,
    "end_time"  : endTime
  });
  resultList = JSON.parse(window.back.queryWithdrawLogList(reqData));
  // console.log("查询取现记录=====" + JSON.stringify(resultList));
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
          /*+'<td style="width: 220px;">'+(item.transId || "")+'</td>'*/
          +'<td>'+(item.targetAddress || "")+'</td>'
          +'<td style="width: 80px;">'+((formatPrice(item.cash)) + item.currency)+'</td>'
          +'<td style="width: 70px;">'+(formatSat(item.amount))+'</td>'
          +'<td style="width: 70px;">'+(formatSat(item.fee))+'</td>'
          +'<td style="width: 70px;">'+(item.redeemStatus? "YES":"NO")+'</td>'
          +'<td style="width: 130px;">'+(item.transTime || "")+'</td>'
          +'<td style="width: 100px;">'+getStatusLabel(item.transStatus)+'</td>'
          + '<td style="width: 70px; "><a onclick="toDetail(' + item.id + ')" >detail</a></td>'
          +'</tr>';
      }
		});
	}else{
		html += '<tr><td colspan = "9" rowspan="2">' +'No data!'+'</td></tr>';
	}
	$('.content-list').html(html);
}

function toDetail(id){
  localStorage.setItem("id",id);
  window.location.href="../../pages/tradeQuery/cashDetail.html";
}

function getStatusLabel(status) {
  switch (status) {
    case 0:
      return 'init';
    case 1:
      return 'pending';
    case 2:
      return 'confirm';
    default:
      return ''
  }
}

function query(){
	var transId = $("#transId").val();
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
	queryWithdraw(transId,startTime,endTime);
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
