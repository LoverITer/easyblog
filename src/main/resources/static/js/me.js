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

/**
 * 高亮显示当前的页码，条件是将需要高亮显示的元素中加入pages类
 */
function showCurrentPageNum(page) {
    var obj=document.getElementsByClassName('pages');
    //var page=[[${articlePages.pageNum}]];
    console.log(obj,page);
    for(var i=0;i<obj.length;i++){
        if(obj[i].textContent==page){
            $(obj[i]).css('background','#eee');
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
