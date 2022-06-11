/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;

public interface NormalTextFunc{
	void setNormalizeCaretPosFunc(NormalizeCaretPosFunc func);
	void setNormalAttr(TextAttrBase norm_attr);
}
