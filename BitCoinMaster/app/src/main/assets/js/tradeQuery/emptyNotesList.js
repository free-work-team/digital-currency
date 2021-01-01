$(function () {
  var currentPage  = 1,
      maxPageNum   = 1,
      totalNum     = 0,
      everyPageNum = 10,
      resultList   = [];

  function init() {
    initLanguage();
    renderPage();
    searchList("");
  }

  function event() {

    //back
    $('.to_back_btn').on('click', function () {
      window.location.href = "../index.html";
    });



    $('.searchBtn').on('click', function () {
      currentPage = 1;
      var reqData = "";
      var startTime = '';
      var endTime = '';

      startTime = $("#startTime_buy").val();
      endTime = $("#endTime_buy").val();
      if (startTime > endTime) {
        showWarn("startTime cannot be greater than the endTime !");
        return;
      }
      reqData = JSON.stringify({
        "start_time": startTime?(startTime +" 00:00:00"):"",
        "end_time"  : endTime?(endTime +" 23:59:59"):""
      });

      searchList(reqData);
    })
  }

  function searchList(reqData) {

    resultList = JSON.parse(window.back.queryEmptyNotesList(reqData));

    everyPageNum = parseInt($("#pageSize option:selected").val());

    totalNum = resultList.length;
    maxPageNum = parseInt(totalNum / everyPageNum) + (totalNum % everyPageNum > 0 ? 1 : 0);

    //加载列表数据
    showList();
    renderPage();
  }

  // 渲染列表
  function showList() {
    var html = "";
    if (resultList.length) {
      $.each(resultList, function (index, item) {
        if ((currentPage - 1) * everyPageNum <= index && index < currentPage * everyPageNum) {
          html += '<tr>'
            + '<td >' + (index+1) + '</td>'
            + '<td >' + (item.createTime || "") + '</td>'
            + '<td >' + ((formatPrice(item.addCash)) + getCurrency()) + '</td>'
            + '<td >' + ((formatPrice(item.buyCash)) + getCurrency()) + '</td>'
            + '<td >' + ((formatPrice(item.sellCash))+ getCurrency()) + '</td>'
            + '</tr>';
        }
      });
    } else {
      html += '<tr><td colspan = "9" rowspan="2">' + 'No data!' + '</td></tr>';
    }
    $('#table_data_buy').html(html);



  }
  function getStatusLabel(status) {
    switch (status) {
      case 0:
        return 'init';
      case 1:
        return 'pending';
      case 2:
        return 'confirm';
      default:
        return ''
    }
  }
  function getProStatusLabel(status) {
    switch (status) {
      case '3':
        return 'fail';
      case '0':
        return 'create';
      case '1':
        return 'pending';
      case '2':
        return 'confirm';
      case '4':
        return 'cancel';
      default:
        return ''
    }
  }
  //初始化语言label
  function initLanguage() {
    // id名必须和language key 相同
   var LanguageObjs = ["lan_emptyTime","lan_addCash","lan_buyCash","lan_sellCash","lan_transactionTime_buy_q","lan_emptyNotes"];
   LanguageManager.getLanguageLabel(LanguageObjs);
  }

  // 渲染页码
  function renderPage() {
    $("#page").paging({
      pageNum  : currentPage, // 当前页面
      totalNum : maxPageNum, // 总页码
      totalList: totalNum, // 记录总数量
      callback : function (num) { //回调函数
        currentPage = num;
        showList();
        $("#pageSize").val(everyPageNum);
      }
    });
    $("#pageSize").val(everyPageNum);
  }

  //重置页码
  function resetPage() {
    currentPage = 1;
    maxPageNum = 1;
    totalNum = 0;
    everyPageNum = 10;
    resultList = [];
    $("#address_buy").val('');
    $("#startTime_buy").val('');
    $("#endTime_buy").val('');
    $("#targetAddress").val('');
    $("#startTime_sell").val('');
    $("#endTime_sell").val('');
    $("#transId_order").val('');
    $("#side").val('');
    $("#startTime_order").val('');
    $("#endTime_order").val('');
    searchList("");
  }

  init();
  event();
});
