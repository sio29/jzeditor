/******************************************************************************
;	メインメニュー
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import javax.swing.KeyStroke;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.MenuInfoSetter;

import sio29.jzeditor.backends.j2d.JzEditorAction;
import sio29.jzeditor.backends.j2d.texttool.CharCode;
import sio29.jzeditor.backends.j2d.texttool.StringConverter;
import sio29.jzeditor.backends.j2d.menu.*;

public class CommandLineMenu implements MenuFeedback{
	public interface Callback extends MenuFeedbackCallback {
		public void execCommand(String cmd);
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public void setMenuFileHistory(UMenu menu);
	}
	private Callback callback;
	private UMenuBar menuBar;
	//
	public CommandLineMenu(Callback callback){
		this.callback=callback;
		menuBar=MakeMenuBar(callback);
	}
	public UMenuBar getMenuBar(){
		return menuBar;
	}
	public UMenuBar createMenuBar(MenuFeedbackCallback callback){
		return MakeMenuBar(callback);
	}
	public UMenuBar createMenuBar(){
		return MakeMenuBar(callback);
	}
	//=============
	//MenuBar
	//=============
	//static UMenuBar MakeMenuBar(Callback callback){
	static UMenuBar MakeMenuBar(MenuFeedbackCallback _callback){
		Callback callback=(Callback)_callback;
		UActionListener listener=new UActionListener(){
			public void actionPerformed(UActionEvent ae){
				String cmd = ae.getActionCommand();
				if(callback!=null){
					callback.execCommand(cmd);
				}
			}
		};
		UMenuBar menuBar=UMenuBarBuilder.create();
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenu("ファイル",'F'),
			new MenuTool.MenuParamMenu("表示"    ,'V'),
			new MenuTool.MenuParamMenu("設定"    ,'S'),
			new MenuTool.MenuParamMenu("ヘルプ"  ,'H'),
		};
		MenuTool.addMenuBarParams(callback,menuBar,params,listener);
		UMenu file_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ファイル");
		UMenu show_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"表示");
		UMenu setting_menu=(UMenu)MenuTool.getMenuItemFromName(menuBar,"設定");
		UMenu help_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ヘルプ");
		if(file_menu   !=null)setMenuFile(callback,file_menu,listener);
		if(show_menu   !=null)setMenuShow(callback,show_menu,listener);
		if(setting_menu!=null)setMenuSetting(callback,setting_menu,listener);
		if(help_menu   !=null)setMenuHelp(callback,help_menu,listener);
		//
		MenuInfoSetter.setMenuItemKeyStroke(new MenuInfoSetter.Callback(){
			public KeyStroke getCommandKeyStroke(String command){
				if(callback==null)return null;
				return callback.getCommandKeyStroke(command);
			}
			public String getCommandName(String command){
				if(callback==null)return null;
				String name=callback.getCommandName(command);
				//if(name!=null)name="**"+name;//※nullチェック用
				return name;
			}
		},UMenuBarJ2D.getJMenuBar(menuBar));
		//
		return menuBar;
	}
	//=============
	//File
	//=============
	static void setMenuFile(CommandLineMenu.Callback callback,UMenu menuFile,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.New,'N'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.NewWindow),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Open,'O'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenWindow),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenAdd),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Save,'S'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SaveAs,'A'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.CloseTab),
			new MenuTool.MenuParamMenuItem(JzEditorAction.CloseAllTab),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenCommandLine),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenOutterNow),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenOutter),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OuterFiler),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Print,'P'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.DocInfo),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.NewBinary),
			new MenuTool.MenuParamMenuItem(JzEditorAction.OpenBinary),
			new MenuTool.MenuParamMenuItem(JzEditorAction.DebugBinEditor),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SelectNextTab),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SelectPrevTab),
			new MenuTool.MenuParamMenuItem(JzEditorAction.DosExec),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenu("履歴"),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Exit,'X'),
		};
		MenuTool.addMenuParams(callback,menuFile,params,listener);
		
		MenuTool.setMenuEnableFuncFromCommand(menuFile,JzEditorAction.Save,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				//return callback.CanSave();
				return false;
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuFile,JzEditorAction.SaveAs,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				//return callback.CanSave();
				return false;
			}
		});
		
		UMenuItem menuFilehist=MenuTool.getMenuItemFromName(menuFile,"履歴");
		MenuTool.setMenuSelectedFunc(menuFile,menuFilehist,new MenuTool.MenuSelectedFunc(){
			public void menuSelected(UMenuItem item){
				callback.setMenuFileHistory((UMenu)item);
			}
		});
	}
	//=============
	//Show
	//=============
	static void setMenuShow(CommandLineMenu.Callback callback,UMenu menuView,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamCheckBox(JzEditorAction.ToolBar,'T'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.StatusBar,'S'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.FunctionKey,'F'),
		};
		MenuTool.addMenuParams(callback,menuView,params,listener);
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ToolBar,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetUseToolBar();
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.StatusBar,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetUseStatusBar();
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.FunctionKey,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetUseFunctionKey();
			}
		});
	}
	//=============
	//Setting
	//=============
	static void setMenuSetting(CommandLineMenu.Callback callback,UMenu menuSetting,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Option,'O'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.ShortCut,'S'),
		};
		MenuTool.addMenuParams(callback,menuSetting,params,listener);
	}
	//=============
	//Help
	//=============
	static void setMenuHelp(CommandLineMenu.Callback callback,UMenu menuHelp,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Help,'H'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.About,'A'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.ScriptTest),
		};
		MenuTool.addMenuParams(callback,menuHelp,params,listener);
	}
}

