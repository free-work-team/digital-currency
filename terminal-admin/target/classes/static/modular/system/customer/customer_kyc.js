// 点击提交
function kycInfoSubmit() {
	// 校验基本信息
    var custName = $("#custName").val();
    var email = $("#email").val();
    if (!custName ) {
      layer.msg('Please enter your full name');
      hideWait();
      return;
    }
    if (!email) {
      layer.msg('Please enter your contact email');
      hideWait();
      return;
    }
    var reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
    if (!reg.test(email)) {
      layer.msg("Please enter the vaild email!");
      hideWait();
      return;
    }
    //阅读协议
    var haveRed = document.getElementById("haveRed");
    var agreementTitle = $("#agreementTitle").val();
    var privacyPolicyTitle = $("#privacyPolicyTitle").val();
    var msg = "Please agree to the ";
    if(agreementTitle){
    	msg = msg + agreementTitle;
    }
    if(privacyPolicyTitle){
    	msg = msg + " and " + privacyPolicyTitle;
    }
    if(!haveRed.checked){
      layer.msg(msg);
      hideWait();
      return;
    }
    showWait();
    setTimeout(function () {
    // 校验邮箱是否已审核通过
    $.ajax({
      type    : 'post',
      url     : "/kyc/checkEmail",
      data    : {"email": email},
      async   : false,
      dataType: 'json',
      success : function (data) {
        if (data.code == 500) {
          hideWait();
          layer.msg(data.message);
        } else {
          var cardType = $('#cardType').val();
          var idCardPositiveIndex = $("#idCardPositiveDiv").find("img").attr("data-fileIndex");
          var idCardObverseIndex = $('#idCardObverseDiv').find('img').attr('data-fileIndex');
          var idCardHandheldIndex = $('#idCardHandheldDiv').find('img').attr('data-fileIndex');
          var idPassportIndex = $('#idPassportDiv').find('img').attr('data-fileIndex');
          // 根据证件类型判断必备上传图片
          if (cardType == 1) {
            if (!idCardPositiveIndex) {
              layer.msg('Please provide the front of your state ID card');
              hideWait();
              return;
            }
            if (!idCardObverseIndex) {
              layer.msg('Please provide the back of your state ID card');
              hideWait();
              return;
            }
          } else if (cardType == 2) {
            if (!idPassportIndex) {
              layer.msg('Please provide the Passport photo');
              hideWait();
              return;
            }
          }
          if (!idCardHandheldIndex) {
            layer.msg('Please provide Selfie showing ID used with your face clearly');
            hideWait();
            return;
          }

          var idCardPositive = idCardPositiveIndex ? commonUploadFun(parseInt(idCardPositiveIndex)) : "";
          var idCardObverse = idCardObverseIndex ? commonUploadFun(parseInt(idCardObverseIndex)) : "";
          var idCardHandheld = idCardHandheldIndex ? commonUploadFun(parseInt(idCardHandheldIndex)) : "";
          var idPassport = idPassportIndex ? commonUploadFun(parseInt(idPassportIndex)) : "";

          if (cardType == 1) {
            if (!(idCardPositive && idCardObverse && idCardHandheld)) {
              hideWait();
              layer.msg("Upload information fail,Please try again");
            }
          } else if (cardType == 2) {
            if (!(idPassport && idCardHandheld)) {
              hideWait();
              layer.msg("Upload information fail,Please try again");
            }
          }
          //组装object 提交后台
          var obj = {};
          obj["custName"] = custName;
          obj["email"] = email;
          obj["idCardPositive"] = idCardPositive;
          obj["idCardObverse"] = idCardObverse;
          obj["idCardHandheld"] = idCardHandheld;
          obj["idPassport"] = idPassport;
          obj["cardType"] = parseInt(cardType);

          $.ajax({
            type    : 'post',
            url     : "/kyc/add",
            data    : {customerInfo: JSON.stringify(obj)},
            async   : false,
            dataType: 'json',
            success : function (data) {
              hideWait();
              if (data.code == 500) {
                layer.msg(data.message);
              } else {
                window.location.href = "/kyc/tosuccess";
                //layer.msg('Success!');
              }
            }
          });
        }
      }
    });
  }, 1000);
}

// 改变证件验证类型
function changeType() {
  var type = $('#cardType').val();
  clearPhoto('handheldArea');
  if (type == 1) {
    $('#idCardArea').removeClass('hidden');
    $('#passportArea').addClass('hidden');
    clearPhoto('passportArea');
  } else if (type == 2) {
    $('#passportArea').removeClass('hidden');
    $('#idCardArea').addClass('hidden');
    clearPhoto('idCardArea');
  }

}

//清空elementId下所有图片上传控件
function clearPhoto(elementId) {
  var that = $("#" + elementId);
  $.each(that.find('.content_photo'), function (index, item) {
    var imageBox = $(item).children('div');
    var divId = $(item).children('div').attr('id');
    id = divId.replace("Div", "");
    //imageBox.empty();
    imageBox.html('<div class="pic_list">'
      + '<span class="btn btn-successed fileinput-button" style="width: 100%;height: 100%;">'
      + '<input type="file" accept="image/*" capture="camera" id="' + id + '" class="filesInput" onchange="getFiles(this,960,218)"/></span></div>');
    $("#cancle-" + divId).attr("style", "display:none;");//隐藏删除图片按钮的x
  })
}
