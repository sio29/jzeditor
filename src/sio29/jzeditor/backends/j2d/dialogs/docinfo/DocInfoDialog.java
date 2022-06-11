/******************************************************************************
;	�h�L�������g���_�C�A���O
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.docinfo;

import java.util.Date;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.ufile.*;

public class DocInfoDialog{
	public static boolean Open(Component parent,NormalText doc){
		String title="�I�v�V����";
		CharCode cc=doc.GetCharCode();
		String cr_type=doc.GetCRType();
		boolean eof_flg=doc.GetEofFlg();
		int total_size=doc.GetTotalSize();
		int total_line_num=doc.GetTotalLineNum();
		UFile filename=doc.GetFilename();
		CharCode[] charcode_list=CharCode.getCharCodeList();
		final JComboBox<String> dl_eof =new JComboBox<String>(new String[]{"�Ȃ�","����"});
		final JComboBox<String> dl_code=new JComboBox<String>(CharCode.convertCharCodeNameList(charcode_list));
		final JComboBox<String> dl_ret =new JComboBox<String>(CRType.GetNameList());
		dl_code.setSelectedIndex(CharCode.getCharCodeIndex(charcode_list,cc));
		dl_ret.setSelectedIndex(CRType.GetIndex(cr_type));
		dl_eof.setSelectedIndex(eof_flg?1:0);
		String size_m=String.format("%dbyte",total_size);
		String line_m=String.format("%d�s",total_line_num);
		String time_m="";
		try{
			Date time=new Date(filename.lastModified());
			time_m=""+time;
		}catch(Exception ex){}
		JLabel dl_filename=new JLabel(filename.getLocalFilename());
		JLabel dl_size=new JLabel(size_m);
		JLabel dl_linenum=new JLabel(line_m);
		JLabel dl_time=new JLabel(time_m);
		//
		Object[][] compos1={
			{"�t�@�C����"		,dl_filename},
			{"�T�C�Y"			,dl_size},
			{"�s��"				,dl_linenum},
			{"���t"				,dl_time},
			{"�����R�[�h"		,dl_code},
			{"���s"				,dl_ret},
			{"EOF"				,dl_eof},
		};
		int width=320;
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		ResizePanelWidth(base_panel1,width);
		final JComponent top_panel=dl_code;
		if(!OptionDialogBase.Open(parent,title,base_panel1,top_panel))return false;
		int code_index=dl_code.getSelectedIndex();
		CharCode new_cc=charcode_list[code_index].copy();
		doc.SetCharCode(new_cc);
		return true;
	}
	private static void ResizePanelWidth(JComponent panel,int width){
		Dimension size=panel.getPreferredSize();
		if(size.width<width)size.width=width;
		panel.setPreferredSize(size);
	}
}
