/******************************************************************************
;	Dosコマンドの実行
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline.dos;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;

import sio29.ulib.ufile.*;

import sio29.jzeditor.backends.j2d.commandline.*;


public class DosExec implements ShellExecCommandBase{
	//============================================
	//	OSタイプを得るコマンドラインの表示
	//============================================
	public static String getOSName(){
		String os_name=System.getProperty("os.name");
		if(os_name!=null){
			os_name=os_name.toLowerCase();
			if(os_name.startsWith("windows")){
				return "windows";
			}else if(os_name.startsWith("linux")){
				return "linux";
			}else if(os_name.startsWith("mac")){
				return "mac";
			}else if(os_name.startsWith("sunos")){
				return "sunos";
			}
		}
		return "";
	}
	//============================================
	//文字列が内部コマンドを含むかチェック
	//============================================
	private static String[] dos_inner_cmd={
		"BREAK",
		"CALL",
		"CHCP",
		"CHDIR","CD",
		"CLS",
		"COPY",
		"CTTY",
		"DATE",
		"DEL","ERASE",
		"DIR",
		"ECHO",
		"EXIT",
		"FOR",
		"GOTO",
		"IF",
		"MKDIR","MD",
		"PATH",
		"PAUSE",
		"PROMPT",
		"REM",
		"RENAME","REN",
		"RMDIR","RD",
		"SET",
		"SHIFT",
		"TIME",
		"TYPE",
		"VER",
		"VERIFY",
		"VOL",
	};
	private static String[] GetInnerCmdList(){
		String os_name=getOSName();
		if(os_name.startsWith("windows")){
			return dos_inner_cmd;
		}else{
			return new String[]{};
		}
	}
	//============================================
	//内部コマンドの作成
	//============================================
	private static String[] MakeDosInnerCmds(String[] cmds){
		String os_name=getOSName();
		if(os_name.startsWith("windows")){
		//Windows
			String[] new_cmds=new String[cmds.length+2];
			new_cmds[0]="cmd.exe";
			new_cmds[1]="/c";
			for(int i=0;i<cmds.length;i++){
				new_cmds[i+2]=cmds[i];
			}
			return new_cmds;
		}else{
		//Windows以外
			return cmds;
		}
	}
	//============================================
	//有効拡張子リストを得る
	//============================================
	private static String[] getExcutableExtArray(){
		String os_name=getOSName();
		if(os_name.startsWith("windows")){
		//Windows
			String pathext = System.getenv("pathext");
			if(pathext==null){
			//pathextがない
				return new String[]{
					"exe",
					"com",
					"bat",
				};
			}else{
			//pathextがある
//System.out.println("pathext:"+pathext );
				//String[] pathexts =pathext.split(";");
				String[] pathexts =pathext.split(File.pathSeparator);
				ArrayList<String> exts=new ArrayList<String>();
				for(int i=0;i<pathexts.length;i++){
					String ext=pathexts[i];
					if(ext==null)continue;
					if(ext.length()==0)continue;
					if(ext.charAt(0)=='.'){
						ext=ext.substring(1);
					}
					if(ext==null)continue;
					if(ext.length()==0)continue;
					ext=ext.toLowerCase();
					exts.add(ext);
//					System.out.println("pathexts["+i+"]:"+ext);
				}
				return (String[])exts.toArray(new String[]{});
			}
		}else{
		//Windows以外
			return null;
		}
	}
	private static void printExtList(String[] extlist){
		if(extlist==null)return;
		for(int i=0;i<extlist.length;i++){
System.out.println(String.format("ext[%d]:%s",i,extlist[i]));
		}
	}
	/*
	//拡張子を得る
	private static String getFilenameExt(String filename){
		int index=filename.lastIndexOf(".");
		if(index<0)return null;
		return filename.substring(index+1);
	}
	//ファイルの拡張子が拡張子リストのいずれか
	private static boolean hasAnyFilenameExt(String filename,String[] extlist){
		String ext=getFilenameExt(filename);
		if(ext==null)return false;
		if(ext.length()==0)return false;
		ext=ext.toLowerCase();
//System.out.println(String.format("ext[%s]:%s",ext,filename));
		for(int i=0;i<extlist.length;i++){
			if(ext.equals(extlist[i]))return true;
		}
		return false;
	}
	*/
	//============================================
	//文字列からコマンド文字列への分解
	//※空白とダブル、シングルコーテーションで囲まれた文字列を分割をする
	//============================================
	private static String[] splitCommandString(String cmd){
		if(cmd==null)return null;
		if(cmd.length()==0)return null;
		String split_m=null;
		String add_m="";
		boolean flg=false;
		int index=0;
		if(cmd.startsWith("\"")){
		//ダブルコーテーションで始まる
			split_m="\"";
			index=1;
		}else if(cmd.startsWith("\'")){
		//シングルコーテーションで始まる
			split_m="\'";
			index=1;
		}else{
		//何もなしの場合
			//return cmd;
			split_m=" ";
			add_m=" ";
			index=0;
		}
		String[] ss=cmd.split(split_m);
		if(ss==null){
			return null;
		}
		if(ss.length<(index+1)){
			return null;
		}
		String new_cmd=ss[index];
		if(new_cmd==null){
			return null;
		}
		if(new_cmd.length()==0){
			return null;
		}
		String new_param="";
		for(int i=index+1;i<ss.length;i++){
			new_param+=(add_m+ss[i]);
		}
		String[] cmd2=new String[2];
		cmd2[0]=new_cmd;
		cmd2[1]=new_param;
		return cmd2;
	}
	//============================================
	//空白で分割
	//============================================
	private static String[] SliteCmdsBySpace(String[] cmds){
		ArrayList<String> buff=new ArrayList<String>();
		for(int i=0;i<cmds.length;i++){
			String m=cmds[i];
			String[] mlist=m.split(" ");
			for(int j=0;j<mlist.length;j++){
				buff.add(mlist[j]);
			}
		}
		cmds = buff.toArray(new String[]{});
		return cmds;
	}
	//============================================
	//実行可能パスリストを得る
	//============================================
	private static String[] getExecutablePathArray(String current_dir){
		ArrayList<String> pathlist=new ArrayList<String>();
		//カレント追加
		if(current_dir!=null){
			pathlist.add(current_dir);
		}
		//環境変数文字列の獲得
		String path=System.getProperty("java.library.path");
		//環境変数を配列に分配
//		String[] tbl=path.split(";");	//※linuxは":"
		String[] tbl=path.split(File.pathSeparator);	//※linuxは":"
		for(int i=0;i<tbl.length;i++){
			//すでに追加してあるのでカレントはスキップ
			if(tbl[i].equals("."))continue;
			pathlist.add(tbl[i]);
		}
		//マクロ置き換え「%～%」という名前を置き換える
		//※"$～"
		for(int i=0;i<pathlist.size();i++){
			String m0=pathlist.get(i);
			if(m0==null)continue;
			if(m0.length()==0)continue;
			String[] mm=m0.split("%");
			if(mm.length<2)continue;
			boolean ok_flg=true;
			for(int j=1;j<mm.length;j+=2){
				String c = System.getenv(mm[j]);
				if(c!=null){
					mm[j]=c;
				}else{
					mm[j]="%"+mm[j]+"%";
				}
			}
			String new_m="";
			for(int j=0;j<mm.length;j++){
				new_m+=mm[j];
			}
			pathlist.set(i,new_m);
		}
		/*
		//パスの最後に「\」追加
		for(int i=0;i<pathlist.size();i++){
			String m0=pathlist.get(i);
			if(m0==null)continue;
			if(m0.length()==0)continue;
			int len=m0.length();
			if(m0.charAt(len-1)!=File.separatorChar){
				m0+=File.separator;
				pathlist.set(i,m0);
			}
		}
		//空白削除
		for(int i=pathlist.size()-1;i>=0;i--){
			String m=pathlist.get(i);
			if(m==null){
				pathlist.remove(i);
			}else if(m.length()==0){
				pathlist.remove(i);
			}
		}
		//同じパスの削除
		for(int i=pathlist.size()-1;i>=0;i--){
			String m0=pathlist.get(i);
			for(int j=0;j<i-1;j++){
				String m1=pathlist.get(j);
				if(m0.compareToIgnoreCase(m1)==0){
					pathlist.remove(i);
					break;
				}
			}
		}
		//文字列リストに変換して返す
		return (String[])pathlist.toArray(new String[]{});
		*/
		//ArrayList<File> files=new ArrayList<File>();
		//HashSet<File> files_flg=new HashSet<File>();
		LinkedHashSet<File> files=new LinkedHashSet<File>();
		for(int i=0;i<pathlist.size();i++){
			String filename=pathlist.get(i);
			if(filename==null)continue;
			if(filename.length()==0)continue;
			try{
				File file=new File(filename).getCanonicalFile();
				if(!file.isDirectory())continue;
				//files_flg.add(file);
				files.add(file);
			}catch(Exception ex){}
		}
//System.out.println(String.format("files:(%d)->(%d)",pathlist.size(),files.size()));
		File[] ret_files=(File[])files.toArray(new File[]{});
		String[] ret_filenames=new String[ret_files.length];
		for(int i=0;i<ret_files.length;i++){
			ret_filenames[i]=ret_files[i].toString();
		}
		return ret_filenames;
	}
	//============================================
	//パスリストと拡張リストとコマンド名からパスを得る
	//cmd		コマンド
	//pathlist	パスリスト
	//extlist	拡張子リスト
	//============================================
	private static String checkAndGetExecutableFilename(String filename){
		try{
			//ファイルがある?
			File file=new File(filename);
			if(file.isFile() && file.canExecute()){
				return file.getAbsolutePath();
			}
		}catch(Exception ex){
		}
		return null;
	}
	//パス部分を得る
	private static String getFilenamePath(String filename){
		int index1=filename.lastIndexOf("/");
		int index2=filename.lastIndexOf("\\");
		int index3=filename.lastIndexOf(":");
		int index=index1;
		if(index2>index){
			index=index2;
		}
		if(index3>index){
			index=index3;
		}
		if(index<0)return null;
		return filename.substring(0,index+1);
	}
	//パス部分を得る
	private static String getFilenameExt(String filename){
		int index=filename.lastIndexOf(".");
		if(index<0)return null;
		return filename.substring(index);
	}
	
	private static String getExcutableFilename(String cmd,String current_dir){
//System.out.println("cmd:"+cmd);
		String[] pathlist=null;
		String cmd_path=getFilenamePath(cmd);
//System.out.println("cmd_path:"+cmd_path);
		if(cmd_path!=null){
			cmd=cmd.substring(cmd_path.length());
//System.out.println("cmd2:"+cmd);
			pathlist=new String[]{cmd_path};
		}else{
			pathlist=getExecutablePathArray(current_dir);
		}
		String[] extlist=getExcutableExtArray();
		if(extlist!=null){
		//Windowsの場合のみ
			String ext=getFilenameExt(cmd);
			if(ext!=null){
				cmd=cmd.substring(0,cmd.length()-ext.length());
//System.out.println("cmd3:"+cmd);
				extlist=new String[]{ext.substring(1)};
			}
		}
//System.out.println("extlist:"+((extlist!=null)?extlist.length:"null"));
//if(extlist!=null){
//for(int i=0;i<extlist.length;i++){
//System.out.println("ext["+i+"]:"+extlist[i]);
//}
//}
		//String[] pathlist=getExecutablePathArray(current_dir);
//	private static String getExcutableFilename(String cmd,String[] pathlist,final String[] extlist){
//System.out.println("cmd:"+cmd);
//printExtList(extlist);

//		if(extlist==null)return null;
		//拡張子ごとにチェック
//		for(int i=0;i<extlist.length;i++){
//			String ext=extlist[i];
			//パスごとにチェック
			for(int j=0;j<pathlist.length;j++){
				String path=pathlist[j];
				/*
				String filename=path+cmd+"."+ext;
				try{
					//ファイルがある?
					File file=new File(filename);
//					if(file.isFile()){
					if(file.isFile() && file.canExecute()){
						return file.getAbsolutePath();
					}
				}catch(Exception ex){
				}
				*/
				if(extlist==null){
//System.out.println("111*");
					String filename=path+cmd;
					String exec_filename=checkAndGetExecutableFilename(filename);
					if(exec_filename!=null){
//System.out.println("111:"+exec_filename);
						return exec_filename;
					}
				}else{
//System.out.println("222*");
					for(int k=0;k<extlist.length;k++){
						String _ext=extlist[k];
						String filename=path+File.separator+cmd+"."+_ext;
						
						String exec_filename=checkAndGetExecutableFilename(filename);
//System.out.println("DosExec.getExcutableFilename:["+j+"]["+k+"]"+filename+"->"+exec_filename);
						if(exec_filename!=null){
//System.out.println("Find !!:"+exec_filename);
							return exec_filename;
						}
					}
				}
				
				
				/*
				try{
					File[] files=new File(path).listFiles(new FilenameFilter(){
						public boolean accept(File dir,String name){
							File file=new File(dir,name);
							if(!file.isFile())return false;
							if(!file.canExecute())return false;
							if(extlist!=null){
								if(!hasAnyFilenameExt(name,extlist))return false;
							}
							//return false;
							return true;
						}
					});
					if(files!=null){
for(int k=0;k<files.length;k++){
System.out.println(String.format("file[%d][%d]:%s",j,k,files[k].toString()));
}
					}
				}catch(Exception ex){
					System.out.println(""+ex);
				}
				*/
			}
//		}
		return null;
	}
	//============================================
	//シェルコマンド実行
	//============================================
