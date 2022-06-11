/******************************************************************************
;	SSHの実行
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline.ssh;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.trilead.ssh2.*;

import sio29.ulib.ufile.*;
import sio29.jzeditor.backends.j2d.commandline.*;

public class SSHExec implements ShellExecCommandBase{
	public interface ShellExecParam{
		public String getHost();
		public String getUser();
		public String getPassword();
	}
	public static ShellExecParam g_def_param=new ShellExecParam(){
		public String getHost(){return "sio29.sakura.ne.jp";}
		public String getUser(){return "sio29";}
		public String getPassword(){return "vff6mtx5wp";}
	};
	
	
	private ShellExecParam g_param;
	//
	public SSHExec(ShellExecParam param){
		g_param=param;
	}
	public ShellExecParam getParam(){
		return g_param;
	}
	
	public String ExecDosCommandWait(String[] cmds,String current_dir,final DosExecOutputListener output_listener,final DosExecInputListener input_listener,final DosExecAboutListener about_listener) throws IOException,InterruptedException {
		ShellExecParam param=getParam();
		SSHExec test=this;
		if(sshSession==null){
		test.init(param.getHost(),param.getUser(),param.getPassword());
		
		if(!test.open()){
			System.out.println("open error!!");
			return "";
		}
		if(!test.openSession()){
			System.out.println("openSession error!!");
			return "";
		}
		OutputStream stdin =test.sshSession.getStdin();
		InputStream  stdout=test.sshSession.getStdout();
		InputStream  stderr=test.sshSession.getStderr();
		
		ShellExec.OutputMessageTask it = new ShellExec.OutputMessageTask(output_listener,stdout,"InputStream");
		ShellExec.OutputMessageTask et = new ShellExec.OutputMessageTask(output_listener,stderr,"ErrorStream");
		ShellExec.InputMessageTask  ot = new ShellExec.InputMessageTask(output_listener,input_listener,stdin,"OutputStream");
		it.start();
		et.start();
		ot.start();
		
		sshSession.requestPTY("vt100", 0, 0, 0, 0, null);//コマンドライン表示
		
		// コマンドの実行
		sshSession.startShell();
		}
		
		//test.sshSession.startShell();
		
//		try{
//			test.sshSession.execCommand(cmds[0]);
//			
//			OutputStream stdin =test.sshSession.getStdin();
//			InputStream  stdout=test.sshSession.getStdout();
//			InputStream  stderr=test.sshSession.getStderr();
//		
//			byte[] buffer = new byte[1024];	
//			int len = stdout.read(buffer);
//			if( len > 0 ) {
//				output_listener.OutputMessage(new String(buffer,"UTF-8"));
//			}
//		
//		
//		} catch(IOException e) {
//			e.printStackTrace(System.err);
//		}
		
		{
		OutputStream stdin =test.sshSession.getStdin();
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(stdin));
		String m="";
		for(int i=0;i<cmds.length;i++){
			bw.write(cmds[i]);
			m+=" "+cmds[i];
		}
		bw.write("\n");
		bw.flush();
		System.out.println(m);
		}

		long timeoutInMillis = 1000*3;
		//long timeoutInMillis = 1000;
		try{
			//InputStream stdout   = sshSession.getStdout();
			//InputStream stderror = sshSession.getStderr();
			int conditions = ChannelCondition.STDOUT_DATA | 
							 ChannelCondition.STDERR_DATA | 
							 ChannelCondition.CLOSED | 
							 ChannelCondition.EXIT_STATUS |
							 ChannelCondition.EXIT_SIGNAL |
							 ChannelCondition.EOF;
			byte[] buffer = new byte[1024];	
			while(true){
				int condition = sshSession.waitForCondition(conditions, timeoutInMillis); // conditions, timeoutInMillis
				if(condition==ChannelCondition.TIMEOUT){
					System.out.println("condition==ChannelCondition.TIMEOUT");
					break;
				}
				if(condition==ChannelCondition.EXIT_STATUS){
					System.out.println("condition==ChannelCondition.EXIT_STATUS");
					break;
				}
				if(condition==ChannelCondition.EXIT_SIGNAL){
					System.out.println("condition==ChannelCondition.EXIT_SIGNAL");
					break;
				}
				
//				if(condition==ChannelCondition.STDOUT_DATA){
//					int len = stdout.read(buffer);
//					if( len > 0 ) {
//						//System.out.write(buffer, 0, len);
//						output_listener.OutputMessage(new String(buffer));
//					}
//				}
//				if( condition == ChannelCondition.STDERR_DATA ) {
//					int len = stderr.read(buffer);
//					 if( len > 0 ) {
//						//System.err.write(buffer, 0, len); 
//						output_listener.OutputMessage(new String(buffer));
//					 }
//				}
				
				if(condition == ChannelCondition.EOF ) {
					// connection closed.
					System.out.println("SSH connection has been closed(EOF).");
					break;
				}
				if(condition >= ChannelCondition.EXIT_STATUS ) {
					//
					// unkown condition (50,58 is not defined in ChannelCondition, but appears!).
					// treat the condition that is bigger than EXIT_STATUS(32) as connection closed.
					//
					System.out.println("SSH connection has been closed("+condition+").");
					break;
				}			
				//condition = sshSession.waitForCondition(conditions, timeoutInMillis);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.err);
		}
		//String cmd="cd www;ls;";
		//cmd+="pwd;";
		//test.exec2(cmd);
		if(false){
			test.closeSession();
			test.close();
		}
		
		System.out.println("SSH End...");
		return "";
	}
	public static void test(ShellExecParam param){
		System.out.println("SSHTest");
		test2(param);
		System.exit(0);
	}
	public static boolean test2(ShellExecParam param){
		SSHExec test=new SSHExec(param);
		test.init(param.getHost(),param.getUser(),param.getPassword());
		
		if(!test.open()){
			return false;
		}
		if(!test.openSession()){
			return false;
		}
		
		String cmd="cd www;ls;";
		cmd+="pwd;";
		test.exec2(cmd);
		
		test.closeSession();
		test.close();
		
		return true;
	}
	protected Connection sshConn;
	protected Session sshSession;
	protected String sshHostname;
	protected String sshUsername;
	protected String sshPassword;
	// コンストラクタ
	public void init(String hostname, String username, String password) {
		sshHostname=hostname;
		sshUsername=username;
		sshPassword=password;
	}
	// コマンド実行（SSHオープン⇒コマンド実行⇒SSHクローズ）
	public boolean execCommand(String command) {
		// SSHオープン
		if(!this.open()){
			return false;
		}
		if(!this.openSession()){
			return false;
		}
		// コマンド実行
		if(!this.exec(command)){
			return false;
		}
		// SSHクローズ
		this.closeSession();
		// SSHクローズ
		this.close();
		return true;
	}

	// SSHオープン
	protected boolean open() {
		try{
			// コネクションインスタンスの作成
			sshConn = new Connection(sshHostname);
			//
			sshConn.addConnectionMonitor(new ConnectionMonitor(){
				@Override
				public void connectionLost(Throwable arg0) {
					System.out.println("addConnectionMonitor:lost connection!");
					System.exit(0);
				}
			});
			// 接続
			sshConn.connect();
			// ユーザ／パスワード認証
			if(!sshConn.authenticateWithPassword(sshUsername, sshPassword)){
				throw new IOException("Error: Failed to authenticateWithPassword");
			}
			// SSHセッションの開始
			//sshSession = sshConn.openSession();
		} catch(IOException e) {
			e.printStackTrace(System.err);
			this.closeSession();
			this.close();
			return false;
		}
		
		return true;
	}
	protected boolean openSession() {
		try{
			sshSession = sshConn.openSession();
			
			System.out.println("stdin:"+sshSession.getStdin());
			System.out.println("stdout:"+sshSession.getStdout());
			System.out.println("stderr:"+sshSession.getStderr());
			
		} catch(IOException e) {
			e.printStackTrace(System.err);
			this.closeSession();
			this.close();
			return false;
		}
		
		return true;
	}
	// コマンド実行＆結果出力
	protected boolean exec(String command) {
		try{
			// コマンドの実行
			sshSession.execCommand(command);
			// コマンド実行結果の標準出力
			this.printStdout(sshSession);
			// コマンド実行結果のエラー出力
			this.printStderr(sshSession);
		} catch(IOException e) {
			e.printStackTrace(System.err);
			this.closeSession();
			this.close();
			return false;
		}
		return true;
	}
	// コマンド実行＆結果出力
	protected boolean exec2(String command) {
		try{
			//sshSession.requestPTY("vt100", 0, 0, 0, 0, null);  
			OutputStream stdin = sshSession.getStdin();
			// コマンドの実行
			sshSession.startShell();
			// send command
			stdin.write( command.getBytes() );
			stdin.write('\n');
			stdin.flush();
			Exec2Wait();
		} catch(IOException e) {
			e.printStackTrace(System.err);
			this.closeSession();
			this.close();
			return false;
		}
		return true;
	}
	
	void Exec2Wait(){
//		long timeoutInMillis = 2000;
//		long timeoutInMillis = 1000*10;
		long timeoutInMillis = 1000*3;
		try{
			
//			// get stdout, in, error
//			OutputStream stdin   = sshSession.getStdin();
//			// send command
//			stdin.write( command.getBytes() );
//			stdin.write('\n');
//			stdin.flush();
			
			InputStream stdout   = sshSession.getStdout();
			InputStream stderror = sshSession.getStderr();
			int conditions = ChannelCondition.STDOUT_DATA | 
							 ChannelCondition.STDERR_DATA | 
							 ChannelCondition.CLOSED | 
							 //ChannelCondition.EXIT_STATUS |
							 //ChannelCondition.EXIT_SIGNAL |
							 ChannelCondition.EOF;
							 
			byte[] buffer = new byte[1024];	
			//int condition = sshSession.waitForCondition(conditions, timeoutInMillis); // conditions, timeoutInMillis
			// read stdout, stderror
			//while(condition!=ChannelCondition.TIMEOUT){
			while(true){
				int condition = sshSession.waitForCondition(conditions, timeoutInMillis); // conditions, timeoutInMillis
				if(condition==ChannelCondition.TIMEOUT){
					System.out.println("condition==ChannelCondition.TIMEOUT");
					break;
				}
				if(condition==ChannelCondition.STDOUT_DATA){
					int len = stdout.read(buffer);
					if( len > 0 ) {
						System.out.write(buffer, 0, len);
					}
				}
				if( condition == ChannelCondition.STDERR_DATA ) {
					int len = stderror.read(buffer);
					 if( len > 0 ) {
						 System.err.write(buffer, 0, len); 
					 }
				}
				if(condition == ChannelCondition.EOF ) {
					// connection closed.
					System.out.println("SSH connection has been closed(EOF).");
					break;
				}
				if(condition >= ChannelCondition.EXIT_STATUS ) {
					//
					// unkown condition (50,58 is not defined in ChannelCondition, but appears!).
					// treat the condition that is bigger than EXIT_STATUS(32) as connection closed.
					//
					System.out.println("SSH connection has been closed("+condition+").");
					break;
				}			
				//condition = sshSession.waitForCondition(conditions, timeoutInMillis);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.err);
		}
	}
	
	// 実行結果の標準出力（StreamGobbler使用）
	protected void printStdout(Session sshSession) throws IOException {
		InputStream stdout = new StreamGobbler(sshSession.getStdout());
		//InputStream stdout = sshSession.getStdout();
		BufferedReader br_out = new BufferedReader(new InputStreamReader(stdout,"UTF8"));
		while (true) {
			String line_out = br_out.readLine();
			if (line_out == null) {
				break;
			}
			System.out.println(line_out);
		}
	}
	// 実行結果のエラー出力（StreamGobbler使用）
	protected void printStderr(Session sshSession) throws IOException {
		InputStream stderr = new StreamGobbler(sshSession.getStderr());
		//InputStream stderr = sshSession.getStderr();
		BufferedReader br_err = new BufferedReader(new InputStreamReader(stderr,"UTF8"));
		while (true) {
			String line_err = br_err.readLine();
			if (line_err == null) {
				break;
			}
			System.out.println("Error(" + sshSession.getExitStatus() + ") " + line_err);
		}
	}
	// SSHクローズ
	protected void closeSession() {
		sshSession.close();
	}
	// SSHクローズ
	protected void close() {
		//sshSession.close();
		sshConn.close();
	}
}



