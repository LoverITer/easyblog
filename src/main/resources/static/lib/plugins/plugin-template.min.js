/*!
 * Link dialog plugin for Editor.md
 *
 * @file        link-dialog.js
 * @author      pandao
 * @version     1.2.0
 * @updateTime  2015-03-07
 * {@link       https://github.com/pandao/editor.md}
 * @license     MIT
 */
(function(){var a=function(b){var d=jQuery;var c={"zh-cn":{toolbar:{table:"表格"},dialog:{table:{title:"添加表格",cellsLabel:"单元格数",alignLabel:"对齐方式",rows:"行数",cols:"列数",aligns:["默认","左对齐","居中对齐","右对齐"]}}},"zh-tw":{toolbar:{table:"添加表格"},dialog:{table:{title:"添加表格",cellsLabel:"單元格數",alignLabel:"對齊方式",rows:"行數",cols:"列數",aligns:["默認","左對齊","居中對齊","右對齊"]}}},en:{toolbar:{table:"Tables"},dialog:{table:{title:"Tables",cellsLabel:"Cells",alignLabel:"Align",rows:"Rows",cols:"Cols",aligns:["Default","Left align","Center align","Right align"]}}}};b.fn.htmlEntities=function(){}};if(typeof require==="function"&&typeof exports==="object"&&typeof module==="object"){module.exports=a}else{if(typeof define==="function"){if(define.amd){define(["editormd"],function(b){a(b)})}else{define(function(c){var b=c("./../../editormd");a(b)})}}else{a(window.editormd)}}})();