$(document).ready(function () {

    //隐藏网站统计图标
    $('#cnzz_stat_icon_1278953609').hide();

    //点击TOP按钮快速到顶部
    $(".scrollTop").click(function () {
        $("html,body").stop().animate({scrollTop: 0}, 1000);
    });


    //实现TOP按钮的交互 性（鼠标移动上去后颜色改变）
    $('.scrollTop').mouseenter(function () {
        $('#top-highlight').toggle();
        $('#top-default').toggle();
    });
    $('.scrollTop').mouseleave(function () {
        $('#top-highlight').toggle();
        $('#top-default').toggle();
    });


    //nav
    var obj=null;
    var As=document.getElementById('starlist').getElementsByTagName('a');
    obj = As[0];
    for(i=1;i<As.length;i++){if(window.location.href.indexOf(As[i].href)>=0)
        obj=As[i];}
    obj.id='selected';

    //nav
    $("#mnavh").click(function(){
        $("#starlist").toggle();
        $("#mnavh").toggleClass("open");
    });

    //search
    $(".searchico").click(function(){
        $(".search").toggleClass("open");
    });

    //searchclose
    $(".searchclose").click(function(){
        $(".search").removeClass("open");
    });

    //banner
    $('#banner').easyFader();

    //nav menu
    $(".menu").click(function(event) {
        $(this).children('.sub').slideToggle();
    });

    //tab
    $('.tab_buttons li').click(function(){
        $(this).addClass('newscurrent').siblings().removeClass('newscurrent');
        $('.newstab>div:eq('+$(this).index()+')').show().siblings().hide();
    });


    ////////////////////////////////////////////////////
    //search
    $(".is-search,.go-left").click(function () {
        $(".search-page").toggle();
    });


    //nav
    $("nav li a:not(:first)").each(function () {
        $this = $(this);
        if ($this[0].href == String(window.location)) {
            $this.parent().addClass("selected");
        }
    });

    $("nav li a").each(function () {
        $this = $(this);
        if ($this[0].href == String(window.location)) {
            $this.parent().addClass("selected");
        }
    });

    //nav
    $("#mnavh").click(function () {
        $("#starlist").toggle();
        $("#mnavh").toggleClass("open");
        $(".sub").hide();
    });
    //nav menu

    $(".menu").click(function (event) {
        $(this).children('.sub').slideToggle();
        $(this).siblings('.menu').children('.sub').slideUp('');
        event.stopPropagation()
    });
    $(".menu a").click(function (event) {
        event.stopPropagation();
    });
    $(".sub li").click(function (event) {
        event.stopPropagation();
    });

    //aside
    var Sticky = new hcSticky('aside', {
        stickTo: 'main',
        innerTop: 0,
        followScroll: false,
        queries: {
            480: {
                disable: true,
                stickTo: 'body'
            }
        }
    });
});