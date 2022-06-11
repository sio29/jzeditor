/******************************************************************************
;	
******************************************************************************/
package sio29.jzeditor.backends.j2d.menu;

import sio29.ulib.udlgbase.*;

public class MenuTool{
	public static interface CommandNameFunc{
		public String getCommandName(String command);
	}
	public static interface MenuEnableFunc{
		public boolean getEnabled();
	}
	public static interface CheckBoxMenuItemFunc{
		public boolean getSelected();
	}
	public static interface MenuSelectedFunc{
		public void menuSelected(UMenuItem item);
	}
	//メニューパラメータ
	public static class MenuParam{
		public char mnemonic=0;
	}
	//Separator
	public static class MenuParamSeparator extends MenuParam{
	}
	//Menu
	public static class MenuParamMenu extends MenuParam{
		public String name;
		public MenuParamMenu(){}
		public MenuParamMenu(String _name){
			name=_name;
		}
		public MenuParamMenu(String _name,char _mnemonic){
			name=_name;
			mnemonic=_mnemonic;
		}
	}
	//MenuItem
	public static class MenuParamMenuItem extends MenuParam{
		public String command;
		public MenuParamMenuItem(){}
		public MenuParamMenuItem(String _command){
			command=_command;
		}
		public MenuParamMenuItem(String _command,char _mnemonic){
			command =_command;
			mnemonic=_mnemonic;
		}
	}
	//CheckBox
	public static class MenuParamCheckBox extends MenuParamMenuItem{
		public boolean flg=true;
		public MenuParamCheckBox(){}
		public MenuParamCheckBox(String _command){
			command=_command;
		}
		public MenuParamCheckBox(String _command,char _mnemonic){
			command =_command;
			mnemonic=_mnemonic;
		}
	}
	//RadioGroup
	public static class MenuParamRadioGroup extends MenuParamMenu{
		public String[] list=null;
		public String[] cmds=null;
		//
		public MenuParamRadioGroup(){}
		public MenuParamRadioGroup(String _name,String[] _list){
			name=_name;
			list=_list;
		}
		public MenuParamRadioGroup(String _name,String[] _list,String[] _cmds){
			name=_name;
			list=_list;
			cmds=_cmds;
		}
		public MenuParamRadioGroup(String _name,String[] _list,char _mnemonic){
			name=_name;
			list=_list;
			mnemonic=_mnemonic;
		}
		public MenuParamRadioGroup(String _name,String[] _list,String[] _cmds,char _mnemonic){
			name=_name;
			list=_list;
			cmds=_cmds;
			mnemonic=_mnemonic;
		}
	}
	
