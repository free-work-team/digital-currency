var isOnline = window.location.href.split("online=")[1];
// 切换币种
var currentCoin = "btc";

var btcShow = false,
    ethShow = false;
// 页面所有参数
var allSetting = {};
var allCoinSettings = {};
var settingObj = {
      onlineShow: function () {
        $('.basicArea').addClass("hidden");
        $('.coinArea').addClass("hidden");
        $('.onlineArea').removeClass("hidden");

        settingObj.temSaveMain();
      },

      basicShow: function () {
        $('.onlineArea').addClass("hidden");
        $('.coinArea').addClass("hidden");
        $('.basicArea').removeClass("hidden");
        if (isOnline == '1') {
          $('#goLogin').removeClass("hidden");
          $('#kycEnableArea').removeClass("hidden");
        }
        if (isOnline == '0') {
          $('#lan_return').removeClass("hidden");
        }
        settingObj.temSaveMain();
        settingObj.temSaveCoin();
      },

      coinShow: function () {
        //校验基础信息，必填项
        var requiredFlag = true;
        $.each($(".base_requerd"), function (index, item) {
          if ($(item).val()) {
            $(item).siblings("div").removeClass("required_input");
          } else {
            $(item).siblings("div").addClass("required_input");
            requiredFlag = false;
          }
        });
        $.each($(".kyc_required_input"), function (index, item) {
          $(item).siblings("div").removeClass("required_input");
          if ($(item).val()) {
            $(item).siblings("div").removeClass("required_input");
          } else {
            if ($('#kycEnable').prop("checked")) {
              $(item).siblings("div").addClass("required_input");
              requiredFlag = false;
            }
          }
        });
        $('#btc').siblings('div').removeClass("required_input");
        $('#eth').siblings('div').removeClass("required_input");
        btcShow = $('#btc').prop("checked");
        ethShow = $('#eth').prop("checked");
        if (btcShow || ethShow) {
          if (requiredFlag) {
            $('.onlineArea').addClass("hidden");
            $('.basicArea').addClass("hidden");
            $('.coinArea').removeClass("hidden");
            $('#btcArea').removeClass("hidden");
            $('#ethArea').removeClass("hidden");
          }

          if (!ethShow) {
            $('#ethArea').addClass("hidden");
          } else {
            $('#btcArea').removeClass("active");
            $('#ethArea').addClass("active");
            currentCoin = 'eth';
          }
          if (!btcShow) {
            $('#btcArea').addClass("hidden");
          } else {
            $('#ethArea').removeClass("active");
            $('#btcArea').addClass("active");
            currentCoin = 'btc';
          }
          settingObj.temSaveMain();

          $('#currencyPair option').addClass('hidden');
          $('.single_' + currentCoin).removeClass('hidden');
          settingObj.renderCoin();


        } else {
          $('#btc').siblings('div').addClass("required_input");
          $('#eth').siblings('div').addClass("required_input");
        }

      },

      // willback
      willback: function () {
        isOnline == '1' ? settingObj.onlineShow() : settingObj.goChoose();
      },

      // back
      goChoose: function () {
        window.location.href = "choose.html";
      },

      // create Json
      initSetting: function (settingStr) {
        allSetting = settingStr ? JSON.parse(settingStr) : '';
        if (!allSetting) {
          // default
          allSetting = {
            cryptoSettings: {
              btc: {
                buySingleFee      : "",//买币交易单笔手续费
                buyTransactionFee : "",//买币交易平台手续费？
                channelParam      : '{"wallet":{"api_key":"","api_secret":""},"exchange":{"kraken_secret":"","currency_pair":"1","kraken_api_key":"","kraken_withdrawals_address_name":"","order_type":""}}',
                confirmations     : 0,
                exchange          : 0,
                exchangeStrategy  : 0,
                hotWallet         : 2,
                minNeedCash       : "",
                rateSource        : 1,
                sellSingleFee     : "",
                sellTransactionFee: ""
              },
              eth: {
                buySingleFee      : "",
                buyTransactionFee : "",
                channelParam      : '{"wallet":{"api_key":"","api_secret":""},"exchange":{"kraken_secret":"","currency_pair":"4","kraken_api_key":"","kraken_withdrawals_address_name":"","order_type":""}}',
                confirmations     : 0,
                exchange          : 0,
                exchangeStrategy  : 0,
                hotWallet         : 2,
                minNeedCash       : "",
                rateSource        : 1,
                sellSingleFee     : "",
                sellTransactionFee: ""
              }
            },
            email         : "",
            hotline       : "",
            kycEnable     : 0,
            way           : 1,
            kycUrl        : "",
            limitCash     : "",
            merchantName  : "",
            online        : isOnline,
            password      : "",
            rateSource    : 0,
            terminalNo    : "",
            webAddress    : ""
          }
        }
        allCoinSettings = allSetting['cryptoSettings'];
        settingObj.renderSetting();
        settingObj.renderCoin();
      },

      // rend main info
      renderSetting: function () {
        $('#webAddress').val(allSetting.webAddress);
        $('#terminalNo').val(allSetting.terminalNo);
        $('#password').val(allSetting.password);
        $('#merchantName').val(allSetting.merchantName);
        $('#hotLine').val(allSetting.hotline);
        $('#mail').val(allSetting.email);
        if (isOnline === '0') {
          allSetting.kycEnable = 0;
        }
        $('#kycEnable').prop("checked", allSetting.kycEnable == 1);
        if (allSetting.kycEnable == 1) {
          $('.kycArea').removeClass('hidden');
          $('#limitCash').val(allSetting.limitCash);
          $('#kycUrl').val(allSetting.kycUrl);
        } else {
          $('.kycArea').addClass('hidden');
        }
        $('#way').prop("checked", allSetting.way == 2);
        if (allSetting.way == 2){
          $('#lan_one_way').addClass("myHide");
          $('#lan_two_way').removeClass("myHide");
        }else{
          $('#lan_one_way').removeClass("myHide");
          $('#lan_two_way').addClass("myHide");
        }



        if (allCoinSettings['btc']) {
          $('#btc').prop("checked", true);
        } else {
          $('#btc').prop("checked", false);
        }

        if (allCoinSettings['eth']) {
          $('#eth').prop("checked", true);
        } else {
          $('#eth').prop("checked", false);
        }
      },

      // rend coin info
      renderCoin: function () {
        var currentSet = allCoinSettings[currentCoin];
        if (!currentSet) {
          // default
          currentSet = {
            buySingleFee      : "",
            buyTransactionFee : "",
            channelParam      : '{"wallet":{"wallet_id":"","wallet_passphrase":"","access_token":"","api_key":"","api_secret":"","password":""},"exchange":{"pro_key":"","pro_passphrase":"","pro_secret":"","kraken_api_key":"","kraken_withdrawals_address_name":"","kraken_secret":"","currency_pair":"2","order_type":"1"}}',
            confirmations     : "6",
            exchange          : "0",
            exchangeStrategy  : "0",
            hotWallet         : "2",
            minNeedCash       : "",
            rateSource        : "1",
            sellSingleFee     : "",
            sellTransactionFee: "",
            price             : ""
          };
          allCoinSettings[currentCoin] = currentSet;
        }
        $('#exchangeStrategy').val(currentSet.exchangeStrategy);
        $('#confirmations').val(currentSet.confirmations);
        $('#rateSource').val(currentSet.rateSource);
        $('#minNeedCash').val(currentSet.minNeedCash);
        $('#price').val(currentSet.price);

        $('#hotWallet').val(currentSet.hotWallet);
        $('#exchange').val(currentSet.exchange);

        $('#buySingleFee').val(currentSet.buySingleFee);
        $('#buyTransactionFee').val(currentSet.buyTransactionFee);
        $('#sellSingleFee').val(currentSet.sellSingleFee);
        $('#sellTransactionFee').val(currentSet.sellTransactionFee);

        // single hide
        if (currentCoin == 'btc') {
          $('.requireDivBtc').removeClass('hidden');
          $('.requireDivBtc .input_setting').addClass("coin_required_input");
        }
        // 单双向
        if (allSetting.way == 2) {
          $('.requireDivWay').removeClass('hidden');
          $('.requireDivWay .input_setting').addClass("coin_required_input");
        } else {
          $('.requireDivWay').addClass('hidden');
          $('.requireDivWay .input_setting').removeClass("coin_required_input");
        }
        if (currentCoin == 'eth') {
          $('.requireDivBtc').addClass('hidden');
          $('.requireDivBtc .input_setting').removeClass("coin_required_input");
        }
        settingObj.refreshWallet(currentSet.hotWallet);
        settingObj.refreshExchange(currentSet.exchange);
        $.each($(".coin_required_input"), function (index, item) {
          $(item).siblings("div").removeClass("required_input");
        });
      },

      // rend coin wallet info
      refreshWallet: function (value) {
        var currenWalletSet = JSON.parse(allCoinSettings[currentCoin].channelParam)['wallet'];
        $('#walletId').removeClass("coin_required_input");
        $('#walletPassphrase').removeClass("coin_required_input");
        $('#accessToken').removeClass("coin_required_input");
        $('#apiKey').removeClass("coin_required_input");
        $('#apiSecret').removeClass("coin_required_input");
        $('#bcWalletId').removeClass("coin_required_input");
        $('#bcPassword').removeClass("coin_required_input");
        if (value == "1") {
          $(".bitgoDiv").removeClass("hidden");
          $(".coinbaseDiv").addClass("hidden");
          $(".blockchainDiv").addClass("hidden");
          if (currenWalletSet.wallet_id && currenWalletSet.wallet_passphrase && currenWalletSet.access_token) {
            $('#walletId').addClass("coin_required_input").val(currenWalletSet.wallet_id);
            $('#walletPassphrase').addClass("coin_required_input").val(currenWalletSet.wallet_passphrase);
            $('#accessToken').addClass("coin_required_input").val(currenWalletSet.access_token);
          } else {
            $('#walletId').addClass("coin_required_input").val("");
            $('#walletPassphrase').addClass("coin_required_input").val("");
            $('#accessToken').addClass("coin_required_input").val("");
          }
        } else if (value == "2") {
          $(".bitgoDiv").addClass("hidden");
          $(".blockchainDiv").addClass("hidden");
          $(".coinbaseDiv").removeClass("hidden");
          if (currenWalletSet.api_key && currenWalletSet.api_secret) {
            $('#apiKey').addClass("coin_required_input").val(currenWalletSet.api_key);
            $('#apiSecret').addClass("coin_required_input").val(currenWalletSet.api_secret);
          } else {
            $('#apiKey').addClass("coin_required_input").val("");
            $('#apiSecret').addClass("coin_required_input").val("");
          }
        } else if (value == "3") {
          $(".coinbaseDiv").addClass("hidden");
          $(".bitgoDiv").addClass("hidden");
          $(".blockchainDiv").removeClass("hidden");
          if (currenWalletSet.wallet_id && currenWalletSet.password) {
            $('#bcWalletId').addClass("coin_required_input").val(currenWalletSet.wallet_id);
            $('#bcPassword').addClass("coin_required_input").val(currenWalletSet.password);
          } else {
            $('#bcWalletId').addClass("coin_required_input").val("");
            $('#bcPassword').addClass("coin_required_input").val("");
          }
        }
      },

      // rend coin exchange info
      refreshExchange: function (value) {
        var currenExchangeSet = JSON.parse(allCoinSettings[currentCoin].channelParam)['exchange'];
        $('#proKey').removeClass("coin_required_input");
        $('#proPassphrase').removeClass("coin_required_input");
        $('#proSecret').removeClass("coin_required_input");
        $('#krakenApiKey').removeClass("coin_required_input");
        $('#krakenWithdrawalsAddressName').removeClass("coin_required_input");
        $('#krakenSecret').removeClass("coin_required_input");
        $('#currencyPair').removeClass("coin_required_input");
        $('#orderType').removeClass("coin_required_input");
        if (value != 0) {
          $(".exchangeDiv").removeClass("hidden");
          if (value == 1) {
            $(".proDiv").removeClass("hidden");
            $(".krakenDiv").addClass("hidden");

            $('#proKey').addClass("coin_required_input").val(currenExchangeSet.pro_key);
            $('#proPassphrase').addClass("coin_required_input").val(currenExchangeSet.pro_passphrase);
            $('#proSecret').addClass("coin_required_input").val(currenExchangeSet.pro_secret);
            $('#currencyPair').addClass("coin_required_input").val(currenExchangeSet.currency_pair);
            $('#orderType').addClass("coin_required_input").val(currenExchangeSet.order_type);
          } else if (value == 2) {
            $(".krakenDiv").removeClass("hidden");
            $(".proDiv").addClass("hidden");

            $('#krakenApiKey').addClass("coin_required_input").val(currenExchangeSet.kraken_api_key);
            $('#krakenWithdrawalsAddressName').addClass("coin_required_input").val(currenExchangeSet.kraken_withdrawals_address_name);
            $('#krakenSecret').addClass("coin_required_input").val(currenExchangeSet.kraken_secret);
            $('#currencyPair').addClass("coin_required_input").val(currenExchangeSet.currency_pair);
            $('#orderType').addClass("coin_required_input").val(currenExchangeSet.order_type);
          }
        } else {
          $(".exchangeDiv").addClass("hidden");
          $(".proDiv").addClass("hidden");
          $(".krakenDiv").addClass("hidden");
        }
      },

      // tem save main info
      temSaveMain: function () {
        allSetting.webAddress = $('#webAddress').val();
        allSetting.terminalNo = $('#terminalNo').val();
        allSetting.password = $('#password').val();
        allSetting.merchantName = $('#merchantName').val();
        allSetting.hotline = $('#hotLine').val();
        allSetting.email = $('#mail').val();
        allSetting.limitCash = $('#limitCash').val();
        allSetting.kycUrl = $('#kycUrl').val();
        allSetting.kycEnable = $('#kycEnable').prop("checked") ? 1 : 0;
        allSetting.way = $('#way').prop("checked") ? 2 : 1;
        allSetting.online = isOnline;
      },

      // tem save coin info
      temSaveCoin: function () {
        allCoinSettings[currentCoin].exchangeStrategy = $('#exchangeStrategy').val();
        allCoinSettings[currentCoin].confirmations = $('#confirmations').val();
        allCoinSettings[currentCoin].rateSource = $('#rateSource').val();
        allCoinSettings[currentCoin].minNeedCash = $('#minNeedCash').val();
        allCoinSettings[currentCoin].price = $('#price').val();
        allCoinSettings[currentCoin].hotWallet = $('#hotWallet').val();
        allCoinSettings[currentCoin].exchange = $('#exchange').val();
        allCoinSettings[currentCoin].buySingleFee = $('#buySingleFee').val();
        allCoinSettings[currentCoin].buyTransactionFee = $('#buyTransactionFee').val();
        allCoinSettings[currentCoin].sellSingleFee = $('#sellSingleFee').val();
        allCoinSettings[currentCoin].sellTransactionFee = $('#sellTransactionFee').val();

        var walletObj = {};
        if (allCoinSettings[currentCoin].hotWallet == 1) {//bitgo
          walletObj = {
            wallet_id        : $('#walletId').val(),
            wallet_passphrase: $('#walletPassphrase').val(),
            access_token     : $('#accessToken').val()
          }
        }
        if (allCoinSettings[currentCoin].hotWallet == 2) {//Coinbase
          walletObj = {
            api_key   : $('#apiKey').val(),
            api_secret: $('#apiSecret').val()
          }
        }
        if (allCoinSettings[currentCoin].hotWallet == 3) {//Blockchain
          walletObj = {
            wallet_id: $('#bcWalletId').val(),
            password : $('#bcPassword').val()
          }
        }
        var exchangeObj = {};
        if (allCoinSettings[currentCoin].exchange == 1) {//pro
          exchangeObj = {
            pro_key       : $('#proKey').val(),
            pro_passphrase: $('#proPassphrase').val(),
            pro_secret    : $('#proSecret').val(),
            currency_pair : $('#currencyPair').val(),
            order_type    : $('#orderType').val()
          }
        }
        if (allCoinSettings[currentCoin].exchange == 2) {//Kraken
          exchangeObj = {
            kraken_api_key                 : $('#krakenApiKey').val(),
            kraken_withdrawals_address_name: $('#krakenWithdrawalsAddressName').val(),
            kraken_secret                  : $('#krakenSecret').val(),
            currency_pair                  : $('#currencyPair').val(),
            order_type                     : $('#orderType').val()
          }
        }
        var channelParamObj = {
          wallet  : walletObj,
          exchange: exchangeObj
        };
        allCoinSettings[currentCoin].channelParam = JSON.stringify(channelParamObj);
      },

      // button save
      save: function () {
        //校验基础信息，必填项
        var requiredFlag = true;
        $.each($(".coin_required_input"), function (index, item) {
          if ($(item).val()) {
            $(item).siblings("div").removeClass("required_input");
          } else {
            $(item).siblings("div").addClass("required_input");
            requiredFlag = false;
          }
        });
        if (!requiredFlag) {
          return;
        }
        if (!btcShow) {
          delete allCoinSettings['btc'];
        }
        if (!ethShow) {
          delete allCoinSettings['eth'];
        }
        settingObj.temSaveCoin();
        // console.log("newSetting:" + JSON.stringify(allCoinSettings));
        allSetting['cryptoSettings'] = allCoinSettings;
        // 存入user 数据库
        var resp = window.back.parameterSetting(JSON.stringify(allSetting));
        settingObj.clearKey();
        allSetting['cryptoSettings'] = allCoinSettings;
        localStorage.setItem("merchant", JSON.stringify(allSetting));
        localStorage.setItem("online", isOnline);
        showSuccess("Success");
        setTimeout(function () {
          window.location.href = "../index.html";
        }, 1000)
      },

      // clear key when save
      clearKey: function () {
        var coinArray = ['btc', 'eth'];
        $.each(coinArray, function (index, item) {
          if (allCoinSettings[item]) {
            delete allCoinSettings[item].channelParam;
          }
        });
      },

      // click login
      login: function () {
        var requireded = true;
        $.each($(".online_required_input"), function (index, item) {
          if ($(item).val()) {
            $(item).siblings("div").removeClass("required_input");
          } else {
            $(item).siblings("div").addClass("required_input");
            requireded = false;
          }
        });
        if (requireded) {
          var setting = window.webTimer.queryTermSet($('#webAddress').val(), $('#terminalNo').val(), $('#password').val());
          // var setting = '{"kycEnable":0,"way":2,"advertContent":"Welcome to China","password":"123456","kycUrl":"","hotline":"222","merchantName":"testaaa","terminalNo":"t00000103","limitCash":"","cryptoSettings":{"eth":{"minNeedCash":"10.00","buySingleFee":"0.00","buyTransactionFee":"2.00","hotWallet":2,"exchange":2,"confirmations":3,"rateSource":1,"sellSingleFee":"2.00","exchangeStrategy":2,"sellTransactionFee":"5.00","channelParam":"{\\"wallet\\":{\\"api_key\\":\\"r4Vw291IqgJvz0G9\\",\\"api_secret\\":\\"jxOA1ZWk1saQpLEGHyGQLlErT60tpT1F\\"},\\"exchange\\":{\\"kraken_secret\\":\\"zdpTNDTCS6AP+rq6WlIAOgIs/SNIYKs9mtLYfGSz2G2sq5X2H+MCTZBY9M32hfW5BTSKvnYYLgCwUigqXiwbTA==\\",\\"currency_pair\\":\\"6\\",\\"kraken_api_key\\":\\"o+vBz8FQ4jqDEzuRIJoDvX81xwwiAEJIbSkyZuFZGYUFMdkQgOFH5M+4\\",\\"kraken_withdrawals_address_name\\":\\"coinbase_zc_eth\\",\\"order_type\\":\\"1\\"}}"},"btc":{"minNeedCash":"10.00","buySingleFee":"5.00","buyTransactionFee":"2.00","hotWallet":2,"exchange":2,"confirmations":3,"rateSource":1,"sellSingleFee":"5.00","exchangeStrategy":1,"sellTransactionFee":"2.00","channelParam":"{\\"wallet\\":{\\"api_key\\":\\"r4Vw291IqgJvz0G9\\",\\"api_secret\\":\\"jxOA1ZWk1saQpLEGHyGQLlErT60tpT1F\\"},\\"exchange\\":{\\"kraken_secret\\":\\"zdpTNDTCS6AP+rq6WlIAOgIs/SNIYKs9mtLYfGSz2G2sq5X2H+MCTZBY9M32hfW5BTSKvnYYLgCwUigqXiwbTA==\\",\\"currency_pair\\":\\"2\\",\\"kraken_api_key\\":\\"o+vBz8FQ4jqDEzuRIJoDvX81xwwiAEJIbSkyZuFZGYUFMdkQgOFH5M+4\\",\\"kraken_withdrawals_address_name\\":\\"coinbase_zc\\",\\"order_type\\":\\"1\\"}}"}},"email":"123@qq.com","online":"1","webAddress":"http://192.168.1.154:8080","code":0}';
          if (setting) {
            localStorage.setItem("online", "1");
            allSetting = JSON.parse(setting);
            if (allSetting.code === 0) {
              allSetting.webAddress = $('#webAddress').val();
              allSetting.terminalNo = $('#terminalNo').val();
              allSetting.password = $('#password').val();
              settingObj.initSetting(JSON.stringify(allSetting));
              settingObj.basicShow();
            } else if (allSetting.code === 200) {// 无可更新参数
              settingObj.basicShow();
            } else {
              showError(allSetting.error || allSetting.message);
            }
          }
        }
      },

      // init
      init: function () {
        settingObj.initLanguage();
        settingObj.classEvent();
        var param = window.back.queryParam();
        // var param = '{"cryptoSettings":{"btc":{"buySingleFee":"5.00","buyTransactionFee":"2.00","channelParam":"{\\"wallet\\":{\\"api_key\\":\\"r4Vw291IqgJvz0G9\\",\\"api_secret\\":\\"jxOA1ZWk1saQpLEGHyGQLlErT60tpT1F\\"},\\"exchange\\":{\\"kraken_secret\\":\\"zdpTNDTCS6AP+rq6WlIAOgIs/SNIYKs9mtLYfGSz2G2sq5X2H+MCTZBY9M32hfW5BTSKvnYYLgCwUigqXiwbTA==\\",\\"currency_pair\\":\\"2\\",\\"kraken_api_key\\":\\"o+vBz8FQ4jqDEzuRIJoDvX81xwwiAEJIbSkyZuFZGYUFMdkQgOFH5M+4\\",\\"kraken_withdrawals_address_name\\":\\"coinbase_zc\\",\\"order_type\\":\\"1\\"}}","confirmations":3,"exchange":2,"exchangeStrategy":1,"hotWallet":2,"minNeedCash":"10.00","rateSource":1,"sellSingleFee":"5.00","sellTransactionFee":"2.00"},"eth":{"buySingleFee":"0.00","buyTransactionFee":"2.00","channelParam":"{\\"wallet\\":{\\"api_key\\":\\"r4Vw291IqgJvz0G9\\",\\"api_secret\\":\\"jxOA1ZWk1saQpLEGHyGQLlErT60tpT1F\\"},\\"exchange\\":{\\"kraken_secret\\":\\"zdpTNDTCS6AP+rq6WlIAOgIs/SNIYKs9mtLYfGSz2G2sq5X2H+MCTZBY9M32hfW5BTSKvnYYLgCwUigqXiwbTA==\\",\\"currency_pair\\":\\"6\\",\\"kraken_api_key\\":\\"o+vBz8FQ4jqDEzuRIJoDvX81xwwiAEJIbSkyZuFZGYUFMdkQgOFH5M+4\\",\\"kraken_withdrawals_address_name\\":\\"coinbase_zc_eth\\",\\"order_type\\":\\"1\\"}}","confirmations":3,"exchange":2,"exchangeStrategy":2,"hotWallet":2,"minNeedCash":"10.00","rateSource":1,"sellSingleFee":"2.00","sellTransactionFee":"5.00"}},"email":"123@qq.com","hotline":"222","kycEnable":0,"kycUrl":"","limitCash":"","merchantName":"testaaa","online":"1","password":"123456","rateSource":0,"way":2,"terminalNo":"t00000103","webAddress":"http://192.168.1.154:8080"}';
        settingObj.initSetting(param);
        if (isOnline === '1') {
          settingObj.onlineShow();
        } else {
          settingObj.basicShow();
        }
      },

      // bind event to class
      classEvent: function () {
        //  切换是否kyc事件
        $('#kycEnable').on('change', function () {
          var that = $(this);
          if (that.prop("checked")) {
            $('.kycArea').removeClass('hidden');
          } else {
            $('.kycArea').addClass('hidden');
          }
        });
        //  切换单双向
        $('#way').on('change', function () {
          if (this.checked){
            $('#lan_one_way').addClass("myHide");
            $('#lan_two_way').removeClass("myHide");
          }else{
            $('#lan_one_way').removeClass("myHide");
            $('#lan_two_way').addClass("myHide");
          }
          $('#way').attr("aria-checked", this.checked);

          // 单双向
          if (this.checked) {
            $('.requireDivWay').removeClass('hidden');
            $('.requireDivWay .coin_required_input').addClass("coin_required_input");
          } else {
            $('.requireDivWay').addClass('hidden');
            $('.requireDivWay .coin_required_input').removeClass("coin_required_input");
          }

        });
        //切换币种
        $('nav a').click(function () {
          //校验基础信息，必填项
          var requiredFlag = true;
          $.each($(".coin_required_input"), function (index, item) {
            if ($(item).val()) {
              $(item).siblings("div").removeClass("required_input");
            } else {
              $(item).siblings("div").addClass("required_input");
              requiredFlag = false;
            }
          });
          if (!requiredFlag) {
            return;
          }
          $('nav a').removeClass('active');
          settingObj.temSaveCoin();
          currentCoin = $(this).attr('data-coin');
          $('#currencyPair option').addClass('hidden');
          $('.single_' + currentCoin).removeClass('hidden');
          settingObj.renderCoin();
          return $(this).addClass('active');
        });
        //切换钱包
        $("#hotWallet").change(function () {
          settingObj.refreshWallet($("#hotWallet").val());
        });
        //切换交易所
        $("#exchange").change(function () {
          settingObj.refreshExchange($("#exchange").val());
        });
        //切换策略
        $("#exchangeStrategy").change(function () {
          if($('#exchangeStrategy').val() == 0){
            $("#exchange").val('0').change();
          }
        });
      },

      initLanguage: function () {
        // id名必须和language key 相同
        var LanguageObjs = ["lan_one_way", "lan_two_way", "lan_rateSource", "lan_onlinePassword", "lan_webAddress", "lan_terminalNo", "lan_merchantName", "lan_hotline", "lan_email", "lan_buyBasicFee", "lan_buyTransactionFee", "lan_sellTransFee", "lan_sellBasicFee", "lan_rate", "lan_account_id", "lan_api_key", "lan_api_secret", "lan_min_need_bitcoin", "lan_min_need_cash", "lan_sell_type", "lan_currency", "lan_hot_wallet", "lan_pro_key", "lan_pro_secret", "lan_pro_passphrase", "lan_limit_cash", "lan_kyc_url", "lan_channel", "lan_accessToken", "lan_walletId_query", "lan_walletPassPhrase", "lan_exchangeStrategy", "lan_blockchain_walletId", "lan_blockchain_password", "lan_currency_pair", "lan_order_type", "lan_kraken_apiKey", "lan_kraken_secret", "lan_kraken_addressName"];
        LanguageManager.getLanguageLabel(LanguageObjs);
      }

    }
;
settingObj.init();
