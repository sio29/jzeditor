/******************************************************************************
;	FTP�֌W
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.ftp;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ListModel;

import org.apache.commons.net.ftp.*;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.ureg.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.backends.j2d.ftp.*;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.confirm.*;


public class JzFilerFTP{
	public static UFileFTPInfo FTPInfoDialog(Component parent,final FTPInfoList info){
		String title="FTP���";
		final JList<UFileFTPInfo> dl_list=new JList<UFileFTPInfo>(info.toArray());
		dl_list.setSelectedIndex(0);
		final JButton dl_new   =new JButton("�V�K");
		final JButton dl_edit  =new JButton("�ҏW");
		final JButton dl_delete=new JButton("�폜");
		final JButton dl_copy  =new JButton("�R�s�[");
		final JPanel dl_buttons=new JPanel();
		//MouseListener���Z�b�g����
		dl_list.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				int bt=e.getButton();
				IVECTOR2 p = ConvAWT.Point_IVEC2(e.getPoint());
				int index = dl_list.locationToIndex(ConvAWT.IVEC2_Point(p));
				ListModel list_model=dl_list.getModel();
				if(index<0 || index>=list_model.getSize())return;
				UFileFTPInfo src_info=(UFileFTPInfo)list_model.getElementAt(index);
				if(bt==MouseEvent.BUTTON1){
					if(e.getClickCount()==2){
						if(FTPSettingDialog(dl_edit,src_info)){
							dl_list.repaint();
						}
					}
				}
			}
		});
		dl_new.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				UFileFTPInfo new_info=new UFileFTPInfo("New","","","");
				info.add(new_info);
				dl_list.setListData(info.toArray());
				dl_list.setSelectedValue(new_info,true);
				if(info.size()>0){
					dl_edit.setEnabled(true);
					dl_delete.setEnabled(true);
					dl_copy.setEnabled(true);
				}
			}
		});
		dl_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index=dl_list.getSelectedIndex();
				UFileFTPInfo del_info=dl_list.getSelectedValue();
				info.remove(del_info);
				dl_list.setListData(info.toArray());
				if(info.size()>0){
					if(index>=info.size()){
						index=info.size()-1;
					}
					dl_list.setSelectedIndex(index);
				}else{
					dl_edit.setEnabled(false);
					dl_delete.setEnabled(false);
					dl_copy.setEnabled(false);
				}
			}
		});
		dl_edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index=dl_list.getSelectedIndex();
				UFileFTPInfo src_info=dl_list.getSelectedValue();
				if(FTPSettingDialog(dl_edit,src_info)){
					dl_list.repaint();
				}
			}
		});
		dl_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				UFileFTPInfo src_info=dl_list.getSelectedValue();
				UFileFTPInfo new_info=src_info.copy();
				info.add(new_info);
				dl_list.setListData(info.toArray());
				dl_list.setSelectedValue(new_info,true);
				if(info.size()>0){
					dl_edit.setEnabled(true);
					dl_delete.setEnabled(true);
					dl_copy.setEnabled(true);
				}
			}
		});
		dl_buttons.add(dl_new);
		dl_buttons.add(dl_edit);
		dl_buttons.add(dl_delete);
		dl_buttons.add(dl_copy);
		final JPanel base_panel1=new JPanel();
		base_panel1.setLayout(new BorderLayout());
		base_panel1.add("North",dl_buttons);
		base_panel1.add("Center",new JScrollPane(dl_list));
		final JComponent top_panel=dl_list;
		if(!OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			return null;
		}
		UFileFTPInfo ret_info=dl_list.getSelectedValue();
		return ret_info;
	}
	//===========================
	//FTP Option
	public static boolean FTPSettingDialog(Component parent,final UFileFTPInfo info){
		String title="FTP�I�v�V����";
		JTextField dl_name        =new JTextField(info.getTitle());			//���O
		JTextField dl_server      =new JTextField(info.getServer());			//�T�[�o�[URL
		JTextField dl_user        =new JTextField(info.getUser());			//���[�U�[
		JPasswordField dl_password=new JPasswordField(info.getPassword());	//�p�X���[�h
		JButton dl_check=new JButton("�ڑ��`�F�b�N");
		dl_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			//�ڑ��`�F�b�N
//System.out.println("����");
				//boolean r=FTPLoginCheck(info);
				boolean r=info.checkConnection();
				String m="";
				if(r){
					m="�ڑ��ɐ������܂���";
				}else{
					m="�ڑ��Ɏ��s���܂���";
				}
				ConfirmDialog.OpenOK(parent,m);
				
			}
		});
		Object[][] compos1={
			{"���O",dl_name},
			{"�T�[�o�[",dl_server},
			{"���[�U�[",dl_user},
			{"�p�X���[�h",dl_password},
			{null,dl_check},
		};
		JComponent base_panel1=OptionDialogBase.MakeGroupContainer(compos1);
		final JComponent top_panel=null;
		if(!OptionDialogBase.Open(parent,title,base_panel1,top_panel)){
			return false;
		}
		info.setTitle   (dl_name.getText());
		info.setServer  (dl_server.getText());
		info.setUser    (dl_user.getText());
		info.setPassword(new String(dl_password.getPassword()));
//System.out.println("password:"+info.getPassword());
		return true;
	}
	/*
	public static boolean FTPLoginCheck(UFileFTPInfo info){
		System.out.println("FTPLoginCheck:"+info);
		boolean ret=false;
		//FileOutputStream ostream = null;
		// FTPClient�̐���
		FTPClient ftpclient=new FTPClient();
		try{
			// �T�[�o�ɐڑ�
			ftpclient.connect(info.getServer());
			int reply = ftpclient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				System.err.println("connect fail");
				//System.exit(1);
				return false;
			}
			// ���O�C��
			if(!ftpclient.login(info.getUser(),info.getPassword())){
				System.err.println("login fail");
				//System.exit(2);
				return false;
			}
			//// �o�C�i�����[�h�ɐݒ�
			//ftpclient.setFileType(FTP.BINARY_FILE_TYPE);
			//// �t�@�C����M
			//ostream = new FileOutputStream("localfile");
			//ftpclient.retrieveFile("remotefile", ostream);
			System.out.println("Lonin OK!!");
			ret=true;
		}catch(Exception e){
			//e.printStackTrace();
			System.out.println(""+e);
		}finally{
			System.out.println("finally");
			try{
				if(ftpclient.isConnected()){
					ftpclient.disconnect();
					System.out.println("disconnect()");
				}
			}catch(Exception ex){}
			//if (ostream != null) {
			//	try {
			//		ostream.close();
			//	}catch(Exception e){
			//		e.printStackTrace();
			//	}
			//}
		}
		return ret;
	}
	*/
}
