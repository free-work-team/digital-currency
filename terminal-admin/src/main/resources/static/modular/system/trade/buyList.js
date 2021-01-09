/**
 * 系统管理--用户管理的单例对象
 */
var Buy = {
    id: "buyTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格的列
 */
Buy.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: 'TransId', field: 'transId',lanId:'tranId', align: 'center', valign: 'middle', sortable: true, width:'190px'},
        {title: 'TerminalNo', field: 'terminalNo',lanId:'terminalNo', align: 'center', valign: 'middle', sortable: true, width:'120px'},
        {title: 'Exchange', field: 'exchange', lanId:'exchange',align: 'center', valign: 'middle', sortable: true,formatter: operateFormatter,width:'130px'},
        {title: 'HotWallet', field: 'channelDesc',lanId:'hotWallet',align: 'center', valign: 'middle', sortable: true,width:'130px'},
        {title: 'cryptoCurrency', field: 'cryptoCurrency',lanId:'cryptoCurrency', align: 'center', valign: 'middle', sortable: true, width:'80px'},
        /*{title: '钱包ID', field: 'wallateId', align: 'center', valign: 'middle', sortable: true},*/
			  {title: 'Coin', field: 'coin', lanId:'coin',align: 'center', valign: 'middle', sortable: true, width:'150px'},
			  {title: 'Currency', field: 'currency', lanId:'currency',align: 'center', valign: 'middle', sortable: true, width:'100px'},
			  {title: 'Amount', field: 'cash',lanId:'amount',align: 'center', valign: 'middle', sortable: true, width:'100px'},
        /*{title: '平台手续费(聪)', field: 'channelFee',  lanId:'cFee',align: 'center', valign: 'middle', sortable: true},*/
        {title: 'Fee', field: 'fee', lanId:'btcFee', align: 'center', valign: 'middle', sortable: true, width:'110px',formatter: calculation},
        {title: 'Price', field: 'priceDesc',lanId:'price',align: 'center', valign: 'middle', sortable: true, width:'120px'},
        {title: 'TransTime', field: 'transTime', lanId:'transTime',align: 'center', valign: 'middle', sortable: true, width:'160px'},
        {title: 'Status', field: 'status', lanId:'status',align: 'center', valign: 'middle', sortable: true, width:'100px'}
        /*{title: 'Remark', field: 'remark',lanId:'remark',align: 'center', valign: 'middle', sortable: true, width:'120px',formatter: remark}*/

        ];
    return columns;
};

/**
 * 操作跳转 
 */
var operateFormatter = function (value,row,index) {//赋予的参数
	if(row.transId && row.strategy != "NO"){
		return  '<a href="javascript:void(0);"onclick="Buy.openOrderDetail(\''+ row.transId + '\')">'+row.strategy+'</a>&nbsp&nbsp';
	}
}
//手续费计算
var calculation = function (value,row,index) {
	var fee = row.fee;
	var price = row.price;
	var currency = row.currency;
	var ca_fee = numMulti(numDiv(fee,100000000),price);
	return ca_fee.toFixed(2) ;
}
//remark
var remark = function (value,row,index) {
	if(row.remark != "" && row.remark != null && row.remark != 'undefined'){
		return  '<span style="color:red;">'+row.remark+'</span>&nbsp&nbsp';
	}else{
		return "-";
	}
}
/**
 * 交易所详情
 */
Buy.openOrderDetail = function(transId){
	/*var url = Feng.ctxPath + '/order/detail/' + transId;
	var area = openWidthHeight("60%","98%");
    var index = layer.open({
        type: 2,
        title: getSingleLanguage('details')||'详情',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        content: url
    });
    this.layerIndex = index;*/
	var ajax = new $ax(Feng.ctxPath + "/order/detailEmpty/" + transId, function (data) {
		if(data.code == 500){
			Feng.error("Exchange information does not exist");
    	}else{
    		var url = Feng.ctxPath + '/order/detail/' + transId;
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
	}, function (data) {
        Feng.error(fail + " " + data.message + " !");
    });
    ajax.start();
}
/**
 * 检查是否选中
 */
Buy.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	Buy.seItem = selected[0];
        return true;
    }
};

/**
 * 点击详情
 */
Buy.openBuyDetail = function(){
	if (this.check()) {
		var currentType = sessionStorage.getItem('currentType');
		var url = Feng.ctxPath + '/buy/detail/' + this.seItem.id + '/' + currentType;
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

Buy.search = function () {
    var queryData = {};
    queryData['transId'] = $("#transId").val();
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['cryptoCurrency'] = $("#cryptoCurrency").val();
    queryData['status'] = $("#status").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
	Buy.table.refresh({query: queryData});
    
}



Buy.onClickDept = function (e, treeId, treeNode) {
	Buy.deptid = treeNode.id;
    Buy.search();
};

Buy.resetSearch = function () {
	$("#transId").val('');
	$("#terminalNo").val('');
	$("#cryptoCurrency").val('');
	$("#status").val('');
	$("#beginTime").val('');
	$("#endTime").val('');
	Buy.search();
}

$(function () {
	var beginTime = $("#beginTime").val();
	var endTime = $("#endTime").val();
	var obj = {};
	obj['beginTime'] = beginTime;
	obj['endTime'] = endTime;
	//var jsonStr = JSON.stringify(obj);
	
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = Buy.initColumn();
    var table = new BSTable("buyTable", "/buy?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(obj);    //自定义传参
    Buy.table = table.init();
    /*var ztree = new $ZTree("deptTree", "/dept/tree");
    ztree.bindOnClick(MgrUser.onClickDept);
    ztree.init();*/
});
