/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;


import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.undo.UndoManager;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;

import javax.swing.text.JTextComponent;

import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.ruler.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.linenumber.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.*;
import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.panes.documenttab.*;
import sio29.jzeditor.backends.j2d.dialogs.docinfo.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.caret.*;
import sio29.jzeditor.backends.j2d.menu.*;

import sio29.jzeditor.backends.j2d.textcomponenttool.vram.*;
//import sio29.jzeditor.backends.j2d.normaltext.jtextpane.*;
//import sio29.jzeditor.backends.j2d.normaltext.jtextarea.*;
//import sio29.jzeditor.backends.j2d.textcomponenttool.jtextpane.*;
//import sio29.jzeditor.backends.j2d.textcomponenttool.jtextarea.*;
/******************************************************************************
;	
******************************************************************************/
public class NormalText extends Container implements EditorTabBase{
	public interface Callback{
		public boolean CheckBlockSelect();
		public void FlipBlockSelect();
		public void SetTitleStr(String m);
		public String GetCurrentDirSub();
		public String SearchDialog();
		public String[] ReplaceDialog();
		public String InsertCopyStringDialog();
		public String InsertSearchStringDialog();
		public String InsertInputStringDialog();
		public String InsertDeleteStringDialog();
		public String InsertDateDialog();
		public UFile InsertFileDialog();
		public String InsertFilenameDialog();
		public String InsertHorizonDialog();
		public String[] InsertTableDialog();
		public void ExecTagJumpSub(UFile filename,int line);
		public boolean CheckAutoTabFlg();
		public NormalTextAttr GetNormAttr();
		public ActionInfoMap getActionInfoMap();
		//public void FeedbackMenu();
		public void FeedbackTitle();
		public void FeedbackStatusBar();
		public void execCommand(String cmd);
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public void setMenuFileHistory(UMenu menu);
	}
	//
	private static final long serialVersionUID = 8531245739641223373L;
	//
	private int compo_type;							//
	private Callback callback;						//
	private UFile filename=null;					//ファイル名
	private CharCode chara_code=CharCode.SJIS;		//Charコード
	private String cr_type=CRType.CR_LF;			//改行タイプ
	private boolean has_eof=false;					//EOFコード(0x1a)の有無
	private NormalTextUndoManager um;				//Undoマネージャー
	private NormalTextAttr norm_attr;				//アトリュビュート設定
	private KeyBoardMacro kbmacro;					//キーボードマクロ
	private MenuFeedback main_menu;					//
	//
	private boolean search_flg=false;				//検索フラグ
	private String search_str=null;					//検索文字列
	private String replace_str=null;				//置換文字列
	private boolean is_select=false;				//選択中
	private int select_mark=0;						//選択位置
	private boolean remember_flg=false;				//記憶位置
	
