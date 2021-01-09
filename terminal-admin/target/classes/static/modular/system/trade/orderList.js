/**
 * 系统管理--用户管理的单例对象
 */
var Order = {
    id: "orderTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**Side size funds date status 
 * 初始化表格的列
 */
Order.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'TransactionID', field: 'transId',lanId:'tranId', align: 'center', valign: 'middle', sortable: true,width:'220px'},
        {title: 'terminalNo', field: 'terminalNo', lanId:'terminalNo',align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: 'Transaction Type', field: 'side',lanId:'side',align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: 'Order Type', field: 'type',lanId:'orderType',align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: 'cryptoCurrency', field: 'cryptoCurrency', lanId:'cryptoCurrency',align: 'center', valign: 'middle', sortable: true,width:'80px'},
        {title: 'Coin', field: 'coin', lanId:'coin',align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: 'currency', field: 'currency', lanId:'currency',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Funds', field: 'funds', lanId:'funds',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Transaction Time', field: 'createTime',lanId:'transTime', align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: 'Status', field: 'transStatusDesc',lanId:'status', align: 'center', valign: 'middle', sortable: true,width:'120px'}
        /*{title: '价格', field: 'price', lanId:'rate',align: 'center', valign: 'middle', sortable: true,width:'130px'}*/
        ];
    return columns;
};

/**
 * 检查是否选中
 */
Order.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	Order.seItem = selected[0];
        return true;
    }
};

/**
 * 点击详情
 */
Order.openSellDetail = function(){
	if (this.check()) {
		var url = Feng.ctxPath + '/order/detail/' + this.seItem.transId;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('details')||'详情',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}

Order.search = function () {
    var queryData = {};
    queryData['transId'] = $("#transId").val();
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['cryptoCurrency'] = $("#cryptoCurrency").val();
    queryData['status'] = $("#status").val();
    queryData['side'] = $("#side").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
	Order.table.refresh({query: queryData});
    
}



Order.onClickDept = function (e, treeId, treeNode) {
	Order.deptid = treeNode.id;
	Order.search();
};

Order.resetSearch = function () {
	$("#transId").val('');
	$("#terminalNo").val('');
	$("#cryptoCurrency").val('');
	$("#status").val('');
	$("#side").val('');
	$("#beginTime").val('');
	$("#endTime").val('');
	Order.search();
}

$(function () {
	var beginTime = $("#beginTime").val();
	var endTime = $("#endTime").val();
	var obj = {};
	obj['beginTime'] = beginTime;
	obj['endTime'] = endTime;
    var defaultColunms = Order.initColumn();
    var table = new BSTable("orderTable", "/order", defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(obj);    //自定义传参
    /*table.onLoadSuccess = function(data){
    	var handlingFee = getSingleLanguage('handlingFee')||"手续费";
    	$('.keep-open').css("display","none");
   		$('.fixed-table-body').next('#handlingFeeSum').remove();
   		$('.fixed-table-body').after('' +
        	'<div id="handlingFeeSum" style="width:100%;float:left;">' +
    		'<div id="" style="float:right;">' +
    		'<div style="float:left;margin:5px 10px;font-size:20px;">' + handlingFee + ':</div>'+
    		'<div style="float:left;margin:5px 60px 5px 10px;font-size:20px;"> ' + data.result.handlingFeeSum + '</div>' +
    		'</div>' +
    		'</div>'
        );
    };*/
    Order.table = table.init();
    /*var ztree = new $ZTree("deptTree", "/dept/tree");
    ztree.bindOnClick(MgrUser.onClickDept);
    ztree.init();*/
});
