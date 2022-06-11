/******************************************************************************
;	ログ出力タスク
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.util.List;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.SwingWorker;
import javax.swing.JComponent;
import javax.swing.InputMap;

import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

public class PrintLogTask extends SwingWorker<String, String> implements KeyListener {
	public interface Callback{
		public InputMap getInputMap();
		public void SetExecCmdFlg(boolean n);
		public void AddTextBottom(String m);
		public void SetKeyListener(KeyListener listener);
		public void RemoveKeyListener(KeyListener listener);
		public void SetCaretPosBottom();
		public void PrintCommandLine();
		public String ExecCommandWait(String[] _cmds,String current_dir,DosExecOutputListener output_listener,DosExecInputListener input_listener,DosExecAboutListener about_listener) throws Exception;
	}
	//
	private Callback callback;
	private volatile boolean break_flg=false;
	private JComponent parent;
	private BackupInputMap bim;
	private String[] cmds;
	private String current_dir;
	private volatile int publish_cnt;
	private volatile boolean done_flg;
	private volatile boolean end_proc_flg;
	private StringBuffer input_buff=new StringBuffer();
	
	public PrintLogTask(String[] _cmds,String _current_dir,Callback callback){
		this.callback=callback;
		break_flg=false;
		bim=new BackupInputMap(GetInputMap());
		cmds=_cmds;
		current_dir=_current_dir;
		publish_cnt=0;
		done_flg=false;
		end_proc_flg=false;
	}
	//=========================
	InputMap GetInputMap(){
		if(callback==null)return null;
		return callback.getInputMap();
	}
	void SetExecCmdFlg(boolean n){
		if(callback==null)return;
		callback.SetExecCmdFlg(n);
	}
	void AddTextBottom(String m){
		if(callback==null)return;
		callback.AddTextBottom(m);
	}
	void SetKeyListener(){
		if(callback==null)return;
		callback.SetKeyListener(this);
	}
	void RemoveKeyListener(){
		if(callback==null)return;
		callback.RemoveKeyListener(this);
	}
	void SetCaretPosBottom(){
		if(callback==null)return;
		callback.SetCaretPosBottom();
	}
	void PrintCommandLine(){
		if(callback==null)return;
		callback.PrintCommandLine();
	}
	String ExecCommandWait(String[] _cmds,String current_dir,DosExecOutputListener output_listener,DosExecInputListener input_listener,DosExecAboutListener about_listener) throws Exception {
		if(callback==null)return null;
		return callback.ExecCommandWait(cmds,current_dir,output_listener,input_listener,about_listener);
	}
	//=========================
	@Override
	public String doInBackground() {
		//出力
		DosExecOutputListener output_listener=new DosExecOutputListener(){
			public void OutputMessage(String m){
				publish_cnt++;
				publish(m);
			}
		};
		//入力
		DosExecInputListener input_listener=new DosExecInputListener(){
			public boolean HasInput(){
				if(input_buff.length()==0)return false;
				return true;
			}
			public String InputMessage(){
				if(input_buff.length()==0)return null;
				return input_buff.toString();
			}
			public void ClearInput(){
				if(input_buff.length()==0)return;
				input_buff.delete(0,input_buff.length());
			}
		};
		//About
		DosExecAboutListener about_listener=new DosExecAboutListener(){
			synchronized public boolean CheckAbout(){
				//if(break_flg)System.out.println("break_flg:"+break_flg);
				return break_flg;
			}
		};
		//CTRL用
		SetKeyListener();
		try{
//System.out.println("exec");
			String ret_m=ExecCommandWait(cmds,current_dir,output_listener,input_listener,about_listener);
			if(ret_m!=null){
				//return ret_m[2];
				return ret_m;
			}
		}catch(Exception ex){
		}
		return null;
	}
	@Override
	synchronized protected void process(List<String> ms) {
		//ブレークされたらメッセージが来ても無視
		if(break_flg){
			//System.out.println("すでにbreakされている!!");
			return;
		}
		//テキスト追加
		for(String m : ms) {
			AddTextBottom(m);
			publish_cnt--;
		}
		SetCaretPosBottom();
		if(publish_cnt==0 && done_flg){
			EndProc();
		}
	}
	@Override
	protected void done() {
//System.out.println("done:"+publish_cnt);
		done_flg=true;
		if(publish_cnt==0 || break_flg){
			EndProc();
		}
	}
	public void EndProc(){
		if(end_proc_flg)return;
		end_proc_flg=true;
		AddTextBottom("\n");
//System.out.println("EndProc");
		RemoveKeyListener();
		//
		SetExecCmdFlg(false);
		PrintCommandLine();
		bim.Undo();
	}
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	synchronized public void keyPressed(KeyEvent e){
		//CTRL+C
		if(e.getKeyCode()==KeyEvent.VK_C && e.isControlDown() && !break_flg){
			//System.out.println("CTRL+C : "+System.nanoTime());
			break_flg=true;
		}else{
			input_buff.append((char)e.getKeyChar());
//System.out.println("Input:"+(char)e.getKeyChar());
		}
	}
}

