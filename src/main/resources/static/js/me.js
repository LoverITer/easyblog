/*
me.js
(c) 2019-2020 by HuangXin. All rights reserved.
*/
/**
 * 获取当前页面的缩放值
 * @returns {number}
 */
function detectZoom() {
    let ratio = 0,
        screen = window.screen,
        ua = navigator.userAgent.toLowerCase();

    if (window.devicePixelRatio !== undefined) {
        ratio = window.devicePixelRatio;
    } else if (ua.indexOf('msie')) {
        if (screen.deviceXDPI && screen.logicalXDPI) {
            ratio = screen.deviceXDPI / screen.logicalXDPI;
        }
    } else if (window.outerWidth !== undefined && window.innerWidth !== undefined) {
        ratio = window.outerWidth / window.innerWidth;
    }

    if (ratio) {
        ratio = Math.round(ratio * 100);
    }
    return ratio;
}

/**
 * 当页面的缩放比例不是100%显示此提示
 */
function showPageZoomWarning() {
    //sessionStorage.getItem("isReload") 检测页面是否第一次加载，只在第一次加载的时候提示
    if (detectZoom() != 125 && !sessionStorage.getItem("isReload")) {
        showInfoMessage("您当前的页面处于缩放，页面可能会错乱，建议缩放比例100%");
        sessionStorage.setItem("isReload", true);
    }
}

/**
 * 高亮显示当前的页码，条件是将需要高亮显示的元素中加入pages类
 */
function showCurrentPageNum(page) {
    var obj = document.getElementsByClassName('pages');
    //var page=[[${articlePages.pageNum}]];
    console.log(obj, page);
    for (var i = 0; i < obj.length; i++) {
        if (obj[i].textContent == page) {
            $(obj[i]).css('background', '#eee');
        }
    }
}

/**
 * 不断检查用户的登录情况
 */
function toggleStatus() {
    var user = sessionStorage.getItem("user");
    if (user != null) {
        $('#header-images').show();
        $('#login-btn').hide();
    } else {
        $('#header-images').hide();
        $('#login-btn').show();
    }
    setTimeout(toggleStatus, 500);
}

/**
 * 邮箱补全
 */
function inputList(inputObj, listObj) {
    let mailBox = [
        "@qq.com",
        "@sina.com",
        "@163.com",
        "@126.com",
        "@yahoo.com.cn",
        "@gmail.com",
        "@sohu.com"
    ];
    $(inputObj).bind('input propertychange', function () {
        let key = $(inputObj).val();
        if (key.indexOf("@") != -1) {
            key = key.slice(0, key.indexOf("@"));
        }
        let mailBoxLen = mailBox.length;
        let html = "";
        for (let i = 0; i < mailBoxLen; i++) {
            html += '<option value="' + key + mailBox[i] + '"></option>';
        }
        listObj.html(html);
    });
}

/**
 * 自动根据Cookie填充用户名和密码
 **/
function autoFillUserAccountByCookie(usernameInput, passwordInput) {
    //记住密码功能
    let str = getCookie("USER-COOKIE");
    //console.log(str);
    let account = str.split("-");
    //自动填充用户名和密码
    $(usernameInput).val(account[0]);
    $(passwordInput).val(AES_ECB_decrypt(account[1], "1a2b3c4d5e6f7g8h"));
}

//获取cookie
function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
    }
    return "";
}

/**
 * @return {string}
 */
function AES_ECB_decrypt(ciphered, keyStr) {
    let key = CryptoJS.enc.Utf8.parse(keyStr);
    // 解密
    let decryptedData = CryptoJS.AES.decrypt(ciphered, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    // 解密后，需要按照Utf8的方式将明文转为字符串
    return CryptoJS.enc.Utf8.stringify(decryptedData).toString();
}


/**
 *一般提示信息框
 */
function showInfoMessage(message) {
    if (message != "") {
        spop({
            template: message,
            autoclose: 3000,
            position: "top-center",
            style: 'info',
        });
    }
}


/**
 *错误提示信息框
 */
function showSuccessMessage(message) {
    if (message != "") {
        spop({
            template: message,
            autoclose: 4000,
            position: "top-center",
            style: 'success',
        });
    }
}

/**
 *错误提示信息框
 */
function showErrorMessage(message) {
    if (message != "") {
        spop({
            template: message,
            autoclose: 4000,
            position: "top-center",
            style: 'error',
        });
    }
}

/**
 * 警告提示信息框
 */
function showWarningMessage(message) {
    if (message != "") {
        spop({
            template: message,
            autoclose: 4000,
            position: "top-center",
            style: 'warning',
        });
    }
}

/**
 * 加载动画
 * @param type
 */
function _loading(type) {
    zeroModal.loading(type);
}

/**
 * 关闭loading框
 */
function _loadingClose() {
    $('.zeromodal-overlay').remove();
    $('.zeromodal-loading2').remove();
}

/**
 * 错误提示框
 * @private
 */
function _error(message) {
    zeroModal.error(message + "!");
}

/**
 * 成功提示框
 * @private
 */
function _success(message) {
    zeroModal.success(message + '!');
}

function _alert1(message) {
    zeroModal.alert(message + "!");
}

function _alert2() {
    zeroModal.alert({
        content: '操作提示!',
        contentDetail: '请选择数据后再进行操作',
        okFn: function () {
            alert('ok callback');
        }
    });
}

/**
 * 简单提示框
 * @param message   确认的操作
 * @private
 */
function _confirmBase(message) {
    zeroModal.confirm(message, function () {
        alert('ok');
    });
}

/**
 * 有详细信息提示的确认框
 * @param option    确认操作
 * @param optionDetail   确认操作详细说明
 * @private
 */
function _confirmWithDetail(option, optionDetail) {
    zeroModal.confirm({
        content: option + '？',
        contentDetail: optionDetail + '。',
        okFn: function () {
            //确认按钮按下

        },
        cancelFn: function () {
            //取消按钮按下

        }
    });
}


/**
 * 密码可见性开关
 * @param obj   显示/不显示的按钮
 * @param passwordObj   密码框
 */
function passwordDisplayToggle(obj, passwordObj) {
    $(obj).click(function () {
        console.log(1111111);
        if ($(this).hasClass('password-hide')) {
            $(this).removeClass('password-hide');
            $(this).addClass('password-show');
            $(passwordObj).attr('type', 'text');
            let value = $(passwordObj).val();
            $(passwordObj).val(value);
        } else {
            $(this).removeClass('password-show');
            $(this).addClass('password-hide');
            $(passwordObj).attr('type', 'password');
        }
    });
}

/**
 * 控制一个元素结点的显示和隐藏
 * @param identity  标识元素结点的class
 */
function showModifyButton(identity) {
    $(identity).hover(function () {
            $('a', this).show();
        },
        function () {
            $('a', this).hide();
        });
}



