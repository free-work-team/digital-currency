/**
 * 单例对象
 */
var CryptoSettings = {
    id: "cryptoSettingsTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格
 */
CryptoSettings.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'36px'},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',width:'90px'},
        {title: 'name', field: 'name',lanId:'cryptoSettingName',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'Virtual Currency', field: 'virtualCurrencyDesc',lanId:'virtualCurrency',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'status', field: 'statusDesc', lanId:'status',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'exchange Strategy', field: 'exchangeStrategyDesc', lanId:'exchangeStrategy',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'Exchange', field: 'exchangeDesc', lanId:'exchange',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'Hot Wallet', field: 'hotWalletDesc', lanId:'hotWallet',align: 'center', valign: 'middle', sortable: true,width:'100px'},
        {title: 'confirmations', field: 'confirmationsDesc', lanId:'confirmations',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        /*{title: 'rate Source', field: 'rate_source', lanId:'rateSource',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'price', field: 'price', lanId:'price',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'buy_transaction_fee', field: 'buy_transaction_fee', lanId:'buyTransactionFee',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'buy_single_fee', field: 'buy_single_fee', lanId:'buySingleFee',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'min_need_cash', field: 'min_need_cash', lanId:'minNeedCash',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'sell_transaction_fee', field: 'sell_transaction_fee', lanId:'sellTransactionFee',align: 'center', valign: 'middle', sortable: true,width:'140px'},
        {title: 'sell_single_fee', field: 'sell_single_fee', lanId:'sellSingleFee',align: 'center', valign: 'middle', sortable: true,width:'140px'},*/
        {title: 'create_user', field: 'create_user', lanId:'createUser',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'create_time', field: 'create_time', lanId:'createTime',align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'update_user', field: 'update_user', lanId:'updateUser',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'update_time', field: 'update_time', lanId:'updateTime',align: 'center', valign: 'middle', sortable: true,width:'160px'}
        ];
    return columns;
};

/**
 * 点击添加
 */
CryptoSettings.openAddView = function () {
	var area = openWidthHeight("60%","98%");
	var index = layer.open({
        type: 2,
        title: getSingleLanguage('add')||'添加',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/cryptoSettings/add'
    });
    this.layerIndex = index;
};
/**
 * 修改
 */
CryptoSettings.openEditView = function(){
	if (this.check()) {
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('edit')||'编辑',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: Feng.ctxPath + '/cryptoSettings/edit/' + this.seItem.id
	    });
	    this.layerIndex = index;
	}
}
/**
 * 删除
 */
CryptoSettings.deleteOper = function(){
	if (this.check()) {
		var id = this.seItem.id;
		layer.confirm(getSingleLanguage('confirmDelete')||'是否删除？', {
		     time: 0 //不自动关闭
		    ,title:getSingleLanguage('info')||'信息'
		    ,btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消']
	 		/*,btnAlign: 'c'//按钮居中*/	    
		    ,yes: function(index){
		    	 layer.close(index);
		    	 var ajax = new $ax(Feng.ctxPath + "/cryptoSettings/delete", function (data) {
		             Feng.success(getSingleLanguage('success')||"成功!");
		             CryptoSettings.search();
		         }, function (data) {
		        	 var fail = getSingleLanguage('fail')||"失败!";
		             Feng.error(fail + ", " + data.responseJSON.message + " !");
		         });
		         ajax.set("id", id);
		         ajax.start();
		    }
		});
	}
}
/**
 * 详情
 */
CryptoSettings.openDetailView =function(){
	if (this.check()) {
		var area = openWidthHeight("60%","98%");
	    var index = layer.open({
	        type: 2,
	        title: getSingleLanguage('details')||'详情',
	        area: [area.width, area.height], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: Feng.ctxPath + '/cryptoSettings/detail/' + this.seItem.id
	    });
	    this.layerIndex = index;
	}
}

/**
 * 检查是否选中
 */
CryptoSettings.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
        CryptoSettings.seItem = selected[0];
        return true;
    }
};
//搜索
CryptoSettings.search = function () {
    var queryData = {};
    queryData['name'] = $("#name").val();
    queryData['virtualCurrency'] = $("#virtualCurrency").val();
    queryData['status'] = $("#status").val();
    CryptoSettings.table.refresh({query: queryData});
}
//重置
CryptoSettings.resetSearch = function () {
	$("#name").val("");
	$("#virtualCurrency").val("");
	$("#status").val("");
	CryptoSettings.search();
}

$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = CryptoSettings.initColumn();
    var table = new BSTable("cryptoSettingsTable", "/cryptoSettings?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    CryptoSettings.table = table.init();
});


