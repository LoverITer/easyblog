//获取当前页面的缩放值
function detectZoom() {
    var ratio = 0,
        screen = window.screen,
        ua = navigator.userAgent.toLowerCase();

    if (window.devicePixelRatio !== undefined) {
        ratio = window.devicePixelRatio;
    } else if (~ua.indexOf('msie')) {
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
};

//60s倒计时
var countdown=60;        //初始值
function settime(obj) {
    if (countdown == 0) {
        /*$('#getCode_btn').removeClass("disabled");
        $('#getCode_btn').html("获取验证码");*/
        $(obj).removeClass("disabled");
        $(obj).html("获取验证码");
        countdown = 60;
        return false;
    } else {
        /* $('#getCode_btn').addClass("disabled");
         $('#getCode_btn').html("重新获取(" + countdown + ")");*/
        $(obj).addClass("disabled");
        $(obj).html("重新获取(" + countdown + ")");
        countdown--;
    }
    setTimeout('settime()',1000);
};
