/******************************************************************************
;	挿入ダイアログオプション
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.insert;


import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class InsertOption{
	public UInputHistory copy_str_hist  =new UInputHistory();		//
	public UInputHistory input_str_hist =new UInputHistory();		//
	public UInputHistory delete_str_hist=new UInputHistory();		//
	String copy_str;
	String input_str;
	String delete_str;
	
	public InsertOption(){
		makeDummy();//ダミー
	}
	void makeDummy(){
		copy_str_hist.Add("テスト1");
		copy_str_hist.Add("テスト2");
		copy_str_hist.Add("テスト3");
		copy_str_hist.Add("テスト4");
		copy_str_hist.Add("テスト5");
		copy_str_hist.Add("テスト6");
		copy_str_hist.Add("テスト7");
		copy_str_hist.Add("テスト8");
	}
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		/*
		hist1.ReadReg(node_name+"/Hist1");
		hist2.ReadReg(node_name+"/Hist2");
		bigsmall_flg=regout.ReadRegBool(node,"bigsmall_flg" ,bigsmall_flg);
		rex_flg     =regout.ReadRegBool(node,"rex_flg" ,rex_flg);
		updown_flg  =regout.ReadRegInt(node,"updown_flg)" ,updown_flg);
		*/
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		/*
		hist1.OutReg(node_name+"/Hist1");
		hist2.OutReg(node_name+"/Hist2");
		regout.OutRegBool(node,"bigsmall_flg" ,bigsmall_flg);
		regout.OutRegBool(node,"rex_flg" ,rex_flg);
		regout.OutRegInt(node,"updown_flg)" ,updown_flg);
		*/
	}
	public void SetCopyString(String n){
		copy_str=n;
		copy_str_hist.Add(n);
	}
	public void SetInputString(String n){
		input_str=n;
		input_str_hist.Add(n);
	}
	public void SetDeleteString(String n){
		delete_str=n;
		delete_str_hist.Add(n);
	}
}
