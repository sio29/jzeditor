/******************************************************************************
;	Vzぽいファイラー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.AbstractAction;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ufile.backends.j2d.ftp.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.lookandfeel.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.confirm.*;
import sio29.ulib.ureg.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;


import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.dialogs.confirm.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.dialogs.grep.*;
import sio29.jzeditor.backends.j2d.jzfiler.preview.*;
import sio29.jzeditor.backends.j2d.jzfiler.ftp.*;

//ファイラーダイアログ
public class JzFilerDialog extends JDialog {
//public class JzFilerDialog extends UDialog.Impl {
	public interface Callback{
		public void selectFile(UFile filename);	//ファイル選択コールバック
	}
	private boolean result_flg=false;				//選択された?
	private UFile g_select_file=null;			//選択されたファイル
	private String g_node_name;						//レジストリノード名
	private JSplitPane prev_sp;						//リストとプレビューのスプリッタ
	private float list_sp_weight=0.5f;				//リストの分割割合
	private float prev_sp_weight=0.8f;				//リストとプレビューの分割割合
	private int sort_type=0;						//ソートタイプ
	private int sort_dir=0;							//ソート方向
	private boolean readonly_flg=false;				//リードオンリー
	private boolean openother_flg=false;			//他のアプリで開く
	private boolean twowindow_flg=true;				//2画面
	private boolean preview_flg=true;				//プレビュー表示
	private IRECT start_rect;						//開始サイズ
	//private JzFilerMenu filer_menu;					//メニュー
	private JzFilerPairFileList pair_list;			//ペアリスト
	private UFileCurrentDrives drive_currents;			//ドライブごとのカレントディレクトリ
	private JzFilerDirComparetor.DirCompareOpt compare_opt=new JzFilerDirComparetor.DirCompareOpt();	//比較オプション
	private HashSet<UFile> dir_history=new HashSet<UFile>();	//ディレクトリ履歴
	private HashSet<UFile> dir_list=new HashSet<UFile>();		//ディレクトリリスト
	private HashSet<File> mask_list=new HashSet<File>();				//マスクリスト
	private HashSet<UFile> file_history=new HashSet<UFile>();	//ファイル履歴
	private File current_mask=new File("*.*");							//現在のマスク
	private UFilenameFilter file_filter=null;							//ファイルフィルター
	private RegOut regout;
	private Callback callback=null;
	private FTPInfoList ftpinfo=new FTPInfoList();
	private JzFilerCommand command=new JzFilerCommand(this);
	//
	private InputTbl[] g_input_tbl={
		/*
		new InputTbl(KeyEvent.VK_UP			,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.searchUpAction),
		new InputTbl(KeyEvent.VK_DOWN		,KeyEvent.SHIFT_DOWN_MASK	,NormalTextAction.searchDownAction),
		new InputTbl(KeyEvent.VK_PAGE_UP	,0							,NormalTextAction.searchUpAction),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,0							,NormalTextAction.searchDownAction),
		*/
	};
	
	//
	public JzFilerDialog(Frame owner,UFile current_dir,UFileCurrentDrives _drive_currents,String title,RegOut _regout,String node_name){
		super(owner);
		init(current_dir,_drive_currents,title,_regout,node_name);
	}
	public JzFilerDialog(UFile current_dir,UFileCurrentDrives _drive_currents,String title,RegOut _regout,String node_name){
		super((Dialog)null,false);
		init(current_dir,_drive_currents,title,_regout,node_name);
	}
	private void init(UFile current_dir,UFileCurrentDrives _drive_currents,String title,RegOut _regout,String node_name){
		final JzFilerDialog _this=this;
		final JzFilerDialog p_this=this;
		setTitle(title);
		this.drive_currents=_drive_currents;
		regout=_regout;
		g_node_name=node_name+"/JzFiler";
		if(getParent()!=null){
			IRECT p_rect=ConvAWT.Rectangle_IRECT(getParent().getBounds());
			Dimension size=new Dimension(720,480);
			int x=p_rect.x+(p_rect.width -size.width )/2;
			int y=p_rect.y+(p_rect.height-size.height)/2;
			setBounds(x,y,size.width,size.height);
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		InitActionInfoMap();
		//
		dir_history.add(current_dir);
		mask_list.add(new File("*.*"));
		//
		ReadReg(regout,g_node_name);
		// ダイアログに表示するコンポーネントを設定
		setLayout(new BorderLayout());
		Container c = getContentPane();
		//
		//メニュー初期化
		UActionListener listener=new UActionListener(){
			public void actionPerformed(UActionEvent e){
				String cmd = e.getActionCommand();
				ActionTool.ExecAction(_this.getActionMap(),cmd);
			}
		};
		JzFilerMenu.Callback menu_callback=
		new JzFilerMenu.Callback(){
			public int getMenuSortType(){
				return _this.getSortType();
			}
			public int getMenuSortDir(){
				return _this.getSortDir();
			}
			public boolean getMenu2WindowFlg(){
				return _this.get2WindowFlg();
			}
			public boolean getMenuPreviewFlg(){
				return _this.getPreviewFlg();
			}
			public boolean getMenuOpenOtherFlg(){
				return _this.getOpenOtherFlg();
			}
			public boolean getMenuReadOnlyFlg(){
				return _this.getReadOnlyFlg();
			}
			//
			public KeyStroke getCommandKeyStroke(String command){
				return _this.getCommandKeyStroke(command);
			}
			public char getCommandMnemonic(String command){
				return _this.getCommandMnemonic(command);
			}
			public String getCommandName(String command){
				return _this.getCommandName(command);
			}
		};
		//filer_menu=new JzFilerMenu(menu_callback);
		//JMenuBar menuBar=filer_menu.MakeMenu(listener);
		//setJMenuBar(menuBar);
		setJMenuBar(((UMenuBarJ2D)JzFilerMenu.MakeMenu(menu_callback,listener)).getJMenuBar());
		//リスト初期化
		InitPairFileList(new UFile[]{current_dir,current_dir},new UFileCurrentDrives[]{drive_currents,drive_currents});
		//プレビュー初期化
		InitPreview();
		//画面分割初期化
		prev_sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,GetPairFileList(),GetPreview());
		//prev_sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,GetPairFileList(),GetPreview());
		prev_sp.setResizeWeight(prev_sp_weight);
		prev_sp.setDividerLocation(prev_sp_weight);
		c.add("Center",prev_sp);
		/*
		float h=(float)prev_sp.getResizeWeight();
		float d=(float)prev_sp.getDividerLocation();
		System.out.println("prev_sp_weight="+prev_sp_weight+" -> "+h+","+d);
		prev_sp.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				System.out.println("resize");
			}
			public void componentShown(ComponentEvent e){
				System.out.println("show");
				float h=(float)prev_sp.getResizeWeight();
				float d=(float)prev_sp.getDividerLocation();
				System.out.println(""+h+","+d);
			}
		});
		*/
		//
		initAction();
		InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),JzFilerAction.Close);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,0),JzFilerAction.Copy);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M,0),JzFilerAction.MaskList);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),JzFilerAction.ChangeDirParent);
		//im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10,0),"none");
		//
		FeedBackReg();
		InitActionMap();
		Dialog.ModalityType modal_type=getModalityType();
		//
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(preview!=null){
					preview.dispose();
				}
			}
			/*
			public void windowActivated(WindowEvent e){
				System.out.println("windowActivated");
				float h=(float)prev_sp.getResizeWeight();
				float d=(float)prev_sp.getDividerLocation();
				System.out.println(""+h+","+d);
				prev_sp.setDividerLocation(prev_sp_weight);
			}
			*/
			public void windowOpened(WindowEvent e){
				//System.out.println("windowOpened");
				//float h=(float)prev_sp.getResizeWeight();
				//float d=(float)prev_sp.getDividerLocation();
				//System.out.println(""+h+","+d);
				//※開くとき位置修正
				prev_sp.setDividerLocation(prev_sp_weight);
			}
		});
		
	}
	/*
	//※一番親のWindowでF10を殺す
	protected void processKeyEvent(KeyEvent e){
System.out.println("JzFilerDialog:processKeyEvent");
		if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F10){
			e.consume();	//F10は処理済とする
			return;
		}
		super.processKeyEvent(e);
	}
	*/
	private void initAction(){
		ActionMap am=getRootPane().getActionMap();
		am.put(JzFilerAction.Close,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					command.OnClose();
				}
			});
		am.put("file-copy",new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					command.OnCopy();
				}
			});
		am.put(JzFilerAction.MaskList,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					command.OnMaskList();
				}
			});
	}
	public void setCallback(Callback n){
		callback=n;
	}
	public KeyStroke getCommandKeyStroke(String command){
		/*
		InputMap inputmap=getRootPane().getInputMap();
		return ActionTool.getKeyStrokeFromCommand(inputmap,command);
		*/
		return null;
	}
	public char getCommandMnemonic(String command){
		return 0;
	}
	private ActionInfoMap g_actioninfo_map=new ActionInfoMap();
	public void InitActionInfoMap(){
		g_actioninfo_map.addActionInfos(JzFilerActionInfo.getActionInfoTbl());
	}
	public ActionInfoMap getActionInfoMap(){
		return g_actioninfo_map;
	}
	public String getCommandName(String command){
		ActionInfoMap actioninfomap=getActionInfoMap();
		if(actioninfomap==null)return null;
		ActionInfo info=actioninfomap.get(command);
		if(info==null)return null;
		return info.getMenuName();
	}
	//1画面/2画面切り替え
	void Make2Window(){
		Container c = getContentPane();
//System.out.println("2win:"+twowindow_flg+",prev:"+preview_flg);
		/*
		if(!twowindow_flg){
			if(!preview_flg){
				c.add("Center",list1);
			}else{
				JSplitPane prev_sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,list1,prev_text_scroll);
				prev_sp.setResizeWeight(prev_sp_weight);
				c.add("Center",prev_sp);
			}
		}else{
			list_sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,list1, list2);
			list_sp.setResizeWeight(list_sp_weight);
			if(!preview_flg){
				c.add("Center",list_sp);
			}else{
				JSplitPane prev_sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,list_sp, prev_text_scroll);
				prev_sp.setResizeWeight(prev_sp_weight);
				c.add("Center",prev_sp);
			}
		}
		*/
	}
	public UFile GetCurrentFile(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return null;
		return list.GetCurrentFile();
	}
	public boolean SetCurrentDir(UFile dir){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return false;
		return list.SetCurrentDir(dir);
	}
	public UFile GetCurrentDir(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return null;
		return list.GetCurrentDir();
	}
	public UFile GetDstFile(){
		JzFilerFileListPane list=GetDstList();
		if(list==null)return null;
		return list.GetCurrentFile();
	}
	public UFile GetDstDir(){
		JzFilerFileListPane list=GetDstList();
		if(list==null)return null;
		return list.GetCurrentDir();
	}
	//
	public void setSortType(int n){
		sort_type=n;
	}
	public int getSortType(){
		return sort_type;
	}
	public void setSortDir(int n){
		sort_dir=n;
	}
	public int getSortDir(){
		return sort_dir;
	}
	public void set2WindowFlg(boolean n){
		twowindow_flg=n;
	}
	public boolean get2WindowFlg(){
		return twowindow_flg;
	}
	public void setPreviewFlg(boolean n){
		preview_flg=n;
	}
	public boolean getPreviewFlg(){
		return preview_flg;
	}
	public void setOpenOtherFlg(boolean n){
		openother_flg=n;
	}
	public boolean getOpenOtherFlg(){
		return openother_flg;
	}
	public void setReadOnlyFlg(boolean n){
		readonly_flg=n;
	}
	public boolean getReadOnlyFlg(){
		return readonly_flg;
	}
	
	//ファイルを選択して終了
	public void SetSelectFileExit(UFile filename){
		if(filename!=null){
			File file=null;
			try{
				file=((UFileURL)filename).getFile();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(file!=null){
				File dir=file;
				if(dir.isFile())dir=dir.getParentFile();
				if(dir!=null){
					try{
						dir_history.add(UFileBuilderURL.createFromFile(dir));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			if(filename.isFile()){
				file_history.add(filename);
			}
		}
		g_select_file=filename;
		result_flg=true;
		//
		if(isModal()){
			dispose();
		}else{
			if(callback!=null){
				callback.selectFile(g_select_file);
			}
		}
	}
	//キャンセル終了
	public void CancelExit(){
		result_flg=false;
		dispose();
	}
	public JzFilerListCellRendererParam GetCellRendererParam(){
		return g_option.cellrenderer;
	}
	//=============================
	//ペアリスト
	JzFilerPairFileList getPairFileList(){
		return pair_list;
	}
	public void InitPairFileList(UFile[] current_dir,UFileCurrentDrives[] drive_currents){
		pair_list=new JzFilerPairFileList(this,current_dir,drive_currents,list_sp_weight);
		pair_list.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if(e instanceof JzFilerChangeEvent){
					JzFilerChangeEvent ev=(JzFilerChangeEvent)e;
					UFile filename=ev.file;
//System.out.println("ev:"+ev.toString());
					switch(ev.type){
						case JzFilerChangeEvent.SELECT_FILE:
							if(filename!=null){
								SetSelectFileExit(filename);
							}
							break;
						case JzFilerChangeEvent.CHANGE_FILE:
						case JzFilerChangeEvent.CHANGE_CURRENT_DIR:
							if(filename!=null){
								SetPreview(filename);
							}
							break;
						case JzFilerChangeEvent.SEPARATOR:
							list_sp_weight=ev.per;
							break;
					}
				}
			}
		});
	}
	public JComponent GetPairFileList(){
		return pair_list.GetPairFileList();
	}
	JzFilerFileListPane GetCurrentList(){
		return pair_list.GetCurrentList();
	}
	JzFilerFileListPane GetDstList(){
		return pair_list.GetDstList();
	}
	void OnUpdatePairList(){
		if(pair_list==null)return;
		pair_list.OnUpdate();
	}
	float GetPairListWeight(){
		if(pair_list==null)return 0.5f;
		return pair_list.GetPairListWeight();
	}
	void SetPairListWeight(float n){
		if(pair_list==null)return;
		pair_list.SetPairListWeight(n);
	}
	public void MakeFileFilterFromMask(String mask_str){
		file_filter=JzFilerMaskFileFilter.createMaskFileFilter(mask_str,true);
	}
	public UFilenameFilter GetFileFilterFromMask(){
		return file_filter;
	}
	private void addFilenameList(ArrayList<UFile> drives,UFile[] local_drives){
		for(int i=0;i<local_drives.length;i++)drives.add(local_drives[i]);
	}
	public UFile[] getDriveList(){
		ArrayList<UFile> drives=new ArrayList<UFile>();
		UFile[] local_drives=UFileBuilderURL.listRoots();
		addFilenameList(drives,local_drives);
		UFileFTPInfo[] ftpinfoones=ftpinfo.toArray();
		for(int i=0;i<ftpinfoones.length;i++){
			drives.add(ftpinfoones[i]);
		}
		return (UFile[])drives.toArray(new UFile[]{});
	}
	//=============================
	//プレビュー
	private JzFilerPreview preview;
	public void InitPreview(){
		preview=new JzFilerPreview();
	}
	public Container GetPreview(){
		//return preview;
		return preview.getContainer();
	}
	public void SetPreview(UFile filename){
		if(preview!=null)preview.SetPreview(filename);
	}
	//===========================
	//●はい・いいえ・取消し
	public int YesNoDialog(String m){
		return ConfirmDialog.Open(this,m);
	}
	//========================================================
	protected void processWindowEvent(WindowEvent e){
		switch(e.getID()){
			case WindowEvent.WINDOW_CLOSING:
				break;
			case WindowEvent.WINDOW_DEACTIVATED:
				OutReg(regout,g_node_name);
				break;
		}
		super.processWindowEvent(e);
	}
	//選択されたファイル(モーダレス、ファイルが選択されるまで戻ってこない)
	public UFile SelectFile(){
		setModal(true);			//※モーダルにする()
		setVisible(true);
		//
		if(!result_flg)return null;
		if(g_select_file==null)return null;
		try{
			return g_select_file;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	public IRECT GetWindowRect(){
		return ConvAWT.Rectangle_IRECT(getBounds());
	}
	public void SetWindowRect(IRECT rect){
		//レジストリ
		setLocation(ConvAWT.IVEC2_Point(rect.getLocation()));
		setSize(ConvAWT.IVEC2_Dimension(rect.getSize()));
	}
	//========================================================
	//オプション
	//========================================================
	private JzFilerOptionData g_option=new JzFilerOptionData();
	public void ReadRegDialogOption(){
		GetRegOption();
	}
	public String GetRegRootName(){
		return g_node_name;
	}
	public String GetOptionRegNodeName(){
		return GetRegRootName()+"/Option";
	}
	public void GetRegOption(){
		SetOptionParam();
		g_option.ReadReg(regout,GetOptionRegNodeName());
		FeedbackOption();
//		norm_attr.MakeAttr();
	}
	public void OutRegOption(){
		SetOptionParam2();
		g_option.OutReg(regout,GetOptionRegNodeName());
	}
	public void SetOptionParam(){
		/*
		g_option.col_back      =norm_attr.back_col;
		g_option.col_normal    =norm_attr.normal_col;
		g_option.col_select    =norm_attr.select_col;
		g_option.col_comment   =norm_attr.comment_col;
		g_option.col_str       =norm_attr.str_col;
		g_option.col_macro     =norm_attr.macro_col;
		g_option.font_name     =norm_attr.font_name;
		g_option.font_size     =norm_attr.font_size;
		g_option.col_backlog   =comline_attr.backlogback_col;
		g_option.lookandfeel   =GetNowLookAndFeel();
		g_option.inner_filer   =now_filer_index;
		*/
		SetOptionParam2();
	}
	public void SetOptionParam2(){
		/*
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
		*/
	}
	public void FeedbackOption(){
		/*
		norm_attr.back_col       =g_option.col_back;
		norm_attr.normal_col     =g_option.col_normal;
		norm_attr.select_col     =g_option.col_select;
		norm_attr.comment_col    =g_option.col_comment;
		norm_attr.str_col        =g_option.col_str;
		norm_attr.macro_col      =g_option.col_macro;
		norm_attr.tab_col        =g_option.col_tab;
		norm_attr.cr_col         =g_option.col_ret;
		norm_attr.eof_col        =g_option.col_eof;
		norm_attr.draw_tab     =g_option.draw_tab;
		norm_attr.draw_cr      =g_option.draw_cr;
		norm_attr.draw_eof     =g_option.draw_eof;
		norm_attr.draw_zenspace=g_option.draw_zenspace;
		norm_attr.draw_space   =g_option.draw_space;
		norm_attr.draw_linenum =g_option.draw_linenum;
		norm_attr.draw_ruler   =g_option.draw_ruler;
		norm_attr.font_name    =g_option.font_name;
		norm_attr.font_size    =g_option.font_size;
		comline_attr.backlogback_col=g_option.col_backlog;
		SetLookAndFeelShort(g_option.lookandfeel);
		now_filer_index          =g_option.inner_filer;
		SetInnerFiler();
		*/
		repaint();
	}
	public void UpdateOptionAttr(){
		pair_list.UpdateAttr();
	}
	public void OptionDialog() {
		SetOptionParam();
		//
		if(JzFilerOptionDialog.Open(this,g_option)){
			FeedbackOption();
			UpdateOptionAttr();
		}
	}
	//========================================================
	public String GetRegNameUFileCurrentDrives(String node_name){
		return node_name+"/UFileCurrentDrives";
	}
	public void ReadRegUFileCurrentDrives(String node_name){
		drive_currents.readReg(regout,GetRegNameUFileCurrentDrives(node_name));
	}
	public void OutRegUFileCurrentDrives(String node_name){
		drive_currents.outReg(regout,GetRegNameUFileCurrentDrives(node_name));
	}
	//========================================================
	public void ReadRegFileMaskSet(RegOut regout,String node_name,Set<File> fileset){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		int num=regout.ReadRegInt(node,"fileset_num",0);
		if(num==0)return;
		for(int i=0;i<num;i++){
			String name=String.format("fileset%d",i);
			String filename=regout.ReadRegStr(node,name,null);
			if(filename!=null && filename.length()>0){
				fileset.add(new File(filename));
			}
		}
	}
	public void OutRegFileMaskSet(RegOut regout,String node_name,Set<File> fileset){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		int num=fileset.size();
		regout.OutRegInt(node,"fileset_num",num);
		int i=0;
		for(File file : fileset){
			String name=String.format("fileset%d",i);
			regout.OutRegStr(node,name,file.toString());
			i++;
		}
	}
	//========================================================
	public void ReadRegFileSet(RegOut regout,String node_name,Set<UFile> fileset){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		int num=regout.ReadRegInt(node,"fileset_num",0);
		if(num==0)return;
		for(int i=0;i<num;i++){
			String name=String.format("fileset%d",i);
			String filename=regout.ReadRegStr(node,name,null);
			if(filename!=null && filename.length()>0){
				//fileset.add(new File(filename));
				try{
					fileset.add(UFileBuilder.createLocal(filename));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
	public void OutRegFileSet(RegOut regout,String node_name,Set<UFile> fileset){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		int num=fileset.size();
		regout.OutRegInt(node,"fileset_num",num);
		int i=0;
		for(UFile filename : fileset){
			String name=String.format("fileset%d",i);
			regout.OutRegStr(node,name,filename.getLocalFilename());
			i++;
		}
	}
	public String GetNodeNameFileHistory(String node_name){
		return node_name+"/FileHistory";
	}
	public String GetNodeNameDirHistory(String node_name){
		return node_name+"/DirHistory";
	}
	public String GetNodeNameDirList(String node_name){
		return node_name+"/DirList";
	}
	public String GetNodeNameFileMask(String node_name){
		return node_name+"/FileMask";
	}
	//========================================================
	//レジストリ
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		start_rect=GetWindowRect();
		start_rect=regout.ReadRegRect(node,"window",start_rect);
		sort_type=regout.ReadRegInt(node,"sort_type",sort_type);
		sort_dir=regout.ReadRegInt(node,"sort_dir",sort_dir);
		list_sp_weight=regout.ReadRegFloat(node,"list_sp_weight",GetPairListWeight());
		prev_sp_weight=regout.ReadRegFloat(node,"prev_sp_weight",prev_sp_weight);
		ReadRegDialogOption();
		ReadRegUFileCurrentDrives(node_name);
		compare_opt.ReadReg(regout,node_name);
		ftpinfo.ReadReg(regout,node_name);
		ReadRegFileSet(regout,GetNodeNameFileHistory(node_name),file_history);
		ReadRegFileSet(regout,GetNodeNameDirHistory(node_name),dir_history);
		ReadRegFileSet(regout,GetNodeNameDirList(node_name),dir_list);
		ReadRegFileMaskSet(regout,GetNodeNameFileMask(node_name),mask_list);
		String _mask=regout.ReadRegStr(node,"current_mask",current_mask.toString());
		if(_mask!=null){
			current_mask=new File(_mask);
			MakeFileFilterFromMask(_mask);
		}
		
	}
	public void FeedBackReg(){
		SetWindowRect(start_rect);
		/*
		SetMenuSortDir();
		SetMenuSortType();
		SetMenu2WindowFlg();
		SetMenuPreviewFlg();
		SetMenuOpenOtherFlg();
		SetMenuReadOnlyFlg();
		*/
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		IRECT rect=GetWindowRect();
		regout.OutRegRect(node,"window",rect);
		regout.OutRegInt(node,"sort_type",sort_type);
		regout.OutRegInt(node,"sort_dir",sort_dir);
		regout.OutRegFloat(node,"list_sp_weight",list_sp_weight);
		regout.OutRegFloat(node,"prev_sp_weight",prev_sp_weight);
		OutRegOption();
		OutRegUFileCurrentDrives(node_name);
		compare_opt.OutReg(regout,node_name);
		ftpinfo.OutReg(regout,node_name);
		OutRegFileSet(regout,GetNodeNameFileHistory(node_name),file_history);
		OutRegFileSet(regout,GetNodeNameDirHistory(node_name),dir_history);
		OutRegFileSet(regout,GetNodeNameDirList(node_name),dir_list);
		OutRegFileMaskSet(regout,GetNodeNameFileMask(node_name),mask_list);
		regout.OutRegStr(node,"current_mask",current_mask.toString());
	}
	JzFilerDirComparetor.DirCompareOpt getDirCompareOpt(){
		return compare_opt;
	}
	HashSet<UFile> getDirHistory(){
		return dir_history;
	}
	HashSet<UFile> getDirList(){
		return dir_list;
	}
	HashSet<File> getFileMaskList(){
		return mask_list;
	}
	HashSet<UFile> getFileHistory(){
		return file_history;
	}
	void setCurrentMask(File n){
		current_mask=n;
	}
	File getCurrentMask(){
		return current_mask;
	}
	FTPInfoList getFTPInfo(){
		return ftpinfo;
	}
	void Update(){
		OnUpdatePairList();
		repaint();
	}
	//========================================================
	//
	//========================================================
	public ActionMap getActionMap(){
		return getRootPane().getActionMap();
	}
	private void InitActionMap(){
		ActionTbl.SetActionTbl(getActionMap(),command.getActionTbl());
		//ActionTool.SetInputTbl(textArea,g_input_tbl);
	}
}
