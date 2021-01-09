/**
 * 初始化单例对象
 */
var CashBox = {
    id: "cashBoxTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格的列
 */
CashBox.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'0px'},
        {title: 'NO.',sortable: true, lanId:"number", align: 'center', valign: 'middle',formatter:serialFormatter,width:'60px'},
        {title: '', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: 'TerminalNo', field: 'terminalNo', lanId:"terminalNo", align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Status', field: 'statusDesc', lanId:"status", align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Create Time', field: 'create_time', lanId:"createTime", align: 'center', valign: 'middle', sortable: true,width:'160px'}
        ];
    return columns;
};
/**
 * 序号
 */
var serialFormatter = function (value,row,index) {//赋予的参数
        //return index + 1;
        var pageSize=$('.table').bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
        var pageNumber=$('.table').bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
        return pageSize * (pageNumber - 1) + index + 1;//返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
};

/**
 * 搜索
 */
CashBox.search = function () {
    var queryData = {};
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['status'] = $("#status").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    CashBox.table.refresh({query: queryData});
    
}

/**
 * 重置
 */
CashBox.resetSearch = function () {
	resetData();
	CashBox.search();
}

function resetData(){
	$("#terminalNo").val('');
	$("#status").val('');
	$("#beginTime").val('');
	$("#endTime").val('');
}

$(function () {
	var beginTime = $("#beginTime").val();
	var endTime = $("#endTime").val();
	var obj = {};
	obj['beginTime'] = beginTime;
	obj['endTime'] = endTime;
	
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = CashBox.initColumn();
    var table = new BSTable("cashBoxTable", "/cashBox?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(obj);    //自定义传参
    CashBox.table = table.init();
});