	private JScrollPane scroll =null;				//スクロールPane
	private int scroll_unit=16;						//スクロール単位
	//ページアップダウンタイプ(false:キャレットが先移動、true:スクロールが先)
	private boolean pageupdown_type=false;
//	private boolean pageupdown_type=true;
	private JComponent g_textArea;					//テキスト
	private LineNumberPane g_linenum_pane;			//行番号
	private RulerPane g_ruler_pane;					//ルーラー
	private boolean make_attr_flg=false;			//色変えフラグ
	//選択
	private int select_mark_line_top;
	private boolean use_select_line_top;
	//
	private boolean normalize_caret_flg=false;				//キャレット正規化
	/*
	private ActionTbl[] g_act_tbl={
//		new ActionTbl("KeyPressed_ChangeBacklogMode",this,"FlipBacklogMode"		,KeyEvent.VK_ESCAPE,KeyEvent.SHIFT_DOWN_MASK),
//		new ActionTbl("KeyPressed_OpenFile"			,this,"OpenDoc"				,KeyEvent.VK_ESCAPE,0),
		new ActionTbl("KeyPressed_TagJump"			,this,"ExecTagJump"			,KeyEvent.VK_F10,KeyEvent.SHIFT_DOWN_MASK),
//		new ActionTbl("KeyPressed_EnterCommand"		,this,"ExecEnterCommand"	,KeyEvent.VK_ENTER,0),
//		new ActionTbl("F10-dummy"					,this,"F10Dummy"			,KeyEvent.VK_F10,0),
		new ActionTbl("block-selection-begin"		,this,"OnSelectStart"		,KeyEvent.VK_F10,0),
		new ActionTbl(DefaultEditorKit.copyAction	,this,"OnSelectCopy"		,KeyEvent.VK_F8,KeyEvent.SHIFT_DOWN_MASK),
		new ActionTbl(DefaultEditorKit.pasteAction	,this,"OnPaste"				,KeyEvent.VK_F9,0),
		new ActionTbl("memorize-word"				,this,"OnRemember"			,KeyEvent.VK_F5,0),
//		new ActionTbl("macro-rec-start"				,this,"MacroRecStart"		,KeyEvent.VK_MINUS,0),
//		new ActionTbl("macro-rec-end"				,this,"MacroRecEnd"			,KeyEvent.VK_MINUS,0),
		new ActionTbl("macro-rec-toggle"			,this,"MacroRecToggle"		,KeyEvent.VK_MINUS,KeyEvent.CTRL_DOWN_MASK),
		new ActionTbl("macro-play"					,this,"MacroPlay"			,KeyEvent.VK_BACK_SLASH,KeyEvent.CTRL_DOWN_MASK),
	};
	*/
	private ActionTbl[] g_act_tbl={
		new ActionTbl(NormalTextAction.TagJump				,this,"ExecTagJump"			),
		new ActionTbl(NormalTextAction.BloclSectionBegin	,this,"OnSelectStart"		),
		new ActionTbl(DefaultEditorKit.copyAction			,this,"OnSelectCopy"		),
		new ActionTbl(DefaultEditorKit.pasteAction			,this,"OnPaste"				),
		new ActionTbl(DefaultEditorKit.cutAction			,this,"OnVzCut"				),
		new ActionTbl(NormalTextAction.MemorizeWord			,this,"OnRemember"			),
//		new ActionTbl(KeyBoardMacroAction.MacroRecToggle	,this,"MacroRecToggle"		),
//		new ActionTbl(KeyBoardMacroAction.MacroPlay			,this,"MacroPlay"			),
		new ActionTbl(NormalTextAction.Undo					,this,"ExecUndo"			),
		new ActionTbl(NormalTextAction.Redo					,this,"ExecRedo"			),
		new ActionTbl(NormalTextAction.halfPageUpAction		,this,"OnHalfPageUp"		),
		new ActionTbl(NormalTextAction.halfPageDownAction	,this,"OnHalfPageDown"		),
		new ActionTbl(NormalTextAction.searchUpAction		,this,"OnSearchUp"			),
		new ActionTbl(NormalTextAction.searchDownAction		,this,"OnSearchDown"		),
		new ActionTbl(NormalTextAction.beginAction			,this,"OnJumpTop"			),
		new ActionTbl(NormalTextAction.endAction			,this,"OnJumpBottom"		),
		new ActionTbl(NormalTextAction.beginLineAction		,this,"OnJumpLineTop"		),
		new ActionTbl(NormalTextAction.endLineAction		,this,"OnJumpLineBottom"	),
		new ActionTbl(NormalTextAction.previousWordAction	,this,"OnJumpLineTop"		),
		new ActionTbl(NormalTextAction.nextWordAction		,this,"OnJumpLineBottom"	),
		
		//new ActionTbl(JzEditorAction.SelectStart            ,this,"OnSelectStart"),
		
		
	};
	//
	private InputTbl[] g_input_tbl={
		//SHIFT+↑、//SHIFT+↓
		//new InputTbl(KeyEvent.VK_UP			,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.pageUpAction),
		//new InputTbl(KeyEvent.VK_DOWN		,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.pageDownAction),
		//new InputTbl(KeyEvent.VK_UP			,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.halfPageUpAction),
		//new InputTbl(KeyEvent.VK_DOWN		,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.halfPageDownAction),
		new InputTbl(KeyEvent.VK_UP			,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.searchUpAction),
		new InputTbl(KeyEvent.VK_DOWN		,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.searchDownAction),
		new InputTbl(KeyEvent.VK_PAGE_UP	,0							,NormalTextAction.searchUpAction),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,0							,NormalTextAction.searchDownAction),
		//
//		new InputTbl(KeyEvent.VK_PAGE_UP	,KeyEvent.CTRL_DOWN_MASK	,DefaultEditorKit.beginAction),
//		new InputTbl(KeyEvent.VK_PAGE_DOWN	,KeyEvent.CTRL_DOWN_MASK	,DefaultEditorKit.endAction),
//		new InputTbl(KeyEvent.VK_PAGE_UP	,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.beginAction),
//		new InputTbl(KeyEvent.VK_PAGE_DOWN	,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.endAction),
		new InputTbl(KeyEvent.VK_PAGE_UP	,KeyEvent.CTRL_DOWN_MASK	,NormalTextAction.beginAction),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,KeyEvent.CTRL_DOWN_MASK	,NormalTextAction.endAction),
		new InputTbl(KeyEvent.VK_PAGE_UP	,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.beginAction),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.endAction),
		//
		new InputTbl(KeyEvent.VK_LEFT		,KeyEvent.CTRL_DOWN_MASK	,DefaultEditorKit.beginLineAction),
		new InputTbl(KeyEvent.VK_RIGHT		,KeyEvent.CTRL_DOWN_MASK	,DefaultEditorKit.endLineAction),
		new InputTbl(KeyEvent.VK_LEFT		,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.previousWordAction),
		new InputTbl(KeyEvent.VK_RIGHT		,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.nextWordAction),
		//
		new InputTbl(KeyEvent.VK_F10		,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.TagJump),
		new InputTbl(KeyEvent.VK_F10		,0							,NormalTextAction.BloclSectionBegin),
		//new InputTbl(KeyEvent.VK_F10		,0							,JzEditorAction.SelectStart),
		new InputTbl(KeyEvent.VK_F10		,Event.SHIFT_MASK			,JzEditorAction.TagJump),
		new InputTbl(KeyEvent.VK_F8			,0							,DefaultEditorKit.cutAction),
		new InputTbl(KeyEvent.VK_F8			,KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.copyAction),
		new InputTbl(KeyEvent.VK_F9			,0							,DefaultEditorKit.pasteAction),
		new InputTbl(KeyEvent.VK_F5			,0							,NormalTextAction.MemorizeWord),
		new InputTbl(KeyEvent.VK_MINUS		,KeyEvent.CTRL_DOWN_MASK	,KeyBoardMacroAction.MacroRecToggle),
		new InputTbl(KeyEvent.VK_BACK_SLASH	,KeyEvent.CTRL_DOWN_MASK	,KeyBoardMacroAction.MacroPlay),
		//
		new InputTbl(KeyEvent.VK_Z			,KeyEvent.CTRL_DOWN_MASK	,NormalTextAction.Undo),
		new InputTbl(KeyEvent.VK_Y			,KeyEvent.CTRL_DOWN_MASK	,NormalTextAction.Redo),
		//------------
		new InputTbl(KeyEvent.VK_N			,KeyEvent.CTRL_DOWN_MASK	,JzEditorAction.New),
		new InputTbl(KeyEvent.VK_F1			,0							,JzEditorAction.Open),
		new InputTbl(KeyEvent.VK_S			,Event.CTRL_MASK			,JzEditorAction.Save),
		new InputTbl(KeyEvent.VK_F12		,0							,JzEditorAction.OpenCommandLine),
		new InputTbl(KeyEvent.VK_P			,Event.CTRL_MASK			,JzEditorAction.Print),
		new InputTbl(KeyEvent.VK_B			,Event.CTRL_MASK			,JzEditorAction.DebugBinEditor),
		new InputTbl(KeyEvent.VK_F2			,0							,JzEditorAction.SelectNextTab),
		new InputTbl(KeyEvent.VK_F2			,Event.SHIFT_MASK			,JzEditorAction.SelectPrevTab),
		new InputTbl(KeyEvent.VK_Z			,Event.CTRL_MASK			,JzEditorAction.Undo),
		new InputTbl(KeyEvent.VK_Y			,Event.CTRL_MASK			,JzEditorAction.Redo),
		new InputTbl(KeyEvent.VK_X			,Event.CTRL_MASK			,JzEditorAction.Cut),
		new InputTbl(KeyEvent.VK_C			,Event.CTRL_MASK			,JzEditorAction.Copy),
		new InputTbl(KeyEvent.VK_V			,Event.CTRL_MASK			,JzEditorAction.Paste),
		new InputTbl(KeyEvent.VK_A			,Event.CTRL_MASK			,JzEditorAction.SelectAll),
		new InputTbl(KeyEvent.VK_D			,Event.CTRL_MASK			,JzEditorAction.SelectClear),
		new InputTbl(KeyEvent.VK_F4			,0							,JzEditorAction.BlockSelect),
		new InputTbl(KeyEvent.VK_INSERT		,0							,JzEditorAction.FlipInsertMode),
		new InputTbl(KeyEvent.VK_F6			,0							,JzEditorAction.Search),
		new InputTbl(KeyEvent.VK_F7			,0							,JzEditorAction.Replace),
		new InputTbl(KeyEvent.VK_F5			,0							,JzEditorAction.Remember),
		new InputTbl(KeyEvent.VK_HOME		,0							,JzEditorAction.JumpTop),
		new InputTbl(KeyEvent.VK_HOME		,Event.CTRL_MASK			,JzEditorAction.JumpBottom),
	};
	//========================================================
	// 初期化
	//========================================================
	public NormalText(int _compo_type,Callback callback){
		super();
		this.callback=callback;
		norm_attr=GetNormAttr();
		compo_type=_compo_type;
		InitTextArea(400,500);
		MakeCaret();
		JComponent textArea=GetTextArea();
		//TextComponentTool.RemoveF10(textArea.getInputMap());	//※
		
		InitUndo();
		
		TextComponentTool.addDocumentListener(textArea,new DocumentListener(){
			public void insertUpdate(DocumentEvent e){
				//記憶クリア
				ClearRemember();
			}
			public void removeUpdate(DocumentEvent e){
				//記憶クリア
				ClearRemember();
			}
			public void changedUpdate(DocumentEvent e){
			}
		});
		TextComponentTool.addUndoableEditListener(textArea,new UndoableEditListener(){
			//※
			public void undoableEditHappened(UndoableEditEvent e){
				//記憶クリア
				ClearRemember();
			}
		
		});
		if(textArea!=null){
			textArea.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					mousePopup(e);
				}
				public void mouseReleased(MouseEvent e){
					mousePopup(e);
				}
			});
		}
		SetOrgActionMap();
		InitActionMap();
		TextComponentTool.setDragEnabled(textArea,true);
		
		if(textArea!=null){
			ActionTool.HookActions(textArea.getActionMap(),new ActionHookProc(){
				public void actionPerformedHook(ActionData act_data){
					kbmacro.actionPerformedHook(act_data);
				}
			});
		}
		InitKeyBoardMacro();
		initMainMenu();
		
		//ステータスバーに表示
		PrintStatusBarNowCaret();
		PrintStatusInsertMode();
		PrintStatusSearch();
		//2ストロークフック
		//※動作していない
		//TwoStrokeTool.hook(textArea);
		//
		if(textArea!=null){
			textArea.addFocusListener(new FocusListener(){
				public void focusGained(FocusEvent e){
					if(callback!=null){
						callback.FeedbackTitle();
					}
				}
				public void focusLost(FocusEvent e){}
			});
		}
		initStatusBar();
	}
	
	//========================================================
	//ポップアップメニュー
	private void mousePopup(MouseEvent e) {
		if(e.isPopupTrigger()) {
System.out.println("Popup");
			//ポップアップメニューを表示する
			JComponent c = (JComponent)e.getSource();
			int x=e.getX();
			int y=e.getY();
			UPopupMenu popup_menu = UPopupMenuBuilder.create();
			ActionMap am = c.getActionMap();
			Action cut = am.get(DefaultEditorKit.cutAction);
			addMenu(popup_menu, "切り取り(X)", cut, 'X', KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
			Action copy = am.get(DefaultEditorKit.copyAction);
			addMenu(popup_menu, "コピー(C)", copy, 'C', KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
			Action paste = am.get(DefaultEditorKit.pasteAction);
			addMenu(popup_menu, "貼り付け(V)", paste, 'V', KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
			Action all = am.get(DefaultEditorKit.selectAllAction);
			addMenu(popup_menu, "すべて選択(A)", all, 'A', KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
			popup_menu.show(new UComponentJ2D(c), x, y);
		}
	}
	protected void addMenu(UPopupMenu popup_menu, String text, Action action, int mnemonic, KeyStroke ks) {
		/*
		if (action != null) {
			UMenuItem mi = popup_menu.add(action);
			if (text != null) {
				mi.setText(text);
			}
			if (mnemonic != 0) {
				mi.setMnemonic((char)mnemonic);
			}
			if (ks != null) {
				mi.setAccelerator(ks);
			}
		}
		*/
		String name="---";
		String cmd=(String )action.getValue(Action.NAME);
		UMenuItem menuitem=UMenuItemBuilder.create(text);
		if(mnemonic != 0) {
			menuitem.setMnemonic((char)mnemonic);
		}
		if(ks != null) {
			menuitem.setAccelerator(new UKeyStrokeJ2D(ks));
		}
		menuitem.addActionListener(new UActionListener(){
			public void actionPerformed(UActionEvent e){
//System.out.println("きた");
				action.actionPerformed(((UActionEventJ2D)e).getActionEvent());
			}
		});
		popup_menu.add(menuitem);
		
	}
	//========================================================
	public boolean CheckBlockSelect(){
		if(callback==null)return false;
		return callback.CheckBlockSelect();
	}
	public void FlipBlockSelect(){
		if(callback==null)return;
		callback.FlipBlockSelect();
	}
	public void SetTitleStr(String m){
		if(callback==null)return;
		callback.SetTitleStr(m);
	}
	String GetCurrentDirSub(){
		if(callback==null)return null;
		return callback.GetCurrentDirSub();
	}
	String SearchDialog(){
		if(callback==null)return null;
		return callback.SearchDialog();
	}
	String[] ReplaceDialog(){
		if(callback==null)return null;
		return callback.ReplaceDialog();
	}
	String InsertCopyStringDialog(){
		if(callback==null)return null;
		return callback.InsertCopyStringDialog();
	}
	String InsertSearchStringDialog(){
		if(callback==null)return null;
		return callback.InsertSearchStringDialog();
	}
	String InsertInputStringDialog(){
		if(callback==null)return null;
		return callback.InsertInputStringDialog();
	}
	String InsertDeleteStringDialog(){
		if(callback==null)return null;
		return callback.InsertDeleteStringDialog();
	}
	String InsertDateDialog(){
		if(callback==null)return null;
		return callback.InsertDateDialog();
	}
	UFile InsertFileDialog(){
		if(callback==null)return null;
		return callback.InsertFileDialog();
	}
	String InsertFilenameDialog(){
		if(callback==null)return null;
		return callback.InsertFilenameDialog();
	}
	String InsertHorizonDialog(){
		if(callback==null)return null;
		return callback.InsertHorizonDialog();
	}
	String[] InsertTableDialog(){
		if(callback==null)return null;
		return callback.InsertTableDialog();
	}
	void ExecTagJumpSub(UFile filename,int line){
		if(callback==null)return;
		callback.ExecTagJumpSub(filename,line);
	}
	boolean CheckAutoTabFlg(){
		if(callback==null)return false;
		return callback.CheckAutoTabFlg();
	}
	//StatusBarPane.StateLabel[] status_labels;
	public void initStatusBar(){
		//status_labels=NormalTextStatusBar.getLabels();
	}
	public StatusBarPane.StateLabel[] getStatusBarLabels(){
		return NormalTextStatusBar.getLabels();
		//return status_labels;
	}
	private StatusBarMessage statusbar_message=new StatusBarMessage();
	void printStatus(String type,Object m){
		statusbar_message.put(type,m);
		if(callback==null)return;
		callback.FeedbackStatusBar();
	}
	public StatusBarMessage  getStatusBarMessage(){
		return statusbar_message;
	}
	
	NormalTextAttr GetNormAttr(){
		if(callback==null)return null;
		return callback.GetNormAttr();
	}
	public FunctionKeyPane.Function[][] getFunctionKeyFunctions(){
		return NormalTextFunctionKey.getFunctions();
	}
	public InputMap getFunctionKeyInputMap(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return null;
		return textArea.getInputMap();
	}
	public ActionInfoMap getFunctionKeyActionInfoMap(){
		if(callback==null)return null;
		return callback.getActionInfoMap();
	}
	public ToolBarPane.ToolBarItem[][] getToolBarItems(){
		return NormalTextToolBar.getItems();
	}
	public MenuFeedback getMenuFeedback(){
		return main_menu;
	}
	private void initMainMenu(){
		final NormalText _this=this;
		NormalTextMenu.Callback menu_callback=new NormalTextMenu.Callback(){
			public void execCommand(String cmd){
				_this.execCommand(cmd);
			}
			public boolean CanSave(){
				return _this.CanSave();
			}
			public boolean CanUndo(){
				return _this.CanUndo();
			}
			public boolean CanRedo(){
				return _this.CanRedo();
			}
			public boolean IsSelected(){
				return _this.IsSelected();
			}
			public CharCode GetCharCode(){
				return _this.GetCharCode();
			}
			public boolean CanMacroRecStart(){
				return _this.CanMacroRecStart();
			}
			public boolean CanMacroPlay(){
				return _this.CanMacroPlay();
			}
			public boolean GetUseToolBar(){
				return _this.GetUseToolBar();
			}
			public boolean GetUseStatusBar(){
				return _this.GetUseStatusBar();
			}
			public boolean GetUseFunctionKey(){
				return _this.GetUseFunctionKey();
			}
			public NormalTextAttr GetNormAttr(){
				return _this.GetNormAttr();
			}
			public boolean CheckAutoTabFlg(){
				return _this.CheckAutoTabFlg();
			}
			public KeyStroke getCommandKeyStroke(String command){
				return _this.getCommandKeyStroke(command);
			}
			public char getCommandMnemonic(String command){
				return _this.getCommandMnemonic(command);
			}
			public String getCommandName(String command){
				return _this.getCommandName(command);
			}
			public void setMenuFileHistory(UMenu menu){
				_this.setMenuFileHistory(menu);
			}
		};
		main_menu=new NormalTextMenu(menu_callback);
		
	}
	public void execCommand(String cmd){
		if(callback==null)return;
		callback.execCommand(cmd);
	}
	public boolean GetUseToolBar(){
		if(callback==null)return false;
		return callback.GetUseToolBar();
	}
	public boolean GetUseStatusBar(){
		if(callback==null)return false;
		return callback.GetUseStatusBar();
	}
	public boolean GetUseFunctionKey(){
		if(callback==null)return false;
		return callback.GetUseFunctionKey();
	}
	public void printCommandKeyStroke(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		InputMap inputmap=textArea.getInputMap();
		KeyStroke[] keys=inputmap.allKeys();
		for(int i=0;i<keys.length;i++){
			KeyStroke key=keys[i];
			Object _action=inputmap.get(key);
			System.out.println("["+i+"]:name("+_action+"),string("+(_action instanceof String)+")");
		}
	}
	public KeyStroke getCommandKeyStroke(String command){
		JComponent textArea=GetTextArea();
		if(textArea==null)return null;
		InputMap inputmap=textArea.getInputMap();
		return ActionTool.getKeyStrokeFromCommand(inputmap,command);
	}
	public char getCommandMnemonic(String command){
		return 0;
	}
	public ActionInfoMap getActionInfoMap(){
		return callback.getActionInfoMap();
	}
	public String getCommandName(String command){
		if(callback==null)return null;
		ActionInfoMap actioninfomap=getActionInfoMap();
		if(actioninfomap==null)return null;
		ActionInfo info=actioninfomap.get(command);
		if(info==null)return null;
		return info.getMenuName();
	}
	public void setMenuFileHistory(UMenu menu){
		callback.setMenuFileHistory(menu);
	}
	//========================================================
	private void PrintStatusBar(String m){
		printStatus(NormalTextStatusBar.STR,m);
	}
	private void PrintStatusPos(String m){
		printStatus(NormalTextStatusBar.POS,m);
	}
	private void PrintStatusSelect(boolean is_select){
		printStatus(NormalTextStatusBar.SELECT,is_select);
	}
	private void PrintStatusIns(boolean n){
		printStatus(NormalTextStatusBar.INS,n);
	}
	private void PrintStatusSearchFlg(boolean search_flg){
		printStatus(NormalTextStatusBar.SEARCHF,search_flg);
	}
	private void PrintStatusSearch(String search_str){
		printStatus(NormalTextStatusBar.STR,search_str);
	}
	private void PrintStatusMacro(int state){
		printStatus(NormalTextStatusBar.MACRO,state);
	}
	//========================================================
	public NormalTextAttr GetNormalAttr(){
		return norm_attr;
	}
	//========================================================
	//アンドゥ
	//========================================================
	void InitUndo(){
		final NormalText p_normaltext=this;
		
		JComponent _textArea=p_normaltext.GetTextArea();
		if((_textArea instanceof JTextComponent)){
			/*
			um = new NormalTextUndoManagerImpl(new NormalTextUndoManagerImpl.Callback(){
				public int getCaretPosition(){
					JComponent textArea=p_normaltext.GetTextArea();
					if(textArea==null)return 0;
					return TextComponentTool.getCaretPosition(textArea);
				}
				public void setCaretPosition(int n){
					JComponent textArea=p_normaltext.GetTextArea();
					if(textArea==null)return;
					TextComponentTool.setCaretPosition(textArea,n);
				}
				public void addUndoableEditListener(UndoableEditListener n){
					JComponent textArea=p_normaltext.GetTextArea();
					if(textArea==null)return;
					TextComponentTool.addUndoableEditListener(textArea,n);
				}
			});
			*/
			um = new NormalTextUndoManagerImpl(_textArea);
		}else if(_textArea instanceof NormalTextVram){
//			um = new NormalTextUndoManager_VRAM(_textArea);
		}
	}
	void DisableUndo(){
		if(um==null)return;
		um.DisableUndo();
	}
	void EnableUndo(){
		if(um==null)return;
		um.EnableUndo();
	}
	void ClearUndoBuff(){
		if(um==null)return;
		um.ClearUndoBuff();
	}
	public boolean CanUndo(){
		if(um==null)return false;
		return um.CanUndo();
	}
	public boolean CanRedo(){
		if(um==null)return false;
		return um.CanRedo();
	}
	void ExecUndo(){
		if(um==null)return;
		um.ExecUndo();
	}
	void ExecRedo(){
		if(um==null)return;
		um.ExecRedo();
	}
	//========================================================
	//========================================================
	void SetOrgActionMap(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		ActionMap act_map=textArea.getActionMap();
//		new Act_backwardAction(act_map);
//		new Act_deletePrevCharAction(act_map);
//		new Act_deletePrevWordAction(act_map);
//		new Act_deleteNextCharAction(act_map);
//		new Act_deleteNextWordAction(act_map);
//		new Act_upAction(act_map);
//		new Act_downAction(act_map);
//		new Act_cutAction(act_map);
//		new Act_insertTabAction(act_map);
		new Act_insertBreakAction(act_map);			//改行
		new Act_cutAction(act_map);					//カット
		new Act_copyAction(act_map);				//コピー
		new Act_pasteAction(act_map);				//ペースト
		new Act_CaretPrev(act_map);					//
		new Act_CaretNext(act_map);					//
		new Act_CaretUp(act_map);					//
		new Act_CaretDown(act_map);					//
		new Act_WordPrev(act_map);					//
		new Act_WordNext(act_map);					//
//		new Act_SelectionPrev(act_map);				//
//		new Act_SelectionNext(act_map);				//
	}
	//アクション:改行
	class Act_insertBreakAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_insertBreakAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.insertBreakAction );
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			if(!isOvertypeMode()){
				OldActionPerformed(e);
			}else{
				JumpNextLineTop();
			}
			AutoTabInsert();
			MakeAttrColor();
		}
	}
	//アクション:カット
	class Act_cutAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_cutAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.cutAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			if(!CheckBlockSelect()){
				OldActionPerformed(e);
			}else{
				OnBlockCut();
			}
		}
	}
	//アクション:コピー
	class Act_copyAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_copyAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.copyAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			if(!CheckBlockSelect()){
				OldActionPerformed(e);
			}else{
				OnBlockCopy();
			}
		}
	}
	//アクション:ペースト
	class Act_pasteAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_pasteAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.pasteAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			OldActionPerformed(e);
			MakeAttrColor();
		}
	}
	//アクション:左移動
	class Act_CaretPrev extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_CaretPrev(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.backwardAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=false;
			OldActionPerformed(e);
			//g_textArea.getCaret().moveDot(g_textArea.getCaret().getDot()-1);
		}
	}
	//アクション:右移動
	class Act_CaretNext extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_CaretNext(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.forwardAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=false;
			OldActionPerformed(e);
		}
	}
	//アクション:上移動
	class Act_CaretUp extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_CaretUp(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.upAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=true;
			OldActionPerformed(e);
		}
	}
	//アクション:下移動
	class Act_CaretDown extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_CaretDown(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.downAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=true;
			OldActionPerformed(e);
		}
	}
	//アクション:1ワード左移動
	class Act_WordPrev extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_WordPrev(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.previousWordAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=false;
			/*
			//行頭か?
			boolean linetop_flg=isLineTop();
			int old_line=GetNowLine();			//移動前のライン
			//デフォルト移動
			OldActionPerformed(e);
			//行頭で行が変わっていたら、新しい行の行末へ移動(Wz4.0相当)
			int new_line=GetNowLine();			//現在のライン
//System.out.println("old_line="+old_line+",new_line="+new_line+",is_linetop="+linetop_flg);
			if(linetop_flg && old_line!=new_line){
				setCursorLineBottom(new_line);
			}
			*/
			/*
			//行頭か?
			if(isLineTop()){
				setCursorPrevLineBottom();
			}else{
				//デフォルト移動
				OldActionPerformed(e);
			}
			*/
			
			boolean linetop_flg=isLineTop();	//行頭か?
			int old_line=GetNowLine();			//移動前のライン
			//デフォルト移動
			OldActionPerformed(e);
			//
			int new_line=GetNowLine();			//現在のライン
			//行が変わっている?
			if(old_line!=new_line){
				if(!linetop_flg){
					setCursorLineTop(old_line);
				}else{
					setCursorLineBottom(old_line-1);
				}
			}
			
		}
	}
	//アクション:1ワード右移動
	class Act_WordNext extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_WordNext(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.nextWordAction);
		}
		public void actionPerformed(ActionEvent e){
			//PrintActName();
			use_select_line_top=false;
			/*
			//行末か?
			boolean linebottom_flg=isLineBottom();
			int old_line=GetNowLine();			//移動前のライン
			//デフォルト移動
			OldActionPerformed(e);
			//行末で行が変わっていたら、新しい行の行頭へ移動(Wz4.0相当)
			int new_line=GetNowLine();		//現在のライン
//System.out.println("old_line="+old_line+",new_line="+new_line+",is_linebottom="+linebottom_flg);
			if(linebottom_flg && old_line!=new_line){
				setCursorLineTop(new_line);
			}
			*/
			/*
			//行末か?
			if(isLineBottom()){
				setCursorNextLineTop();
			}else{
				//デフォルト移動
				OldActionPerformed(e);
			}
			*/
			boolean linebottom_flg=isLineBottom();	//行末か?
			int old_line=GetNowLine();				//移動前のライン
			//デフォルト移動
			OldActionPerformed(e);
			//行末で行が変わっていたら、新しい行の行頭へ移動(Wz4.0相当)
			int new_line=GetNowLine();		//現在のライン
			//行が変わっている?
			if(old_line!=new_line){
				if(!linebottom_flg){
					setCursorLineBottom(old_line);
				}else{
					setCursorLineTop(old_line+1);
				}
			}
			
		}
	}
	/*
	//アクション:選択左移動
	class Act_SelectionPrev extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_SelectionPrev(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.selectionBackwardAction);
		}
		public void actionPerformed(ActionEvent e){
			PrintActName();
			OldActionPerformed(e);
		}
	}
	//アクション:選択右移動
	class Act_SelectionNext extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_SelectionNext(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.selectionForwardAction);
		}
		public void actionPerformed(ActionEvent e){
			PrintActName();
			OldActionPerformed(e);
		}
	}
	*/
	//========================================================
	//※
	/*
	@Override
	protected void processKeyEvent(KeyEvent e){
System.out.println("processKeyEvent:"+e);
		//OSのF10動作を無視
		if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F10){
			e.consume();	//F10は処理済とする
			return;
		}
		super.processKeyEvent(e);
	}
	*/
	//========================================================
	//========================================================
	public Component GetBaseComponent(){
		return scroll;
	}
