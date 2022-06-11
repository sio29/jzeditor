/******************************************************************************
;	�e�L�X�g�R���|�[�l���g�p�c�[��
;	��Swing�p
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool;

import java.lang.CharSequence;
import java.lang.StringBuffer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.FontMetricsWrapper;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.plaf.TextUI;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Caret;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AbstractDocument;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.linenumber.*;

import sio29.jzeditor.backends.j2d.texttool.FilenameLine;
import sio29.jzeditor.backends.j2d.texttool.TextTool;
import sio29.jzeditor.backends.j2d.texttool.CharCode;
import sio29.jzeditor.backends.j2d.texttool.StringConverter;
import sio29.jzeditor.backends.j2d.caret.*;
import sio29.jzeditor.backends.j2d.normaltext.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.jtextpane.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.vram.*;


public class TextComponentTool{
	//============================================
	//�t�H���g�T�C�Y
	//============================================
	public static int getTextAreaFontSize(JComponent _textArea){
		FontMetrics fm=getFontMetrics(_textArea);
		int charWidth = fm.charWidth('M');
		return charWidth;
	}
	public static FontMetrics getFontMetrics(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getFontMetrics();
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.getFontMetrics(textArea.getFont());
	}
	//============================================
	//�^�u�T�C�Y��ݒ�
	//============================================
	public static void setTabSize(JComponent _textArea,int tab_size){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextArea){
			((JTextArea)textArea).setTabSize(tab_size);
		}
	}
	public static void setTabSize2(JComponent _textArea,int tab_size){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setTabSize(tab_size);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea==null)return;
		if(!(textArea instanceof JTextPane))return;
		
		int charWidth = TextComponentTool.getTextAreaFontSize(_textArea);
		int tabLength = charWidth * (tab_size-1);

		TabStop[] tabs = new TabStop[10];
		for(int j=0;j<tabs.length;j++) {
		  tabs[j] = new TabStop((j+1)*tabLength);
		}
		TabSet tabSet = new TabSet(tabs);
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setTabSet(attrs, tabSet);
		int l = textArea.getDocument().getLength();
		((JTextPane)textArea).getStyledDocument().setParagraphAttributes(0, l, attrs, false);
//System.out.println("SetTabSize:"+tabLength +"/"+charWidth );
	}
	//============================================
	//�e�L�X�g�G���A�̕������߂�
	//============================================
	public static int getTextAreaWidth(JComponent _textArea,int width,boolean view_cr,String eof_str){
		try{
			FontMetrics fm=TextComponentTool.getFontMetrics(_textArea);
			//���s�����̃T�C�Y��������
			int ret_size=TextComponentTool.getTextAreaFontSize(_textArea);
			width+=ret_size;	//���s
			//EOF�s�̃T�C�Y�����߂�
			int bottom=TextComponentTool.getDocumentLength(_textArea);
			Rectangle rect=TextComponentTool.modelToView(_textArea,bottom);
			int eof_line_size=rect.x;
			//EOF������������
			int eof_size=fm.stringWidth("[EOF]");
			eof_line_size+=eof_size;
			if(width<eof_line_size)width=eof_line_size;
		}catch(Exception ex){}
		return width;
	}
	//============================================
	//�s��Element�𓾂�
	//============================================
	public static Element getLineParagraphElement(JComponent _textArea,int line){
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		Element root=doc.getDefaultRootElement();
		return root.getElement(line);
	}
	//============================================
	//�s�͈̔͂����߂�
	//============================================
	public static int[] getLineBounds(JComponent _textArea,int line){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getLineBounds(line);
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		Element el=getLineParagraphElement(_textArea,line);
		int start=el.getStartOffset();
		int end  =el.getEndOffset();
		return new int[]{start,end};
	}
	//============================================
	//�s�������߂�
	//============================================
	public static int getLineNum(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getLineNum();
		}
		if(!(_textArea instanceof JTextComponent))return 0;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.getDocument().getDefaultRootElement().getElementCount();
	}
	//============================================
	//�h�L�������g�̒���
	//============================================
	public static int getDocumentLength(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getDocumentLength();
		}
		if(!(_textArea instanceof JTextComponent))return 0;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.getDocument().getLength();
	}
	//============================================
	//�ʒu����s�����߂�
	//============================================
	public static int getLineFromPos(JComponent _textArea,int pos){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getLineFromPos(pos);
		}
		if(!(_textArea instanceof JTextComponent))return 0;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
			Document doc=textArea.getDocument();
			Element root=doc.getDefaultRootElement();
			return root.getElementIndex(pos);
		}catch(Exception ex){
		}
		return 1;
	}
	//============================================
	//�ʒu���猅�����߂�
	//============================================
	public static int getColumnFromPos(JComponent _textArea,int pos){
//System.out.println("getColumnFromPos:"+pos);
		int line=TextComponentTool.getLineFromPos(_textArea,pos);
		int[] bounds=TextComponentTool.getLineBounds(_textArea,line);
		int start_pos=bounds[0];
		int end_pos  =bounds[1];
		int line_len=end_pos-start_pos;
		CharSequence line_str=TextComponentTool.getText(_textArea,start_pos,line_len);
		int len=pos-start_pos;
		if(len>line_len)len=line_len;
		int column=0;
		for(int i=0;i<len;){
			int code=Character.codePointAt(line_str,i);
			i+=Character.charCount(code);
			column+=TextTool.getCharWidth(code);
		}
		return column;
	}
	//============================================
	//�s���𓾂�
	//============================================
	public static int getLineStart(JComponent _textArea,int _line){
		int[] bounds=TextComponentTool.getLineBounds(_textArea,_line);
		if(bounds==null)return 0;
		return bounds[0];
	}
	public static int getLineTop(JComponent _textArea,int _line){
		return getLineStart(_textArea,_line);
	}
	//============================================
	//�s���𓾂�
	//============================================
	public static int getLineBottom(JComponent _textArea,int _line){
		int[] bounds=TextComponentTool.getLineBounds(_textArea,_line);
		if(bounds==null)return 0;
		return bounds[1];
	}
	//============================================
	//�w�胉�C���̕�����𓾂�
	//============================================
	public static CharSequence getLineText(JComponent _textArea,int line){
		if(_textArea instanceof NormalTextVram){
			try{
				return ((NormalTextVram)_textArea).getLineText(line);
			}catch(Exception ex){
				return null;
			}
		}
		int[] bounds=TextComponentTool.getLineBounds(_textArea,line);
		if(bounds==null)return null;
		int line_start=bounds[0];
		int line_end  =bounds[1];
		int line_len=line_end-line_start;
//System.out.println(String.format("line[%d]:bounds(%d-%d)",line,line_start,line_end));
		CharSequence cmd_m=TextComponentTool.getText(_textArea,line_start,line_len);
		if(cmd_m==null)return null;
		//�����񂩂�Ō�̉��s���Ȃ�
		CharSequence cmd_m2=TextTool.GetStringRemoveCR(cmd_m.toString());
		return cmd_m2;
	}
	//============================================
	//���݂̈ʒu
	//============================================
	//�L�����b�g�̈ʒu�l��
	public static int getCaretPosition(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getCaretPosition();
		}
		if(!(_textArea instanceof JTextComponent))return 0;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.getCaretPosition();
	}
	//�L�����b�g�̈ʒu�ݒ�
	public static void setCaretPosition(JComponent _textArea,int p){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setCaretPosition(p);
			((NormalTextVram)_textArea).updateCaretPos();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setCaretPosition(p);
	}
	//�L�����b�g����s�𓾂�
	public static int getCaretLine(JComponent _textArea){
//		if(_textArea instanceof NormalTextVram){
//			return ((NormalTextVram)_textArea).getCaretLine();
//		}
		int caret_pos=TextComponentTool.getCaretPosition(_textArea);
		return TextComponentTool.getLineFromPos(_textArea,caret_pos);
	}
	//�L�����b�g�̌������߂�
	public static int getCaretColumn(JComponent _textArea){
		int caret_pos=TextComponentTool.getCaretPosition(_textArea);
		return TextComponentTool.getColumnFromPos(_textArea,caret_pos);
	}
	//============================================
	//�J�[�\���ʒu�����`�����߂�
	//============================================
	public static Rectangle modelToView(JComponent _textArea,int pos) throws Exception {
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).modelToView(pos);
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		TextUI mapper=textArea.getUI();
		return mapper.modelToView(textArea,pos);
	}
	public static int viewToModel(JComponent _textArea,Point pos) throws Exception {
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).viewToModel(pos);
		}
		if(!(_textArea instanceof JTextComponent))return -1;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.viewToModel(pos);
	}
	//============================================
	//Y���W���烉�C��
	//============================================
	public static int getLineAtPointY(JComponent _textArea,int y){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getLineAtPointY(y);
		}
		if(!(_textArea instanceof JTextComponent))return 0;
		JTextComponent textArea=(JTextComponent)_textArea;
		Element root = textArea.getDocument().getDefaultRootElement();
		int pos = textArea.viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}
	//============================================
	//���݂̍s�̎w��ʒu�ֈړ�
	//============================================
	public static int getPosFromCaretLineAtPointX(JComponent _textArea,int line,int px){
		try{
			int pos=TextComponentTool.getLineTop(_textArea,line);
			Rectangle rect=TextComponentTool.modelToView(_textArea,pos);
			Point new_pos=new Point(px,rect.y);
			int new_caret_pos=TextComponentTool.viewToModel(_textArea,new_pos);
			return new_caret_pos;
		}catch(Exception ex){
			return 0;
		}
	}
	public static int getPosFromCaretLineAtColumn(JComponent _textArea,int line,int colm){
System.out.println("jumpCaretLineAtColumn:"+colm);
		int font_size=TextComponentTool.getTextAreaFontSize(_textArea);
		int px=colm*font_size;
		return TextComponentTool.getPosFromCaretLineAtPointX(_textArea,line,px);
	}
	public static void jumpCaretLineAtPointX(JComponent _textArea,int px){
		int line=TextComponentTool.getCaretLine(_textArea);
		int pos=TextComponentTool.getPosFromCaretLineAtPointX(_textArea,line,px);
		TextComponentTool.setCaretPosition(_textArea,pos);
	}
	public static void jumpCaretLineAtColumn(JComponent _textArea,int colm){
		int line=TextComponentTool.getCaretLine(_textArea);
		int pos=TextComponentTool.getPosFromCaretLineAtColumn(_textArea,line,colm);
		TextComponentTool.setCaretPosition(_textArea,pos);
	}
	//============================================
	//�e�L�X�g�l��
	//============================================
	//�S��
	public static CharSequence getText(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getText();
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		return textArea.getText();
	}
	//�w��ʒu
	public static CharSequence getText(JComponent _textArea,int pos,int size){
		if(_textArea instanceof NormalTextVram){
			try{
				return ((NormalTextVram)_textArea).getText(pos,size);
			}catch(Exception ex){
				return null;
			}
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		try{
			return doc.getText(pos,size);
		}catch(Exception ex){
			return null;
		}
	}
	//============================================
	//�e�L�X�g�ݒ�
	//============================================
	public static void setText(JComponent _textArea,String m,AttributeSet attr){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setText(m);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		try {
			int bottom=doc.getLength();
			doc.remove(0,bottom);
			doc.insertString(0, m, attr);
			TextComponentTool.setCaretPosition(textArea,0);
		} catch (BadLocationException e) {
System.out.println(""+e);
		}
	}
	
	//============================================
	//�w��ʒu�̃��C���͈̔͂����߂�
	//============================================
	public static int[] getLineBoundsFromPos(JComponent _textArea,int pos){
		int line=TextComponentTool.getLineFromPos(_textArea,pos);
		return TextComponentTool.getLineBounds(_textArea,line);
	}
	//============================================
	//���݂̃��C���̕�����𓾂�
	//============================================
	public static CharSequence getCaretLineText(JComponent _textArea){
		int line=TextComponentTool.getCaretLine(_textArea);
		return TextComponentTool.getLineText(_textArea,line);
	}
	//============================================
	//�^�O�W�����v�p�����񂩂�t�@�C�����ƍs�ԍ��𓾂�
	//============================================
	public static FilenameLine getTagJumpFilenameLine(JComponent _textArea,String current_dir){
		CharSequence line_m=TextComponentTool.getCaretLineText(_textArea);
		if(line_m==null)return null;
		//�^�O�W�����v�p�����񂩂�t�@�C�����ƍs�ԍ��𓾂�
		return FilenameLine.GetFilenameLineFromStr(line_m.toString(),current_dir);
	}
	//============================================
	//���݂̍s
	//============================================
	public static int getPrevLine(JComponent _textArea,int line){
		line--;
		if(line<0)line=0;
		return line;
	}
	public static int getNextLine(JComponent _textArea,int line){
		line++;
		int line_bottom=getLineNum(_textArea)-1;
		if(line>line_bottom)line=line_bottom;
		return line;
	}
	//============================================
	//�T�C�Y�����߂�
	//============================================
	public static int getTotalSize(JComponent _textArea){
		int bottom=TextComponentTool.getDocumentLength(_textArea);
		return bottom*2;
	}
	//============================================
	//�h�L�������g����?
	//============================================
	public static boolean isEmpty(JComponent _textArea){
		return (TextComponentTool.getDocumentLength(_textArea)==0);
	}
	//============================================
	//�W�����v
	//============================================
	//�擪�s�փW�����v
	public static void jumpTop(JComponent _textArea){
		TextComponentTool.setCaretPosition(_textArea,0);
	}
	//�ŏI�s�փW�����v
	public static void jumpBottom(JComponent _textArea){
		int bottom=TextComponentTool.getDocumentLength(_textArea);
		TextComponentTool.setCaretPosition(_textArea,bottom);
	}
	//�s���փW�����v
	public static void jumpLineTop(JComponent _textArea,int line){
		int pos=TextComponentTool.getLineStart(_textArea,line);
		TextComponentTool.setCaretPosition(_textArea,pos);
	}
	//�s���փW�����v
	public static void jumpLineBottom(JComponent _textArea,int line){
		int pos=TextComponentTool.getLineBottom(_textArea,line);
		TextComponentTool.setCaretPosition(_textArea,pos);
	}
	//�L�����b�g�̂���s���փW�����v
	public static void jumpCaretLineTop(JComponent _textArea){
		int line=TextComponentTool.getCaretLine(_textArea);
		TextComponentTool.jumpLineTop(_textArea,line);
	}
	//�L�����b�g�̂���s���փW�����v
	public static void jumpCaretLineBottom(JComponent _textArea){
		int line=TextComponentTool.getCaretLine(_textArea);
		TextComponentTool.jumpLineBottom(_textArea,line);
	}
	//���̍s�փW�����v
	public static void jumpNextLineTop(JComponent _textArea){
		int caret_line=TextComponentTool.getCaretLine(_textArea);
		int next_line=TextComponentTool.getNextLine(_textArea,caret_line);
		TextComponentTool.jumpLineTop(_textArea,next_line);
	}
	//�J�[�\�������̍s�̍s���ֈړ�
	public static void jumpCaretLineNextLineTop(JComponent _textArea){
		int _line=TextComponentTool.getCaretLine(_textArea);
		int _line2=TextComponentTool.getNextLine(_textArea,_line);
		TextComponentTool.jumpLineTop(_textArea,_line);
	}
	//�J�[�\����O�̍s�̍s���ֈړ�
	public static void jumpCaretLinePrevLineBottom(JComponent _textArea){
		int _line=TextComponentTool.getCaretLine(_textArea);
		int _line2=TextComponentTool.getPrevLine(_textArea,_line);
		TextComponentTool.jumpLineBottom(_textArea,_line2);
	}
	//============================================
	//�ʒu����s���𓾂�
	//============================================
	public static int getLineTopFromPos(JComponent _textArea,int pos){
		int _line=TextComponentTool.getLineFromPos(_textArea,pos);
		return TextComponentTool.getLineStart(_textArea,_line);
	}
	//============================================
	//�ʒu����s���𓾂�
	//============================================
	public static int getLineBottomFromPos(JComponent _textArea,int pos){
		int line=TextComponentTool.getLineFromPos(_textArea,pos);
		return TextComponentTool.getLineBottom(_textArea,line);
	}
	//========================================================
	//���݈ʒu���s����?
	//========================================================
	public static boolean isLineTop(JComponent _textArea){
		int caret_pos=TextComponentTool.getCaretPosition(_textArea);
		int line=TextComponentTool.getLineFromPos(_textArea,caret_pos);
		int top=TextComponentTool.getLineTop(_textArea,line);
		return (caret_pos==top);
	}
	//========================================================
	//���݈ʒu���s����?
	//========================================================
	public static boolean isLineBottom(JComponent _textArea){
		int caret_pos=TextComponentTool.getCaretPosition(_textArea);
		int line=TextComponentTool.getLineFromPos(_textArea,caret_pos);
		int bottom=TextComponentTool.getLineBottom(_textArea,line);
		return (caret_pos==bottom);
	}
	//============================================
	//CharSequence�̍쐬
	//============================================
	static CharSequence getCharSequenceFromDocument(Document doc){
		return new CharSequence(){
			public char charAt(int index) {
				try{
					return doc.getText(index,1).charAt(0);
				}catch(Exception ex){
					return 0;
				}
			}
			public int length(){
				return doc.getLength();
			}
			public CharSequence subSequence(int start, int end) {
				try{
					return doc.getText(start,end-start);
				}catch(Exception ex){
					return null;
				}
			}
			public String toString(){
				return doc.toString();
			}
		};
	}
	public static CharSequence getCharSequence(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getText();
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		return getCharSequenceFromDocument(doc);
	}
	//============================================
	//�΂̊��ʂփW�����v
	//============================================
	//�΂̊��ʂփW�����v
	public static void jumpPairKakko(JComponent _textArea){
		CharSequence cs=TextComponentTool.getCharSequence(_textArea);
		if(cs==null)return;
		int now_pos=TextComponentTool.getCaretPosition(_textArea);
		int new_pos=TextComponentTool.getJumpPairKakkoPos(cs,now_pos);
		if(new_pos>=0){
			TextComponentTool.setCaretPosition(_textArea,new_pos);
		}
	}
	//�΂̊��ʂ�T��
	public static int getJumpPairKakkoPos(CharSequence doc,int now_pos){
		if(doc==null)return -1;
		int len=doc.length();
		try{
			char this_m=doc.charAt(now_pos);
			boolean dir_flg=false;
			char pair_m;
			if(this_m=='('){
				dir_flg=true;
				pair_m=')';
			}else if(this_m==')'){
				dir_flg=false;
				pair_m='(';
			}else if(this_m=='['){
				dir_flg=true;
				pair_m=']';
			}else if(this_m==']'){
				dir_flg=false;
				pair_m='[';
			}else if(this_m=='{'){
				dir_flg=true;
				pair_m='}';
			}else if(this_m=='}'){
				dir_flg=false;
				pair_m='{';
			}else{
				return -1;
			}
			int hit_cnt=1;
			int end_pos;
			int add_pos;
			if(dir_flg){
				now_pos++;
				end_pos=len-1;
				add_pos=1;
			}else{
				now_pos--;
				end_pos=0;
				add_pos=-1;
			}
			if(now_pos<0 || now_pos>=len)return -1;
			while(true){
				if(now_pos==end_pos)break;
				char m=doc.charAt(now_pos);
				if(m==this_m){
					hit_cnt++;
				}else if(m==pair_m){
					hit_cnt--;
					if(hit_cnt==0){
						return now_pos;
					}
				}
				now_pos+=add_pos;
			}
		}catch(Exception ex){}
		return -1;
	}
	//============================================
	//�e�L�X�g�}��
	//============================================
	//�J�[�\���ʒu
	public static boolean insertText(JComponent _textArea,CharSequence m,AttributeSet attr){
		return insertTextPos(_textArea,m,attr,TextComponentTool.getCaretPosition(_textArea));
	}
	//�e�L�X�g�̍Ō�
	public static boolean insertTextBottom(JComponent _textArea,CharSequence m,AttributeSet attr){
		return insertTextPos(_textArea,m,attr,TextComponentTool.getDocumentLength(_textArea));
	}
	public static boolean insertTextPos(JComponent _textArea,CharSequence m,AttributeSet attr,int pos){
		if(_textArea instanceof NormalTextVram){
			Color fg_col=StyleConstants.getForeground(attr);
			((NormalTextVram)_textArea).setCurrentColor(fg_col);
			boolean r=((NormalTextVram)_textArea).insertText(m,pos);
			((NormalTextVram)_textArea).setCaretPosition(pos+m.length());
			return r;
		}
		if(!(_textArea instanceof JTextComponent))return false;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
			Document doc=((JTextComponent)textArea).getDocument();
			if(doc!=null){
				doc.insertString(pos,m.toString(),attr);
				return true;
			}
		}catch(Exception ex) {
		}
		return false;
	}
	//============================================
	//�e�L�X�g�폜
	//============================================
	public static boolean removeText(JComponent _textArea,int start,int end){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).removeText(start,end);
		}
		if(!(_textArea instanceof JTextComponent))return false;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
			Document doc=textArea.getDocument();
			doc.remove(start,end);
			return true;
		}catch(Exception ex){
		}
		return false;
	}
	//============================================
	//�I�[�g�^�u�̑}������
	//============================================
	public static void insertAutoTab(JComponent _textArea,AttributeSet attr){
		int caret_line=TextComponentTool.getCaretLine(_textArea);
		int old_line_num=caret_line-1;
		int new_line_num=old_line_num+1;
		int new_line_top=TextComponentTool.getLineTop(_textArea,new_line_num);
		CharSequence old_text=TextComponentTool.getLineText(_textArea,old_line_num);
		int tab_num=0;
		int len=old_text.length();
		StringBuilder add_tab=new StringBuilder();
		for(int i=0;i<len;i++){
			char m=old_text.charAt(i);
			if(m!='\t' && m!=' ')break;
			add_tab.append(m);
		}
		insertTextPos(_textArea,add_tab,attr,new_line_top);
	}
	//============================================
	//�I�𕔕����w��̃e�L�X�g�ŏ㏑������
	//============================================
	public static void replaceSelectionText(JComponent _textArea,String text,AttributeSet attr){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).replaceSelectionText(text);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		//Implement overtype mode by selecting the character at the current
		//caret position
		try{
			//�I��͈͂�����?
			boolean is_selected=TextComponentTool.isSelected(_textArea);
			int caret_pos=TextComponentTool.getCaretPosition(_textArea);
			int len=TextComponentTool.getDocumentLength(_textArea);
			if(!is_selected && caret_pos<len){
				CharSequence m=TextComponentTool.getText(_textArea,caret_pos,1);
				if(m.charAt(0)!='\n'){
					textArea.moveCaretPosition(caret_pos + 1);
				}
			}
			//�p�������s
			//super.replaceSelection(text);
//			textArea.super.replaceSelection(text);		//������
		}catch(Exception ex){}
	}
	//============================================
	// �u������
	//============================================
	public static void replace(DocumentFilter.FilterBypass fb,int offset,int length,String text,AttributeSet attrs,boolean isOvertypeMode,int tab_size){
		//�u���������s
		try{
			if(isOvertypeMode){
			//�㏑��
				Document doc=fb.getDocument();
				Element root=doc.getDefaultRootElement();
				int line_index=root.getElementIndex(offset);
				Element line=root.getElement(line_index);
				if(offset==line.getEndOffset()-1){
				//�s���͑}��
					isOvertypeMode=false;
				}else{
				//�^�u
					char m=doc.getText(offset,1).charAt(0);
					if(m==0x09){
						if((offset % tab_size)==(tab_size-1)){
						}else{
						//�^�u�̋󔒂̋󂫂��Ȃ��Ȃ�Ȃ�����}��
							isOvertypeMode=false;
						}
						//System.out.println("Tab");
					}
				}
			}
			if(!isOvertypeMode){
			//�}��
				fb.replace(offset,length,text,attrs);
			}else{
			//�㏑��
				if(length==0){
					length=1;
				}
				fb.replace(offset,length,text,attrs);
			}
		}catch(Exception ex){}
	}
	//============================================
	//�J�b�g&�R�s�[&�y�[�X�g
	//============================================
	public static void cut(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).cut();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.cut();
	}
	public static void copy(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).copy();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.copy();
	}
	public static void paste(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).paste();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.paste();
	}
	//VZ�^�C�v�J�b�g(�J�n�ʒu�ƏI���ʒu�������Ȃ�1�s�J�b�g�A����ȊO�͒ʏ�̃J�b�g)
	public static void vzCut(JComponent _textArea){
		if(!TextComponentTool.isSelected(_textArea)){
		//�J�n�ʒu�ƏI���ʒu�������Ȃ�1�s�J�b�g
			TextComponentTool.lineCut(_textArea);
		}else{
		//����ȊO�͒ʏ�̃J�b�g
			TextComponentTool.cut(_textArea);
		}
	}
	//��s�J�b�g
	public static void lineCut(JComponent _textArea){
		int caret_line=TextComponentTool.getCaretLine(_textArea);
		int[] bounds=TextComponentTool.getLineBounds(_textArea,caret_line);
		if(bounds==null)return;
		TextComponentTool.setSelctionBounds(_textArea,bounds[0],bounds[1]);
		TextComponentTool.cut(_textArea);
	}
	//============================================
	//�I��
	//============================================
	//�S�đI��
	public static void selectAll(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).selectAll();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.selectAll();
	}
	//�I���N���A
	public static void selectClear(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).clearSelect();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		int pos=TextComponentTool.getCaretPosition(_textArea);
		textArea.select(0,0);
		TextComponentTool.setCaretPosition(_textArea,pos);
	}
	//�I��͈͂𓾂�
	public static int[] getSelectionBounds(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getSelectionBounds();
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		int start=textArea.getSelectionStart();
		int end  =textArea.getSelectionEnd();
		return new int[]{start,end};
	}
	//�I��͈͂̐ݒ�
	public static void setSelctionBounds(JComponent _textArea,int start,int end){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).select(start,end);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Caret caret=textArea.getCaret();
		caret.setDot(start);
		caret.moveDot(end);
	}
	//�I���\��?
	public static boolean isSelected(JComponent _textArea){
		int[] bounds=getSelectionBounds(_textArea);
		if(bounds==null)return false;
		return (bounds[0]!=bounds[1]);
	}
	//�I��͈͓�?
	public static boolean checkSelctionBounds(JComponent _textArea,int p){
		int[] bounds=getSelectionBounds(_textArea);
		if(bounds==null)return false;
		int start=bounds[0];
		int end  =bounds[1];
		if(p<start)return false;
		if(p>end)return false;
		return true;
	}
	//�I�𕔕��̕ϊ�
	public static void selectConv(JComponent _textArea,String type,AttributeSet attr) throws Exception{
		int[] bounds=TextComponentTool.getSelectionBounds(_textArea);
		if(bounds==null)return;
		int start_pos=bounds[0];
		int end_pos  =bounds[1];
		int len=end_pos-start_pos;
		try{
			CharSequence select_text=TextComponentTool.getText(_textArea,start_pos,len);
			String new_text=StringConverter.convert(select_text.toString(),type);
			TextComponentTool.removeText(_textArea,start_pos,select_text.length());
			TextComponentTool.insertTextPos(_textArea,new_text,attr,start_pos);
		}catch(Exception ex){
		}
	}
	//============================================
	//�L�����b�g
	//============================================
	//�}�����[�h�t���b�v
	public static void flipInsertMode(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).flipInsertMode();
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Caret caret=textArea.getCaret();
		if(caret instanceof InsertBoxCaret){
			((InsertBoxCaret)caret).flipOvertypeMode();
		}
	}
	//�㏑�����[�h?
	public static boolean isOvertypeMode(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).isOvertypeMode();
		}
		if(!(_textArea instanceof JTextComponent))return false;
		JTextComponent textArea=(JTextComponent)_textArea;
		Caret caret=textArea.getCaret();
		if(caret instanceof InsertBoxCaret){
			return ((InsertBoxCaret)caret).isOvertypeMode();
		}else{
			return false;
		}
	}
	//�L�����b�g�̌`���l�p��
	public static void initCaretShape(JComponent _textArea){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Caret old_caret=textArea.getCaret();
		InsertBoxCaret caret=new InsertBoxCaret(textArea);
		caret.setDot(old_caret.getDot());
		caret.setBlinkRate(old_caret.getBlinkRate());
	}
	//�L�����b�g�ʒu����
	public static void setCaretFilter(JComponent _textArea,CaretFilter filter){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setCaretFilter(filter);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Caret caret=textArea.getCaret();
		if(caret instanceof InsertBoxCaret){
			((InsertBoxCaret)caret).setCaretFilter(filter);
		}
	}
	//============================================
	//�F
	//============================================
	//�e�L�X�g�F?
	//public static void setForeground(JComponent _textArea,Color col){
	public static void setForeground(JComponent _textArea,Object col){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setForeground(col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextPane){
			//AttributeSet 
			//MutableAttributeSet attr=new SimpleAttributeSet(attr);
			//MutableAttributeSet attr=(MutableAttributeSet)(((JTextPane)textArea).getCharacterAttributes());
			//StyleConstants.setForeground(attr,col);
			//((JTextPane)textArea).setCharacterAttributes(attr,false);
			//((JTextPane)textArea).setCharacterAttributes((AttributeSet)col,false);
		}else if(textArea instanceof JTextArea){
			textArea.setForeground((Color)col);
		}
	}
	//�L�����b�g�F
	public static void setCaretColor(JComponent _textArea,Color col){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setCaretColor(col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setCaretColor(col);
	}
	//�I���ʒu�F
	public static void setSelectionColor(JComponent _textArea,Color col){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setSelectionColor(col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setSelectionColor(col);
	}
	//�I�����ꂽ�e�L�X�g�̐F
	public static void setSelectedTextColor(JComponent _textArea,Color col){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setSelectedTextColor(col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setSelectedTextColor(col);
	}
	//�e�L�X�g�̐F
	public static void setDisabledTextColor(JComponent _textArea,Color col){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setDisabledTextColor(col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setDisabledTextColor(col);
	}
	//
	public static void setCharacterAttributes(JComponent _textArea,int start,int len,AttributeSet attr){
System.out.println("setCharacterAttributes");
		if(_textArea instanceof NormalTextVram){
			Color fg_col=StyleConstants.getForeground(attr);
System.out.println(""+fg_col);
			//Color bg_col=StyleConstants.getBackground(attr);
			//Color fg_col=Color.YELLOW;
			((NormalTextVram)_textArea).setCurrentColor(fg_col);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		if(doc instanceof StyledDocument){
			((StyledDocument)doc).setCharacterAttributes(start,len,attr,false);
		}else if(doc instanceof PlainDocument){
		}
	}
	//
	public static void addCaretListener(JComponent _textArea,CaretListener listener){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).addCaretListener(listener);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.addCaretListener(listener);
	}
	//============================================
	//Action
	//============================================
	public static Action[] getActions(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			//return ((NormalTextVram)_textArea).getActions();
			return null;
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextPane){
			StyledEditorKit editkit=(StyledEditorKit)((JTextPane)textArea).getEditorKit();
			return editkit.getActions();
		}
		return null;
	}
	/*
	public static IRECT positonToRect(JComponent _textArea,int pos){
		if(_textArea instanceof NormalTextVram){
			try{
				return ConvAWT.Rectangle_IRECT(((NormalTextVram)_textArea).modelToView(pos));
			}catch(Exception ex){
				return null;
			}
		}
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		TextUI mapper=textArea.getUI();
		try{
			return ConvAWT.Rectangle_IRECT(mapper.modelToView(textArea,pos));
		}catch(Exception ex){
		}
		return null;
	}
	*/
	//============================================
	//�s���
	//============================================
	public static LineNumberPane.LineParam[] getLineParamList(JComponent _textArea,int start,int end){
		int size=end-start+1;
		LineNumberPane.LineParam[] buff=new LineNumberPane.LineParam[size];
		int index=0;
		for(int i=start;i<=end;i++){
			int[] bounds=TextComponentTool.getLineBounds(_textArea,i);
			int start_pos=bounds[0];
			int end_pos  =bounds[1];
			buff[index]=new LineNumberPane.LineParam(i,start_pos,end_pos);
			index++;
		}
		return buff;
	}
	//============================================
	//BOM�����邩?
	//============================================
	public static boolean getBomFlg(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getBomFlg();
		}
		if(!(_textArea instanceof JTextComponent))return false;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		try{
		String m=doc.getText(0,1);
		char n=m.charAt(0);
//System.out.println("top("+Integer.toHexString(n)+")");
		//if(n==0xfeff){
		if(n==CharCode.BOM){
			return true;
		}else{
			return false;
		}
		}catch(Exception ex){
			return false;
		}
	}
	//============================================
	//BOM��ݒ肷��
	//============================================
	public static void setBomFlg(JComponent _textArea,boolean n,AttributeSet attr){
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).setBomFlg(n);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(n==getBomFlg(textArea))return;
		Document doc=textArea.getDocument();
		if(!n){
		//�{���Ȃ�
			try{
				doc.remove(0,1);
			}catch(Exception ex){}
		}else{
		//�{������
			try{
				//doc.insertString(0,new String(new char[]{0xfeff}),attr);
				doc.insertString(0,new String(new char[]{CharCode.BOM}),attr);
			}catch(Exception ex){}
		}
	}
	//============================================
	//�y�[�W�A�b�v�_�E��
	//============================================
	public static void halfPageUp(JComponent _textArea,JScrollPane scroll,boolean with_scroll,int size){
		halfPageUpDown(_textArea,scroll,with_scroll,false,size);
	}
	public static void halfPageDown(JComponent _textArea,JScrollPane scroll,boolean with_scroll,int size){
		halfPageUpDown(_textArea,scroll,with_scroll,true,size);
	}
	public static void halfPageUpDown(JComponent _textArea,JScrollPane scroll,boolean with_scroll,boolean up_down_flg,int size){
		/*
		if(_textArea instanceof NormalTextVram){
			((NormalTextVram)_textArea).halfPageUpDown(size,up_down_flg);
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		*/
		try{
			JViewport vp=scroll.getViewport();
			Dimension vp_size=vp.getViewRect().getSize();
			int height=vp_size.height;
			int add=0;
			if(size==0){
				add=(height/2);
			}else{
				add=height;
			}
			if(!up_down_flg){
				add=-add;
			}
			int bottom=TextComponentTool.getDocumentLength(_textArea);
			Point top_pos   =TextComponentTool.modelToView(_textArea,0).getLocation();
			Point bottom_pos=TextComponentTool.modelToView(_textArea,bottom).getLocation();
			//�J�[�\���ړ�
			int old_caret_pos=TextComponentTool.getCaretPosition(_textArea);
			Point old_pos=TextComponentTool.modelToView(_textArea,old_caret_pos).getLocation();
			Point pos=new Point(old_pos);
			pos.y+=add;
			if(pos.y<top_pos.y   )pos.y=top_pos.y;
			if(pos.y>bottom_pos.y)pos.y=bottom_pos.y;
			if(pos.y==old_pos.y)return;
			int caret_pos=TextComponentTool.viewToModel(_textArea,pos);
			Point new_pos=TextComponentTool.modelToView(_textArea,caret_pos).getLocation();
			TextComponentTool.setCaretPosition(_textArea,caret_pos);
			//�{���̈ړ��ʂ����߂�
			add=new_pos.y-old_pos.y;
			//�X�N���[���ړ�
			if(with_scroll){
				Point scroll_pos=vp.getViewPosition();
				scroll_pos.y+=add;
				if(scroll_pos.y<0)scroll_pos.y=0;
				vp.setViewPosition(scroll_pos);
			}
		}catch(Exception ex){}
	}
	//============================================
	// �܂�Ԃ����s��Ȃ�
	//============================================
	public static boolean NoWrapScrollableTracksViewportWidth(JComponent _textArea){
		if(_textArea instanceof NormalTextVram){
			//return ((NormalTextVram)_textArea).NoWrapScrollableTracksViewportWidth();
			return false;
		}
		if(!(_textArea instanceof JTextComponent))return false;
		JTextComponent textArea=(JTextComponent)_textArea;
		//return false;	// �܂�Ԃ����s��Ȃ�
		Object parent = textArea.getParent();
		if(parent instanceof JViewport){
			JViewport vp=(JViewport)parent;
			int w=vp.getWidth();							// �\���ł���͈�(���)
			TextUI ui=textArea.getUI();
			Dimension sz=ui.getPreferredSize(textArea);		// ���ۂ̕�����T�C�Y
			if (sz.width < w) {
				return true;
			}
		}
		return false;
	}
	//========================================================
	//DocumentFilter�̐ݒ�
	//========================================================
	public static void setDocumentFilter(JComponent _textArea,DocumentFilter filter){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		doc.setDocumentFilter(filter);
	}
	//========================================================
	//
	//========================================================
	public static void addDocumentListener(JComponent _textArea,DocumentListener listener){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		doc.addDocumentListener(listener);
	}
	//========================================================
	//
	//========================================================
	public static void addUndoableEditListener(JComponent _textArea,UndoableEditListener listener){
		if(_textArea instanceof NormalTextVram){
			//((NormalTextVram)_textArea).
			return;
		}
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		//AbstractDocument doc=(AbstractDocument)textArea.getDocument();
		Document doc=textArea.getDocument();
		doc.addUndoableEditListener(listener);
	}
	public static void setDragEnabled(JComponent _textArea,boolean f){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setDragEnabled(true);
	}
	public static void makeNormalTextAttr(JComponent _textArea,NormalTextAttr attr){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		if(doc instanceof StyledDocument){
			NormalTextAttrSetterStyledDocument.MakeAttr(doc,attr);
		}else if(doc instanceof PlainDocument){
			NormalTextAttrSetterPlainDocument.MakeAttr(doc,attr);
		}
	}
	public static void setCharacterAttributes(JComponent _textArea,AttributeSet attr){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextPane){
			((JTextPane)textArea).setCharacterAttributes(attr,true);
		}
	}
	public static void setLineWrap(JComponent _textArea,boolean n){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextArea){
			((JTextArea)textArea).setLineWrap(n);
		}
	}
	public static void setFont(JComponent _textArea,Font font){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		if(textArea instanceof JTextArea){
			((JTextArea)textArea).setFont(font);
		}
	}
	public static String getTextAreaTypeString(JComponent _textArea){
		if(_textArea instanceof JTextPane){
			return "JTextPane";
		}else if(_textArea instanceof JTextArea){
			return "JTextArea";
		}else if(_textArea instanceof NormalTextVram){
			return "VRAM";
		}
		return "---";
	}
	public static void setEditable(JComponent _textArea,boolean n){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		textArea.setEditable(n);
	}
	public static JComponent createTextComponent(int compo_type){
		switch(compo_type){
			case 0:return new JTextPane();
			case 1:return new JTextArea();
			case 2:return new NormalTextVram();
		}
		return null;
	}
	public static JComponent createNormalTextComponent(int compo_type){
		if(compo_type==0){
		//JTextPane
			return new NormalTextJTextPane();
		}else if(compo_type==1){
		//JTextArea
			return new NormalTextJTextArea();
		}else if(compo_type==2){
		//VRAM
			return new NormalTextVram();
		}
		return null;
	}
	public static void setTextAttr(JComponent _textArea,TextAttrBase attr){
		if(_textArea instanceof NormalTextFunc){
			((NormalTextFunc)_textArea).setNormalAttr(attr);
		}
	}
	public static void setNormalizeCaretPosFunc(JComponent _textArea,NormalizeCaretPosFunc func){
		if(_textArea instanceof NormalTextFunc){
			((NormalTextFunc)_textArea).setNormalizeCaretPosFunc(func);
		}
	}
	public static void initNormalTextJTextAreaDrawer(JComponent _textArea){
		if(_textArea instanceof NormalTextJTextArea){
			((NormalTextJTextArea)_textArea).initNormalTextJTextAreaDrawer();
		}
	}
	
	
	/*
		String ct;
		if(compo_type==0){
			ct=CT_JTextPane;
		}else{
			ct=CT_JTextArea;
		}
		if(ct.equals(CT_JTextPane)){
		//JTextPane
			g_textArea=new NormalTextJTextPane(textarea_callback);
		}else if(ct.equals(CT_JTextArea)){
		//JTextArea
			g_textArea=new NormalTextJTextArea(textarea_callback);
		}
	
	*/
	//============================================
	//�n�C���C�g
	//============================================
	public static void cutClipBoardHilighter(JComponent _textArea){
		NormalTextHighlight.CutClipBoardHilighter(_textArea);
	}
	public static void copyClipBoardHilighter(JComponent _textArea){
		NormalTextHighlight.CopyClipBoardHilighter(_textArea);
	}
	public static void paintTextHilighter(Graphics _g,JComponent _textArea,Color col,boolean block_select){
		NormalTextHighlight.paintTextHilighter(_g,_textArea,col,block_select);
	}
	//============================================
	//��ʃT�C�Y�𓾂�
	//============================================
	public static Dimension getJTextComponentSize(JComponent _textArea,JScrollPane scroll,Dimension size,boolean text_bottom_flg){
		if(_textArea instanceof NormalTextVram){
			return ((NormalTextVram)_textArea).getSize();
		}
		if(!(_textArea instanceof JTextComponent))return new Dimension(0,0);
		JTextComponent textArea=(JTextComponent)_textArea;
		//���s��EOF���������T�C�Y�����߂�
		size.width=TextComponentTool.getTextAreaWidth(_textArea,size.width,true,"[EOF]");
		//��ʃT�C�Y�̔�������
		if(text_bottom_flg){
			FontMetrics fm=TextComponentTool.getFontMetrics(_textArea);
			Font font=fm.getFont();
			int font_h=font.getSize();
			JViewport vp=scroll.getViewport();
			Rectangle bounds=vp.getViewRect();
			size.height+=(bounds.height-font_h);
		}
		return size;
	}
	//========================================================
	//OS��F10�@�\�����
	//========================================================
	//��
	public KeyStroke KeyStroke_F10=KeyStroke.getKeyStroke(KeyEvent.VK_F10,0);
	
	public static void RemoveF10(InputMap im){
		KeyStroke f10=KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
		while(true){
			if(im==null)break;
			if(im.get(f10)!=null){
				System.out.println("RemoveF10:found !!");
			}
			im.remove(f10);
			im=im.getParent();
		}
	}
	public static void replaceNoneF10(JComponent _textArea){
		_textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F10,0),"none");
	}
	//========================================================
	//========================================================
	/*
	static void RemoveF10Sub(JComponent cc){
		InputMap im=cc.getInputMap();
		while(true){
			if(im==null)break;
			im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
			im=im.getParent();
		}
		//Object key=im.get(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		//System.out.println(""+key);
	}
	*/
	//========================================================
	//�e�L�X�g�F�ς�
	//========================================================
	static class TextColorComposite implements Composite{
		Color col;
		public TextColorComposite(Color _col){
			col=_col;
		}
		public CompositeContext createContext(ColorModel srcColorModel,ColorModel dstColorModel,RenderingHints hints){
			return new TextBlendingContext(col);
		}
		//�F�ς�
		static class TextBlendingContext implements CompositeContext{
			int col;
			public TextBlendingContext(Color _col){
				col=_col.getRGB();
			}
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut){
				if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
					dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
					dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
					throw new IllegalStateException(
							"Source and destination must store pixels as INT.");
				}
				int width =dstIn.getWidth();
				int height=dstIn.getHeight();
				int size=width*height;
				int[] dstPixels=new int[size];
				dstIn.getDataElements(0,0,width,height,dstPixels);
				for(int i=0;i<size;i++){
					int a=dstPixels[i] & 0xff000000;
					int c=dstPixels[i] & 0x00ffffff;
					if(c!=0)c=col;
					dstPixels[i]=c | a;
				}
				dstOut.setDataElements(0,0,width,height,dstPixels);
			}
			public void dispose(){}
		}
	}
	public static Composite createTextColorComposite(Color col){
		return new TextColorComposite(col);
	}
	//========================================================
	//Font
	//========================================================
	/*
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
	public static FontMetrics createFontMetricsWrapper_ExGap(FontMetrics fm,int gap){
		return new FontMetricsWrapper_ExGap(fm,0);
	}
	*/
	//========================================================
	//
	//========================================================
	private static MutableAttributeSet getColorAttr(AttributeSet attr,Color col){
		MutableAttributeSet new_attr=new SimpleAttributeSet(attr);
		StyleConstants.setForeground(new_attr,col);
		return new_attr;
	}
	public static MutableAttributeSet getColorAttr(String font_name,int font_size,Color back_col,Color col){
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attr,font_name);
		StyleConstants.setFontSize(attr,font_size);
		StyleConstants.setForeground(attr,Color.BLACK);
		StyleConstants.setBackground(attr,back_col);
		return getColorAttr(attr,col);
	}
	public static MutableAttributeSet getColorAttr(String font_name,int font_size,CVECTOR back_col,CVECTOR col){
		return getColorAttr(font_name,font_size,ConvAWT.CVEC2Color(back_col),ConvAWT.CVEC2Color(col));
	}
	//========================================================
	//
	//========================================================
	public static void printInputMap(InputMap im){
		KeyStroke[] keys=im.allKeys();
		for(int i=0;i<keys.length;i++){
			System.out.println("["+i+"]:key("+keys[i]+"):act("+im.get(keys[i])+")");
		}
		
	}
	//========================================================
	//
	//========================================================
	public static JScrollPane getScrollPane(Component _c){
		if(_c==null)return null;
		Component c=_c;
		while(true){
			Container p=c.getParent();
			if(p==null)break;
			if(p instanceof JScrollPane){
				JViewport vp=((JScrollPane)p).getViewport();
				if(vp!=null){
					if(vp.getView()==_c)return (JScrollPane)p;
				}
				break;
			}
			c=p;
		}
		return null;
	}
	public static JViewport getViewport(Component _c){
		if(_c==null)return null;
		Component c=_c;
		while(true){
			Container p=c.getParent();
			if(p==null)break;
			if(p instanceof JViewport){
				return (JViewport)p;
			}
			c=p;
		}
		return null;
	}
}
