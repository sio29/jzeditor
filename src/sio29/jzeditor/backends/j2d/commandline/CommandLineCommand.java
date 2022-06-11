/******************************************************************************
;	コマンドライン
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

public class CommandLineCommand{
	private ActionTbl[] g_act_tbl={
		/*
		new ActionTbl(JzFilerAction.OpenEditor,this,"OnOpenEditor"),
		*/
	};
	ActionTbl[] getActionTbl(){
		return g_act_tbl;
	}
}
