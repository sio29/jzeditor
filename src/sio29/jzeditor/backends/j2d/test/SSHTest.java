/******************************************************************************
;	SSHテスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.test;


import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;

import com.trilead.ssh2.*;


import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class SSHTest{
	public final static String g_host="sio29.sakura.ne.jp";
	public final static String g_user="sio29";
	public final static String g_password="vff6mtx5wp";
	
	
	public static void test(){
		System.out.println("SSHTest");
		test2();
		System.exit(0);
	}
	public static boolean test2(){
		SSHTest test=new SSHTest(g_host,g_user,g_password);
		
		if(!test.open()){
			return false;
		}
		if(!test.openSession()){
			return false;
		}
		
		//test.exec("cd www");
		//test.exec("ls");
		//test.exec("ls");
		//String cmd="";
		//cmd+="cd www;";
		//cmd+="ls;";
		/*
		System.out.println("------------------ 01");
		test.exec("cd www;ls;");
		System.out.println("------------------ 02");
		test.exec("ls -l");
		*/
		
		
		
		System.out.println("------------------ 01");
		String cmd="cd www;ls;";
		//test.exec(cmd);
		//System.out.println("------------------ 02");
		//cmd+="ls -l;pwd;";
		cmd+="pwd;";
		//test.exec(cmd);
		test.exec2(cmd);
		
		
		//SFTPテスト
		//test.SFTPTest();
		
		
		test.closeSession();
		test.close();
		
		//test.execCommand("ls");
		return true;
		
	}
	protected Connection sshConn;
	protected Session sshSession;
	protected String sshHostname;
	protected String sshUsername;
	protected String sshPassword;
	// コンストラクタ
	public SSHTest(String hostname, String username, String password) {
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
	/*
	// コマンド実行＆結果出力
	protected boolean execSession(String command) {
		if(!openSession())return false;
		exec(command);
		closeSession();
		return true;
	}
	*/
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
			/*
System.out.println("exec2_01");
			// コマンド実行結果の標準出力
			this.printStdout(sshSession);
System.out.println("exec2_02");
			// コマンド実行結果のエラー出力
			this.printStderr(sshSession);
System.out.println("exec2_03");
			*/
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
			/*
			// get stdout, in, error
			OutputStream stdin   = sshSession.getStdin();
			// send command
			stdin.write( command.getBytes() );
			stdin.write('\n');
			stdin.flush();
			*/
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
	
	
	void SFTPTest(){
		SFTPv3Client sftpc = null;
		try{
			// SFTP client作成
			sftpc = new SFTPv3Client(sshConn);
			sftpc.setCharset("UTF-8");
			
			String dir_name="www";
			SFTPPrintDir(sftpc,dir_name);
			
			String src_filename="www/image/comike_01.jpg";
			String out_path="./";
			SFTPDownLoad(sftpc,src_filename,out_path);
			
			sftpc.close();
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			if(sftpc!=null){
				sftpc.close();
			}
		}
	}
	void SFTPPrintDir(SFTPv3Client sftpc ,String dir_name){
		try{
			Vector dirs=sftpc.ls(dir_name);
			for(int i=0;i<dirs.size();i++){
				SFTPv3DirectoryEntry dir=(SFTPv3DirectoryEntry)dirs.get(i);
				//System.out.println("["+i+"]:"+dir.filename+":("+dir.longEntry+")");
				System.out.println(dir.longEntry);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.err);
		}
	}
	boolean SFTPDownLoad(SFTPv3Client sftpc ,String src_filename,String dst_path){
		final int DL_MAX_SIZE=32768;
		SFTPv3FileHandle hd=null;
		try{
			String out_filename=src_filename.substring(src_filename.lastIndexOf("/")+1);
System.out.println("out_filename:"+out_filename);
			SFTPv3FileAttributes attr=sftpc.stat(src_filename);
			long size=attr.size;
System.out.println("size:"+size);
			
			hd=sftpc.openFileRO(src_filename);
			byte[] dst=new byte[(int)size];
			int off=0;
			int max_size=DL_MAX_SIZE;
			while(true){
				if(size==0)break;
				long now_size=size;
				if(now_size>max_size)now_size=max_size;
				sftpc.read(hd,off,dst,off,(int)now_size);
				size-=now_size;
				off+=now_size;
			}
			sftpc.closeFile(hd);
			hd=null;
			
			UFileIO.bsave(dst_path+out_filename,dst);
			return true;
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			try{
				if(hd!=null){
					sftpc.closeFile(hd);
				}
			}catch(Exception ex2){}
			return false;
		}
	}
}



class SSHClient implements ConnectionMonitor {
	public static void main(String[] args) {	
		SSHClient ssh = new SSHClient();
		ssh.getUpTime("localhost", "root", "password4root");
	}
	public void getUpTime(String host, String user, String password) {
		String command = "uptime";
		Connection conn = new Connection(host, 22);
		conn.addConnectionMonitor(this);
		try {
			System.out.println("connecting to " + host + "...");
			int connTimeoutInMlillis = 5000;
			conn.connect(null, connTimeoutInMlillis, 0);
			System.out.println("succeed: connect to " + host);
			if ( conn.authenticateWithPassword(user, password) ) {
				Session sess = conn.openSession();
				System.out.println("succeed: authenticate with password.");
				//
				// requestPTY(
				//	 java.lang.String term,
				//	 int term_width_characters,
				//	 int term_height_characters,
				//	 int term_width_pixels,
				//	 int term_height_pixels,
				//	 byte[] terminal_modes
				// )
				//
				sess.requestPTY("vt100", 0, 0, 0, 0, null);
				sess.startShell();
				// get stdout, in, error
				OutputStream stdin   = sess.getStdin();
				InputStream stdout   = sess.getStdout();
				InputStream stderror = sess.getStderr();
				// send command
				stdin.write( command.getBytes() );
				stdin.write('\n');
				stdin.flush();
				
				long timeoutInMillis = 2000;
				int conditions = ChannelCondition.STDOUT_DATA | 
								 ChannelCondition.STDERR_DATA | 
								 ChannelCondition.CLOSED | 
								 ChannelCondition.EOF;
				byte[] buffer = new byte[1024];	
				int condition = sess.waitForCondition(conditions, timeoutInMillis); // conditions, timeoutInMillis
				
				// read stdout, stderror
				while(condition!=ChannelCondition.TIMEOUT){
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
					condition = sess.waitForCondition(conditions, timeoutInMillis);
				}
				// close session
				sess.close();
				// close connection
				conn.close();
			} else {
				System.out.println("login failed.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("lost connection!");
	}
}





