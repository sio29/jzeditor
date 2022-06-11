/******************************************************************************
;	テキストエディタ
******************************************************************************/
package sio29.jzeditor.backends.j2d;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.awt.Container;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.MediaTracker;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ureg.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.lookandfeel.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.doctab.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;

import sio29.ulib.doubleboot.backends.j2d.*;

import sio29.jzeditor.backends.j2d.menu.*;
import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.bineditor.*;
import sio29.jzeditor.backends.j2d.commandline.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.panes.documenttab.*;
import sio29.jzeditor.backends.j2d.dialogs.option.*;
import sio29.jzeditor.backends.j2d.dialogs.docinfo.*;
import sio29.jzeditor.backends.j2d.dialogs.grep.*;
import sio29.jzeditor.backends.j2d.dialogs.search.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.dialogs.lineselector.*;
import sio29.jzeditor.backends.j2d.dialogs.confirm.*;
import sio29.jzeditor.backends.j2d.dialogs.selectdoc.*;
import sio29.jzeditor.backends.j2d.reg.*;
import sio29.jzeditor.backends.j2d.script.*;

import sio29.jzeditor.backends.j2d.jzfiler.*;

public class JzWindow extends JFrame{
	public interface Callback{
		public void DeleteWindowSub(JzWindow window);
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public ImageIcon GetIcon(String filename);
		public OptionData getOptionData();
		public String GetCurrentDir();
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
		public void ExecTagJump(UFile filename,int line);
		public ActionInfoMap getActionInfoMap();
		public String GetRegRootName();
		public UFileCurrentDrives GetUFileCurrentDrives();
		public ActionTbl[] getActionTbl();
		public UInputHistory getFileHistory();
		public NormalTextAttr GetNormAttr();
		public CommandLineAttr GetCommandLineAttr();
		public boolean CheckAutoTabFlg();
		public void OnSave();
		public void OnCloseTab(DocumentTabChildBase child);
		public void OnOpenDoc();
		public void OnExecFD();
		public void OnExecWZ(String[] m);
		public void OnOpenSub(UFile filename,boolean window_flg);
		public boolean OnOpenInnerSub(UFile filename);
		public boolean OnLookAndFeel(String cmd);
		public String getOptionLookAndFeel();
		public void setOptionLookAndFeel(String n);
		public boolean CanSave();
		public boolean CanUndo();
		public boolean CanRedo();
		public boolean IsSelected();
		public CharCode GetCharCode();
		public boolean CanMacroRecStart();
		public boolean CanMacroPlay();
		public void OnSelectConv(String cmd);
		public void OnChangeCode(String cmd);
		public boolean CheckBlockSelect();
		public void FlipBlockSelect();
		public RegOut getRegOut();
	}
	private Callback callback;
	private JFrame frame=this;
	private ToolBarPane toolBar;
	private StatusBarPane statusBar;
	private FunctionKeyPane function_key;
	private Container south_pane;
	private DocumentTab tabpane;
	//
	public JzWindow(final Callback callback){
		init(callback);
	}
	private void init(final Callback callback){
		this.callback=callback;
		init();
	}
	private void init(){
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {	  // 閉じられている
				OnCloseWindow();
			}
		});
		SetWindowIcon();
		SetLookAndFeel();
		//RemoveF10(frame.getInputMap());
		//
		InitActionMap();
		InitSouthPane();
		// ツールバーを作成する
		InitToolBar();
		//タブ初期化
//System.out.println("InitTab prev");
		InitTab();
//System.out.println("InitTab post");
		//FeedbackMenu();
	}
	public void OnCloseWindow(){
		CloseTabChildAll();
		dispose();
		callback.DeleteWindowSub(this);
	}
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
	//※一番親のWindowでF10を殺す
	protected void processKeyEvent(KeyEvent e){
		if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F10){
			e.consume();	//F10は処理済とする
			return;
		}
		super.processKeyEvent(e);
	}
	//========================================================
	// メニューバーを作成する
	//========================================================
	void execCommand(String cmd){
//System.out.println("JzWindow.execCommand:cmd:"+cmd);
		if(callback.OnLookAndFeel(cmd)){
System.out.println("OnLookAndFeel:"+cmd);
			return;
		}
		String strconv_type=StringConverter.getCommand(cmd);
		if(strconv_type!=null){
System.out.println("OnSelectConv:"+cmd);
			callback.OnSelectConv(strconv_type);
			return;
		}
		if(cmd.startsWith("CharaCode_")){
System.out.println("CharaCode_:"+cmd);
			String code=cmd.substring("CharaCode_".length());
			callback.OnChangeCode(code);
			return;
		}
		Action act=ActionTool.ExecAction(getActionMap(),cmd);
	}
