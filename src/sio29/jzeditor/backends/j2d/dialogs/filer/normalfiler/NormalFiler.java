/******************************************************************************
;	デフォルトファイラー
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.filer.normalfiler;

import java.io.File;
import java.awt.Frame;
import javax.swing.filechooser.FileNameExtensionFilter;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.ureg.*;

import sio29.jzeditor.backends.j2d.dialogs.filer.*;

public class NormalFiler implements FilerBase {
	private boolean init_flg=false;
	private DefFiler filer=null;
	public String getFilerName(){
		return "Normal Filer";
	}
	public void Init(FileNameExtensionFilter[] filter){
		if(init_flg)return;
		init_flg=true;
		filer=new DefFiler();
		filer.AddExtList(filter);
		filer.InitFileChooser();
		filer.SetCurrentDir(new File(".").getPath());
	}
	public UFile OnOpenFileChooseUFile(Frame parent,String current_dir,UFileCurrentDrives drive_currents,Callback callback){
		//現在のディレクトリをカレントにしてから
		filer.SetCurrentDir(current_dir);
		//OnOpen();
		UFile filename=filer.OnOpenFileChooseUFile(parent);
		if(filename==null)return null;
		return filename;
	}
	public UFile OnSaveFileChooseUFile(Frame parent,UFile now_filename){
		filer.SetFilename(now_filename);
		UFile filename=filer.OnSaveFileChooseUFile(parent);
		if(filename==null)return null;
		return filename;
	}
	public void SetRegNodeName(RegOut regout,String node_name){}
};
