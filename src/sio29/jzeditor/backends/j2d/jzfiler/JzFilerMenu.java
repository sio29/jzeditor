/******************************************************************************
;	Vzファイラー、メニュー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import javax.swing.KeyStroke;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.MenuInfoSetter;

import sio29.jzeditor.backends.j2d.menu.*;

public class JzFilerMenu{
	public interface Callback extends MenuTool.CommandNameFunc {
		public int getMenuSortType();
		public int getMenuSortDir();
		public boolean getMenu2WindowFlg();
		public boolean getMenuPreviewFlg();
		public boolean getMenuOpenOtherFlg();
		public boolean getMenuReadOnlyFlg();
		//
		public KeyStroke getCommandKeyStroke(String command);
		public char getCommandMnemonic(String command);
	}
	private Callback callback;
	
	public JzFilerMenu(Callback _callback){
		this.callback=_callback;
	}
	//========================================================
	//
	static UMenuBar MakeMenu(Callback callback,UActionListener listener){
		UMenuBar menuBar=UMenuBarBuilder.create();
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenu("ファイル",'F'),
			new MenuTool.MenuParamMenu("編集"    ,'E'),
			new MenuTool.MenuParamMenu("ソート"  ,'S'),
			new MenuTool.MenuParamMenu("ツール"  ,'T'),
			new MenuTool.MenuParamMenu("表示"    ,'H'),
		};
		MenuTool.addMenuBarParams(callback,menuBar,params,listener);
		UMenu file_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ファイル");
		UMenu edit_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"編集");
		UMenu sort_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ソート");
		UMenu tool_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ツール");
		UMenu show_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"表示");
		if(file_menu   !=null)setMenuFile(callback,file_menu,listener);
		if(edit_menu   !=null)setMenuEdit(callback,edit_menu,listener);
		if(sort_menu   !=null)setMenuSort(callback,sort_menu,listener);
		if(tool_menu   !=null)setMenuTool(callback,tool_menu,listener);
		if(show_menu   !=null)setMenuShow(callback,show_menu,listener);
		
		//
		MenuInfoSetter.setMenuItemKeyStroke(new MenuInfoSetter.Callback(){
			public KeyStroke getCommandKeyStroke(String command){
				return callback.getCommandKeyStroke(command);
			}
			public String getCommandName(String command){
				String name=callback.getCommandName(command);
				//if(name!=null)name="**"+name;//※nullチェック用
				return name;
			}
		},UMenuBarJ2D.getJMenuBar(menuBar));
		return menuBar;
	}
	//=============
	//File
	//エディターで開く、関連アプリで開く、閲覧、プロパティ
	//名前の変更、アトリュビートの変更、日付の変更、ショットカットの作成
	//閉じる
	//=============
	static void setMenuFile(JzFilerMenu.Callback callback,UMenu menuFile,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenEditor),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenOther),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenReadOnly),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Prop),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Rename),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Attr),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Date),
			new MenuTool.MenuParamMenuItem(JzFilerAction.FileShortcut),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.FileFTPInfo),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Close),
		};
		MenuTool.addMenuParams(callback,menuFile,params,listener);
	}
	//=============
	//Edit
	//コピー、移動、削除、名前を変えてコピー
	//すべて選択、すべてのファイルを選択、選択の切り替え、選択の解除
	//ファイル名をクリップボードにコピー
	//=============
	static void setMenuEdit(JzFilerMenu.Callback callback,UMenu menuEdit,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzFilerAction.Copy,'C'),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Move),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Delete),
			new MenuTool.MenuParamMenuItem(JzFilerAction.RenameCopy),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SelectAll),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SelectAllFile),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SelectFlip),
			new MenuTool.MenuParamMenuItem(JzFilerAction.UnSelect),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.CopyFilename),
		};
		MenuTool.addMenuParams(callback,menuEdit,params,listener);
	}
	//=============
	//Sort
	//ファイル名順、拡張子順、日付、サイズ
	//降順、昇順
	//=============
	static void setMenuSort(JzFilerMenu.Callback callback,UMenu menuSort,UActionListener listener){
		String[] sorttype_list=new String[]{"ファイル名順","拡張子順","日付","サイズ"};
		String[] sorttype_cmds=new String[]{JzFilerAction.SortFilename,JzFilerAction.SortExt,JzFilerAction.SortDate,JzFilerAction.SortSize};
		
		String[] sortdir_list=new String[]{"降順","昇順"};
		String[] sortdir_cmds=new String[]{JzFilerAction.SortDescending,JzFilerAction.SortAscending};
		
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamRadioGroup("ソート",sorttype_list,sorttype_cmds,'S'),
			//new MenuTool.MenuParamRadioGroup("ソート",sorttype_list,'S'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamRadioGroup("ソート方向",sortdir_list,sortdir_cmds),
			//new MenuTool.MenuParamRadioGroup("ソート方向",sortdir_list),
		};
		MenuTool.addMenuParams(callback,menuSort,params,listener);
		
		URadioGroupMenu menuSortType=(URadioGroupMenu)MenuTool.getMenuItemFromName(menuSort,"ソート");
		URadioGroupMenu menuSortDir =(URadioGroupMenu)MenuTool.getMenuItemFromName(menuSort,"ソート方向");
		
		MenuTool.setMenuSelectedFunc(menuSort,menuSortType,new MenuTool.MenuSelectedFunc(){
			public void menuSelected(UMenuItem item){
				if(item instanceof URadioGroupMenu){
					URadioGroupMenu menu=(URadioGroupMenu)item;
					menu.setSelectIndex(callback.getMenuSortType());
				}
			}
		});
		MenuTool.setMenuSelectedFunc(menuSort,menuSortDir,new MenuTool.MenuSelectedFunc(){
			public void menuSelected(UMenuItem item){
				if(item instanceof URadioGroupMenu){
					URadioGroupMenu menu=(URadioGroupMenu)item;
					menu.setSelectIndex(callback.getMenuSortDir());
				}
			}
		});
		
	}
	//=============
	//Tool
	//新規ファイル作成、新規フォルダ作成
	//フォルダを開く、最近使ったフォルダ、フォルダリスト、フォルダの比較、マスクリスト
	//新規テキストを開く、最近使ったファイル
	//グローバル検索(GREP)
	//=============
	static void setMenuTool(JzFilerMenu.Callback callback,UMenu menuTool,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzFilerAction.NewFile),
			new MenuTool.MenuParamMenuItem(JzFilerAction.NewDir),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.ChangeDir),
			new MenuTool.MenuParamMenuItem(JzFilerAction.ChangeDirParent),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenDir),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenDirHist),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenDirList),
			new MenuTool.MenuParamMenuItem(JzFilerAction.CompareDir),
			new MenuTool.MenuParamMenuItem(JzFilerAction.MaskList),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.OpenNewFile),
			new MenuTool.MenuParamMenuItem(JzFilerAction.FileHist),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Grep),
		};
		MenuTool.addMenuParams(callback,menuTool,params,listener);
	}
	//=============
	//Show
	//設定、閲覧で開く、関連アプリケーションで開く
	//2ウィンドウ、最新情報に更新、プレビュー、新しいウィンドウ
	//ヘルプ、バージョン情報
	//=============
	static void setMenuShow(JzFilerMenu.Callback callback,UMenu menuShow,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzFilerAction.Setting),
			new MenuTool.MenuParamMenuItem(JzFilerAction.KeyShortcut),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SetReadOnly),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SetOpenOther),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Set2Window),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Update),
			new MenuTool.MenuParamMenuItem(JzFilerAction.SetPreview),
			new MenuTool.MenuParamMenuItem(JzFilerAction.NewWindow),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Help),
			new MenuTool.MenuParamMenuItem(JzFilerAction.Version),
		};
		MenuTool.addMenuParams(callback,menuShow,params,listener);
	}
}

