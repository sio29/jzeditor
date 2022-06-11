/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.text.JTextComponent;

import javax.swing.plaf.TextUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Element;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Caret;


import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public class TextDrawDocumentParser_JTextComponent implements TextDrawDocumentParser{
	JTextComponent textArea;
	public TextDrawDocumentParser_JTextComponent(JTextComponent n){
		textArea=n;
	}
	public int getCaretPosition(){
		Caret caret=textArea.getCaret();
		return caret.getDot();
	}
	public int getMarkPosition(){
		Caret caret=textArea.getCaret();
		return caret.getMark();
	}
	public FontMetrics getFontMetrics(){
		Font font=textArea.getFont();
		return textArea.getFontMetrics(font);
	}
	public Rectangle modelToView(int pos) throws Exception {
		TextUI mapper = textArea.getUI();
		return mapper.modelToView(textArea,pos);
	}
	public void drawCaret(Graphics g){
		Caret caret=textArea.getCaret();
		caret.paint(g);
	}
	/*
	public Color[] getColors(){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		return (Color[])doc.getProperty(NormalTextAttrSetterPlainDocument.COLOR_PROP_NAME);
	}
	*/
	public TextColProp getColorsProp(){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		return (TextColProp)doc.getProperty(NormalTextAttrSetterPlainDocument.COLOR_PROP_NAME);
	}
	public void readLock(){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		doc.readLock();
	}
	public void readUnlock(){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		doc.readUnlock();
	}
	public String getText(int pos,int len) throws Exception {
		return textArea.getText(pos,len);
	}
	public int getLength(){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		return doc.getLength();
	}
	public int[] getLineBounds(int i){
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		Element root=doc.getDefaultRootElement();
		Element line=root.getElement(i);
		int start_pos=line.getStartOffset();
		int end_pos  =line.getEndOffset();
		return new int[]{start_pos,end_pos};
	}
	public int getLineAtPoint(int y){
		return TextComponentTool.getLineAtPointY(textArea,y);
	}
	//public IVECTOR2 getTextSize(){
	public Dimension getTextSize(){
		//return ConvAWT.Dimension_IVEC2(textArea.getPreferredSize());
		return textArea.getPreferredSize();
	}
}
