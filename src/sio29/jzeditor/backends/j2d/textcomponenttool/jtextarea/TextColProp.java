/******************************************************************************
;	アトリュビュート作成(PlainDocument)
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;


import java.awt.Color;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;



public interface TextColProp{
	void setColor(int i,int col);
	int getColor(int i);
}

