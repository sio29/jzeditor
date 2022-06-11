/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;

import java.awt.Component;
import java.awt.im.InputContext;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.event.KeyEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.plaf.TextUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Element;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Caret;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.caret.*;


public class NormalTextJTextArea extends JTextArea implements NormalTextFunc{
	public NormalTextJTextArea(){
		TextComponentTool.replaceNoneF10(this);
		
		
		Component c=new javax.swing.JTextArea();
		add(c);
		c.enableInputMethods(true);
		InputContext ic=c.getInputContext();
System.out.println("InputContext="+ic);
		//
		addInputMethodListener(new InputMethodListener(){
			public void caretPositionChanged(InputMethodEvent event){
				System.out.println("caretPositionChanged:"+event);
			}
			public void inputMethodTextChanged(InputMethodEvent event){
				System.out.println("inputMethodTextChanged:"+event);
			}
		});
	}
	
	private NormalizeCaretPosFunc normalizecaretposfunc=null;
	public void setNormalizeCaretPosFunc(NormalizeCaretPosFunc _func){
		normalizecaretposfunc=_func;
	}
	private TextAttrBase norm_attr;
	public void setNormalAttr(TextAttrBase _norm_attr){
		norm_attr=_norm_attr;
	}
	@Override
	public Dimension getPreferredSize(){
		Dimension size=super.getPreferredSize();
		JScrollPane scroll=TextComponentTool.getScrollPane(this);
		boolean text_bottom_flg=true;
		return TextComponentTool.getJTextComponentSize(this,scroll,size,text_bottom_flg);
	}
	
	public void initNormalTextJTextAreaDrawer(){
		drawer=new NormalTextJTextAreaDrawer_Type2();
	}
	private NormalTextJTextAreaDrawer drawer=null;
	public void setNormalTextJTextAreaDrawer(NormalTextJTextAreaDrawer _drawer){
		drawer=_drawer;
	}
	
	@Override
	protected void paintComponent(Graphics _g) {
		super.paintComponent(_g);	//※これをやらないとキャレットが正しく移動しない
		if(drawer!=null)drawer.paintComponent(_g,new TextDrawDocumentParser_JTextComponent(this),norm_attr);
	}
	static class FontMetricsWrapper_ExGap extends FontMetricsWrapper{
		public int font_gap=0;
		FontMetricsWrapper_ExGap(FontMetrics fm,int gap){
			super(fm);
			font_gap=gap;
		}
		@Override
		public int getHeight() {
			return super.getHeight()+font_gap;
		}
	}
	//JTextArea用
	@Override
	public FontMetrics getFontMetrics(Font font) {
		FontMetrics fm=super.getFontMetrics(font);
		return new FontMetricsWrapper_ExGap(fm,0);
	}
	//========================================================
	// 折り返しを行わない
	//========================================================
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return TextComponentTool.NoWrapScrollableTracksViewportWidth(this);
	}
	//========================================================
	//※
	protected void processKeyEvent(KeyEvent e){
		super.processKeyEvent(e);
		if(normalizecaretposfunc!=null)normalizecaretposfunc.normalizeCaretPos();
	}
}


