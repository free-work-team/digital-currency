/**
 * 对象信息对话框（可用于添加和修改对话框）
 */
var titleNotEmpty = getSingleLanguage('titleNotEmpty') || "标题不能为空";
var contentNotEmpty = getSingleLanguage('contentNotEmpty') || "内容不能为空";

var Agreement = {
    objInfoData: {},
    validateFields: {
    	agreementTitle: {
            validators: {
                notEmpty: {
                    message: titleNotEmpty
                }
            }
        },
        agreementContent: {
            validators: {
                notEmpty: {
                    message: contentNotEmpty
                }
            }
        }
       
    }
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
Agreement.set = function (key, value) {
    if(typeof value == "undefined"){
        if(typeof $("#" + key).val() =="undefined"){
            var str="";
            var ids="";
            $("input[name='"+key+"']:checkbox").each(function(){
                if(true == $(this).is(':checked')){
                    str+=$(this).val()+",";
                }
            });
            if(str){
                if(str.substr(str.length-1)== ','){
                    ids = str.substr(0,str.length-1);
                }
            }else{
                $("input[name='"+key+"']:radio").each(function(){
                    if(true == $(this).is(':checked')){
                        ids=$(this).val()
                    }
                });
            }
            this.objInfoData[key] = ids;
        }else{
            this.objInfoData[key]= $("#" + key).val();
        }
    }

    return this;
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
Agreement.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
Agreement.close = function () {
    //parent.layer.close(window.parent.Agreement.layerIndex);
	window.parent.location.reload();
};
/**
 * 验证数据是否为空
 */
Agreement.validate = function () {
    $('#agreementSettingInfoForm').data("bootstrapValidator").resetForm();
    $('#agreementSettingInfoForm').bootstrapValidator('validate');
    return $("#agreementSettingInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 清除数据
 */
Agreement.clearData = function () {
    this.objInfoData = {};
};
/**
 * 收集数据
 */
Agreement.collectData = function () {
    this.set('id').set('agreementTitle').set('agreementContent').set('privacyPolicyTitle').set('privacyPolicyContent');
};
/**
 * 提交添加
 */
Agreement.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var agreementTitle = $("#agreementTitle").val();
    if (agreementTitle.length > 50) {
        Feng.error("The maximum length of the title does not exceed 50");
        return;
    }
    var agreementContent = $("#agreementContent").val();
    if (agreementContent.length > 5000) {
        Feng.error("The maximum length of the content does not exceed 5000");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/agreement/add", function (data) {
        Feng.success(getSingleLanguage('success')||"成功!");
        setTimeout(function (){
        	Agreement.close();
        },"1000");
    }, function (data) {
    	var addFail = getSingleLanguage('fail')||"失败!";
        Feng.error(addFail + data.responseJSON.message + " !");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};

/**
 * 提交修改
 */
Agreement.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var agreementTitle = $("#agreementTitle").val();
    if (agreementTitle.length > 50) {
        Feng.error("The maximum length of the title does not exceed 50");
        return;
    }
    var agreementContent = $("#agreementContent").val();
    if (agreementContent.length > 5000) {
        Feng.error("The maximum length of the content does not exceed 5000");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/agreement/edit", function (data) {
        Feng.success(getSingleLanguage('success')||"成功!");
        setTimeout(function (){
        	Agreement.close();
        },"1000");
        
    }, function (data) {
    	var editFail = getSingleLanguage('fail')||"失败!";
        Feng.error(editFail + data.responseJSON.message + " !");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};

$(function () {
    Feng.initValidator("agreementSettingInfoForm", Agreement.validateFields);
});
