/******************************************************************************
;	VRAM版テキストエリア
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.vram;

import java.lang.CharSequence;
import java.lang.StringBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Toolkit;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.*;
//import java.awt.InputContext;
import java.awt.im.InputContext;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JViewport;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.caret.*;

public class NormalTextVram extends JComponent implements NormalTextFunc{
	private int caret_pos=0;
	private int mark_pos=0;
	private CharSequence text;
	private boolean text_update_flg=false;
	private TextColProp cols_prop;
	private TextAttrBase norm_attr=new TextAttrBase();
	private NormalizeCaretPosFunc normalizecaretpos_func;
	private int[] cr_tbl=new int[20000];
	private int cr_num=0;
	private TextBounds comment_bounds;
	private TextBounds line_comment_bounds;
	private TextBounds str_bounds;
	private int tab_size=4;
	private boolean bom_flg=false;
	private boolean overtype_mode=false;
//	private boolean overtype_mode=true;
	private boolean caret_xor=true;
	private Color current_col=Color.WHITE;
	private Color fg_col=Color.WHITE;
	private Color bg_col=Color.BLACK;
	private Color caret_col=Color.RED;
	private Color select_col;
	private Color selecttext_col;
	private Color disabeltext_col;
	private Color eof_col=Color.MAGENTA;
	private Color hanspace_col=Color.CYAN;
	private Color zenspace_col=Color.CYAN;
	private Color cr_col=Color.CYAN;
	private Color tab_col=Color.CYAN;
	private boolean draw_hanspace=true;
	private boolean draw_zenspace=true;
	private boolean draw_eof=true;
	private boolean draw_tab=true;
	private boolean draw_cr=true;
	private boolean scroll_page_bottom_flg=true;
	private Dimension text_size;
	private int font_h;
	private int font_w;
	private CaretFilter caret_filter=null;
	private ArrayList<CaretListener> caret_listeners=new ArrayList<CaretListener>();
	private UndoBuffer undobuffer=new UndoBuffer();
	//
	public static interface UndoAct{
		public void undo();
		public void redo();
	}
	public static class UndoAct_PutChar implements UndoAct{
		public UndoAct_PutChar(int c){
			
		}
		public void undo(){
			
		}
		public void redo(){
			
		}
	}
	public static class UndoAct_PutString implements UndoAct{
		public UndoAct_PutString(String m){
			
		}
		public void undo(){
			
		}
		public void redo(){
			
		}
	}
	
	public static class UndoBuffer{
		ArrayList<UndoAct> buff=new ArrayList<UndoAct>();
		public void pushUndoBuffer(UndoAct act){
System.out.println("pushUndoBUffer:"+act);
			buff.add(act);
		}
		public void undo(){
			if(!canUndo())return;
System.out.println("undo");
		}
		public void redo(){
			if(!canRedo())return;
System.out.println("redo");
			
		}
		public boolean canUndo(){
			//if(buff.size()<=0)return false;
			return true;
		}
		public boolean canRedo(){
			//if(buff.size()<=0)return false;
			return true;
		}
	}
	//
	static class TextBounds{
		public int[] bounds=new int[10000];
		public int num=0;
		//
		public void clear(){
			num=0;
		}
		public void add(int start,int end){
			if(num*2>bounds.length)return;
			bounds[num*2+0]=start;
			bounds[num*2+1]=end;
			num++;
		}
		public boolean isInside(int p){	
			/*
			for(int i=0;i<num;i++){
				int s=bounds[i*2+0];
				int e=bounds[i*2+1];
				if(s>=p && p<=e)return true;
			}
			*/
			int top=0;
			int bottom=num-1;
			while(true){
				int h=(top+bottom) >> 1;
				if(p<bounds[0]){
					bottom=h-1;
				}else if(p>=bounds[1]){
					top=h+1;
				}else{
					break;
				}
				if(top==bottom)return false;
			}
			
			
			return false;
		}
		
		
	}
	//※2分探索書く
	static int searchBoundsBinTree(int[] tbl,int tbl_num,int n){
		if(tbl==null)return -1;
		if(tbl_num==0)return -1;
		if(tbl_num==1)return 0;
		int t_min=0;
		int t_max=tbl_num-1;
		while(true){
			int min=tbl[t_min*2+0];
			int max=tbl[t_max*2+1];
			if(n<min)return -1;
			if(n>max)return -1;
			int t_cen=(t_min+t_max)/2;
			min=tbl[t_cen*2+0];
			max=tbl[t_cen*2+1];
			if(n<min){
				t_max=t_cen;
			}else if(n>max){
				t_min=t_cen;
			}else{
				return t_cen;
			}
			if(t_min==t_max)return -1;
		}
	}
	//※2分探索書く
	static int searchBinTree(int[] tbl,int tbl_num,int n){
		if(tbl==null)return -1;
		if(tbl_num==0)return -1;
		if(tbl_num==1)return 0;
		int t_min=0;
		int t_max=tbl_num-1;
		while(true){
			int min=tbl[t_min];
			int max=tbl[t_max];
			if(n<min)return -1;
			if(n>max)return -1;
			if(n==min)return t_min;
			if(n==max)return t_max;
			int t_cen=(t_min+t_max)/2;
			int n_cen=tbl[t_cen];
			if(n==n_cen)return t_cen;
			if(t_cen==t_min || t_cen==t_max)return -1;
			//
			if(n<n_cen){
				t_max=t_cen;
			}else{
				t_min=t_cen;
			}
			if(t_min==t_max){
				return -1;
			}
		}
		
	}
	
	
	static class TextColProp_Vram implements TextColProp{
		private int[] cols=new int[65536];
		TextColProp_Vram(){
		}
		public void setColor(int i,int col){
			if(i<0 || i>=cols.length)return;
			cols[i]=col;
		}
		public int getColor(int i){
			if(i<0 || i>=cols.length)return 0xffffffff;
			return cols[i];
		}
		public void fill(){
			for(int i=0;i<cols.length;i++)cols[i]=0xffffffff;
		}
	}
	public NormalTextVram(){
		setOpaque(false);
		setFocusTraversalKeysEnabled(false);
		caret_pos=0;
		mark_pos =0;
		font_h=16;
		font_w=font_h/2;
		text_update_flg=true;
		//
		cols_prop=new TextColProp_Vram();
		((TextColProp_Vram)cols_prop).fill();
		//for(int i=0;i<cols.length;i++)cols[i]=Color.WHITE;
		//for(int i=0;i<cols.length;i++)cols[i]=0xffffffff;
		removeTabFocus();
		initNormalTextJTextAreaDrawer();
		initCaretBlink();
		initMouseEvent();
		initKeyboardEvent();
		initActionMap();
		initInputMap();
		calcLine();
		//
		//TextComponentTool.printInputMap(getInputMap());
//printJTextAreaAction()
		/*
		Component c=new javax.swing.JTextArea();
		add(c);
		c.enableInputMethods(true);
		InputContext ic=c.getInputContext();
System.out.println("InputContext="+ic);
		*/
		
		/*
		enableEvents(AWTEvent.INPUT_METHOD_EVENT_MASK);
		enableInputMethods(true);
		
		addInputMethodListener(new InputMethodListener(){
			public void caretPositionChanged(InputMethodEvent event){
				System.out.println("caretPositionChanged:"+event);
			}
			public void inputMethodTextChanged(InputMethodEvent event){
				System.out.println("inputMethodTextChanged:"+event);
			}
		});
		*/
	}
	/*
	protected void processEvent(AWTEvent e){
System.out.println("processEvent:"+e);
	}
	*/
	protected void processInputMethodEvent(InputMethodEvent e){
System.out.println("processInputMethodEvent:"+e);
	}
	public void setNormalizeCaretPosFunc(NormalizeCaretPosFunc func){
		normalizecaretpos_func=func;
	}
	public void setNormalAttr(TextAttrBase _norm_attr){
System.out.println("setNormalAttr");
		norm_attr=_norm_attr;
		//
		feedbackNormalAttr();
	}
	public void feedbackNormalAttr(){
		if(norm_attr!=null){
			Font font=norm_attr.getFont();
			setFont(font);
			bg_col    =ConvAWT.CVEC2Color(norm_attr.back_col);
			fg_col    =ConvAWT.CVEC2Color(norm_attr.normal_col);
			caret_col =ConvAWT.CVEC2Color(norm_attr.cursor_col);
			select_col=ConvAWT.CVEC2Color(norm_attr.select_col);
			tab_size  =norm_attr.tab_size;
			//
			draw_hanspace=norm_attr.draw_space;
			draw_zenspace=norm_attr.draw_zenspace;
			draw_tab     =norm_attr.draw_tab;
			draw_cr      =norm_attr.draw_cr;
			draw_eof     =norm_attr.draw_eof;
			tab_col     =ConvAWT.CVEC2Color(norm_attr.tab_col);
			cr_col      =ConvAWT.CVEC2Color(norm_attr.cr_col);
			eof_col     =ConvAWT.CVEC2Color(norm_attr.eof_col);
			hanspace_col=ConvAWT.CVEC2Color(norm_attr.space_col);
			zenspace_col=ConvAWT.CVEC2Color(norm_attr.zenspace_col);
		}
		//
		calcFontSize();
	}
	private void calcFontSize(){
		Font font=getFont();
		FontMetrics fm=getFontMetrics(getFont());
		font_h=font.getSize();
		font_w=fm.charWidth('M');
	}
	static class CharSequence_Vram implements CharSequence{
		//char buff[];
		//int cols[];
		int buff[];
		int length;
		int start;
		int end;
		
		public CharSequence_Vram(){
			
		}
		public CharSequence_Vram findIndex(int index){
			return null;
		}
		public int codePointAt(int index){
			return 0;
		}
		
		public char charAt(int index){
			CharSequence_Vram cs=findIndex(index);
			//int c=cs.
			int c=buff[index];
			return (char)c;
		}
		public int length(){
			return length;
		}
		public CharSequence subSequence(int start, int end){
			CharSequence_Vram dst=new CharSequence_Vram();
			return dst;
		}
		public String toString(){
			return new String(buff,start,end);
		}
		//
		public void insert(int pos,CharSequence m,int col){
			
		}
		public void remove(int pos,int bottom){
			
		}
	};
	
	
	
	public void setText(CharSequence m){
		text=m;
		text_update_flg=true;
		//calcLine();
		calcLine2();
		calcSize();
		//
		caret_pos=0;
		mark_pos=0;
	}
	private void calcLine(){
		cr_num=0;
		int c_width=0;
		int c_height=0;
		int bottom=0;
		if(text!=null){
			int len=getDocumentLength();
			int ci=0;
			StringBuffer t=new StringBuffer(text);
			while(true){
				if(ci>=len)break;
				//int i=text.indexOf('\n',ci);
				//int i=Character.indexOf(text,'\n',ci);
				int i=t.indexOf("\n",ci);
				if(i<0)break;
//System.out.println("ci["+cr_num+"]="+ci+"->"+i);
				int ww=i-ci;
				if(ww>c_width)c_width=ww;
				cr_tbl[cr_num]=i+1;
				cr_num++;
				ci=i+1;
			}
			bottom=len;
		}
		cr_tbl[cr_num]=bottom;
		cr_num++;
		c_height=cr_num;
		int c_width_min =2;
		int c_height_min=1;
		if(c_width <c_width_min )c_width =c_width_min;
		if(c_height<c_height_min)c_height=c_height_min;
		int width =font_w*c_width;
		int height=font_h*c_height;
		Dimension size=new Dimension(width,height);
//System.out.println("cr_num:"+cr_num);
//System.out.println("size:"+size);
		
		//size.width=TextComponentTool.GetTextAreaWidth(textArea,size.width,true,"[EOF]");
		JViewport viewport=getViewport(this);
		if(viewport!=null){
			Dimension vs=viewport.getViewSize();
			int font_h2=font_h;//+4;
			size.height+=(vs.height-font_h2);
		}
		
		setPreferredSize(size);
		setSize(size);
		
		//testFindLine();
		
		//text_size=size;
		//
//		printLines();
	}
	private void calcLine2(){
		cr_num=0;
		int bottom=0;
		if(text!=null){
			int len=getDocumentLength();
			int ci=0;
			StringBuffer t=new StringBuffer(text);
			while(true){
				if(ci>=len)break;
				int i=t.indexOf("\n",ci);
				if(i<0)break;
				cr_tbl[cr_num]=i+1;
				cr_num++;
				ci=i+1;
			}
			bottom=len;
		}
		cr_tbl[cr_num]=bottom;
		cr_num++;
	}
	private void calcSize(){
		int c_width=0;
		int c_height=0;
		int top=0;
		for(int i=0;i<cr_num;i++){
			int bottom=cr_tbl[i];
			int tw=0;
			for(int j=top;j<bottom;){
				int c=Character.codePointAt(text,j);
				j+=Character.charCount(c);
				tw+=isHankaku(c)?1:2;
			}
			if(tw>c_width)c_width=tw;
			top=bottom;
		}
		c_height=cr_num;
		int c_width_min =2;
		int c_height_min=1;
		if(c_width <c_width_min )c_width =c_width_min;
		if(c_height<c_height_min)c_height=c_height_min;
		int width =font_w*c_width;
		int height=font_h*c_height;
		Dimension size=new Dimension(width,height);
		//size.width=TextComponentTool.GetTextAreaWidth(textArea,size.width,true,"[EOF]");
		//スクロール時、行末が一番上までくる
		if(scroll_page_bottom_flg){
			JViewport viewport=getViewport(this);
			if(viewport!=null){
				Dimension vs=viewport.getViewSize();
				int font_h2=font_h;//+4;
				size.height+=(vs.height-font_h2);
			}
		}
		
		setPreferredSize(size);
		setSize(size);
	}
	/*
	@Override
	public Dimension getPreferredSize(){
		Dimension size=super.getPreferredSize();
		JScrollPane scroll=TextComponentTool.getScrollPane(this);
		boolean text_bottom_flg=true;
		return TextComponentTool.getJTextComponentSize(this,scroll,size,text_bottom_flg);
	}
	*/
	private void printLines(){
		System.out.println(String.format("line_num=%d:%s",cr_num,text));
		for(int i=0;i<cr_num;i++){
			//int[] bounds=getLineBounds(i);
			//System.out.println(String.format("line[%4d]:(%d-%d)",i,bounds[0],bounds[1]));
			System.out.println(String.format("line[%4d]:(%d)",i,cr_tbl[i]));
		}
	}
	//
	static int findLine1(int[] cr_tbl,int cr_num,int pos){
		if(pos<=0)return 0;
		if(cr_num<=1)return 0;
		int bottom=cr_num-1;
		for(int i=0;i<cr_num;i++){
			if(pos<cr_tbl[i]){
				return i;
			}
		}
		return bottom;
	}
	static int findLine2(int[] cr_tbl,int cr_num,int pos){
		if(pos<=0)return 0;
		if(cr_num<=1)return 0;
		int top   =0;
		int bottom=cr_num-1;
		if(pos< cr_tbl[top])return top;
		if(pos>=cr_tbl[bottom])return bottom;
		while(true){
//System.out.println(String.format("top(%d),bottom(%d)",top,bottom));
			int h=(top+bottom) >> 1;
			int n=cr_tbl[h];
			if(pos<n){
				bottom=h;
			}else if(pos>=n){
				top=h;
			}
			if(top==(bottom-1))return bottom;
			//if(top>=bottom)return top;
		}
	}
	void testFindLine(){
System.out.println("testFindLine");
		int len=getDocumentLength();
		for(int i=0;i<len;i++){
			int pos=i;
			int r1=findLine1(cr_tbl,cr_num,pos);
			//int r2=-1;//findLine2(cr_tbl,cr_num,pos);
			int r2=findLine2(cr_tbl,cr_num,pos);
			if(r1!=r2){
System.out.println(String.format("[%d],r1(%d),r2(%d)",i,r1,r2));
			}
		}
	}
	
	
	
	//位置の行
	public int getLineFromPos(int pos){
		int len=getDocumentLength();
		if(len==0)return 0;
		/*
		if(pos<0){
			return 0;
		}
		for(int i=0;i<cr_num;i++){
			if(pos<cr_tbl[i]){
				return i;
			}
		}
		int i=cr_num-1;
		if(i<0)i=0;
		return i;
		*/
		return findLine1(cr_tbl,cr_num,pos);
		//return findLine2(cr_tbl,cr_num,pos);
	}
	//位置の桁
	public int getColumnFromPos(int pos){
		int len=getDocumentLength();
		if(len==0)return 0;
		int line=getLineFromPos(pos);
		int line_start=getLineTop(line);
		return pos-line_start;
	}
	//ラインの先頭
	public int getLineTop(int line){
		int len=getDocumentLength();
		if(len==0)return 0;
		if(line<=0)return 0;
		if(cr_num==0)return 0;
		if(line>(cr_num-1))line=cr_num-1;
		return cr_tbl[line-1];
	}
	//ラインの最後
	public int getLineBottom(int line){
		int len=getDocumentLength();
		if(len==0)return 0;
		if(cr_num==0)return 0;
		if(line>(cr_num-1))line=cr_num-1;
		return cr_tbl[line];
	}
	//ラインの最後(改行を含めない)
	public int getLineBottomNoCR(int line){	
		int len=getDocumentLength();
		if(len<=0)return 0;
		int ret=getLineBottom(line);
		if(ret!=len)ret--;
		return ret;
	}
	//ラインの範囲
	public int[] getLineBounds(int line){
		int len=getDocumentLength();
		if(len==0)return new int[]{0,0};
		return new int[]{getLineTop(line),getLineBottom(line)};
	}
	//ラインの範囲(改行を含めない)
	public int[] getLineBoundsNoCR(int line){
		int len=getDocumentLength();
		if(len==0)return new int[]{0,0};
		return new int[]{getLineTop(line),getLineBottomNoCR(line)};
	}
	//テキストの獲得
	public CharSequence getText(){
		return text;
	}
	//テキストの獲得
	public CharSequence getText(int pos,int _len) throws Exception {
		int len=getDocumentLength();
		if(len==0)return null;
		int bottom=pos+_len;
		if(bottom>len)bottom=len;
		//return text.substring(pos,bottom);
		return text.subSequence(pos,bottom);
	}
	//指定行のテキストの獲得
	public CharSequence getLineText(int line) throws Exception {
		int len=getDocumentLength();
		if(len==0)return null;
		int[] bounds=getLineBounds(line);
		if(bounds==null)return null;
		//return text.substring(bounds[0],bounds[1]);
		return text.subSequence(bounds[0],bounds[1]);
	}
	//テキストの長さ
	public int getDocumentLength(){
		if(text==null)return 0;
		return text.length();
	}
	//行数
	public int getLineNum(){
		int len=getDocumentLength();
		if(len==0)return 0;
		//if(cr_num<=0)return 1;
		return cr_num;
	}
	public FontMetrics getFontMetrics(){
		return getFontMetrics(getFont());
	}
	//キャレット位置獲得
	public int getCaretPosition(){
		return caret_pos;
	}
	//キャレット位置設定
	public void setCaretPosition(int n){
		caret_pos=n;
	}
	//キャレットの行
	public int getCaretLine(){
		return getLineFromPos(caret_pos);
	}
	//キャレットの桁
	public int getCaretColumn(){
		return getColumnFromPos(caret_pos);
	}
	//マーク位置獲得
	public int getMarkPosition(){
		return mark_pos;
	}
	//マーク位置設定
	public void setMarkPosition(int n){
		mark_pos=n;
	}
	private int getSelectionStart(){
		if(caret_pos<mark_pos){
			return caret_pos;
		}else{
			return mark_pos;
		}
	}
	private int getSelectionEnd(){
		if(caret_pos<mark_pos){
			return mark_pos;
		}else{
			return caret_pos;
		}
	}
	public int[] getSelectionBounds(){
		return new int[]{getSelectionStart(),getSelectionEnd()};
	}
	public CharSequence getSelectionString(){
		if(!isSelected())return null;
		int[] bounds=getSelectionBounds();
		int start=bounds[0];
		int len  =bounds[1]-start;
		if(len<=0)return null;
		try{
			return getText(start,len);
		}catch(Exception ex){
			return null;
		}
	}
	//選択されている?
	public boolean isSelected(){
		return (caret_pos!=mark_pos);
	}
	//全て選択
	public void selectAll(){
		select(0,getDocumentLength());
	}
	//選択クリア
	public void clearSelect(){
		select(caret_pos,caret_pos);
	}
	//選択する
	public void select(int start,int end){
		mark_pos=start;
		caret_pos=end;
		repaint();
		sendCaretEvent();
	}
	//行、桁から位置を求める
	public int getPosAtColmnLine(int colm,int line){
		int[] bounds=getLineBounds(line);
		int pos=bounds[0]+colm;
		int bottom=bounds[1]-1;
		if(bottom<bounds[0])bottom=bounds[0];
		
		if(pos>bottom)pos=bottom;
		int len=getDocumentLength();
		if(pos<0)pos=0;
		if(pos>len)pos=len;
		return pos;
	}
	//画像Yから行を求める
	public int getLineAtPointY(int y){
		if(cr_num<=0)return 0;
		if(y<0)return 0;
		Dimension size=getSize();
		int line=y/font_h;
		if(line>=cr_num)return cr_num-1;
		return line;
	}
	//画像位置から位置を求める
	public int getPosAtPoint(int x,int y){
		int line=getLineAtPointY(y);
		int[] bounds=getLineBounds(line);
		int pos=0;
		if(bounds!=null){
//			int colm=x/font_w;
			int colm=getColumnAtPointX(line,x);
			pos=bounds[0]+colm;
			if(pos>=bounds[1])pos=bounds[1];
		}
		return pos;
	}
	//画像位置から位置を求める
	public int viewToModel(Point pos){
		return getPosAtPoint(pos.x,pos.y);
	}
	//画像Xから桁を求める
	/*
	public int getColumnAtPointX(int line,int x){
		int colm=x/font_w;
		return colm;
	}
	*/
	//
	public int getColumnAtPointX(int line,int x){
		if(x<=0){
			System.out.println("getColumnAtPointX_01:x="+x);
			return 0;
		}
		//
		int len=getDocumentLength();
		if(len==0){
			System.out.println("getColumnAtPointX_02");
			return 0;
		}
		int[] line_bounds=getLineBounds(line);
		int start_pos=line_bounds[0];
		int end_pos  =line_bounds[1];
		int line_len=end_pos-start_pos;
		if(line_len<=0){
			System.out.println("getColumnAtPointX_03");
			return 0;
		}
		//
		CharSequence str_m=null;
		try{
			str_m=getText(start_pos,line_len);
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
		//
		if(str_m==null){
			System.out.println("getColumnAtPointX_04");
			return 0;
		}
		//int str_len=str_m.codePointCount(0,line_len);
		int str_len=Character.codePointCount(str_m,0,line_len);
		if(str_len<1){
			System.out.println("getColumnAtPointX_05");
			return 0;
		}
		//
		//int colm=0;
		int ci=0;
		int tx=0;
		while(true){
			//tw=1;
			if(ci>=line_len)break;
			int c=Character.codePointAt(str_m,ci);
			int tw=isHankaku(c)?1:2;
			//if(ci>=colm)break;
			//ci+=Character.charCount(c);
			if(c!=0x09){
				tx+=tw;
			}else{
				tx+=tab_size;
				int d=tx % tab_size;
				tx-=d;
			}
			if(x<tx)return ci;
			ci+=Character.charCount(c);
		}
		return ci;
		
		
//		int colm=x/font_w;
//		return colm;
	}
	//
	int[] getTxWAtLineColmn(int colm,int line){
		int len=getDocumentLength();
		if(len==0)return new int[]{0,1};
		int[] line_bounds=getLineBounds(line);
		int start_pos=line_bounds[0];
		int end_pos  =line_bounds[1];
		int line_len=end_pos-start_pos;
		if(line_len<=0)return new int[]{0,1};
		//
		CharSequence str_m=null;
		try{
			str_m=getText(start_pos,line_len);
		}catch(Exception ex){
			ex.printStackTrace();
			return new int[]{0,1};
		}
		if(str_m==null)return new int[]{0,1};
		//int str_len=str_m.codePointCount(0,line_len);
		int str_len=Character.codePointCount(str_m,0,line_len);
		if(str_len<1)return new int[]{0,1};
		int ci=0;
		int tx=0;
		int tw=0;
		while(true){
			tw=1;
			if(ci>=line_len)break;
			int c=Character.codePointAt(str_m,ci);
			tw=isHankaku(c)?1:2;
			if(ci>=colm)break;
			ci+=Character.charCount(c);
			if(c!=0x09){
				tx+=tw;
			}else{
				tx+=tab_size;
				int d=tx % tab_size;
				tx-=d;
			}
		}
		return new int[]{tx,tw};
	}
	//位置から画像矩形を求める
	public Rectangle modelToView(int pos) throws Exception {
		int line=0;
		//int colm=0;
		int tx=0;
		int tw=1;
		int len=getDocumentLength();
		if(len>0){
			line=getLineFromPos(pos);
			int colm=getColumnFromPos(pos);
			
			int[] txw=getTxWAtLineColmn(colm,line);
			
			//tx=colm;
			//tw=1;
			tx=txw[0];
			tw=txw[1];
		}
		Rectangle r=new Rectangle(tx*font_w,line*font_h,tw*font_w,font_h);
		return r;
	}
	/*
	public Color[] getColors(){
		return cols;
	}
	*/
	public TextColProp getColorsProp(){
		return cols_prop;
	}
	public Dimension getTextSize(){
		return getSize();
	}
	//タブサイズ
	public void setTabSize(int n){
		tab_size=n;
	}
	//クリップボードから文字を得る
	private static String getClipboardString(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		try{
			return (String)clip.getData(DataFlavor.stringFlavor);
		}catch(Exception e){
			return null;
		}
	}
	//クリップボードへ文字を送る
	private static void putClipboardString(CharSequence m){
		if(m==null)return;
		if(m.length()==0)return;
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		clip.setContents(new StringSelection(m.toString()),null);
	}
	//カット
	public void cut(){
		if(!isSelected())return;
		//選択部分をコピー
		CharSequence m=getSelectionString();
		putClipboardString(m);
		//選択部分をカット
		removeText();
	}
	//コピー
	public void copy(){
		if(!isSelected())return;
		//選択部分をコピー
		CharSequence m=getSelectionString();
		putClipboardString(m);
	}
	//ペースト
	public void paste(){
		String m=getClipboardString();
		if(m==null)return;
		//挿入
		insertText(m);
	}
	public boolean canPaste(){
		return (getClipboardString()!=null);
	}
	//VZタイプカット(開始位置と終了位置が同じなら1行カット、それ以外は通常のカット)
	public void vzCut(){
		if(!isSelected()){
		//開始位置と終了位置が同じなら1行カット
			lineCut();
		}else{
		//それ以外は通常のカット
			cut();
		}
	}
	//一行カット
	public void lineCut(){
		int line=getCaretLine();
		int[] bounds=getLineBounds(line);
		select(bounds[0],bounds[1]);
		removeText();
	}
	//挿入モードのフリップ
	public void flipInsertMode(){
		overtype_mode=!overtype_mode;
		caret_xor=!caret_xor;
		repaint();
	}
	//上書きモード?
	public boolean isOvertypeMode(){
		return overtype_mode;
	}
	public boolean insertChar(CharSequence m){
		boolean r=insertText(m);
//		if(!r)return false;
		caret_pos++;
		mark_pos=caret_pos;
		caret_xor=true;
		repaint();
		sendCaretEvent();
		return true;
	}
	//文字列挿入
	public boolean insertText(CharSequence m){
		return insertText(m,caret_pos);
	}
	//文字列挿入
	public boolean insertText(CharSequence m,int pos){
		if(m==null)return false;
		if(m.length()==0)return false;
		text_update_flg=true;
		int col=current_col.getRGB();
		int m_len =m.length();
		if(text==null){
			text=m;
			//for(int i=0;i<m_len;i++)cols[i]=col;
			for(int i=0;i<m_len;i++){
				cols_prop.setColor(i,col);
			}
		}else{
			int text_len=text.length();
			int m0_len=pos;
			int m1_len=text_len-pos;
			int old_bottom=text_len;
			for(int i=old_bottom-1;i>=m0_len;i--){
//				cols[i+m_len]=cols[i];
			}
			for(int i=0;i<m_len;i++){
				cols_prop.setColor(pos+i,col);
			}
//			CharSequence m0=text.substring(0,pos);
//			CharSequence m1=text.substring(pos);
			CharSequence m0=text.subSequence(0,pos);
			CharSequence m1=text.subSequence(pos,text_len);
//			text=m0+m+m1;
			StringBuffer t=new StringBuffer();
			t.append(m0);
			t.append(m);
			t.append(m1);
			text=t;
		}
		calcLine();
		return true;
	}
	//文字列削除
	public boolean removeText(){
		int[] bounds=getSelectionBounds();
		int start=bounds[0];
		int len  =bounds[1]-start;
		if(len<=0)return false;
		boolean r=removeText(start,len);
		caret_pos=start;
		mark_pos=caret_pos;
		return r;
	}
	//文字列削除
	public boolean removeTextStartEnd(int start,int end){
		if(start==end)return false;
		if(start>end){
			int t=start;
			start=end;
			end=t;
		}
		int _len=end-start;
		return removeText(start,_len);
	}
	//文字列削除
	public boolean removeText(int start,int _len){
		int len=getDocumentLength();
		if(len==0)return false;
		if(start>=len)return false;
		text_update_flg=true;
		CharSequence old_text=text;
		int bottom=start+_len;
		//CharSequence m0=text.substring(0,start);
		CharSequence m0=text.subSequence(0,start);
		if(bottom>=len){
			text=m0;
		}else{
			//CharSequence m1=text.substring(bottom);
			CharSequence m1=text.subSequence(bottom,len);
			//text=m0+m1;
			StringBuffer t=new StringBuffer();
			t.append(m0);
			t.append(m1);
			text=t;
		}
		calcLine();
		return true;
	}
	public void setCurrentColor(Color col){
		current_col=col;
	}
	public void setForeground(Object col){
		if(col instanceof Color){
			fg_col=(Color)col;
		}
	}
	public void setCaretColor(Color col){
		caret_col=col;
	}
	public void setSelectionColor(Color col){
		select_col=col;
	}
	public void setSelectedTextColor(Color col){
		selecttext_col=col;
	}
	public void setDisabledTextColor(Color col){
		disabeltext_col=col;
	}
	public void insertAutoTab(){
	}
	public void replaceSelectionText(CharSequence text){
		removeText();
		insertText(text);
	}
	public boolean getBomFlg(){
		return bom_flg;
	}
	public void setBomFlg(boolean n){
		bom_flg=n;
	}
	public void pushUndoBuffer(UndoAct act){
		undobuffer.pushUndoBuffer(act);
	}
	public void undo(){
		undobuffer.undo();
	}
	public void redo(){
		undobuffer.redo();
	}
	public boolean canUndo(){
		return undobuffer.canUndo();
	}
	public boolean canRedo(){
		return undobuffer.canRedo();
	}
	public void addCaretListener(CaretListener listener){
		caret_listeners.add(listener);
	}
	public CaretEvent createCaretEvent(){
		return new CaretEvent(this){
			public int getDot(){
				return caret_pos;
			}
			public int getMark(){
				return mark_pos;
			}
		};
	}
	static JViewport getViewport(Component _c){
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
	public void sendCaretEventOnly(){
		sendCaretEvent(createCaretEvent());
	}
	public void sendCaretEvent(){
		sendCaretEventOnly();
		scrollCaret();
	}
	public void sendCaretEvent(CaretEvent ev){
		for(int i=0;i<caret_listeners.size();i++){
			CaretListener listener=caret_listeners.get(i);
			if(listener==null)continue;
			listener.caretUpdate(ev);
		}
	}
	public void scrollCaret(){
		JViewport viewport=getViewport(this);
		if(viewport!=null){
			Rectangle vr=viewport.getViewRect();
			try{
				Rectangle cr=modelToView(caret_pos);
				if(!vr.contains(cr)){
					int vx0=vr.x;
					int vx1=vr.x+vr.width;
					int cx0=cr.x;
					int cx1=cr.x+cr.width;
					int vy0=vr.y;
					int vy1=vr.y+vr.height;
					int cy0=cr.y;
					int cy1=cr.y+cr.height;
					if(cx1>vx1)vr.x=cx1-vr.width;
					if(cx0<vx0)vr.x=cr.x;
					if(cy1>vy1)vr.y=cy1-vr.height;
					if(cy0<vy0)vr.y=cr.y;
					viewport.setViewPosition(vr.getLocation());
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public void halfPageUpDown(int page_size_flg,boolean up_down_flg){
		JViewport viewport=getViewport(this);
		if(viewport==null)return;
		Rectangle vr=viewport.getViewRect();
		int height=vr.height;
		int add=0;
		if(page_size_flg==0){
			add=(height/2);
		}else{
			add=height;
		}
		if(!up_down_flg){
			add=-add;
		}
		int bottom=getDocumentLength();
		try{
			Point top_pos   =modelToView(0).getLocation();
			Point bottom_pos=modelToView(bottom).getLocation();
			Point old_pos   =modelToView(caret_pos).getLocation();
			Point pos=new Point(old_pos);
			pos.y+=add;
			if(pos.y<top_pos.y   )pos.y=top_pos.y;
			if(pos.y>bottom_pos.y)pos.y=bottom_pos.y;
			if(pos.y==old_pos.y)return;
			int new_caret_pos=viewToModel(pos);
			Point new_pos=modelToView(new_caret_pos).getLocation();
			caret_pos=new_caret_pos;
			//本当の移動量を求める
			add=new_pos.y-old_pos.y;
			//スクロール移動
			boolean with_scroll=true;
			if(with_scroll){
				Point scroll_pos=viewport.getViewPosition();
				scroll_pos.y+=add;
				if(scroll_pos.y<0)scroll_pos.y=0;
				viewport.setViewPosition(scroll_pos);
			}
			//updateCaretPos();
			//
			mark_pos=caret_pos;
			caret_xor=true;
			repaint();
			sendCaretEventOnly();
			//updateCaretPos(true);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	public void setCaretFilter(CaretFilter _caret_filter){
		caret_filter=_caret_filter;
	}
	void execCaretFilter(){
		if(caret_filter!=null){
			caret_pos=caret_filter.convertCaretPos(caret_pos);
		}
	}
	//==========
	static void printJTextAreaAction(){
System.out.println("------------------ printJTextAreaAction start");
//		javax.swing.JTextArea textarea=new javax.swing.JTextArea();
		javax.swing.JTextPane textarea=new javax.swing.JTextPane();
//		javax.swing.JPanel textarea=new javax.swing.JPanel();
		ActionMap am=textarea.getActionMap();
		if(am!=null){
			Object[] keys=am.allKeys();
			if(keys!=null){
				for(Object key : keys){
					System.out.println(""+key);
				}
			}
		}
System.out.println("------------------ printJTextAreaAction end");
	}
	//==========
	void initMouseEvent(){
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent ev){
				grabFocus();
				int x=ev.getX();
				int y=ev.getY();
				int pos=getPosAtPoint(x,y);
				setCaretPosition(pos);
				setMarkPosition(pos);
				caret_xor=true;
				repaint();
				sendCaretEvent();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent ev){
				int x=ev.getX();
				int y=ev.getY();
				int pos=getPosAtPoint(x,y);
				setCaretPosition(pos);
				//setMarkPosition(pos);
				caret_xor=true;
				repaint();
				sendCaretEvent();
			}
		});
	}
	CharSequence getInputMethodEventString(InputMethodEvent evt){
		AttributedCharacterIterator text=evt.getText();
		if(text==null)return null;
		StringBuilder textBuffer = new StringBuilder();
		int committedCharacterCount = evt.getCommittedCharacterCount();
		char c = text.first();
		while (committedCharacterCount-- > 0) {
			textBuffer.append(c);
			c = text.next();
		}
		while (c != CharacterIterator.DONE) {
			textBuffer.append(c);
			c = text.next();
		}
		return textBuffer.toString();
	}
	void execDefaultTypedAction(String m){
		ActionEvent act_ev=new ActionEvent(this,0,m);
		ActionMap am=getActionMap();
		Action action=am.get(NormalTextAction.DefaultTyped);
		action.actionPerformed(act_ev);
	}
	
	void initKeyboardEvent(){
		addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent ev){
				char c=ev.getKeyChar();
				if(c==KeyEvent.CHAR_UNDEFINED)return;
				if(c==0x7f)return;
				//if(c==0x0d || c==0x0a || c==0x09 || c>=0x20){
				//if(c==0x0d || c==0x0a || c>=0x20){
				if(c>=0x20){
					CharSequence m=""+c;
					//if(c==0x0d || c==0x0a){
					//	m="\n";
					//}
//System.out.println("ins:"+m);
//					insertChar(m);
					/*
					{
					ActionEvent act_ev=new ActionEvent(this,0,m.toString());
					ActionMap am=getActionMap();
					Action action=am.get(NormalTextAction.DefaultTyped);
					action.actionPerformed(act_ev);
					}
					*/
					execDefaultTypedAction(m.toString());
					
				//new ActionTbl(NormalTextAction.DefaultTyped           ,this,"onDefaultTyped"),
					
					//
				}
			}
		});
		/*
		addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent evt) {
System.out.println("inputMethodTextChanged");
				//myInputMethodTextChanged(evt);
				String m=getInputMethodEventString(evt);
				if(m==null)return;
				insertText(m);
			}
			@Override
			public void caretPositionChanged(InputMethodEvent evt) {
			}
		});
		enableInputMethods(true);
		*/
		/*
		Character.Subset[] subsets = null;
		subsets = new Character.Subset[] {java.awt.im.InputSubset.KANJI};
		getInputContext().setCharacterSubsets(subsets);
		*/
//System.out.println(""+getInputContext());
	}
	/*
	@Override
	public InputContext getInputContext(){
		return InputContext.getInstance();
		//return Component.getInstance();
	}
	*/
	//==========
	void removeTabFocus(){
		setFocusTraversalKeysEnabled(false);
		//ActionTool.DeleteInputKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0));
		//ActionTool.DeleteInputKey(this,KeyStroke.getKeyStroke(KeyEvent.VK_TAB,KeyEvent.SHIFT_DOWN_MASK));
	}
	void removeTabFocus2(){
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Set<AWTKeyStroke> forwardKeys = new HashSet<>(focusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		//forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		//forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,  0));
		forwardKeys.remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0));
		focusManager.setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		
		Set<AWTKeyStroke> backwardKeys = new HashSet<>(focusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		//backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
		//backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP,   0));
		backwardKeys.remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,KeyEvent.SHIFT_DOWN_MASK));
		focusManager.setDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
	}
	//==========
	private ActionTbl[] g_act_tbl={
		new ActionTbl(NormalTextAction.CaretUp                ,this,"onCaretUp"),
		new ActionTbl(NormalTextAction.CaretDown              ,this,"onCaretDown"),
		new ActionTbl(NormalTextAction.CaretBackward          ,this,"onCaretLeft"),
		new ActionTbl(NormalTextAction.CaretForward           ,this,"onCaretRight"),
		new ActionTbl(NormalTextAction.CaretBegin             ,this,"onCaretBegin"),
		new ActionTbl(NormalTextAction.CaretEnd               ,this,"onCaretEnd"),
		new ActionTbl(NormalTextAction.CaretBeginLine         ,this,"onCaretBeginLine"),
		new ActionTbl(NormalTextAction.CaretEndLine           ,this,"onCaretEndLine"),
		new ActionTbl(NormalTextAction.CaretBeginParagraph    ,this,"onCaretBeginParagraph"),
		new ActionTbl(NormalTextAction.CaretEndParagraph      ,this,"onCaretEndParagraph"),
		new ActionTbl(NormalTextAction.CaretBeginWord         ,this,"onCaretBeginWord"),
		new ActionTbl(NormalTextAction.CaretEndWord           ,this,"onCaretEndWord"),
		new ActionTbl(NormalTextAction.CaretPreviousWord      ,this,"onCaretPreviousWord"),
		new ActionTbl(NormalTextAction.CaretNextWord          ,this,"onCaretNextWord"),
		new ActionTbl(NormalTextAction.PageUp                 ,this,"onPageUp"),
		new ActionTbl(NormalTextAction.PageDown               ,this,"onPageDown"),
		new ActionTbl(NormalTextAction.SelectionUp            ,this,"onSelectionUp"),
		new ActionTbl(NormalTextAction.SelectionDown          ,this,"onSelectionDown"),
		new ActionTbl(NormalTextAction.SelectionBackward      ,this,"onSelectionBackward"),
		new ActionTbl(NormalTextAction.SelectionForward       ,this,"onSelectionForward"),
		new ActionTbl(NormalTextAction.SelectionBegin         ,this,"onSelectionBegin"),
		new ActionTbl(NormalTextAction.SelectionEnd           ,this,"onSelectionEnd"),
		new ActionTbl(NormalTextAction.SelectionBeginLine     ,this,"onSelectionBeginLine"),
		new ActionTbl(NormalTextAction.SelectionEndLine       ,this,"onSelectionEndLine"),
		new ActionTbl(NormalTextAction.SelectionPageRight     ,this,"onSelectionPageRight"),
		new ActionTbl(NormalTextAction.SelectionPageLeft      ,this,"onSelectionPageLeft"),
		new ActionTbl(NormalTextAction.SelectionPageDown      ,this,"onSelectionPageDown"),
		new ActionTbl(NormalTextAction.SelectionPageUp        ,this,"onSelectionPageUp"),
		new ActionTbl(NormalTextAction.SelectionPreviousWord  ,this,"onSelectionPreviousWord"),
		new ActionTbl(NormalTextAction.SelectionBeginWord     ,this,"onSelectionBeginWord"),
		new ActionTbl(NormalTextAction.SelectionEndWord       ,this,"onSelectionEndWord"),
		new ActionTbl(NormalTextAction.SelectionNextWord      ,this,"onSelectionNextWord"),
		new ActionTbl(NormalTextAction.SelectionBeginParagraph,this,"onSelectionBeginParagraph"),
		new ActionTbl(NormalTextAction.SelectionEndParagraph  ,this,"onSelectionEndParagraph"),
		new ActionTbl(NormalTextAction.SelectWord             ,this,"onSelectWord"),
		new ActionTbl(NormalTextAction.SelectAll              ,this,"onSelectAll"),
		new ActionTbl(NormalTextAction.SelectLine             ,this,"onSelectLine"),
		new ActionTbl(NormalTextAction.SelectParagraph        ,this,"onSelectParagraph"),
		new ActionTbl(NormalTextAction.Unselect               ,this,"onUnselect"),
		new ActionTbl(NormalTextAction.DeletePrevious         ,this,"onDeletePrevious"),
		new ActionTbl(NormalTextAction.DeleteNext             ,this,"onDeleteNext"),
		new ActionTbl(NormalTextAction.DeletePreviousWord     ,this,"onDeletePreviousWord"),
		new ActionTbl(NormalTextAction.DeleteNextWord         ,this,"onDeleteNextWord"),
		new ActionTbl(NormalTextAction.DeleteBeginLine        ,this,"onDeleteBeginLine"),
		new ActionTbl(NormalTextAction.DeleteEndLine          ,this,"onDeleteEndLine"),
		new ActionTbl(NormalTextAction.InsertContent          ,this,"onInsertContent"),
		new ActionTbl(NormalTextAction.InsertBreak            ,this,"onInsertBreak"),
		new ActionTbl(NormalTextAction.InsertTab              ,this,"onInsertTab"),
//		new ActionTbl(NormalTextAction.InsertCR               ,this,"onInsertCR"),
		new ActionTbl(NormalTextAction.InsertBreak            ,this,"onInsertCR"),
		new ActionTbl(NormalTextAction.ExecUndo               ,this,"onUndo"),
		new ActionTbl(NormalTextAction.ExecRedo               ,this,"onRedo"),
		new ActionTbl(NormalTextAction.Undo                   ,this,"onUndo"),
		new ActionTbl(NormalTextAction.Redo                   ,this,"onRedo"),
		new ActionTbl(NormalTextAction.CopyToClipboard        ,this,"onCopy"),
		new ActionTbl(NormalTextAction.CutToClipboard         ,this,"onCut"),
		new ActionTbl(NormalTextAction.PasteFromClipboard     ,this,"onPaste"),
		new ActionTbl(NormalTextAction.DefaultTyped           ,this,"onDefaultTyped"),
	};
	private InputTbl[] g_input_tbl={
		new InputTbl(KeyEvent.VK_UP   ,0,NormalTextAction.CaretUp),
		new InputTbl(KeyEvent.VK_DOWN ,0,NormalTextAction.CaretDown),
		new InputTbl(KeyEvent.VK_LEFT ,0,NormalTextAction.CaretBackward),
		new InputTbl(KeyEvent.VK_RIGHT,0,NormalTextAction.CaretForward),
		new InputTbl(KeyEvent.VK_KP_LEFT ,0,NormalTextAction.CaretBackward),
		new InputTbl(KeyEvent.VK_KP_RIGHT,0,NormalTextAction.CaretForward),
		new InputTbl(KeyEvent.VK_HOME ,0,NormalTextAction.CaretBegin),
		new InputTbl(KeyEvent.VK_END  ,0,NormalTextAction.CaretEnd),
		new InputTbl(KeyEvent.VK_LEFT ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CaretBeginLine),
		new InputTbl(KeyEvent.VK_RIGHT,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CaretEndLine),
		//
		new InputTbl(KeyEvent.VK_PAGE_UP  ,0,NormalTextAction.PageUp),
		new InputTbl(KeyEvent.VK_PAGE_DOWN,0,NormalTextAction.PageDown),
		new InputTbl(KeyEvent.VK_PAGE_UP  ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionPageUp),
		new InputTbl(KeyEvent.VK_PAGE_DOWN,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionPageDown),
		new InputTbl(KeyEvent.VK_PAGE_UP  ,KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK ,NormalTextAction.SelectionPageLeft),
		new InputTbl(KeyEvent.VK_PAGE_DOWN,KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK ,NormalTextAction.SelectionPageRight),
		
		new InputTbl(KeyEvent.VK_UP   ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionUp),
		new InputTbl(KeyEvent.VK_DOWN ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionDown),
		new InputTbl(KeyEvent.VK_LEFT ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionBackward),
		new InputTbl(KeyEvent.VK_RIGHT,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionForward),
		new InputTbl(KeyEvent.VK_HOME ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionBegin),
		new InputTbl(KeyEvent.VK_END  ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.SelectionEnd),
		
		new InputTbl(KeyEvent.VK_C    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CopyToClipboard),
		new InputTbl(KeyEvent.VK_X    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CutToClipboard),
		new InputTbl(KeyEvent.VK_V    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.PasteFromClipboard),
		new InputTbl(KeyEvent.VK_COPY ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CopyToClipboard),
		new InputTbl(KeyEvent.VK_CUT  ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.CutToClipboard),
		new InputTbl(KeyEvent.VK_PASTE,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.PasteFromClipboard),
		
		new InputTbl(KeyEvent.VK_Z    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.Undo),
		new InputTbl(KeyEvent.VK_Y    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.Redo),
//		new InputTbl(KeyEvent.VK_UNDO ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.Undo),
//		new InputTbl(KeyEvent.VK_REDO ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.Redo),
		
		new InputTbl(KeyEvent.VK_BACK_SPACE,0,NormalTextAction.DeletePrevious),
		new InputTbl(KeyEvent.VK_BACK_SPACE,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.DeletePreviousWord),
		new InputTbl(KeyEvent.VK_BACK_SPACE,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.DeleteBeginLine),
		new InputTbl(KeyEvent.VK_DELETE    ,0,NormalTextAction.DeleteNext),
		new InputTbl(KeyEvent.VK_DELETE    ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.DeleteNextWord),
		new InputTbl(KeyEvent.VK_DELETE    ,KeyEvent.CTRL_DOWN_MASK,NormalTextAction.DeleteEndLine),
		new InputTbl(KeyEvent.VK_TAB,0,NormalTextAction.InsertTab),
//		new InputTbl(KeyEvent.VK_ENTER,0,NormalTextAction.InsertCR),
		new InputTbl(KeyEvent.VK_ENTER,0,NormalTextAction.InsertBreak),
		//new InputTbl(KeyEvent.VK_DELETE    ,KeyEvent.SHIFT_DOWN_MASK,NormalTextAction.DeleteNext),
		//new InputTbl(KeyEvent.VK_ENTER,0,NormalTextAction.InsertEnter),
	};
	/*
@ [46]:keys(pressed PAGE_UP),act(page-up)
@ [50]:keys(pressed PAGE_DOWN),act(page-down)
[33]:keys(shift pressed PAGE_UP),act(selection-page-up)
[12]:keys(shift pressed PAGE_DOWN),act(selection-page-down)
[ 8]:keys(shift ctrl pressed PAGE_UP),act(selection-page-left)
[38]:keys(shift ctrl pressed PAGE_DOWN),act(selection-page-right)

[11]:keys(pressed LEFT),act(caret-backward)
[ 9]:keys(pressed KP_LEFT),act(caret-backward)
[49]:keys(shift pressed LEFT),act(selection-backward)
[36]:keys(ctrl pressed LEFT),act(caret-previous-word)
[22]:keys(shift ctrl pressed LEFT),act(selection-previous-word)

[21]:keys(pressed RIGHT),act(caret-forward)
[14]:keys(pressed KP_RIGHT),act(caret-forward)
[ 4]:keys(shift pressed RIGHT),act(selection-forward)
[42]:keys(ctrl pressed RIGHT),act(caret-next-word)
[29]:keys(shift ctrl pressed RIGHT),act(selection-next-word)

[16]:keys(pressed UP),act(caret-up)
[27]:keys(shift pressed UP),act(selection-up)

[25]:keys(pressed DOWN),act(caret-down)
[37]:keys(shift pressed DOWN),act(selection-down)

[ 5]:keys(pressed HOME),act(caret-begin-line)
[20]:keys(shift pressed HOME),act(selection-begin-line)
[31]:keys(ctrl pressed HOME),act(caret-begin)
[44]:keys(shift ctrl pressed HOME),act(selection-begin)

[ 1]:keys(pressed END),act(caret-end-line)
[41]:keys(shift pressed END),act(selection-end-line)
[28]:keys(ctrl pressed END),act(caret-end)
[15]:keys(shift ctrl pressed END),act(selection-end)

[3]:keys(shift pressed INSERT),act(paste-from-clipboard)
[23]:keys(ctrl pressed INSERT),act(copy-to-clipboard)

[39]:keys(pressed BACK_SPACE),act(delete-previous)
[10]:keys(shift pressed BACK_SPACE),act(delete-previous)
[32]:keys(ctrl pressed BACK_SPACE),act(delete-previous-word)

[24]:keys(ctrl pressed DELETE),act(delete-next-word)
[43]:keys(pressed DELETE),act(delete-next)
[47]:keys(shift pressed DELETE),act(cut-to-clipboard)

[48]:keys(pressed ENTER),act(insert-break)

[45]:keys(pressed TAB),act(insert-tab)

[18]:keys(ctrl pressed SPACE),act(activate-link-action)

[19]:keys(pressed COPY),act(copy-to-clipboard)
[26]:keys(pressed PASTE),act(paste-from-clipboard)
[35]:keys(pressed CUT),act(cut-to-clipboard)

[40]:keys(ctrl pressed C),act(copy-to-clipboard)
[13]:keys(ctrl pressed X),act(cut-to-clipboard)
[ 6]:keys(ctrl pressed V),act(paste-from-clipboard)

[34]:keys(ctrl pressed A),act(select-all)
[30]:keys(ctrl pressed BACK_SLASH),act(unselect)

[0]:keys(ctrl pressed T),act(next-link-action)
[2]:keys(shift ctrl pressed O),act(toggle-componentOrientation)
[7]:keys(ctrl pressed H),act(delete-previous)
[17]:keys(shift ctrl pressed T),act(previous-link-action)
	

	
	
*/


