/**
 * 系统管理--用户管理的单例对象
 */
var MgrUser = {
    id: "managerTable",//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    deptid:0
};

/**
 * 初始化表格的列
 */
MgrUser.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,width:'36px'},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',width:'90px'},
        {title: 'Account', field: 'account',lanId:'userAccount',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Name', field: 'name', lanId:'userName',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Phone', field: 'phone', lanId:'userPhone',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Email', field: 'email', lanId:'userEmail',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'Status', field: 'statusDesc', lanId:'status',align: 'center', valign: 'middle', sortable: true,width:'120px'},
        {title: 'createtime', field: 'create_time', lanId:'createTime',align: 'center', valign: 'middle', sortable: true,width:'120px'}
        // ,{title: 'Operation', field: '', lanId:'operate',align: 'center', valign: 'middle', sortable: true,formatter: operateFormatter,width:'180px'}
        ];
    return columns;
};

/**
 * 操作跳转 
 */
var operateFormatter = function (value,row,index) {//赋予的参数
  var updateBtn  = getSingleLanguage("updateBtn"),
      deleteBtn  = getSingleLanguage("deleteBtn"),
      detailBtn  = getSingleLanguage("detailBtn");

    return [
      (row.status != 3 ? '<a href="javascript:void(0);"onclick="MgrUser.edit(\'' + row.id + '\')">' + updateBtn + '</a>&nbsp&nbsp' + '<a href="javascript:void(0);"onclick="MgrUser.delate(\'' + row.id + '\')">' + deleteBtn + '</a>&nbsp&nbsp' : ""),
      /*'<a href="javascript:void(0);"onclick="resetPwd(\''+ row.id + '\')">角色分配</a>&nbsp&nbsp',*/
      /*'<a href="javascript:void(0);"onclick="update(\''+ row.id + '\')">查看权限</a>&nbsp&nbsp',*/
      '<a href="javascript:void(0);"onclick="MgrUser.detail(\'' + row.id + '\')">' + detailBtn + '</a>&nbsp&nbsp'
    ].join('');
}

MgrUser.search = function () {
    var queryData = {};
    queryData['name'] = $("#name").val();
    queryData['phone'] = $("#phone").val();
    queryData['account'] = $("#account").val();
    queryData['status'] = $("#statusDesc").val();
    MgrUser.table.refresh({query: queryData});
}

MgrUser.detail =function(userId){
	var url = Feng.ctxPath + '/mgr/detail/' + userId;
	var area = openWidthHeight("50%","60%");
    var index = layer.open({
        type: 2,
        title: getSingleLanguage('userDetails')||'用户详情',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        content: url
    });
    this.layerIndex = index;
	
}

MgrUser.edit = function(){
  if (this.check()) {
    var url = Feng.ctxPath + '/mgr/edit/' + this.seItem.id;
    var area = openWidthHeight("50%","60%");
    var index = layer.open({
      type: 2,
      title: getSingleLanguage('editUser')||'编辑用户',
      area: [area.width, area.height], //宽高
      fix: false, //不固定
      maxmin: true,
      content: url
    });
    this.layerIndex = index;
  }
}

MgrUser.delate = function(){
  if (this.check()) {
    layer.confirm(getSingleLanguage('confirmDelete')||'是否删除该用户？', {
      time: 0 //不自动关闭
      ,title:getSingleLanguage('info')||'信息'
      ,btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消']
      /*,btnAlign: 'c'//按钮居中*/
      ,yes: function(index){
        layer.close(index);
        var ajax = new $ax(Feng.ctxPath + "/mgr/delete", function (data) {
          Feng.success(getSingleLanguage('deleteSuccess')||"删除成功!");
          MgrUser.search();
        }, function (data) {
          var deleteFail = getSingleLanguage('deleteFail')||"删除失败!";
          Feng.error(deleteFail + data.responseJSON.message + " !");
        });
        ajax.set("userId", this.seItem.id);
        ajax.start();
      }
    });
  }

	
}


/**
 * 检查是否选中
 */
