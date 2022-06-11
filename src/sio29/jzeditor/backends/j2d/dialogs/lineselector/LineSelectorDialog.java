/******************************************************************************
;	行番号選択
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.lineselector;

import java.awt.Component;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;

public class LineSelectorDialog{
	//行番号選択
	public static int Open(Component parent,String title,int line_num,final LineSelectOption opt){
		//リストに行番号一覧設定
		IntValField list=new IntValField(1,line_num);
		//ダイアログオープン
		if(!OptionDialogBase.Open(parent,title,list,list)){
			return -1;
		}else{
		//選択された
			return list.GetVal();
		}
	}
}
