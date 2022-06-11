/******************************************************************************
;	Vzファイラー、ファイル比較用
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.Comparator;

import sio29.ulib.ufile.*;

public class JzFilerFileComparator implements Comparator<Object>{
	private int sort_type;
	private int sort_dir;
	public JzFilerFileComparator(int _sort_type,int _sort_dir){
		sort_type=_sort_type;
		sort_dir =_sort_dir;
	}
	public int compare(Object o1, Object o2){
		if(o1==null)return 0;
		if(o2==null)return 0;
		if(!(o1 instanceof UFile))return 0;
		if(!(o2 instanceof UFile))return 0;
		UFile file1=(UFile)o1;
		UFile file2=(UFile)o2;
		boolean d1=file1.isDirectory();
		boolean d2=file2.isDirectory();
		if(d1!=d2){
			if(d1){
				return -1;
			}else{
				return 1;
			}
		}else{
			if(d1){
			//ディレクトリ
				/*
				String name1=file1.getFileBody();
				String name2=file2.getFileBody();
				boolean n1=name1.equals("..");
				boolean n2=name2.equals("..");
				if(n1 && n2)return 0;
				if(n1){
					if(n2)return 0;
					return -1;
				}else if(n2){
					if(n1)return 0;
					return 1;
				}
				int r=0;
				r=name1.compareToIgnoreCase(name2);
				return r;
				*/
				String path1=""+file1.getFilePath();
				String path2=""+file2.getFilePath();
				int r=0;
				r=path1.compareToIgnoreCase(path2);
				if(r!=0)return r;
				String name1=file1.getFileBody();
				String name2=file2.getFileBody();
				r=name1.compareToIgnoreCase(name2);
				return r;
			}else{
			//ファイル
				int r=0;
				switch(sort_type){
					case 0:
					//ファイル名
						{
						String name1=file1.getFileBody();
						String name2=file2.getFileBody();
						r=name1.compareToIgnoreCase(name2);
						}
						break;
					case 1:
					//拡張子
						{
						String ext1=file1.getFileExt();
						String ext2=file2.getFileExt();
						r=ext1.compareToIgnoreCase(ext2);
						if(r==0){
							String name1=file1.getFileBody();
							String name2=file2.getFileBody();
							r=name1.compareToIgnoreCase(name2);
						}
						}
						break;
					case 2:
					//日付
						{
						long date1=file1.lastModified();
						long date2=file2.lastModified();
						r=(int)(date1-date2);
						if(r==0){
							String name1=file1.getFileBody();
							String name2=file2.getFileBody();
							r=name1.compareToIgnoreCase(name2);
						}
						}
						break;
					case 3:
					//サイズ
						{
						long size1=file1.getSize();
						long size2=file2.getSize();
						r=(int)(size1-size2);
						if(r==0){
							String name1=file1.getFileBody();
							String name2=file2.getFileBody();
							r=name1.compareToIgnoreCase(name2);
						}
						}
						break;
				}
				if(sort_dir!=0)r=-r;
				return r;
			}
		}
	}
}
