//定义存放图片对象的数组
var uploadImgArr = [];
//得到上传的文件路径
var filePath = "";


/**
 * 定义获取图片信息的函数
 * */
function getFiles(e, width, height) {
  var that = $(e);
  var imageBox = that.parents('.pic_list').parents('.layui-input-block');
  e = e || window.event;
  //获取file input中的图片信息列表
  var files = e.files,
      //验证是否是图片文件的正则
      reg   = /image\/.*/i;
  f = files[0];
  //把这个if判断去掉后，也能上传别的文件
  if (!reg.test(f.type)) {
    $("#imageBox").empty();
    imageBox.html("<li>你选择的" + f.name + "文件不是图片</li>");
  } else {
    imageBox.find("li") && imageBox.find("li").remove();
  }
  fileResizetoFile(f,1,function(res){
	  console.log(res);
	  //拿到res，做出你要上传的操作；
	  f = res;
	  uploadImgArr.push(f);

	  var reader = new FileReader();
	  //类似于原生JS实现tab一样（闭包的方法），参见http://www.css119.com/archives/1418
	  reader.onload = (function (file) {
	    //获取图片相关的信息
	    var fileSize = (file.size / 1024).toFixed(2) + "K",
	        fileName = file.name,
	        fileType = file.type;
	    //console.log(fileName)
	    return function (e) {
	      //console.log(e.target)
	      //获取图片的宽高
	      var img = new Image();
	      img.addEventListener("load", imgLoaded, false);
	      img.src = e.target.result;

	      function imgLoaded() {
	        imgWidth = width + "px";
	        imgHeight = height?(height + "px"):'auto';
	        //var marginLeft = "3px";
	        //var marginTop = "6px";
	        //图片加载完成后才能获取imgWidth和imgHeight
	        imageBox.html("<img id=" + fileName + " src=" + e.target.result + " alt="
	          + fileName + " data-fileIndex=" + (uploadImgArr.length - 1) + " title="
	          + fileName + " style=width:" + imgWidth + ";height:" + imgHeight +";>");

	      }

	    }
	  })(f);
	  //读取文件内容
	  reader.readAsDataURL(f);
	  var id = that.parents('.content_photo').children('div').attr('id');
	  $("#cancle-"+id).attr("style","display:block;");//显示删除图片x
	})
 
}


/**
 * 定义获取视频信息的函数
 * */
/*function getVideoFiles(e, width, height) {
  var that = $(e);
  var imageBox = that.parents('.pic_list').children('.imageBox');
  e = e || window.event;
  //获取file input中的图片信息列表
  var files = e.files,
      //验证是否是图片文件的正则
      reg   = /\w+(.flv|.rvmb|.mp4|.avi|.wmv)$/;
  f = files[0];
  //把这个if判断去掉后，也能上传别的文件
  if (!reg.test(f.type)) {
    $("#imageBox").empty();
    imageBox.html("<li>你选择的" + f.name + "文件不是视频</li>");
  } else {
    imageBox.find("li") && imageBox.find("li").remove();
  }
  uploadImgArr.push(f);

  var reader = new FileReader();
  //类似于原生JS实现tab一样（闭包的方法），参见http://www.css119.com/archives/1418
  reader.onload = (function (file) {
    //获取图片相关的信息
    var fileSize = (file.size / 1024).toFixed(2) + "K",
        fileName = file.name,
        fileType = file.type;
    //console.log(fileName)
    return function (e) {
      //获取图片的宽高
      videoLoaded();

      function videoLoaded() {
        imgWidth = width + "px";
        imgHeight = height + "px";
        var marginLeft = "3px";
        var marginTop = "6px";
        //图片加载完成后才能获取imgWidth和imgHeight
        imageBox.html("<video  src=" + e.target.result + " data-fileIndex= " + (uploadImgArr.length - 1) + " style='width:" + imgWidth + ";height:" + imgHeight
          + ";margin-left:" + marginLeft + ";margin-top:" + marginTop + ";float:left;' controls='controls'>your browser does not support the video tag</video>");
      }
    }
  })(f);
  //读取文件内容
  reader.readAsDataURL(f);
}*/


