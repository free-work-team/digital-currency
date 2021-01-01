var currentPage  = 1,
    maxPageNum   = 1,
    totalNum     = 0,
    everyPageNum = 10,
    resultList= [];
/**
 * 运行状态流水
 */
function queryRunStatusMange(status,hardwareModularName,startTime,endTime){
	var reqData = JSON.stringify({
	    "status"       : status,
	    "hardware_modular_name"          : hardwareModularName,
	    "start_time"          : startTime,
	    "end_time"          : endTime
	  });
  resultList = JSON.parse(window.back.queryRunStatusList(reqData));
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
          +'<td style="width:50px;">'+(index+1)+'</td>'
          +'<td>'+(item.hardwareModularName || "")+'</td>'
          +'<td style="width:100px;">'+getStatusLabel(item.status)+'</td>'
          +'<td style="width:200px;">'+(item.updateTime || "")+'</td>'
          // +'<td style="width:300px;">'+'<a onclick="enable(this)" >重启</a>&nbsp&nbsp&nbsp'+'<a onclick="disable(this)" >停用</a>'+'</td>'
          +'</tr>';
      }
    });
  }else{
    html += '<tr><td colspan = "9" rowspan="2">' +'No data!'+'</td></tr>';
  }
  $('.content-list').html(html);
}

function getStatusLabel(status) {
  switch (status) {
    case '-1':
      return 'fail';
    case '1':
      return 'success';
    default:
      return ''
  }
}

function query(){
	var status = $("#status").val();
	var hardwareModularName = $("#hardwareModularName").val();
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
	queryRunStatusMange(status,hardwareModularName,startTime,endTime);
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
