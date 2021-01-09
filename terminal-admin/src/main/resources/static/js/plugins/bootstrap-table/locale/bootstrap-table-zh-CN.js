/**
 * Bootstrap Table Chinese translation
 * Author: Zhixin Wen<wenzhixin2010@gmail.com>
 */
(function ($) {
    'use strict';
    var currentType = sessionStorage.getItem('currentType');
    if (!currentType) {
        sessionStorage.setItem('currentType', 'lanEnglish');
        currentType = 'lanEnglish';
    }
    $.fn.bootstrapTable.locales['zh-CN'] = {
        formatLoadingMessage: function () {
            return '正在努力地加载数据中，请稍候……';
        },
        formatRecordsPerPage: function (pageNumber) {
            var countEveryPage = sessionStorage.getItem('countEveryPage'),
                unit           = sessionStorage.getItem('unit');
            countEveryPage = countEveryPage && JSON.parse(countEveryPage)[currentType];
            unit = unit && JSON.parse(unit)[currentType];
            return countEveryPage+ pageNumber +unit;
            // return '每页显示 ' + pageNumber + ' 条记录';
        },
        formatShowingRows: function (pageFrom, pageTo, totalRows) {
            var showBegin = sessionStorage.getItem('showBegin'),
                showEnd   = sessionStorage.getItem('showEnd'),
                unit      = sessionStorage.getItem('unit'),
                total     = sessionStorage.getItem('total');
            showBegin = showBegin && JSON.parse(showBegin)[currentType];
            showEnd = showEnd && JSON.parse(showEnd)[currentType];
            total = total && JSON.parse(total)[currentType];
            unit = unit && JSON.parse(unit)[currentType];
            return showBegin + pageFrom + showEnd + pageTo + total + totalRows + unit;
            // return '显示第 ' + pageFrom + ' 到第 ' + pageTo + ' 条记录，总共 ' + totalRows + ' 条记录';
        },
        formatSearch: function () {
            return '搜索';
        },
        formatNoMatches: function () {
            return '没有找到匹配的记录';
        },
        formatPaginationSwitch: function () {
            return '隐藏/显示分页';
        },
        formatRefresh: function () {
            return '刷新';
        },
        formatToggle: function () {
            return '切换';
        },
        formatColumns: function () {
            return '列';
        },
        formatExport: function () {
            return '导出数据';
        },
        formatClearFilters: function () {
            return '清空过滤';
        }
    };

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);

})(jQuery);
