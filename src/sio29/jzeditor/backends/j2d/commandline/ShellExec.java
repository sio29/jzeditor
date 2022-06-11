/******************************************************************************
;	Shellコマンドの実行
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ShellExec{
	//============================================
	//メッセージ出力
	//============================================
	public static class OutputMessageTask extends Thread{
		private final String LINE_SEPA = System.getProperty("line.separator");
		private String type_m;
		private volatile BufferedReader br;
		private volatile BufferedReader _br;
		private volatile boolean break_flg=false;
		private volatile InputStream _is;
		private DosExecOutputListener output_listener;
		//
		public OutputMessageTask(final DosExecOutputListener output_listener,InputStream is,String _type_m,String char_code){
			init(output_listener,is,_type_m,char_code);
		}
		public OutputMessageTask(final DosExecOutputListener output_listener,InputStream is,String _type_m){
			init(output_listener,is,_type_m,null);
		}
		public void init(final DosExecOutputListener output_listener,InputStream is,String _type_m,String char_code){
			this.output_listener=output_listener;
			_is=is;
			if(char_code==null){
				br=new BufferedReader(new InputStreamReader(is));
			}else{
				try{
				br=new BufferedReader(new InputStreamReader(is,char_code));
				}catch(Exception ex){}
			}
			_br=br;			//※クーローズ用
			break_flg=false;
			type_m=_type_m;
		}
		public void run() {
			try{
				while(true){
					if(break_flg){
						break;
					}
					//String line = br.readLine();
					//if(line == null)break;
					//line+=LINE_SEPA;
					int c = br.read();
					//int c=_is.read();
					//if(c>=0x01 && c<0x20){
					//	System.out.println(String.format("ESC(%02x)",c));
					//}
					if(c<0)break;
					String line=String.valueOf((char)c);
					if(break_flg){
						break;
					}
					//
					
					
					output_listener.OutputMessage(line);
					if(break_flg){
						break;
					}
				}
			}catch(IOException e){
				System.out.println("Output Error!!");
				//※br=nullでここに飛んでくる
				//System.out.println("IOException");
				//throw new RuntimeException(e);
			}finally{
				try{
					_br.close();
				}catch (IOException e){
				}
			}
		}
		synchronized public void setBreak() {
			break_flg=true;
			br=null;
		}
	}
	//============================================
	//メッセージ入力
	//※標準入力はまだ動作していない!!
	//============================================
	public static class InputMessageTask extends Thread{
		private String type_m;
		private volatile BufferedWriter bw;
		private volatile BufferedWriter _bw;
		private volatile boolean break_flg=false;
		private final DosExecOutputListener output_listener;
		private final DosExecInputListener input_listener;
		//
		public InputMessageTask(final DosExecOutputListener output_listener,final DosExecInputListener input_listener,OutputStream os,String _type_m){
			this.output_listener=output_listener;
			this.input_listener =input_listener;
			bw=new BufferedWriter(new OutputStreamWriter(os));
			_bw=bw;			//※クーローズ用
			break_flg=false;
			type_m=_type_m;
		}
		public void run() {
			try{
				Scanner scan = new Scanner(System.in);
				while(true){
					if(break_flg){
						break;
					}
					//if(!input_listener.HasInput()){
					//	//try{
					//	//Thread.sleep(1);
					//	//}catch(Exception ex){}
					//	continue;
					//}
//System.out.println("きた!!");
					//String line = bw.readLine();
					//if(line == null)break;
					//line+=LINE_SEPA;
					//String line=input_listener.InputMessage();
					//if(line==null)line="";
					
					String line=scan.nextLine()+"\n";
					//String line="[Test]\n";
					//String line="_";
					bw.write(line);
					bw.flush();
					input_listener.ClearInput();
					//
					output_listener.OutputMessage(line);
					
					if(break_flg){
						break;
					}
				}
			}catch(IOException e){
				System.out.println("Input Error!!");
			}finally{
				try{
					_bw.close();
				}catch (IOException e){
				}
			}
			
		}
		synchronized public void setBreak() {
			break_flg=true;
			bw=null;
		}
	}
	//============================================
	//文字列が内部コマンドを含むかチェック
	//============================================
	public static String CheckInnerCmd(String[] inner_cmd,String m){
		int src_len=m.length();
		for(int i=0;i<inner_cmd.length;i++){
			String cmd=inner_cmd[i];
			if(cmd==null)continue;
			int len=cmd.length();
			if(src_len<len)continue;
			String new_m=m.substring(0,len);
			if(cmd.equalsIgnoreCase(new_m)){
				return cmd;
			}
		}
		return null;
	}
	//============================================
	//シェル実行
	//ウェイトなし
	//============================================
	public static void ExecDosCommandNoWait_Sub(String[] cmds,String current_dir) throws IOException,InterruptedException {
		if(cmds==null)return;
		Runtime r = Runtime.getRuntime();
		if(r==null)return;
		File file=null;
		try{
			file=new File(current_dir);
		}catch(Exception ex){
			file=null;
		}
		if(file==null){
			r.exec(cmds);
		}else{
			r.exec(cmds,null,file);
		}
	}
	//============================================
	//シェル実行
	//ウェイトあり
	//============================================
	public static String ExecDosCommandWait_Sub(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener) throws IOException,InterruptedException {
		return ExecDosCommandWait_Sub(cmds,current_dir,output_listener,input_listener,about_listener,null);
	}
	public static String ExecDosCommandWait_Sub(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener,String char_code) throws IOException,InterruptedException {
		if(cmds==null)return null;
		Runtime r = Runtime.getRuntime();
		if(r==null)return null;
		File file=null;
		try{
			file=new File(current_dir);
		}catch(Exception ex){
			file=null;
		}
//for(int i=0;i<cmds.length;i++)System.out.println("cmds["+i+"]:"+cmds[i]);
		Process _p;
		try{
			if(file==null){
				_p=r.exec(cmds);
			}else{
				_p=r.exec(cmds,null,file);
			}
		}catch(Exception ex){
			String err_mess=String.format("\'%s\' は、内部コマンドまたは外部コマンド、\n操作可能なプログラムまたはバッチ ファイルとして認識されていません。",cmds[0]);
			output_listener.OutputMessage(err_mess);
			return null;
		}
		final Process p=_p;
		//
		OutputMessageTask it = new OutputMessageTask(output_listener,p.getInputStream(),"InputStream",char_code);
		OutputMessageTask et = new OutputMessageTask(output_listener,p.getErrorStream(),"ErrorStream",char_code);
		InputMessageTask  ot = new InputMessageTask(output_listener,input_listener,p.getOutputStream(),"OutputStream");
		it.start();
		et.start();
		ot.start();
		//プロセスの終了待ち
		int exit_code=0;
		boolean about_flg=false;	//強制終了した?
		while(true){
			try{
				//終わっていれば終了コードが獲得できる
				//※終了コードが獲得できたから言ってスレッドがすべて終了したわけではない
				exit_code=p.exitValue();
				//System.out.println("exit_code:"+exit_code);
				break;
			}catch(Exception ex){
			//まだ稼動中
			}
			//スレッド待ち(※ここで待つとCTRL+Cが効かない)
//			p.waitFor();
			//
			

			//CTRL+C待ち
			if(about_listener!=null){
				if(about_listener.CheckAbout()){
System.out.println("pre:Break(CTRL+C)");
					p.getOutputStream().write(0x3);
					p.getOutputStream().flush();
//					p.waitFor();
//					p.destroy();
System.out.println("post:Break(CTRL+C)");
					
					p.destroy();
					//p.waitFor();
					//
					it.setBreak();
					et.setBreak();
					ot.setBreak();
					about_flg=true;
					
System.out.println("end:Break(CTRL+C)");
					break;
				}
			}

		}
//		if(!about_flg){
			//スレッド待ち(※きちんと待たないとログがきちんと出力されない)
			p.waitFor();
//		}
		
		
		//InputStreamのスレッド終了待ち
//		it.join();
//		et.join();
//		pp.join();
		String[] returns = new String[3];
		//returns[2] = Integer.toString(exit_code);
		//System.out.println("DosExec End...: "+System.nanoTime());
		//return returns;
		//return exit_code;
		return Integer.toString(exit_code);
	}
}


/******************************************************************************
;	InputStreamを読み込むスレッド
;	※未使用
******************************************************************************/
class InputStreamThread extends Thread {
	private BufferedReader br;
	private List<String> list = new ArrayList<String>();
	// コンストラクター 
	public InputStreamThread(InputStream is) {
		br = new BufferedReader(new InputStreamReader(is));
	}
	// コンストラクター 
	public InputStreamThread(InputStream is, String charset) {
		try {
			br = new BufferedReader(new InputStreamReader(is, charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void run() {
		try {
			for (;;) {
				String line = br.readLine();
				if (line == null) 	break;
				list.add(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}
	// 文字列取得 
	public List<String> getStringList() {
		return list;
	}
}

/******************************************************************************
; Sends CTRL-C to running processes from Java (in Windows)
; and ca get ProcessID(s) for a given process name.
; IMPORTANT!
; This function NEEDS SendSignalC.exe in the ext\ subdirectory.
; @author Kai Goergen
;	※未使用
******************************************************************************/
class SendCTRLC{
	/**
	 * Get all PIDs for a given name and send CTRL-C to all
	 * @param processName
	 * @return
	 */
	public static List<String> sendCTRLC(String processName) {
		// get all ProcessIDs for the processName
		List<String> processIDs = getProcessIDs(processName);
		System.out.println("" + processIDs.size() + " PIDs found for " + processName + ": " + processIDs.toString());
		for (String pid : processIDs) {
			// close it
			sendCTRLC(Integer.parseInt(pid));
		}
		return processIDs;
	}
	/**
	 * Send CTRL-C to the process using a given PID
	 * @param processID
	 */
	public static void sendCTRLC(int processID) {
		System.out.println(" Sending CTRL+C to PID " + processID);
		try {
			Process p = Runtime.getRuntime().exec("cmd /c ext\\SendSignalC.exe " + processID);
			//StreamGobbler.StreamGobblerLOGProcess(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get List of PIDs for a given process name
	 * @param processName
	 * @return
	 */
	public static List<String> getProcessIDs(String processName) {
		List<String> processIDs = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec("tasklist /v /fo csv");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (!line.trim().equals("")) {
					// Pid is after the 1st ", thus it's argument 3 after splitting
					String currentProcessName = line.split("\"")[1];
					// Pid is after the 3rd ", thus it's argument 3 after splitting
					String currentPID = line.split("\"")[3];
					if (currentProcessName.equalsIgnoreCase(processName)) {
						processIDs.add(currentPID);
					}
				}
			}
			input.close();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
		return processIDs;
	}
}
