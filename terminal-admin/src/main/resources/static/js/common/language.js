// 多语言 byhcg
/*使用方法
* 1.给便签添加class ="lanClass"
* 2.给标签添加data-lanid="数据库中多语言的id"
* 3.个别动态的获取调用getSingleLanguage(lanid);
*
* */

$(function () {

  function init() {
    var currentType = sessionStorage.getItem('currentType');
    if (!currentType) {
      sessionStorage.setItem('currentType', 'lanEnglish');
      currentType = 'lanEnglish';
    }
    $("#languageSelect option[value= " + currentType + "]").attr("selected", "selected");
    renderLabel();
  }

  function events() {
    $('#languageSelect').unbind('change').on('change', function () {
      currentType = $('#languageSelect').val();
      sessionStorage.setItem('currentType', currentType);
      renderLabel();
      window.location.href=window.location.href;
      window.location.reload;
    });
  }

  function renderLabel() {
    var currentType = sessionStorage.getItem('currentType');
    if (!currentType) {
      sessionStorage.setItem('currentType', 'lanEnglish');
      currentType = 'lanEnglish';
    }
    $.each($(".lanClass"), function (index, item) {
      var lanId = $(item).attr('data-lanid');
      if (lanId){
        var lanItemStr = sessionStorage.getItem(lanId);
        if (lanItemStr) {
          var lanItem = JSON.parse(lanItemStr);
          $(item).text(lanItem[currentType]);
        }
      }
    });
  }

  init();
  events();
});

// 单独获取
function getSingleLanguage(lanId) {
  var currentType = sessionStorage.getItem('currentType');
  if (!currentType) {
    sessionStorage.setItem('currentType', 'lanEnglish');
    currentType = 'lanEnglish';
  }
  var lanValue = sessionStorage.getItem(lanId);
  return lanValue ? JSON.parse(lanValue)[currentType]:"";
}
