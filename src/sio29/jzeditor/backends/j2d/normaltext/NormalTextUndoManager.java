/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;


public interface NormalTextUndoManager{
	public void DisableUndo();
	public void EnableUndo();
	public void ClearUndoBuff();
	public boolean CanUndo();
	public boolean CanRedo();
	public void ExecUndo();
	public void ExecRedo();
}

