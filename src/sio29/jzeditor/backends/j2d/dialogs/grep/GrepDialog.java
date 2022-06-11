/******************************************************************************
;	Grepダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.grep;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JCheckBox;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class GrepDialog{
	public static String[] Open(Component parent,String title,final GrepOption opt,String current_dir){
		final UInputHistoryTextField dl_str=UInputHistoryTextFieldBuilder.create(opt.str_hist);
		final UFilenameTextField dl_dir=UFilenameTextFieldBuilder.create(opt.dir_hist,true);
		//
		dl_dir.setText(current_dir);
		//
		JCheckBox dl_bigsmall=new JCheckBox("大文字と小文字の区別");
		JCheckBox dl_subdir=new JCheckBox("サブディレクトリ");
		JCheckBox dl_dirlist=new JCheckBox("ディレクトリリスト");
		dl_bigsmall.setSelected(opt.bigsmall_flg);
		dl_subdir.setSelected(opt.subdir_flg);
		dl_dirlist.setSelected(opt.dirlist_flg);
		Object[][] compos={
			{"検索",UComponentJ2D.getJComponent(dl_str)},
			{"フォルダ",UComponentJ2D.getJComponent(dl_dir)},
			{null,dl_bigsmall},
			{null,dl_subdir},
			{null,dl_dirlist},
		};
		JComponent panel=OptionDialogBase.MakeGroupContainer(compos);
		final Component top_panel=UComponentJ2D.getJComponent(dl_str);
		if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		String[] value=new String[2];
		value[0]=dl_str.getText();
		value[1]=dl_dir.getText();
//		value[0]=opt.str_hist.getText();
//		value[1]=opt.dir_hist.getText();
		opt.search_str  =value[0];
		opt.dir_str     =value[1];
		opt.bigsmall_flg=dl_bigsmall.isSelected();
		opt.subdir_flg  =dl_subdir.isSelected();
		opt.dirlist_flg =dl_dirlist.isSelected();
System.out.println("grep:str("+value[0]+"),dir("+value[1]+")");
		return value;
	}
}

