/******************************************************************************
;	ÉãÅ[ÉâÅ[
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import java.awt.Dimension;
import javax.swing.JComponent;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.ulib.udlgbase.backends.j2d.udlg.ruler.RulerPane;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public class NormalTextRulerPane extends RulerPane{
	static class NormalTextRulerCallback implements RulerPane.Callback{
		public NormalText p_normaltext;
		//
		NormalTextRulerCallback(NormalText p_normaltext){
			this.p_normaltext=p_normaltext;
		}
		public String getFontName(){
			return p_normaltext.GetNormalAttr().font_name;
		}
		public int getFontSize(){
			return p_normaltext.GetNormalAttr().font_size;
		}
		private JComponent getTextArea(){
			return p_normaltext.GetTextArea();
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
		public IVECTOR2 getTextSize(){
			JComponent textArea=getTextArea();
			if(textArea==null)return new IVECTOR2(0,0);
			return ConvAWT.Dimension_IVEC2(textArea.getPreferredSize());
		}
	}
	NormalTextRulerPane(NormalText normaltext){
		super(new NormalTextRulerCallback(normaltext));
	}
}
