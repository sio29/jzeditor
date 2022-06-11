/******************************************************************************
;	検索ダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.search;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class SearchDialog{
	public static String Open(Component parent,String title,final SearchOption opt){
		final UInputHistoryTextField dl_search_str_hist=UInputHistoryTextFieldBuilder.create(opt.search_str_hist);
		JCheckBox dl_bigsmall=new JCheckBox("大文字と小文字の区別");
		JCheckBox dl_rex=new JCheckBox("正規表現");
		RadioGroupEx dl_updown =new RadioGroupEx(new String[]{"下へ","上へ"});
		dl_bigsmall.setSelected(opt.bigsmall_flg);
		dl_rex.setSelected(opt.rex_flg);
		dl_updown.SetIndex(opt.updown_flg);
		Object[][] compos={
			{"検索",UComponentJ2D.getJComponent(dl_search_str_hist)},
			{null,dl_bigsmall},
			{null,dl_rex},
			{null,dl_updown.GetButton(0)},
			{null,dl_updown.GetButton(1)},
		};
		JComponent panel=OptionDialogBase.MakeGroupContainer(compos);
		final Component top_panel=UComponentJ2D.getJComponent(dl_search_str_hist);
		if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		String value=dl_search_str_hist.getText();
		opt.search_str=value;
		opt.bigsmall_flg=dl_bigsmall.isSelected();
		opt.rex_flg     =dl_rex.isSelected();
		opt.updown_flg=dl_updown.GetIndex();
		opt.SetSearchString(value);
//System.out.println("("+value+")");
		return value;
	}
}
