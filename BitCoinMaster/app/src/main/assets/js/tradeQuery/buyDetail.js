/**
 * 点击查询取详情
 */
function queryBuyDetail() {
  var id = localStorage.getItem("id");
  var reqData = JSON.stringify({
    "id"  : id
  });
  result = JSON.parse(window.back.queryBuyDetail(reqData));
  //console.log("查询购买详情=====" + JSON.stringify(result));
  $("#transactionId").text(result.transId);
  $("#receiveAddress").text(result.address);
  $("#bitcoin").text(formatCoin(result.amount)+getCoinLabel(result.cryptoCurrency));
  $("#cFee").text(formatCoin(result.channelFee)+getCoinLabel(result.cryptoCurrency));
  $("#fee").text(formatCoin(result.fee)+getCoinLabel(result.cryptoCurrency));
  $("#amount").text(formatPrice(result.cash)+result.currency);
  $("#transactionTime").text(result.transTime);
  $("#status").text(getStatusLabel(result.status));
  $("#remark").text(result.remark);
  $("#channel").text(getWalletLabel(result.channel));
  $("#price_buy").text(formatPrice(result.price)+result.currency);
  $("#strategy").text(getStrategy(result.strategy));
  $("#channelTransId").text(result.channelTransId);
  $("#exchangeStrategy").text(result.exchangeStrategy);
  $("#customerId").text(result.customerId);
}
function toInfoList() {
  window.location.href = "infoList.html";
}

function getStrategy(strategy) {
  switch (strategy) {
    case "0":
      return '';
    case "1":
      return 'Coinbase Pro';
    case "2":
      return 'Kraken';
    default:
      return ''
  }
}

function getStatusLabel(status) {
  switch (status) {
    case '0':
      return 'Init';
    case '1':
      return 'Pending';
    case '2':
      return 'Confirmed';
    case '3':
      return 'Fail';
    case '4':
      return 'Error';
    default:
      return ''
  }
}

function getWalletLabel(wallet) {
  switch (wallet) {
    case '2':
      return 'Coinbase';
    case '3':
      return 'Blockchain';
    case '1':
      return 'Bitgo';
    default:
      return wallet;
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

