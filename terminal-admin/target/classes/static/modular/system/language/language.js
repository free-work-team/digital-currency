/**
 * 多语言l管理的单例
 */
var Language = {
  id        : "languageTable",	//表格id
  seItem    : null,		//选中的条目
  table     : null,
  layerIndex: -1
};

/**
 * 初始化表格的列visible: false,
 */
Language.initColumn = function () {
  var columns = [
    {field: 'selectItem', radio: true,width:'36px'},
    {title: 'id', field: 'id', align: 'center', valign: 'middle',width:'200px'},
    {title: 'English', lanId:'englishOption', field: 'lanEnglish', align: 'center', valign: 'middle', sortable: true,width:'300px'},
    {title: 'Chinese', lanId:'chineseOption', field: 'lanChinese', align: 'center', valign: 'middle', sortable: true,width:'300px'}];
  return columns;
};


/**
 * 关键字搜索
 */
Language.search = function () {
  var queryData = {};
  queryData['keyword'] = $("#keyword").val();
  Language.table.refresh({query: queryData});
};


/**
 * 检查是否选中
 */
Language.check = function () {
  var selected = $('#' + this.id).bootstrapTable('getSelections');
  if (!selected.length) {
    Feng.info(getSingleLanguage('pleaseSelect')||"请先选中表格中的某一记录！");
    return false;
  } else {
    Language.seItem = selected[0];
    return true;
  }
};

/**
 * 点击添加
 */
Language.openAddView = function () {
    var url = Feng.ctxPath + '/language/add';
    var area = openWidthHeight("60%","50%");
    var index = layer.open({
      type   : 2,
      title  : getSingleLanguage('add')||'添加',
      area   : [area.width, area.height], //宽高
      fix    : false, //不固定
      maxmin : true,
      content: url
    });
    this.layerIndex = index;
};
/**
 * 点击修改按钮时
 */
Language.update = function () {
  if (this.check()) {
    var url = Feng.ctxPath + '/language/update/' + this.seItem.id;
    var area = openWidthHeight("60%","50%");
    var index = layer.open({
      type   : 2,
      title  : getSingleLanguage('editLanguage')||'修改',
      area   : [area.width, area.height], //宽高
      fix    : false, //不固定
      maxmin : true,
      content: url
    });
    this.layerIndex = index;
  }
};

Language.close = function () {
  //this.layer.close(Language.layerIndex);
  parent.layer.close(window.parent.Language.layerIndex);
};
//新增提交
Language.addSubmit = function () {
  var lanEnglish = $('#lanEnglish').val(),
      lanChinese = $('#lanChinese').val();

  if (!lanEnglish || !lanChinese) {
    Feng.error("Improve information !");
    return;
  }
  var languageData = {
    id        : $('#languageId').val(),
    lanEnglish: lanEnglish,
    lanChinese: lanChinese
  };
  var ajax = new $ax("/language/add", function (data) {
    Feng.success(getSingleLanguage('success')||"成功!");
    window.parent.Language.table.refresh();
    Language.close();
  }, function (data) {
	  var editFail = getSingleLanguage('fail')||"失败!";
      Feng.error(editFail + data.responseJSON.message + " !");
  });
  ajax.set(languageData);
  ajax.start();
};
// 更新操作
Language.updateInfo = function () {
  var lanEnglish = $('#lanEnglish').val(),
      lanChinese = $('#lanChinese').val();

  if (!lanEnglish || !lanChinese) {
    Feng.error("Improve information !");
    return;
  }
  var languageData = {
    id        : $('#languageId').val(),
    lanEnglish: lanEnglish,
    lanChinese: lanChinese
  };
  var ajax = new $ax("/language/update", function (data) {
    Feng.success(getSingleLanguage('updateSuccess')||"修改成功!");
    window.parent.Language.table.refresh();
    Language.close();
  }, function (data) {
	  var editFail = getSingleLanguage('editFail')||"修改失败!";
      Feng.error(editFail + data.responseJSON.message + " !");
  });
  ajax.set(languageData);
  ajax.start();
};

$(function () {
  var defaultColunms = Language.initColumn();
  var table = new BSTable(Language.id, "/language", defaultColunms);
  table.setPaginationType("server");
  table.init();
  Language.table = table;
});
