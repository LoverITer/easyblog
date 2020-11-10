/*!
 * Test plugin for Editor.md
 *
 * @file        test-plugin.js
 * @author      pandao
 * @version     1.2.0
 * @updateTime  2015-03-07
 * {@link       https://github.com/pandao/editor.md}
 * @license     MIT
 */
(function(){var a=function(b){var c=jQuery;b.testPlugin=function(){alert("testPlugin")};b.fn.testPluginMethodA=function(){alert("testPluginMethodA")}};if(typeof require==="function"&&typeof exports==="object"&&typeof module==="object"){module.exports=a}else{if(typeof define==="function"){if(define.amd){define(["editormd"],function(b){a(b)})}else{define(function(c){var b=c("./../../editormd");a(b)})}}else{a(window.editormd)}}})();