/******************************************************************************
;	Vzファイラー、ペアのリスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.ArrayList;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class JzFilerPairFileList {
	private JzFilerDialog p_filer;
	private JzFilerFileListPane list1;				//リスト1
	private JzFilerFileListPane list2;				//リスト2
	private int current_list=0;						//カレントリスト
	private JSplitPane list_sp;						//左右リストスプリッタ
	private float list_sp_weight=0.5f;				//左右の分割割合
	private ArrayList<ChangeListener> listener=new ArrayList<ChangeListener>();
	
	public JzFilerPairFileList(JzFilerDialog p_filer,UFile[] current_dirs,UFileCurrentDrives[] drive_currents,float list_sp_weight){
		this.p_filer=p_filer;
		InitPairFileList(current_dirs,drive_currents,list_sp_weight);
	}
	public void InitPairFileList(UFile[] current_dir,UFileCurrentDrives[] drive_currents,float list_sp_weight){
		this.list_sp_weight=list_sp_weight;
		list1=new JzFilerFileListPane(p_filer,current_dir[0],drive_currents[0]);
		list2=new JzFilerFileListPane(p_filer,current_dir[1],drive_currents[1]);
		list1.setPair(list2);
		list2.setPair(list1);
		list1.getList().addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
				current_list=0;
			}
			public void focusLost(FocusEvent e){}
		});
		list2.getList().addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
				current_list=1;
			}
			public void focusLost(FocusEvent e){}
		});
		list_sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,list1, list2);
		list_sp.setResizeWeight(list_sp_weight);
		/*
		list_sp.addComponentListener(new ComponentAdapter(){
			public void componentMoved(ComponentEvent e){
				double weight=list_sp.getResizeWeight();
				System.out.println(""+weight);
			}
		});
		*/
		list_sp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				/*
				double weight=list_sp.getResizeWeight();
				System.out.println(""+weight);
				*/
				/*
				JSplitPane sourceSplitPane = (JSplitPane)pce.getSource();
				String propertyName = pce.getPropertyName();
				if(propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
					int current = sourceSplitPane.getDividerLocation();
					System.out.println("Current: " + current);
					Integer last = (Integer)pce.getNewValue();
					System.out.println("Last: " + last);
					Integer priorLast = (Integer)pce.getOldValue();
					System.out.println("Prior last: " + priorLast);
				}
				*/
				/*
					Integer last = (Integer)pce.getNewValue();
					System.out.println("Last: " + last);
					Integer priorLast = (Integer)pce.getOldValue();
					System.out.println("Prior last: " + priorLast);
				*/
				/*
					int last = (Integer)pce.getNewValue();
					int div_size=list_sp.getDividerSize();
					System.out.println("Last: " + last+","+div_size);
				*/
					int l=list_sp.getLeftComponent().getWidth();
					int r=list_sp.getRightComponent().getWidth();
					int lr=l+r;
					float per=(float)l/(float)lr;
				//	System.out.println(""+per);
					sendChangeListener(new JzFilerChangeEvent(this,JzFilerChangeEvent.SEPARATOR,per));
			}
		});
	}
	public void UpdateAttr(){
		list1.UpdateAttr();
		list2.UpdateAttr();
	}
	public void addChangeListener(ChangeListener n){
		listener.add(n);
		list1.addChangeListener(n);
		list2.addChangeListener(n);
	}
	private void sendChangeListener(ChangeEvent ev){
		for(int i=0;i<listener.size();i++){
			ChangeListener l=listener.get(i);
			if(l==null)continue;
			l.stateChanged(ev);
		}
	}
	public JComponent GetPairFileList(){
		return list_sp;
	}
	public JzFilerFileListPane GetList(int i){
		if(i==0){
			return list1;
		}else{
			return list2;
		}
	}
	public void SetSelectFiles(int i,UFile[] file,boolean[] flg){
		JzFilerFileListPane list=GetList(i);
		list.SetSelectFiles(file,flg);
	}
	public JzFilerFileListPane GetCurrentList(){
		return GetList(current_list);
	}
	public JzFilerFileListPane GetDstList(){
		return GetList(current_list ^ 1);
	}
	public void OnUpdate(){
		if(list1!=null)list1.Update();
		if(list2!=null)list2.Update();
	}
	public float GetPairListWeight(){
		return list_sp_weight;
	}
	public void SetPairListWeight(float n){
		list_sp_weight=n;
	}
	public UFile[] GetFiles(int i){
		JzFilerFileListPane list=GetList(i);
		return list.GetFiles();
	}
	public void SelectDrive(){
		JzFilerFileListPane list=GetCurrentList();
		list.SelectDrive();
	}
	public void ChangeDirParent(){
		JzFilerFileListPane list=GetCurrentList();
		list.ChangeDirParent();
	}
}
