/******************************************************************************
;	オプションダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.option;

import sio29.ulib.umat.*;
import sio29.ulib.ureg.*;

public class OptionData{
	public String editor_filename;				//外部エディタファイル名
	public String editor_opt;					//外部エディタオプション
	public String editor_opt_tag;				//外部エディタオプションタグ
	public boolean editor_opt_line;				//外部エディタ行指定
	public String filer_filename;				//外部ファイラーファイル名
	public String filer_opt;					//外部ファイラーオプション
	public CVECTOR col_back;					//背景
	public CVECTOR col_normal;					//通常
	public CVECTOR col_select;					//選択
	public CVECTOR col_tab;						//タブ
	public CVECTOR col_ret;						//改行
	public CVECTOR col_eof;						//EOF
	public CVECTOR col_comment;					//コメント
	public CVECTOR col_str;						//文字列
	public CVECTOR col_macro;					//マクロ
	public CVECTOR col_backlog;					//バックログ
	public boolean draw_tab;					//タブ表示
	public boolean draw_cr;						//改行表示
	public boolean draw_eof;					//EOF表示
	public boolean draw_zenspace;				//全角空白表示
	public boolean draw_space;					//半角空白表示
	public boolean draw_linenum;				//行番号表示
	public boolean draw_ruler;					//ルーラー表示
	public boolean auto_tab_flg;				//オートタブ有効
	public boolean comline_flg;					//立ち上げ時コマンドライン
	public boolean doubleboot_flg;				//二重立ち上げ
	public int doubleboot_port;					//二重立ち上げ用ポート
	public boolean deletereg_flg;				//終了時レジストリ削除
	public String font_name;					//フォント名
	public int font_size;						//フォントサイズ
	public String lookandfeel;					//ルック＆フィール
	public int inner_filer;						//ファイルダイアログのファイルフィルタIndex
	public boolean save_ask_flg;				//保存時確認
	public boolean noclose_filer;				//ファイラーを閉じない
//	public boolean compo_type;					//
	private int compo_type;						//
	//
	public OptionData(){
		editor_filename="c:\\wbin\\wz\\wzeditor.exe";
		//editor_opt="$filename";
		//editor_opt_tag="/j $line $filename";
		editor_opt="";
		editor_opt_tag="/j";
		editor_opt_line=true;
		filer_filename="C:\\wbin\\WinFD\\WinFD.exe";
		//filer_opt="$filename";
		filer_opt="";
		col_back  =CVECTOR.BLACK;
		col_normal=CVECTOR.WHITE;
		col_select=CVECTOR.CYAN.darker().darker();
		col_tab=CVECTOR.CYAN;
		col_ret=CVECTOR.CYAN;
		col_eof=CVECTOR.CYAN;
		col_comment=CVECTOR.CYAN;
		col_str=CVECTOR.CYAN;
		col_macro=CVECTOR.CYAN;
		col_backlog=CVECTOR.BLUE.darker();
		draw_tab=true;
		draw_cr=true;
		draw_eof=true;
		draw_zenspace=true;
		draw_space=false;
		draw_linenum=false;
		draw_ruler=false;
		auto_tab_flg=true;
		//comline_flg=true;
		comline_flg=false;
		doubleboot_flg=false;
		doubleboot_port=38765;
		deletereg_flg=false;
		font_name="ＭＳ ゴシック";
		font_size=14;
		lookandfeel="Windows";
		inner_filer=0;
		save_ask_flg=true;
		noclose_filer=true;
//		compo_type=false;				//
		compo_type=0;				//
	}
	public OptionData(OptionData src){
		editor_filename=src.editor_filename;
		editor_opt=src.editor_opt;
		editor_opt_tag=src.editor_opt_tag;
		filer_filename=src.filer_filename;
		filer_opt=src.filer_opt;
	}
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		editor_filename=regout.ReadRegStr  (node,"editor_filename",editor_filename);
		editor_opt     =regout.ReadRegStr  (node,"editor_opt"     ,editor_opt);
		editor_opt_tag =regout.ReadRegStr  (node,"editor_opt_tag" ,editor_opt_tag);
		editor_opt_line=regout.ReadRegBool (node,"editor_opt_line",editor_opt_line);
		filer_filename =regout.ReadRegStr  (node,"filer_filename" ,filer_filename);
		filer_opt      =regout.ReadRegStr  (node,"filer_opt"      ,filer_opt);
		comline_flg    =regout.ReadRegBool (node,"comline_flg"    ,comline_flg);
		doubleboot_flg =regout.ReadRegBool (node,"doubleboot_flg" ,doubleboot_flg);
		doubleboot_port=regout.ReadRegInt  (node,"doubleboot_port",doubleboot_port);
		font_name      =regout.ReadRegStr  (node,"font_name"      ,font_name);
		font_size      =regout.ReadRegInt  (node,"font_size"      ,font_size);
		col_back       =regout.ReadRegColor(node,"col_back"       ,col_back);
		col_normal     =regout.ReadRegColor(node,"col_normal"     ,col_normal);
		col_select     =regout.ReadRegColor(node,"col_select"     ,col_select);
		col_tab        =regout.ReadRegColor(node,"col_tab"        ,col_tab);
		col_eof        =regout.ReadRegColor(node,"col_eof"        ,col_eof);
		col_comment    =regout.ReadRegColor(node,"col_comment"    ,col_comment);
		col_str        =regout.ReadRegColor(node,"col_str"        ,col_str);
		col_macro      =regout.ReadRegColor(node,"col_macro"      ,col_macro);
		col_backlog    =regout.ReadRegColor(node,"col_backlog"    ,col_backlog);
		lookandfeel    =regout.ReadRegStr  (node,"lookandfeel"    ,lookandfeel);
		inner_filer    =regout.ReadRegInt  (node,"inner_filer"    ,inner_filer);
		save_ask_flg   =regout.ReadRegBool (node,"save_ask_flg"   ,save_ask_flg);
		draw_tab       =regout.ReadRegBool (node,"draw_tab"       ,draw_tab);
		draw_cr        =regout.ReadRegBool (node,"draw_cr"        ,draw_cr);
		draw_eof       =regout.ReadRegBool (node,"draw_eof"       ,draw_eof);
		draw_zenspace  =regout.ReadRegBool (node,"draw_zenspace"  ,draw_zenspace);
		draw_space     =regout.ReadRegBool (node,"draw_space"     ,draw_space);
		draw_linenum   =regout.ReadRegBool (node,"draw_linenum"   ,draw_linenum);
		draw_ruler     =regout.ReadRegBool (node,"draw_ruler"     ,draw_ruler);
		auto_tab_flg   =regout.ReadRegBool (node,"auto_tab_flg"   ,auto_tab_flg);
		noclose_filer  =regout.ReadRegBool (node,"noclose_filer"  ,noclose_filer);
//		compo_type     =regout.ReadRegBool (node,"compo_type"     ,compo_type);
		compo_type     =regout.ReadRegInt  (node,"compo_type"     ,compo_type);
		
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		regout.OutRegStr  (node,"editor_filename",editor_filename);
		regout.OutRegStr  (node,"editor_opt"     ,editor_opt);
		regout.OutRegStr  (node,"editor_opt_tag" ,editor_opt_tag);
		regout.OutRegBool (node,"editor_opt_line",editor_opt_line);
		regout.OutRegStr  (node,"filer_filename" ,filer_filename);
		regout.OutRegStr  (node,"filer_opt"      ,filer_opt);
		regout.OutRegBool (node,"comline_flg"    ,comline_flg);
		regout.OutRegBool (node,"doubleboot_flg" ,doubleboot_flg);
		regout.OutRegInt  (node,"doubleboot_port",doubleboot_port);
		regout.OutRegStr  (node,"font_name"      ,font_name);
		regout.OutRegInt  (node,"font_size"      ,font_size);
		regout.OutRegColor(node,"col_back"       ,col_back);
		regout.OutRegColor(node,"col_normal"     ,col_normal);
		regout.OutRegColor(node,"col_select"     ,col_select);
		regout.OutRegColor(node,"col_tab"        ,col_tab);
		regout.OutRegColor(node,"col_eof"        ,col_eof);
		regout.OutRegColor(node,"col_comment"    ,col_comment);
		regout.OutRegColor(node,"col_str"        ,col_str);
		regout.OutRegColor(node,"col_macro"      ,col_macro);
		regout.OutRegColor(node,"col_backlog"    ,col_backlog);
		regout.OutRegStr  (node,"lookandfeel"    ,lookandfeel);
		regout.OutRegInt  (node,"inner_filer"    ,inner_filer);
		regout.OutRegBool (node,"save_ask_flg"   ,save_ask_flg);
		regout.OutRegBool (node,"draw_tab"       ,draw_tab);
		regout.OutRegBool (node,"draw_cr"        ,draw_cr);
		regout.OutRegBool (node,"draw_eof"       ,draw_eof);
		regout.OutRegBool (node,"draw_zenspace"  ,draw_zenspace);
		regout.OutRegBool (node,"draw_space"     ,draw_space);
		regout.OutRegBool (node,"draw_linenum"   ,draw_linenum);
		regout.OutRegBool (node,"draw_ruler"     ,draw_ruler);
		regout.OutRegBool (node,"auto_tab_flg"   ,auto_tab_flg);
		regout.OutRegBool (node,"noclose_filer"  ,noclose_filer);
//		regout.OutRegBool (node,"compo_type"     ,compo_type);
		regout.OutRegInt  (node,"compo_type"     ,compo_type);
	}
	public int getCompoType(){
//		return 0;
		return compo_type;
	}
	public void setCompoType(int type){
		compo_type=type;
	}
	
}

