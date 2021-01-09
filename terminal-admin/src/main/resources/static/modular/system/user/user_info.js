/**
 * 用户详情对话框（可用于添加和修改对话框）
 */
var accountNotEmpty          = getSingleLanguage('accountNotEmpty')          || "账号不能为空";
var userNameNotEmpty         = getSingleLanguage('userNameNotEmpty')         || "用户名不能为空";
var emailNotEmpty            = getSingleLanguage('emailNotEmpty')            || "邮箱不能为空";
var emailFormat              = getSingleLanguage('emailFormat')              || "电子邮箱格式不正确";
var phoneNotEmpty            = getSingleLanguage('phoneNotEmpty')            || "手机号不能为空";
var phoneFormat              = getSingleLanguage('phoneFormat')              || "手机号格式不正确";
var passwordNotEmpty         = getSingleLanguage('passwordNotEmpty')         || "密码不能为空";
var twoPasswordsInconsistent = getSingleLanguage('twoPasswordsInconsistent') || "两次密码输入不一致";

var UserInfoDlg = {
    userInfoData: {},
    validateFields: {
        account: {
            validators: {
                notEmpty: {
                    message: accountNotEmpty
                }
            }
        },
        name: {
            validators: {
                notEmpty: {
                    message: userNameNotEmpty
                }
            }
        },
        email: {
            validators: {
                notEmpty: {
                    message: emailNotEmpty
                },
                emailAddress: {
                    message: emailFormat
                }
            }
        },
        phone: {
            validators: {
                notEmpty: {
                    message: phoneNotEmpty
                }/*,
                phone:{
                	country: 'CN',
                	message: phoneFormat
                }*/
            }
        },
        password: {
            validators: {
                notEmpty: {
                    message: passwordNotEmpty
                },
                identical: {
                    field: 'rePassword',
                    message: twoPasswordsInconsistent
                },
            }
        },
        rePassword: {
            validators: {
                notEmpty: {
                    message: passwordNotEmpty
                },
                identical: {
                    field: 'password',
                    message: twoPasswordsInconsistent
                },
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
UserInfoDlg.set = function (key, value) {
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
            this.userInfoData[key] = ids;
        }else{
            this.userInfoData[key]= $("#" + key).val();
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
UserInfoDlg.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
UserInfoDlg.close = function () {
    parent.layer.close(window.parent.MgrUser.layerIndex);
};

/**
 * 点击部门input框时
 *
 * @param e
 * @param treeId
 * @param treeNode
 * @returns
 */
UserInfoDlg.onClickDept = function (e, treeId, treeNode) {
    $("#citySel").attr("value", instance.getSelectedVal());
    $("#deptid").attr("value", treeNode.id);
};

/**
 * 显示部门选择的树
 *
 * @returns
 */
UserInfoDlg.showDeptSelectTree = function () {
    var cityObj = $("#citySel");
    var cityOffset = $("#citySel").offset();
    $("#menuContent").css({
        left: cityOffset.left + "px",
        top: cityOffset.top + cityObj.outerHeight() + "px"
    }).slideDown("fast");

    $("body").bind("mousedown", onBodyDown);
};

/**
 * 显示用户详情部门选择的树
 *
 * @returns
 */
UserInfoDlg.showInfoDeptSelectTree = function () {
    var cityObj = $("#citySel");
    var cityPosition = $("#citySel").position();
    $("#menuContent").css({
        left: cityPosition.left + "px",
        top: cityPosition.top + cityObj.outerHeight() + "px"
    }).slideDown("fast");

    $("body").bind("mousedown", onBodyDown);
};

/**
 * 隐藏部门选择的树
 */
UserInfoDlg.hideDeptSelectTree = function () {
    $("#menuContent").fadeOut("fast");
    $("body").unbind("mousedown", onBodyDown);// mousedown当鼠标按下就可以触发，不用弹起
};

/**
 * 验证两个密码是否一致
 */
UserInfoDlg.validatePwd = function () {
    var password = this.get("newPwd");
    var rePassword = this.get("rePwd");
    if (password == rePassword) {
        return true;
    } else {
        return false;
    }
};

/**
 * 验证邮箱。
 */
UserInfoDlg.validateEmail = function () {
	var email_reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    var email = this.get("email");
    if(!email_reg.test(email)){
        return false;
    } else {
        return true;
    }
};

/**
 * 验证手机号。
 */
/*UserInfoDlg.validatePhone = function () {
	var phone_reg = /^1[0-9]{10}$/;
    var phone = this.get("phone");
    if(!phone_reg.test(phone)){
        return false;
    } else {
        return true;
    }
};*/


/**
 * 验证数据是否为空
 */
UserInfoDlg.validate = function () {
    $('#userInfoForm').data("bootstrapValidator").resetForm();
    $('#userInfoForm').bootstrapValidator('validate');
    return $("#userInfoForm").data('bootstrapValidator').isValid();
};

/**
 * 提交添加用户
 */
UserInfoDlg.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    
    var account =  $("input[name='account']").val();
    if (account.length > 30) {
        Feng.error(getSingleLanguage('accountLimit')||"账户最大长度为30");
        return;
    }
    var name = $("input[name='name']").val();
    if ( name.length > 20) {
        Feng.error(getSingleLanguage('nameLimit')||"姓名最大长度为20");
        return;
    }
    
    if (!this.validatePwd()) {
        Feng.error(twoPasswordsInconsistent);
        return;
    }
    
    if (!this.validateEmail()) {
        Feng.error(emailFormat);
        return;
    }
    var email = $("input[name='email']").val();
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    /*if (!this.validatePhone()) {
        Feng.error("手机号不正确！");
        return;
    }*/
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/mgr/add", function (data) {
        Feng.success(getSingleLanguage('addSuccess')||"添加成功!");
        window.parent.MgrUser.table.refresh();
        UserInfoDlg.close();
    }, function (data) {
    	var addFail = getSingleLanguage('addFail')||"添加失败!";
        Feng.error(addFail + data.responseJSON.message + " !");
    });
    ajax.set(this.userInfoData);
    ajax.start();
};
/**
 * 清除数据
 */
UserInfoDlg.clearData = function () {
    this.userInfoData = {};
};
/**
 * 收集数据
 */
UserInfoDlg.collectData = function () {
    this.set('id').set('account').set('email').set('name').set('phone');
};
/**
 * 提交修改
 */
UserInfoDlg.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    
    var account =  $("input[name='account']").val();
    if (account.length > 30) {
        Feng.error(getSingleLanguage('accountLimit')||"账户最大长度为30");
        return;
    }
    var name = $("input[name='name']").val();
    if ( name.length > 20) {
        Feng.error(getSingleLanguage('nameLimit')||"姓名最大长度为20");
        return;
    }
    
    if (!this.validateEmail()) {
        Feng.error(emailFormat);
        return;
    }
    var email = $("input[name='email']").val();
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    /*if (!this.validatePhone()) {
        Feng.error("手机号格式不正确！");
        return;
    }*/
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/mgr/edit", function (data) {
        Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
        if (window.parent.MgrUser != undefined) {
            window.parent.MgrUser.table.refresh();
            UserInfoDlg.close();
        }
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + " !");
    });
    ajax.set(this.userInfoData);
    ajax.start();
};

