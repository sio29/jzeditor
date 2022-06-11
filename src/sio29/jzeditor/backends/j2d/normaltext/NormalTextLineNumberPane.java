/******************************************************************************
;	çsî‘çÜ
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.linenumber.LineNumberPane;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;


public class NormalTextLineNumberPane extends LineNumberPane{
	static class NormalTextLineNumberCallback implements LineNumberPane.Callback{
		public NormalText p_normaltext;
		//
		NormalTextLineNumberCallback(NormalText p_normaltext){
			this.p_normaltext=p_normaltext;
		}
		public String getFontName(){
			return p_normaltext.GetNormalAttr().font_name;
		}
		public int getFontSize(){
			return p_normaltext.GetNormalAttr().font_size;
		}
		public CVECTOR getTextCol(){
			return p_normaltext.GetNormalAttr().linenum_col;
		}
		public CVECTOR getBackCol(){
			return p_normaltext.GetNormalAttr().back_col;
		}
		public CVECTOR getCaretCol(){
			return CVECTOR.YELLOW;
		}
		private JComponent getTextArea(){
			return p_normaltext.GetTextArea();
		}
		public Dimension getTextSize(){
			JComponent textArea=getTextArea();
			Dimension t_size=textArea.getPreferredSize();
			return t_size;
		}
		public int getLineAtPoint(int y){
			JComponent textArea=getTextArea();
			return TextComponentTool.getLineAtPointY(textArea,y);
		}
		public int getCaretPosition(){
			JComponent textArea=getTextArea();
			return TextComponentTool.getCaretPosition(textArea);
		}
		public IRECT PositonToRect(int pos) throws Exception{
			JComponent textArea=getTextArea();
			//return TextComponentTool.positonToRect(textArea,pos);
			return ConvAWT.Rectangle_IRECT(TextComponentTool.modelToView(textArea,pos));
		}
		public int getDocumentLength(){
			JComponent textArea=getTextArea();
			return TextComponentTool.getDocumentLength(textArea);
		}
		public LineParam[] getLineParamList(int start,int end){
			JComponent textArea=getTextArea();
			return TextComponentTool.getLineParamList(textArea,start,end);
		}
	}
	public NormalTextLineNumberPane(NormalText normaltext){
		super(new NormalTextLineNumberCallback(normaltext));
	}
}

