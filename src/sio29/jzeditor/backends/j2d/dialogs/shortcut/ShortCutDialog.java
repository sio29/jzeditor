/******************************************************************************
;	ショートカットダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.shortcut;

import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.util.HashMap;
import java.util.Set;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.JComponent;

import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;

public class ShortCutDialog{
//	ShortCutOption option;
	public static boolean Open(Component parent,JComponent opt,UFile file){
		ShortCutData data=new ShortCutData();
		ShortCutTool.getShortCutDataFromComponent(data,opt);
//		if(!file.exist()){
		{
			ShortCutTool.saveShortCutData(file,data);
			
			ShortCutData _data=ShortCutTool.loadShortCutData(file);
			
		}
		if(ShortCutDialog.Open(parent,opt,data)){
			
			return true;
		}
		return false;
	}
	
	public static boolean Open(Component parent,JComponent opt,ShortCutData data){
//		JTable dl_table=new JTable(data.toArray(),new String[]{"キー","コマンド","説明"});
		JTable dl_table=new JTable(data.toArray(),new String[]{"キー","コマンド"});
		String title="ショートカット";
		Object[][] compos1={
			{"ファイル名"		,"てすと"},
			{null,new JScrollPane(dl_table)}
		};
		int width=320;
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		ResizePanelWidth(base_panel1,width);
		final JComponent top_panel=null;
		if(!OptionDialogBase.Open(parent,title,base_panel1,top_panel))return false;
		return true;
	}
	private static void ResizePanelWidth(JComponent panel,int width){
		Dimension size=panel.getPreferredSize();
		if(size.width<width)size.width=width;
		panel.setPreferredSize(size);
	}
	
	
	
}
