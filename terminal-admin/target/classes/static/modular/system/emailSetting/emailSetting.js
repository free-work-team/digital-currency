/**
 * 对象信息对话框（可用于添加和修改对话框）
 */
var hostNotEmpty = getSingleLanguage('hostNotEmpty') || "Host不能为空";
var emailNotEmpty = getSingleLanguage('emailNotEmpty') || "邮箱不能为空";
var authorizationCodeNotEmpty = getSingleLanguage('authorizationCodeNotEmpty') || "授权码不能为空";
var EmailSetting = {
    objInfoData: {},
    validateFields: {
        host: {
            validators: {
                notEmpty: {
                    message: hostNotEmpty
                }
            }
        },
        email: {
            validators: {
                notEmpty: {
                    message: emailNotEmpty
                }
            }
        },
        password: {
            validators: {
                notEmpty: {
                    message: authorizationCodeNotEmpty
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
EmailSetting.set = function (key, value) {
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
EmailSetting.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
EmailSetting.close = function () {
    //parent.layer.close(window.parent.EmailSetting.layerIndex);
	window.parent.location.reload();
};
/**
 * 验证数据是否为空
 */
EmailSetting.validate = function () {
    $('#emailSettingInfoForm').data("bootstrapValidator").resetForm();
    $('#emailSettingInfoForm').bootstrapValidator('validate');
    return $("#emailSettingInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 清除数据
 */
EmailSetting.clearData = function () {
    this.objInfoData = {};
};
/**
 * 收集数据
 */
EmailSetting.collectData = function () {
    this.set('id').set('host').set('email').set('password');
};
/**
 * 提交添加
 */
EmailSetting.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var host = $("#host").val();
    if ( host == "" || host == null) {
        Feng.error("Host can not be empty");
        return;
    }
    var email = $("#email").val();
    if ( email == "" || email == null) {
        Feng.error("Mailbox can not be empty");
        return;
    }
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    var password = $("#password").val();
    if ( password == "" || password == null) {
        Feng.error("Authorization Code can not be empty");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/emailSetting/add", function (data) {
        Feng.success(getSingleLanguage('addSuccess')||"添加成功!");
        setTimeout(function (){
        	EmailSetting.close();
        },"1000");
    }, function (data) {
    	var addFail = getSingleLanguage('addFail')||"添加失败!";
        Feng.error(addFail + data.responseJSON.message + " !");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};

/**
 * 提交修改
 */
EmailSetting.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }var host = $("#host").val();
    if ( host == "" || host == null) {
        Feng.error("Host can not be empty");
        return;
    }
    var email = $("#email").val();
    if ( email == "" || email == null) {
        Feng.error("Mailbox can not be empty");
        return;
    }
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    var password = $("#password").val();
    if ( password == "" || password == null) {
        Feng.error("Authorization Code can not be empty");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/emailSetting/edit", function (data) {
        Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
        setTimeout(function (){
        	EmailSetting.close();
        },"1000");
        
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + " !");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};

$(function () {
    Feng.initValidator("emailSettingInfoForm", EmailSetting.validateFields);
});
