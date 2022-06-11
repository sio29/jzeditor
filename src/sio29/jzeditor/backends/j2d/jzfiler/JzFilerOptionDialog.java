/******************************************************************************
;	Vz�t�@�C���[�A�I�v�V�����f�[�^
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
		String title="Vz�t�@�C���[�I�v�V����";
		final ColorSelectButton dl_select_front_col=new ColorSelectButton();
		final ColorSelectButton dl_select_back_col=new ColorSelectButton();
		final ColorSelectButton dl_select_back2_col=new ColorSelectButton();
		final ColorSelectButton dl_base_back_col0=new ColorSelectButton();
		final ColorSelectButton dl_base_back_col1=new ColorSelectButton();
		final ColorSelectButton dl_normal_col=new ColorSelectButton();
		final ColorSelectButton dl_dir_col=new ColorSelectButton();
		final ColorSelectButton dl_hidden_col=new ColorSelectButton();
		final ColorSelectButton dl_readonly_col=new ColorSelectButton();
		final JCheckBox dl_use_back_col2=new JCheckBox("�w�i�F�̋����A����g��");
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
			{"�I��F",dl_select_front_col},
			{"�I��w�i�F",dl_select_back_col},
			{"�I��w�i�F(�t�H�[�J�X�Ȃ�)",dl_select_back2_col},
			{"�w�i�F(����)",dl_base_back_col0},
			{"�w�i�F(�)",dl_base_back_col1},
			{"�t�@�C���F",dl_normal_col},
			{"�t�H���_�F",dl_dir_col},
			{"��\���F",dl_hidden_col},
			{"���[�h�I�����[�F",dl_readonly_col},
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
