/******************************************************************************
;	Vzファイラー、オプションデータ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JCheckBox;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

public class JzFilerOptionDialog{
	public static boolean Open(Component parent,JzFilerOptionData opt){
		String title="Vzファイラーオプション";
		final ColorSelectButton dl_select_front_col=new ColorSelectButton();
		final ColorSelectButton dl_select_back_col=new ColorSelectButton();
		final ColorSelectButton dl_select_back2_col=new ColorSelectButton();
		final ColorSelectButton dl_base_back_col0=new ColorSelectButton();
		final ColorSelectButton dl_base_back_col1=new ColorSelectButton();
		final ColorSelectButton dl_normal_col=new ColorSelectButton();
		final ColorSelectButton dl_dir_col=new ColorSelectButton();
		final ColorSelectButton dl_hidden_col=new ColorSelectButton();
		final ColorSelectButton dl_readonly_col=new ColorSelectButton();
		final JCheckBox dl_use_back_col2=new JCheckBox("背景色の偶数、奇数を使う");
		dl_select_front_col.SetItem(opt.cellrenderer.select_front_col);
		dl_select_back_col.SetItem(opt.cellrenderer.select_back_col);
		dl_select_back2_col.SetItem(opt.cellrenderer.select_back2_col);
		dl_base_back_col0.SetItem(opt.cellrenderer.base_back_col[0]);
		dl_base_back_col1.SetItem(opt.cellrenderer.base_back_col[1]);
		dl_normal_col.SetItem(opt.cellrenderer.normal_col);
		dl_dir_col.SetItem(opt.cellrenderer.dir_col);
		dl_hidden_col.SetItem(opt.cellrenderer.hidden_col);
		dl_readonly_col.SetItem(opt.cellrenderer.readonly_col);
		dl_use_back_col2.setSelected(opt.cellrenderer.use_back_col2);
		Object[][] compos1={
			{"選択色",dl_select_front_col},
			{"選択背景色",dl_select_back_col},
			{"選択背景色(フォーカスなし)",dl_select_back2_col},
			{"背景色(偶数)",dl_base_back_col0},
			{"背景色(奇数)",dl_base_back_col1},
			{"ファイル色",dl_normal_col},
			{"フォルダ色",dl_dir_col},
			{"非表示色",dl_hidden_col},
			{"リードオンリー色",dl_readonly_col},
			{null,dl_use_back_col2},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;//panel_editor;
		if(!OptionDialogBase.Open(parent,title,base_panel1,top_panel))return false;
		opt.cellrenderer.select_front_col=dl_select_front_col.GetItem();
		opt.cellrenderer.select_back_col =dl_select_back_col.GetItem();
		opt.cellrenderer.select_back2_col=dl_select_back2_col.GetItem();
		opt.cellrenderer.base_back_col[0]=dl_base_back_col0.GetItem();
		opt.cellrenderer.base_back_col[1]=dl_base_back_col1.GetItem();
		opt.cellrenderer.normal_col      =dl_normal_col.GetItem();
		opt.cellrenderer.dir_col         =dl_dir_col.GetItem();
		opt.cellrenderer.hidden_col      =dl_hidden_col.GetItem();
		opt.cellrenderer.readonly_col    =dl_readonly_col.GetItem();
		opt.cellrenderer.use_back_col2   =dl_use_back_col2.isSelected();
		return true;
	}
}
