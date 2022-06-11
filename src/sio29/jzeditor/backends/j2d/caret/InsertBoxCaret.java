/******************************************************************************
;	通常時のキャレット
******************************************************************************/
package sio29.jzeditor.backends.j2d.caret;

import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.Document;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Position;

public class InsertBoxCaret extends DefaultCaret{
	private JTextComponent textArea=null;		//親のテキスト
	private boolean isOvertypeMode=false;		//上書きモード
	private CaretFilter caret_filter=null;		//CaretFilter
	//
	public InsertBoxCaret(JTextComponent _textArea){
		textArea=_textArea;
		if(textArea!=null){
			if(textArea.getCaret()!=null){
				setBlinkRate(textArea.getCaret().getBlinkRate());
			}
//setBlinkRate(100000);
			textArea.setCaret(this);
		}
	}
	//※デバッグ用
	private boolean paintComponent_flg=false;
	public void setPaintComponentFlg(boolean n){
		paintComponent_flg=n;
	}
	@Override
	protected synchronized void damage(Rectangle r) {
if(paintComponent_flg)System.out.println("damage:"+r);
		if(r!=null) {
			JTextComponent c = getComponent();
			x = r.x;
			y = r.y;
			width=16;
			int pos = textArea.getCaretPosition();
			if(pos<textArea.getDocument().getLength()) {
				Document doc=textArea.getDocument();
				if(doc!=null){
					if(doc instanceof JTextPane){
						Element e=((DefaultStyledDocument)doc).getCharacterElement(pos);
						if(e!=null){
							AttributeSet as=e.getAttributes();
							if(as!=null){
								width=StyleConstants.getFontSize(as);
							}
						}
					}
				}
			}
			height = r.height;
			c.repaint();
		}
	}
	@Override
	public void paint(Graphics _g){
if(paintComponent_flg)System.out.println("Caret:paint:"+_g.getClipBounds());
		if(!isVisible())return;
		Graphics2D g = (Graphics2D)_g;
		final Color linecolor = new Color(250,250,220);
		Insets i = textArea.getInsets();
		int h = height;
		int xx=x;
		int yy=y;
		int width = g.getFontMetrics().charWidth('w');
		int width_base=width;
		boolean select_flg=false;
		//全角などに対応
		if(true) {
			int pos = textArea.getCaretPosition();
			if(pos<textArea.getDocument().getLength()) {
				if(textArea.getSelectedText()!=null) {
					//width = 0;
					select_flg=true;
				}else{
					try{
					String str = textArea.getText(pos, 1);
					width = g.getFontMetrics().stringWidth(str);
					}catch(Exception ex){}
				}
			}
		} //ここまで追加
		
		if(width<width_base)width=width_base;
		if(select_flg)width=2;
		int w=width;
		if(isOvertypeMode) {
			yy+=h/2;
			h=h/2;
		}
		g.setXORMode(Color.black);
		g.setPaint(linecolor);
		g.fillRect(xx, yy, w, h);
	}
	public boolean isOvertypeMode(){
		return isOvertypeMode;
	}
	public void setOvertypeMode(boolean n){
		isOvertypeMode=n;
	}
	public void flipOvertypeMode(){
		isOvertypeMode=!isOvertypeMode;
		repaint();
	}
	public void setCaretFilter(CaretFilter n){
		caret_filter=n;
	}
	//キャレット位置の計算
	private int convertCaretPos(int dot){
//return dot;
		if(caret_filter==null)return dot;
//		return caret_filter.convertCaretPos(this,dot);
		return caret_filter.convertCaretPos(dot);
	}
	@Override
	public void setDot(int dot){
if(paintComponent_flg)System.out.println("setDot:"+dot);
		dot=convertCaretPos(dot);
		super.setDot(dot);
	}
	@Override
	public void setDot(int dot, Position.Bias dotBias){
if(paintComponent_flg)System.out.println("setDot2:"+dot);
		dot=convertCaretPos(dot);
		super.setDot(dot,dotBias);
	}
	@Override
	public void moveDot(int dot){
if(paintComponent_flg)System.out.println("moveDot:"+dot);
		dot=convertCaretPos(dot);
		super.moveDot(dot);
	}
	@Override
	public void moveDot(int dot, Position.Bias dotBias){
if(paintComponent_flg)System.out.println("moveDot2:"+dot);
		dot=convertCaretPos(dot);
		super.moveDot(dot,dotBias);
	}
	@Override
	public void setMagicCaretPosition(Point p){
if(paintComponent_flg)System.out.println("setMagicCaretPosition:"+p);
		super.setMagicCaretPosition(p);
	}
	@Override
	public void setUpdatePolicy(int policy){
if(paintComponent_flg)System.out.println("setUpdatePolicy:"+policy);
		super.setUpdatePolicy(policy);
	}
	@Override
	protected void adjustVisibility(Rectangle nloc){
if(paintComponent_flg)System.out.println("adjustVisibility:"+nloc);
		super.adjustVisibility(nloc);
	}
	/*
	@Override
	protected void repaint(){
if(paintComponent_flg)System.out.println("repaint()");
		super.repaint();
	}
	*/
};
