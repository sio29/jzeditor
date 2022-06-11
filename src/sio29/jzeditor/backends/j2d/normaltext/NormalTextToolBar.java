/******************************************************************************
;	ツールバー
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;

public class NormalTextToolBar{
	private final static ToolBarPane.ToolBarItem[][] items ={
		{
		new ToolBarPane.ToolBarItem("新","New"),
		new ToolBarPane.ToolBarItem("開","Open"),
		new ToolBarPane.ToolBarItem("保","Save"),
		},{
		new ToolBarPane.ToolBarItem("?","About")
		}
	};
	public static ToolBarPane.ToolBarItem[][] getItems(){
		return items;
	}
}

