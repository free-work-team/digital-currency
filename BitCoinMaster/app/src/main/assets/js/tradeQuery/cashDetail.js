
function queryCashDetail() {
  var id = localStorage.getItem("id");
  var reqData = JSON.stringify({
    "id"  : id
  });
  // console.log("查询提现详情入参=====" +reqData);
  result = JSON.parse(window.back.queryWithdrawDetail(reqData));
  //console.log("查询提现详情=====" + JSON.stringify(result));
  $("#channel").text(getWalletLabel(result.channel));
  $("#transactionId").text(result.transId);
  $("#receiveAddress").text(result.targetAddress);
  $("#bitcoin").text(formatCoin(result.amount)+getCoinLabel(result.cryptoCurrency));
  $("#cFee").text(formatCoin(result.cFee)+getCoinLabel(result.cryptoCurrency));
  $("#fee").text(formatCoin(result.fee)+getCoinLabel(result.cryptoCurrency));
  $("#amount").text(formatPrice(result.cash)+result.currency);
  $("#transactionTime").text(result.transTime);
  $("#status").text(getTransStatus(result.transStatus));
  $("#redeemStatus").text(getRedeemStatus(result.redeemStatus));
  $("#redeemTime").text(result.redeemTime);
  // $("#outCount").text(formatPrice(result.outCount)+result.currency);
  $("#sellType").text(getSellType(result.sellType));
  $("#price_sell").text(formatPrice(result.price)+result.currency);
  $("#strategy").text(getStrategy(result.strategy));
  $("#channelTransId").text(result.channelTransId);
  $("#confirmStatus").text(getConfirmStatus(result.confirmStatus));
  $("#exchangeStrategy").text(result.exchangeStrategy);
  $("#remark").text(result.remark);
  $("#customerId").text(result.customerId);

}

function getTransStatus(status) {
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

function getRedeemStatus(status) {
  switch (status) {
    case 0:
      return 'NO';
    case 1:
      return 'YES';
    default:
      return ''
  }
}

function getSellType(sellType) {
  if (sellType){
    return sellType+"-conf";
  }else{
    return '0-conf';
  }
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

function toInfoList() {
  window.location.href = "infoList.html";
}

function getConfirmStatus(status) {
  switch (status) {
    case 0:
      return 'Pending';
    case 1:
      return 'Confirmed';
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
