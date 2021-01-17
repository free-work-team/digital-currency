;(function($, window, document,undefined) {

        var Keyboard = function(ele,opt){
            this.$element = ele;
            this.options = opt;
        };
        Keyboard.prototype = {
            //初始化生成界面
            init:function(){

                if(!$(this.$element).hasClass('mykeyboard')){
                    throw new Error('检测到该元素缺少mykeyboard属性,请添加');
                }
                if($('div.calculator').length >= 1){
                    $('div.calculator').remove();
                }
                var mykeyboard = $('<div class="calculator">\n' +
                    '            <div class="row_line">\n' +
                    '                <span class="num_key" the_val="9">9</span><span class="num_key" the_val="8">8</span><span class="num_key" the_val="7">7</span> \n' +
                    '            </div>\n' +
                    '            <div class="row_line">\n' +
                    '                <span class="num_key" the_val="4">4</span><span class="num_key" the_val="5">5</span><span class="num_key" the_val="6">6</span> \n' +
                    '            </div>\n' +
                    '            <div class="row_line">\n' +
                    '                <span class="num_key" the_val="1">1</span><span class="num_key" the_val="2">2</span><span class="num_key" the_val="3">3</span> \n' +
                    '            </div>\n' +
                    '            <div class="row_line">\n' +
                    '                <span class="btn_key my_clear">Del</span><span class="num_key zero" the_val="0">0</span><span class="btn_key my_backspace"  style="background: url(../../img/icon-home/clear.png) no-repeat 100% 100%;  background-color: #1c406d; background-position: center; background-size: inherit;color: #cecccc;">&nbsp;</span>\n' +
                    '            </div>\n' +
                    '            <div class="row_line">\n' +
                    '                <span id="continue-btn" class="btn_key continue-btn" >Confirm</span> '+
                    '            </div>\n' +
                    '        </div>');

                mykeyboard.appendTo('body');

                if( this.options ){

                    $('div.calculator').css( this.options);

                }else{

                    var margin_top = $(this.$element).position().top;
                    var margin_left = $(this.$element).position().left;
                    var dom_height = $(this.$element).height();
                    $('div.calculator').css({'position':'fixed','left':margin_left+'px','top':(margin_top+dom_height+20)+'px'});
                    var window_height = $(window).height();
                    var current_dom_bottom = window_height - $(this.$element).height() - margin_top;
                    if( current_dom_bottom <= $('div.calculator').height()){
                        $('div.calculator').css({'position':'fixed','left':margin_left+'px','top':(margin_top-$('div.calculator').height()-20)+'px'});
                    }
                }

                this.my_exit();
                this.my_clear();
                this.done();
                this.my_ok();
                this.my_backspace();
            },
            //确定
            my_ok:function(){
                var that = this;
                $('.btn_key.ok').click(function(){
                    $('div.calculator').remove();
                });
            },
            //退格
            my_backspace:function(){
                var that = this;
                $('.my_backspace').on('click',function(){
                  var input_val = $(that.$element).val();
                  $(that.$element).val(input_val.substring(0, input_val.length - 1));
                });
            },
            //退出
            my_exit:function(){
                $('.btn_key.my_exit').on('click',function(){
                    $('div.calculator').remove();
                });
            },
            //清除
            my_clear:function(){
                var that = this;
                $('.my_clear').on('click',function(){
                    $(that.$element).val('');
                });
            },
            //赋值
            done: function() {
                var that = this;
                $('span.num_key').on('click',function(){
                   var num = $(this).attr('the_val');
                   var input_val = $(that.$element).val()?$(that.$element).val():'';
                   var val = input_val + num;
                   $(that.$element).val( val );
                });
            }
        };
        $.fn.mykeyboard = function(options) {
                    var my = new Keyboard(this, options);
                    my.init();
        };
})(jQuery, window, document);
