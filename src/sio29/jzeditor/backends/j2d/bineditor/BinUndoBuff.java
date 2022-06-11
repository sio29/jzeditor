/******************************************************************************
;	バイナリエディタUndo
******************************************************************************/
package sio29.jzeditor.backends.j2d.bineditor;

import java.util.ArrayList;


public class BinUndoBuff{
	private ArrayList<BinUndoAct> buff=new ArrayList<BinUndoAct>();
	private int index=0;
	//
	public BinUndoBuff(){}
	public void Clear(){
		buff.clear();
		index=-1;
	}
	public boolean CanUndo(){
		if(buff.size()==0)return false;
		if(index<0)return false;
		return true;
	}
	public boolean CanRedo(){
		if(buff.size()==0)return false;
		if(index>=(buff.size()-1))return false;
		return true;
	}
	public boolean Undo(){
		if(!CanUndo())return false;
		buff.get(index).undo();
		index--;
		return true;
	}
	public boolean Redo(){
		if(!CanRedo())return false;
		index++;
		buff.get(index).redo();
		return true;
	}
	public void AddUndoAct(BinUndoAct act){
		if(buff.size()>0 && index>=0){
			for(int i=buff.size()-1;i>=(index+1);i--){
				buff.remove(i);
			}
		}
		index=buff.size();
		buff.add(act);
	}
}
