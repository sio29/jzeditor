/******************************************************************************
;	�}���R�}���h
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
	//�R�s�[����������̑}��
	public String InsertCopyStringDialog(){
System.out.println("InsertCopyStringDialog()");
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"�R�s�[����������̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertSearchStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"��������������̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertInputStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"���͂���������̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertDeleteStringDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"�폜����������̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertDateDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"���t�̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String InsertHorizonDialog(){
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"�������̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public UFile InsertFileDialog(){
		/*
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"�t�@�C���̑}��",insert_opt);
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
		String value=InsertDialog.OpenInsertCopyString(frame,"�t�@�C�����̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
	}
	//
	public String[] InsertTableDialog(){
		/*
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String value=InsertDialog.OpenInsertCopyString(frame,"�e�[�u���̑}��",insert_opt);
		if(value==null)return null;
		if(value.length()==0)return null;
		return value;
		*/
		return null;
	}
}
