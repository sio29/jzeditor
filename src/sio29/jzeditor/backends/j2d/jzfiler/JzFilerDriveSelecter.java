/******************************************************************************
;	Vz�t�@�C���[�A�h���C�u�Z���N�^
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.HashMap;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.JOptionPane;

import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;

public class JzFilerDriveSelecter{
	//�h���C�u�̃Z�������_���[
	static class UFile_ListCellRenderer extends JLabel implements ListCellRenderer{
		private final static Color back_col  =Color.WHITE;
		private final static Color str_col   =Color.BLACK;
		private final static Color select_col=Color.BLUE.darker().darker();
		private final static Color drop_col  =Color.GREEN.darker();
		public UFile_ListCellRenderer() {
			setOpaque(true);
		}
		public String getSizeString(long size){
			return ""+size;
		}
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
			UFile ufilename=((UFile)value);
			String filename=ufilename.getLocalFilename();
			String m=filename+" : "+ufilename.getSystemDisplayName();
			setText(m);
			Color background;
			Color foreground;
			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if(dropLocation!=null && !dropLocation.isInsert() && dropLocation.getIndex() == index){
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
	
	//�h���C�u�I��
	public static UFile selectDrive(Component parent,UFile[] drives,UFile current_dir){
		//�h���C�u�ꗗ�𓾂�
		String dir=null;
		try{
			dir=current_dir.getLocalFilename();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		int select_index=-1;
		for(int i=0;i<drives.length;i++){
			String drive=null;
			try{
				drive=drives[i].getLocalFilename();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(dir.startsWith(drive)){
				//�����ʒu
				select_index=i;
				break;
			}
		}
		//�����ɑ΂��Ẵh���C�uIndex�̊l��
		final HashMap<Character,Integer> drive_charmap=new HashMap<Character,Integer>();
		for(int i=0;i<drives.length;i++){
			String path=null;
			try{
				path=drives[i].getLocalFilename();
				if(path!=null && path.length()>=2){
					drive_charmap.put(path.charAt(0),i);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		//�I�v�V����Pane�𓾂�
		final JOptionPane[] option_pane=new JOptionPane[1];
		OptionDialogBase.InitProc proc=new OptionDialogBase.InitProc(){
			public void init(JOptionPane pane){
				option_pane[0]=pane;
			}
		};
		//���X�g�Ƀh���C�u�ꗗ�ݒ�
		final JList<UFile> dl_list=new JList<UFile>(drives);
		dl_list.setCellRenderer(new UFile_ListCellRenderer());
		//A�`Z�̃L�[�������ꂽ�Ƃ��̏���
		dl_list.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				char m=e.getKeyChar();
				if(m>='a' && m<='z')m=(char)(m-'a'+'A');
				if(m>='A' && m<='Z'){
					Integer _index=drive_charmap.get(m);
					if(_index!=null){
						dl_list.setSelectedIndex((int)_index);
						OptionDialogBase.SetValueOK(option_pane[0]);
					}
				}
			}
		});
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
		
		//���݂̃h���C�u�ݒ�
		if(select_index>=0){
			dl_list.setSelectedIndex(select_index);
		}
		final Container root_pane=new JScrollPane(dl_list);
		
		
		//�_�C�A���O�I�[�v��
		if(!OptionDialogBase.Open(parent,"�h���C�u�I��",root_pane,dl_list,null,proc)){
			return null;
		}else{
		//�I�����ꂽ
			if(dl_list.getSelectedIndex()==select_index)return null;
			//�J�����g�f�B���N�g���̐ݒ�
			return dl_list.getSelectedValue();
		}
	}
}
