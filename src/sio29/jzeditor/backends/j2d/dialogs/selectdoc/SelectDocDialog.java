/******************************************************************************
;	ドキュメント選択
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.selectdoc;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class SelectDocDialog{
	public static class Item{
		Object doc;
		String title;
		public Item(){}
		public Item(Object doc,String title){
			this.doc     =doc;
			this.title   =title;
		}
		public String toString(){
			return title;
		}
	}
	public static Object Open(Component parent,String title,final SelectDocDialog.Item[] docs,final Object current_doc){
		JList<SelectDocDialog.Item> dl_list=new JList<SelectDocDialog.Item>(docs);
		if(current_doc!=null){
			for(int i=0;i<docs.length;i++){
				if(docs[i].doc==current_doc){
					dl_list.setSelectedIndex(i);
					break;
				}
			}
		}
		Object[][] compos={
			{"一覧",new JScrollPane(dl_list)},
		};
		JComponent panel=OptionDialogBase.MakeGroupContainer(compos);
		final JComponent top_panel=dl_list;
		if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		SelectDocDialog.Item item=dl_list.getSelectedValue();
		if(item==null)return null;
		if(item.doc==null)return null;
		return item.doc;
	}
}
