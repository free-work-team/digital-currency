/**
 * 系统管理--用户管理的单例对象
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
        {title: 'TerminalNo', field: 'terminal_no', lanId:'terminalNo', align: 'center', valign: 'middle',width:'130px'},
        {title: 'Merchant Name', field: 'merchant_name', lanId:'merchantName',align: 'center', valign: 'middle',width:'130px'},
        {title: 'Hotline', field: 'hotline',lanId:'hotline', align: 'center', valign: 'middle',width:'130px'},
        {title: 'Email', field: 'e_mail', lanId:'userEmail',align: 'center', valign: 'middle',width:'130px'},
        {title: 'Way', field: 'wayDesc', lanId:'way',align: 'center', valign: 'middle',width:'130px'},
        /*{title: 'Hot Wallet', field: 'hotWalletDesc', lanId:'hotWallet',align: 'center', valign: 'middle',width:'100px'},*/
        /*{title: '购买手续费(%)', field: 'buy_transaction_fee',lanId:'buyTransactionFee', align: 'center', valign: 'middle'},
        {title: '购买手续费(聪/笔)', field: 'buy_single_fee',lanId:'buySingleFee', align: 'center', valign: 'middle'},*/
        /*{title: '出售手续费(%)', field: 'sellTransactionFee',lanId:'sellTransactionFee', align: 'center', valign: 'middle'},
        {title: '出售手续费(聪/笔)', field: 'sellSingleFee',lanId:'sellSingleFee', align: 'center', valign: 'middle'},*/
        /*{title: '比特币价格', field: 'rate',lanId:'rate', align: 'center', valign: 'middle',width:'100px'},
        {title: '最低所需比特币', field: 'min_need_bitcoin',lanId:'minNeedBitcoin', align: 'center', valign: 'middle'},
        {title: '最低交易金额', field: 'min_need_cash',lanId:'minNeedCash', align: 'center', valign: 'middle'},*/
        /*{title: 'Currency', field: 'currency',lanId:'currency', align: 'center', valign: 'middle',width:'120px'},
        {title: 'Exchange', field: 'exchange',lanId:'exchange', align: 'center', valign: 'middle',formatter: operateFormatter,width:'140px'},*/
        {title: 'Create Time', field: 'create_time',lanId:'createTime', align: 'center', valign: 'middle',width:'160px'}
        /*{title: '操作', field: '',lanId:'operate', align: 'center', valign: 'middle',formatter: operateFormatter}*/
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
//    queryData['deptid'] = MgrTerm.deptid;
    queryData['merchantName'] = $("#merchantName").val();
    queryData['terminalNo'] = $("#terminalNo").val();
    queryData['hotline'] = $("#hotline").val();
    queryData['hotWallet'] = $("#hotWallet").val();
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
	$("#merchantName").val('');
	$("#terminalNo").val('');
	$("#hotline").val('');
	$("#hotWallet").val('');
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
 * 点击添加
 */
MgrTerm.openAddMgr = function () {
	var area = openWidthHeight("60%","98%");
	var currentType = sessionStorage.getItem('currentType');
    var index = layer.open({
        type: 2,
        title: getSingleLanguage('addTerminal')||'添加终端',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        scrollbar: true,
        content: Feng.ctxPath + '/term/add?currentType=' + currentType
    });
    this.layerIndex = index;
};
/**
 * 点击修改
 */
