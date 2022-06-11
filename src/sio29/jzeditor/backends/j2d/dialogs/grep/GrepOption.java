/******************************************************************************
;	Grepオプション
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.grep;


import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;

public class GrepOption{
	public UInputHistory str_hist=new UInputHistory();		//検索文字列のヒストリ
	public UInputHistory dir_hist=new UInputHistory();		//ディレクトリのヒストリ
	public boolean bigsmall_flg;							//大文字、小文字の判別
	public boolean subdir_flg;								//サブディレクトリも
	public boolean dirlist_flg;								//ディレクトリリストのみ
	//
	public String search_str="";							//検索文字列
	public Object search_opt;								//検索オプション
	public String dir_str="";								//検索フォルダ
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

