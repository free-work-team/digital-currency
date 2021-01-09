/**
 * 对象信息对话框（可用于添加和修改对话框）
 */
var emailNotEmpty = getSingleLanguage('emailNotEmpty') || "邮箱不能为空";

var EmailSetting = {
    objInfoData: {},
    validateFields: {
        email: {
            validators: {
                notEmpty: {
                    message: emailNotEmpty
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
//    parent.layer.close(window.parent.EmailSetting.layerIndex);
    var index = parent.layer.getFrameIndex(window.name);
	parent.layer.close(index);
	//window.parent.location.reload();
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
    this.set('email');
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
    var email = $("input[name='email']").val();
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customer/notifyBox", function (data) {
        Feng.success("success!");
        setTimeout(function (){
        	EmailSetting.close();
        },"1000");
    }, function (data) {
    	var addFail = getSingleLanguage('addFail')||"添加失败!";
        Feng.error(addFail + data.responseJSON.message + "!");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};



$(function () {
    Feng.initValidator("emailSettingInfoForm", EmailSetting.validateFields);
});
