$(function () {
  var currentPage  = 1,
      maxPageNum   = 1,
      totalNum     = 0,
      everyPageNum = 10,
      resultList   = [],
      currentTab   = 1,
      totalTranObj = [];//总交易金额{c:'CNY',t:100,f:20}

  function init() {
    initLanguage();
    /*判断单双向*/
    if(window.business.getTranWay() == 2){//双向
      $('#doubleWay').removeClass('hidden');
      $('.tabs li').css('width','33.3%');
    }

    renderPage();
    searchList("");
  }

  function event() {
    $('.tabClick').on('click', function () {
      currentTab = parseInt($(this).attr('tabindex'));
      resetPage();
    });
    //back
    $('.to_back_btn').on('click', function () {
      window.location.href = "../index.html";
    });


    $('.searchBtn').on('click', function () {
      currentPage = 1;
      var reqData = "";
      currentTab = parseInt($(this).attr('data-index'));
      var startTime = '',
          endTime   = '',
          address   = '',
          transId   = '',
          coin      = '',
          status      = '';
      //买币
      if (currentTab === 1) {
        address = $("#address_buy").val();
        startTime = $("#startTime_buy").val();
        endTime = $("#endTime_buy").val();
        transId = $("#transId_buy").val();
        coin = $("#coin_buy").val();
        status = $("#status_buy").val();
        // if (startTime > endTime) {
        //   showWarn("startTime cannot be greater than the endTime !");
        //   return;
        // }
        reqData = JSON.stringify({
          "trans_id"       : transId,
          "address"        : address,
          "crypto_currency": coin,
          "status"         : status,
          "start_time"     : startTime ? (startTime + " 00:00:00") : "",
          "end_time"       : endTime ? (endTime + " 23:59:59") : ""
        });
      }
      //卖币
      if (currentTab === 2) {
        var targetAddress = $("#targetAddress").val();
        startTime = $("#startTime_sell").val();
        endTime = $("#endTime_sell").val();
        transId = $("#transId_sell").val();
        coin = $("#coin_sell").val();
        status = $("#status_sell").val();


        // if (startTime > endTime) {
        //   showWarn("startTime cannot be greater than the endTime !");
        //   return;
        // }
        reqData = JSON.stringify({
          "trans_id"       : transId,
          "target_address" : targetAddress,
          "crypto_currency": coin,
          "status"         : status,
          "start_time"     : startTime ? (startTime + " 00:00:00") : "",
          "end_time"       : endTime ? (endTime + " 23:59:59") : ""
        });
      }
      // 订单
      if (currentTab === 3) {
        transId = $("#transId_order").val();
        var side = $("#side").val();
        startTime = $("#startTime_order").val();
        endTime = $("#endTime_order").val();
        coin = $("#coin_order").val();
        status = $("#status_order").val();
        // if (startTime > endTime) {
        //   showWarn("startTime cannot be greater than the endTime !");
        //   return;
        // }
        reqData = JSON.stringify({
          "transId"        : transId,
          "side"           : side,
          "crypto_currency": coin,
          "status"         : status,
          "start_time"     : startTime ? (startTime + " 00:00:00") : "",
          "end_time"       : endTime ? (endTime + " 23:59:59") : ""
        });
      }
      searchList(reqData);
    })
  }

  function searchList(reqData) {
    if (currentTab === 1) {
      resultList = JSON.parse(window.back.queryBuyLogList(reqData));
    }
    if (currentTab === 2) {
      resultList = JSON.parse(window.back.queryWithdrawLogList(reqData));
    }
    if (currentTab === 3) {
      resultList = JSON.parse(window.back.queryOrderList(reqData));
    }
    // resultList = JSON.parse('[{\n' +
    //   '    "address": "3QEotKR2YWic2h3Ls6bpENUKW66rjTC3LD",\n' +
    //   '    "amount": "12885",\n' +
    //   '    "cash": "10",\n' +
    //   '    "channel": "2",\n' +
    //   '    "channelFee": "929",\n' +
    //   '    "cryptoCurrency": "btc",\n' +
    //   '    "currency": "CNY",\n' +
    //   '    "customerId": "1051937614@qq.com",\n' +
    //   '    "exchangeRate": "",\n' +
    //   '    "exchangeStrategy": 0,\n' +
    //   '    "fee": "1593",\n' +
    //   '    "id": 1,\n' +
    //   '    "price": "69071.63",\n' +
    //   '    "status": "2",\n' +
    //   '    "terminalNo": "t00000001",\n' +
    //   '    "transId": "159177397811500000001",\n' +
    //   '    "transTime": "2020-06-10 15:26:18"\n' +
    //   '}, {\n' +
    //   '    "address": "3QEotKR2YWic2h3Ls6bpENUKW66rjTC3LD",\n' +
    //   '    "amount": "12885",\n' +
    //   '    "cash": "10",\n' +
    //   '    "channel": "2",\n' +
    //   '    "channelFee": "929",\n' +
    //   '    "cryptoCurrency": "btc",\n' +
    //   '    "currency": "USD",\n' +
    //   '    "customerId": "1051937614@qq.com",\n' +
    //   '    "exchangeRate": "",\n' +
    //   '    "exchangeStrategy": 0,\n' +
    //   '    "fee": "1593",\n' +
    //   '    "id": 1,\n' +
    //   '    "price": "9000.63",\n' +
    //   '    "status": "2",\n' +
    //   '    "terminalNo": "t00000001",\n' +
    //   '    "transId": "159177397811500000001",\n' +
    //   '    "transTime": "2020-06-10 15:26:18"\n' +
    //   '}, {\n' +
    //   '    "address": "3QEotKR2YWic2h3Ls6bpENUKW66rjTC3LD",\n' +
    //   '    "amount": "12885",\n' +
    //   '    "cash": "10",\n' +
    //   '    "channel": "2",\n' +
    //   '    "channelFee": "929",\n' +
    //   '    "cryptoCurrency": "btc",\n' +
    //   '    "currency": "CNY",\n' +
    //   '    "customerId": "1051937614@qq.com",\n' +
    //   '    "exchangeRate": "",\n' +
    //   '    "exchangeStrategy": 0,\n' +
    //   '    "fee": "1593",\n' +
    //   '    "id": 1,\n' +
    //   '    "price": "69071.63",\n' +
    //   '    "status": "2",\n' +
    //   '    "terminalNo": "t00000001",\n' +
    //   '    "transId": "159177397811500000001",\n' +
    //   '    "transTime": "2020-06-10 15:26:18"\n' +
    //   '}, {\n' +
    //   '    "address": "3QEotKR2YWic2h3Ls6bpENUKW66rjTC3LD",\n' +
    //   '    "amount": "12885",\n' +
    //   '    "cash": "12",\n' +
    //   '    "channel": "2",\n' +
    //   '    "channelFee": "929",\n' +
    //   '    "cryptoCurrency": "btc",\n' +
    //   '    "currency": "EUR",\n' +
    //   '    "customerId": "1051937614@qq.com",\n' +
    //   '    "exchangeRate": "",\n' +
    //   '    "exchangeStrategy": 0,\n' +
    //   '    "fee": "1593",\n' +
    //   '    "id": 1,\n' +
    //   '    "price": "7000.63",\n' +
    //   '    "status": "2",\n' +
    //   '    "terminalNo": "t00000001",\n' +
    //   '    "transId": "159177397811500000001",\n' +
    //   '    "transTime": "2020-06-10 15:26:18"\n' +
    //   '}]');

    everyPageNum = parseInt($("#pageSize option:selected").val());
    totalNum = resultList.length;
    maxPageNum = parseInt(totalNum / everyPageNum) + (totalNum % everyPageNum > 0 ? 1 : 0);

    //加载列表数据
    showList();
    renderPage();
  }

  // 渲染列表
  function showList() {
    totalTranObj = [];//清空
    var html = "";
    if (currentTab === 1) {
      if (resultList.length) {
        $.each(resultList, function (index, item) {
          var transCurrencyFee = numMulti(formatCoin(item.fee), item.price);
          setTotalTransAmount(item.currency, item.cash, transCurrencyFee);
          if ((currentPage - 1) * everyPageNum <= index && index < currentPage * everyPageNum) {
            html += '<tr>'
              + '<td>' + (item.transId || "") + '</td>'
              + '<td style="width: 115px;">' + ((formatPrice(item.price)) + item.currency) + '</td>'
              + '<td style="width: 100px;">' + ((formatPrice(item.cash)) + item.currency) + '</td>'
              + '<td style="width: 100px;">' + (formatCoin(item.amount)) + getCoinLabel(item.cryptoCurrency) + '</td>'
              + '<td style="width: 100px;">' + (formatPrice(numMulti(formatCoin(item.fee), item.price))) + item.currency + '</td>'
              + '<td style="width: 100px;">' + (item.transTime || "") + '</td>'
              + '<td style="width: 80px;">' + getStatusLabel(item.status) + '</td>'
              + '<td style="width: 85px; " class="toDetail" data-detail="' + item.id + '"><a >Details</a></td>'
              + '</tr>';
          }
        });
      } else {
        html += '<tr><td colspan = "9" rowspan="2">' + 'No data!' + '</td></tr>';
      }
      $('#table_data_buy').html(html);
    }
    if (currentTab === 2) {
      if (resultList.length) {
        $.each(resultList, function (index, item) {
          var transCurrencyFee = numMulti(formatCoin(item.fee), item.price);
          setTotalTransAmount(item.currency, item.cash, transCurrencyFee);
          if ((currentPage - 1) * everyPageNum <= index && index < currentPage * everyPageNum) {
            html += '<tr>'
              + '<td>' + (item.transId || "") + '</td>'
              + '<td style="width: 115px;">' + ((formatPrice(item.price)) + item.currency) + '</td>'
              + '<td style="width: 100px;">' + ((formatPrice(item.cash)) + item.currency) + '</td>'
              + '<td style="width: 100px;">' + (formatCoin(item.amount)) + getCoinLabel(item.cryptoCurrency) + '</td>'
              + '<td style="width: 100px;">' + (formatPrice(numMulti(formatCoin(item.fee), item.price))) + item.currency + '</td>'
              + '<td style="width: 70px;">' + (item.redeemStatus ? "YES" : "NO") + '</td>'
              + '<td style="width: 100px;">' + (item.transTime || "") + '</td>'
              + '<td style="width: 80px;">' + getStatusLabel(item.transStatus) + '</td>'
              + '<td style="width: 85px; " class="toDetail" data-detail="' + item.id + '"><a>Details</a></td>'
              + '</tr>';
          }
        });
      } else {
        html += '<tr><td colspan = "9" rowspan="2">' + 'No data!' + '</td></tr>';
      }
      $('#table_data_sell').html(html);
    }
    if (currentTab === 3) {
      if (resultList.length) {
        $.each(resultList, function (index, item) {
          if ((currentPage - 1) * everyPageNum <= index && index < currentPage * everyPageNum) {
            html += '<tr>'
              + '<td style="">' + (item.transId || "") + '</td>'
              + '<td style="width: 80px;">' + (item.side || "") + '</td>'
              + '<td style="width: 80px;">' + item.type + '</td>'
              + '<td style="width: 160px;">' + (item.size && item.size + getCoinLabel(item.cryptoCurrency)) + '</td>'
              + '<td style="width: 80px;">' + ((formatPrice(item.funds)) + item.currency) + '</td>'
              + '<td style="width: 120px;">' + (item.createTime || "") + '</td>'
              + '<td style="width: 80px;">' + getProStatusLabel(item.status) + '</td>'
              + '<td style="width: 85px; " class="toDetail" data-detail="' + item.transId + '"><a>Details</a></td>'
              + '</tr>';
          }
        });
      } else {
        html += '<tr><td colspan = "9" rowspan="2">' + 'No data!' + '</td></tr>';
      }
      $('#table_data_order').html(html);
    }

    //detail
    $('.toDetail').unbind('click').on('click', function () {
      var id = $(this).attr('data-detail');
      localStorage.setItem("id", id);
      if (currentTab === 1) {
        window.location.href = "../../pages/tradeQuery/buyDetail.html";
      }
      if (currentTab === 2) {
        window.location.href = "../../pages/tradeQuery/cashDetail.html";
      }
      if (currentTab === 3) {
        window.location.href = "../../pages/tradeQuery/orderDetail.html";
      }
    });


    //渲染统计内容
    renderTotalArea();
  }

  //统计对象
  function setTotalTransAmount(currency, total, fee) {
    //重组totalTranObj
    var haveInsertD = false;
    $.each(totalTranObj, function (index, item) {
      if (item.c == currency) {
        haveInsertD = true;
        totalTranObj[index].t = numAdd(totalTranObj[index].t, total);
        totalTranObj[index].f = numAdd(totalTranObj[index].f, fee);
      }
    });
    // 没有then插入
    if (!haveInsertD) {
      totalTranObj.push({
        c: currency,
        t: total,
        f: fee
      })
    }
  }

  //渲染统计内容
  function renderTotalArea() {
    $('#totalStrArea').html('');
    var htmlStr = '';
    $.each(totalTranObj, function (index, item) {
      htmlStr += '<strong style="font-weight: bolder;font-size: 15px">Total('+item.c+'):' +
        '&emsp;Amount: <strong style="color: red">'+formatPrice(item.t)+'</strong> ' +
        '&emsp;Fee: <strong style="color: red">'+formatPrice(item.f)+'</strong></strong><br>';
    });
    $('#totalStrArea').html(htmlStr);
  }

  function getStatusLabel(status) {
    status = parseInt(status);
    switch (status) {
      case 0:
        return 'Init';
      case 1:
        return 'Pending';
      case 2:
        return 'Confirmed';
      case 3:
        return 'Fail';
      case 4:
        return 'Error';
      default:
        return ''
    }
  }

  function getCoinLabel(coin) {
    switch (coin) {
      case 'btc':
        return ' BTC';
      case 'eth':
        return ' ETH';
      default:
        return ''
    }
  }


  function getProStatusLabel(status) {
    switch (status) {
      case '0':
        return 'Create';
      case '1':
        return 'Pending';
      case '2':
        return 'Confirmed';
      case '3':
        return 'Fail';
      case '4':
        return 'Cancel';
      default:
        return ''
    }
  }

  //初始化语言label
  function initLanguage() {
    // id名必须和language key 相同
    var LanguageObjs = ["lan_status_buy_query","lan_status_sell_query","lan_status_order_query","lan_buyLogList", "lan_coin_order_query", "lan_coin_sell_query", "lan_coin_buy_query", "lan_address_buy", "lan_transId_buy", "lan_transId_buy_list", "lan_price_buy", "lan_price_sell", "lan_exchange_type", "lan_transId_sell", "lan_transId_sell_list", "lan_transactionTime_buy", "lan_transactionTime_buy_q", "lan_status_buy", "lan_receive_address", "lan_Bitcoin_buy", "lan_fee_buy", "lan_amount", "lan_transactionTime_buy", "lan_buy_status", "lan_operate_buy", "lan_all",
      "lan_withdrawLogList", "lan_transactionId_sell", "lan_transactionTime_sell", "lan_transactionTime_sell_q", "lan_targetAddress", "lan_cash", "lan_Bitcoin_sell", "lan_fee_sell", "lan_status", "lan_transactionTime_sell", "lan_sell_status", "lan_operate_sell",
      "lan_orderList", "lan_transactionId_order", "lan_createTime_order", "lan_createTime_order_q", "lan_order_side_query", "lan_order_side_query", "lan_transactionId2", "lan_side", "lan_size", "lan_funds", "lan_productId", "lan_order_status", "lan_operate_order", "lan_all_order"];
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
    $("#transId_buy").val('');
    $("#address_buy").val('');
    $("#startTime_buy").val('');
    $("#endTime_buy").val('');
    $("#coin_buy").val('');
    $("#status_buy").val('');

    $("#transId_sell").val('');
    $("#targetAddress").val('');
    $("#startTime_sell").val('');
    $("#endTime_sell").val('');
    $("#coin_sell").val('');
    $("#status_sell").val('');

    $("#transId_order").val('');
    $("#startTime_order").val('');
    $("#endTime_order").val('');
    $("#side").val('');
    $("#coin_order").val('');
    $("#status_order").val('');

    searchList("");
  }

  init();
  event();
});
