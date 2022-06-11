/******************************************************************************
;	テキストエディタ
******************************************************************************/
package sio29.jzeditor.backends.j2d;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ureg.*;
//import sio29.ulib.ureg.backends.j2d.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.lookandfeel.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.doctab.*;
import sio29.ulib.doubleboot.*;
//import sio29.ulib.doubleboot.backends.j2d.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.bineditor.*;
import sio29.jzeditor.backends.j2d.commandline.*;
import sio29.jzeditor.backends.j2d.normaltext.*;
import sio29.jzeditor.backends.j2d.dialogs.filer.*;
import sio29.jzeditor.backends.j2d.dialogs.filer.normalfiler.*;
import sio29.jzeditor.backends.j2d.dialogs.filer.jzinnerfiler.*;
import sio29.jzeditor.backends.j2d.dialogs.option.*;
import sio29.jzeditor.backends.j2d.dialogs.docinfo.*;
import sio29.jzeditor.backends.j2d.dialogs.grep.*;
import sio29.jzeditor.backends.j2d.dialogs.search.*;
import sio29.jzeditor.backends.j2d.dialogs.insert.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.dialogs.lineselector.*;
import sio29.jzeditor.backends.j2d.dialogs.confirm.*;
import sio29.jzeditor.backends.j2d.dialogs.selectdoc.*;
import sio29.jzeditor.backends.j2d.panes.documenttab.*;
import sio29.jzeditor.backends.j2d.reg.*;
import sio29.jzeditor.backends.j2d.jzfiler.*;
import sio29.jzeditor.backends.j2d.script.*;
import sio29.jzeditor.backends.j2d.test.*;
import sio29.jzeditor.backends.j2d.command.*;

