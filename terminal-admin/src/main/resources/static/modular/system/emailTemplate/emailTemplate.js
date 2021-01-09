/**
 * 邮箱模板-初始化对象
 */
var EmailTemplate = {
    id: "emailTemplateTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
EmailTemplate.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'No.',sortable: true, lanId:'number', align: 'center', valign: 'middle',formatter:serialFormatter,width:'60px'},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: 'Code', field: 'code', lanId:'templateCode', align: 'center', valign: 'middle',width:'190px'},
        {title: 'Name', field: 'name', lanId:'templateName',align: 'center', valign: 'middle',width:'190px'},
        {title: 'Template', field: 'template',lanId:'template', align: 'center', valign: 'middle',width:'400px'},
        {title: 'Create User', field: 'create_user', lanId:'createUser',align: 'center', valign: 'middle',width:'120px'},
        {title: 'Create Time', field: 'create_time',lanId:'createTime', align: 'center', valign: 'middle',width:'140px'},
        {title: 'Update User', field: 'update_user', lanId:'updateUser',align: 'center', valign: 'middle',width:'120px'},
        {title: 'Update Time', field: 'update_time', lanId:'updateTime',align: 'center', valign: 'middle',width:'140px'}
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
 * 点击修改
 */
EmailTemplate.openEditView = function () {
    if (this.check()) {
    	var area = openWidthHeight("50%","60%");
        var index = layer.open({
            type: 2,
            title: getSingleLanguage('edit')||'编辑',
            area: [area.width, area.height], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/emailTemplate/edit/' + this.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 提交修改
 */
EmailTemplate.editSubmit = function () {
	var id = $("#id").val();
	var code = $("#code").val();
	var name = $("#name").val();
	var template = $("#template").val();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/emailTemplate/edit", function (data) {
        Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
        window.parent.EmailTemplate.table.refresh();
        EmailTemplate.close();
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + "!");
    });
    ajax.set("id",id);
    ajax.set("code",code);
    ajax.set("name",name);
    ajax.set("template",template);
    ajax.start();
};

/**
 * 点击详情
 */
EmailTemplate.details = function () {
    if (this.check()) {
    	var area = openWidthHeight("50%","60%");
        var index = layer.open({
            type: 2,
            title: getSingleLanguage('detailBtn')||'详情',
            area: [area.width, area.height], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/emailTemplate/details/' + this.seItem.id
        });
        this.layerIndex = index;
    }
};
/**
 * 搜索
 */
EmailTemplate.search = function () {
    var queryData = {};
    queryData['name'] = $("#name").val();
	EmailTemplate.table.refresh({query: queryData});
}

/**
 * 重置
 */
EmailTemplate.resetSearch = function () {
	$("#name").val('');
	EmailTemplate.search();
}
/**
 * 检查是否选中
 */
EmailTemplate.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
    	EmailTemplate.seItem = selected[0];
        return true;
    }
};
/**
 * 关闭此对话框
 */
EmailTemplate.close = function () {
    parent.layer.close(window.parent.EmailTemplate.layerIndex);
};

$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = EmailTemplate.initColumn();
    var table = new BSTable("emailTemplateTable", "/emailTemplate?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    EmailTemplate.table = table.init();
});















