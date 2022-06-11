/******************************************************************************
;	メインメニュー
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import javax.swing.KeyStroke;

import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.MenuInfoSetter;

import sio29.ulib.umat.*;
import sio29.ulib.udlgbase.*;

import sio29.jzeditor.backends.j2d.JzEditorAction;
import sio29.jzeditor.backends.j2d.texttool.CharCode;
import sio29.jzeditor.backends.j2d.texttool.StringConverter;
import sio29.jzeditor.backends.j2d.normaltext.NormalTextAttr;
import sio29.jzeditor.backends.j2d.menu.*;

public class NormalTextMenu implements MenuFeedback{
	public interface Callback extends MenuFeedbackCallback {
		public void execCommand(String cmd);
		public boolean CanSave();
		public boolean CanUndo();
		public boolean CanRedo();
		public boolean IsSelected();
		public CharCode GetCharCode();
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public NormalTextAttr GetNormAttr();
		public boolean CanMacroRecStart();
		public boolean CanMacroPlay();
		public boolean CheckAutoTabFlg();
		public void setMenuFileHistory(UMenu menu);
	}
	private Callback callback;
	private UMenuBar menuBar;
	//
	public NormalTextMenu(Callback _callback){
		this.callback=_callback;
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
			new MenuTool.MenuParamMenu("編集"    ,'E'),
			new MenuTool.MenuParamMenu("検索"    ,'S'),
			new MenuTool.MenuParamMenu("表示"    ,'V'),
			new MenuTool.MenuParamMenu("挿入"    ,'I'),
			new MenuTool.MenuParamMenu("ツール"  ,'T'),
			new MenuTool.MenuParamMenu("設定"    ,'S'),
			new MenuTool.MenuParamMenu("ヘルプ"  ,'H'),
		};
		MenuTool.addMenuBarParams(callback,menuBar,params,listener);
		UMenu file_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ファイル");
		UMenu edit_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"編集");
		UMenu search_menu =(UMenu)MenuTool.getMenuItemFromName(menuBar,"検索");
		UMenu show_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"表示");
		UMenu insert_menu =(UMenu)MenuTool.getMenuItemFromName(menuBar,"挿入");
		UMenu tool_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ツール");
		UMenu setting_menu=(UMenu)MenuTool.getMenuItemFromName(menuBar,"設定");
		UMenu help_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"ヘルプ");
		if(file_menu   !=null)setMenuFile(callback,file_menu,listener);
		if(edit_menu   !=null)setMenuEdit(callback,edit_menu,listener);
		if(search_menu !=null)setMenuSearch(callback,search_menu,listener);
		if(show_menu   !=null)setMenuShow(callback,show_menu,listener);
		if(insert_menu !=null)setMenuInsert(callback,insert_menu,listener);
		if(tool_menu   !=null)setMenuTool(callback,tool_menu,listener);
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
	static void setMenuFile(NormalTextMenu.Callback callback,UMenu menuFile,UActionListener listener){
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
				return callback.CanSave();
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuFile,JzEditorAction.SaveAs,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.CanSave();
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
	//Edit
	//=============
	static void setMenuEdit(NormalTextMenu.Callback callback,UMenu menuEdit,UActionListener listener){
		String[] charcode_list=CharCode.convertCharCodeNameList(CharCode.getCharCodeList());
		//
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Undo,'U'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Redo,'R'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Cut,'T'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Copy,'C'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Paste,'P'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SelectAll,'A'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SelectClear,'D'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SelectStart,'S'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.BlockSelect),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamRadioGroup("文字コード",charcode_list),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamCheckBox(JzEditorAction.FlipInsertMode),
			new MenuTool.MenuParamMenu("選択部分"),
		};
		MenuTool.addMenuParams(callback,menuEdit,params,listener);
		
		UMenu menuSelectArea =(UMenu)MenuTool.getMenuItemFromName(menuEdit,"選択部分");
		setMenuSelectArea(menuSelectArea ,listener);
		//
		MenuTool.setMenuEnableFuncFromCommand(menuEdit,JzEditorAction.Undo,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.CanUndo();
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuEdit,JzEditorAction.Redo,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.CanRedo();
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuEdit,JzEditorAction.Copy,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.IsSelected();
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuEdit,JzEditorAction.Cut,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.IsSelected();
			}
		});
		MenuTool.setMenuEnableFuncFromCommand(menuEdit,JzEditorAction.SelectClear,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.IsSelected();
			}
		});
		
		UMenuItem _menuCharaCode=MenuTool.getMenuItemFromName(menuEdit,"文字コード");
		if(_menuCharaCode instanceof URadioGroupMenu){
			URadioGroupMenu menuCharaCode=(URadioGroupMenu)_menuCharaCode;
			MenuTool.setMenuSelectedFunc(menuEdit,menuCharaCode,new MenuTool.MenuSelectedFunc(){
				public void menuSelected(UMenuItem item){
					if(item instanceof URadioGroupMenu){
						URadioGroupMenu menu=(URadioGroupMenu)item;
						CharCode code=callback.GetCharCode();
						if(code!=null){
							int index=CharCode.getCharCodeIndex(CharCode.getCharCodeList(),code);
							menu.setSelectIndex(index);
						}
					}
				}
			});
			//※本当はいらないはず!!
			menuCharaCode.addActionListener(new UActionListener(){
				public void actionPerformed(UActionEvent e){
System.out.println("menuCharaCode.addActionListener:"+e);
					int index=menuCharaCode.getSelectIndex();
System.out.println("index="+index);
					CharCode code=callback.GetCharCode();
					if(code!=null){
						int old_index=CharCode.getCharCodeIndex(CharCode.getCharCodeList(),code);
						if(index!=old_index){
System.out.println(""+old_index+"->"+index);
							CharCode new_code=CharCode.getCharCodeList()[index];
System.out.println("new_code:"+new_code);
							
						}else{
System.out.println("same index !!");
						}
					}
					
				}
			});
		}
	}
	//
	static void setMenuSelectArea(UMenu menuSelectArea ,UActionListener listener){
		StringConverter.Type[] types=StringConverter.GetTypes();
		for(int i=0;i<types.length;i++){
			UMenuItem menuConvHira = UMenuItemBuilder.create(types[i].getName());
			menuConvHira.setActionCommand(types[i].getCommand());
			menuConvHira.addActionListener(listener);
			menuSelectArea.add(menuConvHira);
		}
	}
	//=============
	//Search
	//=============
	static void setMenuSearch(NormalTextMenu.Callback callback,UMenu menuSearch,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Search,'S'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Replace,'T'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SearchDown),
			new MenuTool.MenuParamMenuItem(JzEditorAction.SearchUp),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Remember),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpTop,'F'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpBottom,'B'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpLineTop),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpLineBottom),
			new MenuTool.MenuParamMenuItem(JzEditorAction.TagJump),
			new MenuTool.MenuParamMenuItem(JzEditorAction.TagJumpOutter),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpLine),
			new MenuTool.MenuParamMenuItem(JzEditorAction.JumpPairKakko),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Compare),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.Grep,'G'),
		};
		MenuTool.addMenuParams(callback,menuSearch,params,listener);
	}
	//=============
	//Show
	//=============
	static void setMenuShow(NormalTextMenu.Callback callback,UMenu menuView,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamCheckBox(JzEditorAction.ToolBar,'T'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.StatusBar,'S'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.FunctionKey,'F'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenu("テキスト表示",'T'),
		};
		MenuTool.addMenuParams(callback,menuView,params,listener);
		UMenu menuTextLook=(UMenu)MenuTool.getMenuItemFromName(menuView,"テキスト表示");
		if(menuTextLook!=null){
			setMenuTextLook(callback,menuView,menuTextLook ,listener);
		}
		
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
	//
	private static void setMenuTextLook(NormalTextMenu.Callback callback,UMenu menuView ,UMenu menuTextLook ,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowTab,'T'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowRet,'R'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowEOF,'E'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowBigSpace,'B'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowSpace,'S'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowLineNum,'L'),
			new MenuTool.MenuParamCheckBox(JzEditorAction.ShowRuler,'R'),
		};
		MenuTool.addMenuParams(callback,menuTextLook,params,listener);
		//
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowTab,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_tab;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowRet,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_cr;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowEOF,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_eof;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowBigSpace,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_zenspace;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowSpace,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_space;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowLineNum,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_linenum;
			}
		});
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuView,JzEditorAction.ShowRuler,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.GetNormAttr().draw_ruler;
			}
		});
	}
	//=============
	//Insert
	//=============
	static void setMenuInsert(NormalTextMenu.Callback callback,UMenu menuInsert,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertCopyString),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertSearchString),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertInputString),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertDeleteString),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertDate),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertFile),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertFilename),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertHorizon),
			new MenuTool.MenuParamMenuItem(JzEditorAction.InsertTable),
		};
		MenuTool.addMenuParams(callback,menuInsert,params,listener);
	}
	//=============
	//Tool
	//=============
	static void setMenuTool(NormalTextMenu.Callback callback,UMenu menuTool,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.MacroRecToggle),
			new MenuTool.MenuParamMenuItem(JzEditorAction.MacroPlay),
		};
		MenuTool.addMenuParams(callback,menuTool,params,listener);
		//
		MenuTool.setMenuEnableFuncFromCommand(menuTool,JzEditorAction.MacroPlay,new MenuTool.MenuEnableFunc(){
			public boolean getEnabled(){
				return callback.CanMacroPlay();
			}
		});
		MenuTool.setMenuSelectedFuncFromCommand(menuTool,JzEditorAction.MacroRecToggle,new MenuTool.MenuSelectedFunc(){
			public void menuSelected(UMenuItem item){
				boolean macrorecstart_flg=callback.CanMacroRecStart();
				String command=macrorecstart_flg ? JzEditorAction.MacroRecStart : JzEditorAction.MacroRecEnd;
				String name=callback.getCommandName(command);
				//if(name!=null)name="**"+name;//※nullチェック用
				item.setText(name);
			}
		});
	}
	//=============
	//Setting
	//=============
	static void setMenuSetting(NormalTextMenu.Callback callback,UMenu menuSetting,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Option,'O'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.ShortCut,'S'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamMenu("テキスト編集",'T'),
		};
		MenuTool.addMenuParams(callback,menuSetting,params,listener);
		UMenu menuTextEdit = (UMenu)MenuTool.getMenuItemFromName(menuSetting,"テキスト編集");
		setMenuTextEdit(callback,menuTextEdit ,listener);
	}
	//
	static void setMenuTextEdit(NormalTextMenu.Callback callback,UMenu menuTextLook ,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamCheckBox(JzEditorAction.AutoTab,'A'),
		};
		MenuTool.addMenuParams(callback,menuTextLook,params,listener);
		//
		MenuTool.setCheckBoxMenuItemFuncFromCommand(menuTextLook,JzEditorAction.AutoTab,new MenuTool.CheckBoxMenuItemFunc(){
			public boolean getSelected(){
				return callback.CheckAutoTabFlg();
			}
		});
	}
	//=============
	//Help
	//=============
	static void setMenuHelp(NormalTextMenu.Callback callback,UMenu menuHelp,UActionListener listener){
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamMenuItem(JzEditorAction.Help,'H'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.About,'A'),
			new MenuTool.MenuParamMenuItem(JzEditorAction.ScriptTest),
		};
		MenuTool.addMenuParams(callback,menuHelp,params,listener);
	}
}

