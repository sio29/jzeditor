/******************************************************************************
;	Grepオプション
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.grep;

import java.util.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

import sio29.jzeditor.backends.j2d.jzfiler.*;
import sio29.jzeditor.backends.j2d.texttool.*;

public class GrepTool{
	//func grep用関数
	//dir_str ディレクトリ
	//subdir_flg サブディレクトリも参照するか?
	public static void execGrep(JzGrepFunc func,String search_str,Object search_opt,String dir_str,boolean subdir_flg){
//		if(value==null)return;
		UFile search_dir=null;
		try{
			search_dir=UFileBuilder.createLocal(dir_str);
		}catch(Exception ex){
			ex.printStackTrace();
		}
//		if(!search_dir.exists()){
//System.out.println(""+search_dir+"は存在しません");
//			return;
//		}
		/*
		if(!search_dir.isDirectory()){
System.out.println(""+search_dir+"はフォルダできありません");
			return;
		}
		*/
		UFilenameFilter filter=null;
		String name=null;
		if(!search_dir.exists()){
//System.out.println("*** 01");
			if(!search_dir.isDirectory()){
//System.out.println("*** 1");
				//name=search_dir.getName();
				name=search_dir.getFileBody();
				search_dir=search_dir.getParentFile();
			}
		}else{
//System.out.println("*** 02");
			if(!search_dir.isDirectory()){
//System.out.println("*** 2");
				//name=search_dir.getName();
				name=search_dir.getFileBody();
				search_dir=search_dir.getParentFile();
			}
		}
//System.out.println("name:"+name);
//System.out.println("search_dir:"+search_dir);
		if(name!=null){
			filter=JzFilerMaskFileFilter.createMaskFileFilter(name,true);
			
		}
		execGrepSub(func,search_str,search_opt,search_dir,subdir_flg,filter);
	}
	//
	private static void execGrepSub(JzGrepFunc child,String search_str,Object search_opt,UFile search_dir,boolean sub_dir,UFilenameFilter filter){
		if(!search_dir.exists()){
			System.out.println(""+search_dir+"は存在しません");
			return;
		}
		if(!search_dir.isDirectory()){
			System.out.println(""+search_dir+"はフォルダではありません");
			return;
		}
		UFile[] files=null;
		if(filter==null){
			files=search_dir.listFiles();
		}else{
			files=search_dir.listFiles(filter);
		}
		for(int i=0;i<files.length;i++){
			UFile file=files[i];
			if(!file.exists())continue;
			if(!file.isFile())continue;
			execGrepSub2(child,file,search_str,search_opt);
		}
		if(sub_dir){
			for(int i=0;i<files.length;i++){
				UFile file=files[i];
				if(!file.exists())continue;
				if(!file.isDirectory())continue;
				execGrepSub(child,search_str,search_opt,file,true,filter);
			}
		}
	}
	//
	private static void execGrepSub2(JzGrepFunc child,UFile file,String search_str,Object search_opt){
//System.out.println("execGrepSub2:"+file);
		//ファイル名追加
//		child.addFile(file);
		//
		TextData textdata=TextData.LoadText(file);
		if(textdata!=null){
			ArrayList<TextSearchRet> ret=textdata.searchText(search_str,search_opt);
			if(ret.size()>0){
				for(int i=0;i<ret.size();i++){
					TextSearchRet r=ret.get(i);
					child.addString(file,r.line,textdata.getLine(r.line-1));
				}
			}
		}
	}
}