/*
@JTextArea

selection-up
selection-down
selection-backward
selection-forward
selection-page-up
selection-begin
selection-end
selection-page-left
selection-page-down
selection-begin-word
selection-begin-paragraph
selection-begin-line
selection-end-line
selection-previous-word
selection-next-word
selection-end-word
selection-page-right
selection-end-paragraph

select-paragraph
select-word
select-line
select-all
unselect

caret-up
caret-down
caret-backward
caret-forward
caret-begin-word
caret-end-paragraph
caret-end-line
caret-begin-line
caret-begin
caret-end
caret-end-word
caret-next-word
caret-begin-paragraph
caret-previous-word

delete-next
delete-previous
delete-next-word
delete-previous-word

page-up
page-down

cut
copy
paste
cut-to-clipboard
copy-to-clipboard
paste-from-clipboard

insert-tab
insert-content
insert-break

set-read-only
set-writable

default-typed

requestFocus

beep
toggle-componentOrientation
dump-model

java.lang.Object@7e0e6aa2


*/

/*
@JTextPane

selection-page-left
selection-page-up
selection-begin
selection-up
select-paragraph
selection-page-down
selection-begin-word
selection-down
selection-begin-paragraph
selection-end-line
selection-next-word
selection-forward
selection-begin-line
selection-page-right
selection-end-word
selection-backward
selection-previous-word
selection-end-paragraph
selection-end

select-word
select-line
select-all
unselect

caret-begin-word
caret-end-paragraph
caret-end-line
caret-begin-line
caret-begin
caret-down
caret-end
caret-up
caret-backward
caret-end-word
caret-next-word
caret-begin-paragraph
caret-previous-word
caret-forward

delete-next
delete-previous
delete-next-word
delete-previous-word

page-up
page-down

cut
paste
copy
cut-to-clipboard
paste-from-clipboard
copy-to-clipboard

insert-tab
insert-content
insert-break

default-typed

set-read-only
set-writable

font-size-8
font-size-10
font-size-12
font-size-14
font-size-16
font-size-18
font-size-24
font-size-36
font-size-48
font-family-SansSerif
font-underline
font-bold
font-family-Serif
font-family-Monospaced
font-italic


left-justify
center-justify
right-justify

toggle-componentOrientation
beep
dump-model
requestFocus

java.lang.Object@64485a47


*/


	//==========
	//一行上
	private int getPosUp(int pos){
		int line=getLineFromPos(pos);
		if(line<=0)return pos;
		int colm=getCaretColumn();
		line--;
		pos=getPosAtColmnLine(colm,line);
		return pos;
	}
	//一行下
	private int getPosDown(int pos){
		int line=getLineFromPos(pos);
		if(line>=getLineNum()){
			return pos;
		}
		int colm=getCaretColumn();
		line++;
		pos=getPosAtColmnLine(colm,line);
		return pos;
	}
	//一文字前
	private int getPosLeft(int pos){
		int len=getDocumentLength();
		if(len<=0)return pos;
		pos--;
		if(pos<0)pos=0;
		return pos;
	}
	//一文字後ろ
	private int getPosRight(int pos){
		int len=getDocumentLength();
		if(len<=0)return pos;
		pos++;
		if(pos>=len)pos=len;
		return pos;
	}
	//先頭
	private int getPosBegin(){
		return 0;
	}
	//最後
	private int getPosEnd(){
		return getDocumentLength();
	}
	//行頭
	private int getPosBeginLine(int pos){
		int line=getLineFromPos(pos);
		int[] bounds=getLineBounds(line);
		return bounds[0];
	}
	//行末
	private int getPosEndLine(int pos){
		int line=getLineFromPos(pos);
		int[] bounds=getLineBounds(line);
		return bounds[1];
	}
	//行末(※改行含めない)
	private int getPosEndLineNoCR(int pos){
		int len=getDocumentLength();
		if(len<=0)return 0;
		int ret=getPosEndLine(pos);
		if(ret!=len)ret--;
		return ret;
	}
	private int getPosBeginParagraph(int pos){
		return pos;
	}
	private int getPosEndParagraph(int pos){
		return pos;
	}
	//文字列の先頭
	private int getPosBeginWord(int pos){
		return pos;
	}
	//文字列の最後
	private int getPosEndWord(int pos){
		return pos;
	}
	//前の文字列
	private int getPosPreviousWord(int pos){
		return pos;
	}
	//次の文字列
	private int getPosNextWord(int pos){
		return pos;
	}
	//キャレット位置の更新
	public void updateCaretPos(){
		updateCaretPos(false);
	}
	public void updateCaretPos(boolean event_flg){
		execCaretFilter();
		//
		mark_pos=caret_pos;
		caret_xor=true;
		repaint();
		if(!event_flg){
		//
			sendCaretEvent();
		}else{
			sendCaretEventOnly();
		}
	}
	public void updateSelectionPos(){
		caret_xor=true;
		repaint();
		sendCaretEvent();
	}
	//指定位置まで削除(キャレットより前)
	public void deletePrevious(int target_pos){
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos<=0)return;
		if(target_pos<0)return;
		if(target_pos>caret_pos)return;
		if(target_pos==caret_pos)return;
		int old_caret_pos=caret_pos;
		caret_pos=target_pos;
		updateCaretPos();
		removeTextStartEnd(old_caret_pos,caret_pos);
	}
	//指定位置まで削除(キャレットより後ろ)
	public void deleteNext(int target_pos){
		if(target_pos<0)return;
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos>=len)return;
		if(target_pos<0)return;
		if(target_pos<caret_pos)return;
		if(target_pos==caret_pos)return;
		int _len=target_pos-caret_pos;
		removeText(caret_pos,_len);
		updateCaretPos();
	}
	//==========
	//イベント
