/******************************************************************************
;	Vzぽいファイラー
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.filer.jzinnerfiler;

import java.awt.Frame;
import javax.swing.filechooser.FileNameExtensionFilter;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.ureg.*;

import sio29.jzeditor.backends.j2d.dialogs.filer.*;
import sio29.jzeditor.backends.j2d.dialogs.filer.normalfiler.*;
import sio29.jzeditor.backends.j2d.jzfiler.*;


public class JzInnerFiler implements FilerBase{
	private NormalFiler normal_filer=new NormalFiler();
	private RegOut regout=null;
	private String g_node_name=null;
	
	public void Init(FileNameExtensionFilter[] filter){
		normal_filer.Init(filter);
	}
	public String getFilerName(){
		return "Vz Like Filer";
	}
	//読み込みファイルの選択
	public UFile OnOpenFileChooseUFile(Frame parent,String _current_dir,UFileCurrentDrives drive_currents,final Callback callback){
		String title="ファイルオープン";
		UFile current_dir=null;
		try{
			current_dir=UFileBuilder.createLocal(_current_dir);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(callback==null){
		//コールバックなし
//System.out.println("コールバックなし");
			JzFilerDialog dialog=new JzFilerDialog(parent,current_dir,drive_currents,title,regout,g_node_name);
			return dialog.SelectFile();
		}else{
		//コールバックあり
//System.out.println("コールバックあり");
			JzFilerDialog dialog=new JzFilerDialog(current_dir,drive_currents,title,regout,g_node_name);
			dialog.setCallback(new JzFilerDialog.Callback(){
				public void selectFile(UFile filename){
					if(filename==null)return;
					callback.selectFile(filename);
				}
			});
			dialog.setVisible(true);
			return null;
		}
	}
	//保存ファイルの選択
	public UFile OnSaveFileChooseUFile(Frame parent,UFile now_filename){
		return normal_filer.OnSaveFileChooseUFile(parent,now_filename);
	}
	public void SetRegNodeName(RegOut _regout,String _node_name){
		g_node_name=_node_name;
		regout=_regout;
	}
}
