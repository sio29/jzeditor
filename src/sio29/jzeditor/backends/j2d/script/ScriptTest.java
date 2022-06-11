/******************************************************************************
;	スクリプトテスト
******************************************************************************/
package sio29.jzeditor.backends.j2d.script;

import java.io.Writer;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class ScriptTest{
	public static void test() {//throws Exception {
		//String m="print(\"Hello, JavaScript!\");for(var i=0;i<10;i++)print(i);";
		String m="";
		//Nashorn
		m+="load('nashorn:mozilla_compat.js')\n";
		//
		m+="importPackage(java.lang)\n";
		m+="importPackage(java.io)\n";
		m+="importPackage(java.util)\n";
		m+="importPackage(Packages.sio29.ulib.umat)\n";
		m+="System.out.println(\"Hello, JavaScript!\");\n";
		m+="var n=new FVECTOR2(123,456);\n";
		m+="print(n);\n";
		//
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("js");
		ScriptContext context=engine.getContext();
		Writer writer=new Writer(){
			StringBuffer buffer=new StringBuffer();
			 public void close(){
			 	System.out.println("close **");
			 }
			 public void flush(){
			 	System.out.println("flush **");
			 	//String m=buffer.toString();
			 	//System.out.println(m);
			 }
			 public void write(char[] cbuf, int off, int len){
			 	String m=new String(cbuf,off,len);
			 	buffer.append(m);
			 	//System.out.println(m);
			 }
		};
		context.setWriter(writer);
		
		try {
			engine.eval(m);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}
	}
	
}