/**
 * 修改密码
 */
UserInfoDlg.chPwd = function () {
	
	
	 if (!this.validatePwd()) {
	        Feng.error(twoPasswordsInconsistent);
	        return;
	    }
    var ajax = new $ax(Feng.ctxPath + "/mgr/changePwd", function (data) {
		if(data.code==200){
			 Feng.success(getSingleLanguage('loginAgain')||"修改成功，请重新登录");
			 window.location.href=Feng.ctxPath +"/logout";
		  }
      
    }, function (data) {
    	var fail = getSingleLanguage('fail')||"修改失败!";
        Feng.error(fail + data.responseJSON.message + " !");
    });
   
    ajax.set("oldPwd");
    ajax.set("newPwd");
    ajax.set("rePwd");
    ajax.set("checkCode");
    ajax.start();

};

function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
            event.target).parents("#menuContent").length > 0)) {
        UserInfoDlg.hideDeptSelectTree();
    }
}

$(function () {
    Feng.initValidator("userInfoForm", UserInfoDlg.validateFields);
    // 初始化头像上传
    var avatarUp = new $WebUpload("avatar");
    avatarUp.setUploadBarId("progressBar");
    avatarUp.init();

});


/**
 * 发送手机验证码
 * @returns
 */
UserInfoDlg.Send = function () {

	var sentFail = getSingleLanguage('sentFail')||"发送失败!";
	var ajax = new $ax(Feng.ctxPath + "/mgr/sendphonecode", function (data) {
	    Feng.success(getSingleLanguage('sentSuccess')||"发送成功!");
	}, function (data) {
	    Feng.error(sentFail + data.responseJSON.message + " !");
	});
	ajax.start();
}
