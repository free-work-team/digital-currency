/**
 * 代理商对话框（可用于添加和修改对话框）
 */
var agencyNameNotEmpty       = getSingleLanguage('agencyNameNotEmpty')          || "账号不能为空";
var singleFeeNotEmpty        = getSingleLanguage('singleFeeNotEmpty')         || "单笔手续费不能为空";
var feeNotEmpty              = getSingleLanguage('feeNotEmpty')               || "手续费不能为空";
var emailNotEmpty            = getSingleLanguage('emailNotEmpty')            || "邮箱不能为空";
var emailFormat              = getSingleLanguage('emailFormat')              || "电子邮箱格式不正确";
var phoneNotEmpty            = getSingleLanguage('phone Not Empty')            || "手机号不能为空";
var phoneFormat              = getSingleLanguage('phone Format Is wrong')    || "手机号格式不正确";
var statueNotEmpty           = getSingleLanguage('statueNotEmpty')        || "状态不能为空";
var parentNameNotEmpty         = getSingleLanguage('parentNameNotEmpty')              || "父节点不能为空";
var addressNotEmpty          = getSingleLanguage('addressNotEmpty')              || "父节点不能为空";
var addressLimit             = getSingleLanguage('addressLimit')              || "接收地址最大为50个字符";
var agencyNameLimit             = getSingleLanguage('agencyNameLimit')              || "代理商名称最大为30个字符";

var AgencyDlg = {
    AgencyData: {},
    validateFields: {
    	agencyName: {
            validators: {
                notEmpty: {
                    message: agencyNameNotEmpty
                }
            }
        },
        agencySingleFee: {
            validators: {
                notEmpty: {
                    message: singleFeeNotEmpty
                }
            }
        },
        agencyFee: {
            validators: {
                notEmpty: {
                    message: feeNotEmpty
                }
            }
        },
        agencyEmail: {
            validators: {
                notEmpty: {
                    message: emailNotEmpty
                },
                emailAddress: {
                    message: emailFormat
                }
            }
        },
        agencyPhone: {
            validators: {
                notEmpty: {
                    message: phoneNotEmpty
                }
            }
        },
        statue: {
            validators: {
                notEmpty: {
                    message: statueNotEmpty
                }
            }
        },
        parentName: {
            validators: {
                notEmpty: {
                    message: parentNameNotEmpty
                }
            }
        },
        agencyAddress: {
            validators: {
                notEmpty: {
                    message: addressNotEmpty
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
AgencyDlg.set = function (key, value) {
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
            this.AgencyData[key] = ids;
        }else{
            this.AgencyData[key]= $("#" + key).val();
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
AgencyDlg.get = function (key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
AgencyDlg.close = function () {
    parent.layer.close(window.parent.MgrUser.layerIndex);
};

/**
 * 隐藏部门选择的树
 */
AgencyDlg.hideDeptSelectTree = function () {
    $("#menuContent").fadeOut("fast");
    $("body").unbind("mousedown", onBodyDown);// mousedown当鼠标按下就可以触发，不用弹起
};

/**
 * 验证邮箱。
 */
AgencyDlg.validateEmail = function () {
	var email_reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    var email = this.get("agencyEmail");
    if(!email_reg.test(email)){
        return false;
    } else {
        return true;
    }
};

/**
 * 验证手机号。
 */
AgencyDlg.validatePhone = function () {
	var phone_reg = /^1[0-9]{10}$/;
    var phone = this.get("agencyPhone");
    if(!phone_reg.test(phone)){
        return false;
    } else {
        return true;
    }
};


/**
 * 验证数据是否为空
 */
AgencyDlg.validate = function () {
    $('#agencyForm').data("bootstrapValidator").resetForm();
    $('#agencyForm').bootstrapValidator('validate');
    return $("#agencyForm").data('bootstrapValidator').isValid();
};

/**
 * 提交添加代理商
 */
AgencyDlg.addSubmit = function () {
    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    
    var address =  $("input[name='agencyAddress']").val();
    if (address.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    var name = $("input[name='agencyName']").val();
    if ( name.length > 30) {
        Feng.error(getSingleLanguage('agencyNameLimit')||"代理商名称最大长度为30");
        return;
    }
            
    if (!this.validateEmail()) {
        Feng.error(emailFormat);
        return;
    }
    var email = $("input[name='agencyEmail']").val();
    if ( email.length > 50) {
        Feng.error("The maximum length of the mailbox does not exceed 50");
        return;
    }
    if (!this.validatePhone()) {
        Feng.error(phoneFormat);
        return;
    }
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/agencySettings/add", function (data) {
        Feng.success(getSingleLanguage('addSuccess')||"添加成功!");
        window.parent.MgrUser.table.refresh();
        AgencyDlg.close();
    }, function (data) {
    	var addFail = getSingleLanguage('addFail')||"添加失败!";
        Feng.error(addFail + data.responseJSON.message + " !");
    });
    ajax.set(this.AgencyData);
    ajax.start();
};
/**
 * 清除数据
 */
AgencyDlg.clearData = function () {
    this.AgencyData = {};
};
/**
 * 收集数据
 */
AgencyDlg.collectData = function () {
    this.set('id').set('agencyName').set('agencySingleFee').set('agencyFee').set('agencyPhone').set('agencyEmail').set('agencyStatue').set('parentId').set('agencyAddress');
};

/**
 * 提交修改
 */
AgencyDlg.editSubmit = function () {

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
            AgencyDlg.close();
        }
    }, function (data) {
    	var editFail = getSingleLanguage('editFail')||"修改失败!";
        Feng.error(editFail + data.responseJSON.message + " !");
    });
    ajax.set(this.AgencyData);
    ajax.start();
};


function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
            event.target).parents("#menuContent").length > 0)) {
        AgencyDlg.hideDeptSelectTree();
    }
}


/* 
 * 
 * 最简单的树 -- 标准 JSON 数据
 * https://www.jq22.com/yanshi941
 * https://www.jq22.com/jquery-info941
 */
function initZtree() {
	
	var setting = {
		view: {
			dblClickExpand: false
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: onClick
		}
	};
		
    var ztree = new $ZTree("zTree", "/agencySettings/agencyTreeListById/"
    		+ "1");
    ztree.setSettings(setting);
    ztree.init();
}


function onClick(e, treeId, treeNode) {	
	var zTree = $.fn.zTree.getZTreeObj("zTree"),
	nodes = zTree.getSelectedNodes(),
	vName = "",vId="";
	nodes.sort(function compare(a,b){return a.id-b.id;});
	for (var i=0, l=nodes.length; i<l; i++) {
		vName += nodes[i].name + ",";
		vId += nodes[i].id + ",";
	}
	if (vName.length > 0 ) vName = vName.substring(0, vName.length-1);
	if (vId.length > 0 ) vId = vId.substring(0, vId.length-1);
	
	$("#parentName").val(vName);
	$("#parentId").val(vId);
	console.log(vName+","+vId);
}

function showMenu() {
	var cityObj = $("#parentName");
	var cityOffset = $("#parentName").offset();
	$("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast");

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}


$(function () {
	 Feng.initValidator("agencyForm", AgencyDlg.validateFields);	 
	 initZtree();
	 $("#parentName").bind("click",showMenu);
	
});