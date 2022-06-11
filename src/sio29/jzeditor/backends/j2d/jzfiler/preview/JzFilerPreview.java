/******************************************************************************
;	Vzファイラー、プレビューウィンドウ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview;

import java.util.ArrayList;
import java.util.*;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.BoxLayout;
import java.awt.*;
import javax.swing.*;

import sio29.ulib.ufile.*;

import sio29.jzeditor.backends.j2d.jzfiler.preview.ext.*;


//====================
//ビュワー作成リスト
class JzFilerPreviewViwerCreaters{
	private ArrayList<JzFilerPreviewViwerCreater> list=new ArrayList<JzFilerPreviewViwerCreater>();
	//ビュワー作成クラス追加
	public void add(JzFilerPreviewViwerCreater[] creaters){
		for(int i=0;i<creaters.length;i++){
			list.add(creaters[i]);
		}
	}
	//ビュワー作成クラス追加
	public void add(JzFilerPreviewViwerCreater creater){
		list.add(creater);
	}
	//ビュワー作成クラス獲得
	public JzFilerPreviewViwerCreater getCreater(UFile filename){
		if(filename==null)return null;
		for(int i=0;i<list.size();i++){
			JzFilerPreviewViwerCreater creater=list.get(i);
			if(creater.isSupport(filename))return creater;
		}
		return null;
	}
	public int size(){
		return list.size();
	}
	public JzFilerPreviewViwerCreater get(int i){
		return list.get(i);
	}
}
//====================
//プレビュー
public class JzFilerPreview extends Container{
//public class JzFilerPreview extends JScrollPane{
//	private static boolean USE_THREAD=false;	//スレッドを使わない
	private static boolean USE_THREAD=true;		//スレッドを使う
	private JScrollPane prev_text_scroll;		//プレビュースクロール
	private JzFilerPreviewViwerCreaters creaters;
	private CardLayout card;
	//private JComponent view;
	private Container view;
	private HashMap<String,JzFilerPreviewViwerDispoeable> cardmap=new HashMap<String,JzFilerPreviewViwerDispoeable>();
	private ArrayList<LoadFileTaskWorker> tasks=new ArrayList<LoadFileTaskWorker>();
	//
	public JzFilerPreview(){
		creaters=new JzFilerPreviewViwerCreaters();
		addPreviewCreaters();
		InitPreview();
	}
	public void addPreviewCreaters(){
		creaters.add(new JzFilerPreviewCreaterText());
		creaters.add(new JzFilerPreviewCreaterImage());
		creaters.add(new JzFilerPreviewCreaterAudio());
		creaters.add(new JzFilerPreviewCreaterBinary());
	}
	//=============================
	//プレビュー
	public void InitPreview(){
		//Dimension size=getPreferredSize();
		//System.out.println(""+size);
		
		prev_text_scroll=new JScrollPane();
		//prev_text_scroll=this;
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		add(prev_text_scroll);
		//
		//JPanel _view=new JPanel();
		Container _view=new Container();
		view=_view;
		prev_text_scroll.setViewportView(_view);
		card = new CardLayout();
		view.setLayout(card);
		//
		{
			String card_name="none";
			JLabel c=new JLabel("none");
			view.add(card_name,c);
		}
		for(int i=0;i<creaters.size();i++){
			JzFilerPreviewViwerCreater creater=creaters.get(i);
			JzFilerPreviewViwerDispoeable c=creater.createViewer();
			if(c!=null){
				String card_name=creater.getClass().getName();
				view.add(card_name,c.getComponent());
				cardmap.put(card_name,c);
			}
		}
		card.show(view,"none");
		
		//size=getPreferredSize();
		//System.out.println(""+size);
	}
	public void dispose(){
		disposeAllCard();
	}
	public void disposeAllCard(){
		for(String name : cardmap.keySet()){
			JzFilerPreviewViwerDispoeable c=cardmap.get(name);
			if(c!=null){
				c.dispose();
			}
		}
	}
	JzFilerPreviewViwerCreater getViewerCreater(UFile filename){
		return creaters.getCreater(filename);
	}
	void changeCard(UFile filename){
		JzFilerPreviewViwerCreater creater=creaters.getCreater(filename);
		if(creater!=null){
			String card_name=creater.getClass().getName();
			card.show(view,card_name);
		}
	}
	void setCardData(UFile filename,Object data){
		JzFilerPreviewViwerCreater creater=creaters.getCreater(filename);
		if(creater!=null){
			String card_name=creater.getClass().getName();
			JzFilerPreviewViwerDispoeable c=cardmap.get(card_name);
			if(c!=null){
				if(data!=null){
					c.setData(filename,data);
				}
			}
		}
	}
	void clearAllData(){
		for(String name : cardmap.keySet()){
			JzFilerPreviewViwerDispoeable c=cardmap.get(name);
			if(c!=null){
				c.clearData();
			}
		}
	}
	void disposeData(UFile filename,Object data){
		JzFilerPreviewViwerCreater creater=creaters.getCreater(filename);
		if(creater==null)return;
		creater.disposeData(filename,data);
	}
	Object loadDataSub(UFile filename){
		JzFilerPreviewViwerCreater creater=creaters.getCreater(filename);
		if(creater==null)return null;
		JzFilerPreviewDataLoader loader=creater.createDataLoader(filename);
		if(loader==null)return null;
		if(!loader.load(filename))return null;
		return loader.getData();
	}
	public Container getContainer(){
		return this;
	}
	//プレビューの読み込み&表示
	public void SetPreview(UFile filename){
		clearAllData();
		changeCard(filename);
		//
		JzFilerPreviewViwerCreater creater=creaters.getCreater(filename);
		if(creater==null)return;
		//
		if(!USE_THREAD){
		//ブロック
			SetPreviewBlock(filename);
		}else{
		//スレッド
			SetPreviewTask(filename);
		}
	}
	//プレビューの読み込み&表示をブロック実行
	public void SetPreviewBlock(UFile filename){
		Object data=loadDataSub(filename);
		if(data!=null){
			setCardData(filename,data);
		}
	}
	public void SetPreviewTask(UFile filename){
		disposeAllTsak();
		//
		LoadFileTaskWorker task=new LoadFileTaskWorker(this,filename);
		task.execute();
		tasks.add(task);
	}
	void disposeAllTsak(){
		for(int i=tasks.size()-1;i>=0;i--){
			LoadFileTaskWorker task=tasks.get(i);
			if(task.isDone()){
System.out.println("remove:"+task.filename);
				tasks.remove(i);
			}
		}
		for(int i=0;i<tasks.size();i++){
			LoadFileTaskWorker task=tasks.get(i);
			task.dispose();
		}
		//tasks.clear();
	}
	//データの読み込みワーカー
	static class LoadFileTaskWorker extends SwingWorker<Object, Object> {
		public JzFilerPreview p_preview;
		public volatile UFile filename;
		private volatile Object data;
		private volatile boolean dispose_flg=false;
		public LoadFileTaskWorker(JzFilerPreview p_preview,UFile filename){
			this.p_preview=p_preview;
			this.filename=filename;
			data=null;
		}
		// 非同期に行われる処理
		@Override
		public Object doInBackground() {
			try{
				data=p_preview.loadDataSub(filename);
			}catch(Exception ex) {}
			return null;
		}
		// 非同期処理後に実行
		@Override
		protected void done() {
			if(!dispose_flg){
				p_preview.clearAllData();
				p_preview.changeCard(filename);
				JzFilerPreviewViwerCreater creater=p_preview.getViewerCreater(filename);
				if(creater instanceof JzFilerPreviewCreaterAudio){
					System.out.println("setCardData:"+filename);
				}
				p_preview.setCardData(filename,data);
			}else{
				JzFilerPreviewViwerCreater creater=p_preview.getViewerCreater(filename);
				if(creater instanceof JzFilerPreviewCreaterAudio){
					System.out.println("disposeData:"+filename);
				}
				p_preview.disposeData(filename,data);
			}
		}
		public void dispose(){
			if(dispose_flg)return;
			JzFilerPreviewViwerCreater creater=p_preview.getViewerCreater(filename);
			if(creater instanceof JzFilerPreviewCreaterAudio){
				//System.out.println("LoadFileTaskWorker::dispose():"+filename);
			}
			dispose_flg=true;
		}
	}
	
	
}
