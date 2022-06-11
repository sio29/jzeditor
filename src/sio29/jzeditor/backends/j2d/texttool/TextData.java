/******************************************************************************
;	テキストデータ
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.util.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

import sio29.ulib.ufile.*;

public class TextData{
	private CharCode chara_code=null;		//文字コード
	private UFile filename=null;			//ファイル名
//	private String text=null;				//テキストデータ
	private ArrayList<String> text=null;				//テキストデータ
	//
	public TextData(){}
	/*
	public TextData(String _text,CharCode _chara_code,UFile _filename){
		text=_text;
		chara_code=_chara_code;
		filename=_filename;
	}
	*/
	//ファイル名獲得
	public UFile GetFilename(){
		return filename;
	}
	//文字コード獲得
	public CharCode getCharCode(){
		return chara_code;
	}
	public String getLine(int i){
		return text.get(i);
	}
	public int getLineNum(){
		return text.size();
	}
	//テキスト本体を得る
	public String getNonBomTextString(){
		if(text==null)return null;
		//String m=(String)text;
		String m=TextArrayToString(text);
		return getNonBomTextString(m);
	}
	public static String getNonBomTextString(String m){
		if(m==null)return null;
		if(!CharCode.hasBom(m))return m;
		return m.substring(1);
	}
	//テキストデータの読み込み
	public static TextData LoadText(UFile filename){
		return LoadText(filename,-1);
	}
	//テキストデータの読み込み(サイズ指定)
	public static TextData LoadText(UFile filename,int read_size){
		if(filename==null)return null;
		if(!filename.isFile())return null;
		InputStream is;
		try {
			is=filename.getInputStream();
		} catch(Exception ex) {
			return null;
		}
		//文字コードを調べる
		CharCode chara_code=null;
		try{
			chara_code=CheckCharaCode(filename);
//System.out.println("chara_code="+chara_code);
		}catch(Exception ex){}
		//読み込み
		try {
			String conv_code;
			if(chara_code==null){
				conv_code="JISAutoDetect";
			}else{
				conv_code=chara_code.getCode();
			}
			BufferedReader fr = new BufferedReader(new InputStreamReader(is,conv_code));
			if(chara_code==null)chara_code=CharCode.SJIS;
			//String text=ReadTextString(fr);
			ArrayList<String> text=ReadTextString(fr);
			fr.close();
			//return new TextData(text,chara_code,filename);
			TextData data=new TextData();
			data.text=text;
			data.chara_code=chara_code;
			data.filename=filename;
			return data;
		} catch(Exception ex) {
			System.out.println("LoadTextSub:Error !!:"+filename);
			return null;
		}
	}
	private static String TextArrayToString(ArrayList<String> text){
		StringBuffer buff=new StringBuffer();
		for(int i=0;i<text.size();i++){
			buff.append(text.get(i));
			buff.append("\n");
		}
		String m=buff.toString();
		return m;
	}
	//テキストリード
	//private static String ReadTextString(BufferedReader fr){
	private static ArrayList<String> ReadTextString(BufferedReader fr){
		try {
			ArrayList<String> text=new ArrayList<String>();
			//StringBuffer buff=new StringBuffer();
			while(true){
				String str=fr.readLine();
				if(str==null)break;
				//buff.append(str);
				//buff.append("\n");
				text.add(str);
			}
			//String m=buff.toString();
			//return m;
			return text;
		} catch(Exception ex) {
			return null;
		}
	}
	//テキストの保存
	public static boolean SaveText(UFile filename,CharCode chara_code,String text){
		if(filename==null)return false;
		if(chara_code==null)chara_code=CharCode.SJIS;
		try {
			//(3)FileOutputStreamオブジェクトの生成
			OutputStream fos = filename.getOutputStream();
			//
			OutputStreamWriter out = new OutputStreamWriter(fos, chara_code.getCode());
			/*
			String out_text=text;
			
			if(chara_code.getBomFlg() && !CharCode.hasBom(text)){
				out_text=String.format("%c%s",CharCode.BOM,text);
			}
			*/
			String out_text=CharCode.getStringByBomFlg(text,chara_code.getBomFlg());
			
			out.write(out_text);
			out.close();
System.out.println("Save Text : "+filename+"("+chara_code+")");
			return true;
		} catch(Exception ex) {
			//ex.printStackTrace();
			System.out.println("Save Error !! : "+filename);
			return false;
		}
	}
	//文字コードのチェック
	public static CharCode CheckCharaCode(UFile filename) throws IOException {
		CharCode code=null;
		InputStream fis = null;
		try{
			fis = filename.getInputStream();
			code=CheckCharaCode(fis);
		}finally{
			if(fis!=null){
				fis.close();
			}
		}
		return code;
	}
	//文字コード獲得(byteデータから)
	public static CharCode CheckCharaCode(byte[] data) throws IOException {
		return CheckCharaCode(new ByteArrayInputStream(data));
	}
	//文字コード獲得(ストリームから)
	public static CharCode CheckCharaCode(InputStream fis) throws IOException {
		byte[] buf = new byte[4096];
		// (1)
		UniversalDetector detector = new UniversalDetector(null);
		// (2)
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
//System.out.println("nread="+nread+",done="+detector.isDone());
		// (3)
		detector.dataEnd();
		// (4)
		String encoding = detector.getDetectedCharset();
		if(encoding.equals("WINDOWS-1252")){
			encoding="SHIFT_JIS";
		}
//System.out.println("encoding:"+encoding);
		// (5)
		detector.reset();
		if(encoding==null)return null;
		boolean bom_flg=CharCode.hasBom(buf);
		return new CharCode(encoding,bom_flg);
	}
	//
	public ArrayList<TextSearchRet> searchText(String search_str,Object search_opt){
		ArrayList<TextSearchRet> ret=new ArrayList<TextSearchRet>();
		for(int i=0;i<text.size();i++){
			String t=text.get(i);
			int index=t.indexOf(search_str);
			if(index>=0){
				TextSearchRet r=new TextSearchRet();
				r.line=i+1;
				r.col=index;
				ret.add(r);
			}
		}
		return ret;
	}
}
