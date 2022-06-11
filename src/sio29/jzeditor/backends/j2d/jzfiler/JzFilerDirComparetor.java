/******************************************************************************
;	ディレクトリ比較
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.io.File;

import sio29.ulib.ufile.*;
import sio29.ulib.ureg.*;

public class JzFilerDirComparetor{
	//===========================
	//フォルダ比較(存在、日付、サイズ)
	public static class DirCompareOpt{
		public boolean exists;			//存在チェック
		public boolean date;			//日付チェック
		public boolean new_old;			//新しい/古い
		public boolean in_2sec;			//2秒以内の差
		public boolean size;			//サイズチェック
		public boolean big_small;		//小さい/大きい
		public DirCompareOpt(){
			exists   =true;			//存在チェック
			date     =true;			//日付チェック
			new_old  =false;		//新しい/古い
			in_2sec  =false;		//2秒以内の差
			size     =false;		//サイズチェック
			big_small=false;		//小さい/大きい
		}
		public boolean compare_sub(UFile file){
			if(file==null)return false;
			if(file.equals(new File(".")))return false;
			if(file.equals(new File("..")))return false;
			return true;
		}
		public boolean compare(UFile file1,UFile file2){
			if(!compare_sub(file1))return false;
			if(!compare_sub(file2))return false;
			String name1=file1.getFileBody();
			String name2=file2.getFileBody();
			if(!name1.equals(name2))return false;
			return true;
		}
		public RegNode GetRegNode(RegOut regout,String node_name){
			RegNode node=regout.GetRegNode(node_name+"/CompareOpt");
			return node;
		}
		public void ReadReg(RegOut regout,String node_name){
			RegNode node=GetRegNode(regout,node_name);
			if(node==null)return;
			exists=regout.ReadRegBool(node,"exists",exists);
			date=regout.ReadRegBool(node,"date",date);
			new_old=regout.ReadRegBool(node,"new_old",new_old);
			in_2sec=regout.ReadRegBool(node,"in_2sec",in_2sec);
			size=regout.ReadRegBool(node,"size",size);
			big_small=regout.ReadRegBool(node,"big_small",big_small);
		}
		public void OutReg(RegOut regout,String node_name){
			RegNode node=GetRegNode(regout,node_name);
			if(node==null)return;
			regout.OutRegBool(node,"exists",exists);
			regout.OutRegBool(node,"date",date);
			regout.OutRegBool(node,"new_old",new_old);
			regout.OutRegBool(node,"in_2sec",in_2sec);
			regout.OutRegBool(node,"size",size);
			regout.OutRegBool(node,"big_small",big_small);
		}
	}
//	public static boolean[][] CompareDirList(Component parent,UFile[] dir1,UFile[] dir2,DirCompareOpt opt){
	public static boolean[][] CompareDirList(UFile[] dir1,UFile[] dir2,DirCompareOpt opt){
		boolean[][] ret=new boolean[2][];
		boolean[] ret1=new boolean[dir1.length];
		boolean[] ret2=new boolean[dir2.length];
		ret[0]=ret1;
		ret[1]=ret2;
		for(int i=0;i<ret1.length;i++)ret1[i]=false;
		for(int i=0;i<ret2.length;i++)ret2[i]=false;
		//
		for(int i=0;i<ret1.length;i++){
			UFile file1=dir1[i];
			for(int j=0;j<ret2.length;j++){
				if(ret2[j])continue;
				UFile file2=dir2[j];
				if(opt.compare(file1,file2)){
					ret1[i]=true;
					ret2[j]=true;
					break;
				}
			}
		}
		//
		/*
		for(int i=0;i<ret1.length;i++){
			System.out.println(String.format("0(%s) %s:%s",ret1[i],dir1[i],dir1[i].getName()));
		}
		for(int i=0;i<ret2.length;i++){
			System.out.println(String.format("1(%s) %s:%s",ret2[i],dir2[i],dir2[i].getName()));
		}
		*/
		return ret;
	}
}
