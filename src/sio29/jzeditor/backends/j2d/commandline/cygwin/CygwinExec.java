/******************************************************************************
;	Cygwinの実行
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline.cygwin;

import java.io.IOException;

import sio29.ulib.ufile.*;

import sio29.jzeditor.backends.j2d.commandline.*;


public class CygwinExec implements ShellExecCommandBase{
	//============================================
	//Cygwinコマンド
	//============================================
	public static String[] MakeCygwinCommand(String[] cmds){
		String[] base_cmd={
			"C:\\wbin\\cygwin\\bin\\bash.exe",
			"--login",
//			"-x",
//			"-d",
//			"ukky"
		};
		int len=base_cmd.length;
		String[] new_cmds=new String[cmds.length+len];
		for(int i=0;i<base_cmd.length;i++){
			new_cmds[i]=base_cmd[i];
		}
		for(int i=0;i<cmds.length;i++){
			new_cmds[len+i]=cmds[i];
		}
		//cmds[2]="ls";
		return new_cmds;
	}
	public String ExecDosCommandWait(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener) throws IOException,InterruptedException {
		//cmds=CheckCmds(cmds,current_dir);
		
		//cmds=CygwinTest();
		cmds=MakeCygwinCommand(cmds);
		
		String char_code="UTF-8";
		return ShellExec.ExecDosCommandWait_Sub(cmds,current_dir,output_listener,input_listener,about_listener,char_code);
	}
	
}
