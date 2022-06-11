/******************************************************************************
;	�I�v�V�����_�C�A���O
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.option;

import sio29.ulib.umat.*;
import sio29.ulib.ureg.*;

public class OptionData{
	public String editor_filename;				//�O���G�f�B�^�t�@�C����
	public String editor_opt;					//�O���G�f�B�^�I�v�V����
	public String editor_opt_tag;				//�O���G�f�B�^�I�v�V�����^�O
	public boolean editor_opt_line;				//�O���G�f�B�^�s�w��
	public String filer_filename;				//�O���t�@�C���[�t�@�C����
	public String filer_opt;					//�O���t�@�C���[�I�v�V����
	public CVECTOR col_back;					//�w�i
	public CVECTOR col_normal;					//�ʏ�
	public CVECTOR col_select;					//�I��
	public CVECTOR col_tab;						//�^�u
	public CVECTOR col_ret;						//���s
	public CVECTOR col_eof;						//EOF
	public CVECTOR col_comment;					//�R�����g
	public CVECTOR col_str;						//������
	public CVECTOR col_macro;					//�}�N��
	public CVECTOR col_backlog;					//�o�b�N���O
	public boolean draw_tab;					//�^�u�\��
	public boolean draw_cr;						//���s�\��
	public boolean draw_eof;					//EOF�\��
	public boolean draw_zenspace;				//�S�p�󔒕\��
	public boolean draw_space;					//���p�󔒕\��
	public boolean draw_linenum;				//�s�ԍ��\��
	public boolean draw_ruler;					//���[���[�\��
	public boolean auto_tab_flg;				//�I�[�g�^�u�L��
	public boolean comline_flg;					//�����グ���R�}���h���C��
	public boolean doubleboot_flg;				//��d�����グ
	public int doubleboot_port;					//��d�����グ�p�|�[�g
	public boolean deletereg_flg;				//�I�������W�X�g���폜
	public String font_name;					//�t�H���g��
	public int font_size;						//�t�H���g�T�C�Y
	public String lookandfeel;					//���b�N���t�B�[��
	public int inner_filer;						//�t�@�C���_�C�A���O�̃t�@�C���t�B���^Index
	public boolean save_ask_flg;				//�ۑ����m�F
	public boolean noclose_filer;				//�t�@�C���[����Ȃ�
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
		font_name="�l�r �S�V�b�N";
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

