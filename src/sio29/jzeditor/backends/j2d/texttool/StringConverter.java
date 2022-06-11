/******************************************************************************
;	������ϊ�
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
	//�ϊ����[�`��
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
	//�啶���A�������ϊ�
	private static Type BigSmall  =new Type(TypeBigSmall,"�啶���A�������ϊ�"){
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
	//�啶���ϊ�
	private static Type Big       =new Type(TypeBig,"�啶���ϊ�"){
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
	//�������ϊ�
	private static Type Small     =new Type(TypeSmall,"�������ϊ�"){
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
	//�S�p�ϊ�
	private static Type Zen       =new Type(TypeZen,"�S�p�ϊ�"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			m=toFull(m);
			writer.write(m);
			return true;
		}
	};
	//���p�ϊ�
	private static Type Han       =new Type(TypeHan,"���p�ϊ�"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			m=toHalf(m);
			writer.write(m);
			return true;
		}
	};
	//�X�y�[�X���^�u�ϊ�
	private static Type SpaceToTab=new Type(TypeSpaceToTab,"�X�y�[�X���^�u�ϊ�"){
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
	//�^�u���X�y�[�X�ϊ�
	private static Type TabToSpace=new Type(TypeTabToSpace,"�^�u���X�y�[�X�ϊ�"){
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
	//���ȁA�Ђ炪�ȕϊ�
	private static Type KanaHira  =new Type(TypeKanaHira,"���ȁA�Ђ炪�ȕϊ�"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= '��' && m <= '��') {
				m=(char)(m-'��'+'�@');
			}else if(m >= '�@' && m <= '��') {
				m=(char)(m-'�@'+'��');
			}
			writer.write(m);
			return true;
		}
	};
	//���ȕϊ�
	private static Type Kana      =new Type(TypeKana,"���ȕϊ�"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= '��' && m <= '��') {
				m=(char)(m-'��'+'�@');
			}
			writer.write(m);
			return true;
		}
	};
	//�Ђ炪�ȕϊ�
	private static Type Hira      =new Type(TypeHira,"�Ђ炪�ȕϊ�"){
		public boolean convert(StringWriter writer,StringReader reader) throws IOException {
			int m=reader.read();
			if(m==-1)return false;
			if(m >= '�@' && m <= '��') {
				m=(char)(m-'�@'+'��');
			}
			writer.write(m);
			return true;
		}
	};
	//�ϊ����[�`���e�[�u��
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
	//�R�}���h����ϊ����[�`���𓾂�
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
	//�ϊ����[�`�����X�g
	public static Type[] GetTypes(){
		return types;
	}
	//�ϊ����s
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
	//�����ϊ�
	//========================================================
	//�S�p�����p
	public static char toHalf(int c){
		if(c>=65281 && c<=65374){
			c-=65248;
		}
		return (char)c;
		/*
		if(c>='�`' && c<='�y'){
			c=(char)(c-'�`'+'A');
		}else if(c>='��' && c<='��'){
			c=(char)(c-'��'+'a');
		}else if(c>='�O' && c<='�X'){
			c=(char)(c-'�O'+'0');
		}
		return (char)c;
		*/
		
	}
	//���p���S�p
	public static char toFull(int c){
		if(c>=33 && c<=126){
			c+=65248;
		}
		return (char)c;
		/*
		if(c>='A' && c<='Z'){
			c=(char)(c-'A'+'�`');
		}else if(c>='a' && c<='z'){
			c=(char)(c-'a'+'��');
		}else if(c>='0' && c<='9'){
			c=(char)(c-'0'+'�O');
		}
		return (char)c;
		*/
	}
}

