/******************************************************************************
;	アトリュビュート作成(StyledDocument)
******************************************************************************/
//package sio29.jzeditor.backends.j2d.normaltext.jtextpane;
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextpane;

import java.lang.IndexOutOfBoundsException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;

import sio29.ulib.umat.*;
import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;


public class NormalTextAttrSetterStyledDocument extends TextAttrSetter{
	public NormalTextAttrSetterStyledDocument(Document doc,TextAttrBase norm_attr){
		String font_name=norm_attr.font_name;
		int font_size=norm_attr.font_size;
		CVECTOR back_col=norm_attr.back_col;
		MutableAttributeSet attr_normal =TextComponentTool.getColorAttr(font_name,font_size,back_col,norm_attr.normal_col);
		normal =new NormalTextAttrSetterFuncStyledDocument(doc,attr_normal,false);
		if(norm_attr instanceof NormalTextAttr){
			MutableAttributeSet attr_comment=TextComponentTool.getColorAttr(font_name,font_size,back_col,((NormalTextAttr)norm_attr).comment_col);
			MutableAttributeSet attr_str    =TextComponentTool.getColorAttr(font_name,font_size,back_col,((NormalTextAttr)norm_attr).str_col);
			MutableAttributeSet attr_macro  =TextComponentTool.getColorAttr(font_name,font_size,back_col,((NormalTextAttr)norm_attr).macro_col);
			comment=new NormalTextAttrSetterFuncStyledDocument(doc,attr_comment,true);
			str    =new NormalTextAttrSetterFuncStyledDocument(doc,attr_str,true);
			macro  =new NormalTextAttrSetterFuncStyledDocument(doc,attr_macro,true);
		}
	}
	public static Object MakeAttr(Document doc,NormalTextAttr norm_attr){
		int len=doc.getLength();
		if(len==0)return null;
		try{
			String text=doc.getText(0,len);
			if(text==null)return null;
			NormalTextAttrSetterStyledDocument setter=new NormalTextAttrSetterStyledDocument(doc,norm_attr);
			
			TextAttrMaker.makeAttr(text,setter);
		}catch(Exception ex){}
		return null;
	}
	//
	static class NormalTextAttrSetterFuncStyledDocument implements TextAttrSetterFunc{
		Document doc;
		MutableAttributeSet attr;
		boolean replace;
		//
		public NormalTextAttrSetterFuncStyledDocument(Document doc,MutableAttributeSet attr,boolean replace){
			this.doc=doc;
			this.attr=attr;
			this.replace=replace;
		}
		//指定範囲のアトリュビュート設定
		private static void SetCharacterAttributesSub(int offset, int length, Document doc,AttributeSet s, boolean replace){
			if(doc==null)return;
			if(!(doc instanceof DefaultStyledDocument))return;
			((DefaultStyledDocument)doc).setCharacterAttributes(offset,length,s,replace);
		}
		public void set(int off,int len){
			SetCharacterAttributesSub(off,len,doc,attr,replace);
		}
	}
}
