/**
 * 初始化单例对象
 */
var Customer = {
    id: "customerTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格的列
 */
Customer.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'36px'},
        {title: 'NO.',sortable: true, lanId:"number", align: 'center', valign: 'middle',formatter:serialFormatter,width:'60px'},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        /*{title: 'Status', field: 'status', visible: false, align: 'center', valign: 'middle',width:'120px'},*/
        {title: 'Name', field: 'cust_name', lanId:"custName", align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Email', field: 'e_mail', lanId:"email", align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'Status', field: 'statusDesc', lanId:"status", align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Card Type', field: 'cardTypeDesc', lanId:"cardType", align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Create User', field: 'create_user', lanId:"createUser", align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Create Time', field: 'create_time', lanId:"createTime", align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'Update User', field: 'update_user', lanId:"updateUser", align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Update Time', field: 'update_time', lanId:"updateTime", align: 'center', valign: 'middle', sortable: true,width:'160px'}
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
 * 点击详情
 */
Customer.details = function(){
	if (this.check()) {
		var url = Feng.ctxPath + '/customer/details/' + this.seItem.id;
		var area = openWidthHeight("98%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('detailBtn')||'详情',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}

/**
 * 检查是否选中
 */
Customer.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	Customer.seItem = selected[0];
        return true;
    }
};

/**
 * 搜索
 */
Customer.search = function () {
    var queryData = {};
    queryData['email'] = $("#email").val();
    queryData['status'] = $("#status").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    queryData['cardType'] = $("#cardType").val();
	Customer.table.refresh({query: queryData});
    
}

/**
 * 重置
 */
Customer.resetSearch = function () {
	resetData();
	Customer.search();
}

function resetData(){
	$("#email").val('');
	$("#status").val('');
	$("#cardType").val('');
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
    var defaultColunms = Customer.initColumn();
    var table = new BSTable("customerTable", "/customer?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(obj);    //自定义传参
    Customer.table = table.init();
});
