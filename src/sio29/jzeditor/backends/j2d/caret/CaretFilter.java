/******************************************************************************
;	キャレット
******************************************************************************/
package sio29.jzeditor.backends.j2d.caret;

import javax.swing.text.Caret;

public interface CaretFilter{
	//キャレット位置の計算
//	public int convertCaretPos(Caret caret,int n);
	public int convertCaretPos(int n);
}
