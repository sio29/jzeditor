/******************************************************************************
;	�c�[���o�[
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;

public class NormalTextToolBar{
	private final static ToolBarPane.ToolBarItem[][] items ={
		{
		new ToolBarPane.ToolBarItem("�V","New"),
		new ToolBarPane.ToolBarItem("�J","Open"),
		new ToolBarPane.ToolBarItem("��","Save"),
		},{
		new ToolBarPane.ToolBarItem("?","About")
		}
	};
	public static ToolBarPane.ToolBarItem[][] getItems(){
		return items;
	}
}

