/**
 * 角色管理的单例
 */
var Role = {
    id: "roleTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Role.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'36px'},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',width:'90px'},
        {title: 'Name', field: 'name',lanId:'roleName', align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Status', field: 'statusDesc',lanId:'status', align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Create Time', field: 'createTime',lanId:'createTime', align: 'center', valign: 'middle', sortable: true,width:'160px'},
        {title: 'Update Time', field: 'updateTime', lanId:'updateTime',align: 'center', valign: 'middle', sortable: true,width:'160px'},
		{title: 'Remark', field: 'tips', lanId:'remark',align: 'center', valign: 'middle', sortable: true,width:'200px'}]
    return columns;
};


/**
 * 搜索角色
 */
Role.search = function () {
    var queryData = {};
    queryData['name'] = $("#name").val();
    Role.table.refresh({query: queryData});
}

/**
 * 权限配置
 */
Role.assign = function () {
    if (this.check()) {
    	var area = openWidthHeight("30%","60%");
        var index = layer.open({
            type: 2,
            title: getSingleLanguage('authorizeAssignments')||'权限配置',
            area: [area.width, area.height], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/role/setAuthority?roleId=' + this.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 检查是否选中
 */
Role.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
        Role.seItem = selected[0];
        return true;
    }
};
var pName = function(value,row,index){
	if(row.pName == "--"){
		return "顶级";
	}else{
		return row.pName;
	}
}
/**
 * 点击添加管理员
 */
Role.openAddRole = function () {
	var area = openWidthHeight("50%","60%");
    var index = layer.open({
        type: 2,
        title: getSingleLanguage('addRole')||'添加角色',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/role/add'
    });
    this.layerIndex = index;
};

/**
 * 点击修改按钮时
 */
Role.openChangeRole = function (operateType) {
    if (this.check()) {
    	var url = Feng.ctxPath + '/role/detail/' + this.seItem.id;
    	if(operateType==2){//1 详情 2修改
    		url = Feng.ctxPath + '/role/role_edit/' + this.seItem.id;
    	}
    	var area = openWidthHeight("50%","60%");
        var index = layer.open({
            type: 2,
            title: getSingleLanguage('editRole')||'修改角色',
            area: [area.width, area.height], //宽高
            fix: false, //不固定
            maxmin: true,
            content: url
        });
        this.layerIndex = index;
    }
};

/**
 * 删除角色
 */
Role.delRole = function () {
    if (this.check()) {
        layer.confirm(getSingleLanguage('confirmDelete')||'是否删除？', {
            time: 0 //不自动关闭
            ,title:getSingleLanguage('info')||'信息'
            ,btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消']
            /*,btnAlign: 'c'//按钮居中*/
            ,yes: function(index){
                layer.close(index);
                var ajax = new $ax(Feng.ctxPath + "/role/delate", function (data) {
                    Feng.success(getSingleLanguage('deleteSuccess')||"删除成功!");
                    Role.table.refresh();
                    layer.close(index);
                }, function (data) {
                    var deleteFail = getSingleLanguage('deleteFail')||"删除失败!";
                    Feng.error(deleteFail + data.responseJSON.message + " !");
                    layer.close(index);
                });
                ajax.set("roleId", Role.seItem.id);
                ajax.start();
            }
        });
    }
};

/**
 * 生效角色
 */
Role.enable = function () {
    if (this.check()) {

        var operation = function(){
            var ajax = new $ax(Feng.ctxPath + "/role/enable", function () {
                Feng.success("生效成功!");
                Role.table.refresh();
            }, function (data) {
                Feng.error("生效失败!" + data.responseJSON.message + "!");
            });
            ajax.set("roleId", Role.seItem.id);
            ajax.start();
        };

        Feng.confirm("是否生效角色 " + Role.seItem.name + "?",operation);
    }
};

/**
 * 失效角色
 */
Role.disable = function () {
    if (this.check()) {

        var operation = function(){
            var ajax = new $ax(Feng.ctxPath + "/role/disable", function () {
                Feng.success("失效成功!");
                Role.table.refresh();
            }, function (data) {
                Feng.error("失效失败!" + data.responseJSON.message + "!");
            });
            ajax.set("roleId", Role.seItem.id);
            ajax.start();
        };

        Feng.confirm("是否失效角色 " + Role.seItem.name + "?",operation);
    }
};



$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = Role.initColumn();
    var table = new BSTable(Role.id, "/role?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    table.init();
    Role.table = table;
});
