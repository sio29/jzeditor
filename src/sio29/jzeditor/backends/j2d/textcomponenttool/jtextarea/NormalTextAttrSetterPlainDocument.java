/******************************************************************************
;	アトリュビュート作成(PlainDocument)
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;


import java.awt.Color;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;



public class NormalTextAttrSetterPlainDocument extends TextAttrSetter{
	public final static String COLOR_PROP_NAME="AttrCol";
	//
	public NormalTextAttrSetterPlainDocument(Document doc,TextAttrBase _norm_attr){
		if(_norm_attr instanceof NormalTextAttr){
			NormalTextAttr norm_attr=(NormalTextAttr)_norm_attr;
			//コメント
			comment=new NormalTextAttrSetterFuncPlainDocument(doc,ConvAWT.CVEC_Color(norm_attr.comment_col));
			//文字列
			str    =new NormalTextAttrSetterFuncPlainDocument(doc,ConvAWT.CVEC_Color(norm_attr.str_col));
			//マクロ
			macro  =new NormalTextAttrSetterFuncPlainDocument(doc,ConvAWT.CVEC_Color(norm_attr.macro_col));
		}
	}
	static class TextColProp_Plain implements  TextColProp{
		int[] cols;
		TextColProp_Plain(int len){
			cols=new int[len];
			for(int i=0;i<cols.length;i++)cols[i]=0xffffffff;
		}
		public void setColor(int i,int col){
			if(cols==null || i<0 || i>=cols.length)return;
			cols[i]=col;
		}
		public int getColor(int i){
			if(cols==null || i<0 || i>=cols.length)return 0xffffffff;
			return cols[i];
		}
		void fill(){
			for(int i=0;i<cols.length;i++)cols[i]=0xffffffff;
		}
	};
	public static Object MakeAttr(Document doc,NormalTextAttr norm_attr){
		int len=doc.getLength();
		if(len==0)return null;
		try{
			String text=doc.getText(0,len);
			if(text==null)return null;
			
			TextColProp cols_prop=new TextColProp_Plain(len);
			doc.putProperty(COLOR_PROP_NAME,cols_prop);
			
			
			NormalTextAttrSetterPlainDocument setter=new NormalTextAttrSetterPlainDocument(doc,norm_attr);
			
			TextAttrMaker.makeAttr(text,setter);
		}catch(Exception ex){}
		return null;
	}
	//
	static class NormalTextAttrSetterFuncPlainDocument implements TextAttrSetterFunc{
		private Document doc;
		private Color attr;
		//
		public NormalTextAttrSetterFuncPlainDocument(Document doc,Color attr){
			this.doc=doc;
			this.attr=attr;
		}
		//指定範囲のアトリュビュート設定
		private static void SetCharacterAttributesSub(int offset, int length, Document doc,Color s){
			if(doc==null)return;
			if(!(doc instanceof PlainDocument))return;
			TextColProp cols_prop=(TextColProp)doc.getProperty(COLOR_PROP_NAME);
			for(int i=0;i<length;i++)cols_prop.setColor(i+offset,s.getRGB());
		}
		public void set(int off,int len){
			SetCharacterAttributesSub(off,len,doc,attr);
		}
	}
}