	//MenuItem追加
	public static UMenuItem addMenuItemFromCommand(CommandNameFunc callback,UMenu menu,String command,UActionListener listener){
		return addMenuItemFromCommand(callback,menu,command,listener,(char)0);
	}
	public static UMenuItem addMenuItemFromCommand(CommandNameFunc callback,UMenu menu,String command,UActionListener listener,char mnemonic){
		final UMenuItem item = UMenuItemBuilder.create(callback.getCommandName(command));
		if(mnemonic!=0){
			item.setMnemonic(mnemonic);
		}
		item.setActionCommand(command);
		item.addActionListener(listener);
		menu.add(item);
		return item;
	}
	//CheckBoxMenuItem追加
	public static UCheckBoxMenuItem addCheckBoxMenuItemFromCommand(CommandNameFunc callback,UMenu menu,String command,UActionListener listener,boolean flg){
		return addCheckBoxMenuItemFromCommand(callback,menu,command,listener,flg,(char)0);
	}
	public static UCheckBoxMenuItem addCheckBoxMenuItemFromCommand(CommandNameFunc callback,UMenu menu,String command,UActionListener listener,boolean flg,char mnemonic){
		final UCheckBoxMenuItem item = UCheckBoxMenuItemBuilder.create(callback.getCommandName(command),flg);
		if(mnemonic!=0){
			item.setMnemonic(mnemonic);
		}
		item.setActionCommand(command);
		item.addActionListener(listener);
		menu.add(item);
		return item;
	}
	//コマンドからMenuItemを得る
	public static UMenuItem getMenuItemFromCommand(UMenu menu,String command,boolean child_flg){
		if(menu==null)return null;
		if(command==null)return null;
		int item_num=menu.getItemCount();
		for(int i=0;i<item_num;i++){
			UMenuItem item=menu.getItem(i);
			if(item==null)continue;
			String cmd=item.getActionCommand();
			if(cmd!=null){
				if(cmd.equals(command))return item;
			}
			if(child_flg && (item instanceof UMenu)){
				UMenu c_menu=(UMenu)item;
				UMenuItem c_item=getMenuItemFromCommand(c_menu,command,true);
				if(c_item!=null)return c_item;
			}
		}
		return null;
	}
	public static UMenuItem getMenuItemFromCommand(UMenu menu,String command){
		return getMenuItemFromCommand(menu,command,false);
	}
	//名前からMenuItemを得る
	public static UMenuItem getMenuItemFromName(UMenu menu,String name,boolean child_flg){
		if(menu==null)return null;
		if(name==null)return null;
		int item_num=menu.getItemCount();
		for(int i=0;i<item_num;i++){
			UMenuItem item=menu.getItem(i);
			if(item==null)continue;
			//String cmd=item.getName();
			String cmd=item.getText();
			if(cmd!=null){
				if(cmd.equals(name))return item;
			}
			if(child_flg && (item instanceof UMenu)){
				UMenu c_menu=(UMenu)item;
				UMenuItem c_item=getMenuItemFromName(c_menu,name,true);
				if(c_item!=null)return c_item;
			}
		}
		return null;
	}
	public static UMenuItem getMenuItemFromName(UMenu menu,String name){
		return getMenuItemFromName(menu,name,false);
	}
	//名前からMenuItemを得る(MenuBar)
	public static UMenuItem getMenuItemFromName(UMenuBar menubar,String name,boolean child_flg){
//System.out.println("menubar:"+menubar.getMenuCount());
		for(int i=0;i<menubar.getMenuCount();i++){
			UMenu menu=menubar.getMenu(i);
			if(menu==null)continue;
			String cmd=menu.getText();
//System.out.println("["+i+"]:"+cmd);
			if(cmd!=null){
				if(cmd.equals(name))return menu;
			}
			if(child_flg){
				UMenuItem c_item=getMenuItemFromName(menu,name,false);
				if(c_item!=null)return c_item;
			}
		}
		return null;
	}
	public static UMenuItem getMenuItemFromName(UMenuBar menubar,String name){
		return getMenuItemFromName(menubar,name,false);
	}
	//Enable設定
	public static void setMenuEnableFunc(final UMenu menu,final UMenuItem item,final MenuEnableFunc func){
		if(menu==null)return;
		if(item==null)return;
		if(func==null)return;
		menu.addMenuListener(new UMenuListener(){
			public void menuCanceled(UMenuEvent e){}
			public void menuDeselected(UMenuEvent e){}
			public void menuSelected(UMenuEvent e) {
				boolean flg=func.getEnabled();
				item.setEnabled(flg);
			}
		});
	}
	public static void setMenuEnableFuncFromCommand(final UMenu menu,final String command,final MenuEnableFunc func){
		UMenuItem item=getMenuItemFromCommand(menu,command,true);
		setMenuEnableFunc(menu,item,func);
	}
	//メニュー選択時コールバック
	public static void setMenuSelectedFunc(final UMenu menu,final UMenuItem item,final MenuSelectedFunc func){
		if(menu==null)return;
		if(item==null)return;
		if(func==null)return;
		menu.addMenuListener(new UMenuListener(){
			public void menuCanceled(UMenuEvent e){}
			public void menuDeselected(UMenuEvent e){}
			public void menuSelected(UMenuEvent e) {
				if(func!=null)func.menuSelected(item);
			}
		});
	}
	public static void setMenuSelectedFuncFromCommand(final UMenu menu,final String command,final MenuSelectedFunc func){
		UMenuItem item=getMenuItemFromCommand(menu,command,true);
		setMenuSelectedFunc(menu,item,func);
	}
	//チェックボックスの選択
	public static void setCheckBoxMenuItemFunc(final UMenu menu,final UCheckBoxMenuItem item,final CheckBoxMenuItemFunc func){
		if(menu==null)return;
		if(item==null)return;
		if(func==null)return;
		menu.addMenuListener(new UMenuListener(){
			public void menuCanceled(UMenuEvent e){}
			public void menuDeselected(UMenuEvent e){}
			public void menuSelected(UMenuEvent e) {
				boolean flg=func.getSelected();
				item.setSelected(flg);
			}
		});
	}
	public static void setCheckBoxMenuItemFuncFromCommand(final UMenu menu,final String command,final CheckBoxMenuItemFunc func){
		UMenuItem _item=getMenuItemFromCommand(menu,command,true);
		if(_item instanceof UCheckBoxMenuItem){
			UCheckBoxMenuItem item=(UCheckBoxMenuItem)_item;
			setCheckBoxMenuItemFunc(menu,item,func);
		}
	}
	//パラメータでメニュー設定
	public static void addMenuParams(CommandNameFunc callback,final UMenu _menu,MenuParam[] params,UActionListener listener){
		UMenu menu=_menu;
		for(int i=0;i<params.length;i++){
			MenuParam _param=params[i];
			if(_param==null)continue;
			if(_param instanceof MenuParamSeparator){
				menu.addSeparator();
			}else if(_param instanceof MenuParamRadioGroup){
			//RadioGroupMenu追加
				MenuParamRadioGroup param=(MenuParamRadioGroup)_param;
				URadioGroupMenu c_menu=null;
				if(param.cmds==null){
					c_menu=URadioGroupMenuBuilder.create(param.name,param.list);
				}else{
					c_menu=URadioGroupMenuBuilder.create(param.name,param.list,param.cmds);
				}
				if(param.mnemonic!=0)c_menu.setMnemonic(param.mnemonic);
				c_menu.addActionListener(listener);
				menu.add(c_menu);
			}else if(_param instanceof MenuParamMenu){
			//Menu追加
				MenuParamMenu param=(MenuParamMenu)_param;
				UMenu c_menu = UMenuBuilder.create(param.name);
				if(param.mnemonic!=0)c_menu.setMnemonic(param.mnemonic);
				menu.add(c_menu);
			}else if(_param instanceof MenuParamCheckBox){
			//CheckBoxMenuItem追加
				MenuParamCheckBox param=(MenuParamCheckBox)_param;
				addCheckBoxMenuItemFromCommand(callback,menu,param.command,listener,param.flg,param.mnemonic);
			}else if(_param instanceof MenuParamMenuItem){
			//MenuItem追加
				MenuParamMenuItem param=(MenuParamMenuItem)_param;
				addMenuItemFromCommand(callback,menu,param.command,listener,param.mnemonic);
			}
		}
	}
	//パラメータでメニュー設定(MenuBar)
	public static void addMenuBarParams(CommandNameFunc callback,final UMenuBar _menubar,MenuParam[] params,UActionListener listener){
		for(int i=0;i<params.length;i++){
			MenuParam _param=params[i];
			if(_param==null)continue;
			if(_param instanceof MenuParamMenu){
				MenuParamMenu param=(MenuParamMenu)_param;
				UMenu c_menu = UMenuBuilder.create(param.name);
				if(param.mnemonic!=0)c_menu.setMnemonic(param.mnemonic);
				_menubar.add(c_menu);
			}
		}
	}
}
