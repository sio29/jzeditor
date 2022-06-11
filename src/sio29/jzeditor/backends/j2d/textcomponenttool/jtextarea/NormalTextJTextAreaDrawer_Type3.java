/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;

import java.lang.CharSequence;
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

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.caret.*;

public class NormalTextJTextAreaDrawer_Type3 implements NormalTextJTextAreaDrawer{
	public void paintComponent(Graphics _g,TextDrawDocumentParser textArea,TextAttrBase _attr){
		Color back_col    =Color.BLACK;
		Color text_col    =Color.WHITE;
		Color select_col  =Color.BLUE;
		Color tab_col     =Color.WHITE;
		Color cr_col      =Color.WHITE;
		Color space_col   =Color.WHITE;
		Color zenspace_col=Color.WHITE;
		Color eof_col     =Color.WHITE;
		Color kakko_col   =Color.WHITE;
		Color str_col     =Color.WHITE;
		boolean draw_space   =false;
		boolean draw_zenspace=false;
		boolean draw_tab     =false;
		boolean draw_cr      =false;
		boolean draw_eof	 =false;
		if(_attr!=null){
			back_col    =ConvAWT.CVEC2Color(_attr.back_col);
			text_col    =ConvAWT.CVEC2Color(_attr.normal_col);
			select_col  =ConvAWT.CVEC2Color(_attr.select_col);
		}
		if(_attr instanceof NormalTextAttr){
			NormalTextAttr attr=(NormalTextAttr)_attr;
			tab_col     =ConvAWT.CVEC2Color(attr.tab_col);
			cr_col      =ConvAWT.CVEC2Color(attr.cr_col);
			space_col   =ConvAWT.CVEC2Color(attr.space_col);
			zenspace_col=ConvAWT.CVEC2Color(attr.zenspace_col);
			eof_col     =ConvAWT.CVEC2Color(attr.eof_col);
			kakko_col   =ConvAWT.CVEC2Color(attr.kakko_col);
			str_col     =ConvAWT.CVEC2Color(attr.str_col);
			draw_space   =attr.draw_space;
			draw_zenspace=attr.draw_zenspace;
			draw_tab     =attr.draw_tab;
			draw_cr      =attr.draw_cr;
			draw_eof	 =attr.draw_eof;
		}
		//
		Graphics2D g=(Graphics2D)_g;
		IRECT clip=ConvAWT.Rectangle_IRECT(g.getClipBounds());
		//背景クリア
boolean clear_flg=false;
boolean draw_char_flg=false;
boolean draw_caret_flg=false;
//boolean clear_flg=true;
//boolean draw_char_flg=true;
//boolean draw_caret_flg=true;
		if(clear_flg){
			g.setColor(back_col);
			g.fillRect(clip.x,clip.y,clip.width,clip.height);
		}
		//選択範囲
		int caret_pos1=textArea.getCaretPosition();
		int caret_pos2=textArea.getMarkPosition();
		if(caret_pos1!=caret_pos2){
			if(caret_pos1>caret_pos2){
				int t=caret_pos1;
				caret_pos1=caret_pos2;
				caret_pos2=t;
			}
		}else{
			caret_pos1=-1;
			caret_pos2=-1;
		}
		FontMetrics fm = textArea.getFontMetrics();
		Font font=fm.getFont();
		int font_w=fm.charWidth('M');
		//色
		TextColProp cols_prop=textArea.getColorsProp();
		//
		g.setComposite(AlphaComposite.SrcOver);
		int yoff=0;
		//ロック開始
		textArea.readLock();
		int eof_pos=textArea.getLength();
		Rectangle eof_rect=null;
		try{
			eof_rect=textArea.modelToView(eof_pos);
		}catch(Exception ex){}
		//
		int base  = clip.y;
		int start = textArea.getLineAtPoint(base);
		int end   = textArea.getLineAtPoint(base + clip.height);
		int line_num=end-start+1;
		CharSequence[] line_strs=new CharSequence[line_num];
		int[] start_poss=new int[line_num];
		Rectangle[][] line_rects=new Rectangle[line_num][];
		//
		for(int i=start;i<=end;i++){
			int line_index=i-start;
			int[] line_bounds=textArea.getLineBounds(i);
			int start_pos=line_bounds[0];
			int end_pos  =line_bounds[1];
			int str_len=(end_pos-start_pos+1);
			start_poss[line_index]=start_pos;
			line_rects[line_index]=new Rectangle[str_len];
			try{
				CharSequence line_m=textArea.getText(start_pos,str_len);
				line_strs[line_index]=line_m;
				
				for(int j=0;j<str_len;j++){
					line_rects[line_index][j]=textArea.modelToView(start_pos+j);	//※modelToViewが重い
				}
			}catch(Exception ex){
				continue;
			}
		}
		textArea.readUnlock();
		for(int i=0;i<line_num;i++){
			CharSequence line_m=line_strs[i];
			if(line_m==null)continue;
			int str_len=line_m.length();
			int start_pos=start_poss[i];
			for(int index=0;index<str_len;index++){
				int j=start_pos+index;
				//文字
				int c=0;
				if(j<eof_pos){
					c=line_m.charAt(index);
				}
				//描画位置
				Rectangle r = line_rects[i][index];
				int x=r.x;
				int y=r.y+r.height-yoff;
				//選択範囲
				if(caret_pos1<=j && j<=caret_pos2){
					int w=16;
					if((index+1)<str_len){
						Rectangle r2=line_rects[i][index+1];
						w=r2.x-r.x;if(w<=0)w=font_w;
					}
					g.setColor(select_col);
					//g.fillRect(r.x,r.y,w,r.height);
					g.setPaintMode();
				}
				//文字の描画
				if(draw_tab && c=='\t'){
				//タブ
					g.setColor(tab_col);
					int x0=x;
					int x1=x+font_w-2;
					int x2=x1-2;
					int y0=(r.y+y) >> 1;
					int y1=y0-2;
					int y2=y0+2;
					g.drawLine(x0,y0,x1,y0);
					g.drawLine(x2,y1,x1,y0);
					g.drawLine(x2,y2,x1,y0);
				}else if(draw_space && c==' '){
				//半角空白
					g.setColor(space_col);
					int x0=x;
					int x1=x+font_w-2;
					int y0=y-1;
					int y1=y-2;
					g.drawLine(x0,y1,x0,y0);
					g.drawLine(x1,y1,x1,y0);
					g.drawLine(x0,y0,x1,y0);
				}else if(draw_zenspace && c=='　'){
				//全角空白
					g.setColor(zenspace_col);
					g.drawString("□",x,y);
				}else if(draw_cr && (c==0x0d || c==0x0a) && (draw_eof?(j!=eof_pos):true) ){
				//改行
					g.setColor(cr_col);
					int x0=x+font_w/2;
					int x1=x0-2;
					int x2=x0+2;
					int y0=r.y+4;
					int y1=y-2;
					int y2=y1-2;
					g.drawLine(x0,y0,x0,y1);
					g.drawLine(x1,y2,x0,y1);
					g.drawLine(x2,y2,x0,y1);
				}else{
				//文字の描画
					//色を求める
					Color col=null;
					if(cols_prop!=null){
						col=new Color(cols_prop.getColor(j));
					}
					//未定義ならtext_col
					if(col==null){
						col=text_col;
					}
					//括弧
					if(c=='(' || c==')' || 
					   c=='[' || c==']' || 
					   c=='{' || c=='}'){
						col=kakko_col;
					}else if(c=='\'' || c=='\"'){
					//文字列
						col=str_col;
					}
					//文字描画
					if(draw_char_flg){
						g.setColor(col);
						g.drawString(String.format("%c",c),x,y);
					}else{
						if(col!=text_col){
							g.setComposite(TextComponentTool.createTextColorComposite(col));
							//g.setColor(col);
							g.fillRect(r.x,r.y,fm.charWidth(c),r.height);
							g.setComposite(AlphaComposite.SrcOver);
						}
					}
				}
			}
		}
		
		//EOFの描画
		if(draw_eof){
			Rectangle r = eof_rect;
			int x=r.x;
			int y=r.y+r.height-yoff;
			g.setColor(Color.MAGENTA);
			g.drawString("[EOF]",x,y);
		}
		//キャレット
		if(draw_caret_flg){
			textArea.drawCaret(g);
		}
	}
}

