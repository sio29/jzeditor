/******************************************************************************
;	挿入コマンド
******************************************************************************/
package sio29.jzeditor.backends.j2d.command;

import sio29.ulib.ufile.*;
import sio29.ulib.ureg.*;

import sio29.jzeditor.backends.j2d.*;
import sio29.jzeditor.backends.j2d.dialogs.insert.*;

public class InsertCommand{
	private JzEditor jzeditor;
	private InsertOption insert_opt;
	//
	public InsertCommand(JzEditor _jzeditor){
		jzeditor=_jzeditor;
	}
	public void InitInsertOption(){
		insert_opt=new InsertOption();
		ReadRegInsertOption();
	}
	public InsertOption getInsertOption(){
		return insert_opt;
	}
	public String GetInsertOptionRegNodeName(){
		return jzeditor.GetRegRootName()+"/InsertOption";
	}
	public void ReadRegInsertOption(){
		RegOut regout=jzeditor.getRegOut();
		insert_opt.ReadReg(regout,GetInsertOptionRegNodeName());
	}
	public void OutRegInsertOption(){
		RegOut regout=jzeditor.getRegOut();
		insert_opt.OutReg(regout,GetInsertOptionRegNodeName());
	}
	//コピーした文字列の挿入
	public String InsertCopyStringDialog(){
System.out.println("InsertCopyStringDialog()");
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"コピーした文字列の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertSearchStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"検索した文字列の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertInputStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"入力した文字列の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertDeleteStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"削除した文字列の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertDateDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"日付の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertHorizonDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"水平線の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public UFile InsertFileDialog(){
		/*
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"ファイルの挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
		*/
		return null;
	}
	//
	public String InsertFilenameDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"ファイル名の挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String[] InsertTableDialog(){
		/*
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"テーブルの挿入",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
		*/
		return null;
	}
}
