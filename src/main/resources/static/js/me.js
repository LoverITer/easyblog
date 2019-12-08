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
}

var  t=60;
function disableButton60sAndDisplayCountDown(obj) {
    $(obj).addClass('disabled');
    var timer=setTimeout('disableButton60sAndDisplayCountDown()',1000);
    if(t>0){
        t--;
    }else{
        $(obj).removeClass('disabled');
        clearTimeout(timer);   //清除定时器
        t=60;
    }
}
