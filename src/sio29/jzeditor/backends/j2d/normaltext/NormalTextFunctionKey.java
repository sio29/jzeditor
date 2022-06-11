/******************************************************************************
;	ファンクションキーペイン
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;

import sio29.jzeditor.backends.j2d.*;

public class NormalTextFunctionKey{
	final static FunctionKeyPane.Function[][] def_func1=
	{{
		new FunctionKeyPane.Function("ﾌｧｨﾙ",JzEditorAction.Open) ,new FunctionKeyPane.Function("窓換",JzEditorAction.SelectNextTab) ,new FunctionKeyPane.Function("文換",JzEditorAction.SelectDoc) ,new FunctionKeyPane.Function("窓割"),
		new FunctionKeyPane.Function("記憶") ,new FunctionKeyPane.Function("検索") ,new FunctionKeyPane.Function("置換") ,new FunctionKeyPane.Function("ｶｯﾄ"),
		new FunctionKeyPane.Function("ｲﾝｻｰﾄ"),new FunctionKeyPane.Function("ﾌﾞﾛｯｸ"),new FunctionKeyPane.Function("ﾍﾟｰｼﾞ"),null,//new FunctionKeyPane.Function("")
	},{
		new FunctionKeyPane.Function("設定") ,new FunctionKeyPane.Function("")   ,new FunctionKeyPane.Function("比較") ,null,//new FunctionKeyPane.Function(""),
		new FunctionKeyPane.Function("複写") ,new FunctionKeyPane.Function("")   ,new FunctionKeyPane.Function("複写2"),new FunctionKeyPane.Function("ｺﾋﾟｰ"),
		new FunctionKeyPane.Function("ﾍﾟｰｽﾄ"),new FunctionKeyPane.Function("ﾀｸﾞ"),new FunctionKeyPane.Function("")     ,null,//new FunctionKeyPane.Function(""),
	}};
	public static FunctionKeyPane.Function[][] getFunctions(){
		return def_func1;
	}
}
