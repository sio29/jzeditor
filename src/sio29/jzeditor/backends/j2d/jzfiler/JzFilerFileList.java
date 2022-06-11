/******************************************************************************
;	Vzファイラー、ファイルリスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListCellRenderer;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

//イベント
class JzFilerChangeEvent extends ChangeEvent{
	public final static int SELECT_FILE=0;
	public final static int CHANGE_FILE=1;
	public final static int CHANGE_CURRENT_DIR=2;
	public final static int SEPARATOR=3;
	//
	public int type;
	public UFile file;
	public float per;
	//
	public JzFilerChangeEvent(Object source,int type,float per){
		super(source);
		this.type=type;
		this.file=null;
		this.per=per;
	}
	public JzFilerChangeEvent(Object source,int type,UFile file){
		super(source);
		this.type=type;
		this.file=file;
		this.per=0.0f;
	}
	public static String toStringType(int type){
		switch(type){
			case SELECT_FILE:return "SELECT_FILE";
			case CHANGE_FILE:return "CHANGE_FILE";
			case CHANGE_CURRENT_DIR:return "CHANGE_CURRENT_DIR";
			case SEPARATOR:return "SEPARATOR";
		}
		return "---";
	}
	public String toString(){
		return "type("+toStringType(type)+"),ufile("+file+"),per("+per+")";
	}
}

//ファイルリスト
public class JzFilerFileList extends JList<UFile> {
	private JzFilerDialog p_filerdialog;
	private ArrayList<ChangeListener> listener=new ArrayList<ChangeListener>();
	private UFile g_current_dir;					//カレントディレクトリ
	private JzFilerFileList pair_list=null;		//ペア
	private UFileCurrentDrives g_drive_currents;
	private boolean value_change_flg=true;
	//
	public JzFilerFileList(JzFilerDialog p_filerdialog,UFile current_dir,UFileCurrentDrives drive_currents){
		super();
		this.p_filerdialog=p_filerdialog;
		g_drive_currents=drive_currents;
		setOpaque(true);
		setBackground(Color.BLACK);
		final JzFilerFileList _this=this;
		//セルレンダラーの登録
		setCellRenderer(new JzFilerListCellRenderer(GetCellRendererParam()));
		addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(!value_change_flg)return;
				int i=e.getFirstIndex();
//System.out.println("e.getValueIsAdjusting:"+e.getValueIsAdjusting());
				if(!e.getValueIsAdjusting()){
					UFile file=getSelectFileFromIndex(i);
					JzFilerChangeEvent ev=new JzFilerChangeEvent(this,JzFilerChangeEvent.CHANGE_FILE,file);
					sendChangeListener(ev);
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//ダブルクリックでファイルを開く
				if (e.getClickCount() == 2) {
					int i = locationToIndex(e.getPoint());
					selectFileIndex(i);
				}
			}
		});
		initAction();
		
		InputMap  im=getInputMap();
		//
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JzFilerAction.MoveFilerRight);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JzFilerAction.MoveFilerLeft);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), JzFilerAction.SelectDrive);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),JzFilerAction.SelectedFile);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_MASK),JzFilerAction.UnSelect);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0),JzFilerAction.SelectAllFile);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B,0),JzFilerAction.SelectFlip);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0),"selectNextRowExtendSelection");
		//カレントディレクトリの設定
		setCurrentDir(current_dir);
	}
	private void initAction(){
		ActionMap am=getActionMap();
		
		am.put(JzFilerAction.MoveFilerRight,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					moveRightFiler();
				}
			});
		am.put(JzFilerAction.MoveFilerLeft,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					moveLeftFiler();
				}
			});
		am.put(JzFilerAction.SelectDrive,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					SelectDrive();
				}
			});
		am.put(JzFilerAction.ChangeDirParent,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					ChangeDirParent();
				}
			});
		am.put(JzFilerAction.SelectedFile,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					int i=getSelectedIndex();
					selectFileIndex(i);
//System.out.println("selected-file");
				}
			});
		am.put(JzFilerAction.UnSelect,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					clearSelection();
				}
			});
		am.put(JzFilerAction.SelectFlip,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					SelectFlip();
				}
			});
		am.put(JzFilerAction.SelectAllFile,new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e) {
					SelectAllFile();
				}
			});
	}
	//=======================
	public void moveRightFiler(){
		if(pair_list!=null){
			pair_list.grabFocus();
		}
	}
	public void moveLeftFiler(){
		if(pair_list!=null){
			pair_list.grabFocus();
		}
	}
	//=======================
	private void enableSendValueChange(boolean n){
		value_change_flg=n;
	}
	//=======================
	//ドライブ選択
	public void SelectDrive(){
		UFile[] drives=getDriveList();
		UFile new_drive=JzFilerDriveSelecter.selectDrive(this,drives,GetCurrentDir());
		if(new_drive!=null){
			UFile new_dir=g_drive_currents.getDriveCurrentDir(new_drive);
//System.out.println("new_dir="+new_dir);
			if(new_dir!=null){
				changeCurrentDir(new_dir);
			}
		}
	}
	//=======================
	//親ディレクトリへ移動
	public void ChangeDirParent(){
		//System.out.println("ChangeDirParent");
		changeCurrentDirParent();
	}
	//=======================
	public void UpdateAttr(){
		ListCellRenderer _cr=getCellRenderer();
		if(_cr instanceof JzFilerListCellRenderer){
			JzFilerListCellRenderer cr=(JzFilerListCellRenderer)_cr;
			cr.setParam(GetCellRendererParam());
		}
		repaint();
	}
	//=======================
	public int GetSortType(){
		return p_filerdialog.getSortType();
	}
	public int GetSortDir(){
		return p_filerdialog.getSortDir();
	}
	public JzFilerListCellRendererParam GetCellRendererParam(){
		return p_filerdialog.GetCellRendererParam();
	}
	public UFilenameFilter GetFileFilterFromMask(){
		return p_filerdialog.GetFileFilterFromMask();
	}
	public UFile[] getDriveList(){
		return p_filerdialog.getDriveList();
	}
	//=======================
	public UFile GetDestDir(){
		JzFilerFileList pair=GetPairList();
		if(pair==null)return null;
		return pair.GetCurrentDir();
	}
	public void SetPairList(JzFilerFileList n){
		pair_list=n;
	}
	public JzFilerFileList GetPairList(){
		return pair_list;
	}
	public UFile GetCurrentDir(){
		return g_current_dir;
	}
	public UFile[] GetFiles(){
		UFilenameFilter filter=GetFileFilterFromMask();
		return GetFiles(GetCurrentDir(),filter);
	}
	private String toStringFiles(UFile[] files){
		if(files==null)return "files(null)";
		String mm="files("+files.length+")\n";
		for(int i=0;i<files.length;i++){
			mm+="  ["+i+"]:"+files[i]+"\n";
		}
		return mm;
	}
	//現在ディレクトリのファイルリスト
	private static UFile[] GetFiles(UFile current_dir,UFilenameFilter filter){
		ArrayList<UFile> m=new ArrayList<UFile>();
		//親ディレクトリ
		UFile parent_dir=current_dir.getParentFile();
		if(parent_dir!=null){
			m.add(parent_dir);
		}
		//元のファイルリスト
		UFile[] files;
		if(filter==null){
			files=current_dir.listFiles();
		}else{
			files=current_dir.listFiles(filter);
		}
		//追加
		for(int i=0;i<files.length;i++){
			UFile file=files[i];
			m.add(file);
		}
		return (UFile[])m.toArray(new UFile[]{});
	}
	//ソート
	private static void SortFileList(UFile[] files,int sort_type,int sort_dir){
		if(files==null)return;
		if(files.length<2)return;
		Arrays.sort(files,new JzFilerFileComparator(sort_type,sort_dir));
	}
	//ファイルリストの設定
	private void setFileList(){
		UFile[] mm=GetFiles();
		int sort_type=GetSortType();
		int sort_dir=GetSortDir();
		SortFileList(mm,sort_type,sort_dir);
		ListCellRenderer _cr=getCellRenderer();
		if(_cr instanceof JzFilerListCellRenderer){
			JzFilerListCellRenderer cr=(JzFilerListCellRenderer)_cr;
			cr.setCurrentDir(GetCurrentDir());
		}
		enableSendValueChange(false);
		setListData(mm);
		enableSendValueChange(true);
	}
	//Update
	void Update(){
		setFileList();
		setSelectedIndex(0);
	}
	//親ディレクトリへ移動
	public void changeCurrentDirParent(){
		UFile file;
		try{
			file=GetCurrentDir().getParentFile();
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}
		changeCurrentDir(file);
	}
	//カレントの変更
	public void changeCurrentDir(UFile current_dir){
		if(current_dir==null)return;
		setCurrentDir(current_dir);
		g_drive_currents.setCurrentDir(current_dir);
		//
		JzFilerChangeEvent ev=new JzFilerChangeEvent(this,JzFilerChangeEvent.CHANGE_CURRENT_DIR,current_dir);
		sendChangeListener(ev);
	}
	//ファイルIndexを得る
	public int getFileIndex(UFile file){
		if(file==null)return -1;
		ListModel lm=getModel();
		if(lm==null)return -1;
		for(int i=0;i<lm.getSize();i++){
			Object o=lm.getElementAt(i);
			if(file.equals(o))return i;
		}
		return -1;
	}
	//カレントディレクトリの設定
	public boolean setCurrentDir(UFile current_dir){
		UFile old_current_dir=GetCurrentDir();
		g_current_dir=current_dir;
		setFileList();
		//
		int select_index=getFileIndex(old_current_dir);
		if(select_index<0)select_index=0;
		setSelectedIndex(select_index);
		return true;
	}
	public UFile getSelectFileFromIndex(int index){
		Object obj=getSelectedValue();
		if(obj==null)return null;
		if(!(obj instanceof UFile))return null;
		return (UFile)obj;
	}
	//indexでファイル選択
	public void selectFileIndex(int index){
		//System.out.println("selectFileIndex:"+index);
		Object obj=getSelectedValue();
		if(obj==null)return;
		if(obj instanceof UFile){
			UFile file=(UFile)obj;
			if(file.isDirectory()){
				String name=null;
				try{
					name=((UFileURL)file).getFile().getName();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				if(name.equals(".")){
				}else{
					if(name.equals("..")){
						changeCurrentDirParent();
					}else{
						changeCurrentDir(file);
					}
				}
			}else if(file.canRead()){
				JzFilerChangeEvent ev=new JzFilerChangeEvent(this,JzFilerChangeEvent.SELECT_FILE,file);
				sendChangeListener(ev);
			}
		}
	}
	//選択されているファイル
	public UFile getSelectFile(){
		Object obj=getSelectedValue();
		if(obj==null)return null;
		if(!(obj instanceof UFile))return null;
		/*
		try{
			return ((UFile)obj).getCanonicalFile();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		*/
		return ((UFile)obj);
	}
	//ChangeListener登録
	public void addChangeListener(ChangeListener n){
		listener.add(n);
	}
	private void sendChangeListener(ChangeEvent ev){
//System.out.println("sendChangeListener:ev:"+ev.toString());
		for(int i=0;i<listener.size();i++){
			ChangeListener l=listener.get(i);
			if(l==null)continue;
			l.stateChanged(ev);
		}
	}
	//全選択
	void SelectAll(){
		ListModel model = getModel();
		addSelectionInterval(1,model.getSize()-1);
	}
	//選択する
	private void SetSelected(ArrayList<Integer> slist){
		clearSelection();
		int[] slist2=new int[slist.size()];
		for(int i=0;i<slist.size();i++){
			slist2[i]=slist.get(i);
		}
		setSelectedIndices(slist2);
	}
	//ファイルを全選択
	void SelectAllFile(){
		ListModel model = getModel();
		ArrayList<Integer> slist=new ArrayList<Integer>();
		for(int i=0;i<model.getSize();i++){
			UFile file=(UFile)model.getElementAt(i);
			if(file.isFile()){
				slist.add(i);
			}
		}
		SetSelected(slist);
	}
	//選択を反転
	void SelectFlip(){
		ListModel model = getModel();
		ArrayList<Integer> slist=new ArrayList<Integer>();
		for(int i=0;i<model.getSize();i++){
			if(!isSelectedIndex(i)){
				slist.add(i);
			}
		}
		SetSelected(slist);
	}
	//選択クリア
	void UnSelect(){
		clearSelection();
	}
	//選択ファイルを返す
	public List<UFile> GetSelectFileList(){
		return getSelectedValuesList();
	}
	public void SetSelectFiles(UFile[] files,boolean[] flgs){
		HashMap<UFile,Boolean> map=new HashMap<UFile,Boolean>();
		for(int i=0;i<files.length;i++){
			map.put(files[i],flgs[i]);
		}
		ArrayList<Integer> indeces=new ArrayList<Integer>();
		ListModel model=getModel();
		for(int i=0;i<model.getSize();i++){
			Object n=model.getElementAt(i);
			if(!(n instanceof UFile))continue;
			UFile file=(UFile)n;
			Boolean flg=map.get(file);
			if(flg==null)continue;
			if((boolean)flg){
				indeces.add(i);
			}
		}
		int[] ret=new int[indeces.size()];
		for(int i=0;i<indeces.size();i++){
			ret[i]=indeces.get(i);
		}
		setSelectedIndices(ret);
	}
}
