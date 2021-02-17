/**
 * 系统管理--代理商的单例对象
 */
var MgrTerm = {
    id: "managerTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
MgrTerm.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'30px'},
        {title: 'NO.',sortable: false, lanId:'number', align: 'center', valign: 'middle',formatter:serialFormatter,width:'60px'},
        {title: '', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: 'agencyName', field: 'agency_name', lanId:'agencyName', align: 'center', valign: 'middle',width:'130px'},
        {title: 'parentId', field: 'parent_id', lanId:'parentId', align: 'center', valign: 'middle',width:'130px'},
        {title: 'isParent', field: 'is_parent', lanId:'isParent', align: 'center', valign: 'middle',width:'130px'},
        {title: 'agencySingleFee', field: 'agency_single_fee', lanId:'agencySingleFee',align: 'center', valign: 'middle',width:'130px'},
        {title: 'agencyFee(%)', field: 'agency_fee',lanId:'agencyFee', align: 'center', valign: 'middle',width:'130px'},
        {title: 'agencyPhone', field: 'agency_phone', lanId:'agencyPhone',align: 'center', valign: 'middle',width:'130px'},
        {title: 'agencyStatue', field: 'agency_statue', lanId:'agencyStatue',align: 'center', valign: 'middle',width:'130px'},
        {title: 'createDate', field: 'create_date',lanId:'createDate', align: 'center', valign: 'middle',width:'160px'}
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
 * 操作跳转 
 */
var operateFormatter = function (value,row,index) {//赋予的参数
	if(row.exchange == "NO"){
		return  '-';
	}else{
		return row.exchange;
	}
}
/**
 * 搜索
 */
MgrTerm.search = function () {
    var queryData = {};
    queryData['agencyName'] = $("#agencyName").val();
    queryData['agencyStatue'] = $("#agencyStatue").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    if(validateDate($("#beginTime").val(),$("#endTime").val()) == false ){
		return false;
	}else{
		MgrTerm.table.refresh({query: queryData});
	}
}

/**
 * 重置
 */
MgrTerm.resetSearch = function () {
	resetData();
    MgrTerm.search();
}
function resetData(){
	$("#agencyName").val('');
	$("#agencyStatue").val('');
	$("#beginTime").val('');
	$("#endTime").val('');
}
/**
 * 检查是否选中
 */
MgrTerm.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	MgrTerm.seItem = selected[0];
        return true;
    }
};

/**
 * 新增代理商
 */
MgrTerm.openAddAgency = function () {
	var area = openWidthHeight("60%","98%");
	var currentType = sessionStorage.getItem('currentType');
    var index = layer.open({
        type: 2,
        title: getSingleLanguage('addAgency')||'addAgency',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        scrollbar: true,
        content: Feng.ctxPath + '/agencySettings/add'
    });
    this.layerIndex = index;
};


/**
 * 点击详情
 */
MgrTerm.openSmsDetail = function(){
	if (this.check()) {
		var currentType = sessionStorage.getItem('currentType');
		var url = Feng.ctxPath + '/sms/detail/' + this.seItem.id +'/'+currentType;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('smsDetails')||'短信详情',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}
/**
 * 修改代理商
 */
MgrTerm.openEditAgency = function(){
	if (this.check()) {
		var url = Feng.ctxPath + '/agencySettings/edit/' + this.seItem.id;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('editAgency')||'修改代理商',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}


/**
 * 悬停显示该列所有信息
 */
var createtime = function (value, row, index) {
	var values = row.createtime;
    var span=document.createElement('span');
    span.setAttribute('title',values);
    span.innerHTML = values;
    return span.outerHTML;
}



$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = MgrTerm.initColumn();
    var table = new BSTable("managerTable", "/agencySettings?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    MgrTerm.table = table.init();   
});
