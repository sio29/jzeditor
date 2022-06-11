/******************************************************************************
;	
******************************************************************************/
package sio29.jzeditor.backends.j2d.menu;

import javax.swing.KeyStroke;

import sio29.ulib.udlgbase.*;

public interface MenuFeedback{
	public interface MenuFeedbackCallback extends MenuTool.CommandNameFunc {
		public KeyStroke getCommandKeyStroke(String command);
		public char getCommandMnemonic(String command);
	}
	//
	public UMenuBar getMenuBar();
	//public UMenuBar createMenuBar(MenuFeedbackCallback callback);
	public UMenuBar createMenuBar();
}
