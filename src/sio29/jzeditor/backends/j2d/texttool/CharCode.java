/******************************************************************************
;	文字コード
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.util.*;

public class CharCode{
	public final static char BOM=(char)0xfeff;
	public final static CharCode SJIS      =new CharCode("SHIFT_JIS",false);
	//public final static CharCode JIS       =new CharCode("JIS",false);
	public final static CharCode JIS       =new CharCode("ISO-2022-JP",false);
	//public final static CharCode EUC       =new CharCode("EUC",false);
	public final static CharCode EUC_JP    =new CharCode("EUC-JP",false);
	public final static CharCode UTF8      =new CharCode("UTF-8",false);
	public final static CharCode UTF8BOM   =new CharCode("UTF-8",true);
	public final static CharCode UTF16LE   =new CharCode("UTF-16LE",false);
	public final static CharCode UTF16BE   =new CharCode("UTF-16BE",false);
	public final static CharCode UTF16LEBOM=new CharCode("UTF-16LE",true);
	public final static CharCode UTF16BEBOM=new CharCode("UTF-16BE",true);
	//リスト
	//private static CharCode[] g_cahrcode_list={
	private static CharCode[] cahrcode_list={
		SJIS,
		JIS,
		//EUC,
		EUC_JP,
		UTF8,
		UTF8BOM,
		UTF16LE,
		UTF16BE,
		UTF16LEBOM,
		UTF16BEBOM,
	};
	//private static ArrayList<CharCode> cahrcode_list=new ArrayList<CharCode>();
	private String code;			//文字コード
	private boolean bom_flg;		//BOMフラグ
	//
	/*
	static{
		for(int i=0;i<g_cahrcode_list.length;i++){
			cahrcode_list.add(g_cahrcode_list[i]);
		}
	}
	static boolean hasCharCode(CharCode _cc){
		for(int i=0;i<cahrcode_list.size();i++){
			CharCode cc=cahrcode_list.get(i);
			if(cc.equals(_cc)){
				return true;
			}
		}
		return false;
	}
	static void addCharCode(CharCode _cc){
		if(!hasCharCode(_cc)){
			cahrcode_list.add(_cc);
		}
	}
	*/
	public CharCode(){}
	public CharCode(String _code,boolean _bom_flg){
		code   =_code;
		bom_flg=_bom_flg;
		//addCharCode(this);
	}
	public CharCode(CharCode src){
		code   =src.code;
		bom_flg=src.bom_flg;
		//addCharCode(this);
	}
	public boolean equals(Object n){
		if(n==null)return false;
		if(!(n instanceof CharCode))return false;
		CharCode src=(CharCode)n;
		if(!(code.equals(src.code)))return false;
		if(bom_flg!=src.bom_flg)return false;
		return true;
	}
	public String toString(){
		return getName();
	}
	public String getName(){
		if(!bom_flg)return code;
		return code+String.format("(BOM)");
	}
	public String getCode(){
		return code;
	}
	public boolean getBomFlg(){
		return bom_flg;
	}
	public CharCode copy(){
		return new CharCode(this);
	}
	//======================================
	//static
	//リストを得る
	public static CharCode[] getCharCodeList(){
		return cahrcode_list;
		//return (CharCode[] )cahrcode_list.toArray(new CharCode[]{});
	}
	//名前リストへ変換
	public static String[] convertCharCodeNameList(CharCode[] charcode_list){
		String[] list=new String[cahrcode_list.length];
		for(int i=0;i<cahrcode_list.length;i++){
			list[i]=cahrcode_list[i].getName();
		//String[] list=new String[cahrcode_list.size()];
		//for(int i=0;i<cahrcode_list.size();i++){
		//	list[i]=cahrcode_list.get(i).getName();
		}
		return list;
	}
	//Indexを求める
	public static int getCharCodeIndex(CharCode[] charcode_list,CharCode src){
		for(int i=0;i<cahrcode_list.length;i++){
			if(cahrcode_list[i].equals(src))return i;
		//for(int i=0;i<cahrcode_list.size();i++){
		//	if(cahrcode_list.get(i).equals(src))return i;
		}
		return -1;
	}
	//名前とBOMフラグからCharCodeを求める
	public static CharCode getCharCode(String code,boolean bom_flg){
		for(int i=0;i<cahrcode_list.length;i++){
			CharCode cc=cahrcode_list[i];
		//for(int i=0;i<cahrcode_list.size();i++){
		//	CharCode cc=cahrcode_list.get(i);
			if(!cc.code.equals(code))continue;
			if(cc.bom_flg!=bom_flg)continue;
			return cc.copy();
		}
		return null;
	}
	//名前からCharCodeを求める
	public static CharCode getCharCodeFromName(String name){
		for(int i=0;i<cahrcode_list.length;i++){
			CharCode cc=cahrcode_list[i];
		//for(int i=0;i<cahrcode_list.size();i++){
		//	CharCode cc=cahrcode_list.get(i);
			String name2=cc.getName();
			if(name.equals(name2))return cc.copy();
		}
		return null;
	}
	//BOMを除いた文字列
	public static String getNonBomString(String m){
		/*
		if(!hasBom(m))return m;
		return m.substring(1);
		*/
		return getStringByBomFlg(m,false);
	}
	//BOMを追加/削除した文字列
	public static String getStringByBomFlg(String m,boolean bom_flg){
		//テキストのBOMの有無
		boolean text_bom_flg=hasBom(m);
		//同じ?
		if(text_bom_flg==bom_flg)return m;
		if(!bom_flg){
		//BOMを除いた文字列
			return m.substring(1);
		}else{
		//BOMを追加文字列
			return String.format("%c%s",CharCode.BOM,m);
		}
	}
	//BOMがある?
	public static boolean hasBom(String m){
		if(m==null)return false;
		if(m.length()==0)return false;
		return (m.charAt(0)==BOM);
	}
	//BOMがある?
	public static boolean hasBom(byte[] m){
		if(m==null)return false;
		if(m.length<2)return false;
		int m0=m[0] & 0xff;
		int m1=m[1] & 0xff;
		if(m0==0xfe && m1==0xff)return true;
		if(m1==0xfe && m0==0xff)return true;
		if(m.length<3)return false;
		int m2=m[2] & 0xff;
		if(m0==0xef && m1==0xbb && m2==0xbf)return true;
		return false;
	}
}
