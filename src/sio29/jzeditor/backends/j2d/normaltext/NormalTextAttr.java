/******************************************************************************
;	ノーマルテキストアトリュビュート
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import java.awt.Font;
import javax.swing.text.StyleConstants;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public class NormalTextAttr extends TextAttrBase{
//	public boolean draw_space   =false;
//	public boolean draw_zenspace=true;
//	public boolean draw_tab     =true;
//	public boolean draw_cr      =true;
//	public boolean draw_eof     =true;
	public boolean draw_linenum =false;
	public boolean draw_ruler   =false;
	public CVECTOR comment_col  = CVECTOR.GREEN;
	public CVECTOR str_col      = CVECTOR.YELLOW;
	public CVECTOR macro_col    = CVECTOR.MAGENTA;
	public CVECTOR linenum_col  = CVECTOR.CYAN;
//	public CVECTOR tab_col      = CVECTOR.CYAN;
//	public CVECTOR cr_col       = CVECTOR.CYAN;
//	public CVECTOR eof_col      = CVECTOR.MAGENTA;
//	public CVECTOR space_col    = CVECTOR.CYAN;
//	public CVECTOR zenspace_col = CVECTOR.CYAN;
	public CVECTOR kakko_col    = CVECTOR.GREEN;
	//
	public MutableAttributeSet attr_normal;
	public NormalTextAttr(){
//		draw_space=false;
//		draw_zenspace=false;
//		draw_tab=false;
//		draw_cr=false;
//		draw_eof=false;
		
		
		MakeAttr();
	}
	public void MakeAttr(){
		attr_normal =TextComponentTool.getColorAttr(font_name,font_size,back_col,normal_col);
	}
}

