/******************************************************************************
;	Jzファイラーコマンド情報
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

public class JzFilerActionInfo{
	public final static ActionInfo[] g_actioninfo_tbl={
		new ActionInfo(JzFilerAction.OpenEditor			,"エディターで開く"),
		new ActionInfo(JzFilerAction.OpenOther			,"関連アプリで開く"),
		new ActionInfo(JzFilerAction.OpenReadOnly		,"閲覧"),
		new ActionInfo(JzFilerAction.Prop				,"プロパティ"),
		new ActionInfo(JzFilerAction.Rename				,"名前の変更"),
		new ActionInfo(JzFilerAction.Attr				,"アトリュビートの変更"),
		new ActionInfo(JzFilerAction.Date				,"日付の変更"),
		new ActionInfo(JzFilerAction.FileShortcut		,"ショートカットの作成"),
		new ActionInfo(JzFilerAction.Close				,"終了"),
		new ActionInfo(JzFilerAction.Copy				,"コピー"),
		new ActionInfo(JzFilerAction.Move				,"移動"),
		new ActionInfo(JzFilerAction.Delete				,"削除"),
		new ActionInfo(JzFilerAction.RenameCopy			,"名前を変えてコピー"),
		new ActionInfo(JzFilerAction.SelectAll			,"すべて選択"),
		new ActionInfo(JzFilerAction.SelectAllFile		,"すべてのファイルを選択"),
		new ActionInfo(JzFilerAction.SelectFlip			,"選択の切り替え"),
		new ActionInfo(JzFilerAction.UnSelect			,"選択の解除"),
		new ActionInfo(JzFilerAction.CopyFilename		,"ファイル名をクリップボードにコピー"),
		new ActionInfo(JzFilerAction.SortFilename		,"ファイル名順"),
		new ActionInfo(JzFilerAction.SortExt			,"拡張子順"),
		new ActionInfo(JzFilerAction.SortDate			,"日付順"),
		new ActionInfo(JzFilerAction.SortSize			,"サイズ順"),
		new ActionInfo(JzFilerAction.SortDescending		,"降順"),
		new ActionInfo(JzFilerAction.SortAscending		,"昇順"),
		new ActionInfo(JzFilerAction.NewFile			,"新規ファイル作成"),
		new ActionInfo(JzFilerAction.NewDir				,"新規フォルダ作成"),
		new ActionInfo(JzFilerAction.ChangeDir			,"ドライブの変更"),
		new ActionInfo(JzFilerAction.ChangeDirParent	,"親ディレクトリへ移動"),
		new ActionInfo(JzFilerAction.OpenDir			,"フォルダを開く"),
		new ActionInfo(JzFilerAction.OpenDirHist		,"最近使ったフォルダ"),
		new ActionInfo(JzFilerAction.OpenDirList		,"フォルダリスト"),
		new ActionInfo(JzFilerAction.CompareDir			,"フォルダの比較"),
		new ActionInfo(JzFilerAction.MaskList			,"マスクリスト"),
		new ActionInfo(JzFilerAction.OpenNewFile		,"新規テキストを開く"),
		new ActionInfo(JzFilerAction.FileHist			,"最近使ったファイル"),
		new ActionInfo(JzFilerAction.Grep				,"Grep"),
		new ActionInfo(JzFilerAction.Setting			,"設定"),
		new ActionInfo(JzFilerAction.KeyShortcut		,"キーのショートカット"),
		new ActionInfo(JzFilerAction.SetReadOnly		,"閲覧で開く"),
		new ActionInfo(JzFilerAction.SetOpenOther		,"関連アプリケーションで開く"),
		new ActionInfo(JzFilerAction.Set2Window			,"2ウィンドウ"),
		new ActionInfo(JzFilerAction.Update				,"最新情報に更新"),
		new ActionInfo(JzFilerAction.SetPreview			,"プレビュー"),
		new ActionInfo(JzFilerAction.NewWindow			,"新しいウィンドウ"),
		new ActionInfo(JzFilerAction.Help				,"ヘルプ"),
		new ActionInfo(JzFilerAction.Version			,"バージョン情報"),
		new ActionInfo(JzFilerAction.FileFTPInfo		,"FTP登録"),
		
	};
	public static ActionInfo[] getActionInfoTbl(){
		return g_actioninfo_tbl;
	}
}