//	public void onCaretUp(){
	public void onCaretUp(ActionEvent ae){
//System.out.println("onCaretUp:"+ae);
		caret_pos=getPosUp(caret_pos);
		updateCaretPos();
	}
	public void onCaretDown(){
		caret_pos=getPosDown(caret_pos);
		updateCaretPos();
	}
	public void onCaretLeft(){
		caret_pos=getPosLeft(caret_pos);
		updateCaretPos();
	}
	public void onCaretRight(){
		caret_pos=getPosRight(caret_pos);
		updateCaretPos();
	}
	public void onCaretBegin(){
		caret_pos=getPosBegin();
		updateCaretPos();
	}
	public void onCaretEnd(){
		caret_pos=getPosEnd();
		updateCaretPos();
	}
	public void onCaretBeginParagraph(){
		caret_pos=getPosBeginParagraph(caret_pos);
		updateCaretPos();
	}
	public void onCaretEndParagraph(){
		caret_pos=getPosEndParagraph(caret_pos);
		updateCaretPos();
	}
	public void onCaretBeginLine(){
		caret_pos=getPosBeginLine(caret_pos);
		updateCaretPos();
	}
	public void onCaretEndLine(){
		//caret_pos=getPosEndLine(caret_pos);
		caret_pos=getPosEndLineNoCR(caret_pos);
		updateCaretPos();
	}
	public void onCaretBeginWord(){
		caret_pos=getPosBeginWord(caret_pos);
		updateCaretPos();
	}
	public void onCaretEndWord(){
		caret_pos=getPosEndWord(caret_pos);
		updateCaretPos();
	}
	public void onCaretPreviousWord(){
		caret_pos=getPosPreviousWord(caret_pos);
		updateCaretPos();
	}
	public void onCaretNextWord(){
		caret_pos=getPosNextWord(caret_pos);
		updateCaretPos();
	}
	public void onSearchUp(){
System.out.println("onSearchUp");
	}
	public void onSearchDown(){
System.out.println("onSearchDown");
	}
	public void onPageUp(){
System.out.println("onPageUp");
	}
	public void onPageDown(){
System.out.println("onPageDown");
	}
	public void onSelectionUp(){
		caret_pos=getPosUp(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionDown(){
		caret_pos=getPosDown(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionBackward(){
		caret_pos=getPosLeft(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionForward(){
		caret_pos=getPosRight(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionBegin(){
		caret_pos=getPosBegin();
		updateSelectionPos();
	}
	public void onSelectionEnd(){
		caret_pos=getPosEnd();
		updateSelectionPos();
	}
	public void onSelectionBeginLine(){
		caret_pos=getPosBeginLine(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionEndLine(){
		//caret_pos=getPosEndLine(caret_pos);
		caret_pos=getPosEndLineNoCR(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionPageUp(){
System.out.println("onSelectionPageUp");
	}
	public void onSelectionPageDown(){
System.out.println("onSelectionPageDown");
	}
	public void onSelectionPageLeft(){
System.out.println("onSelectionPageLeft");
	}
	public void onSelectionPageRight(){
System.out.println("onSelectionPageRight");
	}
	public void onSelectionPreviousWord(){
		caret_pos=getPosPreviousWord(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionBeginWord(){
		caret_pos=getPosBeginWord(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionEndWord(){
		caret_pos=getPosEndWord(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionNextWord(){
		caret_pos=getPosNextWord(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionBeginParagraph(){
		caret_pos=getPosBeginParagraph(caret_pos);
		updateSelectionPos();
	}
	public void onSelectionEndParagraph(){
		caret_pos=getPosEndParagraph(caret_pos);
		updateSelectionPos();
	}
	public void onSelectWord(){
System.out.println("onSelectWord");
	}
	public void onSelectAll(){
		selectAll();
	}
	public void onSelectLine(){
		int line=getCaretLine();
		int[] bounds=getLineBounds(line);
		select(bounds[0],bounds[1]);
	}
	public void onSelectParagraph(){
System.out.println("onSelectParagraph");
	}
	public void onUnselect(){
		clearSelect();
	}
	public void onDeletePrevious(){
		/*
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos<=0)return;
		int old_caret_pos=caret_pos;
		if(caret_pos>0)caret_pos--;
		updateCaretPos();
		removeTextStartEnd(old_caret_pos,caret_pos);
		*/
		deletePrevious(caret_pos-1);
	}
	public void onDeleteNext(){
		/*
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos>=len)return;
		removeText(caret_pos,1);
		updateCaretPos();
		*/
		deleteNext(caret_pos+1);
	}
	public void onDeletePreviousWord(){
System.out.println("onDeletePreviousWord");
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos<=0)return;
		int pos=getPosPreviousWord(caret_pos);
	}
	public void onDeleteNextWord(){
System.out.println("onDeleteNextWord");
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos>=len)return;
		int pos=getPosNextWord(caret_pos);
	}
	public void onDeleteBeginLine(){
System.out.println("onDeleteBeginLine");
		/*
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos<=0)return;
		int line=getCaretLine();
		int line_top=getLineTop(line);
		if(line_top==caret_pos)return;
		int old_caret_pos=caret_pos;
		caret_pos=line_top;
		updateCaretPos();
		removeTextStartEnd(old_caret_pos,caret_pos);
		*/
		int line=getCaretLine();
		int line_top=getLineTop(line);				
		deletePrevious(line_top);
	}
	public void onDeleteEndLine(){
System.out.println("onDeleteEndLine");
		/*
		int len=getDocumentLength();
		if(len==0)return;
		if(caret_pos>=len)return;
		int line=getCaretLine();
		int line_bottom=getLineBottomNoCR(line);
		if(line_bottom==caret_pos)return;
		int _len=line_bottom-caret_pos;
		removeText(caret_pos,_len);
		updateCaretPos();
		*/
		int line=getCaretLine();
		int line_bottom=getLineBottomNoCR(line);
		deleteNext(line_bottom);
	}
	public void onInsertContent(){
System.out.println("onInsertContent");
	}
	public void onInsertBreak(){
System.out.println("onInsertBreak");
	}
	public void onInsertTab(){
		insertChar("\t");
	}
	public void onInsertCR(){
		insertChar("\n");
	}
	public void onUndo(){
		undo();
	}
	public void onRedo(){
		redo();
	}
	public void onCut(){
		cut();
	}
	public void onCopy(){
		copy();
	}
	public void onPaste(){
		paste();
	}
	public void onDefaultTyped(ActionEvent ev){
//	public void onDefaultTyped(MouseEvent ev){
		String cmd=ev.getActionCommand();
//System.out.println("onDefaultTyped:"+cmd);
//		String m=ae.
		insertChar(cmd);
	}
	
	//==========
	void initActionMap(){
		ActionTbl.SetActionTbl(this,g_act_tbl);
	}
	void initInputMap(){
		ActionTool.SetInputTbl(this,g_input_tbl);
	}
	//==========
	static boolean isHankaku(int c){
		return 
			(c<=0x007e) ||	// 英数字
			(c==0x00a5) ||	// \記号
			(c==0x203e) ||	// ~記号
			(c>=0xff61 && c<=0xff9f);	// 半角カナ
	}
	static boolean isKakko(int c){
		return 
			(c=='(' || c==')' || 
			 c=='[' || c==']' || 
			 c=='{' || c=='}');
	}
	static boolean isStr(int c){
		return (c=='\'' || c=='\"');
	}
	//==========
	public void initNormalTextJTextAreaDrawer(){
		drawer=new NormalTextJTextAreaDrawer_Type2();
	}
	private NormalTextJTextAreaDrawer drawer=null;
	public void setNormalTextJTextAreaDrawer(NormalTextJTextAreaDrawer _drawer){
		drawer=_drawer;
	}
	@Override
	protected void paintComponent(Graphics _g) {
//		super.paintComponent(_g);	//※これをやらないとキャレットが正しく移動しない
//		if(drawer!=null)drawer.paintComponent(_g,new TextDrawDocumentParser_VRAM(this),norm_attr);
		paintComponentSub(_g);
	}
	void paintComponentSub(Graphics _g) {
		Graphics2D g=(Graphics2D)_g;
		//
		feedbackNormalAttr();
		//背景クリア
		Rectangle clip=g.getClipBounds();
		g.setColor(bg_col);
		g.fillRect(clip.x,clip.y,clip.width,clip.height);
		//
		g.setComposite(AlphaComposite.SrcOver);
		Font font=getFont();
		//
		final char tab_char=0xffeb;
		final char ret_char=0xffec;
		final String tab_string="\uffeb";
		final String ret_string="\uffec";
		final String hanspace_string="\u005f";//"\uffed";
		final String zenspace_string="□";
		final Color kakko_col=Color.GREEN;
		final Color str_col=Color.RED;
		final Color text_col=Color.WHITE;//text_col;
		final Color select_col=Color.BLUE;
		final Color comment_col=Color.GREEN;
		final int[] select_bounds=getSelectionBounds();
		final boolean is_select=(select_bounds[0]!=select_bounds[1]);
		
		if(getDocumentLength()>0){
			int start_line=getLineAtPointY(clip.y);
			int end_line  =getLineAtPointY(clip.y+clip.height);
			for(int i=start_line;i<=end_line;i++){
				int[] line_bounds=getLineBounds(i);
				int start_pos=line_bounds[0];
				int end_pos  =line_bounds[1];
				int line_len=end_pos-start_pos;
				if(line_len>0){
					try{
						CharSequence str_m=getText(start_pos,line_len);
						int str_len=Character.codePointCount(str_m,0,line_len);
						int ci=0;
						int ty=getLineFromPos(start_pos);
						int tx=0;
						int c=-1;
						int prev_c=-1;
						boolean line_comment=false;
						for(int j=0;j<str_len;j++){
							int cci=start_pos+ci;
							prev_c=c;
							c=Character.codePointAt(str_m,ci);
							ci+=Character.charCount(c);
							int y=ty*font_h;
							int x=tx*font_w;
							int y2=y+font_h;
							int old_tx=tx;
							if(c!=0x09){
								tx+=isHankaku(c)?1:2;
							}else{
								tx+=tab_size;
								int d=tx % tab_size;
								tx-=d;
							}
							int w=(tx-old_tx)*font_w;
							//選択範囲
							if(is_select && (select_bounds[0]<=cci && cci<=select_bounds[1]) ){
								g.setColor(select_col);
								g.fillRect(x,y,w,font_h);
								g.setPaintMode();
							}
							//
							if(c==' ' && draw_hanspace){
							//半角空白
								g.setColor(hanspace_col);
								g.drawString(hanspace_string,x,y2);
							}else if(c=='　' && draw_zenspace){
							//全角空白
								g.setColor(zenspace_col);
								g.drawString(zenspace_string,x,y2);
							}else if(c=='\n' && draw_cr){
							//改行
								g.setColor(cr_col);
								g.drawString(ret_string,x,y2);
							}else if(c=='\t' && draw_tab){
							//タブ
								g.setColor(tab_col);
								g.drawString(tab_string,x,y2);
							}else{
							//文字の描画
								if(!line_comment){
									if(prev_c=='/' && c=='/')line_comment=true;
								}
								Color col=text_col;
								if(cols_prop!=null){
									int ci_col=cols_prop.getColor(start_pos+ci);
									col=new Color(ci_col);
								}
								//括弧
								if(isKakko(c)){
									col=kakko_col;
								}else if(isStr(c)){
								//文字列
									col=str_col;
								}
								//コメント
								if(line_comment){
									col=comment_col;
								}
							
								String mm=new String(Character.toChars(c));
								g.setColor(col);
								g.drawString(mm,x,y2);
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		//EOF
		if(draw_eof){
			try{
				int bottom=getDocumentLength();
				Rectangle r=modelToView(bottom);
				g.setColor(eof_col);
				g.drawString("[EOF]",r.x,r.y+font_h);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		//キャレット
		drawCaret(g);
	}
	//キャレット初期化
	public void initCaretBlink(){
		Timer timer=new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				caret_xor=!caret_xor;
				repaint();
			}
		}, 10,60*10);
	}
	//キャレット描画
	public void drawCaret(Graphics g){
		if(!caret_xor)return;
		try{
			Rectangle r=modelToView(caret_pos);
			g.setColor(caret_col);
			
			if(overtype_mode){
				int h=r.height/2;
				g.fillRect(r.x,r.y+h,r.width,h);
			}else{
				g.fillRect(r.x,r.y,r.width,r.height);
			}
		}catch(Exception ex){}
	}
	//==========
	static class TextDrawDocumentParser_VRAM implements TextDrawDocumentParser{
		private NormalTextVram vram;
		public TextDrawDocumentParser_VRAM(NormalTextVram _vram){
			vram=_vram;
		}
		public int getCaretPosition(){
			return vram.getCaretPosition();
		}
		public int getMarkPosition(){
			return vram.getMarkPosition();
		}
		public FontMetrics getFontMetrics(){
			return vram.getFontMetrics();
		}
		public Rectangle modelToView(int pos) throws Exception {
			return vram.modelToView(pos);
		}
		public void drawCaret(Graphics g){
			vram.drawCaret(g);
		}
		public TextColProp getColorsProp(){
			return vram.getColorsProp();
		}
		public void readLock(){
		}
		public void readUnlock(){
		}
		public CharSequence getText(int pos,int len) throws Exception {
			return vram.getText(pos,len);
		}
		public int getLength(){
			return vram.getDocumentLength();
		}
		public int[] getLineBounds(int i){
			return vram.getLineBounds(i);
		}
		public int getLineAtPoint(int y){
			return vram.getLineAtPointY(y);
		}
		public Dimension getTextSize(){
			return vram.getTextSize();
		}
		
	}
}
