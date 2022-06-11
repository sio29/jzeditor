/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea;

import java.awt.Graphics;
import javax.swing.text.JTextComponent;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public interface NormalTextJTextAreaDrawer{
	void paintComponent(Graphics _g,TextDrawDocumentParser textArea,TextAttrBase attr);

}