//	private static boolean CheckShellScript(String[] cmds,String current_dir,String[] extlist){
	private static boolean CheckShellScript(String[] cmds,String current_dir){
		if(cmds==null)return false;
		if(cmds.length==0)return false;
		//文字列からファイル名を得る
		String cmd=cmds[0];
		//文字列からコマンド文字列への分解
		String[] new_cmds=splitCommandString(cmd);
		if(new_cmds==null)return false;
		if(new_cmds.length<2)return false;
		//実行可能パスリストを得る
//		String[] pathlist=getExecutablePathArray(current_dir);
/*
Arrays.sort(pathlist,new Comparator<String>(){
public int compare(String o1, String o2){
	return o1.compareTo(o2);
}
});
*/
//System.out.println("---- 01");
		//パスリストと拡張リストとコマンド名からパスを得る
//		String filename=getExcutableFilename(new_cmds[0],pathlist,extlist);
		String filename=getExcutableFilename(new_cmds[0],current_dir);
//System.out.println("---- 02");
		if(filename==null)return false;
		String ret_cmd=filename+new_cmds[1];
		cmds[0]=ret_cmd;
		return true;
	}
	//============================================
	//cmdsのチェック
	//============================================
//	static interface Callback{
//		public String[] GetInnerCmdList();				//内部コマンドかのチェック
//		public String[] getExcutableExtArray();				//実行可能拡張子の獲得
//		public String[] MakeInnerCmds(String[] cmds);	//内部コマンドの作成
//	};
	private static String[] CheckCmds(String[] cmds,String current_dir){
		if(cmds==null)return null;
		if(cmds.length==0)return null;
		//内部コマンドかのチェック
		String[] inner_cmd=GetInnerCmdList();
		if(ShellExec.CheckInnerCmd(inner_cmd,cmds[0])!=null){
			//内部コマンドの場合
			cmds=MakeDosInnerCmds(cmds);
		}else{
			//実行可能拡張子
//			String[] extlist=getExcutableExtArray();
			//シェルスクリプトかのチェック
//			if(CheckShellScript(cmds,current_dir,extlist)){
			if(CheckShellScript(cmds,current_dir)){
			}
		}
		//空白で分割
		return SliteCmdsBySpace(cmds);
	}
	//============================================
	//Sygwinコマンド
	//============================================
	public static String[] CygwinTest(){
		String[] cmds=new String[3];
		cmds[0]="C:\\wbin\\cygwin\\bin\\bash.exe";
		cmds[1]="--login";
		cmds[2]="ls";
		return cmds;
	}
	
	//============================================
	//シェル実行
	//============================================
	//ウェイトあり
	public String ExecDosCommandWait(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener) throws IOException,InterruptedException {
		String os_name=getOSName();
		if(os_name.startsWith("windows")){
			cmds=CheckCmds(cmds,current_dir);
		}
//		return ShellExec.ExecDosCommandWait_Sub(cmds,current_dir,output_listener,input_listener,about_listener);
		return ShellExec.ExecDosCommandWait_Sub(cmds,current_dir,output_listener,input_listener,about_listener,"ShiftJIS");
	}
}

