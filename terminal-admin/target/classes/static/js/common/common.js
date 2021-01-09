/**
 * 重置
 */
function resetData() {
	$("#clearForm").find("input").each(function(){
		   this.value ="";
	});
	$('select').prop('selectedIndex', 0);
}

//bootstrapTable 切换语言 - 英文
var currentType = sessionStorage.getItem('currentType');
if (currentType === "lanEnglish") {
	$.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['en-US']);
}else{
	$.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);
}

//显示广告大图
function showPic(dom){
	 layer.open({
			type : 1,
			area : [ '600px',  '500px' ],
			title : "大图",
			id : 'actingPicLayer' ,// 防止重复弹出
			content : '<img src="'+Feng.ctxPath+$(dom).data("url")+'" style="width:600px;height:456px;">',
			shade : 0.1
		});
}

/**
 * 关闭弹窗
 * @returns
 */
function closes(){
	var index = parent.layer.getFrameIndex(window.name);
	parent.layer.close(index);
}

/**
 * 中英文验证
 * @param value
 * @returns 
 */
function ckzy(value){
	var chcharacter = /[a-zA-Z\u4E00-\u9FA5](?:·[\u4E00-\u9FA5])*$/;
	return chcharacter.test(value);
}

/**
 * 空验证
 * @param value
 * @returns 
 */
function isNotNull(value){
	if(value == "" || value == null || value == undefined){
		return false;
	}
	return true;
}

/**
 * 跳转详情页面。
 * @returns
 */
function detail(id,url,width,height){
	var index = layer.open({
		title:"详情",
	    type: 2,
	    area: [width, height], //宽高
	    fix: false, //不固定
	    maxmin: true,
	    content: Feng.ctxPath + url+"?id="+id
	});
	this.layerIndex = index;
}

/**
 * 初始化时间控件。
 * @returns
 */
$(function (){
	laydate.render({
	    elem: '#beginTime',
	    lang: 'en'
	});
	laydate.render({
	    elem: '#endTime',
	    lang: 'en'
	});
	laydate.render({
	    elem: '#startTime',
	    lang: 'en'
	});
	laydate.render({
	    elem: '#endingTime',
	    lang: 'en'
	});
})

/**
 * 校验开始和结束时间
 * @returns
 */
function validateDate(startTime,endTime){
	if(isNotNull(startTime)!= false && isNotNull(endTime)== false
			|| isNotNull(endTime)!= false && isNotNull(startTime)== false){
		layer.msg("起始时间|结束时间不能为空！");
		return false;
	}
	//开始时间必须大于结束时间
	var startDate = new Date(startTime);
	var endDate = new Date(endTime);
	if(startDate>endDate){
		layer.msg("起始时间不能大于结束时间！");
		return false;
	}
	return true;
}

/**
 * 是否是正整数
 * @returns
 */
function validatePosNum(value){
	var r = /^\+?[1-9][0-9]*$/;
	if(!r.test(value)){
		return false;
	}
	return true;
}

/**
 * 身份证号
 * @returns
 */
function validateID(value){
	var r = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
	if(!r.test(value)){
		return false;
	}
	return true;
}

/**
 * 只能是数字和字母。
 * @returns
 */
function validateNumZM(value){	
	if(!(/^[a-zA-Z0-9]+$/.test(value))){
		return false;
	}
	return true;
}

/**
 * 手机号。
 * @returns
 */
function validatePhone(value){	
	if(!(value.length == 11 && /^1[0-9]{10}$/.test(value))){
		return false;
	}
	return true;
}

/**
 * 银行卡号。
 * @returns
 */
function validateCard(value){	
	if(!(/^[0-9]{1,30}$/.test(value))){
		return false;
	}
	return true;
}

/**
 * 金额
 * @returns
 */
function validateMoney(value){	
	var re  = /^[0-9]+(.[0-9]{0,3})?$/;
	if(re.test(value) == false  || value < 0){
		layer.msg("金额必须大于等于0且最多保留三位小数！");
		return false;
	}
	return true;
}

/**
 * 显示图片。
 * @param templatePath
 * @returns
 */
function preview(templatePath){
	templatePath = templatePath.replace(/\\/g,"/");
	var index = layer.open({
        type: 2,
        title: 'Preview',
        area: ['90%', '90%'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + "/image/preview?imgPath="+templatePath
    });
}


/**
 * 乘法运算，避免数据相乘小数点后产生多位数和计算精度损失。
 *
 * @param num1被乘数 | num2乘数
 */
function numMulti(num1, num2) {
  var baseNum = 0;
  try {
    baseNum += num1.toString().split(".")[1].length;
  } catch (e) {
  }
  try {
    baseNum += num2.toString().split(".")[1].length;
  } catch (e) {
  }
  return scientificToNumber(Number(num1.toString().replace(".", "")) * Number(num2.toString().replace(".", "")) / Math.pow(10, baseNum));
};

/**
 * 除法运算，避免数据相除小数点后产生多位数和计算精度损失。
 *
 * @param num1被除数 | num2除数
 */
function numDiv(num1, num2) {
  var baseNum1 = 0, baseNum2 = 0;
  var baseNum3, baseNum4;
  try {
    baseNum1 = num1.toString().split(".")[1].length;
  } catch (e) {
    baseNum1 = 0;
  }
  try {
    baseNum2 = num2.toString().split(".")[1].length;
  } catch (e) {
    baseNum2 = 0;
  }
  with (Math) {
    baseNum3 = Number(num1.toString().replace(".", ""));
    baseNum4 = Number(num2.toString().replace(".", ""));
    return scientificToNumber((baseNum3 / baseNum4) * pow(10, baseNum2 - baseNum1));
  }
};

// 处理科学计数法显示问题
function scientificToNumber(num) {
  if(/\d+\.?\d*e[\+\-]*\d+/i.test(num)) {    //正则匹配科学计数法的数字
    var zero = '0',                                    //
        parts = String(num).toLowerCase().split('e'), //拆分成系数和指数
        e = parts.pop(),//存储指数
        l = Math.abs(e), //取绝对值，l-1就是0的个数
        sign = e/l,          //判断正负
        coeff_array = parts[0].split('.');   //将系数按照小数点拆分
    if(sign === -1) {           //如果是小数
      num = zero + '.' + new Array(l).join(zero) + coeff_array.join('');   //拼接字符串，如果是小数，拼接0和小数点
    } else {
      var dec = coeff_array[1];
      if(dec) l = l - dec.length;  //如果是整数，将整数除第一位之外的非零数字计入位数，相应的减少0的个数
      num = coeff_array.join('') + new Array(l+1).join(zero);    //拼接字符串，如果是整数，不需要拼接小数点
    }
  }
  return num;
}

//设置弹窗宽高['800px', '650px']
function openWidthHeight(width,height){
	var area = {};
	var wid,hei;
	if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
		wid = "90%";
		hei = "70%";
	} else {
		wid = width;
		hei = height;
	}
	//area = "'" + wid + "','" + hei +"'";
	area["width"] = wid;
	area["height"] = hei;
	return area;
}
