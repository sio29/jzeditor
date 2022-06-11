/******************************************************************************
;	Vzファイラー、リスト親
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.List;
import java.awt.Container;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class JzFilerFileListPane extends Container{
	private JzFilerDialog p_filerdialog;
	private JzFilerFileList list;
	private JScrollPane scroll;
	private JzFilerFileListPane pair;
	private Container status;
	private JLabel status1;
	private JLabel status2;
	//
	public JzFilerFileListPane(JzFilerDialog _p_filerdialog,UFile current_dir,UFileCurrentDrives drive_currents){
		p_filerdialog=_p_filerdialog;
		list=new JzFilerFileList(p_filerdialog,current_dir,drive_currents);
		list.setVisible(true);
		scroll=new JScrollPane(list,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVisible(true);
		setLayout(new BorderLayout());
		status1=new JLabel("ステータス1");
		status2=new JLabel("ステータス2");
		status=new Container();
		status.setLayout(new BoxLayout(status,BoxLayout.Y_AXIS));
		status.add(status1);
		status.add(status2);
		add("North",status);
		add("Center",scroll);
		setVisible(true);
		PrintNowDir();
		//
		list.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if(e instanceof JzFilerChangeEvent){
					JzFilerChangeEvent ev=(JzFilerChangeEvent)e;
					if(ev.type==JzFilerChangeEvent.CHANGE_CURRENT_DIR){
						PrintNowDir();
					}
				}
			}
		});
	}
	public void UpdateAttr(){
		list.UpdateAttr();
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
	
	//=======================
	//ペアの設定
	public void setPair(JzFilerFileListPane _pair){
		pair=_pair;
		list.SetPairList(_pair.list);
	}
	public JzFilerFileListPane getPair(){
		return pair;
	}
	public JzFilerFileList getList(){
		return list;
	}
	//ステータスの表示
	public void PrintNowDir(){
		if(list!=null){
			UFile dir=list.GetCurrentDir();
			String m0="";
			String m1="";
			if(dir!=null){
				//m0=dir.toString();
				m0=dir.getLocalFilename();
				long free=dir.getFreeSpace();
				UFile[] files=dir.listFiles();
				int files_len=files.length;
				m1=String.format("files(%d) / free(%,dbyte)",files_len,free);
			}
			PrintStatus(0,m0);
			PrintStatus(1,m1);
		}
	}
	//ステータスの表示
	public void PrintStatus(int i,String m){
		if(i==0){
			status1.setText(m);
		}else{
			status2.setText(m);
		}
	}
	public UFile GetCurrentFile(){
		return list.getSelectFile();
	}
	public boolean SetCurrentDir(UFile dir){
		return list.setCurrentDir(dir);
	}
	public UFile GetCurrentDir(){
		return list.GetCurrentDir();
	}
	public void SelectAll(){
		list.SelectAll();
	}
	public void SelectAllFile(){
		list.SelectAllFile();
	}
	public void SelectFlip(){
		list.SelectFlip();
	}
	public void UnSelect(){
		list.UnSelect();
	}
	public List<UFile> GetSelectFileList(){
		return list.GetSelectFileList();
	}
	public void Update(){
		list.Update();
		PrintNowDir();
	}
	public UFile getSelectFile(){
		return list.getSelectFile();
	}
	public void addChangeListener(ChangeListener listener){
		list.addChangeListener(listener);
	}
	public UFile[] GetFiles(){
		return list.GetFiles();
	}
	public void SetSelectFiles(UFile[] file,boolean[] flg){
		list.SetSelectFiles(file,flg);
	}
	public void SelectDrive(){
		list.SelectDrive();
	}
	public void ChangeDirParent(){
		list.ChangeDirParent();
	}
}

