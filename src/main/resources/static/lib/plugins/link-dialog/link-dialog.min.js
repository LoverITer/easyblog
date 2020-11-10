/*!
 * Link dialog plugin for Editor.md
 *
 * @file        link-dialog.js
 * @author      pandao
 * @version     1.2.1
 * @updateTime  2015-06-09
 * {@link       https://github.com/pandao/editor.md}
 * @license     MIT
 */
(function(){var a=function(b){var c="link-dialog";b.fn.linkDialog=function(){var i=this;var m=this.cm;var j=this.editor;var g=this.settings;var l=m.getSelection();var d=this.lang;var h=d.dialog.link;var n=this.classPrefix;var f=n+c,k;m.focus();if(j.find("."+f).length>0){k=j.find("."+f);k.find("[data-url]").val("http://");k.find("[data-title]").val(l);this.dialogShowMask(k);this.dialogLockScreen();k.show()}else{var e='<div class="'+n+'form"><label>'+h.url+'</label><input type="text" value="http://" data-url /><br/><label>'+h.urlTitle+'</label><input type="text" value="'+l+'" data-title /><br/></div>';k=this.createDialog({title:h.title,width:380,height:211,content:e,mask:g.dialogShowMask,drag:g.dialogDraggable,lockScreen:g.dialogLockScreen,maskStyle:{opacity:g.dialogMaskOpacity,backgroundColor:g.dialogMaskBgColor},buttons:{enter:[d.buttons.enter,function(){var o=this.find("[data-url]").val();var q=this.find("[data-title]").val();if(o==="http://"||o===""){alert(h.urlEmpty);return false}var p="["+q+"]("+o+' "'+q+'")';if(q==""){p="["+o+"]("+o+")"}m.replaceSelection(p);this.hide().lockScreen(false).hideMask();return false}],cancel:[d.buttons.cancel,function(){this.hide().lockScreen(false).hideMask();return false}]}})}}};if(typeof require==="function"&&typeof exports==="object"&&typeof module==="object"){module.exports=a}else{if(typeof define==="function"){if(define.amd){define(["editormd"],function(b){a(b)})}else{define(function(c){var b=c("./../../editormd");a(b)})}}else{a(window.editormd)}}})();