MgrTerm.openTermView = function(){
	if (this.check()) {
		var currentType = sessionStorage.getItem('currentType');
		var url = Feng.ctxPath + '/term/edit/' + this.seItem.id + '/'+currentType;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('editingTerminal')||'编辑终端',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}
/**
 * 点击删除
 */
MgrTerm.delTerm = function(){
	/*layer.confirm('确认删除该用户？', {
        btn: ['确定', '取消'],
        shade: false //不显示遮罩
    }, function () {
        var ajax = new $ax(Feng.ctxPath + "/mgr/delete", function (data) {
            Feng.success("删除成功!");
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("userId", userId);
        ajax.start();
    });*/
	if (this.check()) {
		var userId = this.seItem.id;
		parent.layer.confirm(getSingleLanguage('confirmDelete')||'确认删除该终端？', {
			title:getSingleLanguage('info')||'信息',
			btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消'],
	        shade: false //不显示遮罩
			},function(index){
		    	 layer.close(index);
		    	 var ajax = new $ax(Feng.ctxPath + "/term/delete", function (data) {
	           Feng.success(getSingleLanguage('deleteSuccess')||"删除成功!");
	           MgrTerm.search();
		         }, function (data) {
		        	 var deleteFail = getSingleLanguage('deleteFail')||"删除失败!";
		        	 Feng.error(deleteFail + " " + data.responseJSON.message + " !");
		         });
		         ajax.set("userId", userId);
		         ajax.start();
	
		});
	}
}
/**
 * 点击详情
 */
MgrTerm.openTermDetail = function(){
	if (this.check()) {
		var currentType = sessionStorage.getItem('currentType');
		var url = Feng.ctxPath + '/term/detail/' + this.seItem.id +'/'+currentType;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('terminalDetails')||'终端详情',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}
/**
 * 点击修改密码
 */
MgrTerm.openChangePwd = function(){
	if (this.check()) {
		var url = Feng.ctxPath + '/term/changePwd/' + this.seItem.id;
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('editingTerminal')||'编辑终端',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: url
	    });
	    this.layerIndex = index;
	}
}

/**
 * 点击重置密码
 */
MgrTerm.resetPwd = function () {
    if (this.check()) {
        var userId = this.seItem.id;
        parent.layer.confirm(getSingleLanguage('confirmReset')||'确认重置密码？', {
        	title:getSingleLanguage('info')||'信息',
        	btn: [getSingleLanguage('submitBtn')||'确认', getSingleLanguage('cancelBtn')||'取消'],
            shade: false //不显示遮罩
        }, function () {
            var ajax = new $ax(Feng.ctxPath + "/term/resetPwd", function (data) {
                Feng.success(getSingleLanguage('resetSuccess')||"重置密码成功!");
            }, function (data) {
            	var resetFail = getSingleLanguage('resetFail')||"重置密码失败!";
                Feng.error(resetFail + " " + data.responseJSON.message + " !");
            });
            ajax.set("userId", userId);
            ajax.start();
        });
    }
};

/**
 * 悬停显示该列所有信息。
 */
var createtime = function (value, row, index) {
	var values = row.createtime;
    var span=document.createElement('span');
    span.setAttribute('title',values);
    span.innerHTML = values;
    return span.outerHTML;
}

/**
 * 悬停显示该列所有信息。
 */
var account = function (value, row, index) {
	var values = row.account;
    var span=document.createElement('span');
    span.setAttribute('title',values);
    span.innerHTML = values;
    return span.outerHTML;
}

/**
 * 悬停显示该列所有信息。
 */
var deptName = function (value, row, index) {
	var values = row.deptName;
    var span=document.createElement('span');
    span.setAttribute('title',values);
    span.innerHTML = values;
    return span.outerHTML;
}

/**
 * 悬停显示该列所有信息。
 */
var deptno = function (value, row, index) {
	var values = row.deptno;
    var span=document.createElement('span');
    span.setAttribute('title',values);
    span.innerHTML = values;
    return span.outerHTML;
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

MgrTerm.onClickDept = function (e, treeId, treeNode) {
    MgrTerm.deptid = treeNode.id;
    MgrTerm.search();
};

$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = MgrTerm.initColumn();
    var table = new BSTable("managerTable", "/term?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    MgrTerm.table = table.init();
    /*var ztree = new $ZTree("deptTree", "/dept/tree");
    ztree.bindOnClick(MgrTerm.onClickDept);
    ztree.init();*/
});
