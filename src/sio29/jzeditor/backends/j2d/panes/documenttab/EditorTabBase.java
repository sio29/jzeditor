/******************************************************************************
;	タブのコンポーネントのインターフェース
******************************************************************************/
package sio29.jzeditor.backends.j2d.panes.documenttab;

import java.util.HashMap;
import java.awt.Component;
import javax.swing.InputMap;

import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.doctab.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

import sio29.jzeditor.backends.j2d.texttool.FilenameLine;
import sio29.jzeditor.backends.j2d.texttool.CharCode;
import sio29.jzeditor.backends.j2d.menu.MenuFeedback;

public interface EditorTabBase extends DocumentTabChildBase{
	public boolean IsDarty();
	public Component GetDropTarget();
	//
	public String GetStatusBarStr();
	public String GetCurrentDir();
	public void SetFilename(UFile n);
	public StatusBarPane.StateLabel[] getStatusBarLabels();
	public StatusBarMessage getStatusBarMessage();
	public FunctionKeyPane.Function[][] getFunctionKeyFunctions();
	public ActionInfoMap getFunctionKeyActionInfoMap();
	public InputMap getFunctionKeyInputMap();
	public ToolBarPane.ToolBarItem[][] getToolBarItems();
	public MenuFeedback getMenuFeedback();
	//File
	public boolean OnOpen(UFile filename);
	public boolean OnOpenAdd(UFile filename);
	public boolean OnSave(UFile filename);
	public boolean CanSave();
	public boolean CanPrint();
	public boolean CanOpenOutter();
	//State
	public CharCode GetCharCode();
	public void SetCharCode(CharCode n);
	public String GetCRType();
	public void SetCRType(String n);
	public boolean GetEofFlg();
	public void SetEofFlg(boolean n);
	public int GetNowLine();
	public int GetTotalLineNum();
	//Undo
	public void OnUndo();
	public void OnRedo();
	public boolean CanUndo();
	public boolean CanRedo();
	//Select
	public void OnSelectAll();
	public void OnSelectClear();
	public void OnSelectStart();
	public boolean IsSelected();
	//文字コード変換
	public void OnChangeCode(String code);
	//選択部分
	public void OnSelectConv(String type);
	//Cut&Paste
	public void OnCut();
	public void OnCopy();
	public void OnPaste();
	//Jump
	public void OnTagJump();
	public void OnJumpTop();
	public void OnJumpBottom();
	public void OnJumpLineTop();
	public void OnJumpLineBottom();
	public void OnJumpPairKakko();
	public boolean CanJumpLine();
	public void SetJumpLine(int line);
	public FilenameLine GetTagJumpFilenameLine();
	//Search
	public void OnSearch();
	public void OnReplace();
	public void OnSearchUp();
	public void OnSearchDown();
	public void OnRemember();
	//マクロ
	public void OnMacroRecStart();
	public void OnMacroRecEnd();
	public void OnMacroRecToggle();
	public void OnMacroPlay();
	public boolean CanMacroRecStart();
	public boolean CanMacroRecEnd();
	public boolean CanMacroPlay();
	//イベント
	public void OnVzCut();
	public void OnPrint();
	public void OnDocInfo();
	public void OnShortCut();
	public void OnUpdate();
	public void OnUpdateAttr();
	public void OnFlipInsertMode();
	//イベント挿入
	public void OnInsertCopyString();
	public void OnInsertSearchString();
	public void OnInsertInputString();
	public void OnInsertDeleteString();
	public void OnInsertDate();
	public void OnInsertFile();
	public void OnInsertFilename();
	public void OnInsertHorizon();
	public void OnInsertTable();
}
