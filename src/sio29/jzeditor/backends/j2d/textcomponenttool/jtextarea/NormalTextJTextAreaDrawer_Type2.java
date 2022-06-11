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

public class NormalTextJTextAreaDrawer_Type2 implements NormalTextJTextAreaDrawer{
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
//boolean clear_flg=false;
//boolean draw_char_flg=false;
//boolean draw_caret_flg=false;
boolean clear_flg=true;
boolean draw_char_flg=true;
boolean draw_caret_flg=true;
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
		int start = textArea.getLineAtPoint(clip.y);
		int end   = textArea.getLineAtPoint(clip.y+clip.height);
		for(int i=start;i<=end;i++){
			int[] line_bounds=textArea.getLineBounds(i);
			if(line_bounds==null)continue;
			int start_pos=line_bounds[0];
			int end_pos  =line_bounds[1];
			if(start_pos<0 || end_pos<0 || end_pos<=start_pos)continue;
			CharSequence line_str=null;
			try{
				line_str=textArea.getText(start_pos,end_pos-start_pos);
			}catch(Exception ex){
				ex.printStackTrace();
				continue;
			}
			if(line_str==null)continue;
			int line_str_len=line_str.length();
			if(line_str_len==0)continue;
			char tab_char=0xffeb;
			char ret_char=0xffec;
			String tab_string="\uffeb";
			String ret_string="\uffec";
			String space_string="\u005f";//"\uffed";
			
			int index=0;
			while(true){
				int j=start_pos+index;
				if(j>=end_pos)break;
				if(j>=eof_pos)break;
				try{
					
					//文字
					int code=0;
					if(j<eof_pos){
					//※文字の獲得が重い!!
						code=Character.codePointAt(line_str,index);
						int index_add=Character.charCount(code);
						index+=index_add;
					}
					//描画位置
					IRECT r = ConvAWT.Rectangle_IRECT(textArea.modelToView(j));
					int x=r.x;
					int y=r.y+r.height-yoff;
					//選択範囲
					if(caret_pos1<=j && j<=caret_pos2){
						IRECT r2=ConvAWT.Rectangle_IRECT(textArea.modelToView(j+1));
						int w=r2.x-r.x;if(w<=0)w=font_w;
						g.setColor(select_col);
						g.fillRect(r.x,r.y,w,r.height);
						g.setPaintMode();
					}
					//文字の描画
					if(draw_tab && code=='\t'){
					//タブ
						g.setColor(tab_col);
						g.drawString(tab_string,x,y);
					}else if(draw_space && code==' '){
					//半角空白
						g.setColor(space_col);
						g.drawString(space_string,x,y);
					}else if(draw_zenspace && code=='　'){
					//全角空白
						g.setColor(zenspace_col);
						g.drawString("□",x,y);
					}else if(draw_cr && (code==0x0d || code==0x0a) && (draw_eof?(j!=eof_pos):true) ){
					//改行
						g.setColor(cr_col);
						g.drawString(ret_string,x,y);
					}else{
					//文字の描画
						//色を求める
						Color col=null;
						//if(cols!=null){
						//	//※改行するまで正しいサイズのcolsにならない
						//	if(j<cols.length){
						//		col=cols[j];
						//	}
						//}
						if(cols_prop!=null){
							//※改行するまで正しいサイズのcolsにならない
							//if(j<cols.length){
								col=new Color(cols_prop.getColor(j));
							//}
						}
						//未定義ならtext_col
						if(col==null){
							col=text_col;
						}
						//括弧
						if(code=='(' || code==')' || 
						   code=='[' || code==']' || 
						   code=='{' || code=='}'){
							col=kakko_col;
						}else if(code=='\'' || code=='\"'){
						//文字列
							col=str_col;
						}
						//文字描画
						if(draw_char_flg){
							g.setColor(col);
							g.drawString(new String(new int[]{code},0,1),x,y);
							//※表示できない文字がある
							if(!font.canDisplay(code)){
								//System.out.println(String.format("Error(%08x)",code));
							}
						}else{
							if(col!=text_col){
								g.setComposite(TextComponentTool.createTextColorComposite(col));
								//g.setColor(col);
								g.fillRect(r.x,r.y,fm.charWidth(code),r.height);
								g.setComposite(AlphaComposite.SrcOver);
							}
						}
					}
				}catch(Exception ex){
					System.out.printf("paintComponentSub2:Error!!:line(%d),pos(%d/%d):%s\n",i,j,eof_pos,ex);
					ex.printStackTrace();
					break;
				}
			}
		}
		//EOFの描画
		if(draw_eof){
			try{
				IRECT r = ConvAWT.Rectangle_IRECT(textArea.modelToView(eof_pos));
				int x=r.x;
				int y=r.y+r.height-yoff;
				g.setColor(Color.MAGENTA);
				g.drawString("[EOF]",x,y);
			}catch(Exception ex){}
		}
		//ロック解除
		textArea.readUnlock();
		//キャレット
		if(draw_caret_flg){
			textArea.drawCaret(g);
		}
	}
}