/**
 * 删除上传照片
 */
function cancleFile(e) {
  var that = $(e);
  var imageBox = that.parents('.content_photo').children('div');
  var divId = that.parents('.content_photo').children('div').attr('id');
  id = divId.replace("Div","");
  //imageBox.empty();
  imageBox.html('<div class="pic_list">'
		  + '<span class="btn btn-successed fileinput-button" style="width: 100%;height: 100%;">'
		  + '<input type="file" accept="image/*" capture="camera" id="' + id + '" class="filesInput" onchange="getFiles(this,960,218)"/></span></div>');
  $("#cancle-"+divId).attr("style","display:none;");//隐藏删除图片按钮的x
}

//开始上传照片
/**
 * 上传图片util
 * @param imgWidth 200
 * @param imgHeight 200
 * @param fileHeader /item
 * @param fileIndex
 * @returns {string}
 */
function commonUploadFun(fileIndex) {
	
  //var fileName ="";
  function fun() {
    if (uploadImgArr.length > 0) {
      var singleImg = uploadImgArr[fileIndex];
      var xhr = new XMLHttpRequest();
      if (xhr.upload) {
    	// 文件上传成功或是失败
	      xhr.onreadystatechange = function () {
	        if (xhr.readyState === 4) {
	          var resultValue = "";
	          if (xhr.status === 200) {
	            resultValue = xhr.responseText;
	            resultValue = resultValue.replace("\"", "").replace("\"", "");
	            filePath = resultValue;
	          }
	        }
	      };
        
//        var date = new Date();
//        var timestamp = date.valueOf();
//        var random = Math.random().toString(36).substr(2,4);
//        var y = date .getFullYear();
//        var m = date .getMonth() + 1;
//        var d = date .getDate();
//        
//        fileName = "/img/cust/"+ y + "/" + m + "/" + d + "/" +  timestamp+random+".jpg";
        //formdata.append("imgWidth", imgWidth);
        //formdata.append("imgHeight", imgHeight);
        //formdata.append("fileHeader", fileHeader);
        
        var formdata = new FormData();
        formdata.append("FileData", singleImg);
       // formdata.append("filename", fileName);
        // 开始上传
        xhr.open("POST", "/kyc/kycImage_upload", false);
        xhr.send(formdata);
      }
    }
  }
  fun();
  return filePath;
  
}
function filetoDataURL(file,fn){
  var reader = new FileReader();
  reader.onloadend = function(e){
    fn(e.target.result);
  };
  reader.readAsDataURL(file);
};
function dataURLtoImage(dataurl,fn){
  var img = new Image();
  img.onload = function() {
    fn(img);
  };
  img.src = dataurl;
};
function canvasResizetoFile(canvas,quality,fn){
  canvas.toBlob(function(blob) {
    fn(blob);
  },'image/jpeg',quality);
};
function imagetoCanvas(image){
	  var cvs = document.createElement("canvas");
	  var ctx = cvs.getContext('2d');
	  cvs.width = image.width/4.5;
	  cvs.height = image.height/4.5;
	  ctx.drawImage(image, 0, 0, cvs.width, cvs.height);
	  return cvs ;
	};
function fileResizetoFile(file,quality,fn){
  filetoDataURL (file,function(dataurl){
    dataURLtoImage(dataurl,function(image){
      canvasResizetoFile(imagetoCanvas(image),quality,fn);
    })
  })
}
/*
function summernoteUpload(file, item) {
  data = new FormData();
  data.append("FileData", file);
  data.append("fileHeader", "summernote");
  data.append("imgWidth", 400);
  data.append("imgHeight", 300);
  console.log(data);
  $.ajax({
    data       : data,
    type       : "POST",
    url        : Feng.ctxPath + "/image/commonImage_upload",
    cache      : false,
    contentType: false,
    processData: false,
    success    : function (url) {
      $(item).summernote('insertImage', url, "test");
    }
  });
}
*/