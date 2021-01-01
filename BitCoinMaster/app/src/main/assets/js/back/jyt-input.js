(function ($) {
  $.JYTInput = function (arg) {
    var el = arg == null || arg.el == null ? "inputBtn" : arg.el;
    var showEl = arg == null || arg.showEl == null ? "inputBtn" : arg.showEl;
    var showMaxLength = arg == null || arg.showMaxLength == null ? 15 : arg.showMaxLength;
    this.init = function () {
      var tableHtml = utils.getInputHtml()
      var elDom = $("#" + el);
      elDom.html(tableHtml);
      utils.addEvent();
    };
    this.getData = function () {
      return data.join("");
    };
    var data = [];
    var utils = {
      getInputHtml: function () {
        var html = [];
        var data = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "x"];
        html.push("<table>");
        html.push("<tr>");
        for (var i = 0; i < data.length; i++) {
          html.push("<td><div data-text='" + data[i] + "'>" + data[i] + "</div></td>");
          if ((i + 1) % 3 === 0) {
            html.push("</tr><tr>");
          }
        }
        html.push("</tr>");
        html.push("</table>");
        return html.join("")
      },
      addEvent    : function () {
        var self = this;
        $("#" + el).find("table  td  div").off().on("click", function () {
          self.putData($(this).data("text"))
        });
        $("#" + el).find("table  td  div").last().on("click", function () {
          data.splice(data.length - 1, 1);
          $("#" + showEl).html(data.join(""));
        })
      },
      putData     : function (dataInfo) {
        if (!isNaN(dataInfo) && data.length < showMaxLength) {
          data.push(dataInfo);
        }
        $("#" + showEl).html(data.join(""))
      }
    }
  }
})(jQuery);
