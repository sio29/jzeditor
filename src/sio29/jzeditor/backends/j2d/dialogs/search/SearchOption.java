/******************************************************************************
;	検索、置換オプション
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.search;


import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class SearchOption{
	public UInputHistory search_str_hist =new UInputHistory();		//検索文字列のヒストリ
	public UInputHistory replace_str_hist=new UInputHistory();	//置換文字列のヒストリ
	public String search_str=null;								//検索文字列
	public String replace_str=null;								//置換文字列
	public boolean bigsmall_flg;								//大文字、小文字の判別
	public boolean rex_flg;										//正規表現
	public int updown_flg;										//検索方向(0:下,1:上)
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
