/******************************************************************************
;	Vz�t�@�C���[�A�c�[����
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
	//���͂��E�������E�����
	public static int YesNoDialog(Component parent,String m){
		return ConfirmDialog.Open(parent,m);
	}
	//===========================
	//��OK
	public static void OKDialog(Component parent,String m){
		ConfirmDialog.OpenOK(parent,m);
	}
	//===========================
	//�t�@�C�����̓���
	public static String InputFilenameDialog(Component parent,String title,String filename,boolean dir_flg){
		final FilenameTextField dl_filename=new FilenameTextField(dir_flg);
		if(filename!=null){
			dl_filename.setText(filename);
		}else{
			dl_filename.setCurrentDir(new File("."));
		}
		String name=null;
		if(!dir_flg){
			name="�t�@�C����";
		}else{
			name="�t�H���_��";
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
	//�p�l�����̃��T�C�Y
	private static void ResizePanelWidth(JComponent panel,int width){
		Dimension size=panel.getPreferredSize();
		if(size.width<width)size.width=width;
		panel.setPreferredSize(size);
	}
	//===========================
	//���t�̓���
	public static long InputDateDialog(Component parent,long m){
		Calendar cl=Calendar.getInstance();
		cl.setTimeInMillis(m);
		int year  =cl.get(Calendar.YEAR);
		int month =cl.get(Calendar.MONTH)+1;
		int day   =cl.get(Calendar.DAY_OF_MONTH);
		int hour  =cl.get(Calendar.HOUR_OF_DAY);
		int minute=cl.get(Calendar.MINUTE);
		int second=cl.get(Calendar.SECOND);
		String title="���t�̕ύX";
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
			{"�N/��/��",dl_year,dl_month ,dl_day},
			{"��/��/�b",dl_hour,dl_minute,dl_second},
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
	//�t�@�C���A�g�����r���[�g�̓���
	public static int FILEATTR_READONLY=0x01;
	public static int FILEATTR_HIDDEN  =0x02;
	public static int FILEATTR_SYSTEM  =0x04;
	public static int FILEATTR_ARCHIVE =0x08;
	public static int InputAttrDialogWin(Component parent,int attr){
		String title="�����̕ύX";
		final JCheckBox dl_readonly=new JCheckBox("�ǂݍ��ݐ�p");
		final JCheckBox dl_hidden  =new JCheckBox("�B��");
		final JCheckBox dl_system  =new JCheckBox("�V�X�e��");
		final JCheckBox dl_archive =new JCheckBox("�A�[�J�C�u");
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
	//�t�@�C���A�g�����r���[�g�̊l��
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
	//�t�@�C���A�g�����r���[�g�̐ݒ�
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
	//�t�@�C���v���p�e�B�_�C�A���O
	public static void PropertyDialog(Component parent,UFile _file1){
		File file=null;
		try{
			file=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		//
		String title="�v���p�e�B";
		String type_m=null;
		if(file.isDirectory()){
			type_m="�f�B���N�g��";
		}else if(file.isFile()){
			type_m="�t�@�C��";
		}else{
			type_m="�s��";
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
		final JCheckBox dl_readonly=new JCheckBox("�ǂݍ��ݐ�p");
		final JCheckBox dl_hidden  =new JCheckBox("�B��");
		final JCheckBox dl_system  =new JCheckBox("�V�X�e��");
		dl_readonly.setSelected((attr & FILEATTR_READONLY)!=0);
		dl_hidden.setSelected((attr & FILEATTR_HIDDEN)!=0);
		dl_system.setSelected((attr & FILEATTR_SYSTEM)!=0);
		dl_readonly.setEnabled(false);
		dl_hidden.setEnabled(false);
		dl_system.setEnabled(false);
		
		Object[][] compos1={
			{"���",type_m},
			{"�t�@�C����",name},
			{"�t���p�X",filename},
			{"�ŏI�X�V��",date_m},
			{"�T�C�Y",size_m},
			{"����",dl_readonly},
			{null,dl_hidden},
			{null,dl_system},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		OptionDialogBase.OpenOK(parent,title,base_panel1,top_panel);
	}
	//===========================
	//�t�@�C����r�_�C�A���O
	public static boolean FileCompareDialog(Component parent,JzFilerDirComparetor.DirCompareOpt opt){
		String title="�t�@�C����r";
		final JCheckBox dl_readonly =new JCheckBox("�ǂݍ��ݐ�p");
		final JCheckBox dl_exists   =new JCheckBox("����");
		final JCheckBox dl_date     =new JCheckBox("���t");
		final JCheckBox dl_new_old  =new JCheckBox("�Â���");
		final JCheckBox dl_in_2sec  =new JCheckBox("2�b�ȓ��͓���");
		final JCheckBox dl_size     =new JCheckBox("�T�C�Y");
		final JCheckBox dl_big_small=new JCheckBox("��������");
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
	//�t�@�C�����X�gCellRenderer
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
	//�t�@�C�������_�C�A���O
	private static void SetListModelFileSet(DefaultListModel<UFile> model,Set<UFile> history){
		model.removeAllElements();
		for(UFile file : history)model.addElement(file);
	}
	public static UFile FileHistoryDialog(final Component parent,final String title,final Set<UFile> history,File current_file,final boolean dir_flg){
		final DefaultListModel<UFile> model=new DefaultListModel<UFile>();
		SetListModelFileSet(model,history);
		//�I�v�V����Pane�𓾂�
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
		final JButton dl_add   =new JButton("�ǉ�");
		final JButton dl_delete=new JButton("�폜");
		final JButton dl_change=new JButton("�ύX");
		dl_buttons.add(dl_add);
		dl_buttons.add(dl_delete);
		dl_buttons.add(dl_change);
		dl_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("add:����");
				//String filename=InputFilenameDialog(parent,"�t�@�C����",null);
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
//System.out.println("delete:����");
				UFile file=dl_list.getSelectedValue();
				if(file!=null){
					history.remove(file);
					SetListModelFileSet(model,history);
				}
			}
		});
		dl_change.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("change:����");
				UFile file=dl_list.getSelectedValue();
				if(file!=null){
					//String filename=InputFilenameDialog(parent,"�t�@�C����",(String)file.toString());
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
	//�t�@�C���}�X�NCellRenderer
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
	//�t�@�C���}�X�N�I��
	private static void SetListModelFileSet2(DefaultListModel<File> model,Set<File> history){
		model.removeAllElements();
		for(File file : history)model.addElement(file);
	}
	public static File FileMaskDialog(final Component parent,final String title,final Set<File> history,File current_file,final boolean dir_flg){
		final DefaultListModel<File> model=new DefaultListModel<File>();
		SetListModelFileSet2(model,history);
		//�I�v�V����Pane�𓾂�
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
		final JButton dl_add   =new JButton("�ǉ�");
		final JButton dl_delete=new JButton("�폜");
		final JButton dl_change=new JButton("�ύX");
		final JButton dl_reset =new JButton("����");
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
	//�t�@�C�������N���b�v�{�[�h�ɃR�s�[
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
	//�t�@�C���̃R�s�[
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
			int rc=YesNoDialog(parent,String.format("%s\n�R�s�[��ɓ������O�̃t�@�C�������݂��܂��B\n���O��ς��ăR�s�[���܂���?\n",file1));
			if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("�����t�@�C����:"+file1);
			return false;
		}
		String m=String.format("%s��%s�փR�s�[���܂���?",file1,file2);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("copy:"+file1+" -> "+file2);
		return ufileToolJ2D.copy(file1,file2);
	}
	//===========================
	//�t�@�C���̈ړ�
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
System.out.println("�����p�X:"+src_path);
			return false;
		}
		String m=String.format("%s��%s�ֈړ����܂���?",file1,dst_path);
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
	//�t�@�C���̍폜
	public static boolean DeleteFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		String m=String.format("%s���폜���܂���?",file1);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
System.out.println("delete:"+file1);
		return file1.delete();
	}
	//===========================
	//�t�@�C������ς��ăR�s�[
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
		String new_name=InputFilenameDialog(parent,"���O��ς��ăR�s�[",name,false);
		if(new_name==null)return false;
		UFile _file2=_file1.changeFileBody(new_name);
		if(_file1.equals(_file2)){
			OKDialog(parent,String.format("%s\n�R�s�[���ƃR�s�[�悪�����t�@�C���ł�",file1));
			return false;
		}
		
		File file2=null;
		try{
			file2=((UFileURL)_file2).getFile();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(file2.exists()){
		//�������̂�����
			int rc=YesNoDialog(parent,String.format("%s\n�R�s�[��ɓ������O�̃t�@�C�������݂��܂��B\n���O��ς��ăR�s�[���܂���?\n",file1));
			if(rc!=JOptionPane.OK_OPTION)return false;
		}else{
		//�������̂��Ȃ�
			String m=String.format("%s��%s�փR�s�[���܂���?",file1,file2);
			int rc=YesNoDialog(parent,m);
			if(rc!=JOptionPane.OK_OPTION)return false;
		}
System.out.println("rename copy:"+file1+" -> "+file2);
		return ufileToolJ2D.copy(file1,file2);
	}
	//===========================
	//�t�@�C�����̕ύX
	public static boolean RenameFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		String name=_file1.getFileBody();
		String new_name=InputFilenameDialog(parent,"�t�@�C�����̕ύX",name,false);
		if(new_name==null)return false;
//System.out.println("new_name:"+new_name);
		//�������̂����邩�`�F�b�N
		UFile _file2=_file1.changeFileBody(new_name);
		if(_file2.exists()){
			OKDialog(parent,String.format("%s\n�������O�̃t�@�C�������݂��܂�",file1));
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
	//���t�̕ύX
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
	//�����̕ύX
	public static boolean ChangeAttrFile(Component parent,UFile _file1){
		return ChangeAttrFileWin(parent,_file1);
	}
	//�����̕ύX(Windows)
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
	//�V���[�g�J�b�g�̍쐬
	public static boolean MakeShortCutFile(Component parent,UFile _file1){
		File file1=null;
		try{
			file1=((UFileURL)_file1).getFile();
		}catch(Exception ex){}
		if(file1==null)return false;
		//
		String m=String.format("%s�̃V���[�g�J�b�g���쐬���܂���?",file1);
		int rc=YesNoDialog(parent,m);
		if(rc!=JOptionPane.OK_OPTION)return false;
		//
		try{
			String filename=file1.getAbsolutePath();
			String name=file1.getName();
			String path=file1.getParent();
			String shortcut_name=name+" - �V���[�g�J�b�g";
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
	//�v���p�e�B�[�̕\��
	public static boolean ViewProperty(Component parent,UFile _file1){
		if(_file1==null)return false;
		PropertyDialog(parent,_file1);
		return false;
	}
	//===========================
	//�V�K�t�@�C��
	public static boolean NewFile(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		if(current_dir==null)return false;
		//
		String name=InputFilenameDialog(parent,"�V�K�t�@�C���̍쐬","",false);
		if(name==null)return false;
		File file=new File(current_dir,name);
		if(file.exists()){
			OKDialog(parent,String.format("%s\n�������O�̃t�@�C�������݂��܂�",file));
			return false;
		}
		try{
			Files.createFile(file.toPath());
			return true;
		}catch(Exception ex){
System.out.println(String.format("%s�̍쐬�Ɏ��s���܂���",file));
			return false;
		}
	}
	//===========================
	//�V�K�f�B���N�g��
	public static boolean NewDir(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		if(current_dir==null)return false;
		String name=InputFilenameDialog(parent,"�V�K�f�B���N�g���̍쐬","",true);
		if(name==null)return false;
		File file=new File(current_dir,name);
System.out.println(""+file);
		if(file.exists()){
			OKDialog(parent,String.format("%s\n�������O�̃t�H���_�����݂��܂�",file));
			return false;
		}
		try{
			Files.createDirectory(file.toPath());
			return true;
		}catch(Exception ex){
System.out.println(String.format("%s�̍쐬�Ɏ��s���܂���",file));
			return false;
		}
	}
	//===========================
	//�}�X�N�I��(�o�^�A�I��)
	//===========================
	//�t�H���_�J��(�W���_�C�A���O)
	public static UFile OpenDir(Component parent,UFile _current_dir){
		File current_dir=null;
		try{
			current_dir=((UFileURL)_current_dir).getFile();
		}catch(Exception ex){}
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("�t�H���_�̑I��");
		filechooser.setDialogType(JFileChooser.OPEN_DIALOG);
		filechooser.setAcceptAllFileFilterUsed(false);	//�S�Ẵt�@�C��
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
	//�t�H���_����
	public static File SelectDirHistory(Component parent,Set<UFile> history){
		
		return null;
	}
	//===========================
	//�t�H���_���X�g(�o�^&�I��)
	public static File SelectDirList(Component parent,Set<UFile> dirlist){
		
		return null;
	}
	*/
}

