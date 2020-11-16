jQuery(document).ready(function () {

    /*********************登录逻辑***************************/
    $('#login form').submit(function () {
        var username = $(this).find('.username').val();
        var password = $(this).find('.password').val();
        if (username == '') {
            toast("请输入账号");
            $(this).parent().find('.username').focus();
            return false;
        }
        if (password == '') {
            toast("请输入密码");
            $(this).parent().find('.password').focus();
            return false;
        }
    });

    /******************************注册逻辑****************************************/
    $('#registration-submit').click(function () {
        var username = $('form .username').val();
        var password = $('form .password').val();
        let captchaCode = $('form .captchaCode').val();
        console.log(username + " " + password + " " + captchaCode);
        if (username == '') {
            toast("请输入账号");
            $('form .username').focus();
            return false;
        }
        if (password == '') {
            toast("请输入密码");
            $('form .password').focus();
            return false;
        }
        if (captchaCode == '') {
            toast("请输入验证码");
            $('form .captchaCode').focus();
            return false;
        }

        $.ajax({
            url: "/user/register",
            method: "GET",
            sync: true,
            data: {password: password, username: username, captchaCode: captchaCode},
            dataType: "json",
            success: function (response) {
                if (response.success) {
                    window.location.href = "/user/register-success?username=" + username;
                } else {
                    toast(response.message);
                }
            },
            error: function (response) {
                toast(response.message);
            }
        })

    });

    /******************************找回密码逻辑****************************************/
    $('#reset-password-submit').click(function () {
        var username = $('form .username').val();
        var password = $('form .password').val();
        let captchaCode = $('form .captchaCode').val();
        console.log(username + " " + password + " " + captchaCode);
        if (username == '') {
            toast("请输入账号");
            $('form .username').focus();
            return false;
        }
        if (captchaCode == '') {
            toast("请输入验证码");
            $('form .captchaCode').focus();
            return false;
        }
        if (password == '') {
            toast("请输入密码");
            $('form .password').focus();
            return false;
        }

        $.ajax({
            url: "/user/resetPassword",
            method: "GET",
            sync: true,
            data: {password: password, username: username, captchaCode: captchaCode},
            dataType: "json",
            success: function (response) {
                if (response.success) {
                    confirm({
                        title: '友情提示',
                        content: response.message,
                        doneText: '确认',
                        cancalText: '取消'
                    }).then(() => {
                        window.location.href = "/user/login.html?username=" + username;
                    }).catch(() => {
                    });

                } else {
                    toast(response.message);
                }
            },
            error: function (response) {
                toast(response.message);
            }
        })
    });
});

/**
 * 倒计时并禁用按钮
 */
function countDown() {
    if (maxtime >= 0) {
        $("#code-btn").attr("disabled", "disabled");
        $("#code-btn").text("重新发送(" + maxtime + ")");
        --maxtime;
    } else {
        clearInterval(timeOut);
        $("#code-btn").text("获取验证码");
        $("#code-btn").removeAttr("disabled");
        maxtime = 60;
    }
}

/**
 * 获取ULR中的用户账号
 * @param name
 * @returns {string|null}
 */
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var q = window.location.pathname.substr(1).match(reg_rewrite);
    if (r != null) {
        return unescape(r[2]);
    } else if (q != null) {
        return unescape(q[2]);
    } else {
        return null;
    }
}
