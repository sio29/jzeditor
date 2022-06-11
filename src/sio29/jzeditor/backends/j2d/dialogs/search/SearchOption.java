/******************************************************************************
;	�����A�u���I�v�V����
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.search;


import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class SearchOption{
	public UInputHistory search_str_hist =new UInputHistory();		//����������̃q�X�g��
	public UInputHistory replace_str_hist=new UInputHistory();	//�u��������̃q�X�g��
	public String search_str=null;								//����������
	public String replace_str=null;								//�u��������
	public boolean bigsmall_flg;								//�啶���A�������̔���
	public boolean rex_flg;										//���K�\��
	public int updown_flg;										//��������(0:��,1:��)
	//
	public SearchOption(){
		bigsmall_flg=false;
		rex_flg=false;
		updown_flg=0;
	}
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		search_str_hist.ReadReg(regout,node_name+"/SearchStrHist");
		replace_str_hist.ReadReg(regout,node_name+"/ReplaceStrHist");
		bigsmall_flg=regout.ReadRegBool(node,"bigsmall_flg" ,bigsmall_flg);
		rex_flg     =regout.ReadRegBool(node,"rex_flg" ,rex_flg);
		updown_flg  =regout.ReadRegInt(node,"updown_flg)" ,updown_flg);
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		search_str_hist.OutReg(regout,node_name+"/SearchStrHist");
		replace_str_hist.OutReg(regout,node_name+"/ReplaceStrHist");
		regout.OutRegBool(node,"bigsmall_flg" ,bigsmall_flg);
		regout.OutRegBool(node,"rex_flg" ,rex_flg);
		regout.OutRegInt(node,"updown_flg)" ,updown_flg);
	}
	public void SetSearchString(String n){
		search_str=n;
		search_str_hist.Add(n);
	}
	public void SetReplaceString(String n){
		replace_str=n;
		replace_str_hist.Add(n);
	}
}
