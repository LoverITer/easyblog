/*
easyblog.js
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
            autoclose: 3000,
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
 * 密码框文本的可见性开关
 * @param obj   显示/不显示的按钮
 * @param passwordObj   密码框
 */
function passwordDisplayToggle(obj, passwordObj) {
    $(obj).click(function () {
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
 * 控制一个结点下的a元素响应鼠标悬浮事件===>显示和隐藏
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


/**
 * 向服务器发送AJAX请求检查检查用户的登录情况用于判断显示头像还是显示登录按钮
 */
function toggleStatus(userId) {
    let userJSONStr = localStorage.getItem("user");
    console.log(userJSONStr);
    if (userJSONStr != null && userJSONStr !== "0" && userJSONStr !== "undefined") {
        changeUserLogState2Login();
    } else if (userId >= 0) {
        $.ajax({
            url: "/user/checkUserStatus",
            method: "GET",
            sync: true,
            data: {userId: userId},
            dataType: "json",
            success: function (response) {
                if (response.success) {
                    //console.log('用户登录了');
                    localStorage.setItem("user", response.message);
                    changeUserLogState2Login();
                } else {
                    //console.log('用户没登录');
                    changeUserLogState2LogOut();
                }
            },
            error: function () {
                changeUserLogState2LogOut();
            }
        });
    } else {
        changeUserLogState2LogOut();
    }
}

/**
 * @private
 */
function changeUserLogState2Login() {
    if ($('#login-btn').css('display') != "none") {
        $('#login-btn').css("cssText", 'display:none ;margin-left: 1em !important;');
    }
    if ($('#header-images').css('display') == "none") {
        $('#header-images').css("cssText", 'display:block ;margin-left: 1em !important;');
    }
}

/**
 * @private
 */
function changeUserLogState2LogOut() {
    if ($('#header-images').css('display') != "none") {
        $('#header-images').css("cssText", 'display:none ;margin-left: 1em !important;');
    }
    if ($('#login-btn').css('display') == "none") {
        $('#login-btn').css("cssText", 'display:flex ;margin-left: 1em !important;');
    }
}


/**
 * 用户退出登录AJAX请求
 * @param userId
 */
function logOut(userId) {
    //console.log("userid:"+userId);
    $('#logout').click(function () {
        $.ajax({
            url: "/user/logout",
            method: "GET",
            sync: true,
            data: {userId: userId},
            dataType: "json",
            success: function (response) {
                if (response.success) {
                    localStorage.removeItem("user");
                    changeUserLogState2LogOut();
                    window.location.reload();
                }
            },
            error: function () {
                showErrorMessage("服务异常，请重试！");
            }
        });
    });
}