/******************************************************************************
;	
******************************************************************************/
public class JzEditor{
	//========================================================
	private static final long serialVersionUID = 8531245739641223373L;
	public static final String app_name="JzEditor";
	public static final String app_ver="0.01";
	public final static String regnode_base="/sio29/jzeditor";
	public final static String regnode_root=regnode_base+"/root";
	//========================================================
	// プライベート変数
	private ArrayList<JzWindow> windowlist=new ArrayList<JzWindow>();
	private JzWindow g_current_window;
	//
	private JzEditorRegPara regpara=new JzEditorRegPara(regnode_root);
	private boolean use_toolbar=false;
	private boolean use_statusbar=true;
	private boolean use_functionkey=true;
	private boolean blockselect_flg=false;
	private boolean new_visible_flg=false;
	//ファイルオープンモード
	private boolean open_mode=false;		//内部(false:内部,true:外部)
	//private boolean open_mode=true;		//外部
	private CommandLineAttr comline_attr=new CommandLineAttr();
	private NormalTextAttr norm_attr=new NormalTextAttr();
	private UDoubleBootChecker doubleboot_checker;
	private ComlineOption g_comline_option=new ComlineOption();
	private UFileCurrentDrives drive_currents=new UFileCurrentDrives();
	private ActionInfoMap g_actioninfo_map=new ActionInfoMap();
	//コマンド
	private GrepCommand grep=new GrepCommand(this);
	private SearchCommand search=new SearchCommand(this);
	private InsertCommand insert=new InsertCommand(this);
	//========================================================
	//========================================================
	public static void initClass(){
		try{
			Class.forName("sio29.ulib.ufile.url.UFileBaseURL");
			Class.forName("sio29.ulib.uimage.backends.j2d.canvas.UImageBaseJ2D");
			Class.forName("sio29.ulib.udlgbase.backends.j2d.UDlgBaseJ2D");
			Class.forName("sio29.ulib.utouch.backends.j2d.UTouchBaseJ2D");
			Class.forName("sio29.ulib.ureg.backends.j2d.URegBaseJ2D");
			Class.forName("sio29.ulib.ufont.backends.j2d.UFontBaseJ2D");
			Class.forName("sio29.ulib.usystem.backends.j2d.USystemBaseJ2D");
			Class.forName("sio29.ulib.doubleboot.backends.j2d.UDoubleBootCheckerBaseJ2D");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		RegOutBuilder.setRegNodeRoot(regnode_root);
	}
	// メインルーチン
	public static void main(String[] args) {
		initClass();
if(args.length>0 && args[0].toLowerCase().startsWith("--sshtest")){
	SSHTest.test();	//SSHテスト
}
		new JzEditor(args);
	}
	public JzWindow getCurrentWindow(){
		return g_current_window;
	}
	public boolean GetUseToolBar(){
		return use_toolbar;
	}
	public void setUseToolBar(boolean n){
		use_toolbar=n;
	}
	public boolean GetUseStatusBar(){
		return use_statusbar;
	}
	public void setUseStatusBar(boolean n){
		use_statusbar=n;
	}
	public boolean GetUseFunctionKey(){
		return use_functionkey;
	}
	
	public void setUseFunctionKey(boolean n){
		use_functionkey=n;
	}
	public boolean getUlockSelectFlg(){
		return blockselect_flg;
	}
	public void setUlockSelectFlg(boolean n){
		blockselect_flg=n;
	}
	public boolean getUpenMode(){
		return open_mode;
	}
	public void setUpenMode(boolean n){
		open_mode=n;
	}
	
	//========================================================
	//レジストリ
	//========================================================
	private RegOut regout;
	public void initRegOut(){
		if(regout==null){
			regout=RegOutBuilder.create();
		}
	}
	public RegOut getRegOut(){
		initRegOut();
		return regout;
	}
	
	public static boolean ReadReg(RegOut regout,RegPara regpara){
		return TextEditRegOut.ReadReg(regout,regpara.node_name,regpara);
	}
	public static boolean OutReg(RegOut regout,RegPara regpara){
		return TextEditRegOut.OutReg(regout,regpara.node_name,regpara);
	}
	public void readNowReg(){
		RegOut regout=getRegOut();
		//レジストリの読み込み
		ReadReg(regout,regpara);
		ReadRegCurrentDir();
	}
	public IRECT getStartWindowBounds(){
		return regpara.window;
	}
	public IVECTOR2 getStartWindowPos(){
		return regpara.window.getLocation();
	}
	public IVECTOR2 getStartWindowSize(){
		return regpara.window.getSize();
	}
	public IRECT getCurrentWindowRect(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		return ConvAWT.Rectangle_IRECT(frame.getBounds());
	}
	public void writeNowReg(){
		RegOut regout=getRegOut();
		if(!g_option.deletereg_flg){
			regpara.window=getCurrentWindowRect();
			//レジストリの書き込み
			OutReg(regout,regpara);
			OutRegCurrentDir();
			//
			OutRegFileHistory();
			OutRegTabChild();
			OutRegOption();
			//
			if(grep!=null)grep.outRegGrepOption(regout);
			if(search!=null)search.OutRegSearchOption();
			if(insert!=null)insert.OutRegInsertOption();
			//
			OutRegOpenDocHistory();
		}else{
			regout.DeleteRegNode(regnode_base);
			System.out.println("Delete Reg !!"+regnode_base);
		}
	}
	public String GetRegRootName(){
		return regnode_root;
	}
	public CommandLineAttr GetCommandLineAttr(){
		return comline_attr;
	}
	//========================================================
	//========================================================
	public static void setShotdown(final JzEditor t) {
		UShutdownHookBuilder.hook(new UShutdownHook.Callback(){
			public void shutdown(){
				t.writeNowReg();
			}
		});
	}
	public void OnCloseWindowExit(){
		//CloseTabChildAll();
		writeNowReg();
		System.exit(0);
	}
	public void setFocusWindowAndTab(){
		SetFocusCurrentTab();
		//
		JzWindow frame=getCurrentWindow();
		if(frame!=null){
			frame.toFront();
			frame.setFocusable(true);
		}
	}
	//========================================================
	// 初期化
	//========================================================
	// コンストラクタ
	public JzEditor(String[] args) {
		//コマンドラインの解析
		g_comline_option=ComlineOption.parseArgs(args);
//System.out.println("start---------------------------------------------");
		//レジストリの初期化
		//レジストリの読み込み
		ReadRegDialogOption();
		//多重起動のチェックをする
		if(g_option.doubleboot_flg){
			doubleboot_checker=UDoubleBootCheckerBuilder.create();
			doubleboot_checker.setPort(g_option.doubleboot_port);
			doubleboot_checker.addDoubleBootListener(new UDoubleBootListener(){
				public void actionDoubleBoot(String[] args){
					if(args.length==0){
						OnNew();
					}else{
						try{
							UFile filename=UFileBuilder.createLocal(args[0]);
							OnOpenInnerSub(filename);
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					setFocusWindowAndTab();
				}
			});
			if(!doubleboot_checker.checkDoubleWakeup(false,args)){
				return;
			}
		}
		setShotdown(this);
		//
		readNowReg();
		initFileHistory();
		//
		if(search!=null)search.InitSearchOption();
		if(insert!=null)insert.InitInsertOption();
		if(grep!=null)grep.initGrepOption();
		//
		initOpenDocHistory();
		initActionInfoMap();
		initFilerFilter();
		//
		initWindowList();
	}
	//========================================================
	// メニューバーを作成する
	//========================================================
//	void FeedbackMenu(){
//		JzWindow frame=getCurrentWindow();
//		if(frame==null)return;
//		frame.FeedbackMenu();
//	}
	//========================================================
	// アイコンイメージ
	//========================================================
	public ImageIcon GetIcon(String filename){
		try{
			URL url=this.getClass().getResource(filename);
			return new ImageIcon(url);
		}catch(Exception ex){
			return null;
		}
	}
	//====================================================
	//	タブ
	//====================================================
	public void initTab2(){
		boolean open_flg=false;
		OptionData option=getOptionData();
		ComlineOption comline=getComlineOption();
		//ファイルオープン
		if(comline.start_filenames.size()>0){
			FileOpenOpt opt=new FileOpenOpt(false,false,true);
			try{
				for(int i=0;i<comline.start_filenames.size();i++){
					UFile start_filename=comline.start_filenames.get(i);
					OnOpenInnerSub(start_filename,opt);
					open_flg=true;
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		//コマンドライン
		if(option.comline_flg || comline.shell_flg){
			InitCommandLine();
			open_flg=true;
		}
		//ファイラー
		if(comline.filer_flg){
			FileOpenOpt opt=new FileOpenOpt(false,false);
			OnOpenFileChooseUFile(opt,null);
			open_flg=true;
		}
		//何もファイルが開いてなければ新規ファイル
		if(!open_flg){
			OnNew();
		}
		//カレントタブにフォーカスを合わせる
		JzWindow window=getCurrentWindow();
		if(window!=null){
			SetFocusCurrentTab();
		}
		//
		new_visible_flg=true;
	}
	public void initWindowList(){
		initTab2();
	}
	//新規Window
	public JzWindow NewWindowSub(final Class p_instance,Object[] params){
		IRECT old_bounds=GetCreateWindowBounds();
		final JzEditor _this=this;
		JzWindow.Callback callback=new JzWindow.Callback(){
			public void DeleteWindowSub(JzWindow window){
				_this.DeleteWindowSub(window);
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
			public ImageIcon GetIcon(String filename){
				return _this.GetIcon(filename);
			}
			public OptionData getOptionData(){
				return _this.getOptionData();
			}
			public String GetCurrentDir(){
				return _this.GetCurrentDir();
			}
			public String SearchDialog(){
				if(_this.search!=null)return null;
				return _this.search.SearchDialog();
			}
			public String[] ReplaceDialog(){
				if(_this.search!=null)return null;
				return _this.search.ReplaceDialog();
			}
			public String InsertCopyStringDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertCopyStringDialog();
			}
			public String InsertSearchStringDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertSearchStringDialog();
			}
			public String InsertInputStringDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertInputStringDialog();
			}
			public String InsertDeleteStringDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertDeleteStringDialog();
			}
			public String InsertDateDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertDateDialog();
			}
			public UFile InsertFileDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertFileDialog();
			}
			public String InsertFilenameDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertFilenameDialog();
			}
			public String InsertHorizonDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertHorizonDialog();
			}
			public String[] InsertTableDialog(){
				if(_this.insert!=null)return null;
				return _this.insert.InsertTableDialog();
			}
			public void ExecTagJump(UFile filename,int line){
				_this.ExecTagJump(filename,line);
			}
			public ActionInfoMap getActionInfoMap(){
				return _this.getActionInfoMap();
			}
			public String GetRegRootName(){
				return _this.GetRegRootName();
			}
			public UFileCurrentDrives GetUFileCurrentDrives(){
				return _this.GetUFileCurrentDrives();
			}
			public ActionTbl[] getActionTbl(){
				return _this.getActionTbl();
			}
			public UFilenameInputHistory getFileHistory(){
				return _this.getFileHistory();
			}
			public NormalTextAttr GetNormAttr(){
				return _this.GetNormAttr();
			}
			public CommandLineAttr GetCommandLineAttr(){
				return _this.GetCommandLineAttr();
			}
			public boolean CheckAutoTabFlg(){
				return _this.CheckAutoTabFlg();
			}
			public void OnSave(){
				_this.OnSave();
			}
			public void OnCloseTab(DocumentTabChildBase child){
				_this.OnCloseTab((EditorTabBase)child);
			}
			public void OnOpenDoc(){
				_this.OnOpenDoc();
			}
			public void OnOpenSub(UFile filename,boolean window_flg){
				_this.OnOpenSub(filename,window_flg);
			}
			public boolean OnOpenInnerSub(UFile filename){
				return _this.OnOpenInnerSub(filename);
			}
			public void OnExecFD(){
				_this.OnExecFD();
			}
			public void OnExecWZ(String[] m){
				_this.OnExecWZ(m);
			}
			public boolean OnLookAndFeel(String cmd){
				return _this.OnLookAndFeel(cmd);
			}
			public String getOptionLookAndFeel(){
				return _this.getOptionLookAndFeel();
			}
			public void setOptionLookAndFeel(String n){
				_this.setOptionLookAndFeel(n);
			}
			public boolean CanSave(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.CanSave();
			}
			public boolean CanUndo(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.CanUndo();
			}
			public boolean CanRedo(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.CanRedo();
			}
			public boolean IsSelected(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.IsSelected();
			}
			public CharCode GetCharCode(){
				EditorTabBase child=_this.GetCurrentTabChild();
				if(child==null)return null;
				return child.GetCharCode();
			}
			public boolean CanMacroRecStart(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.CanMacroRecStart();
			}
			public boolean CanMacroPlay(){
				EditorTabBase documenttab=_this.GetCurrentTabChild();
				if(documenttab==null)return false;
				return documenttab.CanMacroPlay();
			}
			public void OnSelectConv(String cmd){
				_this.OnSelectConv(cmd);
			}
			public void OnChangeCode(String cmd){
				_this.OnChangeCode(cmd);
			}
			public boolean CheckBlockSelect(){
				return _this.CheckBlockSelect();
			}
			public void FlipBlockSelect(){
				_this.FlipBlockSelect();
			}
			public RegOut getRegOut(){
				return _this.getRegOut();
			}
		};
		final JzWindow window=new JzWindow(callback);
		windowlist.add(window);
		g_current_window=window;
		window.setBounds(ConvAWT.IRECT_Rectangle(old_bounds));
		if(p_instance!=null){
			window.NewTabAndFocusFromClass(p_instance);
		}
		if(new_visible_flg)SetFocusCurrentTab();
		return window;
	}
	//Window削除
	public void DeleteWindowSub(JzWindow window){
		windowlist.remove(window);
		//window.dispose();
		if(GetWindowNum()==0){
			OnCloseWindowExit();
		}
	}
	//ウィンドウの数獲得
	public int GetWindowNum(){
		return windowlist.size();
	}
	//ウィンドウ追加
	public void AddWindow(JzWindow window){
		windowlist.add(window);
	}
	//カレントウィンドウ設定
	public void SetCurrentWindow(JzWindow window){
		g_current_window=window;
	}
	//カレントウィンドウ獲得
	public JzWindow GetCurrentWindow(){
		return g_current_window;
	}
	public IRECT GetCreateWindowBounds(){
		JzWindow window=GetCurrentWindow();
		if(window==null){
			return getStartWindowBounds();
		}else{
			IRECT rect=ConvAWT.Rectangle_IRECT(window.getBounds());
			rect.x+=32;
			rect.y+=32;
			return rect;
		}
	}
	public IRECT GetCurrentWindowBounds(){
		JzWindow window=GetCurrentWindow();
		if(window==null){
			return getStartWindowBounds();
		}else{
			return ConvAWT.Rectangle_IRECT(window.getBounds());
		}
	}
	public IVECTOR2 GetCurrentWindowSize(){
		JzWindow window=GetCurrentWindow();
		if(window==null){
			return getStartWindowSize();
		}else{
			return ConvAWT.Dimension_IVEC2(window.getSize());
		}
	}
	//====================================================
	//	タブ
	//====================================================
	public void OutRegTabChild(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.OutRegTabChild();
	}
	public void SetFocusCurrentTab(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.setVisible(true);
		frame.SetFocusCurrentTab();
	}
	public void CloseTabChildAll(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.CloseTabChildAll();
	}
	public EditorTabBase GetCurrentTabChild(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		return (EditorTabBase)frame.GetCurrentTabChild();
	}
	//新しいタブの作成
	public EditorTabBase NewTabFromClass(final Class p_instance){
		JzWindow frame=getCurrentWindow();
		if(frame==null){
		//カレントウィンドウがなければ作成
			frame=NewWindowSub(null,null);
		}
		return (EditorTabBase)frame.NewTabFromClass(p_instance);
	}
	public EditorTabBase SearchTabFromClassInstance(Class p_instance){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		return (EditorTabBase)frame.SearchTabFromClassInstance(p_instance);
	}
	public void SetFocusTabChild(DocumentTabChildBase child){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.SetFocusTabChild(child);
	}
	public EditorTabBase SearchTabFromClassAndFilename(final Class p_instance,final UFile filename){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		return (EditorTabBase)frame.SearchTabFromClassAndFilename(p_instance,filename);
	}
	public EditorTabBase[] GetTabChilds(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		//return (EditorTabBase[])frame.GetTabChilds();
		//DocumentTabChildBase[]->EditorTabBase[]変換
		DocumentTabChildBase[] childs=frame.GetTabChilds();
		EditorTabBase[] ret_childs=new EditorTabBase[childs.length];
		for(int i=0;i<childs.length;i++)ret_childs[i]=(EditorTabBase)childs[i];
		return ret_childs;
	}
	public void SetTitleCurrentTab(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.SetTitleCurrentTab();
	}
	public void CloseTabChild(DocumentTabChildBase child){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.CloseTabChild(child);
	}
	public int GetTabChildNum(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return 0;
		return frame.GetTabChildNum();
	}
	public void SetCurrentTabChild(DocumentTabChildBase n){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.SetCurrentTabChild(n);
	}
	public void SelectNextTab(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.SelectNextTab();
	}
	public void SelectPrevTab(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		frame.SelectPrevTab();
	}
	public EditorTabBase NewTabAndFocusFromClass(final Class p_instance){
		JzWindow frame=getCurrentWindow();
		if(frame==null){
		//カレントウィンドウがなければ作成
			frame=NewWindowSub(null,null);
		}
		return (EditorTabBase)frame.NewTabAndFocusFromClass(p_instance);
	}
	public NormalText NewNormalText(){
		return (NormalText)NewTabAndFocusFromClass(NormalText.class);
	}
	public BinEditor NewBinEditor(){
		return (BinEditor)NewTabAndFocusFromClass(BinEditor.class);
	}
	//====================================================
	//	CommandLine
	//====================================================
	public EditorTabBase InitCommandLine(){
		return NewTabFromClass(CommandLine.class);
	}
	public CommandLine GetCommandLine(){
		return (CommandLine)SearchTabFromClassInstance(CommandLine.class);
	}
	//DOS実行
	public void DosExec() {
		OnOpenCommandLine();
		CommandLine command_line=GetCommandLine();
		if(command_line==null)return;
		UInputHistory historylist=command_line.GetComlineHistory();
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		String value=InputHistoryDialog.Open(frame,"コマンド",UFilenameTextFieldJ2D.UInputHistory2InputHistory(historylist));
		if(value==null)return;
		if(value.length()==0)return;
		command_line.ExecDosCommandWait_Sub(value);
	}
	//コマンドライン
	public void OnOpenCommandLine(){
		EditorTabBase command_line=GetCommandLine();
		if(command_line==null){
			command_line=InitCommandLine();
		}
		SetFocusTabChild(command_line);
	}
	public ComlineOption getComlineOption(){
		return g_comline_option;
	}
	
	//====================================================
	//カレントディレクトリ
	//====================================================
	void InitCurrentDir(){
		drive_currents=new UFileCurrentDrives();
		drive_currents.initUFileCurrentDrives();
	}
	public UFileCurrentDrives GetUFileCurrentDrives(){
		return drive_currents;
	}
	String GetCurrentDirRegName(){
		return GetRegRootName()+"/current_dir";
	}
	void ReadRegCurrentDir(){
		if(drive_currents==null){
			InitCurrentDir();
		}
		RegOut regout=getRegOut();
		drive_currents.readReg(regout,GetCurrentDirRegName());
		drive_currents.setNowCurrent();
	}
	void OutRegCurrentDir(){
		RegOut regout=getRegOut();
		drive_currents.outReg(regout,GetCurrentDirRegName());
	}
	public String GetCurrentDir(){
		String current_dir=null;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			current_dir=child.GetCurrentDir();
			if(current_dir!=null)return current_dir;
		}
		try{
			current_dir=new File(".").getCanonicalFile().getPath();
		}catch(Exception ex){}
		return current_dir;
	}
	//====================================================
	//タグジャンプ
	//====================================================
	public void ExecTagJump(UFile filename,int line){
		if(!open_mode){
		//内部
			OnOpenInnerSub(filename);
			//
			EditorTabBase child=SearchTabFromClassAndFilename(NormalText.class,filename);
			if(child!=null){
				child.SetFocus();
				if(child.CanJumpLine()){
					child.SetJumpLine(line);
				}
			}
		}else{
		//外部
			OnExecWZTagJump(filename,line);
		}
	}
	//========================================================
	//ファイル履歴
	//========================================================
	private UFilenameInputHistory filehist=new UFilenameInputHistory();
	void initFileHistory(){
		ReadRegFileHistory();
	}
	UFilenameInputHistory getFileHistory(){
		return filehist;
	}
	void AddFileHistory(UFile filename){
		filehist.Add(filename);
		//
		//JzWindow frame=getCurrentWindow();
		//if(frame!=null){
		//	frame.SetMenuFileHistory();
		//}
	}
	String GetFileHistoryRegNodeName(){
		return GetRegRootName()+"/FileHistory";
	}
	void ReadRegFileHistory(){
		RegOut regout=getRegOut();
		filehist.ReadReg(regout,GetFileHistoryRegNodeName());
	}
	void OutRegFileHistory(){
		RegOut regout=getRegOut();
		filehist.OutReg(regout,GetFileHistoryRegNodeName());
	}
	//========================================================
	//外部ファィラー
	//========================================================
	//WinFDの立ち上げ
	public void OnExecFD() {
		try{
			String cmd=g_option.filer_filename;
			String current_dir=GetCurrentDir();
			String[] cmds=new String[2];
			cmds[0]=cmd;
			cmds[1]=current_dir;
			ShellExec.ExecDosCommandNoWait_Sub(cmds,current_dir);
		}catch(Exception ex){
			System.out.println("FD失敗");
		}
	}
	//========================================================
	//外部エディタ
	//========================================================
	//WzEditorの立ち上げ
	public void OnExecWZ(String[] m) {
		try{
			String wz_filename=g_option.editor_filename;
			String current_dir=GetCurrentDir();
			String[] cmds=null;
			if(m==null){
				cmds=new String[1];
				cmds[0]=wz_filename;
			}else{
				cmds=new String[1+m.length];
				cmds[0]=wz_filename;
				for(int i=0;i<m.length;i++){
					cmds[i+1]=m[i];
				}
			}
			ShellExec.ExecDosCommandNoWait_Sub(cmds,current_dir);
		}catch(Exception ex){
			System.out.println("WZ失敗");
		}
	}
	public void OnExecWZTagJump(UFile filename,int line) {
		String[] opt;
		if(g_option.editor_opt_line){
			opt=new String[2];
			opt[0]=filename.getLocalFilename();
			opt[1]=g_option.editor_opt_tag+line;
		}else{
			opt=new String[1];
			opt[0]=filename.getLocalFilename();
		}
		OnExecWZ(opt);
	}
	//========================================================
	//文換
	//========================================================
	public EditorTabBase SelectDocDialog(){
//System.out.println("SelectDocDialog_01");
		EditorTabBase[] childs=GetTabChilds();
//System.out.println("SelectDocDialog_02");
		SelectDocDialog.Item[] docs=new SelectDocDialog.Item[childs.length];
		for(int i=0;i<childs.length;i++){
			EditorTabBase child=childs[i];
			String name="";
			if(child.GetFilename()==null){
				name="- 名称未設定 -";
			}else{
				name=child.GetFilename().getLocalFilename();
			}
			docs[i]=new SelectDocDialog.Item(child,name);
		}
		EditorTabBase current_doc=GetCurrentTabChild();
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		Object doc=SelectDocDialog.Open(frame,"ドキュメント選択",docs,current_doc);
		if(doc==null)return null;
		return (EditorTabBase)doc;
	}
	//========================================================
	//指定行へジャンプ
	//========================================================
	private LineSelectOption linesel_opt=new LineSelectOption();
	public int JumpLineDialog(){
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return -1;
		int line_num=child.GetTotalLineNum();
		String title="ジャンプ先の行番号("+line_num+")";
		JzWindow frame=getCurrentWindow();
		if(frame==null)return -1;
		int line=LineSelectorDialog.Open(frame,title,line_num,linesel_opt);
		return line;
	}
	//========================================================
	//オプション
	//========================================================
	private OptionData g_option=new OptionData();
	public OptionData getOptionData(){
		return g_option;
	}
	public void ReadRegDialogOption(){
		GetRegOption();
	}
	public String GetOptionRegNodeName(){
		return GetRegRootName()+"/Option";
	}
	public void GetRegOption(){
		SetOptionParam();
		RegOut regout=getRegOut();
		if(!g_comline_option.reset_reg){
			g_option.ReadReg(regout,GetOptionRegNodeName());
		}
		FeedbackOption();
		norm_attr.MakeAttr();
	}
	public void OutRegOption(){
//System.out.println("OutRegOption");
		SetOptionParam2();
		RegOut regout=getRegOut();
		g_option.OutReg(regout,GetOptionRegNodeName());
	}
	public void SetOptionParam(){
		g_option.col_back      =norm_attr.back_col;
		g_option.col_normal    =norm_attr.normal_col;
		g_option.col_select    =norm_attr.select_col;
		g_option.col_comment   =norm_attr.comment_col;
		g_option.col_str       =norm_attr.str_col;
		g_option.col_macro     =norm_attr.macro_col;
		g_option.font_name     =norm_attr.font_name;
		g_option.font_size     =norm_attr.font_size;
		g_option.col_backlog   =comline_attr.backlogback_col;
		g_option.lookandfeel   =LookAndFeelEx.GetNowLookAndFeel();
		g_option.inner_filer   =GetCurrentFilerIndex();
		SetOptionParam2();
	}
	public void SetOptionParam2(){
		g_option.col_tab       =norm_attr.tab_col;
		g_option.col_ret       =norm_attr.cr_col;
		g_option.col_eof       =norm_attr.eof_col;
		
		g_option.draw_tab      =norm_attr.draw_tab;
		g_option.draw_cr       =norm_attr.draw_cr;
		g_option.draw_eof      =norm_attr.draw_eof;
		g_option.draw_zenspace =norm_attr.draw_zenspace;
		g_option.draw_space    =norm_attr.draw_space;
		g_option.draw_linenum  =norm_attr.draw_linenum;
		g_option.draw_ruler    =norm_attr.draw_ruler;
	}
	public void FeedbackOption(){
		norm_attr.back_col       =g_option.col_back;
		norm_attr.normal_col     =g_option.col_normal;
		norm_attr.select_col     =g_option.col_select;
		norm_attr.comment_col    =g_option.col_comment;
		norm_attr.str_col        =g_option.col_str;
		norm_attr.macro_col      =g_option.col_macro;
		norm_attr.tab_col        =g_option.col_tab;
		norm_attr.cr_col         =g_option.col_ret;
		norm_attr.eof_col        =g_option.col_eof;
		norm_attr.draw_tab       =g_option.draw_tab;
		norm_attr.draw_cr        =g_option.draw_cr;
		norm_attr.draw_eof       =g_option.draw_eof;
		norm_attr.draw_zenspace  =g_option.draw_zenspace;
		norm_attr.draw_space     =g_option.draw_space;
		norm_attr.draw_linenum   =g_option.draw_linenum;
		norm_attr.draw_ruler     =g_option.draw_ruler;
		norm_attr.font_name    =g_option.font_name;
		norm_attr.font_size    =g_option.font_size;
		comline_attr.backlogback_col=g_option.col_backlog;
		SetLookAndFeelShort(g_option.lookandfeel);
		SetCurrentFilerIndex(g_option.inner_filer);
		SetInnerFiler();
	}
	public void UpdateOptionAttr(){
		norm_attr.MakeAttr();
		//
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	public void OptionDialog() {
		SetOptionParam();
		//
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		if(OptionDialog.Open(new UWindowJ2D(frame),g_option)){
			FeedbackOption();
			UpdateOptionAttr();
		}
	}
	public boolean CheckAutoTabFlg(){
		return g_option.auto_tab_flg;
	}
	public NormalTextAttr GetNormAttr(){
		return norm_attr;
	}
	//========================================================
	//選択
	//========================================================
	public boolean CheckBlockSelect(){
		return blockselect_flg;
	}
	public void FlipBlockSelect(){
		blockselect_flg=!blockselect_flg;
	}
	//========================================================
	//ルック&フィール切り替え
	//========================================================
	public String getOptionLookAndFeel(){
		return g_option.lookandfeel;
	}
	public void setOptionLookAndFeel(String n){
		g_option.lookandfeel=n;
	}
	public boolean SetLookAndFeelShort(String type){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return false;
		return frame.SetLookAndFeelShort(type);
	}
	public boolean SetLookAndFeel(String lookAndFeel){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return false;
		return frame.SetLookAndFeel(lookAndFeel);
	}
	//========================================================
	//ファイルオープン
	//========================================================
	//ファイラーテーブル
	private FilerBase[] filer_tbl=new FilerBase[]{
		new NormalFiler(),		//通常ファイラー
		new JzInnerFiler()		//Vzライクファイラー
	};
	private int now_filer_index=0;
	private FilerBase now_filer=filer_tbl[now_filer_index];
	void SetCurrentFilerIndex(int n){
		now_filer_index=n;
	}
	int GetCurrentFilerIndex(){
		return now_filer_index;
	}
	//ファイラー初期化
	void SetInnerFiler(){
		now_filer=filer_tbl[now_filer_index];
		initFilerFilter();
	}
	//ファイルオープン履歴
	private UFilenameInputHistory open_historylist=new UFilenameInputHistory();
	String GetOpenDocRegNodeName(){
		return GetRegRootName()+"/OpenDocHistory";
	}
	String GetFilerRegNodeName(){
		return GetRegRootName()+"/Filer";
	}
	void AddOpenFileHistory(UFile filename){
		open_historylist.Add(filename);
	}
	UFilenameInputHistory GetOpenFileHistory(){
		return open_historylist;
	}
	//拡張子の初期化
	void initFilerFilter(){
//System.out.println("initFilerFilter");
		final FileNameExtensionFilter[] def_filter={
			new FileNameExtensionFilter("*.txt", "txt","text","doc"),
			new FileNameExtensionFilter("*.java", "java"),
			new FileNameExtensionFilter("*.c *.cpp *.cc", "c","cpp","cc"),
			new FileNameExtensionFilter("*.h", "h","inc"),
			new FileNameExtensionFilter("*.php", "php"),
			new FileNameExtensionFilter("*.js", "js"),
			new FileNameExtensionFilter("*.asm", "asm","s"),
			new FileNameExtensionFilter("*.bat", "bat"),
		};
		now_filer.Init(def_filter);
		RegOut regout=getRegOut();
		now_filer.SetRegNodeName(regout,GetFilerRegNodeName());
	}
	void initOpenDocHistory(){
		RegOut regout=getRegOut();
		open_historylist.ReadReg(regout,GetOpenDocRegNodeName());
	}
	void OutRegOpenDocHistory(){
		RegOut regout=getRegOut();
		open_historylist.OutReg(regout,GetOpenDocRegNodeName());
	}
	class FileOpenOpt{
		boolean bin_flg;
		boolean add_flg;
		boolean window_flg;
		public FileOpenOpt(){
			this.bin_flg   =false;
			this.add_flg   =false;
			this.window_flg=false;
		}
		public FileOpenOpt(boolean bin_flg,boolean add_flg){
			this.bin_flg   =bin_flg;
			this.add_flg   =add_flg;
			this.window_flg=false;
		}
		public FileOpenOpt(boolean bin_flg,boolean add_flg,boolean window_flg){
			this.bin_flg   =bin_flg;
			this.add_flg   =add_flg;
			this.window_flg=window_flg;
		}
	}
	/*
	private UFile OpenFiler(FileOpenOpt opt){
		String current_dir=GetCurrentDir();
		JzWindow frame=getCurrentWindow();
		FilerBase.Callback callback=new FilerBase.Callback(){
			public void selectFile(UFile filename){
				if(OnOpenInnerSub(filename)){
					AddOpenFileHistory(filename);
				}
			}
		};
		return now_filer.OnOpenFileChooseUFile(frame,current_dir,GetUFileCurrentDrives(),callback);
	}
	*/
	private UFile OnOpenFileChooseUFile(FileOpenOpt opt,final FilerBase.Callback _callback){
		//initFilerFilter();
		JzWindow frame=getCurrentWindow();
		FilerBase.Callback callback=new FilerBase.Callback(){
			public void selectFile(UFile filename){
				if(_callback!=null){
					_callback.selectFile(filename);
				}else{
					if(OnOpenInnerSub(filename)){
						AddOpenFileHistory(filename);
					}
				}
			}
		};
		String current_dir=GetCurrentDir();
		return now_filer.OnOpenFileChooseUFile(frame,current_dir,GetUFileCurrentDrives(),callback);
	}
	public UFile OnSaveFileChooseUFile(UFile now_filename){
		initFilerFilter();
		JzWindow frame=getCurrentWindow();
		if(frame==null)return null;
		return now_filer.OnSaveFileChooseUFile(frame,now_filename);
	}
	//========================================================
	//ファイルオープン
	//========================================================
	//ファイルがバイナリか?
	private final static String[] bin_ext_tbl={
		"exe","com","bin","dat","jar","class","zip","lzh"
	};
	private static boolean CheckBinFilename(UFile filename){
		String ext=filename.getFileExt();
		for(int i=0;i<bin_ext_tbl.length;i++){
			if(ext.compareToIgnoreCase(bin_ext_tbl[i])==0)return true;
		}
		return false;
	}
	
	//ファイルを開く
	public void OnOpenSub(UFile filename,boolean window_flg){
		boolean bin_flg=CheckBinFilename(filename);
		OnOpenSub(filename,bin_flg,window_flg);
	}
	//ファイルを開く
	public void OnOpenSub(UFile filename,boolean bin_flg,boolean window_flg){
		if(!open_mode){
			FileOpenOpt opt=new FileOpenOpt(bin_flg,false,window_flg);
			OnOpenInnerSub(filename,opt);
		}else{
			OnOpenOuterSub(filename);
		}
	}
	//外部で開く
	public void OnOpenOuterSub(UFile filename) {
		filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),filename);
		String[] m=new String[1];
		m[0]=filename.getLocalFilename();
		OnExecWZ(m) ;
		AddFileHistory(filename);
		//FeedbackMenu();
	}
	//外部でタグジャンプ
	public void OnOpenOuterSubTabJump(UFile filename,int line) {
		filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),filename);
		OnExecWZTagJump(filename,line);
		AddFileHistory(filename);
		//FeedbackMenu();
	}
	public boolean OnOpenInnerSub(UFile filename) {
		return OnOpenInnerSub(filename,null);
	}
	//内部で開く
	public boolean OnOpenInnerSub(UFile filename,FileOpenOpt opt){
		//オプション
		if(opt==null){
			opt=new FileOpenOpt();
		}
		//ファイル名
		filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),filename);
		//タイプによるクラス
		Class _class=null;
		if(!opt.bin_flg){
			_class=NormalText.class;
		}else{
			_class=BinEditor.class;
		}
		//すでに同じファイルがある
		if(!opt.add_flg){
		//新規ですでに開いている場合
			EditorTabBase child=SearchTabFromClassAndFilename(_class,filename);
			if(child!=null){
				SetFocusTabChild(child);
				return true;
			}
		}
		//ファイルがあるか?
		if(!filename.isFile())return false;
		//
		boolean add_flg=opt.add_flg;
		EditorTabBase child=GetCurrentTabChild();
		if(add_flg && (child==null))add_flg=false;
		if(!add_flg){
		//新規
			child=NewTabFromClass(_class);
			if(!child.OnOpen(filename))return false;
			SetFocusTabChild(child);
			SetTitleCurrentTab();
			AddFileHistory(filename);
			//FeedbackMenu();
		}else{
		//追加
			if(!child.OnOpenAdd(filename))return false;
		}
		return true;
	}
	//ファイルオープン
	public void OpenDoc(boolean bin_flg,boolean window_flg){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		String value=InputHistoryDialog.Open(frame,"ファイルオープン",UFilenameTextFieldJ2D.UInputHistory2InputHistory(GetOpenFileHistory()));
		if(value==null)return;
		if(value.length()==0){
		//空の場合
//System.out.println("OpenDoc");
			FileOpenOpt opt=new FileOpenOpt(bin_flg,false,window_flg);
			OnOpenFileChooseUFile(opt,null);
		}else{
		//ファイル名指定があった場合
			UFile filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),value);
			OnOpenSub(filename,bin_flg,window_flg);
		}
	}
	//========================================================
	//========================================================
	// [ファイル]-[新規作成]
	public void OnNewWindow(){
		Class p_instance=NormalText.class;
		Object[] params=null;
		NewWindowSub(p_instance,params);
	}
	public void OnOpenWindow() {
		OpenDoc(false,true);
	}
	// [ファイル]-[新規作成]
	public void OnNew() {
		NewNormalText();
	}
	public void OnOpen() {
//System.out.println("OnOpen");
		OnOpenDoc();
	}
	public void OnOpenDoc(){
		OpenDoc(false,false);
	}
	/*
	// [ファイル]-[開く]
	public void OnOpenFiler(){
System.out.println("OnOpenFiler");
		FileOpenOpt opt=new FileOpenOpt(false,false);
		OnOpenFileChooseUFile(opt,new FilerBase.Callback(){
			public void selectFile(UFile filename){
				OnOpenInnerSub(filename,opt);
			}
		});
	}
	*/
	
	// [ファイル]-[既存のファイルへ追加]
	public void OnOpenAdd() {
		FileOpenOpt opt=new FileOpenOpt(false,true);
		OnOpenFileChooseUFile(opt,new FilerBase.Callback(){
			public void selectFile(UFile filename){
				OnOpenInnerSub(filename,opt);
			}
		});
	}
	
	// [ファイル]-[外部エディタで開く]
	public void OnOpenOutter() {
		FileOpenOpt opt=new FileOpenOpt();
		OnOpenFileChooseUFile(opt,new FilerBase.Callback(){
			public void selectFile(UFile filename){
				OnOpenOuterSub(filename);
			}
		});
	}
	//現在のテキストを外部エディタで開く
	public void OnOpenOutterNow() {
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return;
		if(!child.CanOpenOutter())return;
		UFile filename=child.GetFilename();
		if(filename==null){
			OnOpenOutter();
			return;
		}
		int line=child.GetNowLine()+1;
		OnOpenOuterSubTabJump(filename,line);
	}
	//外部ファイラー
	public void OnOuterFiler() {
		OnExecFD();
	}
	//バイナリエディタで開く
	public void OnNewBinEditor(){
		NewBinEditor();
	}
	//バイナリエディタで開く
	public void OnOpenBinEditor(){
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		String value=InputHistoryDialog.Open(frame,"ファイルオープン",UFilenameTextFieldJ2D.UInputHistory2InputHistory(GetOpenFileHistory()));
		if(value==null)return;
		if(value.length()==0){
		//空の場合
			FileOpenOpt opt=new FileOpenOpt(true,false);
			OnOpenFileChooseUFile(opt,null);
		}else{
		//ファイル名指定があった場合
			UFile filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),value);
			OnOpenSub(filename,true);
		}
	}
	//バイナリエディタデバッグ
	public void OnDebugBinEditor(){
		BinEditor bineditor=NewBinEditor();
		bineditor.SetDummyDocument();
	}
	//カレントタブを閉じる
	public void OnCurrentCloseTab(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			OnCloseTab(child);
		}
	}
	//指定のタブを閉じる
	public void OnCloseTab(DocumentTabChildBase child){
		if(child==null)return;
		CloseTabChild(child);
		if(GetTabChildNum()<=0){
			OnNew();
		}
	}
	//全てのタブを閉じる
	public void OnCloseAllTab(){
		CloseTabChildAll();
		OnNew();
	}
	//印刷
	public void OnPrint(){
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return;
		if(!child.CanPrint())return;
		child.OnPrint();
	}
	//文章の情報
	public void OnDocInfo(){
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return;
		child.OnDocInfo();
	}
	// [ファイル]-[上書き保存]
	public void OnSave() {
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return;
		if(!child.CanSave())return;
		UFile filename=child.GetFilename();
		if (filename == null) {
			OnSaveAs();
		} else {
			filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),filename);
			child.OnSave(filename);
		}
	}
	// [ファイル]-[名前を付けて保存]
	public void OnSaveAs() {
		EditorTabBase child=GetCurrentTabChild();
		if(child==null)return;
		if(!child.CanSave())return;
		UFile filename=OnSaveFileChooseUFile(child.GetFilename());
		if(filename==null)return;
		filename=UFileBuilderURL.getAbsoluteUFile(GetCurrentDir(),filename);
		if(child.OnSave(filename)){
			child.SetFilename(filename);
			SetTitleCurrentTab();
			AddFileHistory(filename);
			//FeedbackMenu();
		}
	}
	// [ファイル]-[終了]
	public void OnExit() {
		CloseTabChildAll();
		
		System.exit(0);
	}
	//アンドゥ
	public void OnUndo() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUndo();
		}
	}
	//リドゥ
	public void OnRedo() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnRedo();
		}
	}
	//全て選択
	public void OnSelectAll() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSelectAll();
		}
	}
	//選択クリア
	public void OnSelectClear() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSelectClear();
		}
	}
	//矩形選択
	public void OnBlockSelect(){
		FlipBlockSelect();
	}
	//タグジャンプ(内部)
	public void OnTagJump() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnTagJump();
		}
	}
	//タグジャンプ(外部)
	public void OnTagJumpOutter() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			FilenameLine fnl=child.GetTagJumpFilenameLine();
			if(fnl==null)return;
			OnExecWZTagJump(fnl.getFilename(),fnl.getLine());
		}
	}
	//先頭へ
	public void OnJumpTop(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnJumpTop();
		}
	}
	//文末へ
	public void OnJumpBottom(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnJumpBottom();
		}
	}
	//行頭へ
	public void OnJumpLineTop(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnJumpLineTop();
		}
	}
	//行末へ
	public void OnJumpLineBottom(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnJumpLineBottom();
		}
	}
	//検索
	public void OnSearch(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSearch();
		}
	}
	//置換
	public void OnReplace(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnReplace();
		}
	}
	//ドキュメント選択
	public void OnSelectDoc(){
		EditorTabBase child=SelectDocDialog();
		if(child==null)return;
		SetCurrentTabChild(child);
	}
	//Grep
	public void OnGrep(){
		if(grep!=null)grep.execGrep();
	}
	//指定行へジャンプ
	public void OnJumpLine(){
		int n=JumpLineDialog();
		if(n<0)return;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.SetJumpLine(n);
		}
	}
	//対カッコへ
	public void OnJumpPairKakko(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnJumpPairKakko();
		}
	}
	//比較
	public void OnCompare(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
//			child.OnCompare();
		}
	}
	//上へ検索
	public void OnSearchUp(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSearchUp();
		}
	}
	//下へ検索
	public void OnSearchDown(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSearchDown();
		}
	}
	//記憶
	public void OnRemember(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnRemember();
		}
	}
	
	// [編集]-[切り取り]
	public void OnCut() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnCut();
		}
	}
	// [編集]-[コピー]
	public void OnCopy() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnCopy();
		}
	}
	// [編集]-[貼り付け]
	public void OnPaste() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnPaste();
		}
	}
	// [表示]-[ルック＆フィール]
	public boolean OnLookAndFeel(String type) {
		return SetLookAndFeelShort(type);
	}
	//タブ表示
	public void OnShowTab(){
		norm_attr.draw_tab=!norm_attr.draw_tab;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//改行表示
	public void OnShowRet(){
		norm_attr.draw_cr=!norm_attr.draw_cr;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//EOF表示
	public void OnShowEOF(){
		norm_attr.draw_eof=!norm_attr.draw_eof;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//全角空白表示
	public void OnShowBigSpace(){
		norm_attr.draw_zenspace=!norm_attr.draw_zenspace;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//空白表示
	public void OnShowSpace(){
		norm_attr.draw_space=!norm_attr.draw_space;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//行番号表示
	public void OnShowLineNum(){
		norm_attr.draw_linenum=!norm_attr.draw_linenum;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//ルーラー表示
	public void OnShowRuler(){
		norm_attr.draw_ruler=!norm_attr.draw_ruler;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdateAttr();
		}
	}
	//オートタブ切り替え
	public void OnAutoTab(){
		g_option.auto_tab_flg=!g_option.auto_tab_flg;
	}
	// [ヘルプ]-[Help]
	public void OnHelp(){
		try{
			UFile filename=UFileBuilder.createLocal("help/index.html");
			java.awt.Desktop.getDesktop().open(((UFileURL)filename).getFile());
		}catch(Exception ex){
			System.out.println("Helpが開けません!!");
		}
	}
	// [ヘルプ]-[ScriptTest]
	public void OnScriptTest(){
		ScriptTest.test();
	}
	// [ヘルプ]-[バージョン情報]
	public void OnAbout() {
		JzWindow frame=getCurrentWindow();
		if(frame==null)return;
		String about_mess=app_name+" Ver"+app_ver;
		UOptionDialogBaseBuilder.showMessageDialog(new UWindowJ2D(frame),about_mess,app_name,UOptionDialogBase.INFORMATION_MESSAGE);
	}
	//次のタブ
	public void OnSelectNextTab() {
		SelectNextTab();
	}
	//前りタブ
	public void OnSelectPrevTab() {
		SelectPrevTab();
	}
	//DOS実行
	public void OnDosExec() {
		DosExec();
	}
	//オプション
	public void OnOption() {
		OptionDialog();
	}
	//ショートカット
	public void OnShortCut() {
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnShortCut();
		}
	}
	//アップデート
	public void OnUpdate(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnUpdate();
		}
	}
	//文字コード変更
	public void OnChangeCode(String code){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
//System.out.println("OnChangeCode:code="+code);
			child.OnChangeCode(code);
		}
	}
	//大文字小文字変換
	public void OnConvBigSmall(){
		OnSelectConv(StringConverter.TypeBigSmall);
	}
	//選択部分の変換
	public void OnSelectConv(String type){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSelectConv(type);
		}
	}
	//選択開始
	public void OnSelectStart(){
		norm_attr.draw_linenum=!norm_attr.draw_linenum;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnSelectStart();
		}
	}
	//ツールバー表示
	public void OnFlipToolBar(){
		use_toolbar=!use_toolbar;
		JzWindow frame=getCurrentWindow();
		if(frame!=null){
			frame.FeedBackToolBar();
		}
	}
	//ステータスバー表示
	public void OnFlipStatusBar(){
		use_statusbar=!use_statusbar;
		JzWindow frame=getCurrentWindow();
		if(frame!=null){
			frame.FeedBackStatusBar();
		}
	}
	//ファンクションキー表示
	public void OnFlipFunctionKey(){
		use_functionkey=!use_functionkey;
		JzWindow frame=getCurrentWindow();
		if(frame!=null){
			frame.FeedBackFunctionKey();
		}
	}
	//挿入モード
	public void OnFlipInsertMode(){
		norm_attr.draw_linenum=!norm_attr.draw_linenum;
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnFlipInsertMode();
		}
	}
	//マクロ記録開始
	public void OnMacroRecStart(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnMacroRecStart();
		}
	}
	//マクロ記録終了
	public void OnMacroRecEnd(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnMacroRecEnd();
		}
	}
	//マクロ記録トグル
	public void OnMacroRecToggle(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnMacroRecToggle();
		}
	}
	//マクロ再生
	public void OnMacroPlay(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnMacroPlay();
		}
	}
	//コピーした文字列の挿入
	public void OnInsertCopyString(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertCopyString();
		}
	}
	//検索した文字列の挿入
	public void OnInsertSearchString(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertSearchString();
		}
	}
	//入力した文字列の挿入
	public void OnInsertInputString(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertInputString();
		}
	}
	//削除した文字列の挿入
	public void OnInsertDeleteString(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertDeleteString();
		}
	}
	//日付の挿入
	public void OnInsertDate(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertDate();
		}
	}
	//ファイルの挿入
	public void OnInsertFile(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertFile();
		}
	}
	//ファイル名の挿入
	public void OnInsertFilename(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertFilename();
		}
	}
	//水平線の挿入
	public void OnInsertHorizon(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertHorizon();
		}
	}
	//テーブルの挿入
	public void OnInsertTable(){
		EditorTabBase child=GetCurrentTabChild();
		if(child!=null){
			child.OnInsertTable();
		}
	}
	//========================================================
	//
	//========================================================
	private ActionTbl[] g_act_tbl={
		new ActionTbl(JzEditorAction.NewWindow,this,"OnNewWindow"),
		new ActionTbl(JzEditorAction.OpenWindow,this,"OnOpenWindow"),
		new ActionTbl(JzEditorAction.New,this,"OnNew"),
		new ActionTbl(JzEditorAction.Open,this,"OnOpen"),
		new ActionTbl(JzEditorAction.OpenAdd,this,"OnOpenAdd"),
		new ActionTbl(JzEditorAction.OpenOutter,this,"OnOpenOutter"),
		new ActionTbl(JzEditorAction.OpenOutterNow,this,"OnOpenOutterNow"),
		new ActionTbl(JzEditorAction.OuterFiler,this,"OnOuterFiler"),
		new ActionTbl(JzEditorAction.Save,this,"OnSave"),
		new ActionTbl(JzEditorAction.SaveAs,this,"OnSaveAs"),
		new ActionTbl(JzEditorAction.CloseTab,this,"OnCurrentCloseTab"),
		new ActionTbl(JzEditorAction.CloseAllTab,this,"OnCloseAllTab"),
		new ActionTbl(JzEditorAction.OpenCommandLine,this,"OnOpenCommandLine"),
		new ActionTbl(JzEditorAction.Print,this,"OnPrint"),
		new ActionTbl(JzEditorAction.DocInfo,this,"OnDocInfo"),
		new ActionTbl(JzEditorAction.Exit,this,"OnExit"),
		new ActionTbl(JzEditorAction.Undo,this,"OnUndo"),
		new ActionTbl(JzEditorAction.Redo,this,"OnRedo"),
		new ActionTbl(JzEditorAction.SelectAll,this,"OnSelectAll"),
		new ActionTbl(JzEditorAction.SelectClear,this,"OnSelectClear"),
		new ActionTbl(JzEditorAction.BlockSelect,this,"OnBlockSelect"),
		new ActionTbl(JzEditorAction.TagJump,this,"OnTagJump"),
		new ActionTbl(JzEditorAction.TagJumpOutter,this,"OnTagJumpOutter"),
		new ActionTbl(JzEditorAction.JumpTop,this,"OnJumpTop"),
		new ActionTbl(JzEditorAction.JumpBottom,this,"OnJumpBottom"),
		new ActionTbl(JzEditorAction.JumpLineTop,this,"OnJumpLineTop"),
		new ActionTbl(JzEditorAction.JumpLineBottom,this,"OnJumpLineBottom"),
		new ActionTbl(JzEditorAction.Cut,this,"OnCut"),
		new ActionTbl(JzEditorAction.Copy,this,"OnCopy"),
		new ActionTbl(JzEditorAction.Paste,this,"OnPaste"),
		new ActionTbl(JzEditorAction.ToolBar,this,"OnFlipToolBar"),
		new ActionTbl(JzEditorAction.StatusBar,this,"OnFlipStatusBar"),
		new ActionTbl(JzEditorAction.FunctionKey,this,"OnFlipFunctionKey"),
		new ActionTbl(JzEditorAction.Help,this,"OnHelp"),
		new ActionTbl(JzEditorAction.About,this,"OnAbout"),
		new ActionTbl(JzEditorAction.ScriptTest,this,"OnScriptTest"),
		new ActionTbl(JzEditorAction.SelectNextTab,this,"OnSelectNextTab"),
		new ActionTbl(JzEditorAction.SelectPrevTab,this,"OnSelectPrevTab"),
		new ActionTbl(JzEditorAction.DosExec,this,"OnDosExec"),
		new ActionTbl(JzEditorAction.Option,this,"OnOption"),
		new ActionTbl(JzEditorAction.ShortCut,this,"OnShortCut"),
		new ActionTbl(JzEditorAction.Search,this,"OnSearch"),
		new ActionTbl(JzEditorAction.Replace,this,"OnReplace"),
		new ActionTbl(JzEditorAction.SelectDoc,this,"OnSelectDoc"),
		new ActionTbl(JzEditorAction.Grep,this,"OnGrep"),
		new ActionTbl(JzEditorAction.JumpLine,this,"OnJumpLine"),
		new ActionTbl(JzEditorAction.JumpPairKakko,this,"OnJumpPairKakko"),
		new ActionTbl(JzEditorAction.Compare,this,"OnCompare"),
		new ActionTbl(JzEditorAction.SearchDown,this,"OnSearchDown"),
		new ActionTbl(JzEditorAction.SearchUp,this,"OnSearchUp"),
		new ActionTbl(JzEditorAction.Remember,this,"OnRemember"),
		new ActionTbl(JzEditorAction.ShowTab,this,"OnShowTab"),
		new ActionTbl(JzEditorAction.ShowRet,this,"OnShowRet"),
		new ActionTbl(JzEditorAction.ShowEOF,this,"OnShowEOF"),
		new ActionTbl(JzEditorAction.ShowBigSpace,this,"OnShowBigSpace"),
		new ActionTbl(JzEditorAction.ShowSpace,this,"OnShowSpace"),
		new ActionTbl(JzEditorAction.ShowLineNum,this,"OnShowLineNum"),
		new ActionTbl(JzEditorAction.ShowRuler,this,"OnShowRuler"),
		new ActionTbl(JzEditorAction.NewBinary,this,"OnNewBinEditor"),
		new ActionTbl(JzEditorAction.OpenBinary,this,"OnOpenBinEditor"),
		new ActionTbl(JzEditorAction.DebugBinEditor,this,"OnDebugBinEditor"),
		new ActionTbl(JzEditorAction.AutoTab,this,"OnAutoTab"),
		new ActionTbl(JzEditorAction.SelectStart,this,"OnSelectStart"),
		new ActionTbl(JzEditorAction.FlipInsertMode,this,"OnFlipInsertMode"),
		new ActionTbl(JzEditorAction.MacroRecStart,this,"OnMacroRecStart"),
		new ActionTbl(JzEditorAction.MacroRecEnd,this,"OnMacroRecEnd"),
		new ActionTbl(JzEditorAction.MacroRecToggle,this,"OnMacroRecToggle"),
		new ActionTbl(JzEditorAction.MacroPlay,this,"OnMacroPlay"),
		new ActionTbl(JzEditorAction.InsertCopyString,this,"OnInsertCopyString"),
		new ActionTbl(JzEditorAction.InsertSearchString,this,"OnInsertSearchString"),
		new ActionTbl(JzEditorAction.InsertInputString,this,"OnInsertInputString"),
		new ActionTbl(JzEditorAction.InsertDeleteString,this,"OnInsertDeleteString"),
		new ActionTbl(JzEditorAction.InsertDate,this,"OnInsertDate"),
		new ActionTbl(JzEditorAction.InsertFile,this,"OnInsertFile"),
		new ActionTbl(JzEditorAction.InsertFilename,this,"OnInsertFilename"),
		new ActionTbl(JzEditorAction.InsertHorizon,this,"OnInsertHorizon"),
		new ActionTbl(JzEditorAction.InsertTable,this,"OnInsertTable"),
	};
	public ActionTbl[] getActionTbl(){
		return g_act_tbl;
	}
	private void initActionInfoMap(){
		g_actioninfo_map.addActionInfos(JzEditorActionInfo.getActionInfoTbl());
		ActionTbl.checkActionTbl(g_act_tbl);
	}
	public ActionInfoMap getActionInfoMap(){
		return g_actioninfo_map;
	}
}

/******************************************************************************
;	おしまい
******************************************************************************/
