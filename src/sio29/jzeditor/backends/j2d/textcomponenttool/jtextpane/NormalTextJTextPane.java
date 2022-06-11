/******************************************************************************
;	JTextPane版テキストエリア
******************************************************************************/
//package sio29.jzeditor.backends.j2d.normaltext.jtextpane;
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextpane;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.TabSet;
import javax.swing.text.IconView;
import javax.swing.text.ComponentView;
import javax.swing.text.StyleConstants;
import javax.swing.text.Position;
import javax.swing.text.BadLocationException;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public class NormalTextJTextPane extends JTextPane implements NormalTextFunc{
	public NormalTextJTextPane(){
	}
	NormalizeCaretPosFunc normalizecaretposfunc=null;
	public void setNormalizeCaretPosFunc(NormalizeCaretPosFunc func){
		normalizecaretposfunc=func;
	}
	public void setNormalAttr(TextAttrBase norm_attr){
		if(norm_attr==null)return;
		setEditorKit(new NormalTextEditorKit(norm_attr));
	}
	@Override
	public Dimension getPreferredSize(){
		Dimension size=super.getPreferredSize();
		JScrollPane scroll=TextComponentTool.getScrollPane(this);
		boolean text_bottom_flg=true;
		return TextComponentTool.getJTextComponentSize(this,scroll,size,text_bottom_flg);
	}
	//========================================================
	// 折り返しを行わない
	//========================================================
//※※※
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return TextComponentTool.NoWrapScrollableTracksViewportWidth(this);
	}
	//========================================================
	//※
	protected void processKeyEvent(KeyEvent e){
		super.processKeyEvent(e);
		//選択範囲の補正
//		NormalizeCaretPos();
		if(normalizecaretposfunc!=null)normalizecaretposfunc.normalizeCaretPos();
	}
}
