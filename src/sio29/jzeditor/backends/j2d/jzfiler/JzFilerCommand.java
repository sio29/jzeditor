/******************************************************************************
;	Jz�t�@�C���[�R�}���h
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.ufile.backends.j2d.ftp.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

import sio29.jzeditor.backends.j2d.dialogs.grep.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.jzfiler.ftp.*;

public class JzFilerCommand{
	private JzFilerDialog jzfiler;
	public JzFilerCommand(JzFilerDialog _jzfiler){
		jzfiler=_jzfiler;
	}
	//===========================
	public boolean SetCurrentDir(UFile dir){
		return jzfiler.SetCurrentDir(dir);
	}
	UFile GetCurrentDir(){
		return jzfiler.GetCurrentDir();
	}
	
	UFile GetCurrentFile(){
		return jzfiler.GetCurrentFile();
	}
	UFile GetDstDir(){
		return jzfiler.GetDstDir();
	}
	JzFilerFileListPane GetCurrentList(){
		return jzfiler.GetCurrentList();
	}
	void SetSelectFileExit(UFile filename){
		jzfiler.SetSelectFileExit(filename);
	}
	void CancelExit(){
		jzfiler.CancelExit();
	}
	/*
	void GetMenuSortType(){
		jzfiler.GetMenuSortType();
	}
	void GetMenuSortDir(){
		jzfiler.GetMenuSortDir();
	}
	void GetMenuReadOnlyFlg(){
		jzfiler.GetMenuReadOnlyFlg();
	}
	void GetMenuOpenOtherFlg(){
		jzfiler.GetMenuOpenOtherFlg();
	}
	void GetMenu2WindowFlg(){
		jzfiler.GetMenu2WindowFlg();
	}
	void GetMenuPreviewFlg(){
		jzfiler.GetMenuPreviewFlg();
	}
	*/
	void SetPreview(UFile filename){
		jzfiler.SetPreview(filename);
	}
	Component getComponent(){
		return jzfiler;
	}
	JzFilerPairFileList getPairFileList(){
		return jzfiler.getPairFileList();
	}
	JzFilerDirComparetor.DirCompareOpt getDirCompareOpt(){
		return jzfiler.getDirCompareOpt();
	}
	HashSet<UFile> getDirHistory(){
		return jzfiler.getDirHistory();
	}
	HashSet<UFile> getDirList(){
		return jzfiler.getDirList();
	}
	HashSet<File> getFileMaskList(){
		return jzfiler.getFileMaskList();
	}
	void setCurrentMask(File n){
		jzfiler.setCurrentMask(n);
	}
	File getCurrentMask(){
		return jzfiler.getCurrentMask();
	}
	void MakeFileFilterFromMask(String mask_str){
		jzfiler.MakeFileFilterFromMask(mask_str);
	}
	HashSet<UFile> getFileHistory(){
		return jzfiler.getFileHistory();
	}
	void OptionDialog(){
		jzfiler.OptionDialog();
	}
	void Make2Window(){
		jzfiler.Make2Window();
	}
	void OnUpdatePairList(){
		jzfiler.OnUpdatePairList();
	}
	FTPInfoList getFTPInfo(){
		return jzfiler.getFTPInfo();
	}
	void Update(){
		jzfiler.Update();
	}
	//===========================
	//�G�f�B�^�ŊJ��
	void OnOpenEditor(){
//System.out.println("OnOpenEditor");
		SetSelectFileExit(GetCurrentFile());
	}
	void OnOpenReadOnly(){
		OnOpenEditor();
	}
	//�O���A�v���ŊJ��
	void OnOpenOther(){
//System.out.println("OnOpenOther");
		SetSelectFileExit(GetCurrentFile());
	}
	//�V�K�e�L�X�g���J��
	void OnOpenNewFile(){
//System.out.println("OnOpenNewFile");
		SetSelectFileExit(null);
	}
	//����
	void OnClose(){
		CancelExit();
	}
	//�S�đI��
	void OnSelectAll(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectAll();
	}
	//�S�Ẵt�@�C���I��
	void OnSelectAllFile(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectAllFile();
	}
	//�I���̃t���b�v
	void OnSelectFlip(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectFlip();
	}
	//�I������
	void OnUnSelect(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.UnSelect();
	}
	//�t�@�C�����̃R�s�[
	void OnCopyFilename(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		List<UFile> vals=list.GetSelectFileList();
		JzFilerTool.CopyFilename(vals);
	}
	//�t�@�C�����Ń\�[�g
	void OnSortFilename(){
//System.out.println("OnSortFilename");
		//GetMenuSortType();
		jzfiler.setSortType(0);
		jzfiler.Update();
	}
	//�g���q�Ń\�[�g
	void OnSortExt(){
//System.out.println("OnSortExt");
		//GetMenuSortType();
		jzfiler.setSortType(1);
		jzfiler.Update();
	}
	//���t�Ń\�[�g
	void OnSortDate(){
//System.out.println("OnSortDate");
		//GetMenuSortType();
		jzfiler.setSortType(2);
		jzfiler.Update();
	}
	//�T�C�Y�Ń\�[�g
	void OnSortSize(){
//System.out.println("OnSortSize");
		//GetMenuSortType();
		jzfiler.setSortType(3);
		jzfiler.Update();
	}
	//�\�[�g����
	void OnSortDescending(){
//System.out.println("OnSortDescending");
		//GetMenuSortDir();
		jzfiler.setSortDir(0);
		jzfiler.Update();
	}
	//�\�[�g����
	void OnSortAscending(){
//System.out.println("OnSortAscending");
		//GetMenuSortDir();
		jzfiler.setSortDir(1);
		jzfiler.Update();
	}
	//==================================
	//�R�s�[
	void OnCopy(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.CopyFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//�ړ�
	void OnMove(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.MoveFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//�폜
	void OnDelete(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		if(JzFilerTool.DeleteFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//���O��ς��ăR�s�[
	void OnRenameCopy(){
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.RenameCopyFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//�t�@�C�����̕ύX
	void OnRename(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.RenameFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//�����̕ύX
	void OnAttr(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.ChangeAttrFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//���t�ύX
	void OnDate(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.ChangeDateFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//�V���[�g�J�b�g�̍쐬
	void OnFileShortcut(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.MakeShortCutFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//�v���p�e�B
	void OnProp(){
		UFile file1=GetCurrentFile();
		JzFilerTool.ViewProperty(getComponent(),file1);
	}
	//�V�K�t�@�C��
	void OnNewFile(){
		UFile current_dir=GetCurrentDir();
		if(JzFilerTool.NewFile(getComponent(),current_dir)){
			OnUpdate();
		}
	}
	//�V�K�t�H���_
	void OnNewDir(){
		UFile current_dir=GetCurrentDir();
		if(JzFilerTool.NewDir(getComponent(),current_dir)){
			OnUpdate();
		}
	}
	//�t�H���_���J��
	void OnChangeDir(){
		JzFilerPairFileList pair_list=getPairFileList();
		pair_list.SelectDrive();
	}
	//�e�f�B���N�g���ֈړ�
	void OnChangeDirParent(){
		JzFilerPairFileList pair_list=getPairFileList();
		pair_list.ChangeDirParent();
	}
	//�t�H���_���J��
	void OnOpenDir(){
		UFile current_dir=GetCurrentDir();
		UFile new_dir=JzFilerTool.OpenDir(getComponent(),current_dir);
		if(new_dir!=null){
			SetCurrentDir(new_dir);
			OnUpdate();
		}
	}
	//�t�H���_�̔�r
	void OnCompareDir(){
		JzFilerDirComparetor.DirCompareOpt compare_opt=getDirCompareOpt();
		JzFilerPairFileList pair_list=getPairFileList();
		if(JzFilerTool.FileCompareDialog(getComponent(),compare_opt)){
			UFile[] dir1=pair_list.GetFiles(0);
			UFile[] dir2=pair_list.GetFiles(1);
			boolean[][] ret=JzFilerDirComparetor.CompareDirList(dir1,dir2,compare_opt);
			pair_list.SetSelectFiles(0,dir1,ret[0]);
			pair_list.SetSelectFiles(1,dir2,ret[1]);
		}
	}
	//�t�H���_����
	void OnOpenDirHist(){
		HashSet<UFile> dir_history=getDirHistory();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"�t�H���_����",dir_history,null,true);
		if(file!=null && file.isDirectory()){
//System.out.println(""+file);
			SetCurrentDir(file);
			OnUpdate();
		}
	}
	//�t�H���_���X�g
	void OnOpenDirList(){
		HashSet<UFile> dir_list=getDirList();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"�t�H���_���X�g",dir_list,null,true);
		if(file!=null && file.isDirectory()){
			SetCurrentDir(file);
			OnUpdate();
		}
	}
	//�t�@�C������
	void OnFileHist(){
		HashSet<UFile> file_history=getFileHistory();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"�t�@�C������",file_history,null,false);
		if(file!=null){
			SetSelectFileExit(file);
		}
	}
	//�}�X�N���X�g�쐬
	void OnMaskList(){
		HashSet<File> mask_list=getFileMaskList();
		File current_mask=getCurrentMask();
		mask_list.add(new File("*.*"));
		File file=JzFilerTool.FileMaskDialog(getComponent(),"�}�X�N���X�g",mask_list,current_mask,false);
		if(file!=null){
			current_mask=file;
			setCurrentMask(current_mask);
			MakeFileFilterFromMask(current_mask.toString());
			OnUpdate();
		}
	}
	//Grep
	static GrepOption grep_opt=new GrepOption();
	void OnGrep(){
		String[] ret=GrepDialog();
		String dir_str=grep_opt.dir_str;
		if(dir_str==null || dir_str.length()==0)return;
		boolean subdir_flg=grep_opt.subdir_flg;
		String search_str=grep_opt.search_str;
		Object search_opt=grep_opt.search_opt;
		//
		JzGrepFunc func=new JzGrepFunc(){
			public void addFile(UFile file){
				String m=String.format("%s\n",file.toString());
				System.out.print(m);
			}
			public void addString(UFile file,int line,String m){
				String m2=String.format("%s(%d) %s\n",file.toString(),line,m);
				System.out.print(m2);
			}
		};
		GrepTool.execGrep(func,search_str,search_opt,dir_str,subdir_flg);
		
	}
	String[] GrepDialog(){
		UFile current_dir=GetCurrentDir();
		try{
			String current_dir_name=((UFileURL)current_dir).getFile().getAbsolutePath();
			String[] value=GrepDialog.Open(getComponent(),"Grep",grep_opt,current_dir_name);
			if(value==null)return null;
			if(value[0].length()==0 || value[1].length()==0)return null;
			return value;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	//�ݒ�
	void OnSetting(){
		OptionDialog();
	}
	//�V���[�g�J�b�g
	void OnKeyShortcut(){
		/*
		JzFilerPairFileList pair_list=getPairFileList();
		JComponent c=pair_list.GetCurrentList().getList();
		ShortCutData shortcut=new ShortCutData();
		shortcut.setComponent(c);
		if(ShortCutDialog.Open(getComponent(),c,shortcut)){
			
		}
		*/
		JzFilerPairFileList pair_list=getPairFileList();
		JComponent c=pair_list.GetCurrentList().getList();
		try{
			UFile file=UFileBuilder.createLocal("jzfiler.shortcut");
			if(ShortCutDialog.Open(getComponent(),c,file)){
				
			}
		}catch(Exception ex){}
		
	}
	//�ǂݍ��ݐ�p
	void OnSetReadOnly(){
//System.out.println("OnSetReadOnly");
		//GetMenuReadOnlyFlg();
		jzfiler.setReadOnlyFlg(!jzfiler.getReadOnlyFlg());
	}
	//���G�f�B�^�ŊJ��
	void OnSetOpenOther(){
//System.out.println("OnSetOpenOther");
		//GetMenuOpenOtherFlg();
		jzfiler.setOpenOtherFlg(!jzfiler.getOpenOtherFlg());
	}
	//2���
	void OnSet2Window(){
//System.out.println("OnSet2Window");
		//GetMenu2WindowFlg();
		jzfiler.set2WindowFlg(!jzfiler.get2WindowFlg());
		Make2Window();
	}
	//�V�K�E�B���h�E
	void OnNewWindow(){
	}
	//�w���v
	void OnHelp(){
		String filename="help/jzfiler/index.html";
		try{
			UFile ufilename=UFileBuilder.createLocal(filename);
			File file=((UFileURL)ufilename).getFile();
			java.awt.Desktop.getDesktop().open(file);
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Help���J���܂���!!:"+filename);
		}
	}
	//�o�[�W����
	public static final String app_name="JzFiler";
	public static final String app_ver="0.01";
	void OnVersion(){
		String about_mess=app_name+" Ver"+app_ver;
		UOptionDialogBaseBuilder.showMessageDialog(new UComponentJ2D(getComponent()),about_mess,app_name,UOptionDialogBase.INFORMATION_MESSAGE);
	}
	//�X�V
	void OnUpdate(){
		//repaint();
		Update();
	}
	//�v���r���[�ݒ�
	void OnSetPreview(){
//System.out.println("OnSetPreview");
		//GetMenuPreviewFlg();
		jzfiler.setPreviewFlg(!jzfiler.getPreviewFlg());
		Make2Window();
	}
	//FTP�ݒ�
	void OnFTPInfo(){
		FTPInfoList ftpinfo=getFTPInfo();
		//FTPInfoOne ret=JzFilerFTP.FTPInfoDialog(getComponent(),ftpinfo);
		UFileFTPInfo ret=JzFilerFTP.FTPInfoDialog(getComponent(),ftpinfo);
		if(ret!=null){
			
			
			
		}
	}
	//========================================================
	//
	//========================================================
	private ActionTbl[] g_act_tbl={
		new ActionTbl(JzFilerAction.OpenEditor,this,"OnOpenEditor"),
		new ActionTbl(JzFilerAction.OpenOther,this,"OnOpenOther"),
		new ActionTbl(JzFilerAction.OpenReadOnly,this,"OnOpenReadOnly"),
		new ActionTbl(JzFilerAction.Prop,this,"OnProp"),
		new ActionTbl(JzFilerAction.Rename,this,"OnRename"),
		new ActionTbl(JzFilerAction.Attr,this,"OnAttr"),
		new ActionTbl(JzFilerAction.Date,this,"OnDate"),
		new ActionTbl(JzFilerAction.FileShortcut,this,"OnFileShortcut"),
		new ActionTbl(JzFilerAction.Close,this,"OnClose"),
		new ActionTbl(JzFilerAction.Copy,this,"OnCopy"),
		new ActionTbl(JzFilerAction.Move,this,"OnMove"),
		new ActionTbl(JzFilerAction.Delete,this,"OnDelete"),
		new ActionTbl(JzFilerAction.RenameCopy,this,"OnRenameCopy"),
		new ActionTbl(JzFilerAction.SelectAll,this,"OnSelectAll"),
		new ActionTbl(JzFilerAction.SelectAllFile,this,"OnSelectAllFile"),
		new ActionTbl(JzFilerAction.SelectFlip,this,"OnSelectFlip"),
		new ActionTbl(JzFilerAction.UnSelect,this,"OnUnSelect"),
		new ActionTbl(JzFilerAction.CopyFilename,this,"OnCopyFilename"),
		new ActionTbl(JzFilerAction.SortFilename,this,"OnSortFilename"),
		new ActionTbl(JzFilerAction.SortExt,this,"OnSortExt"),
		new ActionTbl(JzFilerAction.SortDate,this,"OnSortDate"),
		new ActionTbl(JzFilerAction.SortSize,this,"OnSortSize"),
		new ActionTbl(JzFilerAction.SortDescending,this,"OnSortDescending"),
		new ActionTbl(JzFilerAction.SortAscending,this,"OnSortAscending"),
		new ActionTbl(JzFilerAction.NewFile,this,"OnNewFile"),
		new ActionTbl(JzFilerAction.NewDir,this,"OnNewDir"),
		new ActionTbl(JzFilerAction.ChangeDir,this,"OnChangeDir"),
		new ActionTbl(JzFilerAction.ChangeDirParent,this,"OnChangeDirParent"),
		new ActionTbl(JzFilerAction.OpenDir,this,"OnOpenDir"),
		new ActionTbl(JzFilerAction.OpenDirHist,this,"OnOpenDirHist"),
		new ActionTbl(JzFilerAction.OpenDirList,this,"OnOpenDirList"),
		new ActionTbl(JzFilerAction.CompareDir,this,"OnCompareDir"),
		new ActionTbl(JzFilerAction.MaskList,this,"OnMaskList"),
		new ActionTbl(JzFilerAction.OpenNewFile,this,"OnOpenNewFile"),
		new ActionTbl(JzFilerAction.FileHist,this,"OnFileHist"),
		new ActionTbl(JzFilerAction.Grep,this,"OnGrep"),
		new ActionTbl(JzFilerAction.Setting,this,"OnSetting"),
		new ActionTbl(JzFilerAction.KeyShortcut,this,"OnKeyShortcut"),
		new ActionTbl(JzFilerAction.SetReadOnly,this,"OnSetReadOnly"),
		new ActionTbl(JzFilerAction.SetOpenOther,this,"OnSetOpenOther"),
		new ActionTbl(JzFilerAction.Set2Window,this,"OnSet2Window"),
		new ActionTbl(JzFilerAction.Update,this,"OnUpdate"),
		new ActionTbl(JzFilerAction.SetPreview,this,"OnSetPreview"),
		new ActionTbl(JzFilerAction.NewWindow,this,"OnNewWindow"),
		new ActionTbl(JzFilerAction.Help,this,"OnHelp"),
		new ActionTbl(JzFilerAction.Version,this,"OnVersion"),
		new ActionTbl(JzFilerAction.FileFTPInfo,this,"OnFTPInfo"),
	};
	ActionTbl[] getActionTbl(){
		return g_act_tbl;
	}
}

