/******************************************************************************
;	イメージビュワー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview.ext;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;


import sio29.ulib.ufile.*;

import sio29.jzeditor.backends.j2d.jzfiler.preview.*;


public class JzFilerPreviewCreaterImage implements JzFilerPreviewViwerCreater{
	public boolean isSupport(UFile filename){
		if(!filename.isFile())return false;
		String ext=filename.getFileExt();
		return isImageFileExt(ext);
	}
	/*
	public JzFilerPreviewViwer createViewer(){
		return new JzFilerPreviewViwer_Image();
	}
	*/
	//public Component createViewer(UFile filename){
	//public JzFilerPreviewViwerDispoeable createViewer(UFile filename){
	//	return new JzFilerPreviewViwer_Image(this,filename);
	public JzFilerPreviewViwerDispoeable createViewer(){
		return new JzFilerPreviewViwer_Image(this);
	}
	public JzFilerPreviewDataLoader createDataLoader(UFile filename){
		return new JzFilerPreviewDataLoader_Image(this,filename);
	}
	public void disposeData(UFile filename,Object data){
	}
	//拡張子が画像か?
	private static boolean isImageFileExt(String ext){
		if(ext==null)return false;
		if(ext.length()==0)return false;
		String[] exts=ImageIO.getReaderFileSuffixes();
		ext=ext.toLowerCase();
		for(int i=0;i<exts.length;i++){
			if(ext.equals(exts[i]))return true;
		}
		return false;
	}
}

class JzFilerPreviewDataLoader_Image implements JzFilerPreviewDataLoader{
	private static int IMAGE_MAX_SIZE=256;		//画像の表示サイズ
	private JzFilerPreviewViwerCreater creator;
	private UFile filename;
	private Image image;
	//
	JzFilerPreviewDataLoader_Image(JzFilerPreviewViwerCreater _creator,UFile _filename){
		creator=_creator;
		filename=_filename;
	}
	public boolean load(UFile _filename){
		filename=_filename;
		image=GetPreviewImageFile(filename);
		return (image!=null);
	}
	public Object getData(){
		return image;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void dispose(){
	}
	//画像ファイルの読み込み
	private static Image GetPreviewImageFile(UFile filename){
		int MAX_SIZE=IMAGE_MAX_SIZE;
		try{
			BufferedImage image=ImageIO.read(filename.getInputStream());
			Image image2=image;
			int width =image.getWidth();
			int height=image.getHeight();
			if(width>MAX_SIZE || height>MAX_SIZE){
				if(width>height){
					height=height*MAX_SIZE/width;
					width =MAX_SIZE;
				}else{
					width =width*MAX_SIZE/height;
					height=MAX_SIZE;
				}
				image2=image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
			}
			return image2;
		}catch(Exception ex){}
		return null;
	}
}


class JzFilerPreviewViwer_Image extends Container implements JzFilerPreviewViwerDispoeable{
	private static int IMAGE_MAX_SIZE=256;		//画像の表示サイズ
	private Image image;
	private UFile filename;
	private JzFilerPreviewViwerCreater creator;
	//
	public JzFilerPreviewViwer_Image(JzFilerPreviewViwerCreater _creator){
		super();
		creator=_creator;
		//
		//JLabel c=new JLabel(new ImageIcon(image));
		//add(c);
	}
	public void dispose(){
		//System.out.println("JzFilerPreviewViwer_Image::dispose");
	}
	public Component getComponent(){
		return this;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void setData(UFile _filename,Object data){
		filename=_filename;
		if(data instanceof Image){
			image=(Image)data;
		}
	}
	public void clearData(){
		filename=null;
	}
	public UFile getFilename(){
		return filename;
	}
}
