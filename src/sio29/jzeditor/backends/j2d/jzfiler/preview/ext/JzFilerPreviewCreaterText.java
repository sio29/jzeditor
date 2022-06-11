/******************************************************************************
;	テキストビュワー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview.ext;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextArea;
import java.awt.*;
import javax.swing.*;

import sio29.ulib.ufile.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.jzfiler.preview.*;

//====================
//テキスト
public class JzFilerPreviewCreaterText implements JzFilerPreviewViwerCreater{
	public boolean isSupport(UFile filename){
		return isTextFile(filename);
	}
	public JzFilerPreviewViwerDispoeable createViewer(){
		return new JzFilerPreviewViwer_Text(this);
	}
	public JzFilerPreviewDataLoader createDataLoader(UFile filename){
		return new JzFilerPreviewDataLoader_Text(this,filename);
	}
	public void disposeData(UFile filename,Object data){
	}
	final static String[] ext_tbl={
		"txt",
		"log",
		"c",
		"cpp",
		"java",
		"bat",
		"htm",
		"html",
		"xml",
		"dm",
		"adoc",
		"js",
		"asm",
		"s",
		"sh",
	};
	private static boolean isTextFile(UFile filename){
		String ext=filename.getFileExt();
		if(ext==null)return false;
		ext=ext.toLowerCase();
		boolean r=false;
		for(int i=0;i<ext_tbl.length;i++){
			if(ext_tbl[i].equals(ext)){
				r=true;
				break;
			}
		}
//System.out.println(ext+":"+r);
		return r;
	}
	/*
	//テキストか?
	private static boolean isTextFile(UFile filename){
		if(filename==null)return false;
		try{
			CharCode charcode=TextData.CheckCharaCode(filename);
			if(charcode!=null)return true;
		}catch(Exception ex){
		}
		return false;
	}
	*/
}

class JzFilerPreviewDataLoader_Text implements JzFilerPreviewDataLoader{
	private static int preview_read_size=1024;	//プレビュー読み込みサイズ
	private JzFilerPreviewViwerCreater creator;
	private UFile filename;
	private String text;
	//
	JzFilerPreviewDataLoader_Text(JzFilerPreviewViwerCreater _creator,UFile _filename){
		creator=_creator;
		filename=_filename;
	}
	public boolean load(UFile _filename){
		filename=_filename;
		text=GetPreviewTextFromFile(filename);
		return (text!=null);
	}
	public Object getData(){
		return text;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void dispose(){
	}
	//テキストの読み込み
	private static String GetPreviewTextFromFile(UFile file){
		String m="";
		if(file==null)return m;
		if(!file.isFile())return m;
		TextData textdata=TextData.LoadText(file,preview_read_size);
		if(textdata!=null){
			m+="文字コード:"+textdata.getCharCode().getName()+"\n";
			String text=textdata.getNonBomTextString();
			m+=text;
		}
		return m;
	}
}


//class JzFilerPreviewViwer_Text extends Container implements JzFilerPreviewViwerDispoeable{
class JzFilerPreviewViwer_Text extends JPanel implements JzFilerPreviewViwerDispoeable{
	private static int preview_read_size=1024;	//プレビュー読み込みサイズ
	private UFile filename;
	private JTextArea prev_text;
	private JzFilerPreviewViwerCreater creator;
	//
	public JzFilerPreviewViwer_Text(JzFilerPreviewViwerCreater _creator){
		super();
		creator=_creator;
		//
		prev_text=new JTextArea();
		InitTextArea(prev_text,null);
		add(prev_text);
	}
	public void dispose(){
		//System.out.println("JzFilerPreviewViwer_Text::dispose");
	}
	public Component getComponent(){
		return this;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void setData(UFile _filename,Object data){
		filename=_filename;
		//
		prev_text.setText("");
		if(data instanceof String){
			prev_text.setText((String)data);
			prev_text.setCaretPosition(0);
		}
	}
	public void clearData(){
		filename=null;
	}
	public UFile getFilename(){
		return filename;
	}
	//テキストエリアの初期化
	private static void InitTextArea(JTextArea prev_text,String _text){
		prev_text.setEditable(false);
		prev_text.setForeground(Color.WHITE);
		prev_text.setBackground(Color.BLACK);
		if(_text!=null){
			prev_text.setText(_text);
		}
		prev_text.setCaretPosition(0);
	}
}

