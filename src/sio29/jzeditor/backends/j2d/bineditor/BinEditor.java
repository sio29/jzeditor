/******************************************************************************
;	バイナリエディタ
******************************************************************************/
package sio29.jzeditor.backends.j2d.bineditor;

import java.io.File;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import javax.swing.JScrollBar;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.event.MouseInputListener;

import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;
import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.toolbar.*;

import sio29.jzeditor.backends.j2d.texttool.*;
import sio29.jzeditor.backends.j2d.panes.documenttab.*;
import sio29.jzeditor.backends.j2d.dialogs.shortcut.*;
import sio29.jzeditor.backends.j2d.menu.*;

public class BinEditor extends JComponent implements EditorTabBase , MouseInputListener, MouseWheelListener,ComponentListener {
//public class BinEditor extends Container implements EditorTabBase , MouseInputListener, MouseWheelListener,ComponentListener {
	public interface Callback{
		//public void FeedbackMenu();
		public void FeedbackTitle();
		public void FeedbackStatusBar();
		public void execCommand(String cmd);
		public boolean GetUseToolBar();
		public boolean GetUseStatusBar();
		public boolean GetUseFunctionKey();
		public ActionInfoMap getActionInfoMap();
		public void setMenuFileHistory(UMenu menu);
	}
	//
	private final static int XTOP=64;
	private final static int YTOP=16;
	private final static int CHAR_W=18;
	private final static int CHAR_H=16;
	private final static int DUMPX=XTOP+18*CHAR_W;
	private final static int LINE_NUM=16;					//一行に表示できる数
	//
	private Callback callback;
	private BinData bindata=new BinData();
	private byte[] copydata=null;
	private int select_pos=0;
	private int caret_pos=0;
	private int input_cnt=0;
	private int  input_tmp_pos=0;
	private byte input_tmp_num=0;
	private byte input_tmp_old=0;
	private boolean input_tmp_insert_flg;
	private int scroll_top=0;
	private boolean insert_flg=false;
	private UFile filename=null;
	private JScrollBar scrollbar ;
	private Container frame;
	//
	public BinEditor(){
		init();
	}
	public BinEditor(Callback callback){
		this.callback=callback;
		init();
	}
	private void init(){
		setPreferredSize(new Dimension(640,480));
		setSize(new Dimension(640,480));
		setVisible(true);
		//
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);
		//
		scrollbar=new JScrollBar();
		scrollbar.addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int n=e.getValue();
				if(n<0)n=0;
				scroll_top=n*16;
				repaint();
			}
		});
		//フレーム
		frame=new Container ();
		frame.setLayout(new BorderLayout());
		frame.add("Center",this);
		frame.add("East",scrollbar);
		ClearDocument();
		InitActionMap();
		initMainMenu();
		InitUndo();
		initStatusBar();
		
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
//System.out.println("BinEditor.focus");
				if(callback!=null){
					//callback.FeedbackMenu();
					callback.FeedbackTitle();
				}
			}
			public void focusLost(FocusEvent e){}
		});
		
	}
	//========================================================
	//========================================================
	//ステータスバーに表示
	private void PrintStatusBar(String m){
//		if(callback!=null){
//			callback.PrintStatusBar(m);
//		}
	}
	
	//========================================================
	//========================================================
	public Component GetBaseComponent(){
		return frame;
	}
	public boolean IsEmpty(){
		if(bindata==null)return true;
		if(bindata.getSize()==0)return true;
		return false;
	}
	public boolean OnOpen(UFile filename){
		bindata.setBytes(filename.bload_malloc());
		if(bindata==null)return false;
		return true;
	}
	public boolean OnOpenAdd(UFile filename){
		return false;
	}
	public boolean OnSave(UFile filename){
		if(bindata==null)return false;
		try{
			return filename.bsave(bindata.getBytes());
		}catch(Exception ex){
			return false;
		}
	}
	//ドキュメントのクリア
	void ClearDocument(){
		filename=null;
		bindata=new BinData();
		caret_pos=0;
		select_pos=0;
		scroll_top=0;
		CalcWindowSize();
	}
	//ダミーデータの作成
	public void SetDummyDocument(){
		byte[] data=new byte[256*10];
		for(int i=0;i<data.length;i++){
			data[i]=(byte)((int)(Math.random()*255.0f) & 0xff);
		}
		//SetDocument("ダミー",data);
		SetDocument(null,data);
	}
	//ドキュメントの読み込み
	boolean LoadData(UFile _filename){
		try{
			byte[] data=_filename.bload_malloc();
			if(data!=null){
				SetDocument(_filename,data);
				return true;
			}
			return false;
		}catch(Exception ex){
			return false;
		}
	}
	//ドキュメントの保存
	boolean SaveData(UFile _filename){
		try{
			return filename.bsave(bindata.getBytes());
		}catch(Exception ex){
			return false;
		}
	}
	//ドキュメント設定
	public void SetDocument(UFile _filename,byte[] data){
		filename=_filename;
		bindata.setBytes(data);
		caret_pos=0;
		select_pos=0;
		scroll_top=0;
		CalcWindowSize();
	}
	public CharCode GetCharCode(){return null;}
	public void SetCharCode(CharCode n){}
	public String GetCRType(){return null;}
	public void SetCRType(String n){}
	public boolean GetEofFlg(){return false;}
	public void SetEofFlg(boolean n){}
	
	//スクロール位置
	private void AddScrollPos(int n){
		scroll_top+=(n*16);
		CalcWindowSize();
		repaint();
	}
	//スクロール位置の計算
	private void CalcScrollPos(){
		int now_y=scroll_top/16;
		int page_line_num=GetPageLineNum();
		int doc_line_num =GetDocLineNum();
		int x=caret_pos & 15;
		int y=caret_pos / 16;
		if(now_y>y)now_y=y;
		int now_y_bottom=now_y+page_line_num-1;
		if(now_y_bottom<y){
			now_y=y-page_line_num+1;
		}
		scroll_top=now_y*16;
		//
		NormalizeScrollTop();
	}
	//スクロールの先頭位置の正規化
	private void NormalizeScrollTop(){
		int now_y=scroll_top/16;
		int page_line_num=GetPageLineNum();
		int doc_line_num=GetDocLineNum();
		if(now_y<0)now_y=0;
		int now_y_bottom=now_y+page_line_num-1;
		if((now_y_bottom+1)>=doc_line_num){
			now_y=doc_line_num-page_line_num;
			if(now_y<0)now_y=0;
		}
		scroll_top=now_y*16;
	}
	//ウインドウサイズの計算
	private void CalcWindowSize(){
		NormalizeScrollTop();
		//ページ行数
		int page_line_num=GetPageLineNum();
		//ドキュメント行数
		int doc_line_num =GetDocLineNum();
		//現在の位置
		int now_y=scroll_top/16;
		if(page_line_num>=doc_line_num){
		//スクロールしない場合
			scrollbar.setEnabled(false);
		}else{
		//スクロールする場合
			scrollbar.setEnabled(true);
			int max=doc_line_num;
			scrollbar.setModel(new DefaultBoundedRangeModel(now_y,page_line_num,0,max));
		}
	}
	//画面位置からカーソル位置を求める
	private int MousePos2CaretPos(IVECTOR2 pos){
		if(pos.x<XTOP)return -1;
		if(pos.x>=(XTOP+CHAR_W*17))return -1;
		if(pos.x>=(XTOP+CHAR_W*8) && pos.x<(XTOP+CHAR_W*9))return -1;
		int x=0;
		if(pos.x<(XTOP+CHAR_W*8)){
			x=(pos.x-XTOP)/CHAR_W;
		}else{
			x=(pos.x-(XTOP+CHAR_W*9))/CHAR_W;
			x+=8;
		}
		if(x<0)x=0;
		if(x>15)x=15;
		int y=(pos.y-YTOP)/CHAR_H;
		int index=(y*16)+x+scroll_top;
		if(index>bindata.getSize())return -1;
		return index;
	}
	//カーソル位置から画面位置を求める
	private IVECTOR2 Pos2Point(int n){
		n-=scroll_top;
		int i=n / 16;
		int j=n & 15;
		int y=i*CHAR_H;
		int x=j*CHAR_W;
		if(j>=8)x+=CHAR_W;
		x+=XTOP;
		y+=YTOP;
		return new IVECTOR2(x,y);
	}
	//カーソル位置の正規化
	private void NormalizeCaretPos(){
		int bottom=bindata.getSize();
		if(caret_pos<0)caret_pos=0;
		if(caret_pos>bottom)caret_pos=bottom;
	}
	//カーソル移動
	//shift true  選択保持
	//      false 選択クリア
	private void AddCartePos(int n,boolean shift_flg){
		caret_pos+=n;
		MoveCaretAfter(shift_flg);
	}
	//カーソル設定
	//shift true  選択保持
	//      false 選択クリア
	private void SetCaretPos(int n,boolean shift_flg){
		caret_pos=n;
		MoveCaretAfter(shift_flg);
	}
	//行移動
	//shift true  選択保持
	//      false 選択クリア
	private void AddCarteLine(int n,boolean shift_flg){
		int y=caret_pos / 16;
		int x=caret_pos & 15;
		int doc_line_num=GetDocLineNum();
		int line_bottom=doc_line_num-1;
		//int line_bottom=doc_line_num;
		y+=n;
		if(y<0)y=0;
		if(y>line_bottom)y=line_bottom;
		int new_pos=y*16+x;
		if(new_pos<(bindata.getSize()+1)){
			caret_pos=new_pos;
		}
		MoveCaretAfter(shift_flg);
	}
	//始めに移動
	//shift true  選択保持
	//      false 選択クリア
	private void SetCaretTop(boolean shift_flg){
		caret_pos=0;
		MoveCaretAfter(shift_flg);
	}
	//一番最後に移動
	//shift true  選択保持
	//      false 選択クリア
	private void SetCaretBottom(boolean shift_flg){
		caret_pos=bindata.getSize();
		MoveCaretAfter(shift_flg);
	}
	//行頭に移動
	private void SetCaretLineTop(boolean shift_flg){
		int y=caret_pos / 16;
		caret_pos=y*16;
		MoveCaretAfter(shift_flg);
	}
	//行末に移動
	private void SetCaretLineBottom(boolean shift_flg){
		int y=caret_pos / 16;
		caret_pos=y*16+15;
		int bottom=(bindata.getSize()+1);
		if(caret_pos>bottom){
			caret_pos=bottom;
		}
		MoveCaretAfter(shift_flg);
	}
	
	public boolean IsSelected(){
		return (caret_pos==select_pos);
	}
	//選択範囲の獲得(top,bottom)
	private int[] GetSelectBound(){
		int select_top   =caret_pos;
		int select_bottom=select_pos;
		if(select_top>select_bottom){
			int tmp=select_top;
			select_top   =select_bottom;
			select_bottom=tmp;
		}
		return new int[]{select_top,select_bottom};
	}
	//選択範囲のクリア
	private void ClearSelect(){
		select_pos=caret_pos;
		repaint();
	}
	//選択範囲のコピー
	private void CopySelect(){
		if(bindata.getSize()==0)return;
		int[] select_bound=GetSelectBound();
		int copy_size=select_bound[1]-select_bound[0];
		if(copy_size<=0)return;
		CopySelectSub(select_bound[0],copy_size);
	}
	//選択範囲のカット
	private void CutSelect(){
		if(bindata.getSize()==0)return;
		int[] select_bound=GetSelectBound();
		int cut_size=select_bound[1]-select_bound[0];
		if(cut_size<=0)return;
		CopySelectSub(select_bound[0],cut_size);
		CutSelectSub(select_bound[0],cut_size);
	}
	//コピーサブ
	private void CopySelectSub(int top,int copy_size){
		if(bindata.getSize()==0)return;
		if(copy_size<=0)return;
		copydata=bindata.getBytes(top,copy_size);
	}
	//カットサブ
	private void CutSelectSub(int top,int cut_size){
		if(bindata.getSize()==0)return;
		if(cut_size<=0)return;
		//
		byte[] cut_data=bindata.getBytes(top,cut_size);
		addUndoAct_Cut(top,cut_data).redo();
		//
		caret_pos=top;
		NormalizeCaretPos();
		//CalcWindowSize();
		MoveCaretAfter(false);
	}
	private BinUndoAct addUndoAct_Cut(final int top,final byte[] cut_data){
		final BinData _bindata=bindata;
		BinUndoAct undo_act=new BinUndoAct(){
			public void undo(){
				_bindata.insertBytes(top,cut_data);
			}
			public void redo(){
				_bindata.deleteBytes(top,cut_data.length);
			}
		};
		return AddUndoAct(undo_act);
	}
	//カーソル位置のデータを削除
	private void DeleteCaretData(){
		if(caret_pos>=bindata.getSize())return;
		int[] select_bound=GetSelectBound();
		int size=select_bound[1]-select_bound[0];
		if(size==0){
			CutSelectSub(caret_pos,1);
		}else{
			CutSelectSub(select_bound[0],size);
		}
	}
	//バックスペース位置のデータの削除
	private void BackspaceCaretData(){
		if(caret_pos<=0)return;
		CutSelectSub(caret_pos-1,1);
	}
	//ペースト
	private void PasteSelect(){
		if(copydata==null)return;
		PasteSelectSub(copydata);
	}
	//ペーストサブ
	private void PasteSelectSub(byte[] ins_data){
		int top=caret_pos;
		int prev_top=caret_pos;
		//選択部分があるか?
		int[] select_bound=GetSelectBound();
		byte[] cut_data=null;
		int cut_size=select_bound[1]-select_bound[0];
		if(cut_size>0){
			top=select_bound[0];
			prev_top=select_bound[1]+1;
			cut_data=bindata.getBytes(top,cut_size);
		}
		if(insert_flg){
		//挿入
			addUndoAct_PasteIns(top,ins_data,cut_data).redo();
		}else{
		//上書き
			//上書きされるデータ
			byte[] old_data=null;
			int old_size=ins_data.length;
			if((prev_top+old_size)>bindata.getSize()){
				old_size=bindata.getSize()-prev_top;
			}
			if(old_size>0){
				old_data=bindata.getBytes(prev_top,old_size);
			}
			addUndoAct_PasteOver(top,ins_data,old_data,cut_data).redo();
		}
		caret_pos=top+ins_data.length;
		NormalizeCaretPos();
		//CalcWindowSize();
		MoveCaretAfter(false);
	}
	private BinUndoAct addUndoAct_PasteIns(final int top,final byte[] ins_data,final byte[] cut_data){
		final BinData _bindata=bindata;
		BinUndoAct undo_act=new BinUndoAct(){
			public void undo(){
				_bindata.deleteBytes(top,ins_data.length);
				if(cut_data!=null){
					_bindata.insertBytes(top,cut_data);
				}
			}
			public void redo(){
				if(cut_data!=null){
					_bindata.deleteBytes(top,cut_data.length);
				}
				_bindata.insertBytes(top,ins_data);
			}
		};
		return AddUndoAct(undo_act);
	}
	private BinUndoAct addUndoAct_PasteOver(final int top,final byte[] ins_data,final byte[] old_data,final byte[] cut_data){
		final BinData _bindata=bindata;
		BinUndoAct undo_act=new BinUndoAct(){
			public void undo(){
				if(old_data!=null){
					_bindata.overwriteBytes(top,old_data);
					if(ins_data.length!=old_data.length){
						_bindata.resize(top+old_data.length);
					}
				}
				if(cut_data!=null){
					_bindata.insertBytes(top,cut_data);
				}
			}
			public void redo(){
				if(cut_data!=null){
					_bindata.deleteBytes(top,cut_data.length);
				}
				_bindata.overwriteBytes(top,ins_data);
			}
		};
		return AddUndoAct(undo_act);
	}
	
	//ドキュメントの行数
	private int GetDocLineNum(){
		int doc_line_num=((bindata.getSize()+1)+15) / 16;
		return doc_line_num;
	}
	//1画面に表示できる行数
	private int GetPageLineNum(){
		Dimension size=getSize();
		return (size.height/CHAR_H)-1;
	}
	//カーソル移動後の処理
	//shift true  選択保持
	//      false 選択クリア
	private void MoveCaretAfter(boolean shift_flg){
		if(input_cnt!=0){
			addUndoAct_InsOver1_UseTmp();
		}
		input_cnt=0;			//入力カウンタクリア
		NormalizeCaretPos();
		if(!shift_flg)ClearSelect();
		CalcScrollPos();
		CalcWindowSize();
		repaint();
		PrintStatusBarNowCaret();
	}
	//すべて選択
	private void SelectAll(){
		select_pos=0;
		caret_pos =bindata.getSize();
		MoveCaretAfter(true);
	}
	//データのリサイズ
	private void ResizeBindata(int size){
		bindata.resize(size);
		CalcWindowSize();
	}
	//数値入力
	private void InputNum(int n){
		if(input_cnt==0){
			input_tmp_pos=caret_pos;
			input_tmp_num=(byte)(n & 0xff);
			byte[] now_n=new byte[]{input_tmp_num};
			input_tmp_insert_flg=insert_flg;
			if(caret_pos>=bindata.getSize()){
				input_tmp_insert_flg=true;
			}
			if(input_tmp_insert_flg){
				input_tmp_old   =-1;
				bindata.insertBytes(caret_pos,now_n);
			}else{
				input_tmp_old   =bindata.get(caret_pos);
				bindata.overwriteBytes(caret_pos,now_n);
			}
			input_cnt++;
		}else{
			input_tmp_num=(byte)((((input_tmp_num & 0xff) << 4) | n) & 0xff);
			bindata.overwriteBytes(caret_pos,new byte[]{input_tmp_num});
			addUndoAct_InsOver1_UseTmp();
			caret_pos++;
			input_cnt=0;
		}
		NormalizeCaretPos();
		ClearSelect();
		repaint();
		if(input_cnt==0){
			MoveCaretAfter(false);
		}
	}
	private BinUndoAct addUndoAct_InsOver1_UseTmp(){
		final boolean _ins_flg=input_tmp_insert_flg;
		final int _ins_pos=input_tmp_pos;
		final byte[] _del_mem=new byte[]{input_tmp_old};
		final byte[] _ins_mem=new byte[]{input_tmp_num};
		final BinData _bindata=bindata;
		return addUndoAct_InsOver1(_ins_flg,_ins_pos,_ins_mem,_del_mem);
	}
	private BinUndoAct addUndoAct_InsOver1(boolean _ins_flg,final int _ins_pos,final byte[] _ins_mem,final byte[] _del_mem){
		if(_ins_flg){
			return addUndoAct_Ins1(_ins_pos,_ins_mem);
		}else{
			return addUndoAct_Over1(_ins_pos,_ins_mem,_del_mem);
		}
	}
	private BinUndoAct addUndoAct_Ins1(final int _ins_pos,final byte[] _ins_mem){
		final BinData _bindata=bindata;
		BinUndoAct undo_act=new BinUndoAct(){
			public void undo(){
				_bindata.deleteBytes(_ins_pos,_ins_mem.length);
			}
			public void redo(){
				_bindata.insertBytes(_ins_pos,_ins_mem);
			}
		};
		return AddUndoAct(undo_act);
	}
	private BinUndoAct addUndoAct_Over1(final int _ins_pos,final byte[] _ins_mem,final byte[] _del_mem){
		final BinData _bindata=bindata;
		BinUndoAct undo_act=new BinUndoAct(){
			public void undo(){
				_bindata.overwriteBytes(_ins_pos,_del_mem);
			}
			public void redo(){
				_bindata.overwriteBytes(_ins_pos,_ins_mem);
			}
		};
		return AddUndoAct(undo_act);
	}
	//数値入力0x00～0x0f
	private void InputNum_0(){InputNum(0);}
	private void InputNum_1(){InputNum(1);}
	private void InputNum_2(){InputNum(2);}
	private void InputNum_3(){InputNum(3);}
	private void InputNum_4(){InputNum(4);}
	private void InputNum_5(){InputNum(5);}
	private void InputNum_6(){InputNum(6);}
	private void InputNum_7(){InputNum(7);}
	private void InputNum_8(){InputNum(8);}
	private void InputNum_9(){InputNum(9);}
	private void InputNum_A(){InputNum(10);}
	private void InputNum_B(){InputNum(11);}
	private void InputNum_C(){InputNum(12);}
	private void InputNum_D(){InputNum(13);}
	private void InputNum_E(){InputNum(14);}
	private void InputNum_F(){InputNum(15);}
	public void OnInsertCopyString(){}
	public void OnInsertSearchString(){}
	public void OnInsertInputString(){}
	public void OnInsertDeleteString(){}
	public void OnInsertDate(){}
	public void OnInsertFile(){}
	public void OnInsertFilename(){}
	public void OnInsertHorizon(){}
	public void OnInsertTable(){}
	//========================================================
	//表示
	//========================================================
	public void paint(Graphics _g){
		Graphics2D g=(Graphics2D)_g;
		Dimension size=getSize();
		//背景フィル
		g.setColor(Color.BLACK);
		g.fillRect(0,0,size.width,size.height);
		//選択範囲
		int[] select_bound=GetSelectBound();
		//1画面に表示できる行数
		int page_line_num=GetPageLineNum();
		//ドキュメントの行数
		int doc_line_num=GetDocLineNum();
		//現在のスクロール位置
		int scroll_line_top=scroll_top/16;
		//表示行数
		int line_num=scroll_line_top+page_line_num;
		if(line_num>doc_line_num)line_num=doc_line_num;
		line_num-=scroll_line_top;
		//カーソル行数
		int caret_line=(caret_pos/16)-(scroll_top/16);
		//フォント加算位置
		int font_add_y=15;
		//一番上表示
		for(int j=0;j<16;j++){
			IVECTOR2 pos=Pos2Point(scroll_top+j);
			String m=String.format("+%1X",j);
			g.setColor(Color.YELLOW);
			g.drawString(m,pos.x,font_add_y);
		}
		//行表示
		for(int i=0;i<line_num;i++){
			int line_top=scroll_top+i*16;
			if(caret_line==i){
				if(input_cnt==0){
					g.setColor(Color.CYAN);
				}else{
					g.setColor(Color.RED);
				}
			}else{
				g.setColor(Color.WHITE);
			}
			IVECTOR2 pos1=Pos2Point(line_top);
			//アドレス
			String adr_m=String.format("%08X",line_top);
			g.drawString(adr_m,0,pos1.y+font_add_y);
			//0x08の「-」
			if((line_top+8)<bindata.getSize()){
				IVECTOR2 pos2=Pos2Point(line_top+8);
				g.drawString("-",pos2.x-14,pos2.y+font_add_y);
			}
			if(line_top<bindata.getSize()){
				int line_bottom=line_top+15;
				if(line_bottom>(bindata.getSize()-1))line_bottom=(bindata.getSize()-1);
				int line_size=(line_bottom+1)-line_top;
				byte[] line=bindata.getBytes(line_top,line_size);
				String line_m=new String(line);
				g.drawString(line_m,DUMPX,pos1.y+font_add_y);
			}
			for(int j=0;j<16;j++){
				int index=line_top+j;
				IVECTOR2 pos=Pos2Point(index);
				if(index>bindata.getSize())continue;
				String m="";
				int csr_x=pos.x;
				int csr_y=pos.y;
				int csr_w=16;
				int csr_h=16;
				Color m_col;
				if(index<bindata.getSize()){
					int n=bindata.get(index) & 0xff;
					m=String.format("%02X",n);
					csr_w=16;
					m_col=Color.WHITE;
				}else{
					m="[EOF]";
					csr_w=32;
					m_col=Color.MAGENTA;
				}
				//
				if(index>=select_bound[0] && index<=select_bound[1]){
					if(input_cnt==0){
						if(caret_pos==index){
							g.setColor(Color.CYAN);
							if(!insert_flg){
								csr_h-=10;
								csr_y+=10;
							}
						}else{
							g.setColor(Color.CYAN.darker());
						}
					}else{
						g.setColor(Color.YELLOW);
					}
					g.fillRect(csr_x,csr_y,csr_w,csr_h);
					//文字の描画
					g.setColor(Color.MAGENTA);
					g.drawString(m,pos.x,pos.y+font_add_y);
				}else{
					//文字の描画
					g.setColor(m_col);
					g.drawString(m,pos.x,pos.y+font_add_y);
				}
			}
		}
	}
	//ステータスバーに表示
	private void PrintStatusBarNowCaret(){
		PrintStatusBar(GetStatusBarStr());
	}
	//========================================================
	//========================================================
	private BinUndoBuff undobuff;
	private void InitUndo(){
		undobuff=new BinUndoBuff();
		ClearUndoBuff();
	}
	private void ClearUndoBuff(){
		undobuff.Clear();
	}
	public boolean CanUndo(){
		return undobuff.CanUndo();
	}
	public boolean CanRedo(){
		return undobuff.CanRedo();
	}
	private void ExecUndo(){
		undobuff.Undo();
		//
		NormalizeCaretPos();
		ClearSelect();
		repaint();
		MoveCaretAfter(false);
	}
	private void ExecRedo(){
		undobuff.Redo();
		//
		NormalizeCaretPos();
		ClearSelect();
		repaint();
		MoveCaretAfter(false);
	}
	private BinUndoAct AddUndoAct(BinUndoAct act){
		undobuff.AddUndoAct(act);
		//act.redo();
		return act;
	}
	//========================================================
	//========================================================
	public String GetTabTitleStr(){
		return "バイナリエディタ";
	}
	public String GetTitleStr(){
		return "BinEditor";
	}
	public String GetStatusBarStr(){
		return String.format("adr(%08x)",caret_pos);
	}
	public FilenameLine GetTagJumpFilenameLine(){
		return null;
	}
	public String GetCurrentDir(){
		return new File(".").getAbsolutePath();
	}
	public void SetFocus(){
		grabFocus();
	}
	public void FlipInsertMode(){
		insert_flg=!insert_flg;
		repaint();
	}
	public void MoveLeft(){
		AddCartePos(-1,false);
	}
	public void MoveRight(){
		AddCartePos( 1,false);
	}
	public void MoveUp(){
		AddCarteLine(-1,false);
	}
	public void MoveDown(){
		AddCarteLine( 1,false);
	}
	public void MovePageUp(){
		AddCarteLine(-16,false);
	}
	public void MovePageDown(){
		AddCarteLine( 16,false);
	}
	public void MoveTop(){
		SetCaretTop(false);
	}
	public void MoveBottom(){
		SetCaretBottom(false);
	}
	public void MoveLeftKeepSelect(){
		AddCartePos(-1,true);
	}
	public void MoveRightKeepSelect(){
		AddCartePos( 1,true);
	}
	public void MoveUpKeepSelect(){
		AddCarteLine(-1,true);
	}
	public void MoveDownKeepSelect(){
		AddCarteLine( 1,true);
	}
	public void MovePageUpKeepSelect(){
		AddCarteLine(-16,true);
	}
	public void MovePageDownKeepSelect(){
		AddCarteLine( 16,true);
	}
	public void MoveTopKeepSelect(){
		SetCaretTop(true);
	}
	public void MoveBottomKeepSelect(){
		SetCaretBottom(true);
	}
	void MoveLineTop(){
		SetCaretLineTop(false);
	}
	void MoveLineBottom(){
		SetCaretLineBottom(false);
	}
	void MoveLineTopKeepSelect(){
		SetCaretLineTop(true);
	}
	void MoveLineBottomKeepSelect(){
		SetCaretLineBottom(true);
	}
	//========================================================
	public void ReadReg(){}
	public void OutReg(){}
	public void OnVzCut(){ActionTool.ExecAction(this,"cut-to-clipboard");}
	public void OnCut(){ActionTool.ExecAction(this,"cut-to-clipboard");}
	public void OnCopy(){ActionTool.ExecAction(this,"copy-to-clipboard");}
	public void OnPaste(){ActionTool.ExecAction(this,"paste-from-clipboard");}
	public void OnPrint(){}
	public void OnDocInfo(){}
	public void OnSelectAll(){ActionTool.ExecAction(this,"select-all");}
	public void OnSelectClear(){ActionTool.ExecAction(this,"unselect");}
	public void OnChangeCode(String code){}
	public void OnSelectConv(String type){}
	public void OnTagJump(){}
	public void OnJumpTop(){ActionTool.ExecAction(this,"caret-begin-line");}
	public void OnJumpBottom(){ActionTool.ExecAction(this,"caret-end-line");}
	public void OnJumpLineTop(){}
	public void OnJumpLineBottom(){}
	public void OnUndo(){ActionTool.ExecAction(this,"exec-undo");}
	public void OnRedo(){ActionTool.ExecAction(this,"exec-redo");}
	public void OnSearch(){}
	public void OnReplace(){}
	public void OnSearchUp(){}
	public void OnSearchDown(){}
	public void OnRemember(){}
	public void OnUpdate(){}
	public void OnUpdateAttr(){}
	public void OnSelectStart(){}
	public void OnFlipInsertMode(){ActionTool.ExecAction(this,"flip-insert-mode");}
	public boolean CanSave(){
		return true;
	}
	public boolean CanPrint(){
		return false;
	}
	public void SetFilename(UFile n){
		filename=n;
	}
	public UFile GetFilename(){
		return filename;
	}
	public Component GetDropTarget(){
		return null;
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
	public void OnMacroRecStart(){}
	public void OnMacroRecEnd(){}
	public void OnMacroRecToggle(){}
	public void OnMacroPlay(){}
	public boolean CanMacroRecStart(){return false;}
	public boolean CanMacroRecEnd(){return false;}
	public boolean CanMacroPlay(){return false;}
	//ショートカット
	public void OnShortCut(){
		/*
		ShortCutData shortcut=new ShortCutData();
		shortcut.setComponent(this);
		if(ShortCutDialog.Open(this,this,shortcut)){
			
		}
		*/
		try{
			UFile file=UFileBuilder.createLocal("bineditor.shortcut");
			if(ShortCutDialog.Open(this,this,file)){
				
			}
		}catch(Exception ex){}
	}
//	StatusBarPane.StateLabel[] status_labels;
	public void initStatusBar(){
//		status_labels=BinEditorStatusBar.getLabels();
	}
	public StatusBarPane.StateLabel[] getStatusBarLabels(){
		return BinEditorStatusBar.getLabels();
//		return status_labels;
	}
	/*
	public HashMap<String,Object> getPrintStatuss(){
		return null;
	}
	*/
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
		return BinEditorFunctionKey.getFunctions();
	}
	public InputMap getFunctionKeyInputMap(){
		return null;
	}
	public ActionInfoMap getFunctionKeyActionInfoMap(){
		return null;
	}
	public ToolBarPane.ToolBarItem[][] getToolBarItems(){
		return BinEditorToolBar.getItems();
	}
	private MenuFeedback main_menu;				//
	public MenuFeedback getMenuFeedback(){
		return main_menu;
	}
	private void initMainMenu(){
		final BinEditor _this=this;
		BinEditorMenu.Callback menu_callback=new BinEditorMenu.Callback(){
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
			public boolean CanSave(){
				return _this.CanSave();
			}
			public boolean CanUndo(){
				return _this.CanUndo();
			}
			public boolean CanRedo(){
				return _this.CanRedo();
			}
			public boolean IsSelected(){
				return _this.IsSelected();
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
		main_menu=new BinEditorMenu(menu_callback);
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
		InputMap inputmap=getInputMap();
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
	
	//========================================================
	private ActionTbl[] g_act_tbl={
		new ActionTbl(DefaultEditorKit.backwardAction 				,this,"MoveLeft"				),
		new ActionTbl(DefaultEditorKit.forwardAction 				,this,"MoveRight"				),
		new ActionTbl(DefaultEditorKit.upAction						,this,"MoveUp"					),
		new ActionTbl(DefaultEditorKit.downAction 					,this,"MoveDown"				),
		new ActionTbl(DefaultEditorKit.pageUpAction 				,this,"MovePageUp"				),
		new ActionTbl(DefaultEditorKit.pageDownAction 				,this,"MovePageDown"			),
		new ActionTbl(DefaultEditorKit.beginAction 					,this,"MoveTop"					),
		new ActionTbl(DefaultEditorKit.endAction					,this,"MoveBottom"				),
		new ActionTbl(DefaultEditorKit.beginParagraphAction 		,this,"MoveLineTop"				),
		new ActionTbl(DefaultEditorKit.endParagraphAction 			,this,"MoveLineBottom"			),
		new ActionTbl(DefaultEditorKit.selectionBackwardAction 		,this,"MoveLeftKeepSelect"		),
		new ActionTbl(DefaultEditorKit.selectionForwardAction 		,this,"MoveRightKeepSelect"		),
		new ActionTbl(DefaultEditorKit.selectionUpAction 			,this,"MoveUpKeepSelect"		),
		new ActionTbl(DefaultEditorKit.selectionDownAction 			,this,"MoveDownKeepSelect"		),
		new ActionTbl(DefaultEditorKit.selectionBeginLineAction		,this,"MoveTopKeepSelect"		),
		new ActionTbl(DefaultEditorKit.selectionEndLineAction		,this,"MoveBottomKeepSelect"	),
		new ActionTbl(DefaultEditorKit.selectionBeginParagraphAction,this,"MoveLineTopKeepSelect"	),
		new ActionTbl(DefaultEditorKit.selectionEndParagraphAction 	,this,"MoveLineBottomKeepSelect"),
		new ActionTbl(DefaultEditorKit.selectAllAction 				,this,"SelectAll"				),
		new ActionTbl(DefaultEditorKit.copyAction 					,this,"CopySelect"				),
		new ActionTbl(DefaultEditorKit.cutAction 					,this,"CutSelect"				),
		new ActionTbl(DefaultEditorKit.pasteAction 					,this,"PasteSelect"				),
		new ActionTbl(DefaultEditorKit.deleteNextCharAction 		,this,"DeleteCaretData"			),
		new ActionTbl(DefaultEditorKit.deletePrevCharAction 		,this,"BackspaceCaretData"		),
		new ActionTbl(BinEditorAction.selectionPageUp			 	,this,"MovePageUpKeepSelect"	),
		new ActionTbl(BinEditorAction.selectionPageDown				,this,"MovePageDownKeepSelect"	),
		new ActionTbl(BinEditorAction.selectClear					,this,"ClearSelect"				),
		new ActionTbl(BinEditorAction.FlipInsertMode				,this,"FlipInsertMode"			),
		new ActionTbl(BinEditorAction.Input_0						,this,"InputNum_0"				),
		new ActionTbl(BinEditorAction.Input_1						,this,"InputNum_1"				),
		new ActionTbl(BinEditorAction.Input_2						,this,"InputNum_2"				),
		new ActionTbl(BinEditorAction.Input_3						,this,"InputNum_3"				),
		new ActionTbl(BinEditorAction.Input_4						,this,"InputNum_4"				),
		new ActionTbl(BinEditorAction.Input_5						,this,"InputNum_5"				),
		new ActionTbl(BinEditorAction.Input_6						,this,"InputNum_6"				),
		new ActionTbl(BinEditorAction.Input_7						,this,"InputNum_7"				),
		new ActionTbl(BinEditorAction.Input_8						,this,"InputNum_8"				),
		new ActionTbl(BinEditorAction.Input_9						,this,"InputNum_9"				),
		new ActionTbl(BinEditorAction.Input_A						,this,"InputNum_A"				),
		new ActionTbl(BinEditorAction.Input_B						,this,"InputNum_B"				),
		new ActionTbl(BinEditorAction.Input_C						,this,"InputNum_C"				),
		new ActionTbl(BinEditorAction.Input_D						,this,"InputNum_D"				),
		new ActionTbl(BinEditorAction.Input_E						,this,"InputNum_E"				),
		new ActionTbl(BinEditorAction.Input_F						,this,"InputNum_F"				),
		new ActionTbl(BinEditorAction.Undo							,this,"ExecUndo"				),
		new ActionTbl(BinEditorAction.Redo							,this,"ExecRedo"				),
	};
	
	private InputTbl[] g_input_tbl={
		new InputTbl(KeyEvent.VK_LEFT		,0													,DefaultEditorKit.backwardAction),
		new InputTbl(KeyEvent.VK_RIGHT		,0													,DefaultEditorKit.forwardAction),
		new InputTbl(KeyEvent.VK_UP			,0													,DefaultEditorKit.upAction),
		new InputTbl(KeyEvent.VK_DOWN		,0													,DefaultEditorKit.downAction),
		new InputTbl(KeyEvent.VK_PAGE_UP	,0													,DefaultEditorKit.pageUpAction),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,0													,DefaultEditorKit.pageDownAction),
		new InputTbl(KeyEvent.VK_HOME		,0													,DefaultEditorKit.beginAction),
		new InputTbl(KeyEvent.VK_END		,0													,DefaultEditorKit.endAction),
		new InputTbl(KeyEvent.VK_LEFT		,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.beginParagraphAction),
		new InputTbl(KeyEvent.VK_RIGHT		,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.endParagraphAction),
		new InputTbl(KeyEvent.VK_LEFT		,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionBackwardAction),
		new InputTbl(KeyEvent.VK_RIGHT		,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionForwardAction),
		new InputTbl(KeyEvent.VK_UP			,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionUpAction),
		new InputTbl(KeyEvent.VK_DOWN		,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionDownAction),
		new InputTbl(KeyEvent.VK_HOME		,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionBeginLineAction),
		new InputTbl(KeyEvent.VK_END		,KeyEvent.SHIFT_DOWN_MASK							,DefaultEditorKit.selectionEndLineAction),
		new InputTbl(KeyEvent.VK_LEFT		,KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.selectionBeginParagraphAction),
		new InputTbl(KeyEvent.VK_RIGHT		,KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK	,DefaultEditorKit.selectionEndParagraphAction),
		new InputTbl(KeyEvent.VK_C			,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.copyAction),
		new InputTbl(KeyEvent.VK_X			,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.cutAction),
		new InputTbl(KeyEvent.VK_V			,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.pasteAction),
		new InputTbl(KeyEvent.VK_DELETE		,0													,DefaultEditorKit.deleteNextCharAction),
		new InputTbl(KeyEvent.VK_BACK_SPACE	,0													,DefaultEditorKit.deletePrevCharAction),
		new InputTbl(KeyEvent.VK_A			,KeyEvent.CTRL_DOWN_MASK							,DefaultEditorKit.selectAllAction),
		new InputTbl(KeyEvent.VK_Z			,KeyEvent.CTRL_DOWN_MASK							,BinEditorAction.Undo),
		new InputTbl(KeyEvent.VK_Y			,KeyEvent.CTRL_DOWN_MASK							,BinEditorAction.Redo),
		new InputTbl(KeyEvent.VK_PAGE_UP	,KeyEvent.SHIFT_DOWN_MASK							,BinEditorAction.selectionPageUp),
		new InputTbl(KeyEvent.VK_PAGE_DOWN	,KeyEvent.SHIFT_DOWN_MASK							,BinEditorAction.selectionPageDown),
		new InputTbl(KeyEvent.VK_D			,KeyEvent.CTRL_DOWN_MASK							,BinEditorAction.selectClear),
		new InputTbl(KeyEvent.VK_INSERT		,0													,BinEditorAction.FlipInsertMode),
		new InputTbl(KeyEvent.VK_0			,0													,BinEditorAction.Input_0),
		new InputTbl(KeyEvent.VK_1			,0													,BinEditorAction.Input_1),
		new InputTbl(KeyEvent.VK_2			,0													,BinEditorAction.Input_2),
		new InputTbl(KeyEvent.VK_3			,0													,BinEditorAction.Input_3),
		new InputTbl(KeyEvent.VK_4			,0													,BinEditorAction.Input_4),
		new InputTbl(KeyEvent.VK_5			,0													,BinEditorAction.Input_5),
		new InputTbl(KeyEvent.VK_6			,0													,BinEditorAction.Input_6),
		new InputTbl(KeyEvent.VK_7			,0													,BinEditorAction.Input_7),
		new InputTbl(KeyEvent.VK_8			,0													,BinEditorAction.Input_8),
		new InputTbl(KeyEvent.VK_9			,0													,BinEditorAction.Input_9),
		new InputTbl(KeyEvent.VK_A			,0													,BinEditorAction.Input_A),
		new InputTbl(KeyEvent.VK_B			,0													,BinEditorAction.Input_B),
		new InputTbl(KeyEvent.VK_C			,0													,BinEditorAction.Input_C),
		new InputTbl(KeyEvent.VK_D			,0													,BinEditorAction.Input_D),
		new InputTbl(KeyEvent.VK_E			,0													,BinEditorAction.Input_E),
		new InputTbl(KeyEvent.VK_F			,0													,BinEditorAction.Input_F),
		new InputTbl(KeyEvent.VK_NUMPAD0	,0													,BinEditorAction.Input_0),
		new InputTbl(KeyEvent.VK_NUMPAD1	,0													,BinEditorAction.Input_1),
		new InputTbl(KeyEvent.VK_NUMPAD2	,0													,BinEditorAction.Input_2),
		new InputTbl(KeyEvent.VK_NUMPAD3	,0													,BinEditorAction.Input_3),
		new InputTbl(KeyEvent.VK_NUMPAD4	,0													,BinEditorAction.Input_4),
		new InputTbl(KeyEvent.VK_NUMPAD5	,0													,BinEditorAction.Input_5),
		new InputTbl(KeyEvent.VK_NUMPAD6	,0													,BinEditorAction.Input_6),
		new InputTbl(KeyEvent.VK_NUMPAD7	,0													,BinEditorAction.Input_7),
		new InputTbl(KeyEvent.VK_NUMPAD8	,0													,BinEditorAction.Input_8),
		new InputTbl(KeyEvent.VK_NUMPAD9	,0													,BinEditorAction.Input_9),
	};
	
	private void InitActionMap(){
		ActionTbl.SetActionTbl(this,g_act_tbl);
		ActionTool.SetInputTbl(this,g_input_tbl);
	}
	//========================================================
	//マウス入力
	private boolean pressed_flg=false;
	public void mouseClicked(MouseEvent e){
		int bt=e.getButton();
		if(bt==MouseEvent.BUTTON1){
			pressed_flg=false;
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		int bt=e.getButton();
		if(bt==MouseEvent.BUTTON1){
			pressed_flg=true;
			int n=MousePos2CaretPos(ConvAWT.Point_IVEC2(e.getPoint()));
			if(n<=0)return;
			SetCaretPos(n,false);
		}else{
		}
	}
	public void mouseReleased(MouseEvent e){
		int bt=e.getButton();
		if(bt==MouseEvent.BUTTON1){
			pressed_flg=false;
		}
	}
	public void mouseDragged(MouseEvent e){
		int bt=e.getButton();
		if(pressed_flg){
			int n=MousePos2CaretPos(ConvAWT.Point_IVEC2(e.getPoint()));
			if(n<=0)return;
			SetCaretPos(n,true);
		}
	}
	public void mouseMoved(MouseEvent e){}
	public void mouseWheelMoved(MouseWheelEvent e){
		int n=e.getWheelRotation()*3;
		AddScrollPos(n);
	}
	public void componentHidden(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	public void componentResized(ComponentEvent e){
		CalcWindowSize();
		repaint();
	}
	public void componentShown(ComponentEvent e){
		CalcWindowSize();
		repaint();
	}
}
