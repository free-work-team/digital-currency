/**
 * 系统管理--用户管理的单例对象
 */
var RunManage = {
    id: "runManageTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格的列
 */
RunManage.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '终端号', field: 'terminalNo', lanId:"terminalNo",align: 'center', valign: 'middle', sortable: true},
        {title: '硬件模块名称', field: 'deviceName', lanId:"deviceName",align: 'center', valign: 'middle', sortable: true},
        {title: '运行状态', field: 'statusDesc',lanId:"runningStatus", align: 'center', valign: 'middle', sortable: true},
        {title: '更新时间', field: 'uploadTime',lanId:"updateTime",align: 'center', valign: 'middle', sortable: true},
        {title: '描述', field: 'desc', lanId:"remark",align: 'center', valign: 'middle', sortable: true}
        ];
    return columns;
};

RunManage.search = function () {
    var queryData = {};
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['deviceName'] = $("#deviceName").val();
    queryData['status'] = $("#status").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    if(validateDate($("#beginTime").val(),$("#endTime").val()) == false ){
		return false;
	}else{
		RunManage.table.refresh({query: queryData});
	}
    
}



RunManage.onClickDept = function (e, treeId, treeNode) {
	RunManage.deptid = treeNode.id;
	RunManage.search();
};

RunManage.resetSearch = function () {
	/*resetData();
	RunManage.search();*/
	window.location.reload();
}

$(function () {
    var defaultColunms = RunManage.initColumn();
    var table = new BSTable("runManageTable", "/device/list", defaultColunms);
    table.setPaginationType("server");
    RunManage.table = table.init();
});