MgrUser.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
        return false;
    } else {
        MgrUser.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加管理员
 */
MgrUser.openAddMgr = function () {
	var area = openWidthHeight("50%","60%");
	var index = layer.open({
        type: 2,
        title: getSingleLanguage('addUser')||'添加用户',
        area: [area.width, area.height], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/mgr/add'
    });
    this.layerIndex = index;
};



/**
 * 点击角色分配
 * @param
 */
MgrUser.roleAssign = function () {
    if (this.check()) {
    	var area = openWidthHeight("30%","60%");
        var index = layer.open({
            type: 2,
            title: getSingleLanguage('roleAssignments')||'角色分配',
            area: [area.width, area.height], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/mgr/setRole/' + this.seItem.id
        });
        this.layerIndex = index;
    }
};


/**
 * 冻结用户账户
 * @param userId
 */
MgrUser.freezeAccount = function () {
    if (this.check()) {
      if (this.seItem.status == 3){
        Feng.error(getSingleLanguage("NoOperation"));
        return;
      }
      if (this.seItem.status == 2 || this.seItem.status == 4){
        Feng.success(getSingleLanguage('success'));
        return;
      }
      var userId = this.seItem.id;
      layer.confirm(getSingleLanguage('confirmFreeze')||'确认冻结？', {
        time: 0 //不自动关闭
        ,title:getSingleLanguage('info')||'信息'
        ,btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消']
        /*,btnAlign: 'c'//按钮居中*/
        ,yes: function(index){
          layer.close(index);
          var ajax = new $ax(Feng.ctxPath + "/mgr/freeze", function (data) {
            Feng.success(getSingleLanguage('success'));
            MgrUser.table.refresh();
          }, function (data) {
            Feng.error(getSingleLanguage("fail") + data.responseJSON.message + "!");
          });
          ajax.set("userId", userId);
          ajax.start();
        }
      })
    }
};

/**
 * 解除冻结用户账户
 * @param userId
 */
MgrUser.unfreeze = function () {
    if (this.check()) {
      if (this.seItem.status == 3){
        Feng.error(getSingleLanguage("NoOperation"));
        return;
      }
      if (this.seItem.status == 1){
        Feng.success(getSingleLanguage('success'));
        return;
      }
    	var userId = this.seItem.id;
    	layer.confirm(getSingleLanguage('confirmUnfreeze')||'确认解冻？', {
		     time: 0 //不自动关闭
		    ,title:getSingleLanguage('info')||'信息'
		    ,btn: [getSingleLanguage('confirmBtn')||'确认', getSingleLanguage('cancelBtn')||'取消']
	 		/*,btnAlign: 'c'//按钮居中*/	    
		    ,yes: function(index){
		    	 layer.close(index);
				var ajax = new $ax(Feng.ctxPath + "/mgr/unfreeze", function (data) {
				    Feng.success(getSingleLanguage('unfreezeSuccess'));
				    MgrUser.table.refresh();
				}, function (data) {
				    Feng.error(getSingleLanguage('unfreezeFail'));
				});
				ajax.set("userId", userId);
				ajax.start();
		    }
		});
    }
}

/**
 * 重置密码
 */
MgrUser.resetPwd = function () {
    if (this.check()) {
        var userId = this.seItem.id;
        parent.layer.confirm('是否重置密码？', {
            btn: ['确定', '取消'],
            shade: false //不显示遮罩
        }, function () {
            var ajax = new $ax(Feng.ctxPath + "/mgr/reset", function (data) {
                Feng.success("重置密码成功!");
            }, function (data) {
                Feng.error("重置密码失败!");
            });
            ajax.set("userId", userId);
            ajax.start();
        });
    }
};

MgrUser.resetSearch = function () {
	$("#name").val("");
	$("#phone").val("");
	$("#account").val("");
	MgrUser.search();
}

$(function () {
	var currentType = sessionStorage.getItem('currentType');
    var defaultColunms = MgrUser.initColumn();
    var table = new BSTable("managerTable", "/mgr?currentType=" + currentType, defaultColunms);
    table.setPaginationType("server");
    MgrUser.table = table.init();
    /*var ztree = new $ZTree("deptTree", "/dept/tree");
    ztree.bindOnClick(MgrUser.onClickDept);
    ztree.init();*/
});


