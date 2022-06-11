/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;

import java.lang.CharSequence;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.text.JTextComponent;

import sio29.ulib.umat.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public interface TextDrawDocumentParser{
	int getCaretPosition();
	int getMarkPosition();
	FontMetrics getFontMetrics();
	Rectangle modelToView(int pos) throws Exception ;
	void drawCaret(Graphics g);
	TextColProp getColorsProp();
	void readLock();
	void readUnlock();
	CharSequence getText(int pos,int len) throws Exception ;
	int getLength();
	int[] getLineBounds(int line);
	int getLineAtPoint(int y);
	Dimension getTextSize();
}
