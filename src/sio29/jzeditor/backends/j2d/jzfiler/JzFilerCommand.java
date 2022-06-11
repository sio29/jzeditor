/******************************************************************************
;	Jzファイラーコマンド
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
	//エディタで開く
	void OnOpenEditor(){
//System.out.println("OnOpenEditor");
		SetSelectFileExit(GetCurrentFile());
	}
	void OnOpenReadOnly(){
		OnOpenEditor();
	}
	//外部アプリで開く
	void OnOpenOther(){
//System.out.println("OnOpenOther");
		SetSelectFileExit(GetCurrentFile());
	}
	//新規テキストを開く
	void OnOpenNewFile(){
//System.out.println("OnOpenNewFile");
		SetSelectFileExit(null);
	}
	//閉じる
	void OnClose(){
		CancelExit();
	}
	//全て選択
	void OnSelectAll(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectAll();
	}
	//全てのファイル選択
	void OnSelectAllFile(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectAllFile();
	}
	//選択のフリップ
	void OnSelectFlip(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.SelectFlip();
	}
	//選択解除
	void OnUnSelect(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		list.UnSelect();
	}
	//ファイル名のコピー
	void OnCopyFilename(){
		JzFilerFileListPane list=GetCurrentList();
		if(list==null)return;
		List<UFile> vals=list.GetSelectFileList();
		JzFilerTool.CopyFilename(vals);
	}
	//ファイル名でソート
	void OnSortFilename(){
//System.out.println("OnSortFilename");
		//GetMenuSortType();
		jzfiler.setSortType(0);
		jzfiler.Update();
	}
	//拡張子でソート
	void OnSortExt(){
//System.out.println("OnSortExt");
		//GetMenuSortType();
		jzfiler.setSortType(1);
		jzfiler.Update();
	}
	//日付でソート
	void OnSortDate(){
//System.out.println("OnSortDate");
		//GetMenuSortType();
		jzfiler.setSortType(2);
		jzfiler.Update();
	}
	//サイズでソート
	void OnSortSize(){
//System.out.println("OnSortSize");
		//GetMenuSortType();
		jzfiler.setSortType(3);
		jzfiler.Update();
	}
	//ソート方向
	void OnSortDescending(){
//System.out.println("OnSortDescending");
		//GetMenuSortDir();
		jzfiler.setSortDir(0);
		jzfiler.Update();
	}
	//ソート方向
	void OnSortAscending(){
//System.out.println("OnSortAscending");
		//GetMenuSortDir();
		jzfiler.setSortDir(1);
		jzfiler.Update();
	}
	//==================================
	//コピー
	void OnCopy(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.CopyFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//移動
	void OnMove(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.MoveFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//削除
	void OnDelete(){
		SetPreview(null);
		UFile file1=GetCurrentFile();
		if(JzFilerTool.DeleteFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//名前を変えてコピー
	void OnRenameCopy(){
		UFile file1=GetCurrentFile();
		UFile dst_path=GetDstDir();
		if(JzFilerTool.RenameCopyFile(getComponent(),file1,dst_path)){
			OnUpdate();
		}
	}
	//ファイル名の変更
	void OnRename(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.RenameFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//属性の変更
	void OnAttr(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.ChangeAttrFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//日付変更
	void OnDate(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.ChangeDateFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//ショートカットの作成
	void OnFileShortcut(){
		UFile file1=GetCurrentFile();
		if(JzFilerTool.MakeShortCutFile(getComponent(),file1)){
			OnUpdate();
		}
	}
	//プロパティ
	void OnProp(){
		UFile file1=GetCurrentFile();
		JzFilerTool.ViewProperty(getComponent(),file1);
	}
	//新規ファイル
	void OnNewFile(){
		UFile current_dir=GetCurrentDir();
		if(JzFilerTool.NewFile(getComponent(),current_dir)){
			OnUpdate();
		}
	}
	//新規フォルダ
	void OnNewDir(){
		UFile current_dir=GetCurrentDir();
		if(JzFilerTool.NewDir(getComponent(),current_dir)){
			OnUpdate();
		}
	}
	//フォルダを開く
	void OnChangeDir(){
		JzFilerPairFileList pair_list=getPairFileList();
		pair_list.SelectDrive();
	}
	//親ディレクトリへ移動
	void OnChangeDirParent(){
		JzFilerPairFileList pair_list=getPairFileList();
		pair_list.ChangeDirParent();
	}
	//フォルダを開く
	void OnOpenDir(){
		UFile current_dir=GetCurrentDir();
		UFile new_dir=JzFilerTool.OpenDir(getComponent(),current_dir);
		if(new_dir!=null){
			SetCurrentDir(new_dir);
			OnUpdate();
		}
	}
	//フォルダの比較
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
	//フォルダ履歴
	void OnOpenDirHist(){
		HashSet<UFile> dir_history=getDirHistory();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"フォルダ履歴",dir_history,null,true);
		if(file!=null && file.isDirectory()){
//System.out.println(""+file);
			SetCurrentDir(file);
			OnUpdate();
		}
	}
	//フォルダリスト
	void OnOpenDirList(){
		HashSet<UFile> dir_list=getDirList();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"フォルダリスト",dir_list,null,true);
		if(file!=null && file.isDirectory()){
			SetCurrentDir(file);
			OnUpdate();
		}
	}
	//ファイル履歴
	void OnFileHist(){
		HashSet<UFile> file_history=getFileHistory();
		UFile file=JzFilerTool.FileHistoryDialog(getComponent(),"ファイル履歴",file_history,null,false);
		if(file!=null){
			SetSelectFileExit(file);
		}
	}
	//マスクリスト作成
	void OnMaskList(){
		HashSet<File> mask_list=getFileMaskList();
		File current_mask=getCurrentMask();
		mask_list.add(new File("*.*"));
		File file=JzFilerTool.FileMaskDialog(getComponent(),"マスクリスト",mask_list,current_mask,false);
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
	//設定
	void OnSetting(){
		OptionDialog();
	}
	//ショートカット
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
	//読み込み専用
	void OnSetReadOnly(){
//System.out.println("OnSetReadOnly");
		//GetMenuReadOnlyFlg();
		jzfiler.setReadOnlyFlg(!jzfiler.getReadOnlyFlg());
	}
	//他エディタで開く
	void OnSetOpenOther(){
//System.out.println("OnSetOpenOther");
		//GetMenuOpenOtherFlg();
		jzfiler.setOpenOtherFlg(!jzfiler.getOpenOtherFlg());
	}
	//2画面
	void OnSet2Window(){
//System.out.println("OnSet2Window");
		//GetMenu2WindowFlg();
		jzfiler.set2WindowFlg(!jzfiler.get2WindowFlg());
		Make2Window();
	}
	//新規ウィンドウ
	void OnNewWindow(){
	}
	//ヘルプ
	void OnHelp(){
		String filename="help/jzfiler/index.html";
		try{
			UFile ufilename=UFileBuilder.createLocal(filename);
			File file=((UFileURL)ufilename).getFile();
			java.awt.Desktop.getDesktop().open(file);
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Helpが開けません!!:"+filename);
		}
	}
	//バージョン
	public static final String app_name="JzFiler";
	public static final String app_ver="0.01";
	void OnVersion(){
		String about_mess=app_name+" Ver"+app_ver;
		UOptionDialogBaseBuilder.showMessageDialog(new UComponentJ2D(getComponent()),about_mess,app_name,UOptionDialogBase.INFORMATION_MESSAGE);
	}
	//更新
	void OnUpdate(){
		//repaint();
		Update();
	}
	//プレビュー設定
	void OnSetPreview(){
//System.out.println("OnSetPreview");
		//GetMenuPreviewFlg();
		jzfiler.setPreviewFlg(!jzfiler.getPreviewFlg());
		Make2Window();
	}
	//FTP設定
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

