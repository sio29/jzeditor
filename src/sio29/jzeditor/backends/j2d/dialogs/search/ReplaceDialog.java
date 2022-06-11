/******************************************************************************
;	�u���_�C�A���O
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.search;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class ReplaceDialog{
	public static String[] Open(Component parent,String title,final SearchOption opt){
		final UInputHistoryTextField dl_search_str_hist =UInputHistoryTextFieldBuilder.create(opt.search_str_hist);
		final UInputHistoryTextField dl_replace_str_hist=UInputHistoryTextFieldBuilder.create(opt.replace_str_hist);
		JCheckBox dl_bigsmall=new JCheckBox("�啶���Ə������̋��");
		JCheckBox dl_rex=new JCheckBox("���K�\��");
		RadioGroupEx dl_updown =new RadioGroupEx(new String[]{"����","���"});
		Object[][] compos={
			{"����",UComponentJ2D.getJComponent(dl_search_str_hist)},
			{"�u��",UComponentJ2D.getJComponent(dl_replace_str_hist)},
			{null,dl_bigsmall},
			{null,dl_rex},
			{null,dl_updown.GetButton(0)},
			{null,dl_updown.GetButton(1)},
		};
		JComponent panel=OptionDialogBase.MakeGroupContainer(compos);
		final Component top_panel=UComponentJ2D.getJComponent(dl_search_str_hist);
		dl_bigsmall.setSelected(opt.bigsmall_flg);
		dl_rex.setSelected(opt.rex_flg);
		dl_updown.SetIndex(opt.updown_flg);
		if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		String[] value=new String[2];
		value[0]=dl_search_str_hist.getText();
		value[1]=dl_replace_str_hist.getText();
		opt.search_str =value[0];
		opt.replace_str=value[1];
		opt.bigsmall_flg=dl_bigsmall.isSelected();
		opt.rex_flg     =dl_rex.isSelected();
		opt.updown_flg=dl_updown.GetIndex();
		opt.SetSearchString(value[0]);
		opt.SetReplaceString(value[1]);
System.out.println("("+value[0]+")->("+value[1]+")");
		return value;
	}
}
