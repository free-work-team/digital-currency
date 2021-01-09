/**
 * 角色详情对话框（可用于添加和修改对话框）
 */
var roleNameNotEmpty = getSingleLanguage('roleNameNotEmpty') || "角色名不能为空";
var RolInfoDlg = {
    roleInfoData: {},
    validateFields: {
        name: {
            validators: {
                notEmpty: {
                    message: roleNameNotEmpty
                }
            }
        }
       
    }
};

/**
 * 清除数据
 */
RolInfoDlg.clearData = function () {
    this.roleInfoData = {};
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
RolInfoDlg.set = function (key, value) {
    this.roleInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
RolInfoDlg.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
RolInfoDlg.close = function () {
    parent.layer.close(window.parent.Role.layerIndex);
};

/**
 * 收集数据
 */
RolInfoDlg.collectData = function () {
    this.set('name').set('tips');
};

/**
 * 验证数据是否为空
 */
RolInfoDlg.validate = function () {
    $('#roleInfoForm').data("bootstrapValidator").resetForm();
    $('#roleInfoForm').bootstrapValidator('validate');
    return $("#roleInfoForm").data('bootstrapValidator').isValid();
};

/**
 * 提交添加用户
 */
RolInfoDlg.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/role/add", function (data) {
        Feng.success(getSingleLanguage('addSuccess')||"添加成功!");
        window.parent.Role.table.refresh();
        RolInfoDlg.close();
    }, function (data) {
    	var addFail = getSingleLanguage('addFail')||"添加失败!";
        Feng.error(addFail + data.responseJSON.message + " !");
    });
    ajax.set(this.roleInfoData);
    ajax.start();
};

/**
 * 提交修改
 */
RolInfoDlg.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/role/edit", function (data) {
        Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
        window.parent.Role.table.refresh();
        RolInfoDlg.close();
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + "!");
    });
    ajax.set(this.roleInfoData);
    ajax.start();
};

$(function () {
    Feng.initValidator("roleInfoForm", RolInfoDlg.validateFields);

    /* var deptTree = new $ZTree("deptTree", "/dept/tree");
    deptTree.bindOnClick(RolInfoDlg.onClickDept);
    deptTree.bindOnDblClick(RolInfoDlg.onDblClickDept)
    deptTree.init();
    RolInfoDlg.deptZtree = deptTree;

    var pNameTree = new $ZTree("pNameTree", "/role/roleTreeList");
    pNameTree.bindOnClick(RolInfoDlg.onClickPName);
    pNameTree.init();
    RolInfoDlg.pNameZtree = pNameTree;*/
});
