/******************************************************************************
;	ファイル名と行番号
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class FilenameLine{
	private UFile filename;		//ファイル名
	private int line;				//行番号
	//
//	public FilenameLine(){}
	public FilenameLine(UFile _filename){
		filename=_filename;
		line=0;
	}
	public FilenameLine(UFile _filename,int _line){
		filename=_filename;
		line=_line;
	}
	public String toString(){
		return filename+" ("+line+")";
	}
	public UFile getFilename(){
		return filename;
	}
	public int getLine(){
		return line;
	}
	//==============================================
	//ファイル名と行番号を獲得する
	//==============================================
	public static FilenameLine GetFilenameLineFromStr(String m,String current_dir){
		FilenameLine fnl=GetFilenameLineFromStr(m);
		if(fnl==null)return null;
		if(current_dir!=null){
			fnl.filename=UFileBuilderURL.getAbsoluteUFile(current_dir,fnl.filename);
		}
		return fnl;
	}
//	***
	//タグジャンプ用文字列からファイル名と行番号を得る
	private static FilenameLine GetFilenameLineFromStr(String m){
		if(m==null)return null;
		int len=m.length();
		if(len==0)return null;
		//「.」のチェック
		int dot_index=m.indexOf(".".charAt(0));
		if(dot_index<0){
			System.out.println("dotがない");
			return null;
		}
//		System.out.println("dot_index("+dot_index+")");
		//行番号の有無のチェック「(),[],{},:」
		final char[] separator_char={'(',':','[','{'};
		int linenum_index=StringIndexOfFromCharList(m,dot_index,separator_char);
		if(linenum_index<0){
			System.out.println("セパレータがない");
			return null;
		}
		//セパレータより前の文字をファイル名とする
		String filename=m.substring(0,linenum_index);
		if((linenum_index+1)>=len)return null;
		//後ろの空白を削る
		filename=TextTool.GetStringRemoveBottomSpace(filename);
		filename=GetStringRemoveAntHeader(filename);
		filename=TextTool.GetStringRemovePrevSpace(filename);
		//行番号を得る
		int line=0;
		try{
			String line_m=m.substring(linenum_index+1);
			//line=Integer.getInteger(line_m);
			
//System.out.println("line_m:[[["+line_m+"]]]");
			String[] mm=line_m.split("[^0-9]");
			if(mm.length==0)return null;
			boolean set_flg=false;
			for(int i=0;i<mm.length;i++){
				if(mm[i]==null)continue;
				if(mm[i].length()==0)continue;
//System.out.println("mm["+i+"]:("+mm[i]+")");
				line=Integer.parseInt(mm[i]);
				set_flg=true;
				break;
			}
			if(!set_flg)return null;
//			Integer line=new Integer(m.substring(linenum_index+1));
//System.out.println("filename:("+filename+"):line("+line+")");
		}catch(Exception ex){
			System.out.println("Error");
			return null;
		}
		//テキストと行
		System.out.println("filename:("+filename+"):line("+line+")");
		//テキストと行
		try{
			FilenameLine fnl=new FilenameLine(UFileBuilder.createLocal(filename),line);
			return fnl;
		}catch(Exception ex){
			return null;
		}
	}
	private static int StringIndexOfFromCharList(String m,int start_index,char[] charlist){
		boolean flg=false;
		int index=m.length();
		for(int i=0;i<charlist.length;i++){
			int new_index=m.indexOf(charlist[i],start_index);
			if(new_index<0)continue;
			if(new_index<index){
				index=new_index;
				flg=true;
			}
		}
		if(!flg)return -1;
		return index;
	}
	//==============================================
	//文字列からANTのヘッダー文字([～～～])を省く
	//============================================
	private static String GetStringRemoveAntHeader(String m){
		if(m==null)return m;
		int len=m.length();
		if(len==0)return m;
		int i0=m.indexOf('[');
		if(i0<0)return m;
		int i1=m.indexOf(']',i0+1);
		if(i1<0)return m;
		String m0=m.substring(0,i0);
		String m1=m.substring(i1+1);
//System.out.println("m0「"+m0+"」");
//System.out.println("m1「"+m1+"」");
		return m0+m1;
	}
}
