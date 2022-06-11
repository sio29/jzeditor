/******************************************************************************
;	�R�}���h���C��
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.net.InetAddress;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard; 
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.AbstractButton;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.AttributeSet;

import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.inputhistory.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;
import sio29.ulib.ureg.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.textcomponenttool.*;
import sio29.jzeditor.backends.j2d.panes.documenttab.*;
import sio29.jzeditor.backends.j2d.caret.*;
import sio29.jzeditor.backends.j2d.commandline.dos.*;
import sio29.jzeditor.backends.j2d.commandline.cygwin.*;
import sio29.jzeditor.backends.j2d.commandline.ssh.*;
import sio29.jzeditor.backends.j2d.menu.*;

public class CommandLine extends JComponent implements EditorTabBase{
	//�R�[���o�b�N
	public interface Callback{
		public CommandLineAttr GetCommandLineAttr();
		public String GetRegRootName();
		public void ExecTagJumpSub(UFile filename,int line);
		public void OpenDoc();
		public void ExecFD();
		public void ExecWZ(String[] opt);
		public UFileCurrentDrives GetUFileCurrentDrives();
		//public void FeedbackMenu();
		public void FeedbackTitle();
		public void FeedbackStatusBar();
		public void execCommand(String cmd);
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public ActionInfoMap getActionInfoMap();
		public RegOut getRegOut();
		public void setMenuFileHistory(UMenu menu);
	}
	//
	public final static int TYPE_DOS   =0;
	public final static int TYPE_SSH   =1;
	public final static int TYPE_CYGWIN=2;
	//
	private Callback callback;
	private static final long serialVersionUID = 8531245739641223373L;
//	private int g_textComoentType=0;			//JTextArea
	private int g_textComoentType=1;			//JTextPane
	private JComponent g_textArea;
	private boolean backlog_mode=false;
	private volatile boolean execcmd_flg=false;
	//���݂̃R�}���h���C���̐擪�ʒu
	private int now_comline_start=0;
	//���͗���
	private UInputHistory comline_historylist=new UInputHistory();
	private CommandLineAttr comline_attr;
	private JScrollPane scroll ;
	private int scroll_unit=16;
	private PrintLogTask log_task=null;	//	���O�o�̓^�X�N
	//========================================================
	// ������
	//========================================================
	// �R���X�g���N�^
	public CommandLine(int compo_type,Callback callback,int shell_type){
//type=0;//dos
//type=1;//ssh
//type=2;//cygwin
		g_textComoentType=compo_type;
		//if(compo_type==0){
		//}else{
		//}
		this.callback=callback;
		comline_attr=GetCommandLineAttr();
		//InitCurrentDir();
		InitTextArea(400, 500);
		InitActionMap();
		JComponent textArea=GetTextArea();
		TextComponentTool.setDragEnabled(textArea,true);	//�����h���b�O��L��
		SetDocumentFilter();
		initMainMenu();
		//
		InitShellExecCommandBase(shell_type);
		initStatusBar();
	}
	//========================================================
	//========================================================
	CommandLineAttr GetCommandLineAttr(){
		if(callback==null)return null;
		return callback.GetCommandLineAttr();
	}
	private void PrintStatusBar(String m){
//		if(callback==null)return;
//		callback.PrintStatusBar(m);
	}
	private String GetRegRootName(){
		if(callback==null)return null;
		return callback.GetRegRootName();
	}
	private void ExecTagJumpSub(UFile filename,int line){
		if(callback==null)return;
		callback.ExecTagJumpSub(filename,line);
	}
	private void OpenDoc(){
		if(callback==null)return;
		callback.OpenDoc();
	}
	public void ExecFD(){
		if(callback==null)return;
		callback.ExecFD();
	}
	public void ExecWZ(String[] opt){
		if(callback==null)return;
		callback.ExecWZ(opt);
	}
	private UFileCurrentDrives GetUFileCurrentDrives(){
		if(callback==null)return null;
		return callback.GetUFileCurrentDrives();
	}
	
	//========================================================
	//========================================================
	public JComponent GetTextArea(){
		return g_textArea;
	}
	public Component GetBaseComponent(){
		return scroll;
	}
	public String GetTabTitleStr(){
		return "�R�}���h���C��";
	}
	public String GetTitleStr(){
		String m="CommandLine";
		String __mm=TextComponentTool.getTextAreaTypeString(GetTextArea());
		m+=" ["+__mm+"]";
		return m;
	}
	public String GetStatusBarStr(){
		return "";
	}
	
	public void OnVzCut(){
		OnCut();
	}
	//�J�b�g
	public void OnCut(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode()){
		}else{
			TextComponentTool.cut(textArea);
		}
	}
	//�R�s�[
	public void OnCopy(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode()){
		}else{
			TextComponentTool.copy(textArea);
		}
	}
	//�y�[�X�g
	public void OnPaste(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode()){
		}else{
			TextComponentTool.paste(textArea);
		}
	}
	public void OnPrint(){
	}
	public void OnDocInfo(){
	}
	public void OnShortCut(){
	}
	//�I���\��?
	public boolean IsSelected(){
		JComponent textArea=GetTextArea();
		return TextComponentTool.isSelected(textArea);
	}
	//�S�đI��
	public void OnSelectAll(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode()){
		}else{
			TextComponentTool.selectAll(textArea);
		}
	}
	//�I���N���A
	public void OnSelectClear(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode()){
		}else{
			TextComponentTool.selectClear(textArea);
		}
	}
	public void OnChangeCode(String code){}
	public void OnSelectConv(String type){}
	public CharCode GetCharCode(){return null;}
	public void SetCharCode(CharCode n){}
	public String GetCRType(){return null;}
	public void SetCRType(String n){}
	public boolean GetEofFlg(){return false;}
	public void SetEofFlg(boolean n){}
	public void OnTagJump(){
		ExecTagJump();
	}
	public void OnJumpTop(){
	}
	public void OnJumpBottom(){
	}
	public void OnJumpLineTop(){
	}
	public void OnJumpLineBottom(){
	}
	//
	public void OnUndo(){}
	public void OnRedo(){}
	public boolean CanUndo(){return false;}
	public boolean CanRedo(){return false;}
	//
	public void OnSearch(){}
	public void OnReplace(){}
	public void OnSearchUp(){}
	public void OnSearchDown(){}
	public void OnRemember(){}
	//
	public void OnUpdate(){
	}
	public void OnUpdateAttr(){
		InitAttr();
		SetAttrBackLogMode();
		JComponent textArea=GetTextArea();
		textArea.updateUI();
	}
	public void OnSelectStart(){
	}
	public void OnFlipInsertMode(){
		JComponent textArea=GetTextArea();
		TextComponentTool.flipInsertMode(textArea);
	}
	public boolean CanSave(){
		return false;
	}
	public boolean CanPrint(){
		return false;
	}
	public void SetFilename(UFile n){
	}
	public UFile GetFilename(){
		return null;
	}
	public boolean OnOpen(UFile filename){
		return false;
	}
	public boolean OnOpenAdd(UFile filename){
		return false;
	}
	public boolean OnSave(UFile filename){
		return false;
	}
	public Component GetDropTarget(){
		JComponent textArea=GetTextArea();
		return textArea;
	}
	public boolean IsEmpty(){
		return true;
	}
	public boolean CanJumpLine(){
		return false;
	}
	public void SetJumpLine(int line){
	}
	public int GetNowLine(){
		return 0;
	}
	public boolean CanOpenOutter(){
		return false;
	}
	public boolean IsDarty(){
		return false;
	}
	public int GetTotalLineNum(){
		return 0;
	}
	public void OnJumpPairKakko(){
	}
	public boolean isOvertypeMode(){
		JComponent textArea=GetTextArea();
		return TextComponentTool.isOvertypeMode(textArea);
	}
	public void OnMacroRecStart(){}
	public void OnMacroRecEnd(){}
	public void OnMacroRecToggle(){}
	public void OnMacroPlay(){}
	public boolean CanMacroRecStart(){return false;}
	public boolean CanMacroRecEnd(){return false;}
	public boolean CanMacroPlay(){return false;}
	
	public void OnInsertCopyString(){}
	public void OnInsertSearchString(){}
	public void OnInsertInputString(){}
	public void OnInsertDeleteString(){}
	public void OnInsertDate(){}
	public void OnInsertFile(){}
	public void OnInsertFilename(){}
	public void OnInsertHorizon(){}
	public void OnInsertTable(){}
	
	//StatusBarPane.StateLabel[] status_labels;
	public void initStatusBar(){
		//status_labels=CommandLineStatusBar.getLabels();
	}
	public StatusBarPane.StateLabel[] getStatusBarLabels(){
		return CommandLineStatusBar.getLabels();
		//return status_labels;
	}
	private StatusBarMessage statusbar_message=new StatusBarMessage();
	void printStatus(String type,Object m){
		statusbar_message.put(type,m);
		if(callback==null)return;
		callback.FeedbackStatusBar();
	}
	public StatusBarMessage  getStatusBarMessage(){
		return statusbar_message;
	}
	
	public FunctionKeyPane.Function[][] getFunctionKeyFunctions(){
		return CommandLineFunctionKey.getFunctions();
	}
	public InputMap getFunctionKeyInputMap(){
		return null;
	}
	public ActionInfoMap getFunctionKeyActionInfoMap(){
		return null;
	}
	public ToolBarPane.ToolBarItem[][] getToolBarItems(){
		return CommandLineToolBar.getItems();
	}
	private MenuFeedback main_menu;				//
	public MenuFeedback getMenuFeedback(){
		return main_menu;
	}
	private void initMainMenu(){
		final CommandLine _this=this;
		CommandLineMenu.Callback menu_callback=new CommandLineMenu.Callback(){
			public void execCommand(String cmd){
				_this.execCommand(cmd);
			}
			public boolean GetUseToolBar(){
				return _this.GetUseToolBar();
			}
			public boolean GetUseStatusBar(){
				return _this.GetUseStatusBar();
			}
			public boolean GetUseFunctionKey(){
				return _this.GetUseFunctionKey();
			}
			public KeyStroke getCommandKeyStroke(String command){
				return _this.getCommandKeyStroke(command);
			}
			public char getCommandMnemonic(String command){
				return _this.getCommandMnemonic(command);
			}
			public String getCommandName(String command){
				return _this.getCommandName(command);
			}
			public void setMenuFileHistory(UMenu menu){
				_this.setMenuFileHistory(menu);
			}
		};
		main_menu=new CommandLineMenu(menu_callback);
	}
	public void execCommand(String cmd){
		if(callback==null)return;
		callback.execCommand(cmd);
	}
	public boolean GetUseToolBar(){
		if(callback==null)return false;
		return callback.GetUseToolBar();
	}
	public boolean GetUseStatusBar(){
		if(callback==null)return false;
		return callback.GetUseStatusBar();
	}
	public boolean GetUseFunctionKey(){
		if(callback==null)return false;
		return callback.GetUseFunctionKey();
	}
	public KeyStroke getCommandKeyStroke(String command){
		JComponent textArea=GetTextArea();
		InputMap inputmap=textArea.getInputMap();
		return ActionTool.getKeyStrokeFromCommand(inputmap,command);
	}
	public char getCommandMnemonic(String command){
		return 0;
	}
	public ActionInfoMap getActionInfoMap(){
		return callback.getActionInfoMap();
	}
	public String getCommandName(String command){
		if(callback==null)return null;
		ActionInfoMap actioninfomap=getActionInfoMap();
		if(actioninfomap==null)return null;
		ActionInfo info=actioninfomap.get(command);
		if(info==null)return null;
		return info.getMenuName();
	}
	public void setMenuFileHistory(UMenu menu){
		callback.setMenuFileHistory(menu);
	}
	
	//====================================================
	//
	//====================================================
	//Attr������
	void InitAttr(){
		JComponent textArea=GetTextArea();
		TextComponentTool.setCharacterAttributes(textArea,comline_attr.attr_normal);//,false
		TextComponentTool.setForeground(textArea,Color.WHITE);
		textArea.setBackground(ConvAWT.CVEC2Color(comline_attr.GetBackCol(IsBackLogMode())));
		TextComponentTool.setCaretColor(textArea,ConvAWT.CVEC2Color(comline_attr.cursor_col));
		TextComponentTool.setSelectionColor(textArea,ConvAWT.CVEC2Color(comline_attr.select_col));
	}
	// �e�L�X�g�G���A���쐬����
	void InitTextArea(int width, int height) {
//		g_textArea=TextComponentTool.createTextComponent(g_textComoentType);
		g_textArea=TextComponentTool.createNormalTextComponent(g_textComoentType);
		if(g_textArea==null)return;
		add(g_textArea);
		
		//TextComponentTool.printInputMap(g_textArea.getInputMap());
		
		
		JComponent textArea=GetTextArea();
		InitAttr();
		//
		SetOrgActionMap();
		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollBar sb=scroll.getVerticalScrollBar();
		sb.setUnitIncrement(scroll_unit);
		//
		InitCaret();
		//
		StartTextArea();
		//
		textArea.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
				if(callback!=null){
					callback.FeedbackTitle();
				}
			}
			public void focusLost(FocusEvent e){}
		});
	}
	//
	void StartTextArea(){
		JComponent textArea=GetTextArea();
		PrintCommandLine();
		//AddTextBottom("\n",comline_attr.attr_comline);	//�����s���Ȃ��Ƃ�������������ς���Ȃ�
		textArea.grabFocus();
	}
	public void SetFocus(){
		JComponent textArea=GetTextArea();
		textArea.grabFocus();
		InitCaret();
	}
	void PrintStatusBarNowCaret(){
		JComponent textArea=GetTextArea();
		int pos=TextComponentTool.getCaretPosition(textArea);
		int line_num=TextComponentTool.getCaretLine(textArea);
		int line_start=TextComponentTool.getLineStart(textArea,pos);
		int line_pos=pos-line_start+1;
		
		PrintStatusBar("line("+line_num+"),pos("+line_pos+"),offset("+pos+")");
	}
	//========================================================
	//========================================================
	public static void SetButtonActionFromActionMap(UMenuItem menu_item,ActionMap am,String name){
		menu_item.setActionCommand(name);
		menu_item.addActionListener(new UActionListener(){
			public void actionPerformed(UActionEvent ev){
				Action act=am.get(name);
				act.actionPerformed(UActionEventJ2D.getActionEvent(ev));
			}
		});
	}
	
	void InitPopupMenu(){
		UPopupMenu menu;
		menu = UPopupMenuBuilder.create();
		//
		UMenuItem menu_paste = UMenuItemBuilder.create("Paste(P)");
		menu_paste.setMnemonic('P');
		SetButtonActionFromActionMap(menu_paste,getActionMap(),DefaultEditorKit.pasteAction);
		menu.add(menu_paste);
		//
		UMenuItem menu_cut = UMenuItemBuilder.create("Cut(X)");
		menu_cut.setMnemonic('X');
		SetButtonActionFromActionMap(menu_cut,getActionMap(),DefaultEditorKit.cutAction);
		menu.add(menu_cut);
		//
		UMenuItem menu_copy = UMenuItemBuilder.create("Copy(C)");
		menu_copy.setMnemonic('C');
		SetButtonActionFromActionMap(menu_copy,getActionMap(),DefaultEditorKit.copyAction);
		menu.add(menu_copy);
		//
		JComponent textArea=GetTextArea();
		textArea.setComponentPopupMenu(UPopupMenuJ2D.getJPopupMenu(menu));
	}
	//========================================================
	//========================================================
	static void PrintActionList(JComponent textArea){
		Action[] acts=TextComponentTool.getActions(textArea);
		if(acts==null)return;
		for(int i=0;i<acts.length;i++){
			Action act=acts[i];
			String name=(String)act.getValue(Action.NAME);
			System.out.println("act["+i+"]:"+name);
		}
	}
	//========================================================
	//========================================================
	private DocumentFilter MakeDocumentFilter(){
		//�폜�R�}���h�t�b�N
		return new DocumentFilter(){
			//�폜
			@Override
			public void remove(DocumentFilter.FilterBypass fb, int offset, int length){
				//�I�t�Z�b�g�ƒ����v�Z
				RemoveOffsetLength offlen=CalcRemoveOffsetLength(offset,length);
				offset=offlen.offset;
				length=offlen.length;
				//�폜���s
				try{
					fb.remove(offset,length);
					SetComlineAttr();
				}catch(Exception ex){}
			}
			//�}��
			@Override
			public void insertString(DocumentFilter.FilterBypass fb,int offset,String text,AttributeSet attr){
				replace(fb,offset,0,text,attr);
			}
			//�u������
			@Override
			public void replace(DocumentFilter.FilterBypass fb,int offset,int length,String text,AttributeSet attrs){
				JComponent textArea=GetTextArea();
				//�I�t�Z�b�g�ƒ����v�Z
				RemoveOffsetLength offlen=CalcRemoveOffsetLength(offset,length);
				offset=offlen.offset;
				length=offlen.length;
				//
				if(!isCommandLineInput()){
				//���s�����o�b�N���O��
				}else{
					int cr=text.indexOf("\n");
					if(cr>=0){
						//System.out.println("���s����!!");
					}
				}
				//�u���������s
				TextComponentTool.replace(fb,offset,length,text,attrs,isOvertypeMode(),GetTabSize());
			}
		};
	}
	void SetDocumentFilter(){
		JComponent textArea=GetTextArea();
		DocumentFilter filter=MakeDocumentFilter();
		TextComponentTool.setDocumentFilter(textArea,filter);
	}
	void ReleaseDocumentFilter(){
		JComponent textArea=GetTextArea();
		TextComponentTool.setDocumentFilter(textArea,null);
	}
	static class RemoveOffsetLength{
		public int offset;
		public int length;
		public RemoveOffsetLength(int offset,int length){
			this.offset=offset;
			this.length=length;
		}
		public void normalize(int start){
			if(offset<start){
				length-=(start-offset);
				if(length<0)length=0;
				offset=start;
			}
		}
	}
	public boolean isCommandLineInput(){
		if(GetExecCmdFlg() || IsBackLogMode() || IsPrintCommnadLine()){
			return false;
		}else{
			return true;
		}
	}
	
	public RemoveOffsetLength CalcRemoveOffsetLength(int offset, int length){
		RemoveOffsetLength offlen=new RemoveOffsetLength(offset,length);
		if(!isCommandLineInput()){
		//���s�����o�b�N���O��
		}else{
		//�R�}���h���C��
			int start=GetComLineStart();
			offlen.normalize(start);
		}
		return offlen;
	}
	public int GetTabSize(){
		return comline_attr.tab_size;
	}
	//========================================================
	//========================================================
	void SetOrgActionMap(){
		JComponent textArea=GetTextArea();
		ActionMap act_map=textArea.getActionMap();
		//���͗���
		new Act_upAction(act_map);
		new Act_downAction(act_map);
		//�P��\��
		new Act_insertTabAction(act_map);
		//�y�[�X�g
		new Act_paste(act_map);
	}
	//==============================
	//�A�N�V����:��ړ�(����)
	class Act_upAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_upAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.upAction);
		}
		public void actionPerformed(ActionEvent e){
			if(GetExecCmdFlg())return;
			if(!IsBackLogMode()){
				SetComlineHistory(InputHistory.PREV);
			}else{
				OldActionPerformed(e);
			}
		}
	}
	//�A�N�V����:���ړ�(����)
	class Act_downAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_downAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.downAction);
		}
		public void actionPerformed(ActionEvent e){
			if(GetExecCmdFlg())return;
			if(!IsBackLogMode()){
				SetComlineHistory(InputHistory.NEXT);
			}else{
				OldActionPerformed(e);
			}
		}
	}
	//==============================
	//�A�N�V����:�^�u(���͕��)
	class Act_insertTabAction extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_insertTabAction(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.insertTabAction);
		}
		public void actionPerformed(ActionEvent e){
			if(GetExecCmdFlg())return;
			//PrintActName();
//			if(!CheckSelctionBounds())return;
//			OldActionPerformed(e);
		}
	}
	//==============================
	//�A�N�V����:�y�[�X�g
	class Act_paste extends AbstractActionExt {
		private static final long serialVersionUID = 8531245739641223373L;
		public Act_paste(ActionMap act_map){
			GetOldAct(act_map,DefaultEditorKit.pasteAction);
		}
		public void actionPerformed(ActionEvent e){
			JComponent textArea=GetTextArea();
			String m=getClipboardString();
			String[] mm=m.split("\n");
			m=mm[0];
			TextComponentTool.insertText(textArea,m,comline_attr.attr_comline);
		}
	}
	public static String getClipboardString() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		try {
			return (String) clip.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	//========================================================
	//�I��͈͂̃`�F�b�N
	//========================================================
	public int GetComLineStart(){
		return now_comline_start;
	}
	public void SetComLineStart(int n){
		now_comline_start=n;
	}
	public boolean CheckSelctionBounds(){
		JComponent textArea=GetTextArea();
		return TextComponentTool.checkSelctionBounds(textArea,GetComLineStart());
	}
	public boolean IsBackLogMode(){
		return backlog_mode;
	}
	public void FlipBackLogMode(){
		backlog_mode=!backlog_mode;
	}
	//========================================================
	//========================================================
	public void ReadReg(){
		InitComlineHistory();
	}
	public void OutReg(){
		OutRegComlineHistory();
	}
	//========================================================
	//����\��
	//========================================================
	//�R�}���h���C���̃N���A
	public void ClearComline(){
		JComponent textArea=GetTextArea();
		int len=TextComponentTool.getDocumentLength(textArea);
		int pos=GetComLineStart();
		TextComponentTool.removeText(textArea,pos,len-pos);
	}
	String GetComlineRegNodeName(){
		return GetRegRootName()+"/ComlineHistory";
	}
	public UInputHistory GetComlineHistory(){
		return comline_historylist;
	}
	RegOut getRegOut(){
		return callback.getRegOut();
	}
	void InitComlineHistory(){
		RegOut regout=getRegOut();
		comline_historylist.ReadReg(regout,GetComlineRegNodeName());
	}
	void OutRegComlineHistory(){
		RegOut regout=getRegOut();
		comline_historylist.OutReg(regout,GetComlineRegNodeName());
	}
	//type=0 �オ�����ꂽ�ꍇ
	//type=1 ���������ꂽ�ꍇ
	public void SetComlineHistory(int type){
		//�R�}���h�̊l��
		CharSequence line_m=GetComlineString();
		String m=comline_historylist.GetPrevNextWithFirstCheck(type,line_m.toString());
		if(m==null)m="";
		//�R�}���h���C���̃N���A
		ClearComline();
		//�R�}���h���C���ɕ\��
		AddTextBottom(m,comline_attr.attr_comline);
		//���A�g�����r���[�g���ς���Ă��܂��̂�
		SetComlineAttr();
	}
	//========================================================
	//�A�g�����r���[�g�̍쐬
	//========================================================
	//�o�b�N���O���[�h�̐؂�ւ��\��
	void SetAttrBackLogMode(){
		JComponent textArea=GetTextArea();
		textArea.setBackground(ConvAWT.CVEC2Color(comline_attr.GetBackCol(IsBackLogMode())));
		//
		int len=TextComponentTool.getDocumentLength(textArea);
		AttributeSet back_attr=comline_attr.GetBackColAttr(IsBackLogMode());
		TextComponentTool.setCharacterAttributes(textArea,0,len,back_attr);
	}
	//============================================
	//�e�L�X�g�̍Ō�ɕ�����̒ǉ�
	//============================================
	boolean AddTextBottom(String m,AttributeSet attr){
		JComponent textArea=GetTextArea();
		return TextComponentTool.insertTextBottom(textArea,m,attr);
	}
	//�e�L�X�g�̍Ō�ɕ�����̒ǉ�(���Ōオ�s���łȂ���Ή��s)
	boolean AddTextBottomWithLineTop(String m,AttributeSet attr){
		JComponent textArea=GetTextArea();
		try{
			int bottom=TextComponentTool.getDocumentLength(textArea);
			if(bottom>0){
				CharSequence __m=TextComponentTool.getText(textArea,bottom-1,1);
				char bottom_c=__m.charAt(0);
				//�Ōオ���s�łȂ����
				if(bottom_c!=0x0a){
					TextComponentTool.insertTextBottom(textArea,"\n",comline_attr.attr_path);
				}
			}
		}catch(Exception ex) {
		}
		return AddTextBottom(m,attr);
	}
	//============================================
	//�f�B���N�g���s�̕\��
	//============================================
	public void PrintCurrentDir(){
		String current_dir=GetCurrentDir();
		String m=current_dir+"\n";
		AddTextBottom(m,comline_attr.attr_normal);
	}
	//============================================
	//�����R�}���h
	//============================================
	//cls�̎��s
	public void ExecCls(){
		JComponent textArea=GetTextArea();
		int len=TextComponentTool.getDocumentLength(textArea);
		TextComponentTool.removeText(textArea,0,len);
	}
	//exit
	public void ExecExit(){
		System.out.println("Exit");
		System.exit(1);
	}
	//cd
	public void ExecChnageDir(String m){
		if(m==null){
			PrintCurrentDir();
			return;
		}
		if(m.length()==0){
			PrintCurrentDir();
			return;
		}
		//
		try{
			UFileCurrentDrives drive_currents=GetUFileCurrentDrives();
			String mm=drive_currents.makeAbsoluteCurrentDirPath(m);
//System.out.println("ExecChnageDir:m("+m+"),mm("+mm+")");
			if(mm==null){
				PrintErrorText("("+m+"):�w��̃f�B���N�g���͑��݂��܂���\n");
				return;
			}
			//�f�B���N�g���`�F���W
			SetCurrentDir(mm);
		}catch(Exception ex){
//System.out.println("ExecChnageDir Error!!:("+m+")");
		}
	}
	//c: �h���C�u�`�F���W
	public void ExecChnageDrive(String m){
		UFileCurrentDrives drive_currents=GetUFileCurrentDrives();
		String n=drive_currents.getDrivePathFromDriveChangeString(m);
		if(n==null){
			PrintCurrentDir();
			return;
		}
		SetCurrentDrive(n);
	}
	//============================================
	//�R�}���h
	//============================================
	//�R�}���h���͂��ꂽ
	public void ExecEnterCommand(){
System.out.println("ExecEnterCommand");
		JComponent textArea=GetTextArea();
		if(IsBackLogMode())return;
		if(GetExecCmdFlg())return;
		int pos=TextComponentTool.getCaretPosition(textArea);
		int len=TextComponentTool.getDocumentLength(textArea);
System.out.println(String.format("pos(%d),len(%d)",pos,len));
		if(pos<=len){
			//
			comline_historylist.ClearFirstInput();
			//�R�}���h������̊l��
			CharSequence cmd2=GetComlineString();
System.out.println(String.format("cmd2(%s)",cmd2));
			if(cmd2!=null){
				String cmd=cmd2.toString();
				//�����ɒǉ�
				comline_historylist.Add(cmd);
				//�R�}���h�̎��s
				ExecDosCommandWait_Sub(cmd);
			}
			if(!GetExecCmdFlg()){
			//�R�}���h���C���̕\��
				PrintCommandLine();
			}
		}
	}
	//�o�b�N���O���[�h��
	public void FlipBacklogMode(){
		if(IsBackLogMode()){
			ExecChangeCommandLineMode();
		}else{
			ExecChangeBacklogMode();
		}
	}
	//�o�b�N���O���[�h��
	public void ExecChangeBacklogMode(){
		JComponent textArea=GetTextArea();
		if(IsBackLogMode())return;
		if(GetExecCmdFlg())return;
		//Shift+ESC
		FlipBackLogMode();
		SetAttrBackLogMode();
		TextComponentTool.setEditable(textArea,false);
		textArea.repaint();
	}
	//�R�}���h���C�����[�h��
	public void ExecChangeCommandLineMode(){
		JComponent textArea=GetTextArea();
		if(!IsBackLogMode())return;
		if(GetExecCmdFlg())return;
		FlipBackLogMode();
		SetAttrBackLogMode();
		//���A����ꍇ
		NormalizeComlineStartPos();
		SetComlineAttr();
		TextComponentTool.setEditable(textArea,true);
		textArea.repaint();
	}
	
	//�^�O�W�����v�p�̃t�@�C�����ƍs�ԍ�
	public FilenameLine GetTagJumpFilenameLine(){
		JComponent textArea=GetTextArea();
		CharSequence line_m=TextComponentTool.getCaretLineText(textArea);
		if(line_m==null)return null;
		return FilenameLine.GetFilenameLineFromStr(line_m.toString(),GetCurrentDir());
	}
	//�^�O�W�����v��
	public void ExecTagJump(){
		if(!IsBackLogMode())return;
		if(GetExecCmdFlg())return;
		FilenameLine fnl=GetTagJumpFilenameLine();
		if(fnl==null)return;
		ExecTagJumpSub(fnl.getFilename(),fnl.getLine());
	}
	//============================================
	//============================================
	public void F10Dummy(){
	}
	void SetExecCmdFlg(boolean n){
		execcmd_flg=n;
	}
	boolean GetExecCmdFlg(){
		return execcmd_flg;
	}
	
	//============================================
	//	�R�}���h���C�����s
	//============================================
	public interface InnerCommandChecker{
		public InnerCommand GetInnerCommand(String cmd);
	}
	
	private ShellExecCommandBase g_shellexec_base=null;
	private InnerCommandChecker g_innerchecker=null;
	public ShellExecCommandBase getShellExecCommandBase(){
		return g_shellexec_base;
	}
	//DOS
	public void InitShellExecCommandBase_Dos(){
		g_shellexec_base=new DosExec();
		g_innerchecker=new InnerCommandChecker(){
			public InnerCommand GetInnerCommand(String cmd){
				return InnerCommandTool.GetInnerCommand(CommandLineInnerCommand.getInnerCommands(),cmd);
			}
		};
	}
	//Cygwin
	public void InitShellExecCommandBase_Cygwin(){
		g_shellexec_base=new CygwinExec();
		g_innerchecker=new InnerCommandChecker(){
			public InnerCommand GetInnerCommand(String cmd){
				return null;
			}
		};
	}
	//SSH
	public void InitShellExecCommandBase_SSH(){
		g_shellexec_base=new SSHExec(SSHExec.g_def_param);
		g_innerchecker=new InnerCommandChecker(){
			public InnerCommand GetInnerCommand(String cmd){
				return null;
			}
		};
	}
	public void InitShellExecCommandBase(int type){
		switch(type){
			case 0:InitShellExecCommandBase_Dos();break;
			case 1:InitShellExecCommandBase_SSH();break;
			case 2:InitShellExecCommandBase_Cygwin();break;
		}
	}
	//============================================
	//	�R�}���h���C�����s
	//============================================
	//�҂�
	public boolean ExecDosCommandWait_Sub(String cmd){
		final CommandLine p_commandline=this;
		JComponent textArea=GetTextArea();
		if(cmd==null)return false;
		if(cmd.length()==0)return false;
		//
		try{
			//�����R�}���h���`�F�b�N����
			InnerCommand inner_cmd=g_innerchecker.GetInnerCommand(cmd);
			if(inner_cmd!=null){
			//�����R�}���h���s
				String cmd_name=inner_cmd.name;
				String opt;
				if(inner_cmd.new_opt!=null){
					opt=inner_cmd.new_opt;
				}else{
					opt=cmd.substring(inner_cmd.name.length());
					opt=TextTool.GetNoSpaceTopStr(opt);
				}
				SetExecCmdFlg(true);
				InnerCommandTool.ExecInnerCommandSub(inner_cmd,opt,this);
				SetExecCmdFlg(false);
				return true;
			}else{
			//�O���R�}���h��
				AddTextBottom("\n",comline_attr.attr_normal);
				final String current_dir=GetCurrentDir();
				final String[] cmds=new String[1];
				cmds[0]=cmd;
				//
				SetExecCmdFlg(true);
				log_task=new PrintLogTask(cmds,current_dir,new PrintLogTask.Callback(){
					public InputMap getInputMap(){
						JComponent textArea=p_commandline.GetTextArea();
						return textArea.getInputMap();
					}
					public void SetExecCmdFlg(boolean n){
						p_commandline.SetExecCmdFlg(n);
					}
					public void AddTextBottom(String m){
						p_commandline.AddTextBottom(m,p_commandline.GetCommandLineAttr().attr_normal);
					}
					public void SetKeyListener(KeyListener listener){
						JComponent textArea=p_commandline.GetTextArea();
						textArea.addKeyListener(listener);
					}
					public void RemoveKeyListener(KeyListener listener){
						JComponent textArea=p_commandline.GetTextArea();
						textArea.removeKeyListener(listener);
					}
					public void SetCaretPosBottom(){
						int new_bottom=TextComponentTool.getDocumentLength(textArea);
						JComponent textArea=p_commandline.GetTextArea();
						TextComponentTool.setCaretPosition(textArea,new_bottom);
					}
					public void PrintCommandLine(){
						p_commandline.PrintCommandLine();
					}
					public String ExecCommandWait(String[] cmds,String current_dir,DosExecOutputListener output_listener,DosExecInputListener input_listener,DosExecAboutListener about_listener) throws Exception {
//System.out.println("@@@ "+cmds[0]);
						ShellExecCommandBase shellexec=getShellExecCommandBase();
						return shellexec.ExecDosCommandWait(cmds,current_dir,output_listener,input_listener,about_listener);
					}
				});
				log_task.execute();
				return true;
			}
		}catch(Exception ex) {
			PrintErrorCommand(cmd);
			return false;
		}
	}
	//============================================
	//���݂̃R�}���h���C���̕�����𓾂�
	//============================================
	public CharSequence GetComlineString(){
		JComponent textArea=GetTextArea();
		try{
			//�R�}���h���C���s�̕�������w�b�_�[���݂Ŋl������
			CharSequence line_m=TextComponentTool.getCaretLineText(textArea);
			//�w�b�_�[�����̊l��
			String header_m=GetCommandLineHeaderStr();
			//�w�b�_�[��������������������Ԃ�
			//return line_m.substring(header_m.length());
			return line_m.subSequence(header_m.length(),line_m.length());
		}catch(Exception ex){
			ex.printStackTrace();
			//System.out.println("�R�}���h������̊l���Ɏ��s!!");
			return null;
		}
	}
	//============================================
	//	�J�����g�f�B���N�g���A�h���C�u�̐ݒ�l��
	//============================================
	public String GetCurrentDir(){
		UFileCurrentDrives drive_currents=GetUFileCurrentDrives();
		return drive_currents.getCurrentDirPath();
	}
	void SetCurrentDir(String m){
		UFileCurrentDrives drive_currents=GetUFileCurrentDrives();
		drive_currents.setCurrentDir(m);
	}
	void SetCurrentDrive(String n){
		UFileCurrentDrives drive_currents=GetUFileCurrentDrives();
		if(!drive_currents.setCurrentDrive(n)){
			PrintErrorText("["+n+"]�͑��݂��܂���!!\n");
		}
	}
	//============================================
	//	OS�^�C�v�𓾂�R�}���h���C���̕\��
	//============================================
	public static String getOSName(){
		String os_name=System.getProperty("os.name");
		if(os_name!=null){
			os_name=os_name.toLowerCase();
			if(os_name.startsWith("windows")){
				return "windows";
			}else if(os_name.startsWith("linux")){
				return "linux";
			}else if(os_name.startsWith("mac")){
				return "mac";
			}else if(os_name.startsWith("sunos")){
				return "sunos";
			}
		}
		return "";
	}
	//============================================
	//	�R�}���h���C���̕\��
	//============================================
	//�R�}���h���C���w�b�_�[����
	//boolean show_user_flg=false;
	boolean show_user_flg=true;
	boolean show_host_flg=true;
	public String GetCommandLineHeaderStr(){
		String m="";
		String last_sepa=">";
		String os_name=getOSName();
		if(os_name.equals("windows")){
			show_user_flg=false;
			show_host_flg=false;
			last_sepa=">";
		}else if(os_name.equals("linux")){
			last_sepa=" $";
		}else if(os_name.equals("mac")){
			last_sepa=" $";
		}else if(os_name.equals("sunos")){
			last_sepa=" $";
		}
		//
		if(show_user_flg){
			m+=System.getProperty("user.name");
		}
		if(show_host_flg){
			try{
				m+="@"+InetAddress.getLocalHost().getHostName();
			}catch(Exception ex){}
		}
		if(show_user_flg || show_host_flg){
			m+=" ";
		}
		String current_dir=GetCurrentDir();
		m+=current_dir+last_sepa;
		return m;
	}
	boolean print_command_line_flg=false;
	public boolean IsPrintCommnadLine(){
		return print_command_line_flg;
	}
	//�R�}���h���C���̕\��
	public void PrintCommandLine(){
		JComponent textArea=GetTextArea();
		if(GetExecCmdFlg())return;
		print_command_line_flg=true;
		String comline_str=GetCommandLineHeaderStr();
		AddTextBottomWithLineTop(comline_str,comline_attr.attr_path);
		//
		int new_bottom=TextComponentTool.getDocumentLength(textArea);
		SetComLineStart(new_bottom);			//�R�}���h���C���̈ʒu�ێ�
		TextComponentTool.setCaretPosition(textArea,new_bottom);
		//
		SetComlineAttr();
		print_command_line_flg=false;
	}
	//�A�g�����r���[�g�ݒ�
	public void SetComlineAttr(){
		JComponent textArea=GetTextArea();
		TextComponentTool.setCharacterAttributes(textArea,comline_attr.attr_comline);//,false);
	}
	//�G���[�R�}���h�̕\��
	public static String command_err_m="(%s)�́A�����R�}���h�܂��͊O���R�}���h�A\n����\�ȃv���O�����܂��̓o�b�`�t�@�C���Ƃ��ĔF������Ă��܂���B\n";
	public void PrintErrorCommand(String cmd){
		String err_m=String.format(command_err_m,cmd);
		//AddTextBottom(err_m,comline_attr.attr_error);
		PrintErrorText(err_m);
	}
	//�G���[�e�L�X�g�̕\��
	public void PrintErrorText(String m){
		AddTextBottom(m,comline_attr.attr_error);
	}
	//============================================
	//�L�����b�g���w��̈ʒu�Ɉړ��ł��邩
	//============================================
	public int CalcCaretPos(int p){
		JComponent textArea=GetTextArea();
		if(IsBackLogMode()){
			//�o�b�N���O���͂��̂܂�
			return p;
		}
		if(GetExecCmdFlg()){
			//�R�}���h���s��
			return p;
		}
		
		//�R�}���h���C����
		try{
			int new_p=p;
			int[] bounds=TextComponentTool.getLineBoundsFromPos(textArea,GetComLineStart());
			if(bounds!=null){
				int line_start=bounds[0];
				int line_end  =bounds[1];
//				int bottom=line_end-1;
				int bottom=line_end;
				//
				if(new_p<GetComLineStart()){
					new_p=GetComLineStart();
				}
				if(new_p>bottom){
					new_p=bottom;
				}
			}
			return new_p;
		}catch(Exception ex){
			return p;
		}
	}
	//�R�}���h���C���̐擪�ʒu�̏C��
	public boolean NormalizeComlineStartPos(){
		JComponent textArea=GetTextArea();
		if(IsBackLogMode())return false;		//�o�b�N���O���[�h
		if(GetExecCmdFlg())return false;		//�R�}���h���s��
		int len=TextComponentTool.getDocumentLength(textArea);
		//
		try{
			int[] bounds=TextComponentTool.getLineBoundsFromPos(textArea,GetComLineStart());
			if(bounds!=null){
				int line_start=bounds[0];
				int line_end  =bounds[1];
				CharSequence last_m=TextComponentTool.getText(textArea,line_start,line_end-line_start);
				String comline_m=GetCommandLineHeaderStr();
				if(last_m.toString().startsWith(comline_m)){
					SetComLineStart(line_start+comline_m.length());
					SetComlineAttr();
					//
					NormalizeCaretPos();
					return true;
				}
				PrintCommandLine();
			}
		}catch(Exception ex){
			PrintCommandLine();
		}
		return true;
	}
	//�L�����b�g�ʒu�̕␳
	public void NormalizeCaretPos(){
		JComponent textArea=GetTextArea();
		int p=TextComponentTool.getCaretPosition(textArea);
		int new_p=CalcCaretPos(p);
		if(p!=new_p){
			TextComponentTool.setCaretPosition(textArea,new_p);
		}
	}
	//============================================
	//============================================
	private ActionTbl[] g_act_tbl={
		new ActionTbl(CommandLineAction.ChangeBackLog	,this,"FlipBacklogMode"		),
		new ActionTbl(CommandLineAction.OpenFile		,this,"OpenDoc"				),
		new ActionTbl(CommandLineAction.TagJump			,this,"ExecTagJump"			),
		new ActionTbl(CommandLineAction.EnterCommand	,this,"ExecEnterCommand"	),
		new ActionTbl(CommandLineAction.F10Dummy		,this,"F10Dummy"			),
	};
	private InputTbl[] g_input_tbl={
		new InputTbl(KeyEvent.VK_ESCAPE	,KeyEvent.SHIFT_DOWN_MASK	,CommandLineAction.ChangeBackLog),
		new InputTbl(KeyEvent.VK_ESCAPE	,0							,CommandLineAction.OpenFile),
		new InputTbl(KeyEvent.VK_F10	,KeyEvent.SHIFT_DOWN_MASK	,CommandLineAction.TagJump),
		new InputTbl(KeyEvent.VK_ENTER	,0							,CommandLineAction.EnterCommand),
		new InputTbl(KeyEvent.VK_F10	,0							,CommandLineAction.F10Dummy),
	};
	void InitActionMap(){
		JComponent textArea=GetTextArea();
		ActionTbl.SetActionTbl(textArea,g_act_tbl);
		ActionTool.SetInputTbl(textArea,g_input_tbl);
	}
	//============================================
	//�L�����b�g
	//============================================
	//�L�����b�g�̏�����
	public void InitCaret() {
		JComponent textArea=GetTextArea();
		TextComponentTool.initCaretShape(textArea);
		TextComponentTool.setCaretFilter(textArea,new CaretFilter(){
//			public int convertCaretPos(Caret caret,int n){
			public int convertCaretPos(int n){
				return CalcCaretPos(n);
			}
		});
	}
}
