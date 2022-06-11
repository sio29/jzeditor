/******************************************************************************
;	FTP関係
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
		String title="FTP情報";
		final JList<UFileFTPInfo> dl_list=new JList<UFileFTPInfo>(info.toArray());
		dl_list.setSelectedIndex(0);
		final JButton dl_new   =new JButton("新規");
		final JButton dl_edit  =new JButton("編集");
		final JButton dl_delete=new JButton("削除");
		final JButton dl_copy  =new JButton("コピー");
		final JPanel dl_buttons=new JPanel();
		//MouseListenerをセットする
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
		String title="FTPオプション";
		JTextField dl_name        =new JTextField(info.getTitle());			//名前
		JTextField dl_server      =new JTextField(info.getServer());			//サーバーURL
		JTextField dl_user        =new JTextField(info.getUser());			//ユーザー
		JPasswordField dl_password=new JPasswordField(info.getPassword());	//パスワード
		JButton dl_check=new JButton("接続チェック");
		dl_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			//接続チェック
//System.out.println("きた");
				//boolean r=FTPLoginCheck(info);
				boolean r=info.checkConnection();
				String m="";
				if(r){
					m="接続に成功しました";
				}else{
					m="接続に失敗しました";
				}
				ConfirmDialog.OpenOK(parent,m);
				
			}
		});
		Object[][] compos1={
			{"名前",dl_name},
			{"サーバー",dl_server},
			{"ユーザー",dl_user},
			{"パスワード",dl_password},
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
		// FTPClientの生成
		FTPClient ftpclient=new FTPClient();
		try{
			// サーバに接続
			ftpclient.connect(info.getServer());
			int reply = ftpclient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				System.err.println("connect fail");
				//System.exit(1);
				return false;
			}
			// ログイン
			if(!ftpclient.login(info.getUser(),info.getPassword())){
				System.err.println("login fail");
				//System.exit(2);
				return false;
			}
			//// バイナリモードに設定
			//ftpclient.setFileType(FTP.BINARY_FILE_TYPE);
			//// ファイル受信
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
