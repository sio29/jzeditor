/******************************************************************************
;	ノーマルテキスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.JComponent;

import sio29.jzeditor.backends.j2d.textcomponenttool.*;


//public class NormalTextUndoManagerImpl extends UndoManager implements NormalTextUndoManager{
public class NormalTextUndoManagerImpl implements NormalTextUndoManager{
	public interface Callback{
		public int getCaretPosition();
		public void setCaretPosition(int n);
		public void addUndoableEditListener(UndoableEditListener n);
	};
	//
	private Callback callback;
	private int undo_enable=0;
	UndoManager um;
	//
	public NormalTextUndoManagerImpl(final JComponent _textArea){
		Callback cb=new NormalTextUndoManagerImpl.Callback(){
			public int getCaretPosition(){
				JComponent textArea=_textArea;
				if(textArea==null)return 0;
				return TextComponentTool.getCaretPosition(textArea);
			}
			public void setCaretPosition(int n){
				JComponent textArea=_textArea;
				if(textArea==null)return;
				TextComponentTool.setCaretPosition(textArea,n);
			}
			public void addUndoableEditListener(UndoableEditListener n){
				JComponent textArea=_textArea;
				if(textArea==null)return;
				TextComponentTool.addUndoableEditListener(textArea,n);
			}
		};
		this.callback=cb;
		um=new UndoManagerImpl(callback);
		callback.addUndoableEditListener(um);
		
	}
	public NormalTextUndoManagerImpl(Callback callback){
		this.callback=callback;
		um=new UndoManagerImpl(callback);
		callback.addUndoableEditListener(um);
		
		//InitUndoSub();
	}
	private int getCaretPosition(){
		return callback.getCaretPosition();
	}
	private void setCaretPosition(int n){
		callback.setCaretPosition(n);
	}
	/*
	private void addUndoableEditListener(UndoableEditListener n){
		//callback.addUndoableEditListener(n);
		//callback.addUndoableEditListener(um);
	}
	*/
	
	/*
	public void undo(){
		while(true){
			if(!um.canUndo())break;
			UndoableEdit _edit=um.editToBeUndone();
			if(!(_edit instanceof AbstractDocument.DefaultDocumentEvent)){
				super.undo();
				return;
			}
			AbstractDocument.DefaultDocumentEvent edit=(AbstractDocument.DefaultDocumentEvent)_edit;
			//
			DocumentEvent.EventType type=edit.getType();
			//System.out.println("undo:"+edit.getClass().getName()+":type("+type+")");
			super.undo();
			if(type!=DocumentEvent.EventType.CHANGE)break;
		}
	}
	public void redo(){
		int pos=0;
		boolean flg=false;
		while(true){
			if(!um.canRedo())break;
			UndoableEdit _edit=um.editToBeRedone() ;
			if(!(_edit instanceof AbstractDocument.DefaultDocumentEvent)){
				super.redo();
				return;
			}
			AbstractDocument.DefaultDocumentEvent edit=(AbstractDocument.DefaultDocumentEvent)_edit;
			//
			DocumentEvent.EventType type=edit.getType();
			if(flg && type!=DocumentEvent.EventType.CHANGE)break;
			//System.out.println("redo:"+edit.getClass().getName()+":type("+type+")");
			super.redo();
			if(type!=DocumentEvent.EventType.CHANGE){
				flg=true;
				//insert/removeの時のキャレット位置を保持
				pos=getCaretPosition();
			}
		}
		//insert/removeの時のキャレット位置に戻す
		setCaretPosition(pos);
	}
	*/
	static class UndoManagerImpl extends UndoManager{
		Callback callback;
		UndoManagerImpl(Callback _callback ){
			callback =_callback ;
		}
		public void undo(){
			while(true){
				if(!canUndo())break;
				UndoableEdit _edit=editToBeUndone();
				if(!(_edit instanceof AbstractDocument.DefaultDocumentEvent)){
					super.undo();
					return;
				}
				AbstractDocument.DefaultDocumentEvent edit=(AbstractDocument.DefaultDocumentEvent)_edit;
				//
				DocumentEvent.EventType type=edit.getType();
				//System.out.println("undo:"+edit.getClass().getName()+":type("+type+")");
				super.undo();
				if(type!=DocumentEvent.EventType.CHANGE)break;
			}
		}
		public void redo(){
			int pos=0;
			boolean flg=false;
			while(true){
				if(!canRedo())break;
				UndoableEdit _edit=editToBeRedone() ;
				if(!(_edit instanceof AbstractDocument.DefaultDocumentEvent)){
					super.redo();
					return;
				}
				AbstractDocument.DefaultDocumentEvent edit=(AbstractDocument.DefaultDocumentEvent)_edit;
				//
				DocumentEvent.EventType type=edit.getType();
				if(flg && type!=DocumentEvent.EventType.CHANGE)break;
				//System.out.println("redo:"+edit.getClass().getName()+":type("+type+")");
				super.redo();
				if(type!=DocumentEvent.EventType.CHANGE){
					flg=true;
					//insert/removeの時のキャレット位置を保持
					pos=callback.getCaretPosition();
				}
			}
			//insert/removeの時のキャレット位置に戻す
			callback.setCaretPosition(pos);
		}
		
	}
	
	
	/*
	private void InitUndoSub(){
		//addUndoableEditListener(this);
		callback.addUndoableEditListener(um);
	}
	*/
	public void DisableUndo(){
		undo_enable++;
	}
	public void EnableUndo(){
		undo_enable--;
	}
	public void ClearUndoBuff(){
		um.discardAllEdits() ;
	}
	public boolean CanUndo(){
		return um.canUndo();
	}
	public boolean CanRedo(){
		return um.canRedo();
	}
	public void ExecUndo(){
		if(um.canUndo()){
			um.undo();
		}
	}
	public void ExecRedo(){
		if(um.canRedo()){
			um.redo();
		}
	}
}

