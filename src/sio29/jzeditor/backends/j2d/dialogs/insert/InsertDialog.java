/******************************************************************************
;	挿入ダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.insert;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import sio29.ulib.ufile.*;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class InsertDialog{
	public static String OpenInsertCopyString(Component parent,String title,final InsertOption opt){
		/*
		//final InputHistoryTextField dl_copy_str_hist=new InputHistoryTextField(opt.copy_str_hist);
		
		String[] str_list=opt.copy_str_hist.GetStringList();
		final JList<String> dl_list=new JList<String>(str_list);
		dl_list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				
			}
		});
		
		//JCheckBox dl_bigsmall=new JCheckBox("大文字と小文字の区別");
		//JCheckBox dl_rex=new JCheckBox("正規表現");
		//RadioGroupEx dl_updown =new RadioGroupEx(new String[]{"下へ","上へ"});
		//dl_bigsmall.setSelected(opt.bigsmall_flg);
		//dl_rex.setSelected(opt.rex_flg);
		//dl_updown.SetIndex(opt.updown_flg);
		//Object[][] compos={
		//	{"コピーした文字列",dl_list},
			//{"検索",dl_copy_str_hist},
			//{null,dl_bigsmall},
			//{null,dl_rex},
			//{null,dl_updown.GetButton(0)},
			//{null,dl_updown.GetButton(1)},
		//};
		//JComponent panel=OptionDialogBase.MakeGroupContainer(compos);
		//final JComponent top_panel=dl_copy_str_hist;
		
		Object[][] compos1={
			{null,new JScrollPane(dl_list)},
//			{null,dl_buttons},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		//if(OptionDialogBase.Open(parent,title,base_panel1,top_panel,null,proc)){
		//	return dl_list.getSelectedValue();
		//}
		
		//final JComponent top_panel=dl_list;
		//if(!OptionDialogBase.Open(parent,title,panel,top_panel))return null;
		String value=dl_list.getSelectedValue();
		
		
		//String value=dl_copy_str_hist.getText();
		//opt.copy_str=value;
		//opt.bigsmall_flg=dl_bigsmall.isSelected();
		//opt.rex_flg     =dl_rex.isSelected();
		//opt.updown_flg=dl_updown.GetIndex();
		//opt.SetCopyString(value);
//System.out.println("("+value+")");
		return value;
		*/
		//final DefaultListModel<File> model=new DefaultListModel<File>();
		//SetListModelFileSet2(model,history);
		//オプションPaneを得る
		final JOptionPane[] option_pane=new JOptionPane[1];
		OptionDialogBase.InitProc proc=new OptionDialogBase.InitProc(){
			public void init(JOptionPane pane){
				option_pane[0]=pane;
			}
		};
		String[] str_list=opt.copy_str_hist.GetStringList();
		final JList<String> dl_list=new JList<String>(str_list);
		//final JList<File> dl_list=new JList<File>(model);
		//dl_list.setCellRenderer(new FileMask_ListCellRenderer());
		//
		dl_list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int index = dl_list.locationToIndex(e.getPoint());
					dl_list.setSelectedIndex(index);
					OptionDialogBase.SetValueOK(option_pane[0]);
				}
			}
    	});
		final JPanel dl_buttons=new JPanel();
		final JButton dl_add   =new JButton("追加");
		final JButton dl_delete=new JButton("削除");
		final JButton dl_change=new JButton("変更");
		final JButton dl_reset =new JButton("解除");
		dl_buttons.add(dl_add);
		dl_buttons.add(dl_delete);
		dl_buttons.add(dl_change);
		dl_buttons.add(dl_reset);
		dl_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				/*
				String filename=InputFilenameDialog(parent,title,null,dir_flg);
				if(filename!=null){
					try{
						File file=new File(filename);
						history.add(file);
						SetListModelFileSet2(model,history);
					}catch(Exception ex){
					}
				}
				*/
				
			}
		});
		dl_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				/*
				String m=dl_list.getSelectedValue();
				if(m!=null){
					history.remove(file);
					SetListModelFileSet2(model,history);
				}
				*/
			}
		});
		dl_change.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				/*
				File file=dl_list.getSelectedValue();
				if(file!=null){
					String filename=InputFilenameDialog(parent,title,(String)file.toString(),dir_flg);
					if(filename!=null){
						int index=dl_list.getSelectedIndex();
						try{
							File new_file=new File(filename);
							history.remove(file);
							history.add(new_file);
							SetListModelFileSet2(model,history);
						}catch(Exception ex){}
					}
				}
				*/
			}
		});
		dl_reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				/*
				try{
					File file=new File("*.*");
					history.add(file);
					SetListModelFileSet2(model,history);
					dl_list.setSelectedValue(file,true);
				}catch(Exception ex){}
				*/
			}
		});
		dl_delete.setEnabled(false);
		dl_change.setEnabled(false);
		//
		dl_list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				int index=dl_list.getSelectedIndex();
				if(index>=0){
					dl_delete.setEnabled(true);
					dl_change.setEnabled(true);
				}else{
					dl_delete.setEnabled(false);
					dl_change.setEnabled(false);
				}
			}
		});
		//if(current_file!=null)dl_list.setSelectedValue(current_file,true);
		
		Object[][] compos1={
			{null,new JScrollPane(dl_list)},
			{null,dl_buttons},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		if(OptionDialogBase.Open(parent,title,base_panel1,top_panel,null,proc)){
			return dl_list.getSelectedValue();
		}
		return null;
		
	}
	public static String OpenInsertSearchString(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String OpenInsertInputString(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String OpenInsertDeleteString(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String OpenInsertDate(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String OpenInsertHorizon(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String[] OpenInsertTable(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static String OpenInsertFilename(Component parent,String title,final InsertOption opt){
		return null;
	}
	public static UFile OpenInsertFile(Component parent,String title,final InsertOption opt){
		return null;
	}
}
