/******************************************************************************
;	コマンドラインのアトリュビュート
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;

public class CommandLineAttr extends TextAttrBase{
	public CVECTOR comline_col= CVECTOR.YELLOW;
	public CVECTOR path_col   = CVECTOR.CYAN;
	public CVECTOR error_col  = CVECTOR.RED;
	public CVECTOR backlogback_col= CVECTOR.BLUE.darker().darker();
	//
	public MutableAttributeSet base_attr;			//ベースのアトリュビュート
	public MutableAttributeSet attr_normal;		//通常アトリュビュート
	public MutableAttributeSet attr_comline;		//コマンドラインのアトリュビュート
	public MutableAttributeSet attr_path;			//パス表示のアトリュビュート
	public MutableAttributeSet attr_error;			//エラー表示のアトリュビュート
	//
	public CommandLineAttr(){
//		draw_space=false;
//		draw_zenspace=false;
//		draw_tab=false;
//		draw_cr=false;
//		draw_eof=false;
		//
		MakeAttr();
	}
	public void MakeAttr(){
		base_attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(base_attr, font_name);
		StyleConstants.setFontSize(base_attr,font_size);
		//通常アトリュビュート
		attr_normal = new SimpleAttributeSet(base_attr);
		StyleConstants.setForeground(attr_normal,ConvAWT.CVEC2Color(normal_col));
		StyleConstants.setBackground(attr_normal,ConvAWT.CVEC2Color(back_col));
		//コマンドラインのアトリュビュート
		attr_comline = new SimpleAttributeSet(base_attr);
		StyleConstants.setForeground(attr_comline,ConvAWT.CVEC2Color(comline_col));
		StyleConstants.setBackground(attr_comline,ConvAWT.CVEC2Color(back_col));
		//パス表示のアトリュビュート
		attr_path    = new SimpleAttributeSet(base_attr);
		StyleConstants.setForeground(attr_path,ConvAWT.CVEC2Color(path_col));
		StyleConstants.setBackground(attr_path,ConvAWT.CVEC2Color(back_col));
		//エラー表示のアトリュビュート
		attr_error = new SimpleAttributeSet(base_attr);
		StyleConstants.setForeground(attr_error,ConvAWT.CVEC2Color(error_col));
		StyleConstants.setBackground(attr_error,ConvAWT.CVEC2Color(back_col));
	}
	public CVECTOR GetBackCol(boolean log_flg){
		if(!log_flg){
			return back_col;
		}else{
			return backlogback_col;
		}
	}
	public MutableAttributeSet GetBackColAttr(boolean log_flg){
		CVECTOR col=GetBackCol(log_flg);
		MutableAttributeSet attr = new SimpleAttributeSet(base_attr);
		StyleConstants.setBackground(attr,ConvAWT.CVEC2Color(col));
		return attr;
	}
}
