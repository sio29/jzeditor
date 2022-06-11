/******************************************************************************
;	ノーマルテキストアトリュビュート
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool;

import java.awt.Font;

import sio29.ulib.umat.*;

public class TextAttrBase{
	public String font_name="ＭＳ ゴシック";
	public int font_size=14;
	public CVECTOR back_col  =CVECTOR.BLACK;
	public CVECTOR cursor_col=CVECTOR.WHITE;
	public CVECTOR select_col=CVECTOR.CYAN.darker().darker();
	public CVECTOR normal_col=CVECTOR.WHITE;
	public int tab_size=4;
	//
	public boolean draw_space   =false;
	public boolean draw_zenspace=false;//true;
	public boolean draw_tab     =false;//true;
	public boolean draw_cr      =false;//true;
	public boolean draw_eof     =false;//true;
	public CVECTOR tab_col      = CVECTOR.CYAN;
	public CVECTOR cr_col       = CVECTOR.CYAN;
	public CVECTOR eof_col      = CVECTOR.MAGENTA;
	public CVECTOR space_col    = CVECTOR.CYAN;
	public CVECTOR zenspace_col = CVECTOR.CYAN;
	
	//
	public Font getFont(){
		return new Font(font_name,Font.PLAIN,font_size);
	}
}

