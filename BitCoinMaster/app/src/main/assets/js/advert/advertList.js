var currentPage  = 1,
    maxPageNum   = 1,
    totalNum     = 0,
    everyPageNum = 10,
    resultList= [];
/**
 * 广告列表
 */
function queryAdvertList(startTime,endTime){
	var reqData = JSON.stringify({
	    "start_time"       : startTime,
	    "end_time"          : endTime
	  });
  resultList = JSON.parse(window.back.queryAdvertList(reqData));
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
          +'<td style="width:200px;">'+(item.address || "")+'</td>'
          +'<td style="width:150px;">'+(item.termOfValidity || "")+'</td>'
          +'<td style="width:150px;">'+(item.uploadTime || "")+'</td>'
          +'<td style="width:250px;">'+'<a onclick="add(this)" >新增</a>&nbsp&nbsp&nbsp'+'<a onclick="query(this)" >查看</a>&nbsp&nbsp&nbsp'+'<a onclick="disable(this)" >停用</a>&nbsp&nbsp&nbsp'+'<a onclick="delate(this)" >删除</a>'+'</td>'
          +'</tr>';
      }
    });
  }else{
    html += '<tr><td colspan = "9" rowspan="2">' +'No data!'+'</td></tr>';
  }
  $('.content-list').html(html);
}




function query(){
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
	queryAdvertList(startTime,endTime);
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
