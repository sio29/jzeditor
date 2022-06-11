/******************************************************************************
;	テキストツール
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.io.File;
import java.util.ArrayList;

import com.ibm.icu.lang.*;

import sio29.ulib.ufile.*;

public class TextTool{
	//============================================
	//============================================
	public static int[] StringToCodePointArray(String str){
		int str_len=str.length();
		int code_point_len=str.codePointCount(0,str_len);
		int[] buff=new int[code_point_len];
		
		return buff;
	}
	
	//============================================
	//============================================
	public static int getCharWidth(int code){
//		return getCharWidth1(code);
		return getCharWidth2(code);
	}
	public static int getCharWidth1(int code){
		boolean han_flg=false;
		if(code<0x80 || code==0xa5 || code==0x203e || (code>=0xff61 && code<=0xff9f)){
			han_flg=true;
		}
		return han_flg?1:2;
	}
	public static int getCharWidth2(int code){
		/*
		if(locale == null) {
			throw new NullPointerException("locale is null");
		}
		*/
		
		int value = UCharacter.getIntPropertyValue(code,UProperty.EAST_ASIAN_WIDTH);
		switch(value) {
			case UCharacter.EastAsianWidth.NARROW:
			case UCharacter.EastAsianWidth.NEUTRAL:
			case UCharacter.EastAsianWidth.HALFWIDTH:
				return 1;
			case UCharacter.EastAsianWidth.FULLWIDTH:
			case UCharacter.EastAsianWidth.WIDE:
				return 2;
			case UCharacter.EastAsianWidth.AMBIGUOUS:
				/*
				if(EAST_ASIAN_LANGS.contains(locale.getLanguage())) {
					return 2;
				} else {
					return 1;
				}
				*/
				//return 2;
				return 1;
		}
System.out.println("err");
		return 1;
	}
	//============================================
	//文字列から最後の改行コード(0x0d,0x0a)を省く
	//============================================
	public static String GetStringRemoveCR(String m){
		if(m==null)return m;
		int len=m.length();
		if(len==0)return m;
		int i=len-1;
		while(true){
			if(i<0)return "";
			//if(i<0)return null;
			char c=m.charAt(i);
			if(c!=0x0d && c!=0x0a)break;
			i--;
		}
		int new_len=i+1;
		if(len==new_len)return m;
		return m.substring(0,new_len);
	}
	//==============================================
	//文字列から最後の空白コード(0x20,0x09)を省く
	//============================================
	public static String GetStringRemoveBottomSpace(String m){
		if(m==null)return m;
		int len=m.length();
		if(len==0)return m;
		int i=len-1;
		while(true){
			if(i<0)return "";
			//if(i<0)return null;
			char c=m.charAt(i);
			if(c!=0x20 && c!=0x09)break;
			i--;
		}
		int new_len=i+1;
		if(len==new_len)return m;
		return m.substring(0,new_len);
	}
	//==============================================
	//文字列から前の空白コード(0x20,0x09)を省く
	//============================================
	public static String GetStringRemovePrevSpace(String m){
		if(m==null)return m;
		int len=m.length();
		if(len==0)return m;
		int i=0;
		while(true){
			if(i>=len)return "";
			char c=m.charAt(i);
			if(c!=0x20 && c!=0x09)break;
			i++;
		}
		int new_len=len-i;
		if(len==new_len)return m;
		return m.substring(i,len);
	}
	//==============================================
	//タブを変換した文字数を得る
	//==============================================
	public static int StrlenFromTab(String m,int tab_len){
		int src_len=m.length();
		int dst_len=0;
		for(int i=0;i<src_len;i++){
			char mm=m.charAt(i);
//System.out.println("["+i+"]"+(int)mm+":tab("+((int)'\t')+")");
			if(mm=='\t'){
				if((dst_len % tab_len)==0){
					dst_len+=tab_len;
//System.out.println("dst_len "+(dst_len/2));
				}else{
					dst_len=((dst_len+(tab_len-1)) / tab_len)*tab_len;
				}
			}else if(mm<0x80){
				dst_len++;
			}else {
				dst_len+=2;
			}
		}
		return dst_len;
	}
	public static int StrlenToTab(String m,int tab_len,int index){
		if(index==0)return 0;
		int src_len=m.length();
		int dst_len=0;
		for(int i=0;i<src_len;i++){
			if(dst_len>=index)return i;
			char mm=m.charAt(i);
//System.out.println("["+i+"]"+(int)mm+":tab("+((int)'\t')+")");
			if(mm=='\t'){
				if((dst_len % tab_len)==0){
					dst_len+=tab_len;
//System.out.println("dst_len "+(dst_len/2));
				}else{
					dst_len=((dst_len+(tab_len-1)) / tab_len)*tab_len;
				}
			}else if(mm<0x80){
				dst_len++;
			}else {
				dst_len+=2;
			}
		}
		return src_len;
	}
	
	//==============================================
	//コマンドラインの文字列を得る
	//「>」移行の文字列を返す
	//==============================================
	public static String GetCommandLineString(String line,String separator){
		if(line==null)return null;
		if(line.length()==0)return null;
		int pos=line.indexOf(separator);
		if(pos<0)return line;
		return line.substring(pos+1,line.length());
	}
	//============================================
	//先頭が空白じゃない文字列の獲得
	//============================================
	public static String GetNoSpaceTopStr(String m){
		if(m==null)return null;
		if(m.length()==0)return null;
		int len=m.length();
		boolean flg=false;
		int i;
		char c_space=" ".charAt(0);
		char c_tab  ="\t".charAt(0);
		for(i=0;i<len;i++){
			int code=m.charAt(i);
			if(code==c_space)continue;
			if(code==c_tab  )continue;
			flg=true;
			break;
		}
		if(!flg)return null;
		return m.substring(i);
	}
};

