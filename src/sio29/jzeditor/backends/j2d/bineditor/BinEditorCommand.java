/******************************************************************************
;	バイナリエディタ
******************************************************************************/
package sio29.jzeditor.backends.j2d.bineditor;

import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

public class BinEditorCommand{
	private ActionTbl[] g_act_tbl={
		/*
		new ActionTbl(JzFilerAction.OpenEditor,this,"OnOpenEditor"),
		*/
	};
	ActionTbl[] getActionTbl(){
		return g_act_tbl;
	}
}
