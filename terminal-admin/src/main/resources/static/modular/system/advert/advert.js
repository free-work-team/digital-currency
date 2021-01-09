/**
 * 对象信息对话框（可用于添加和修改对话框）
 */
var titleNotEmpty = getSingleLanguage('titleNotEmpty') || "Host不能为空";
var contentNotEmpty = getSingleLanguage('contentNotEmpty') || "邮箱不能为空";

var Advert = {
    objInfoData: {},
    validateFields: {
    	advertTitle: {
            validators: {
                notEmpty: {
                    message: titleNotEmpty
                }
            }
        },
        advertContent: {
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
Advert.set = function (key, value) {
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
Advert.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
Advert.close = function () {
    //parent.layer.close(window.parent.Advert.layerIndex);
	window.parent.location.reload();
};
/**
 * 验证数据是否为空
 */
Advert.validate = function () {
    $('#advertSettingInfoForm').data("bootstrapValidator").resetForm();
    $('#advertSettingInfoForm').bootstrapValidator('validate');
    return $("#advertSettingInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 清除数据
 */
Advert.clearData = function () {
    this.objInfoData = {};
};
/**
 * 收集数据
 */
Advert.collectData = function () {
    this.set('id').set('advertTitle').set('advertContent');
};
/**
 * 提交添加
 */
Advert.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var advertTitle = $("input[name='advertTitle']").val();
    if ( advertTitle.length > 50) {
        Feng.error("The maximum length of the title does not exceed 50");
        return;
    }
    var advertContent = $("textarea[name='advertContent']").val();
    if ( advertContent.length > 2048) {
        Feng.error("The maximum length of the content does not exceed 2000");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/advert/add", function (data) {
        Feng.success(getSingleLanguage('addSuccess')||"添加成功!");
        setTimeout(function (){
        	Advert.close();
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
Advert.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    var advertTitle = $("input[name='advertTitle']").val();
    if ( advertTitle.length > 50) {
        Feng.error("The maximum length of the title does not exceed 50");
        return;
    }
    var advertContent = $("textarea[name='advertContent']").val();
    if ( advertContent.length > 2048) {
        Feng.error("The maximum length of the content does not exceed 2000");
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/advert/edit", function (data) {
        Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
        setTimeout(function (){
        	Advert.close();
        },"1000");
        
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + " !");
    });
    ajax.set(this.objInfoData);
    ajax.start();
};

$(function () {
    Feng.initValidator("advertSettingInfoForm", Advert.validateFields);
});
