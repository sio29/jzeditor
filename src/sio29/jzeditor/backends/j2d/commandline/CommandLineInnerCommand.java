/******************************************************************************
;	�����R�}���h
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

public class CommandLineInnerCommand{
	//============================================
	//�����R�}���h
	//============================================
	//cls
	public static void ICmd_Cls(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		comline.ExecCls();
	}
	//exit
	public static void ICmd_Exit(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		comline.ExecExit();
	}
	//cd
	public static void ICmd_ChangeDir(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		comline.ExecChnageDir(m);
	}
	//c:
	public static void ICmd_ChangeDrive(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		comline.ExecChnageDrive(m);
	}
	//FD
	public static void ICmd_FD(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		comline.ExecFD();
	}
	//WZ
	public static void ICmd_WZ(Object parent,String m){
		if(!(parent instanceof CommandLine))return;
		CommandLine comline=(CommandLine)parent;
		String[] opt=null;
		if(m!=null){
			opt=new String[1];
			opt[0]=m;
		}
		comline.ExecWZ(opt);
	}
	//============================================
	//============================================
	//�����R�}���h�e�[�u��
	//�G�C���A�X
//	final static String class_name="sio29.jzeditor.backends.j2d.commandline.CommandLineInnerCommand";
	final static Class class_name=CommandLineInnerCommand.class;
	
	private static InnerCommand[] g_inner_cmd={
		//�h���C�u�R�}���h
		new InnerCommand(null   ,class_name,"ICmd_ChangeDrive"),
		//�����R�}���h
		new InnerCommand("cls"  ,class_name,"ICmd_Cls"),
		new InnerCommand("exit" ,class_name,"ICmd_Exit"),
		new InnerCommand("chdir",class_name,"ICmd_ChangeDir"),
		new InnerCommand("cd"   ,class_name,"ICmd_ChangeDir"),
		//�G�C���A�X(?)
		new InnerCommand("fd"   ,class_name,"ICmd_FD"),
		new InnerCommand("wz"   ,class_name,"ICmd_WZ"),
	};
	public static InnerCommand[] getInnerCommands(){
		return g_inner_cmd;
	}
	
}
