/*
*底部左右滚动广告1000字
* hcg
*/
var MyMar;
var scrollerAD = {
  start  : function (msg) {
    if (!msg){
      return;
    }
    // 查看当前最大宽度
    var maxWidth = document.body.clientWidth;
    var strWidth = msg.length * 22;
    maxWidth=strWidth>maxWidth?strWidth:maxWidth;
    // 如果存在先删除
    var deleteNode = document.getElementById("scrollerArea");
    deleteNode && deleteNode.remove();

    // dom 添加到body上
    document.body.insertAdjacentHTML("beforeend",
      '<div id="scrollerArea" style="overflow:hidden;height:50px;width:' + maxWidth + 'px;position: fixed;bottom: 0px;">\n' +
      '  <table cellpadding="0" cellspace="0" border="0">\n' +
      '    <tr>\n' +
      '      <td id="scrollerArea1">\n' +
      '        <table width="' + maxWidth + '" border="0" cellspacing="0" cellpadding="0">\n' +
      '          <tr>\n' +
      '            <td style="width:' + maxWidth + 'px;font-size: 35px;color: red;text-align: center;">' + msg + '</td>\n' +
      '          </tr>\n' +
      '        </table>\n' +
      '      </td>\n' +
      '      <td id="scrollerArea2"></td>\n' +
      '    </tr>\n' +
      '  </table>\n' +
      '</div>');

    // 滚起来
    var speed = 20;
    MyMar && clearInterval(MyMar);
    MyMar = setInterval(Marquee, speed);
    document.getElementById('scrollerArea2').innerHTML = document.getElementById('scrollerArea1').innerHTML;
    document.getElementById('scrollerArea').onmousedown = function () {
      clearInterval(MyMar)
    };
    document.getElementById('scrollerArea').onmouseup = function () {
      MyMar = setInterval(Marquee, speed)
    };

    function Marquee() {
      if (document.getElementById('scrollerArea2').offsetWidth - document.getElementById('scrollerArea').scrollLeft <= 0)
        document.getElementById('scrollerArea').scrollLeft -= document.getElementById('scrollerArea1').offsetWidth;
      else {
        document.getElementById('scrollerArea').scrollLeft++;
      }
    }
  },
  restart: function () {
    this.start(localStorage.getItem("ADStr"));
  }
};
scrollerAD.restart();
