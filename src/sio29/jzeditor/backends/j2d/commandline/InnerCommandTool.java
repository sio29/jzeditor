/******************************************************************************
;	内部コマンド
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.lang.reflect.Method;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class InnerCommandTool{
	//内部コマンドの獲得
	public static InnerCommand GetInnerCommand(InnerCommand[] inner_cmd,String m){
		if(inner_cmd==null)return null;
		if(inner_cmd.length==0)return null;
		int src_len=m.length();
		//文字列がドライブコマンドか?
		String drive=UFileCurrentDrives.getDrivePathFromDriveChangeString(m);
		if(drive!=null){
			if(inner_cmd.length<1)return null;
			InnerCommand cmd_tbl=inner_cmd[0];
			if(cmd_tbl==null)return null;
			return new InnerCommand(m,cmd_tbl.class_p,cmd_tbl.proc_name,m);
		}
		//それ以外のコマンドのチェック
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
	//内部コマンドの実行
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
	//内部コマンドの実行サブ
	public static boolean ExecInnerCommandSub(InnerCommand cmd_tbl,String opt,Object parent){
		try{
			if(cmd_tbl==null)return false;
			String proc_name=cmd_tbl.proc_name;
			if(proc_name==null)return false;
			//クラス獲得
			Object class_p=cmd_tbl.class_p;
			Class<?> c=null;	//ワーニングを出さないため
			if(class_p instanceof String){
			//class_pが文字列の場合
				String class_name=(String)class_p;
				c=Class.forName(class_name);
				class_p=null;
			}else if(class_p instanceof Class){
			//class_pがClassの場合
				c=(Class)class_p;
				class_p=null;
			}else{
			//class_pがクラスのインスタンスの場合
				c=class_p.getClass();
			}
			//関数名からメソッドを得る
			Method method = c.getDeclaredMethod(proc_name, new Class[]{Object.class,String.class});
			method.setAccessible(true);
			//staticメンバーのとき第１パラメタはnull
			Object obj=method.invoke(class_p,new Object[]{parent,opt});
			//返り値
			return true;
		}catch(Exception e){
			System.out.println("ExecInnerCommand Error!!("+cmd_tbl.name+")");
			return false;
		}
	}
}
