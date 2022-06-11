/******************************************************************************
;	コマンドの実行
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.io.IOException;

public interface ShellExecCommandBase{
	public String ExecDosCommandWait(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener) throws IOException,InterruptedException ;
}
