/******************************************************************************
;	検索コマンド
******************************************************************************/
package sio29.jzeditor.backends.j2d.command;

import sio29.ulib.ufile.*;
import sio29.ulib.ureg.*;

import sio29.jzeditor.backends.j2d.*;
import sio29.jzeditor.backends.j2d.dialogs.search.*;

//検索
public class SearchCommand{
	private JzEditor jzeditor;
	private SearchOption search_opt;
	//
	public SearchCommand(JzEditor _jzeditor){
		jzeditor=_jzeditor;
	}
	public void InitSearchOption(){
		search_opt=new SearchOption();
		ReadRegSearchOption();
	}
	public SearchOption getSearchOption(){
		return search_opt;
	}
	public String GetSearchOptionRegNodeName(){
		return jzeditor.GetRegRootName()+"/SearchOption";
	}
	public void ReadRegSearchOption(){
		RegOut regout=jzeditor.getRegOut();
		search_opt.ReadReg(regout,GetSearchOptionRegNodeName());
	}
	public void OutRegSearchOption(){
		RegOut regout=jzeditor.getRegOut();
		search_opt.OutReg(regout,GetSearchOptionRegNodeName());
	}
	//検索
	public String SearchDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=SearchDialog.Open(frame,"検索",search_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		//検索
		
		//
		return value;
	}
	//置換
	public String[] ReplaceDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String[] value=ReplaceDialog.Open(frame,"置換",search_opt);
		if(value==null)return null;
		if(value[0].length()==0 || value[1].length()==0)return null;
		//置換
		
		//
		return value;
	}
}