//	public JScrollPane GetScrollPane(){
//		return scroll;
//	}
	//タブタイトルの表示
	public String GetTabTitleStr(){
		String m="";
		if(filename==null){
			m="名称未設定";
		}else{
			try{
				m=filename.getFileBody();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return m;
	}
	//タイトルバーに文字列表示
	public String GetTitleStr(){
		String m="";
		if(filename==null){
			m="- 名称未設定 -";
		}else{
			String filename_m=filename.getLocalFilename();
			m=filename_m+" ("+GetCharCode().getName()+")";
			boolean bom_flg=GetBomFlg();
			if(bom_flg){
				m+="[BOM付き]";
			}
		}
		if(IsDarty()){
			m+="(変更)";
		}
		String __mm=TextComponentTool.getTextAreaTypeString(GetTextArea());
		m+=" ["+__mm+"]";
		return m;
	}
	public String GetStatusBarStr(){
		return "";
	}
	public String GetCurrentDir(){
		return null;
	}
	public void ReadReg(){}
	public void OutReg(){}
	public void OnVzCut(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.vzCut(textArea);
		MakeAttrColor();
	}
	public void OnLineCut(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.lineCut(textArea);
		MakeAttrColor();
	}
	public void OnCut(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.cut(textArea);
		MakeAttrColor();
	}
	public void OnCopy(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.copy(textArea);
	}
	public void OnPaste(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.paste(textArea);
		MakeAttrColor();
	}
	public void OnBlockCut(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.cutClipBoardHilighter(textArea);
		MakeAttrColor();
	}
	public void OnBlockCopy(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.copyClipBoardHilighter(textArea);
	}
	//印刷可能?
	public boolean CanPrint(){
		return true;
	}
	//プリント
	public void OnPrint(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		try{
System.out.println("OnPrint");
			PrintText.print(TextComponentTool.getText(textArea).toString(),filename);
		}catch(Exception ex){
			UOptionDialogBaseBuilder.showMessageDialog(new UComponentJ2D(this),
				"印刷に失敗しました",
				"印刷失敗",
				UOptionDialogBase.WARNING_MESSAGE);
		}
	}
	//文章の情報
	public void OnDocInfo(){
		DocInfoDialog.Open(this,this);
	}
	//ショートカット
	public void OnShortCut(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		try{
			UFile file=UFileBuilder.createLocal("normaltext.shortcut");
			if(ShortCutDialog.Open(this,textArea,file)){
				
			}
		}catch(Exception ex){}
	}
	//選択
	public boolean IsSelected(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.isSelected(textArea);
	}
	//全て選択
	public void OnSelectAll(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.selectAll(textArea);
	}
	//選択クリア
	public void OnSelectClear(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.selectClear(textArea);
	}
	//選択部分の変換
	public void OnSelectConv(String type){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		try{
			TextComponentTool.selectConv(textArea,type,norm_attr.attr_normal);
		}catch(Exception ex){
			System.out.println("変換に失敗しました!!:"+ex);
		}
	}
	//文字コード変換
	public void OnChangeCode(String code){
		CharCode new_charcode=CharCode.getCharCodeFromName(code);
		if(new_charcode==null)return;
		SetCharCode(new_charcode);
		if(callback!=null){
			callback.FeedbackTitle();
		}
	}
	//文字コード獲得
	public CharCode GetCharCode(){
		return chara_code;
	}
	//文字コード設定
	public void SetCharCode(CharCode n){
		chara_code=n;
	}
	//BOMがあるか?
	public boolean GetBomFlg(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.getBomFlg(textArea);
	}
	//BOMフラグを設定する
	public void SetBomFlg(boolean n){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.setBomFlg(textArea,n,norm_attr.attr_normal);
	}
	//改行タイプ
	public String GetCRType(){
		return cr_type;
	}
	//改行タイプ
	public void SetCRType(String n){
		cr_type=n;
	}
	//EOFがあるか
	public boolean GetEofFlg(){
		return has_eof;
	}
	//EOFがあるか
	public void SetEofFlg(boolean n){
		has_eof=n;
	}
	

	//タグジャンプ
	public void OnTagJump(){
		ExecTagJump();
	}
	//先頭行へジャンプ
	public void OnJumpTop(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpTop(textArea);
	}
	//最終行へジャンプ
	public void OnJumpBottom(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpBottom(textArea);
	}
	//行頭へジャンプ
	public void OnJumpLineTop(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLineTop(textArea);
	}
	//行末へジャンプ
	public void OnJumpLineBottom(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLineBottom(textArea);
	}
	//上書き時の改行(次の行への移動のみ)
	public void JumpNextLineTop(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpNextLineTop(textArea);
	}
	//アンドゥ
	public void OnUndo(){
		ExecUndo();
	}
	//リドゥ
	public void OnRedo(){
		ExecRedo();
	}
	//検索ダイアログ
	public void OnSearch(){
		String value=SearchDialog();
		if(value==null)return;
		//
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		try{
			int pos=TextComponentTool.getCaretPosition(textArea);
			int len=TextComponentTool.getDocumentLength(textArea);
			CharSequence text=TextComponentTool.getText(textArea,pos,len-pos);
			Pattern pattern= Pattern.compile(value);
			Matcher m = pattern.matcher(text);
			if(m.find()) {
				int start=m.start();
				int end=m.end();
				int new_pos=pos+start;
				TextComponentTool.setCaretPosition(textArea,new_pos);
			}
		}catch(Exception ex){
			
		}
	}
	//置換ダイアログ
	public void OnReplace(){
		String[] value=ReplaceDialog();
		if(value==null)return;
	}
	//更新
	public void OnUpdate(){
	}
	//更新後処理
	public void OnUpdateAttr(){
//System.out.println("OnUpdateAttr");
		JComponent textArea=GetTextArea();
		MakeAttr();
		MakeAttrColor();
		
		if(textArea!=null){
			textArea.updateUI();
		}
		InitLineNumberPane();
		InitRulerPane();
	}
	//セーブ可能?
	public boolean CanSave(){
		return true;
		//return CanUndo();
	}
	//ファイル名の設定
	public void SetFilename(UFile n){
		filename=n;
	}
	//ファイル名の獲得
	public UFile GetFilename(){
		return filename;
	}
	//ドロップ場所
	public Component GetDropTarget(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return null;
		return textArea;
	}
	//外部ファイラーでオープンできる?
	public boolean CanOpenOutter(){
		return true;
	}
	//編集された?
	public boolean IsDarty(){
		return CanUndo();
	}
	//総行数を求める
	public int GetTotalLineNum(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return 0;
		return TextComponentTool.getLineNum(textArea);
	}
	//サイズを求める
	public int GetTotalSize(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return 0;
		return TextComponentTool.getTotalSize(textArea);
	}
	//対の括弧へジャンプ
	public void OnJumpPairKakko(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpPairKakko(textArea);
	}
	//コピーした文字列の挿入
	public void OnInsertCopyString(){
//System.out.println("OnInsertCopyString");
		String value=InsertCopyStringDialog();
		if(value!=null){
		}
	}
	//検索した文字列の挿入
	public void OnInsertSearchString(){
//System.out.println("OnInsertSearchString");
		String value=InsertSearchStringDialog();
		if(value!=null){
		}
	}
	//入力した文字列の挿入
	public void OnInsertInputString(){
//System.out.println("OnInsertInputString");
		String value=InsertInputStringDialog();
		if(value!=null){
		}
	}
	//削除した文字列の挿入
	public void OnInsertDeleteString(){
//System.out.println("OnInsertDeleteString");
		String value=InsertDeleteStringDialog();
		if(value!=null){
		}
	}
	//日付の挿入
	public void OnInsertDate(){
//System.out.println("OnInsertDate");
		String value=InsertDateDialog();
		if(value!=null){
			this.InsertText(new Date().toString());
		}
	}
	//ファイルの挿入
	public void OnInsertFile(){
//System.out.println("OnInsertFile");
		UFile value=InsertFileDialog();
		if(value!=null){
		}
	}
	//ファイル名の挿入
	public void OnInsertFilename(){
//System.out.println("OnInsertFilename");
		String value=InsertFilenameDialog();
		if(value!=null){
		}
	}
	//水平線の挿入
	public void OnInsertHorizon(){
//System.out.println("OnInsertHorizon");
		String value=InsertHorizonDialog();
		if(value!=null){
			String m="-------------------------------------\n";
			this.InsertText(m);
		}
	}
	//テーブルの挿入
	public void OnInsertTable(){
//System.out.println("OnInsertTable");
		String[] value=InsertTableDialog();
		if(value!=null){
		}
	}
	
	//========================================================
	//========================================================
	//ファイルオープン
	public boolean OnOpen(UFile filename){
		TextData data=TextData.LoadText(filename);
		if(data==null)return false;
			String m=data.getNonBomTextString();
			if(m==null)return false;
			this.SetText(m);
			this.MakeAttrColor();
		//}
//System.out.println("OnOpenInner:"+data.filename+"("+filename+")");
		this.filename=data.GetFilename();
		SetCharCode(data.getCharCode());
		//読み込んだとこまでアンドゥバッファクリア
		ClearUndoBuff();
		return true;
	}
	public boolean OnOpenAdd(UFile filename){
		TextData data=TextData.LoadText(filename);
		if(data==null)return false;
		String m=data.getNonBomTextString();
		if(m==null)return false;
		this.InsertText(m);
		this.MakeAttrColor();
		return true;
	}
	//ファイル保存
	public boolean OnSave(UFile filename){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		if(filename == null)return false;
		if(!TextData.SaveText(filename,GetCharCode(),TextComponentTool.getText(textArea).toString()))return false;
		//アンドゥバッファクリア
		ClearUndoBuff();
		return true;
	}
	//========================================================
	//
	//========================================================
	//現在の位置
	public int GetNowPos(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return 0;
		return TextComponentTool.getCaretPosition(textArea);
	}
	//現在の行
	public int GetNowLine(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return 0;
		return TextComponentTool.getCaretLine(textArea);
	}
	//タグジャンプ用文字列からファイル名と行番号を得る
	public FilenameLine GetTagJumpFilenameLine(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return null;
		return TextComponentTool.getTagJumpFilenameLine(textArea,GetCurrentDirSub());
	}
	//タグジャンプへ
	public void ExecTagJump(){
		//タグジャンプ用文字列からファイル名と行番号を得る
		FilenameLine fnl=GetTagJumpFilenameLine();
		if(fnl==null)return;
		//タグジャンプ実行
		ExecTagJumpSub(fnl.getFilename(),fnl.getLine());
	}
	//行頭か?
	public boolean isLineTop(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.isLineTop(textArea);
	}
	//行末か?
	public boolean isLineBottom(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.isLineBottom(textArea);
	}
	//カーソルを行頭へ移動
	public void setCursorLineTop(int new_line){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpLineTop(textArea,new_line);
	}
	//カーソルを次の行の行頭へ移動
	public void setCursorNextLineTop(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLineNextLineTop(textArea);
	}
	//カーソルを行末へ移動
	public void setCursorLineBottom(int new_line){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpLineBottom(textArea,new_line);
	}
	//カーソルを前の行の行末へ移動
	public void setCursorPrevLineBottom(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLinePrevLineBottom(textArea);
	}
	//========================================================
	//	サイズ計算
	//========================================================
	public Dimension getTextSize(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return new Dimension(0,0);
		Dimension t_size=textArea.getPreferredSize();
		return t_size;
	}
	private Dimension getScrollSize(){
		Dimension s_size=scroll.getViewportBorderBounds().getSize();
		return s_size;
	}
	public Dimension calcBaseSize(){
		Dimension t_size=getTextSize();
		Dimension s_size=getScrollSize();
		int width =t_size.width;
		if(width<s_size.width)width=s_size.width;
		int height=t_size.height;
//		if(true)height+=(s_size.height/2);	//※画面サイズの半分足す
		return new Dimension(width,height);
	}
	//========================================================
	// 初期化
	//========================================================
	public boolean GetLineNumFlg(){
		return norm_attr.draw_linenum;
	}
	public void SetLineNumFlg(boolean n){
		norm_attr.draw_linenum=n;
	}
	public boolean GetRulerFlg(){
		return norm_attr.draw_ruler;
	}
	public void SetRulerFlg(boolean n){
		norm_attr.draw_ruler=n;
	}
	public JComponent GetTextArea(){
		return g_textArea;
	}
	public LineNumberPane GetLineNumberPane(){
		return g_linenum_pane;
	}
	public RulerPane GetRulerPane(){
		return g_ruler_pane;
	}
	private void InitTextArea(int width, int height){
//※※※
		final NormalText p_normaltext=this;
		g_textArea=TextComponentTool.createNormalTextComponent(compo_type);
		//
		JComponent textArea=GetTextArea();
		//if(textArea==null)return;
		TextComponentTool.initNormalTextJTextAreaDrawer(textArea);
		MakeAttr();
		TextComponentTool.setTextAttr(textArea,GetNormalAttr());
		TextComponentTool.setNormalizeCaretPosFunc(textArea,new NormalizeCaretPosFunc(){
			public void normalizeCaretPos(){
				NormalizeCaretPos();
			}
		});
		if(textArea!=null){
			textArea.setVisible(true);
		}
		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollBar sb=scroll.getVerticalScrollBar();
		sb.setUnitIncrement(scroll_unit);
		InitLineNumberPane();
		InitRulerPane();
		//
		DocumentFilter filter=new DocumentFilter(){
			//挿入
			@Override
			public void insertString(DocumentFilter.FilterBypass fb,int offset,String text,AttributeSet attr){
				replace(fb,offset,0,text,attr);
			}
			//置き換え
			@Override
			public void replace(DocumentFilter.FilterBypass fb,int offset,int length,String text,AttributeSet attrs){
				TextComponentTool.replace(fb,offset,length,text,attrs,isOvertypeMode(),GetTabSize());
			}
		};
		TextComponentTool.setDocumentFilter(textArea,filter);
	}
	//行番号初期化
	void InitLineNumberPane(){
		if(GetLineNumFlg()){
			if(scroll.getRowHeader()==null){
				LineNumberPane linenum_pane=new NormalTextLineNumberPane(this);			//行番号
				g_linenum_pane=linenum_pane;
				linenum_pane.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						if(!(e instanceof LineNumberPane.LineNumberEvnet))return;
						LineNumberPane.LineNumberEvnet ev=(LineNumberPane.LineNumberEvnet)e;
						int y=ev.getLine()+1;
						SetJumpLine(y);
//System.out.println("click:"+y);
					}
				});
				scroll.setRowHeaderView(linenum_pane);
				JComponent textArea=GetTextArea();
				TextComponentTool.addCaretListener(textArea,new CaretListener(){
					public void caretUpdate(CaretEvent e){
						linenum_pane.repaint();
					}
				});
			}
		}else{
			//scroll.setRowHeaderView(null);
			scroll.setRowHeader(null);
		}
	}
	//ルーラー初期化
	void InitRulerPane(){
		if(GetRulerFlg()){
			if(scroll.getColumnHeader()==null){
				RulerPane ruler_pane=new NormalTextRulerPane(this);				//ルーラー
				g_ruler_pane=ruler_pane;
				ruler_pane.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						if(!(e instanceof RulerPane.RulerEvnet))return;
						RulerPane.RulerEvnet ev=(RulerPane.RulerEvnet)e;
						int x=ev.getPos();
						int px=ev.getPointX();
						//SetJumpCurrentLinePos(x);
						
						SetJumpCurrentLinePointX(px);
//System.out.println("click:"+x);
					}
				});
				scroll.setColumnHeaderView(ruler_pane);
				JComponent textArea=GetTextArea();
				TextComponentTool.addCaretListener(textArea,new CaretListener(){
					public void caretUpdate(CaretEvent e){
						ruler_pane.repaint();
					}
				});
			}
		}else{
			//scroll.setColumnHeaderView(null);
			scroll.setColumnHeader(null);
		}
	}
	
	void StartTextArea(){
	}
	public void SetFocus(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		textArea.grabFocus();
	}
	/*
	//※
	Dimension getViewSize(){
		//return scroll.getViewport().getViewSize();
		return scroll.getViewportBorderBounds().getSize();
	}
	*/
	//テキスト設定
	public void SetText(String m){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.setText(textArea,m,norm_attr.attr_normal);
	}
	//テキスト設定
	public void InsertText(String m){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.insertText(textArea,m,norm_attr.attr_normal);
	}
	//指定行へジャンプできる?
	public boolean CanJumpLine(){
		return true;
	}
	//指定行へジャンプ
	public void SetJumpLine(int line){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpLineTop(textArea,line);
	}
	public void SetJumpCurrentLinePointX(int px){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLineAtPointX(textArea,px);
	}
	//現在の行の指定位置へジャンプ(ルーラーから)
	public void SetJumpCurrentLinePos(int colm){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.jumpCaretLineAtColumn(textArea,colm);
	}
	//ドキュメントが空?
	public boolean IsEmpty(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.isEmpty(textArea);
	}
	//========================================================
	//オートタブの挿入
	public void AutoTabInsert(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		if(!CheckAutoTabFlg())return;
		if(isOvertypeMode())return;
		TextComponentTool.insertAutoTab(textArea,norm_attr.attr_normal);
	}
	//========================================================
	//選択部分の上書き
	public void replaceSelection(String text) {
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		//上書きモード?
		if(isOvertypeMode()) {
			TextComponentTool.replaceSelectionText(textArea,text,norm_attr.attr_normal);
		}
	}
	
	//========================================================
	//ステータスバーに表示
	void PrintStatusBarNowCaret(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		try{
			int len=TextComponentTool.getDocumentLength(textArea);
			int pos=TextComponentTool.getCaretPosition(textArea);
			//行番号
			int line_num=TextComponentTool.getCaretLine(textArea);
			//桁番号
			int line_pos=TextComponentTool.getCaretColumn(textArea);
			//文字コード獲得
			int code=-1;
			if(pos==(len-1)){
				CharSequence s=TextComponentTool.getText(textArea,pos,1);
				code=s.charAt(0);
			}else if((pos+1)<len){
				CharSequence mm=TextComponentTool.getText(textArea,pos,2);
				char code0=mm.charAt(0);
				char code1=mm.charAt(1);
				if(!Character.isSurrogatePair(code0,code1)){
					code=code0;
				}else{
					code=Character.toCodePoint(code0,code1);
				}
			}
			//文字コード
			String code_m;
			if(code<0){
				code_m="---";
			}else if(code<0x100){
				code_m=String.format("(%02x)",code);
			}else if(code<0x10000){
				code_m=String.format("(%04x)",code);
			}else{
				code_m=String.format("(%06x)",code);
			}
			if(code!=-1){
				int code_width=TextTool.getCharWidth(code);
				code_m+=" "+((code_width==1) ? "半角":"全角");
			}
			Font font=textArea.getFont();
			int font_size=font.getSize();
			FontMetrics fm=getFontMetrics(font);
			int char_width=fm.charWidth(code);
			
			code_m+=" "+char_width+"/"+font_size+"="+((char_width>(font_size/2))?"zen":"han");
			String m=""+line_num+":"+line_pos+" "+code_m;
			PrintStatusPos(m);
		}catch(Exception ex){}
	}
	//========================================================
	//上書きモード切替
	public boolean isOvertypeMode(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return false;
		return TextComponentTool.isOvertypeMode(textArea);
	}
	//========================================================
	//アトリュビュートの作成
	//========================================================
	public void MakeAttr(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		textArea.setBackground(ConvAWT.CVEC2Color(norm_attr.back_col));
		TextComponentTool.setCaretColor(textArea,ConvAWT.CVEC2Color(norm_attr.cursor_col));
		TextComponentTool.setSelectionColor(textArea,ConvAWT.CVEC2Color(norm_attr.select_col));
		TextComponentTool.setSelectedTextColor(textArea,ConvAWT.CVEC2Color(norm_attr.normal_col));
		TextComponentTool.setDisabledTextColor(textArea,ConvAWT.CVEC2Color(norm_attr.normal_col));
		textArea.setForeground(ConvAWT.CVEC2Color(norm_attr.normal_col));
		/*
		if(textArea instanceof JTextPane){
		//JTextPane
			TextComponentTool.setCharacterAttributes(textArea,norm_attr.attr_normal);
		}else if(textArea instanceof JTextArea){
		//JTextArea
			TextComponentTool.setTabSize(textArea,GetTabSize());
			TextComponentTool.setLineWrap(textArea,false);
			TextComponentTool.setFont(textArea,norm_attr.getFont());
		}
		*/
		//
		TextComponentTool.setCharacterAttributes(textArea,norm_attr.attr_normal);
		//
		TextComponentTool.setTabSize(textArea,GetTabSize());
		TextComponentTool.setLineWrap(textArea,false);
		TextComponentTool.setFont(textArea,norm_attr.getFont());
		
	}
	public int GetTabSize(){
		return norm_attr.tab_size;
	}
	//========================================================
	//色変え
	//========================================================
	//色作成
	void MakeAttrColor(){
		if(make_attr_flg)return;
		make_attr_flg=true;
		DisableUndo();
		JComponent textArea=GetTextArea();
		TextComponentTool.makeNormalTextAttr(textArea,norm_attr);
		EnableUndo();
		make_attr_flg=false;
	}
	//========================================================
	//	選択開始
	//========================================================
	//選択開始
	public boolean IsSelect(){
		return is_select;
	}
	public void OnSelectStart(){
//System.out.println("NormalText:OnSelectStart");
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		is_select=!IsSelect();
		if(IsSelect()){
		//選択開始
//System.out.println("選択開始");
			select_mark=TextComponentTool.getCaretPosition(textArea);
			select_mark_line_top=TextComponentTool.getLineTopFromPos(textArea,select_mark);
			//use_select_line_top=false;
			use_select_line_top=true;
		}else{
		//選択キャンセル
//System.out.println("選択キャンセル");
			TextComponentTool.selectClear(textArea);
		}
		PrintStatusSelect(IsSelect());
		// の入力は無視
		//e.consume();
		//return;
	}
	//選択部分のコピー
	public void OnSelectCopy(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
//System.out.println("OnSelectCopy");
		TextComponentTool.copy(textArea);
		//範囲選択中
		if(IsSelect()){
			is_select=false;
			//
			TextComponentTool.selectClear(textArea);
		}
	}
	//
	//キャレット位置の正規化
	void NormalizeCaretPos(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		//範囲選択中
		if(IsSelect()){
			if(!normalize_caret_flg){
				normalize_caret_flg=true;
				//カーソルが移動した?
				int n=TextComponentTool.getCaretPosition(textArea);
				int line_top=TextComponentTool.getLineTopFromPos(textArea,n);
				if(line_top==select_mark_line_top || !use_select_line_top){
				//同じ行
				//選択開始位置から
					TextComponentTool.setSelctionBounds(textArea,select_mark,n);
				}else{
				//違う行
				//選択開始の行頭から
					TextComponentTool.setSelctionBounds(textArea,select_mark_line_top,n);
				}
				normalize_caret_flg=false;
			}
		}
	}
	//========================================================
	//挿入モードのフリップ
	public void OnFlipInsertMode(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.flipInsertMode(textArea);
		PrintStatusInsertMode();
	}
	public void PrintStatusInsertMode(){
		PrintStatusIns(isOvertypeMode());
	}
	//========================================================
	//記憶クリア
	public void ClearRemember(){
		remember_flg=false;
	}
	//記憶
	public void OnRemember(){
//System.out.println("記憶");
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		if(remember_flg){
		//前回と記憶位置が同じ
			
		}else{
		//前回と記憶位置が違う
			search_str="";
			try{
				int pos=TextComponentTool.getCaretPosition(textArea);
				int end_pos=TextComponentTool.getDocumentLength(textArea);
				CharSequence m=TextComponentTool.getText(textArea,pos,end_pos-pos);
				String[] ss=m.toString().split("[,(){}\n]");
				search_str=ss[0];
				search_flg=true;
				PrintStatusSearch();
			}catch(Exception ex){
//System.out.println("記憶エラー");
			}
		}
	}
	void PrintStatusSearch(){
		PrintStatusSearchFlg(search_flg);
		PrintStatusSearch(search_str);
	}
	//========================================================
	//半ページアップ、ダウン
	public boolean IsSearch(){
		return search_flg;
	}
	//上へ検索
	public void OnSearchUp(){
		if(IsSearch()){
		//検索文字がある
//System.out.println("OnSearchUp");
			
		}else{
		//検索文字がないのでページアップへ
			OnHalfPageUp();
		}
	}
	//下へ検索
	public void OnSearchDown(){
		if(IsSearch()){
		//検索文字がある
//System.out.println("OnSearchDown");
			
		}else{
		//検索文字がないのでページダウンへ
			OnHalfPageDown();
		}
	}
	
	//半ページアップ
	public void OnHalfPageUp(){
//System.out.println("OnHalfPageUp");
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		if(!pageupdown_type){
			//boolean scroll_to_cusor=false;
			boolean scroll_to_cusor=true;
			int size=0;
			TextComponentTool.halfPageUp(textArea,scroll,scroll_to_cusor,size);
		}
	}
	//半ページダウン
	public void OnHalfPageDown(){
//System.out.println("OnHalfPageDown");
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		if(!pageupdown_type){
			//boolean scroll_to_cusor=false;
			boolean scroll_to_cusor=true;
			int size=0;
			TextComponentTool.halfPageDown(textArea,scroll,scroll_to_cusor,size);
		}
	}
	//============================================
	//============================================
	public void F10Dummy(){
	}
	void InitActionMap(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		ActionTbl.SetActionTbl(textArea,g_act_tbl);
		ActionTool.SetInputTbl(textArea,g_input_tbl);
	}
	//========================================================
	//OSのF10機能を削る
	//========================================================
	//※
	/*
	public static void RemoveF10(InputMap im){
		KeyStroke f10=KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
		while(true){
			if(im==null)break;
			if(im.get(f10)!=null){
				System.out.println("RemoveF10:found !!");
			}
			im.remove(f10);
			im=im.getParent();
		}
	}
	*/
	//========================================================
	//========================================================
	/*
	static void RemoveF10Sub(JComponent cc){
		InputMap im=cc.getInputMap();
		while(true){
			if(im==null)break;
			im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
			im=im.getParent();
		}
		//Object key=im.get(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		//System.out.println(""+key);
	}
	*/
	//============================================
	//	キーボードマクロ
	//============================================
	public void InitKeyBoardMacro(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		kbmacro=new KeyBoardMacro();
		kbmacro.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				KeyBoardMacroChangeEvent ev=(KeyBoardMacroChangeEvent)e;
				PrintStatusMacro(ev.state);
			}
		});
		kbmacro.addMacroAction(textArea);
	}
	public void MacroRecStart(){
		kbmacro.MacroRecStart();
	}
	public void MacroRecEnd(){
		kbmacro.MacroRecEnd();
	}
	public void MacroRecToggle(){
		kbmacro.MacroRecToggle();
	}
	public void MacroPlay(){
//		kbmacro.MacroPlay();
		kbmacro.printMacro();
	}
	public void OnMacroRecStart(){
		MacroRecStart();
	}
	public void OnMacroRecEnd(){
		MacroRecEnd();
	}
	public void OnMacroRecToggle(){
		MacroRecToggle();
	}
	public void OnMacroPlay(){
		MacroPlay();
	}
	public boolean CanMacroRecStart(){
		return kbmacro.CanMacroRecStart();
	}
	public boolean CanMacroRecEnd(){
		return kbmacro.CanMacroRecEnd();
	}
	public boolean CanMacroPlay(){
		return kbmacro.CanMacroPlay();
	}
	//========================================================
	//========================================================
	void MakeCaret(){
		JComponent textArea=GetTextArea();
		if(textArea==null)return;
		TextComponentTool.initCaretShape(textArea);
		TextComponentTool.addCaretListener(textArea,new CaretListener(){
			public void caretUpdate(CaretEvent e){
				//記憶クリア
				ClearRemember();
				//ステータスバーに表示
				PrintStatusBarNowCaret();
			}
		});
	}
}
