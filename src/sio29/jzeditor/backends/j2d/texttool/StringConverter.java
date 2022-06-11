/******************************************************************************
;	文字列変換
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

public class StringConverter{
	public static final String TypeBigSmall  ="ConvBigSmall";
	public static final String TypeBig       ="ConvBig";
	public static final String TypeSmall     ="ConvSmall";
	public static final String TypeZen       ="ConvZen";
	public static final String TypeHan       ="ConvHan";
	public static final String TypeSpaceToTab="ConvSpaceToTab";
	public static final String TypeTabToSpace="ConvTabToSpace";
	public static final String TypeKanaHira  ="ConvKanaHira";
	public static final String TypeKana      ="ConvKana";
	public static final String TypeHira      ="ConvHira";
	//変換ルーチン
	public static abstract class Type{
		private final String command;
		private final String name;
		public Type(String command,String name){
			this.command=command;
			this.name    =name;
		}
		public boolean equals(Object o){
			if(o==null)return false;
			if(o instanceof Type){
				return command.equals(((Type)o).command);
			}else if(o instanceof String){
				return command.equals((String)o);
			}
			return false;
		}
		public String getCommand(){
			return command;
		}
		public String getName(){
			return name;
		}
		public String toString(){
			return command;
		}
		public abstract boolean convert(StringWriter writer,StringReader reader) throws IOException ;
	}
	//大文字、小文字変換
	private static Type BigSmall  =new Type(TypeBigSmall,"大文字、小文字変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m>='A' && m<='Z'){
				m=(char)(m-'A'+'a');
			}else if(m>='a' && m<='z'){
				m=(char)(m-'a'+'A');
			}
			writer.write(m);
			return true;
		}
	};
	//大文字変換
	private static Type Big       =new Type(TypeBig,"大文字変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m>='a' && m<='z'){
				m=(char)(m-'a'+'A');
			}
			writer.write(m);
			return true;
		}
	};
	//小文字変換
	private static Type Small     =new Type(TypeSmall,"小文字変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m>='A' && m<='Z'){
				m=(char)(m-'A'+'a');
			}
			writer.write(m);
			return true;
		}
	};
	//全角変換
	private static Type Zen       =new Type(TypeZen,"全角変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			m=toFull(m);
			writer.write(m);
			return true;
		}
	};
	//半角変換
	private static Type Han       =new Type(TypeHan,"半角変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			m=toHalf(m);
			writer.write(m);
			return true;
		}
	};
	//スペース→タブ変換
	private static Type SpaceToTab=new Type(TypeSpaceToTab,"スペース→タブ変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m==' '){
				reader.mark(0);
				int m1=reader.read();
				int m2=reader.read();
				int m3=reader.read();
				if(m1==' ' && m2==' ' && m3==' '){
					writer.write("\t");
					return true;
				}else{
					reader.reset();
				}
			}
			writer.write(m);
			return true;
		}
	};
	//タブ→スペース変換
	private static Type TabToSpace=new Type(TypeTabToSpace,"タブ→スペース変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m=='\t'){
				writer.write("    ");
			}else{
				writer.write(m);
			}
			return true;
		}
	};
	//かな、ひらがな変換
	private static Type KanaHira  =new Type(TypeKanaHira,"かな、ひらがな変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= 'ぁ' && m <= 'ん') {
				m=(char)(m-'ぁ'+'ァ');
			}else if(m >= 'ァ' && m <= 'ン') {
				m=(char)(m-'ァ'+'ぁ');
			}
			writer.write(m);
			return true;
		}
	};
	//かな変換
	private static Type Kana      =new Type(TypeKana,"かな変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= 'ぁ' && m <= 'ん') {
				m=(char)(m-'ぁ'+'ァ');
			}
			writer.write(m);
			return true;
		}
	};
	//ひらがな変換
	private static Type Hira      =new Type(TypeHira,"ひらがな変換"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= 'ァ' && m <= 'ン') {
				m=(char)(m-'ァ'+'ぁ');
			}
			writer.write(m);
			return true;
		}
	};
	//変換ルーチンテーブル
	private static Type[] types=new Type[]{
		BigSmall  ,
		Big       ,
		Small     ,
		Zen       ,
		Han       ,
		SpaceToTab,
		TabToSpace,
		KanaHira  ,
		Kana      ,
		Hira      
	};
	//コマンドから変換ルーチンを得る
	public static Type GetType(String command){
		for(int i=0;i<types.length;i++){
			if(types[i].command.equals(command))return types[i];
		}
		return null;
	}
	public static String getCommand(String command){
		Type type=GetType(command);
		if(type==null)return null;
		return type.getCommand();
	}
	//変換ルーチンリスト
	public static Type[] GetTypes(){
		return types;
	}
	//変換実行
	public static String convert(String text,String command){
		String new_text="";
		Type type=GetType(command);
		StringReader reader=new StringReader(text);
		StringWriter writer=new StringWriter();
		try{
			while(true){
				if(!type.convert(writer,reader))break;
			}
		}catch(Exception ex){}
		return writer.toString();
	}
	//========================================================
	//文字変換
	//========================================================
	//全角→半角
	public static char toHalf(int c){
		if(c>=65281 && c<=65374){
			c-=65248;
		}
		return (char)c;
		/*
		if(c>='Ａ' && c<='Ｚ'){
			c=(char)(c-'Ａ'+'A');
		}else if(c>='ａ' && c<='ｚ'){
			c=(char)(c-'ａ'+'a');
		}else if(c>='０' && c<='９'){
			c=(char)(c-'０'+'0');
		}
		return (char)c;
		*/
		
	}
	//半角→全角
	public static char toFull(int c){
		if(c>=33 && c<=126){
			c+=65248;
		}
		return (char)c;
		/*
		if(c>='A' && c<='Z'){
			c=(char)(c-'A'+'Ａ');
		}else if(c>='a' && c<='z'){
			c=(char)(c-'a'+'ａ');
		}else if(c>='0' && c<='9'){
			c=(char)(c-'0'+'０');
		}
		return (char)c;
		*/
	}
}

