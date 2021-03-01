var screen = {
  getType: function () {
    switch (document.body.clientWidth) {
      case 1920:
        return 1;
      case 1080:
        return 2;
      case 1280:
        return 3;
      case 1024:
        return 4;
      default:
        return 1;
    }
  }
};
