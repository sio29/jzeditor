/******************************************************************************
;	Vz�t�@�C���[�A���j���[
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
			new MenuTool.MenuParamMenu("�t�@�C��",'F'),
			new MenuTool.MenuParamMenu("�ҏW"    ,'E'),
			new MenuTool.MenuParamMenu("�\�[�g"  ,'S'),
			new MenuTool.MenuParamMenu("�c�[��"  ,'T'),
			new MenuTool.MenuParamMenu("�\��"    ,'H'),
		};
		MenuTool.addMenuBarParams(callback,menuBar,params,listener);
		UMenu file_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"�t�@�C��");
		UMenu edit_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"�ҏW");
		UMenu sort_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"�\�[�g");
		UMenu tool_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"�c�[��");
		UMenu show_menu   =(UMenu)MenuTool.getMenuItemFromName(menuBar,"�\��");
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
				//if(name!=null)name="**"+name;//��null�`�F�b�N�p
				return name;
			}
		},UMenuBarJ2D.getJMenuBar(menuBar));
		return menuBar;
	}
	//=============
	//File
	//�G�f�B�^�[�ŊJ���A�֘A�A�v���ŊJ���A�{���A�v���p�e�B
	//���O�̕ύX�A�A�g�����r�[�g�̕ύX�A���t�̕ύX�A�V���b�g�J�b�g�̍쐬
	//����
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
	//�R�s�[�A�ړ��A�폜�A���O��ς��ăR�s�[
	//���ׂđI���A���ׂẴt�@�C����I���A�I���̐؂�ւ��A�I���̉���
	//�t�@�C�������N���b�v�{�[�h�ɃR�s�[
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
	//�t�@�C�������A�g���q���A���t�A�T�C�Y
	//�~���A����
	//=============
	static void setMenuSort(JzFilerMenu.Callback callback,UMenu menuSort,UActionListener listener){
		String[] sorttype_list=new String[]{"�t�@�C������","�g���q��","���t","�T�C�Y"};
		String[] sorttype_cmds=new String[]{JzFilerAction.SortFilename,JzFilerAction.SortExt,JzFilerAction.SortDate,JzFilerAction.SortSize};
		
		String[] sortdir_list=new String[]{"�~��","����"};
		String[] sortdir_cmds=new String[]{JzFilerAction.SortDescending,JzFilerAction.SortAscending};
		
		MenuTool.MenuParam[] params=new MenuTool.MenuParam[]{
			new MenuTool.MenuParamRadioGroup("�\�[�g",sorttype_list,sorttype_cmds,'S'),
			//new MenuTool.MenuParamRadioGroup("�\�[�g",sorttype_list,'S'),
			new MenuTool.MenuParamSeparator(),
			new MenuTool.MenuParamRadioGroup("�\�[�g����",sortdir_list,sortdir_cmds),
			//new MenuTool.MenuParamRadioGroup("�\�[�g����",sortdir_list),
		};
		MenuTool.addMenuParams(callback,menuSort,params,listener);
		
		URadioGroupMenu menuSortType=(URadioGroupMenu)MenuTool.getMenuItemFromName(menuSort,"�\�[�g");
		URadioGroupMenu menuSortDir =(URadioGroupMenu)MenuTool.getMenuItemFromName(menuSort,"�\�[�g����");
		
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
	//�V�K�t�@�C���쐬�A�V�K�t�H���_�쐬
	//�t�H���_���J���A�ŋߎg�����t�H���_�A�t�H���_���X�g�A�t�H���_�̔�r�A�}�X�N���X�g
	//�V�K�e�L�X�g���J���A�ŋߎg�����t�@�C��
	//�O���[�o������(GREP)
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
	//�ݒ�A�{���ŊJ���A�֘A�A�v���P�[�V�����ŊJ��
	//2�E�B���h�E�A�ŐV���ɍX�V�A�v���r���[�A�V�����E�B���h�E
	//�w���v�A�o�[�W�������
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

