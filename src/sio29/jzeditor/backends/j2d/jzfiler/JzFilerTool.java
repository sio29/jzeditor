/******************************************************************************
;	Vzファイラー、ツール類
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Date;
import java.util.Calendar;
import java.util.Set;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.roxes.win32.LnkFile;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.confirm.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ufile.backends.j2d.*;
import sio29.ulib.ureg.*;

public class JzFilerTool{
	//===========================
	//●はい・いいえ・取消し
	public static int YesNoDialog(Component parent,String m){
		return ConfirmDialog.Open(parent,m);
	}
	//===========================
	//●OK
	public static void OKDialog(Component parent,String m){
		ConfirmDialog.OpenOK(parent,m);
	}
	//===========================
	//ファイル名の入力
	public static String InputFilenameDialog(Component parent,String title,String filename,boolean dir_flg){
		final FilenameTextField dl_filename=new FilenameTextField(dir_flg);
		if(filename!=null){
			dl_filename.setText(filename);
		}else{
			dl_filename.setCurrentDir(new File("."));
		}
		String name=null;
		if(!dir_flg){
			name="ファイル名";
		}else{
			name="フォルダ名";
		}
		Object[][] compos1={
			{name,dl_filename},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=dl_filename;
		if(OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			String new_filename=dl_filename.getText();
			if((filename==null) || (new_filename.length()>0 && !filename.equals(new_filename))){
				return new_filename;
			}
		}else{
			return null;
		}
		return filename;
	}
	//===========================
	//パネル幅のリサイズ
	private static void ResizePanelWidth(JComponent panel,int width){
		Dimension size=panel.getPreferredSize();
		if(size.width<width)size.width=width;
		panel.setPreferredSize(size);
	}
	//===========================
	//日付の入力
	public static long InputDateDialog(Component parent,long m){
		Calendar cl=Calendar.getInstance();
		cl.setTimeInMillis(m);
		int year  =cl.get(Calendar.YEAR);
		int month =cl.get(Calendar.MONTH)+1;
		int day   =cl.get(Calendar.DAY_OF_MONTH);
		int hour  =cl.get(Calendar.HOUR_OF_DAY);
		int minute=cl.get(Calendar.MINUTE);
		int second=cl.get(Calendar.SECOND);
		String title="日付の変更";
		final IntValField dl_year  =new IntValField(1970,9999);
		final IntValField dl_month =new IntValField(1,12);
		final IntValField dl_day   =new IntValField(1,31);
		final IntValField dl_hour  =new IntValField(0,23);
		final IntValField dl_minute=new IntValField(0,59);
		final IntValField dl_second=new IntValField(0,59);
		dl_year  .SetVal(year  );
		dl_month .SetVal(month );
		dl_day   .SetVal(day   );
		dl_hour  .SetVal(hour  );
		dl_minute.SetVal(minute);
		dl_second.SetVal(second);
		Object[][] compos1={
			{"年/月/日",dl_year,dl_month ,dl_day},
			{"時/分/秒",dl_hour,dl_minute,dl_second},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=dl_year;
		if(OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			int new_year  =dl_year.GetVal();
			int new_month =dl_month.GetVal();
			int new_day   =dl_day.GetVal();
			int new_hour  =dl_hour.GetVal();
			int new_minute=dl_minute.GetVal();
			int new_second=dl_second.GetVal();
			if(year  !=new_year   ||
			   month !=new_month  ||
			   day   !=new_day    ||
			   hour  !=new_hour   ||
			   minute!=new_minute ||
			   second!=new_second){
				cl.set(Calendar.YEAR,new_year);
				cl.set(Calendar.MONTH,new_month-1);
				cl.set(Calendar.DAY_OF_MONTH,new_day);
				cl.set(Calendar.HOUR_OF_DAY,new_hour);
				cl.set(Calendar.MINUTE,new_minute);
				cl.set(Calendar.SECOND,new_second);
				long new_m=cl.getTimeInMillis();
				return new_m;
			}
		}
		return m;
	}
	//===========================
	//ファイルアトリュビュートの入力
	public static int FILEATTR_READONLY=0x01;
	public static int FILEATTR_HIDDEN  =0x02;
	public static int FILEATTR_SYSTEM  =0x04;
	public static int FILEATTR_ARCHIVE =0x08;
	public static int InputAttrDialogWin(Component parent,int attr){
		String title="属性の変更";
		final JCheckBox dl_readonly=new JCheckBox("読み込み専用");
		final JCheckBox dl_hidden  =new JCheckBox("隠し");
		final JCheckBox dl_system  =new JCheckBox("システム");
		final JCheckBox dl_archive =new JCheckBox("アーカイブ");
		dl_readonly.setSelected((attr & FILEATTR_READONLY)!=0);
		dl_hidden.setSelected((attr & FILEATTR_HIDDEN)!=0);
		dl_system.setSelected((attr & FILEATTR_SYSTEM)!=0);
		dl_archive.setSelected((attr & FILEATTR_ARCHIVE)!=0);
		dl_system.setEnabled(false);
		dl_archive.setEnabled(false);
		Object[][] compos1={
			{null,dl_readonly},
			{null,dl_hidden},
			{null,dl_system},
			{null,dl_archive},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=dl_readonly;
		if(OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			int new_attr=0;
			if(dl_readonly.isSelected())new_attr|=FILEATTR_READONLY;
			if(dl_hidden.isSelected()  )new_attr|=FILEATTR_HIDDEN;
			if(dl_system.isSelected()  )new_attr|=FILEATTR_SYSTEM;
			if(dl_archive.isSelected() )new_attr|=FILEATTR_ARCHIVE;
			if(attr!=new_attr){
				return new_attr;
			}
		}
		return attr;
	}
	//===========================
	//ファイルアトリュビュートの獲得
	private static int GetFileAttrWin(File file){
		int attr=0;
		attr|=(!file.canWrite())?FILEATTR_READONLY:0;
		attr|=file.isHidden()?FILEATTR_HIDDEN:0;
		Path path=file.toPath();
		try{
			attr|=((Boolean)Files.getAttribute(path,"dos:system"))?FILEATTR_SYSTEM:0;
		}catch(Exception ex){
			//System.out.println("dos:system error");
		}
		try{
			attr|=((Boolean)Files.getAttribute(path,"dos:archive"))?FILEATTR_ARCHIVE:0;
		}catch(Exception ex){
			//System.out.println("dos:archive error");
		}
		return attr;
	}
	//===========================
	//ファイルアトリュビュートの設定
	private static boolean SetFileAttrWin(File file,int attr){
		int old_attr=GetFileAttrWin(file);
		int dif_attr=old_attr ^ attr;
		if(dif_attr==0)return false;
		Path path=file.toPath();
		if((dif_attr & FILEATTR_READONLY)!=0){
			try{
				Files.setAttribute(path,"dos:readonly",(attr & FILEATTR_READONLY)!=0);
			}catch(Exception ex){
				//System.out.println("dos:radonly error");
				return false;
			}
		}
		if((dif_attr & FILEATTR_HIDDEN)!=0){
			try{
				Files.setAttribute(path,"dos:hidden",(attr & FILEATTR_HIDDEN)!=0);
			}catch(Exception ex){
				//System.out.println("dos:hidden error");
				return false;
			}
		}
		return true;
	}
	//===========================
	//ファイルプロパティダイアログ
	public static void PropertyDialog(Component parent,UFile _file1){
		File file=null;
		try{
			file=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		//
		String title="プロパティ";
		String type_m=null;
		if(file.isDirectory()){
			type_m="ディレクトリ";
		}else if(file.isFile()){
			type_m="ファイル";
		}else{
			type_m="不明";
		}
		String name=file.getName();
		String filename=file.toString();
		Date date=new Date(file.lastModified());
		DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date_m=df.format(date);
		String size_m="";
		if(file.isFile()){
			long filesize=file.length();
			size_m=String.format("%dbyte",filesize);
		}else{
			size_m="---";
		}
		int attr=GetFileAttrWin(file);
		final JCheckBox dl_readonly=new JCheckBox("読み込み専用");
		final JCheckBox dl_hidden  =new JCheckBox("隠し");
		final JCheckBox dl_system  =new JCheckBox("システム");
		dl_readonly.setSelected((attr & FILEATTR_READONLY)!=0);
		dl_hidden.setSelected((attr & FILEATTR_HIDDEN)!=0);
		dl_system.setSelected((attr & FILEATTR_SYSTEM)!=0);
		dl_readonly.setEnabled(false);
		dl_hidden.setEnabled(false);
		dl_system.setEnabled(false);
		
		Object[][] compos1={
			{"種類",type_m},
			{"ファイル名",name},
			{"フルパス",filename},
			{"最終更新日",date_m},
			{"サイズ",size_m},
			{"属性",dl_readonly},
			{null,dl_hidden},
			{null,dl_system},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		OptionDialogBase.OpenOK(parent,title,base_panel1,top_panel);
	}
	//===========================
	//ファイル比較ダイアログ
	public static boolean FileCompareDialog(Component parent,JzFilerDirComparetor.DirCompareOpt opt){
		String title="ファイル比較";
		final JCheckBox dl_readonly =new JCheckBox("読み込み専用");
		final JCheckBox dl_exists   =new JCheckBox("存在");
		final JCheckBox dl_date     =new JCheckBox("日付");
		final JCheckBox dl_new_old  =new JCheckBox("古い順");
		final JCheckBox dl_in_2sec  =new JCheckBox("2秒以内は同じ");
		final JCheckBox dl_size     =new JCheckBox("サイズ");
		final JCheckBox dl_big_small=new JCheckBox("小さい順");
		dl_exists.setSelected(opt.exists);
		dl_date.setSelected(opt.date);
		dl_new_old.setSelected(opt.new_old);
		dl_in_2sec.setSelected(opt.in_2sec);
		dl_size.setSelected(opt.size);
		dl_big_small.setSelected(opt.big_small);
		Object[][] compos1={
			{null,dl_exists},
			{null,dl_date},
			{null,dl_new_old},
			{null,dl_in_2sec},
			{null,dl_size},
			{null,dl_big_small},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		if(OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			opt.exists=dl_exists.isSelected();
			opt.date=dl_date.isSelected();
			opt.new_old=dl_new_old.isSelected();
			opt.in_2sec=dl_in_2sec.isSelected();
			opt.size=dl_size.isSelected();
			opt.big_small=dl_big_small.isSelected();
			return true;
		}
		return false;
	}
	//===========================
	//ファイルリストCellRenderer
	static class UFile_ListCellRenderer extends JLabel implements ListCellRenderer {
		private final static Color back_col  =Color.WHITE;
		private final static Color str_col   =Color.BLACK;
		private final static Color select_col=Color.BLUE.darker().darker();
		private final static Color drop_col  =Color.GREEN.darker();
		public UFile_ListCellRenderer() {
			setOpaque(true);
		}
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
			UFileURL ufilename=(UFileURL)value;
			setText(ufilename.getLocalFilename());
			Color background;
			Color foreground;
			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if(dropLocation!=null
							 && !dropLocation.isInsert()
							 && dropLocation.getIndex() == index) {
				 background = drop_col;
				 foreground = back_col;
			// check if this cell is selected
			}else if(isSelected){
				background = select_col;
				foreground = back_col;
			// unselected, and not the DnD drop location
			}else{
				background = back_col;
				foreground = str_col;
			};
			setBackground(background);
			setForeground(foreground);
			return this;
		}
	}
	//===========================
	//ファイル履歴ダイアログ
	private static void SetListModelFileSet(DefaultListModel<UFile> model,Set<UFile> history){
		model.removeAllElements();
		for(UFile file : history)model.addElement(file);
	}
	public static UFile FileHistoryDialog(final Component parent,final String title,final Set<UFile> history,File current_file,final boolean dir_flg){
		final DefaultListModel<UFile> model=new DefaultListModel<UFile>();
		SetListModelFileSet(model,history);
		//オプションPaneを得る
		final JOptionPane[] option_pane=new JOptionPane[1];
		OptionDialogBase.InitProc proc=new OptionDialogBase.InitProc(){
			public void init(JOptionPane pane){
				option_pane[0]=pane;
			}
		};
		final JList<UFile> dl_list=new JList<UFile>(model);
		dl_list.setCellRenderer(new UFile_ListCellRenderer());
//		if(current_file!=null)dl_list.setSelectedValue(current_file,true);
		//
		dl_list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int index = dl_list.locationToIndex(e.getPoint());
					dl_list.setSelectedIndex(index);
					OptionDialogBase.SetValueOK(option_pane[0]);
					//System.out.println(""+index);
				}
			}
    	});
		
		
		final JPanel dl_buttons=new JPanel();
		final JButton dl_add   =new JButton("追加");
		final JButton dl_delete=new JButton("削除");
		final JButton dl_change=new JButton("変更");
		dl_buttons.add(dl_add);
		dl_buttons.add(dl_delete);
		dl_buttons.add(dl_change);
		dl_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("add:きた");
				//String filename=InputFilenameDialog(parent,"ファイル名",null);
				String filename=InputFilenameDialog(parent,title,null,dir_flg);
				if(filename!=null){
					try{
						UFile file=UFileBuilder.createLocal(filename);
						history.add(file);
						SetListModelFileSet(model,history);
					}catch(Exception ex){
					}
				}
			}
		});
		dl_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("delete:きた");
				UFile file=dl_list.getSelectedValue();
				if(file!=null){
					history.remove(file);
					SetListModelFileSet(model,history);
				}
			}
		});
		dl_change.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("change:きた");
				UFile file=dl_list.getSelectedValue();
				if(file!=null){
					//String filename=InputFilenameDialog(parent,"ファイル名",(String)file.toString());
					String filename=InputFilenameDialog(parent,title,(String)file.toString(),dir_flg);
					if(filename!=null){
						int index=dl_list.getSelectedIndex();
						try{
							UFile new_file=UFileBuilder.createLocal(filename);
							history.remove(file);
							history.add(new_file);
							SetListModelFileSet(model,history);
						}catch(Exception ex){
						}
					}
				}
			}
		});
		dl_delete.setEnabled(false);
		dl_change.setEnabled(false);
		//
		dl_list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				//System.out.println(""+e);
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
		if(current_file!=null)dl_list.setSelectedValue(current_file,true);
		
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
	//===========================
	//ファイルマスクCellRenderer
	static class FileMask_ListCellRenderer extends JLabel implements ListCellRenderer {
		private final static Color back_col  =Color.WHITE;
		private final static Color str_col   =Color.BLACK;
		private final static Color select_col=Color.BLUE.darker().darker();
		private final static Color drop_col  =Color.GREEN.darker();
		public FileMask_ListCellRenderer() {
			setOpaque(true);
		}
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
			File file=(File)value;
			setText(file.toString());
			Color background;
			Color foreground;
			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if(dropLocation!=null
							 && !dropLocation.isInsert()
							 && dropLocation.getIndex() == index) {
				 background = drop_col;
				 foreground = back_col;
			// check if this cell is selected
			}else if(isSelected){
				background = select_col;
				foreground = back_col;
			// unselected, and not the DnD drop location
			}else{
				background = back_col;
				foreground = str_col;
			};
			setBackground(background);
			setForeground(foreground);
			return this;
		}
	}
	//===========================
	//ファイルマスク選択
	private static void SetListModelFileSet2(DefaultListModel<File> model,Set<File> history){
		model.removeAllElements();
		for(File file : history)model.addElement(file);
	}
	public static File FileMaskDialog(final Component parent,final String title,final Set<File> history,File current_file,final boolean dir_flg){
		final DefaultListModel<File> model=new DefaultListModel<File>();
		SetListModelFileSet2(model,history);
		//オプションPaneを得る
		final JOptionPane[] option_pane=new JOptionPane[1];
		OptionDialogBase.InitProc proc=new OptionDialogBase.InitProc(){
			public void init(JOptionPane pane){
				option_pane[0]=pane;
			}
		};
		final JList<File> dl_list=new JList<File>(model);
		dl_list.setCellRenderer(new FileMask_ListCellRenderer());
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
				String filename=InputFilenameDialog(parent,title,null,dir_flg);
				if(filename!=null){
					try{
						File file=new File(filename);
						history.add(file);
						SetListModelFileSet2(model,history);
					}catch(Exception ex){
					}
				}
			}
		});
		dl_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				File file=dl_list.getSelectedValue();
				if(file!=null){
					history.remove(file);
					SetListModelFileSet2(model,history);
				}
			}
		});
		dl_change.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
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
			}
		});
		dl_reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					File file=new File("*.*");
					history.add(file);
					SetListModelFileSet2(model,history);
					dl_list.setSelectedValue(file,true);
				}catch(Exception ex){}
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
		if(current_file!=null)dl_list.setSelectedValue(current_file,true);
		
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
	//===========================
	//ファイル名をクリップボードにコピー
	public static void CopyFilename(List<UFile> vals){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard cb = kit.getSystemClipboard();
		String m="";
		for(int i=0;i<vals.size();i++){
			m+=vals.get(i)+"\n";
		}
		StringSelection strSel = new StringSelection(m);
		cb.setContents(strSel, strSel);
	}
	//===========================
	//ファイルのコピー
	public static boolean CopyFile(Component parent,UFile _file1,UFile _dst_path){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		File dst_path=null;
		try{
			dst_path=((UFileURL)_dst_path).getFile();
		}catch(Exception ex){}
		String filebody=file1.getName();
		File file2=new File(dst_path,filebody);
		File src_path=file1.getParentFile();
		if(file1.equals(file2)){
			int rc=YesNoDialog(parent,String.format("%s\nコピー先に同じ名前のファイルが存在します。\n名前を変えてコピーしますか?\n",file1));
			if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("同じファイル名:"+file1);
			return false;
		}
		String m=String.format("%sを%sへコピーしますか?",file1,file2);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("copy:"+file1+" -> "+file2);
		return ufileToolJ2D.copy(file1,file2);
	}
	//===========================
	//ファイルの移動
	public static boolean MoveFile(Component parent,UFile _file1,UFile _dst_path){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		File dst_path=null;
		try{
			dst_path=((UFileURL)_dst_path).getFile();
		}catch(Exception ex){}
		String filebody=file1.getName();
		File src_path=file1.getParentFile();
		if(src_path.equals(dst_path)){
System.out.println("同じパス:"+src_path);
			return false;
		}
		String m=String.format("%sを%sへ移動しますか?",file1,dst_path);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
		File file2=new File(dst_path,filebody);
		Path src = file1.toPath();
		Path dst = file2.toPath();
		try{
			//Path r = Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			Path r = Files.move(src, dst);
			return true;
		}catch(Exception ex){
System.out.println("move err:"+ex);
			return false;
		}
	}
	//===========================
	//ファイルの削除
	public static boolean DeleteFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		String m=String.format("%sを削除しますか?",file1);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("delete:"+file1);
		return file1.delete();
	}
	//===========================
	//ファイル名を変えてコピー
	public static boolean RenameCopyFile(Component parent,UFile _file1,UFile _dst_path){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		File dst_path=null;
		try{
			dst_path=((UFileURL)_dst_path).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		String name=_file1.getFileBody();
		String new_name=InputFilenameDialog(parent,"名前を変えてコピー",name,false);
		if(new_name==null)return false;
		UFile _file2=_file1.changeFileBody(new_name);
		if(_file1.equals(_file2)){
			OKDialog(parent,String.format("%s\nコピー元とコピー先が同じファイルです",file1));
			return false;
		}
		
		File file2=null;
		try{
			file2=((UFileURL)_file2).getFile();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(file2.exists()){
		//同じものがある
			int rc=YesNoDialog(parent,String.format("%s\nコピー先に同じ名前のファイルが存在します。\n名前を変えてコピーしますか?\n",file1));
			if(rc!=JOptionPane.OK_OPTION)return false;
		}else{
		//同じものがない
			String m=String.format("%sを%sへコピーしますか?",file1,file2);
			int rc=YesNoDialog(parent,m);
			if(rc!=JOptionPane.OK_OPTION)return false;
		}
System.out.println("rename copy:"+file1+" -> "+file2);
		return ufileToolJ2D.copy(file1,file2);
	}
	//===========================
	//ファイル名の変更
	public static boolean RenameFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		String name=_file1.getFileBody();
		String new_name=InputFilenameDialog(parent,"ファイル名の変更",name,false);
		if(new_name==null)return false;
//System.out.println("new_name:"+new_name);
		//同じものがあるかチェック
		UFile _file2=_file1.changeFileBody(new_name);
		if(_file2.exists()){
			OKDialog(parent,String.format("%s\n同じ名前のファイルが存在します",file1));
			return false;
		}
		//
		File file2=null;
		try{
			file2=((UFileURL)_file2).getFile();
		}catch(Exception ex){
			ex.printStackTrace();
		}
//System.out.println("file2:"+file2);
		try{
			file1.renameTo(file2);
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	//===========================
	//日付の変更
	public static boolean ChangeDateFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		long date=file1.lastModified();
		long new_date=InputDateDialog(parent,date);
		if(date==new_date)return false;
		return file1.setLastModified(new_date);
	}
	//===========================
	//属性の変更
	public static boolean ChangeAttrFile(Component parent,UFile _file1){
		return ChangeAttrFileWin(parent,_file1);
	}
	//属性の変更(Windows)
	public static boolean ChangeAttrFileWin(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		int attr=GetFileAttrWin(file1);
		int new_attr=InputAttrDialogWin(parent,attr);
		if(attr==new_attr)return false;
		return SetFileAttrWin(file1,new_attr);
	}
	//===========================
	//ショートカットの作成
	public static boolean MakeShortCutFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		//
		String m=String.format("%sのショートカットを作成しますか?",file1);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
		//
		try{
			String filename=file1.getAbsolutePath();
			String name=file1.getName();
			String path=file1.getParent();
			String shortcut_name=name+" - ショートカット";
System.out.println("MakeShortCutFile:path("+path+"),name("+shortcut_name+")");
			LnkFile shortcut = new LnkFile(path,shortcut_name);
			shortcut.setPath(filename);
			shortcut.save();
			return true;
		}catch(Exception ex){
System.out.println(""+ex);
		}
		return false;
	}
	//===========================
	//プロパティーの表示
	public static boolean ViewProperty(Component parent,UFile _file1){
		if(_file1==null)return false;
		PropertyDialog(parent,_file1);
		return false;
	}
	//===========================
	//新規ファイル
	public static boolean NewFile(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		if(current_dir==null)return false;
		//
		String name=InputFilenameDialog(parent,"新規ファイルの作成","",false);
		if(name==null)return false;
		File file=new File(current_dir,name);
		if(file.exists()){
			OKDialog(parent,String.format("%s\n同じ名前のファイルが存在します",file));
			return false;
		}
		try{
			Files.createFile(file.toPath());
			return true;
		}catch(Exception ex){
System.out.println(String.format("%sの作成に失敗しました",file));
			return false;
		}
	}
	//===========================
	//新規ディレクトリ
	public static boolean NewDir(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		if(current_dir==null)return false;
		String name=InputFilenameDialog(parent,"新規ディレクトリの作成","",true);
		if(name==null)return false;
		File file=new File(current_dir,name);
System.out.println(""+file);
		if(file.exists()){
			OKDialog(parent,String.format("%s\n同じ名前のフォルダが存在します",file));
			return false;
		}
		try{
			Files.createDirectory(file.toPath());
			return true;
		}catch(Exception ex){
System.out.println(String.format("%sの作成に失敗しました",file));
			return false;
		}
	}
	//===========================
	//マスク選択(登録、選択)
	//===========================
	//フォルダ開く(標準ダイアログ)
	public static UFile OpenDir(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("フォルダの選択");
		filechooser.setDialogType(JFileChooser.OPEN_DIALOG);
		filechooser.setAcceptAllFileFilterUsed(false);	//全てのファイル
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filechooser.setCurrentDirectory(current_dir);
		int selected = filechooser.showOpenDialog(parent);
		if(selected!=JFileChooser.APPROVE_OPTION)return null;
		File new_dir=filechooser.getSelectedFile();
		if(!new_dir.isDirectory())return null;
System.out.println(""+new_dir);
		try{
			return UFileBuilderURL.createFromFile(new_dir);
		}catch(Exception ex){
			return null;
		}
	}
	/*
	//===========================
	//フォルダ履歴
	public static File SelectDirHistory(Component parent,Set<UFile> history){
		
		return null;
	}
	//===========================
	//フォルダリスト(登録&選択)
	public static File SelectDirList(Component parent,Set<UFile> dirlist){
		
		return null;
	}
	*/
}