//	void FeedbackMenu(){
//System.out.println("FeedbackMenu");
//	}
	void setCurrentMenuBar(){
//System.out.println("setCurrentMenuBar");
		boolean menu_show=false;
		DocumentTabChildBase child=GetCurrentTabChild();
		if(child!=null){
			MenuFeedback menu_feedback=((EditorTabBase)child).getMenuFeedback();
			if(menu_feedback!=null){
				UMenuBar menu_bar=menu_feedback.getMenuBar();
				//UMenuBar menu_bar=menu_feedback.createMenuBar();
				if(menu_bar!=null){
					getRootPane().setJMenuBar(((UMenuBarJ2D)menu_bar).getJMenuBar());
					menu_show=true;
				}
			}
		}
		if(!menu_show){
			getRootPane().setJMenuBar(null);
		}
	}
	//========================================================
	// ツールバーを作成する
	//========================================================
	public void InitToolBar() {
		final JzWindow _this=this;
		toolBar = new ToolBarPane();
		toolBar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				String cmd = ae.getActionCommand();
				_this.execCommand(cmd);
			}
		});
		getContentPane().add(toolBar, BorderLayout.NORTH);
		toolBar.setVisible(callback.GetUseToolBar());
	}
	private ToolBarPane getToolBar(){
		return toolBar;
	}
	public void FeedBackToolBar() {
		if(toolBar==null)return;
		boolean show_flg=false;
		DocumentTabChildBase child=GetCurrentTabChild();
		if(child!=null){
			ToolBarPane.ToolBarItem[][] items=((EditorTabBase)child).getToolBarItems();
			if(items!=null){
				toolBar.setItems(items);
				show_flg=callback.GetUseToolBar();
			}
		}
		toolBar.setVisible(show_flg);
		//FeedbackMenu();
	}
	//========================================================
	//	下ペイン作成(ファンクションキー+ステータスバー)
	//========================================================
	public void InitSouthPane(){
		south_pane=new Container();
		south_pane.setLayout(new BoxLayout(south_pane,BoxLayout.Y_AXIS));
		getContentPane().add(south_pane,BorderLayout.SOUTH);
		// ファンクションキー
		InitFunctionKey();
		// ステータスバーを作成する
		InitStatusBar();
	}
	//========================================================
	// ステータスバーを作成する
	//========================================================
	public void InitStatusBar() {
		statusBar =new StatusBarPane(new StatusBarPane.Callback(){
			public ImageIcon getWScaleIcon(){
				return GetIcon("data/wscale.png");
			}
		});
		south_pane.add(statusBar);
	}
	private StatusBarPane getStatusBar(){
		return statusBar;
	}
	public void FeedBackStatusBar(){
		if(statusBar==null)return;
		boolean show_flg=false;
		DocumentTabChildBase child=GetCurrentTabChild();
		if(child!=null){
			StatusBarPane.StateLabel[] labels=((EditorTabBase)child).getStatusBarLabels();
//System.out.println("FeedBackStatusBar():"+labels);
			if(labels!=null){
				statusBar.setLabels(labels);
				show_flg=callback.GetUseStatusBar();
				statusBar.setStatusBarMessage(((EditorTabBase)child).getStatusBarMessage());
			}
		}
		statusBar.setVisible(show_flg);
		//FeedbackMenu();
	}
	//========================================================
	// ファンクションキーを作成する
	//========================================================
	void InitFunctionKey(){
		function_key=new FunctionKeyPane();
		south_pane.add(function_key);
		function_key.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String cmd = e.getActionCommand();
//System.out.println("cmd:"+cmd);
				execCommand(cmd);
			}
		});
	}
	public void FeedBackFunctionKey(){
		if(function_key==null)return;
		boolean show_flg=false;
		DocumentTabChildBase child=GetCurrentTabChild();
		if(child!=null){
			FunctionKeyPane.Function[][] functions=((EditorTabBase)child).getFunctionKeyFunctions();
			if(functions!=null){
				function_key.SetDef(functions);
				show_flg=callback.GetUseFunctionKey();
				InputMap inputmap=((EditorTabBase)child).getFunctionKeyInputMap();
				ActionInfoMap actioninfomap=((EditorTabBase)child).getFunctionKeyActionInfoMap();
				if(inputmap!=null && actioninfomap!=null){
					function_key.makeKey(inputmap,actioninfomap);
				}
			}
		}
		function_key.setVisible(show_flg);
		//FeedbackMenu();
	}
	void SetFunctionKeyShift(boolean n) {
		if(function_key==null)return;
		function_key.SetFunction(n);
	}
	private FunctionKeyPane getFunctionKey(){
		return function_key;
	}
	//========================================================
	// アイコンイメージ
	//========================================================
	void SetWindowIcon(){
		try{
			URL url=this.getClass().getResource("data/icon.png");
			ImageIcon icon = new ImageIcon(url);
			if(icon.getImageLoadStatus()!=MediaTracker.COMPLETE){
				System.out.println("icon.png not found !!:"+url);
			}
			setIconImage(icon.getImage());
		}catch(Exception ex){}
	}
	public ImageIcon GetIcon(String filename){
		return callback.GetIcon(filename);
	}
	//====================================================
	//	タブ
	//====================================================
	public void InitTab(){
		final JzWindow _this=this;
		tabpane=new DocumentTab(new DocumentTab.Callback(){
			public void SetParentTitle(String n){
//System.out.println(""+n);
				_this.setTitle(n);
			}
			public void CloseTabEvent(DocumentTabChildBase child){
				callback.OnCloseTab(child);
			}
			public void OnChangeCurrentTab(){
				_this.OnChangeCurrentTab();
			}
		});
		this.add(tabpane);
	}
	//カレントタブが切り替わった
	public void OnChangeCurrentTab(){
//System.out.println("OnChangeCurrentTab");
		//FeedbackMenu();
		setCurrentMenuBar();
		
		FeedbackTitle();
		FeedBackStatusBar();
		FeedBackToolBar();
		FeedBackFunctionKey();
	}
	public void OutRegTabChild(){
		if(tabpane==null)return;
		tabpane.OutRegTabChild();
	}
	public void SetFocusCurrentTab(){
		if(tabpane==null)return;
		tabpane.SetFocusCurrentTab();
	}
	public DocumentTabChildBase GetCurrentTabChild(){
		if(tabpane==null)return null;
		return tabpane.GetCurrentTabChild();
	}
	public void SetCurrentTabChild(DocumentTabChildBase n){
		if(tabpane==null)return;
		tabpane.SetCurrentTabChild(n);
	}
	public DocumentTabChildBase[] GetTabChilds(){
		if(tabpane==null)return null;
		return tabpane.GetTabChilds();
	}
	public void InitTabChild(DocumentTabChildBase n){
		if(tabpane==null)return;
		if(n==null)return;
		tabpane.InitTabChild(n);
		SetDragAndDropTarget(((EditorTabBase)n).GetDropTarget());
	}
	public void SetFocusTabChild(DocumentTabChildBase child){
		if(tabpane==null)return;
		tabpane.SetFocusTabChild(child);
	}
	public int GetTabChildNum(){
		if(tabpane==null)return 0;
		return tabpane.GetTabChildNum();
	}
	public DocumentTabChildBase SearchTabFromClassInstance(Class p_instance){
		if(tabpane==null)return null;
		return tabpane.SearchTabFromClassInstance(p_instance);
	}
	public DocumentTabChildBase GetTabChildFromIndex(int i){
		if(tabpane==null)return null;
		return tabpane.GetTabChildFromIndex(i);
	}
	public void CloseTabChild(DocumentTabChildBase child){
		if(child==null)return;
		if(tabpane==null)return;
		int r=SaveConfirmDialog.NO;
		if(callback.getOptionData().save_ask_flg && ((EditorTabBase)child).IsDarty()){
			r=SaveConfirmDialog.open(this);
			if(r==SaveConfirmDialog.YES){
				callback.OnSave();
			}
		}
		if(r==SaveConfirmDialog.CANCEL)return;
		tabpane.CloseTabChild(child);
	}
	public void CloseTabChildAll(){
		if(tabpane==null)return;
		int num=tabpane.GetTabChildNum();
		for(int i=num-1;i>=0;i--){
			DocumentTabChildBase child=tabpane.GetTabChild(i);
			CloseTabChild(child);
		}
	}
	public void SetTitleCurrentTab(){
		if(tabpane==null)return;
		tabpane.SetTitleCurrentTab();
	}
	public void FeedbackTitle(){
		DocumentTabChildBase child=GetCurrentTabChild();
		if(child!=null){
			setTitle(child.GetTitleStr());
		}
	}
	public void SelectNextTab(){
		if(tabpane==null)return;
		tabpane.SelectNextTab();
	}
	public void SelectPrevTab(){
		if(tabpane==null)return;
		tabpane.SelectPrevTab();
	}
	public DocumentTabChildBase SearchTabFromClassAndFilename(final Class p_instance,final UFile filename){
		if(tabpane==null)return null;
		return tabpane.SearchTabFromClassAndFilename(p_instance,filename);
	}
	public DocumentTabChildBase SearchEmptyTab(final Class p_instance){
		if(tabpane==null)return null;
		return tabpane.SearchEmptyTab(p_instance);
	}
	public DocumentTabChildBase NewTabFromClass(final Class p_instance){
		DocumentTabChildBase child=SearchEmptyTab(p_instance);
		if(child!=null)return child;
		child=NewEditorTabBaseFromClass(p_instance);
		InitTabChild(child);
		return child;
	}
	public DocumentTabChildBase NewTabAndFocusFromClass(final Class p_instance){
		DocumentTabChildBase child=NewTabFromClass(p_instance);
		if(child==null)return null;
		SetFocusTabChild(child);
		return child;
	}
	
	public DocumentTabChildBase NewEditorTabBaseFromClass(final Class p_instance){
		final JzWindow _this=this;
		if(NormalText.class==p_instance){
			int compo_type=callback.getOptionData().getCompoType();
System.out.println("compo_type="+compo_type);
			return new NormalText(compo_type,new NormalText.Callback(){
				public boolean CheckBlockSelect(){
					return callback.CheckBlockSelect();
				}
				public void FlipBlockSelect(){
					callback.FlipBlockSelect();
				}
				public void SetTitleStr(String m){
					_this.setTitle(m);
				}
				public String GetCurrentDirSub(){
					return callback.GetCurrentDir();
				}
				public String SearchDialog(){
					return callback.SearchDialog();
				}
				public String[] ReplaceDialog(){
					return callback.ReplaceDialog();
				}
				public String InsertCopyStringDialog(){
					return callback.InsertCopyStringDialog();
				}
				public String InsertSearchStringDialog(){
					return callback.InsertSearchStringDialog();
				}
				public String InsertInputStringDialog(){
					return callback.InsertInputStringDialog();
				}
				public String InsertDeleteStringDialog(){
					return callback.InsertDeleteStringDialog();
				}
				public String InsertDateDialog(){
					return callback.InsertDateDialog();
				}
				public UFile InsertFileDialog(){
					return callback.InsertFileDialog();
				}
				public String InsertFilenameDialog(){
					return callback.InsertFilenameDialog();
				}
				public String InsertHorizonDialog(){
					return callback.InsertHorizonDialog();
				}
				public String[] InsertTableDialog(){
					return callback.InsertTableDialog();
				}
				public void ExecTagJumpSub(UFile filename,int line){
					callback.ExecTagJump(filename,line);
				}
				public boolean CheckAutoTabFlg(){
					return callback.CheckAutoTabFlg();
				}
				public NormalTextAttr GetNormAttr(){
					return callback.GetNormAttr();
				}
				public ActionInfoMap getActionInfoMap(){
					return callback.getActionInfoMap();
				}
//				public void FeedbackMenu(){
//					_this.FeedbackMenu();
//				}
				public void FeedbackTitle(){
					_this.FeedbackTitle();
				}
				public void FeedbackStatusBar(){
					_this.FeedBackStatusBar();
				}
				public void execCommand(String cmd){
					_this.execCommand(cmd);
				}
				public boolean GetUseToolBar(){
					return callback.GetUseToolBar();
				}
				public boolean GetUseStatusBar(){
					return callback.GetUseStatusBar();
				}
				public boolean GetUseFunctionKey(){
					return callback.GetUseFunctionKey();
				}
				public void setMenuFileHistory(UMenu menu){
					_this.setMenuFileHistory(menu);
				}
			});
		}else if(CommandLine.class==p_instance){
			int compo_type=callback.getOptionData().getCompoType();
System.out.println("compo_type="+compo_type);
			return new CommandLine(compo_type,new CommandLine.Callback(){
				public CommandLineAttr GetCommandLineAttr(){
					return callback.GetCommandLineAttr();
				}
				public String GetRegRootName(){
					return callback.GetRegRootName();
				}
				public void ExecTagJumpSub(UFile filename,int line){
					callback.ExecTagJump(filename,line);
				}
				public void OpenDoc(){
					callback.OnOpenDoc();
				}
				public void ExecFD(){
					callback.OnExecFD();
				}
				public void ExecWZ(String[] opt){
					callback.OnExecWZ(opt);
				}
				public UFileCurrentDrives GetUFileCurrentDrives(){
					return callback.GetUFileCurrentDrives();
				}
				public FunctionKeyPane getFunctionKey(){
					return _this.getFunctionKey();
				}
//				public void FeedbackMenu(){
//					_this.FeedbackMenu();
//				}
				public void FeedbackTitle(){
					_this.FeedbackTitle();
				}
				public void FeedbackStatusBar(){
					_this.FeedBackStatusBar();
				}
				public void execCommand(String cmd){
					_this.execCommand(cmd);
				}
				public boolean GetUseToolBar(){
					return callback.GetUseToolBar();
				}
				public boolean GetUseStatusBar(){
					return callback.GetUseStatusBar();
				}
				public boolean GetUseFunctionKey(){
					return callback.GetUseFunctionKey();
				}
				public ActionInfoMap getActionInfoMap(){
					return callback.getActionInfoMap();
				}
				public RegOut getRegOut(){
					return callback.getRegOut();
				}
				public void setMenuFileHistory(UMenu menu){
					_this.setMenuFileHistory(menu);
				}
			},CommandLine.TYPE_DOS);
		}else if(BinEditor.class==p_instance){
			return new BinEditor(new BinEditor.Callback(){
				public FunctionKeyPane getFunctionKey(){
					return _this.getFunctionKey();
				}
//				public void FeedbackMenu(){
//					_this.FeedbackMenu();
//				}
				public void FeedbackTitle(){
					_this.FeedbackTitle();
				}
				public void FeedbackStatusBar(){
					_this.FeedBackStatusBar();
				}
				public void execCommand(String cmd){
					_this.execCommand(cmd);
				}
				public boolean GetUseToolBar(){
					return callback.GetUseToolBar();
				}
				public boolean GetUseStatusBar(){
					return callback.GetUseStatusBar();
				}
				public boolean GetUseFunctionKey(){
					return callback.GetUseFunctionKey();
				}
				public ActionInfoMap getActionInfoMap(){
					return callback.getActionInfoMap();
				}
				public void setMenuFileHistory(UMenu menu){
					_this.setMenuFileHistory(menu);
				}
			});
		}
		return null;
	}
	//========================================================
	//ファイル履歴
	//========================================================
	private UFileHistoryMenu filehistmenu=new UFileHistoryMenu();
	void setMenuFileHistory(UMenu menu){
		if(menu==null)return;
		filehistmenu.SetMenuFileHistory(menu,callback.getFileHistory(),new UActionListener(){
			// イベント処理
			public void actionPerformed(UActionEvent ae) {
				String cmd = ae.getActionCommand();
				CheckFileHistoryCommand(cmd);
			}
		});
	}
	boolean CheckFileHistoryCommand(String cmd){
		UFile ufilename=filehistmenu.CheckFileHistoryCommandUFile(cmd);
		if(ufilename==null)return false;
		callback.OnOpenSub(ufilename,false);
		return true;
	}
	//========================================================
	// ドラッグ＆ドロップイベントを受け取る。
	//========================================================
	void SetDragAndDropTarget(Component c){
		UFileDropTargetBuilder.setFileDropTarget(new UComponentJ2D(c),new UFileDropTarget.Callback(){
			public void drop(UFile[] files){
				for(int i=0;i<files.length;i++){
					callback.OnOpenInnerSub(files[i]);
				}
			}
		});
	}
	//========================================================
	//========================================================
	public ActionMap getActionMap(){
		return getRootPane().getActionMap();
	}
	public ActionTbl[] getActionTbl(){
		return callback.getActionTbl();
	}
	public void InitActionMap(){
		ActionTbl.SetActionTbl(getActionMap(),getActionTbl());
	}
	//========================================================
	//
	//========================================================
	static ULookAndFeel lookandfeel;
	static void initLookAndFeel(){
		if(lookandfeel==null){
			lookandfeel=ULookAndFeelBuilder.create();
		}
	}
	public void SetLookAndFeel(){
		initLookAndFeel();
		String type=getOptionLookAndFeel();
		SetLookAndFeelShort(type);
	}
	public String getOptionLookAndFeel(){
		return callback.getOptionLookAndFeel();
	}
	public void setOptionLookAndFeel(String n){
		callback.setOptionLookAndFeel(n);
	}
	public boolean SetLookAndFeelShort(String type){
		initLookAndFeel();
		String lookAndFeel=lookandfeel.GetLookAndFeelLongName(type);
		if(lookAndFeel==null)return false;
		return SetLookAndFeel(lookAndFeel);
	}
	public boolean SetLookAndFeel(String lookAndFeel){
		initLookAndFeel();
		if(!lookandfeel.SetLookAndFeel(new UWindowJ2D(frame),lookAndFeel))return false;
		setOptionLookAndFeel(lookandfeel.GetNowLookAndFeel());
		return true;
	}
}
