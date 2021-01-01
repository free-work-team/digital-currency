var ErrorPage = {
  toError: function (errorMessage) {
    var encodeErrorMessage = encodeURI(encodeURI(errorMessage));
    window.location.href = "../common/error.html?error=" + encodeErrorMessage;
  }
};
