/******************************************************************************
;	ファイラーのベース
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.filer;

import java.awt.Frame;
import javax.swing.filechooser.FileNameExtensionFilter;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ureg.*;

public interface FilerBase{
	public interface Callback{
		public void selectFile(UFile filename);
	}
	public String getFilerName();
	public void Init(FileNameExtensionFilter[] filter);
	public UFile OnOpenFileChooseUFile(Frame parent,String current_dir,UFileCurrentDrives drive_currents,Callback callback);
	public UFile OnSaveFileChooseUFile(Frame parent,UFile now_filename);
	public void SetRegNodeName(RegOut regout,String node_name);
};
