// var baseUrl = 'https://test.bitgo.com/api';

//登录
function login() {
	var account = $("#account").val();
	var password = $("#password").val();
	// var verificationCode = $("#verificationCode").val();
	if(account == null || account== ""){
    showWarn("please enter your account！");
		return;
	}
	if(password == null || password== ""){
    showWarn("please enter your password！");
		return;
	}
	// if(!validate()){
	//   return;
	// }
	var reqData = JSON.stringify({
	    "account" : account,
	    "password" : password
	 });
	 localStorage.setItem("user", reqData);
	 var respData = window.back.login(reqData);
	 if(respData){
		 window.location.href="index.html";
	 }else{
		 showError("Invalid account or password.");
	 }
	 //respData = JSON.parse(respData);
	 //return respData;
}
//重置
function reset() {
	window.location.reload();
}


// var code; //在全局 定义验证码
// function createCode() {
// code = "";
// var codeLength = 4;//验证码的长度
// var checkCode = document.getElementById("checkCode");
// var selectChar = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');//所有候选组成验证码的字符，当然也可以用中文的
//
// for (var i = 0; i < codeLength; i++) {
// 	var charIndex = Math.floor(Math.random() * 36);
// 		code += selectChar[charIndex];
// 	}
// 	if (checkCode) {
// 		checkCode.className = "code";
// 		checkCode.value = code;
// 		//Test
//    //$("#verificationCode").val(code);
// 	}
// }

// function validate() {
// 	var inputCode = document.getElementById("verificationCode").value;
// 	if (inputCode.length <= 0) {
// 		showError("please enter the verification code！");
// 		return false;
// 	}else if (inputCode.toUpperCase() !== code.toUpperCase()) {
// 		showError("verification code error！");
// 		createCode();//刷新验证码
// 		return false;
// 	}
//   return true;
// }

// function showError(msg){
//   $('#msg').text(msg);
// }
