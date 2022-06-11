/******************************************************************************
;	Grep�I�v�V����
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.grep;


import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class GrepOption{
	public UInputHistory str_hist=new UInputHistory();		//����������̃q�X�g��
	public UInputHistory dir_hist=new UInputHistory();		//�f�B���N�g���̃q�X�g��
	public boolean bigsmall_flg;							//�啶���A�������̔���
	public boolean subdir_flg;								//�T�u�f�B���N�g����
	public boolean dirlist_flg;								//�f�B���N�g�����X�g�̂�
	//
	public String search_str="";							//����������
	public Object search_opt;								//�����I�v�V����
	public String dir_str="";								//�����t�H���_
	//
	public GrepOption(){
		bigsmall_flg=true;
		subdir_flg  =true;
		dirlist_flg =false;
		
	}
	
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		str_hist.ReadReg(regout,node_name+"/StrHist");
		dir_hist.ReadReg(regout,node_name+"/DirHist");
		bigsmall_flg=regout.ReadRegBool(node,"bigsmall_flg",bigsmall_flg);
		subdir_flg  =regout.ReadRegBool(node,"subdir_flg"  ,subdir_flg);
		dirlist_flg =regout.ReadRegBool(node,"dirlist_flg" ,dirlist_flg);
		
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		str_hist.OutReg(regout,node_name+"/StrHist");
		dir_hist.OutReg(regout,node_name+"/DirHist");
		regout.OutRegBool(node,"bigsmall_flg",bigsmall_flg);
		regout.OutRegBool(node,"subdir_flg"  ,subdir_flg);
		regout.OutRegBool(node,"dirlist_flg" ,dirlist_flg);
		
	}
}

