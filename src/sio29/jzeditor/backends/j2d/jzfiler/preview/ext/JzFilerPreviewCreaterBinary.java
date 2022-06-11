/******************************************************************************
;	バイナリビュワー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview.ext;

import java.io.InputStream;
import java.awt.Component;
import java.awt.*;
import javax.swing.*;


import sio29.ulib.ufile.*;
import sio29.ulib.umat.*;

import sio29.jzeditor.backends.j2d.bineditor.*;
import sio29.jzeditor.backends.j2d.jzfiler.preview.*;


public class JzFilerPreviewCreaterBinary implements JzFilerPreviewViwerCreater{
	public boolean isSupport(UFile filename){
		if(!filename.isFile())return false;
		return true;
	}
	public JzFilerPreviewViwerDispoeable createViewer(){
		return new JzFilerPreviewViwer_Bin(this);
	}
	public JzFilerPreviewDataLoader createDataLoader(UFile filename){
		return new JzFilerPreviewDataLoader_Bin(this,filename);
	}
	public void disposeData(UFile filename,Object data){
	}
	
}

class JzFilerPreviewDataLoader_Bin implements JzFilerPreviewDataLoader{
	private static int preview_read_size=1024;	//プレビュー読み込みサイズ
	private JzFilerPreviewViwerCreater creator;
	private UFile filename;
	private byte[] data;
	//
	JzFilerPreviewDataLoader_Bin(JzFilerPreviewViwerCreater _creator,UFile _filename){
		creator=_creator;
		filename=_filename;
	}
	public boolean load(UFile _filename){
		filename=_filename;
		data=GetPreviewBinFromFile(filename);
		return (data!=null);
	}
	public Object getData(){
		return data;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void dispose(){
	}
	//テキストの読み込み
	public static byte[] GetPreviewBinFromFile(UFile filename){
		InputStream is=null;
		byte[] data=new byte[0];
		try{
			is=filename.getInputStream();
			data=new byte[preview_read_size];
			int size=is.read(data,0,preview_read_size);
			if(size==0){
				data=new byte[0];
			}else if(size!=preview_read_size){
				data=UArrays.copyOf(data,size);
			}
		}catch(Exception ex){
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception ex){
			}
		}
		return data;
	}
}


//class JzFilerPreviewViwer_Bin extends Container implements JzFilerPreviewViwerDispoeable{
class JzFilerPreviewViwer_Bin extends JPanel implements JzFilerPreviewViwerDispoeable{
	private UFile filename;
	private JzFilerPreviewViwerCreater creator;
	private BinEditor bineditor;
	//
	public JzFilerPreviewViwer_Bin(JzFilerPreviewViwerCreater _creator){
		super();
		creator=_creator;
		
		//Dimension size=getPreferredSize();
		bineditor=new BinEditor();
		add(bineditor);
		//bineditor.setPreferredSize(size);
		//bineditor.setSize(size);
	}
	public void dispose(){
		//System.out.println("JzFilerPreviewViwer_Bin::dispose");
	}
	public Component getComponent(){
		return this;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void setData(UFile _filename,Object data){
		filename=_filename;
		if(data instanceof byte[]){
			bineditor.SetDocument(_filename,(byte[])data);
		}
	}
	public void clearData(){
		filename=null;
	}
	public UFile getFilename(){
		return filename;
	}
}
