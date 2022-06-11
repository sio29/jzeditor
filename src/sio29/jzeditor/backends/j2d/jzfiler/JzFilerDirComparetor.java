/******************************************************************************
;	�f�B���N�g����r
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.io.File;

import sio29.ulib.ufile.*;
import sio29.ulib.ureg.*;

public class JzFilerDirComparetor{
	//===========================
	//�t�H���_��r(���݁A���t�A�T�C�Y)
	public static class DirCompareOpt{
		public boolean exists;			//���݃`�F�b�N
		public boolean date;			//���t�`�F�b�N
		public boolean new_old;			//�V����/�Â�
		public boolean in_2sec;			//2�b�ȓ��̍�
		public boolean size;			//�T�C�Y�`�F�b�N
		public boolean big_small;		//������/�傫��
		public DirCompareOpt(){
			exists   =true;			//���݃`�F�b�N
			date     =true;			//���t�`�F�b�N
			new_old  =false;		//�V����/�Â�
			in_2sec  =false;		//2�b�ȓ��̍�
			size     =false;		//�T�C�Y�`�F�b�N
			big_small=false;		//������/�傫��
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
