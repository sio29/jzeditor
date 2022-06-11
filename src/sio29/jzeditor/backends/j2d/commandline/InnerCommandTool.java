/******************************************************************************
;	�����R�}���h
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.lang.reflect.Method;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class InnerCommandTool{
	//�����R�}���h�̊l��
	public static InnerCommand GetInnerCommand(InnerCommand[] inner_cmd,String m){
		if(inner_cmd==null)return null;
		if(inner_cmd.length==0)return null;
		int src_len=m.length();
		//�����񂪃h���C�u�R�}���h��?
		String drive=UFileCurrentDrives.getDrivePathFromDriveChangeString(m);
		if(drive!=null){
			if(inner_cmd.length<1)return null;
			InnerCommand cmd_tbl=inner_cmd[0];
			if(cmd_tbl==null)return null;
			return new InnerCommand(m,cmd_tbl.class_p,cmd_tbl.proc_name,m);
		}
		//����ȊO�̃R�}���h�̃`�F�b�N
		//for(int i=0;i<inner_cmd.length;i++){
		for(int i=1;i<inner_cmd.length;i++){
			InnerCommand cmd_tbl=inner_cmd[i];
			if(cmd_tbl==null)break;
			String cmd=cmd_tbl.name;
			if(cmd==null)break;
			int len=cmd.length();
			if(src_len<len)continue;
			String new_m=m.substring(0,len);
			if(cmd.equalsIgnoreCase(new_m)){
				return cmd_tbl;
			}
		}
		return null;
	}
	//�����R�}���h�̎��s
//	public static boolean ExecInnerCommand(InnerCommand[] inner_cmd,String m,String opt,Object parent){
//		try{
//			if(m==null)return false;
//			if(m.length()==0)return false;
//			InnerCommand cmt_tbl=GetInnerCommand(inner_cmd,m);
//			if(cmt_tbl==null)return false;
//			return ExecInnerCommandSub(cmt_tbl,opt,parent);
//		}catch(Exception ex){
//			return false;
//		}
//	}
	//�����R�}���h�̎��s�T�u
	public static boolean ExecInnerCommandSub(InnerCommand cmd_tbl,String opt,Object parent){
		try{
			if(cmd_tbl==null)return false;
			String proc_name=cmd_tbl.proc_name;
			if(proc_name==null)return false;
			//�N���X�l��
			Object class_p=cmd_tbl.class_p;
			Class<?> c=null;	//���[�j���O���o���Ȃ�����
			if(class_p instanceof String){
			//class_p��������̏ꍇ
				String class_name=(String)class_p;
				c=Class.forName(class_name);
				class_p=null;
			}else if(class_p instanceof Class){
			//class_p��Class�̏ꍇ
				c=(Class)class_p;
				class_p=null;
			}else{
			//class_p���N���X�̃C���X�^���X�̏ꍇ
				c=class_p.getClass();
			}
			//�֐������烁�\�b�h�𓾂�
			Method method = c.getDeclaredMethod(proc_name, new Class[]{Object.class,String.class});
			method.setAccessible(true);
			//static�����o�[�̂Ƃ���P�p�����^��null
			Object obj=method.invoke(class_p,new Object[]{parent,opt});
			//�Ԃ�l
			return true;
		}catch(Exception e){
			System.out.println("ExecInnerCommand Error!!("+cmd_tbl.name+")");
			return false;
		}
	}
}
