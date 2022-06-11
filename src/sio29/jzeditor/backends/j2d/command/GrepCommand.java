/******************************************************************************
;	Grepコマンド
******************************************************************************/
package sio29.jzeditor.backends.j2d.command;

import sio29.ulib.ufile.*;
import sio29.ulib.ureg.*;

import sio29.jzeditor.backends.j2d.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.dialogs.grep.*;

//Grepコマンド
public class GrepCommand{
	private JzEditor jzeditor;
	private GrepOption grep_opt=new GrepOption();
	//
	public GrepCommand(JzEditor _jzeditor){
		jzeditor=_jzeditor;
	}
	//Grep実行
	public void execGrep(){
		String[] value=openGrepDialog();
		String dir_str=grep_opt.dir_str;
		if(dir_str==null || dir_str.length()==0)return;
		boolean subdir_flg=grep_opt.subdir_flg;
		String search_str=grep_opt.search_str;
		Object search_opt=grep_opt.search_opt;
		//新規テキストWindowの作成
		final NormalText child=jzeditor.NewNormalText();
		JzGrepFunc func=new JzGrepFunc(){
			//ファイルの追加
			public void addFile(UFile filename){
				child.InsertText(String.format("%s\n",filename.getLocalFilename()));
			}
			//テキストの追加
			public void addString(UFile filename,int line,String m){
				child.InsertText(String.format("%s(%d) %s\n",filename.getLocalFilename(),line,m));
			}
		};
		//
		
		//Grep実行
		GrepTool.execGrep(func,search_str,search_opt,dir_str,subdir_flg);
	}
	public String[] openGrepDialog(){
		String current_dir=jzeditor.GetCurrentDir();
		JzWindow frame=jzeditor.getCurrentWindow();
		if(frame==null)return null;
		String[] value=GrepDialog.Open(frame,"Grep",grep_opt,current_dir);
		if(value==null)return null;
		if(value[0].length()==0 || value[1].length()==0)return null;
		return value;
	}
	public void initGrepOption(){
		grep_opt=new GrepOption();
		RegOut regout=jzeditor.getRegOut();
		readRegGrepOption(regout);
	}
	private String getGrepOptionRegNodeName(){
		return jzeditor.GetRegRootName()+"/GrepOption";
	}
	public void readRegGrepOption(RegOut regout){
		grep_opt.ReadReg(regout,getGrepOptionRegNodeName());
	}
	public void outRegGrepOption(RegOut regout){
		grep_opt.OutReg(regout,getGrepOptionRegNodeName());
	}
}

