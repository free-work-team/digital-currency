/**
 * 订单详情
 */
function queryOrderDetail() {
  var id = localStorage.getItem("id");
  result = JSON.parse(window.back.queryOrderDetail(id));
  //console.log("查询订单详情=====" + JSON.stringify(result));
  $("#transId").text(result.transId);
  $("#orderId").text(result.id);
  $("#size").text(result.size && (result.size +getCoinLabel(result.cryptoCurrency)));
  $("#price").text(formatPrice(result.price));
  $("#funds").text(formatPrice(result.funds) + result.currency);
  $("#productId").text(result.productId);
  $("#side").text(result.side);
  $("#type").text(result.type);
  $("#timeInForce").text(result.timeInForce);
  $("#createdAt").text(result.createdAt);
  $("#createTime").text(result.createTime);
  $("#fillFees").text(formatPrice(result.fillFees));
  $("#filledSize").text(result.filledSize && (result.filledSize +getCoinLabel(result.cryptoCurrency)));
  $("#executedValue").text(formatPrice(result.executedValue));
  $("#status").text(getStatusLabel(result.status));
  // $("#settled").text(result.settled);
  $("#message").text(result.message);
}

function toInfoList() {
  window.location.href = "infoList.html";
}
function getStatusLabel(status) {
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
