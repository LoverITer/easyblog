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
function autoFillUserAccountByCookie(usernameInput,passwordInput) {
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
            style: 'waring',
        });
    }
}


/**
 * 密码可见性开关
 * @param obj   显示/不显示按钮
 * @param passwordObj   密码框
 */
function passwordDisplayToggle(obj,passwordObj) {
    $(obj).click(function () {
        if($(this).hasClass('password-hide')){
            $(this).removeClass('password-hide');
            $(this).addClass('password-show');
            $(passwordObj).attr('type','text');
            let value=$(passwordObj).val();
            $(passwordObj).val(value);
        }else{
            $(this).removeClass('password-show');
            $(this).addClass('password-hide');
            $(passwordObj).attr('type','password');
        }
    });
}


