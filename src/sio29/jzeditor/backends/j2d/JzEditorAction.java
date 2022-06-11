/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d;

public class JzEditorAction{
	public static String LookAndFeel="jzeditor-look-and-feel";
	public static String SelectConv="jzeditor-select-conv";
	public static String CharCode="jzeditor-charcode";
	//
	public static String New="jzeditor-new";
	public static String NewWindow="jzeditor-new-window";
	public static String Open="jzeditor-open";
	public static String OpenWindow="jzeditor-open-window";
	public static String OpenAdd="jzeditor-open-add";
	public static String OpenOutter="jzeditor-open-outter";
	public static String OpenOutterNow="jzeditor-open-outter-now";
	public static String OuterFiler="jzeditor-outter-filer";
	public static String Save="jzeditor-save";
	public static String SaveAs="jzeditor-save-as";
	public static String CloseTab="jzeditor-close-tab";
	public static String CloseAllTab="jzeditor-close-all-tab";
	public static String OpenCommandLine="jzeditor-open-command-line";
	public static String Print="jzeditor-print";
	public static String DocInfo="jzeditor-doc-info";
	public static String Exit="jzeditor-exit";
	public static String Undo="jzeditor-undo";
	public static String Redo="jzeditor-Redo";
	public static String SelectAll="jzeditor-select-all";
	
	public static String SelectClear="jzeditor-selectclear";
	public static String BlockSelect="jzeditor-blockselect";
	public static String TagJump="jzeditor-tagjump";
	public static String TagJumpOutter="jzeditor-tagjumpoutter";
	public static String JumpTop="jzeditor-jumptop";
	public static String JumpBottom="jzeditor-jumpbottom";
	public static String JumpLineTop="jzeditor-jumplinetop";
	public static String JumpLineBottom="jzeditor-jumplinebottom";
	public static String Cut="jzeditor-cut";
	public static String Copy="jzeditor-copy";
	public static String Paste="jzeditor-paste";
	public static String ToolBar="jzeditor-toolbar";
	public static String StatusBar="jzeditor-statusbar";
	public static String FunctionKey="jzeditor-functionkey";
	public static String Help="jzeditor-help";
	public static String About="jzeditor-about";
	public static String ScriptTest="jzeditor-scripttest";
	public static String SelectNextTab="jzeditor-selectnexttab";
	public static String SelectPrevTab="jzeditor-selectprevtab";
	public static String DosExec="jzeditor-dosexec";
	public static String Option="jzeditor-option";
	public static String ShortCut="jzeditor-shortcut";
	public static String Search="jzeditor-search";
	public static String Replace="jzeditor-replace";
	public static String SelectDoc="jzeditor-selectdoc";
	public static String Grep="jzeditor-grep";
	public static String JumpLine="jzeditor-jumpline";
	public static String JumpPairKakko="jzeditor-jumppairkakko";
	public static String Compare="jzeditor-compare";
	public static String SearchDown="jzeditor-searchdown";
	public static String SearchUp="jzeditor-searchup";
	public static String Remember="jzeditor-remember";
	public static String ShowTab="jzeditor-showtab";
	public static String ShowRet="jzeditor-showret";
	public static String ShowEOF="jzeditor-showeof";
	public static String ShowBigSpace="jzeditor-showbigspace";
	public static String ShowSpace="jzeditor-showspace";
	public static String ShowLineNum="jzeditor-showlinenum";
	public static String ShowRuler="jzeditor-showruler";
	public static String NewBinary="jzeditor-newbinary";
	public static String OpenBinary="jzeditor-openbinary";
	public static String DebugBinEditor="jzeditor-debugbineditor";
	public static String AutoTab="jzeditor-autotab";
	public static String SelectStart="jzeditor-selectstart";
	public static String FlipInsertMode="jzeditor-flipinsertmode";
	public static String MacroRecStart="jzeditor-macrorecstart";
	public static String MacroRecEnd="jzeditor-macrorecend";
	public static String MacroRecToggle="jzeditor-macrorectoggle";
	public static String MacroPlay="jzeditor-macroplay";

	public static String InsertCopyString="jzeditor-insert-copy-string";
	public static String InsertSearchString="jzeditor-insert-search-string";
	public static String InsertInputString="jzeditor-insert-input-string";
	public static String InsertDeleteString="jzeditor-insert-delete-string";
	public static String InsertDate="jzeditor-insert-date";
	public static String InsertFile="jzeditor-insert-file";
	public static String InsertFilename="jzeditor-insert-filename";
	public static String InsertHorizon="jzeditor-insert-horizon";
	public static String InsertTable="jzeditor-insert-table";
};

/*
shift pressed DELETE = cut-to-clipboard
pressed DELETE = delete-next
ctrl pressed DELETE = delete-next-word
shift ctrl pressed O = toggle-componentOrientation
shift pressed PAGE_UP = normal-begin
shift pressed PAGE_DOWN = normal-end
pressed BACK_SPACE = delete-previous
shift pressed END = selection-end-line
shift ctrl pressed PAGE_UP = selection-page-left
pressed ENTER = insert-break
ctrl pressed MINUS = macro-rec-toggle
pressed TAB = insert-tab
shift pressed HOME = selection-begin-line
shift pressed LEFT = caret-previous-word
shift ctrl pressed PAGE_DOWN = selection-page-right
shift pressed UP = normal-search-up
pressed COPY = copy-to-clipboard
shift pressed RIGHT = caret-next-word
pressed PASTE = paste-from-clipboard
shift ctrl pressed END = selection-end
shift pressed DOWN = normal-search-down
pressed CUT = cut-to-clipboard
shift pressed BACK_SPACE = delete-previous
shift ctrl pressed HOME = selection-begin
ctrl pressed INSERT = copy-to-clipboard
shift ctrl pressed T = previous-link-action
ctrl pressed BACK_SLASH = macro-rec-end
shift ctrl pressed LEFT = selection-previous-word
ctrl pressed BACK_SPACE = delete-previous-word
ctrl pressed H = delete-previous
shift ctrl pressed RIGHT = selection-next-word
pressed PAGE_DOWN = normal-search-down
ctrl pressed SPACE = activate-link-action
pressed KP_LEFT = caret-backward
pressed PAGE_UP = normal-search-up
pressed KP_RIGHT = caret-forward
pressed END = caret-end-line
pressed UP = caret-up
ctrl pressed PAGE_UP = normal-begin
pressed LEFT = caret-backward
ctrl pressed PAGE_DOWN = normal-end
pressed DOWN = caret-down
pressed RIGHT = caret-forward
ctrl pressed END = caret-end
shift pressed F8 = copy-to-clipboard
ctrl pressed LEFT = caret-begin-line
pressed F9 = paste-from-clipboard
ctrl pressed RIGHT = caret-end-line
pressed F8 = cut-to-clipboard
shift pressed INSERT = paste-from-clipboard
pressed F10 = normal-block-selection-begin
ctrl pressed T = next-link-action

*/
