/******************************************************************************
;	セーブ確認ダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.confirm;

import javax.swing.JOptionPane;
import javax.swing.JFrame;

import sio29.ulib.udlgbase.backends.j2d.udlg.confirm.*;

public class SaveConfirmDialog{
	public static final int YES   =0;
	public static final int NO    =1;
	public static final int CANCEL=2;
	//
	public static int open(JFrame frame){
		if(frame==null)return CANCEL;
		int option = ConfirmDialog.Open(frame,"保存しますか?");
		if (option == JOptionPane.YES_OPTION){
			return YES;
		}else if (option == JOptionPane.NO_OPTION){
			return NO;
		}else if (option == JOptionPane.CANCEL_OPTION){
			return CANCEL;
		}
		return CANCEL;
	}
}
