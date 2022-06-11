/******************************************************************************
;	コマンド実行入力リスナー
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

public interface DosExecInputListener{
	public boolean HasInput();
	public String InputMessage();
	public void ClearInput();
};
