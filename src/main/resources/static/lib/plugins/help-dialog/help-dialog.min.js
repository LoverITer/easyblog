/*!
 * Help dialog plugin for Editor.md
 *
 * @file        help-dialog.js
 * @author      pandao
 * @version     1.2.0
 * @updateTime  2015-03-08
 * {@link       https://github.com/pandao/editor.md}
 * @license     MIT
 */
(function(){var a=function(b){var d=jQuery;var c="help-dialog";b.fn.helpDialog=function(){var i=this;var e=this.lang;var j=this.editor;var g=this.settings;var o=g.pluginPath+c+"/";var m=this.classPrefix;var f=m+c,k;var h=e.dialog.help;if(j.find("."+f).length<1){var l='<div class="markdown-body" style="font-family:微软雅黑, Helvetica, Tahoma, STXihei,Arial;height:390px;overflow:auto;font-size:14px;border-bottom:1px solid #ddd;padding:0 20px 20px 0;"></div>';k=this.createDialog({name:f,title:h.title,width:840,height:540,mask:g.dialogShowMask,drag:g.dialogDraggable,content:l,lockScreen:g.dialogLockScreen,maskStyle:{opacity:g.dialogMaskOpacity,backgroundColor:g.dialogMaskBgColor},buttons:{close:[e.buttons.close,function(){this.hide().lockScreen(false).hideMask();return false}]}})}k=j.find("."+f);this.dialogShowMask(k);this.dialogLockScreen();k.show();var n=k.find(".markdown-body");if(n.html()===""){d.get(o+"help.md",function(q){var p=b.$marked(q);n.html(p);n.find("a").attr("target","_blank")})}}};if(typeof require==="function"&&typeof exports==="object"&&typeof module==="object"){module.exports=a}else{if(typeof define==="function"){if(define.amd){define(["editormd"],function(b){a(b)})}else{define(function(c){var b=c("./../../editormd");a(b)})}}else{a(window.editormd)}}})();