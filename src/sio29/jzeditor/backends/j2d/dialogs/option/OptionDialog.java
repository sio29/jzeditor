/******************************************************************************
;	オプションダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.option;

import sio29.ulib.umat.*;
import sio29.ulib.udlgbase.*;

public class OptionDialog {
	public static boolean Open(UComponent parent,OptionData opt){
		String title="オプション";
		final UFilenameTextField  panel_editor=UFilenameTextFieldBuilder.create();
		final UInputHistoryTextField panel_editoropt=UInputHistoryTextFieldBuilder.create();
		final UInputHistoryTextField panel_tagjump=UInputHistoryTextFieldBuilder.create();
		final UCheckBox panel_tagjumpline=UCheckBoxBuilder.create("行番号を指定する");
		final UFilenameTextField  panel2=UFilenameTextFieldBuilder.create();
		final UInputHistoryTextField panel_fileropt =UInputHistoryTextFieldBuilder.create();
		final UColorSelectButton panel_backcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_textcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_selectcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_tabcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_retcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_eofcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_commentcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_strcol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_macrocol=UColorSelectButtonBuilder.create();
		final UColorSelectButton panel_backlogcol=UColorSelectButtonBuilder.create();
		final UCheckBox panel_comline=UCheckBoxBuilder.create();
		final UCheckBox panel_doubleboot=UCheckBoxBuilder.create();
		final UIntValField panel_useport =UIntValFieldBuilder.create();
		final UCheckBox panel_delreg=UCheckBoxBuilder.create();
		final UFontSelectField panel_fontselect=UFontSelectFieldBuilder.create ();
		final UIntValField panel_fontsize =UIntValFieldBuilder.create();
		final ULookAndFeelSelectField panel_lookandfeel =ULookAndFeelSelectFieldBuilder.create();
		final UComboBox<String> panel_innerfiler =UComboBoxBuilder.<String>create(new String[]{"ノーマル","Vzライク"});
		final UCheckBox panel_saveask=UCheckBoxBuilder.create();
		final UCheckBox panel_noclose_filer=UCheckBoxBuilder.create();
		//final UCheckBox panel_compo_type=UCheckBoxBuilder.create();
		final UComboBox panel_compo_type=UComboBoxBuilder.<String>create(new String[]{"JTextPane","JTextArea","VRAM"});
		//
		Object[][] compos1={
			{"外部エディタ"		,panel_editor},
			{"オプション"		,panel_editoropt},
			{null,panel_tagjumpline},
			{"タブジャンプ"		,panel_tagjump},
			{"外部ファイラー"	,panel2},
			{"オプション"		,panel_fileropt},
		};
		Object[][] compos2={
			{"フォント選択"		,panel_fontselect},
			{"フォントサイズ"	,panel_fontsize},
			{"背景色"			,panel_backcol},
			{"テキスト色"		,panel_textcol},
			{"選択色"			,panel_selectcol},
			{"タブ色"			,panel_tabcol},
			{"改行色"   		,panel_retcol},
			{"EOF色"    		,panel_eofcol},
			{"コメント色"		,panel_commentcol},
			{"文字列色"			,panel_strcol},
			{"マクロ色"			,panel_macrocol},
			{"バックログ色"		,panel_backlogcol},
		};
		Object[][] compos3={
			{"立ち上げ時コマンドライン",panel_comline},
			{"二重起動禁止"			,panel_doubleboot},
			{"使用ポート"			,panel_useport},
			{"終了時レジストリ削除"	,panel_delreg},
			{"ルックアンドフィール"	,panel_lookandfeel},
			{"内部ファイラー"		,panel_innerfiler},
			{"ファイラーを閉じない"	,panel_noclose_filer},
			{"セーブ時ダイアログ"	,panel_saveask},
			{"コンポーネント"		,panel_compo_type},
			
		};
		int width=320;
		UComponent base_panel1=UOptionDialogBaseBuilder.MakeGroupContainer(compos1);
		UComponent base_panel2=UOptionDialogBaseBuilder.MakeGroupContainer(compos2);
		UComponent base_panel3=UOptionDialogBaseBuilder.MakeGroupContainer(compos3);
		ResizePanelWidth(base_panel1,width);
		ResizePanelWidth(base_panel2,width);
		ResizePanelWidth(base_panel3,width);
		//
		final UComponent top_panel=panel_editor;
		panel_editor.setText(opt.editor_filename);
		panel_editoropt.setText(opt.editor_opt);
		panel_tagjump.setText(opt.editor_opt_tag);
		panel_tagjumpline.setSelected(opt.editor_opt_line);
		panel2.setText(opt.filer_filename);
		panel_fileropt.setText(opt.filer_opt);
		panel_backcol.SetItem(opt.col_back);
		panel_textcol.SetItem(opt.col_normal);
		panel_selectcol.SetItem(opt.col_select);
		panel_tabcol.SetItem(opt.col_tab);
		panel_retcol.SetItem(opt.col_ret);
		panel_eofcol.SetItem(opt.col_eof);
		panel_commentcol.SetItem(opt.col_comment);
		panel_strcol.SetItem(opt.col_str);
		panel_macrocol.SetItem(opt.col_macro);
		panel_backlogcol.SetItem(opt.col_backlog);
		panel_comline.setSelected(opt.comline_flg);
		panel_doubleboot.setSelected(opt.doubleboot_flg);
		panel_useport.SetVal(opt.doubleboot_port);
		panel_delreg.setSelected(opt.deletereg_flg);
		panel_fontselect.setSelectFontName(opt.font_name);
		panel_fontsize.SetVal(opt.font_size);
		panel_lookandfeel.setSelectLookAndFeelName(opt.lookandfeel);
		panel_innerfiler.setSelectedIndex(opt.inner_filer);
		panel_saveask.setSelected(opt.save_ask_flg);
		panel_noclose_filer.setSelected(opt.noclose_filer);
		//panel_compo_type.setSelected(opt.compo_type);
		panel_compo_type.setSelectedIndex(opt.getCompoType());
		//
		final UTabbedPane tab=UTabbedPaneBuilder.create();
		tab.add("外部呼出し",base_panel1);
		tab.add("色設定",base_panel2);
		tab.add("基本設定",base_panel3);
		//
		if(!UOptionDialogBaseBuilder.Open(parent,title,tab,top_panel))return false;
		OptionData new_opt=opt;
		new_opt.editor_filename=panel_editor.getText();
		new_opt.editor_opt=panel_editoropt.getText();
		new_opt.editor_opt_tag=panel_tagjump.getText();
		new_opt.editor_opt_line=panel_tagjumpline.isSelected();
		new_opt.filer_filename=panel2.getText();
		new_opt.filer_opt=panel_fileropt.getText();
		new_opt.col_back=panel_backcol.GetItem();
		new_opt.col_normal=panel_textcol.GetItem();
		new_opt.col_select=panel_selectcol.GetItem();
		new_opt.col_tab=panel_tabcol.GetItem();
		new_opt.col_ret=panel_retcol.GetItem();
		new_opt.col_eof=panel_eofcol.GetItem();
		new_opt.col_comment=panel_commentcol.GetItem();
		new_opt.col_str=panel_strcol.GetItem();
		new_opt.col_macro=panel_macrocol.GetItem();
		new_opt.col_backlog=panel_backlogcol.GetItem();
		new_opt.comline_flg=panel_comline.isSelected();
		new_opt.doubleboot_flg=panel_doubleboot.isSelected();
		new_opt.doubleboot_port=panel_useport.GetVal();
		new_opt.deletereg_flg=panel_delreg.isSelected();
		new_opt.font_name=panel_fontselect.getSelectFontName();
		new_opt.font_size=panel_fontsize.GetVal();
		new_opt.lookandfeel=panel_lookandfeel.getSelectLookAndFeelName();
		new_opt.inner_filer=panel_innerfiler.getSelectedIndex();
		new_opt.save_ask_flg=panel_saveask.isSelected();
		new_opt.noclose_filer=panel_noclose_filer.isSelected();
		//new_opt.compo_type =panel_compo_type.isSelected();
		new_opt.setCompoType(panel_compo_type.getSelectedIndex());
		
		return true;
	}
	private static void ResizePanelWidth(UComponent panel,int width){
		IVECTOR2 size=panel.getPreferredSize();
		if(size.x<width)size.x=width;
		panel.setPreferredSize(size);
	}
}
