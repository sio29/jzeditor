/******************************************************************************
;	エディターキット
******************************************************************************/
//package sio29.jzeditor.backends.j2d.normaltext.jtextpane;
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextpane;

import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import javax.swing.JEditorPane;
import javax.swing.text.ViewFactory;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ParagraphView;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.TabStop;
import javax.swing.text.TabSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.IconView;
import javax.swing.text.ComponentView;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;

//タブと全角空白の表示
public class NormalTextEditorKit extends StyledEditorKit {
	private final SimpleAttributeSet attrs = new SimpleAttributeSet();
//	private NormalTextAttr norm_attr;
	private TextAttrBase norm_attr;
	//
//	public NormalTextEditorKit(NormalTextAttr _norm_attr){
	public NormalTextEditorKit(TextAttrBase _norm_attr){
		//setting=_setting;
		norm_attr=_norm_attr;
	}
	public void install(JEditorPane c) {
		int tab_size=4;
		FontMetrics fm = c.getFontMetrics(c.getFont());
		int tabLength = fm.charWidth('m') * (tab_size-1);
		TabStop[] tabs = new TabStop[100];
		for(int j=0;j<tabs.length;j++) {
			tabs[j] = new TabStop((j+1)*tabLength);
		}
		TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(attrs, tabSet);
		super.install(c);
	}
	public ViewFactory getViewFactory() {
		return new MyViewFactory2(norm_attr);
	}
	public Document createDefaultDocument() {
		Document d = super.createDefaultDocument();
		if(d instanceof StyledDocument) {
			((StyledDocument)d).setParagraphAttributes(0, d.getLength(), attrs, false);
		}
		return d;
	}
	//View
	static class MyViewFactory2 implements ViewFactory {
		//private NormalTextAttr norm_attr;
		private TextAttrBase norm_attr;
		//
		//public MyViewFactory2(NormalTextAttr _norm_attr){
		public MyViewFactory2(TextAttrBase _norm_attr){
			norm_attr=_norm_attr;
		}
		public View create(Element elem) {
			String kind = elem.getName();
			if(kind!=null) {
				if(kind.equals(AbstractDocument.ContentElementName)) {
					//全角空白、半角空白、タブの表示
					return new WhitespaceLabelView(elem,norm_attr);
				}else if(kind.equals(AbstractDocument.ParagraphElementName)) {
					//改行表示
					return new MyParagraphView(elem,norm_attr);
				}else if(kind.equals(AbstractDocument.SectionElementName)) {
					//EOF表示
					return new EditOriginalBoxView(elem, View.Y_AXIS,norm_attr);
				}else if(kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				}else if(kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			//全角空白、半角空白、タブの表示
			return new WhitespaceLabelView(elem,norm_attr);
		}
	}
	//全角空白、半角空白、タブの表示
	static class WhitespaceLabelView extends LabelView {
		private static final String IdeographicSpace = "\u3000";
		private static final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {1.0f}, 0.0f);
		//private NormalTextAttr norm_attr;
		private TextAttrBase norm_attr;
		//
		//public WhitespaceLabelView(Element elem,NormalTextAttr _norm_attr) {
		public WhitespaceLabelView(Element elem,TextAttrBase _norm_attr) {
			super(elem);
			norm_attr=_norm_attr;
		}
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			paintOriginalParagraph(g, a);
		}
		private void paintOriginalParagraph(Graphics g, Shape a) {
			Rectangle rec = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
			final int X = rec.x;
			final int Y = rec.y;
			final int HEIGHT = rec.height;
			final int WIDTH = HEIGHT/2;
			final Color defaultColor = g.getColor();
			
			String text = getText(getStartOffset(), getEndOffset()).toString();
			FontMetrics fontMetrics = g.getFontMetrics();
			int space = 0;
			
			for( int i = 0; i < text.length(); i++ ) {
				int ori_x = X + space + 2;
				int ori_y = Y + 2;
				
				char word = text.charAt(i);
				
				switch(word) {
					case '　':
					//全角空白
						if(((NormalTextAttr)norm_attr).draw_zenspace){
							g.setColor(ConvAWT.CVEC_Color(((NormalTextAttr)norm_attr).space_col));
							g.drawRect(ori_x, ori_y, WIDTH*2 -6, HEIGHT - 6);
						}
						break;
					case ' ':
					//半角空白
						if(((NormalTextAttr)norm_attr).draw_space){
							g.setColor(ConvAWT.CVEC_Color(((NormalTextAttr)norm_attr).zenspace_col));
							g.drawLine(ori_x, ori_y +  HEIGHT - 8, ori_x, ori_y +  HEIGHT - 6);
							g.drawLine(ori_x + WIDTH -4, ori_y + HEIGHT - 8, ori_x + WIDTH -4, ori_y + HEIGHT - 6);
							g.drawLine(ori_x, ori_y + HEIGHT - 6, ori_x + WIDTH -4, ori_y + HEIGHT - 6);
						}
						break;
					case '\t':
					//タブ
						if(((NormalTextAttr)norm_attr).draw_tab){
							g.setColor(ConvAWT.CVEC_Color(((NormalTextAttr)norm_attr).tab_col));
							g.drawLine(ori_x, ori_y +  HEIGHT/2, ori_x + WIDTH/2, ori_y +  HEIGHT/2);
							g.drawLine(ori_x + WIDTH/3, ori_y + HEIGHT/2 - HEIGHT/6, ori_x + WIDTH/2, ori_y +  HEIGHT/2);
							g.drawLine(ori_x + WIDTH/3, ori_y + HEIGHT/2 + HEIGHT/6 , ori_x + WIDTH/2, ori_y +  HEIGHT/2);
						}
						break;
				}
				
				if( word != '\t' ) {
					space += fontMetrics.stringWidth(""+word);
				} else {
					space += (int)getTabExpander().nextTabStop((float)(X + space), i) - (X + space);
				}
			}
			
			g.setColor(defaultColor);
		}
	}
	//改行表示
	static class MyParagraphView extends ParagraphView {
		//private NormalTextAttr norm_attr;
		private TextAttrBase norm_attr;
		//
		//public MyParagraphView(Element elem,NormalTextAttr _norm_attr) {
		public MyParagraphView(Element elem,TextAttrBase _norm_attr) {
			super(elem);
			norm_attr=_norm_attr;
		}
		@Override public void paint(Graphics g, Shape allocation) {
			if(g==null || allocation==null)return;
			super.paint(g, allocation);
			//EOFを描画する場合、最後は改行マークを表示しない
			if(((NormalTextAttr)norm_attr).draw_eof && getEndOffset() == getDocument().getEndPosition().getOffset() )return;
			//表示へ
			paintCustomParagraph(g, allocation);
		}
		//改行表示
		private void paintCustomParagraph(Graphics g, Shape a) {
			if(g==null || a==null)return;
			try{
				if(((NormalTextAttr)norm_attr).draw_cr){
					int end=getEndOffset();
	//System.out.println("end:"+end);
					Shape paragraph = modelToView(end, a, Position.Bias.Backward);
					Rectangle r = (paragraph==null)?a.getBounds():paragraph.getBounds();
					int w = r.width;
					int h = r.height;
					int x = r.x;
					int y = r.y;
					Color old = g.getColor();
					g.setColor(ConvAWT.CVEC_Color(((NormalTextAttr)norm_attr).cr_col));
					int x0=x+2;
					int x1=x0-2;
					int x2=x0+2;
					int y0=y  +3;
					int y1=y+h-3;
					int y2=y1-2;
					g.drawLine(x0, y0, x0, y1);
					g.drawLine(x0, y1, x1, y2);
					g.drawLine(x0, y1, x2, y2);
					g.setColor(old);
				}
			}catch(Exception e) { e.printStackTrace(); }
		}
	}
	//EOF表示
	static class EditOriginalBoxView extends BoxView {
		//private NormalTextAttr norm_attr;
		private TextAttrBase norm_attr;
		//
		//public EditOriginalBoxView(Element elem, int y,NormalTextAttr _norm_attr) {
		public EditOriginalBoxView(Element elem, int y,TextAttrBase _norm_attr) {
			super(elem, y);
			norm_attr=_norm_attr;
		}
		
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			paintOriginalParagraph(g, a);
		}
		//EOF表示
		private void paintOriginalParagraph(Graphics g, Shape a) {
			IRECT clip=ConvAWT.Rectangle_IRECT(g.getClipBounds());
			try {
				if(((NormalTextAttr)norm_attr).draw_eof){
					Font font=norm_attr.getFont();
					g.setFont(font);
					//
					int end=getEndOffset();
	//System.out.println("end:"+end);
					Shape paragraph = modelToView(end, a, Position.Bias.Backward);
					Rectangle rec = (paragraph==null) ? a.getBounds() : paragraph.getBounds();
					final int X = rec.x;
					final int Y = rec.y;
					final int HEIGHT = rec.height;
					final Color defaultColor = g.getColor();
					
					FontMetrics fm = g.getFontMetrics();
					int charWidth = fm.stringWidth("[EOF]");
	//System.out.println("w:"+charWidth);
					
					
					g.setColor(ConvAWT.CVEC_Color(((NormalTextAttr)norm_attr).eof_col));
					g.drawString("[EOF]", X, Y+HEIGHT);
					
					g.setColor(defaultColor);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			};
		}
	}
}
