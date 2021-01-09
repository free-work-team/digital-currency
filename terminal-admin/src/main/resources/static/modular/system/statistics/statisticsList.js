/**
 * 系统管理--用户管理的单例对象
 */
var Statistics = {
    id: "statisticsTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Statistics.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'0px'},
        {title: 'Statistics Time', field: 'date',lanId:'statisticsTime', align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'TerminalNo', field: 'terminalNo',lanId:'terminalNo', align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'cryptoCurrency', field: 'cryptoCurrency',lanId:'cryptoCurrency', align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Coin', field: 'coin', lanId:'coin',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'currency', field: 'currency',lanId:'currency', align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Amount', field: 'cash', lanId:'amount',align: 'center', valign: 'middle', sortable: true,width:'120px',formatter: currencyUnit},
        {title: 'Fee', field: 'fee', lanId:'fillFees', align: 'center', valign: 'middle', sortable: true,width:'120px',formatter: currencyUnit},
        {title: 'Profit', field: 'profit', lanId:'profit',align: 'center', valign: 'middle', sortable: true,width:'120px',formatter: currencyUnit},
        {title: 'Count', field: 'count', lanId:'countSum',align: 'center', valign: 'middle', sortable: true,width:'120px'}
        
        ];
    return columns;
};
var currencyUnit = function (value,row,index) {
	var currency = row.currency;
	if(currency==null || currency=="" || currency =="undefined"){
		currency = "";
	}
	return value + currency;
}
/**
 * 检查是否选中
 */
Statistics.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	Statistics.seItem = selected[0];
        return true;
    }
};


Statistics.search = function () {
    var queryData = {};
    queryData['tradeType'] = $("#tradeType").val();
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['cryptoCurrency'] = $("#cryptoCurrency").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
	Statistics.table.refresh({query: queryData});
    
}



Statistics.onClickDept = function (e, treeId, treeNode) {
	Statistics.deptid = treeNode.id;
    Statistics.search();
};

Statistics.resetSearch = function () {
	$("#tradeType").val('');
	$("#terminalNo").val('');
	//$("#cryptoCurrency").val('');
	$("#beginTime").val('');
	$("#endTime").val('');
	Statistics.search();
}

$(function () {
	var beginTime = $("#beginTime").val();
	var endTime = $("#endTime").val();
	var cryptoCurrency = $("#cryptoCurrency").val();
	var obj = {};
	obj['beginTime'] = beginTime;
	obj['endTime'] = endTime;
	obj['cryptoCurrency'] = cryptoCurrency;
	var defaultColunms = Statistics.initColumn();
	
	var type = $('#tradeType').val();
	if(type == 1){
		var table = new BSTable("statisticsTable", "/tradeStatistics/buy", defaultColunms);
	    table.setPaginationType("server");
	    table.setQueryParams(obj);    //自定义传参
//		table.setOnPostBody();
	    table.onLoadSuccess = function(data){
	        $('.keep-open').css("display","none");
	       	//if(data.total !=0){
	   	        $('.fixed-table-body').next('#totalArea').remove();
	   	        $('.fixed-table-body').after('' +
	   	          '<table id="totalArea" class="table table-hover table-striped" style="width: 100%">\n' +
	   	          '  <thead>\n' +
	   	          '  <tr>\n' +
	   	          '    <th colspan="3" style="text-align: center; vertical-align: middle;width:440px;">\n' +
	   	          '      <div class="th-inner" >SUM</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:160px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.amount + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.cash.toFixed(2) + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px; ">\n' +
	   	          '      <div class=" th-inner">' + data.result.fee.toFixed(2) + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.profit.toFixed(2) + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px; ">\n' +
	   	          '      <div class=" th-inner">' + data.result.count + '</div>\n' +
	   	          '    </th>\n' +
	   	          '  </tr>\n' +
	   	          '  </thead>\n' +
	   	          '</table>');
	   	    	//}
	       };
	    Statistics.table = table.init();
	}else if(type == 2){
		var table = new BSTable("statisticsTable", "/tradeStatistics/sell", defaultColunms);
	    table.setPaginationType("server");
	    table.setQueryParams(obj);    //自定义传参
//		table.setOnPostBody();
	    table.onLoadSuccess = function(data){
	        $('.keep-open').css("display","none");
//	       	if(data.total !=0){
	       		$('.fixed-table-body').next('#totalArea').remove();
	   	        $('.fixed-table-body').after('' +
	   	          '<table id="totalArea" class="table table-hover table-striped" style="width: 100%">\n' +
	   	          '  <thead>\n' +
	   	          '  <tr>\n' +
	   	          '    <th colspan="3" style="text-align: center; vertical-align: middle;width:440px;">\n' +
	   	          '      <div class="th-inner" >SUM</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:160px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.amount + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.cash.toFixed(2) + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.fee + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.profit + '</div>\n' +
	   	          '    </th>\n' +
	   	          '    <th style="text-align: center; vertical-align: middle;width:120px;">\n' +
	   	          '      <div class=" th-inner">' + data.result.count + '</div>\n' +
	   	          '    </th>\n' +
	   	          '  </tr>\n' +
	   	          '  </thead>\n' +
	   	          '</table>');
//	   	    	}
	       };
	    Statistics.table = table.init();
	}

});
